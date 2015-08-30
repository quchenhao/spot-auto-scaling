package auto_scaling.monitor.aws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.ResourceType;
import auto_scaling.cloud.UnSupportedResourceException;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.ResourceMonitor;
import auto_scaling.util.InstanceFilter;
import auto_scaling.util.MetricResourceMapper;
import auto_scaling.util.aws.AmazonClient;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

/** 
* @ClassName: AWSCloudWatchResourceMonitor 
* @Description: the resource monitor implementation for Amazon AWS using Cloud Watch
* @author Chenhao Qu
* @date 06/06/2015 2:28:37 pm 
*  
*/
public class AWSCloudWatchResourceMonitor extends ResourceMonitor {

	/** 
	* @Fields cloudWatchClient : the cloud watch client
	*/ 
	protected AmazonCloudWatchClient cloudWatchClient;
	/** 
	* @Fields statistics : the statistics
	*/ 
	protected Collection<String> statistics;
	/** 
	* @Fields period : the monitoring window
	*/ 
	protected int period;
	/** 
	* @Fields nameSpace : the metric name space
	*/ 
	protected String nameSpace;
	/** 
	* @Fields resourceType : the resource type
	*/ 
	protected ResourceType resourceType;

	/** 
	* <p>Description: </p> 
	* @param configuration the cloud configuration
	* @param monitorName the monitor name
	* @param metric the metric name
	* @param nameSpace the metric name space
	* @param statistics the statistics
	* @param monitorInterval the monitoring interval
	* @param period the monitoring window
	* @throws UnSupportedResourceException 
	*/
	public AWSCloudWatchResourceMonitor(ICloudConfiguration configuration,
			String monitorName, String metric, String nameSpace,
			Collection<String> statistics, int monitorInterval, int period)
			throws UnSupportedResourceException {
		super(monitorName, metric, monitorInterval);
		this.cloudWatchClient = AmazonClient
				.getAmazonCloudWatchClient(configuration);
		this.nameSpace = nameSpace;
		this.period = period;
		this.statistics = statistics;
		this.resourceType = MetricResourceMapper.getResourceType(metricName);
	}

	/* (non-Javadoc) 
	* <p>Title: doMonitoring</p> 
	* <p>Description: </p>  
	* @see auto_scaling.monitor.Monitor#doMonitoring() 
	*/
	@Override
	public synchronized void doMonitoring() {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();

		Collection<InstanceStatus> instances = InstanceFilter
				.getAttachedInstances(systemStatus.getAllInstances());

		Calendar calendar = Calendar.getInstance();

		Date endTime = calendar.getTime();

		Date startTime = new Date(endTime.getTime() - monitorInterval * 1000);

		for (InstanceStatus instanceStatus : instances) {
			
			Dimension dimension = new Dimension();
			dimension.setName("InstanceId");

			dimension.setValue(instanceStatus.getId());

			GetMetricStatisticsRequest request = new GetMetricStatisticsRequest();
			
			request.setStartTime(startTime);
			request.setNamespace(nameSpace);
			request.setPeriod(period);

			Collection<Dimension> dimensions = new ArrayList<Dimension>(1);
			dimensions.add(dimension);

			request.setDimensions(dimensions);
			request.setMetricName(metricName);
			request.setUnit("Percent");
			request.setStatistics(statistics);

			request.setEndTime(endTime);

			try {
				GetMetricStatisticsResult results = cloudWatchClient
						.getMetricStatistics(request);
				
				List<Datapoint> datapoints = results.getDatapoints();
				
				if (datapoints.size() == 0) {
					continue;
				}

				double total = 0;

				for (Datapoint datapoint : datapoints) {
					double value = datapoint.getAverage();
					total += value;
				}

				double average = total / datapoints.size();
				double utilization = average / 100;
				instanceStatus.setResourceConsumptionValue(
						resourceType.getName(), utilization);
				
			} catch (AmazonServiceException e) {
				monitorLog.error(logFormatter.getMessage("In " + monitorName
						+ " Thread"));
				monitorLog.error(logFormatter.getMessage("HTTP Status Code: "
						+ e.getStatusCode()));
				monitorLog.error(logFormatter.getMessage("AWS Error Code: "
						+ e.getErrorCode()));
				monitorLog.error(logFormatter.getMessage("Error Type: "
						+ e.getErrorType()));
				monitorLog.error(logFormatter.getMessage("Request ID: "
						+ e.getRequestId()));
				monitorLog.error(logFormatter.getExceptionString(e));
			}
		}

		if (instances.size() > 0) {
			monitorLog.trace(logFormatter.getMessage("In " + monitorName
					+ " Thread"));
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(EventDataName.RESOURCE_TYPE, resourceType);
			Event newEvent = eventGenerator.generateEvent(
					Events.RESOURCE_REQUIREMENT_UPDATE_EVENT, data);

			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);
			monitorLog.info(logFormatter.getGenerateEventLogString(newEvent,
					resourceType.getName()));
		}

	}

}
