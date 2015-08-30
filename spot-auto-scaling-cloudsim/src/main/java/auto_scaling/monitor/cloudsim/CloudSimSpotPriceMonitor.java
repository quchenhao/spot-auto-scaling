package auto_scaling.monitor.cloudsim;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.text.ParseException;
import java.util.Collection;

import org.cloudbus.cloudsim.core.CloudSim;

import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.SpotPricingStatus;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.core.SpotPricingManager;
import auto_scaling.core.cloudsim.CloudSimSpotPriceSource;
import auto_scaling.event.Event;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.SpotPriceMonitor;
import auto_scaling.util.cloudsim.TimeConverter;

/** 
* @ClassName: CloudSimSpotPriceMonitor 
* @Description: the spot price monitor implementation for cloudSim
* @author Chenhao Qu
* @date 06/06/2015 2:44:07 pm 
*  
*/
public class CloudSimSpotPriceMonitor extends SpotPriceMonitor{

	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param monitorInterval the monitoring interval
	*/
	public CloudSimSpotPriceMonitor(String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
	}

	/* (non-Javadoc) 
	* <p>Title: doMonitoring</p> 
	* <p>Description: </p>  
	* @see auto_scaling.monitor.Monitor#doMonitoring() 
	*/
	@Override
	public void doMonitoring() {
		CloudSimSpotPriceSource cloudSimSpotPriceSource = CloudSimSpotPriceSource.getCloudSimSpotPriceSource();
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
		
		Collection<InstanceTemplate> allInstanceTemplates = instanceTemplateManager.getAllInstanceTemplates();
		SpotPricingManager spotPricingManager = SpotPricingManager.getSpotPricingManager();
		
		double time = CloudSim.clock();
		Date date = TimeConverter.convertSimulationTimeToDate(time);
		for (InstanceTemplate instanceTemplate : allInstanceTemplates) {
			SpotPricingStatus spotPricingStatus = spotPricingManager.getSpotPricingStatus(instanceTemplate);
			try {
				double price = cloudSimSpotPriceSource.getCurrentSpotPrice(instanceTemplate, time);
				spotPricingStatus.setPrice(price);
				spotPricingStatus.setLastUpdateTimeStamp(date);
			} catch (IOException | ParseException e) {
				monitorLog.error(logFormatter.getExceptionString(e));
			}
		}
		
		EventGenerator eventGenerator = EventGenerator.getEventGenerator();
		Event newEvent = eventGenerator.generateEvent(
				Events.SPOT_PRICE_UPDATE_EVENT, new HashMap<String, Object>());

		Queue<Event> priorityQueue = EventQueueManager.getEventsQueue();
		priorityQueue.add(newEvent);

		monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, null));
	}

}
