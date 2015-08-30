package auto_scaling.monitor.aws;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.VMStatusMonitor;
import auto_scaling.util.aws.AmazonClient;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;

/** 
* @ClassName: AWSVMStatusMonitor 
* @Description: the vm status implementation for Amazon AWS 
* @author Chenhao Qu
* @date 06/06/2015 2:38:44 pm 
*  
*/
public class AWSVMStatusMonitor extends VMStatusMonitor{

	/** 
	* @Fields ec2Client : Amazon EC2 client
	*/ 
	protected AmazonEC2Client ec2Client;
	/** 
	* @Fields configuration : the cloud configuration
	*/ 
	protected ICloudConfiguration configuration;
	
	/** 
	* <p>Description: </p> 
	* @param configuration the cloud configuration
	* @param monitorName the monitor name
	* @param monitorInterval the monitoring interval
	*/
	public AWSVMStatusMonitor(ICloudConfiguration configuration, String monitorName, int monitorInterval) {
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
		
		Collection<String> instanceIds = systemStatus.getConfirmedInstancesIds();
		
		if (instanceIds.size() == 0) {
			return;
		}
		
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		request.setInstanceIds(instanceIds);
		
		DescribeInstancesResult result = ec2Client.describeInstances(request);
		
		List<InstanceStatus> onlineInstances = new ArrayList<InstanceStatus>();
		
		for (Reservation reservation : result.getReservations()) {
			processReservation(reservation, onlineInstances);
		}
		
		if (onlineInstances.size() > 0) {
			
			monitorLog.trace(logFormatter.getMessage("In " + monitorName + " Thread"));
			
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(EventDataName.ONLINE_INSTANCES, onlineInstances);
			Event newEvent = eventGenerator.generateEvent(Events.INSTANCES_ONLINE_EVENT, data);
			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);
			
			String instances = "";
			for (InstanceStatus instanceStatus : onlineInstances) {
				instances += " " + instanceStatus.getId();
			}
			
			monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, instances));
		}
	}

	/**
	 * @Title: processReservation 
	 * @Description: 
	 * @param reservation
	 * @param startedInstances
	 * @throws
	 */
	private void processReservation(Reservation reservation, List<InstanceStatus> startedInstances) {
		for (Instance instance : reservation.getInstances()) {
			processInstance(instance, startedInstances);
		}
	}

	/**
	 * @Title: processInstance 
	 * @Description: 
	 * @param instance
	 * @param startedInstances
	 * @throws
	 */
	private void processInstance(Instance instance, List<InstanceStatus> startedInstances) {
		String instanceId = instance.getInstanceId();
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		InstanceStatus instanceStatus = systemStatus.getInstanceStatusByInstanceId(instanceId);
		InstanceState currentState = instance.getState();
		String runningStatus = currentState.getName();
		
		if (instanceStatus.getRunningStatus().equals(RunningStatus.PENDING) && runningStatus.equals(RunningStatus.RUNNING) ) {
			if (instanceStatus.getPublicUrl() == null || instanceStatus.getPublicUrl().equals("")) {
				instanceStatus.setPublicUrl(instance.getPublicDnsName());
			}
			boolean booted = checkBooted(instanceStatus);
			if (booted) {
				instanceStatus.setRunningStatus(RunningStatus.RUNNING);
				startedInstances.add(instanceStatus);
				monitorLog.info(logFormatter.getMessage("instance running: " + instanceStatus.getId()));
			}
		}
		
		if (instanceStatus.getRunningStatus().equals(RunningStatus.SHUTTING_DOWN) || instanceStatus.getRunningStatus().equals(RunningStatus.STOPPING)) {
			if (runningStatus.equals(RunningStatus.TERMINATED)  || runningStatus.equals(RunningStatus.STOPPED) ) {
				systemStatus.removeInstance(instanceStatus);
				monitorLog.info(logFormatter.getMessage("terminated or stopped: " + instanceStatus.getId()));
			}
		}
		
	}

	private boolean checkBooted(InstanceStatus instanceStatus) {
	    InetAddress inet;
		try {
			inet = InetAddress.getByName(instanceStatus.getPublicUrl());
			boolean bool = inet.isReachable(5000);
			return bool;
		} catch (IOException e) {
			return false;
		}
	}

}
