package auto_scaling.monitor.aws;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.SpotPrice;

import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.SpotPricingStatus;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.core.SpotPricingManager;
import auto_scaling.event.Event;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.SpotPriceMonitor;
import auto_scaling.util.aws.AmazonClient;

/** 
* @ClassName: AWSSpotPriceMonitor 
* @Description: the spot price implementation for Amazon AWS
* @author Chenhao Qu
* @date 06/06/2015 2:34:28 pm 
*  
*/
public class AWSSpotPriceMonitor extends SpotPriceMonitor {

	/** 
	* @Fields ec2Client : Amazon EC2 client
	*/ 
	protected AmazonEC2Client ec2Client;
	/** 
	* @Fields configuration : the cloud configuration
	*/ 
	protected ICloudConfiguration configuration;

	public AWSSpotPriceMonitor(ICloudConfiguration configuration,
			String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
		this.ec2Client = AmazonClient.getAmazonEC2Client(configuration);
		this.configuration = configuration;
	}

	/* (non-Javadoc) 
	* <p>Title: doMonitoring</p> 
	* <p>Description: </p>  
	* @see auto_scaling.monitor.Monitor#doMonitoring() 
	*/
	@Override
	public synchronized void doMonitoring() {
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager
				.getInstanceTemplateManager();
		Collection<InstanceTemplate> allInstanceTemplates = instanceTemplateManager
				.getAllInstanceTemplates();

		Calendar calendar = Calendar.getInstance();
		Date endTime = calendar.getTime();
		calendar.add(Calendar.SECOND, -monitorInterval);
		Date startTime = calendar.getTime();

		Set<String> instanceTypes = new HashSet<String>();
		Set<String> oss = new HashSet<String>();
		for (InstanceTemplate instanceTemplate : allInstanceTemplates) {
			instanceTypes.add(instanceTemplate.getName());
			oss.add(instanceTemplate.getOs());
		}

		DescribeSpotPriceHistoryRequest request = new DescribeSpotPriceHistoryRequest();
		request.setAvailabilityZone(configuration.getAvailabilityZone());
		request.setInstanceTypes(instanceTypes);
		request.setStartTime(startTime);
		request.setEndTime(endTime);
		request.setProductDescriptions(oss);

		DescribeSpotPriceHistoryResult result = ec2Client
				.describeSpotPriceHistory(request);

		SpotPricingManager spotPricingManager = SpotPricingManager
				.getSpotPricingManager();
		synchronized (spotPricingManager) {
			for (int i = 0; i < result.getSpotPriceHistory().size(); i++) {
				SpotPrice price = result.getSpotPriceHistory().get(i);
				String instanceType = price.getInstanceType();
				String os = price.getProductDescription();
				InstanceTemplate instanceTemplate = instanceTemplateManager
						.getInstanceTemplate(instanceType, os);
				if (instanceTemplate != null) {
					double spotPrice = Double.parseDouble(price.getSpotPrice());
					SpotPricingStatus spotPricingStatus = spotPricingManager.getSpotPricingStatus(instanceTemplate);
					Date lastUpdateTime = spotPricingStatus.getLastUpdateTimeStamp();
					if (lastUpdateTime == null || lastUpdateTime.before(price.getTimestamp())) {
						spotPricingStatus.setPrice(spotPrice);
						spotPricingStatus.setLastUpdateTimeStamp(endTime);
						spotPriceLog.info(logFormatter.getMessage(price.getTimestamp().getTime() + " " + instanceTemplate.getName() + " " + price.getSpotPrice()));
					}
				}
			}
		}

		monitorLog.trace(logFormatter.getMessage("In " + monitorName + " Thread"));

		EventGenerator eventGenerator = EventGenerator.getEventGenerator();
		Event newEvent = eventGenerator.generateEvent(
				Events.SPOT_PRICE_UPDATE_EVENT, new HashMap<String, Object>());

		Queue<Event> priorityQueue = EventQueueManager.getEventsQueue();
		priorityQueue.add(newEvent);

		monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, null));
	}

}
