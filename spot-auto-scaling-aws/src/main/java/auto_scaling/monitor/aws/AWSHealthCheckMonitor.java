package auto_scaling.monitor.aws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.InstanceStatusSummary;

import auto_scaling.cloud.HealthStatus;
import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.HealthCheckMonitor;
import auto_scaling.util.aws.AmazonClient;

/** 
* @ClassName: AWSHealthCheckMonitor 
* @Description: the health check monitor implementation for Amazon AWS
* @author Chenhao Qu
* @date 06/06/2015 2:32:30 pm 
*  
*/
public class AWSHealthCheckMonitor extends HealthCheckMonitor {
	
	/** 
	* @Fields configuration : the cloud configuration
	*/ 
	protected ICloudConfiguration configuration;
	/** 
	* @Fields ec2Client : the Amazon EC2 client
	*/ 
	protected AmazonEC2Client ec2Client;

	/** 
	* <p>Description: </p> 
	* @param configuration the cloud configuration
	* @param monitorName the monitor name
	* @param monitorInterval the monitoring interval
	*/
	public AWSHealthCheckMonitor(ICloudConfiguration configuration, String monitorName, int monitorInterval) {
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
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		Collection<String> instanceIds = systemStatus.getRunningInstancesIds();
		
		DescribeInstanceStatusRequest request = new DescribeInstanceStatusRequest();
		request.setInstanceIds(instanceIds);
		
		DescribeInstanceStatusResult result = ec2Client.describeInstanceStatus(request);
		
		List<InstanceStatus> impairedInstances = new ArrayList<InstanceStatus>();
		
		for (com.amazonaws.services.ec2.model.InstanceStatus instanceStatus : result.getInstanceStatuses()) {
			InstanceStatusSummary instanceStatusSummary = instanceStatus.getInstanceStatus();
			InstanceStatusSummary systemStatusSummary = instanceStatus.getSystemStatus();
			
			
			if (instanceStatusSummary.getStatus().equals(HealthStatus.SystemStatus_Impaired) || systemStatusSummary.getStatus().equals(HealthStatus.SystemStatus_Impaired)) {
				InstanceStatus localStatus = systemStatus.getInstanceStatusByInstanceId(instanceStatus.getInstanceId());
				localStatus.setRunningStatus(RunningStatus.IMPAIRED);
				impairedInstances.add(localStatus);
			}
		}
		
		if (impairedInstances.size() > 0) {
			monitorLog.trace(logFormatter.getMessage("In " + monitorName + " Thread"));
			
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(EventDataName.IMPAIRED_INSTANCES, impairedInstances);
			
			Event newEvent = eventGenerator.generateEvent(Events.INSTANCES_IMPAIRED_EVENT, data);
			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);
			
			String instances = "";
			for (InstanceStatus instanceStatus : impairedInstances) {
				instances += " " + instanceStatus.getId();
			}
			
			monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, instances));
		}
	}

}
