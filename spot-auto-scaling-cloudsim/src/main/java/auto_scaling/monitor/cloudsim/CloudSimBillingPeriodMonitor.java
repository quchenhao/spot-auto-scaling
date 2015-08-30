package auto_scaling.monitor.cloudsim;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.cloudbus.cloudsim.core.CloudSim;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.BillingPeriodMonitor;
import auto_scaling.util.cloudsim.TimeConverter;

/** 
* @ClassName: CloudSimBillingPeriodMonitor 
* @Description: the billing period monitor implementation for cloudSim
* @author Chenhao Qu
* @date 06/06/2015 2:40:45 pm 
*  
*/
public class CloudSimBillingPeriodMonitor extends BillingPeriodMonitor{

	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param monitorInterval the monitor interval
	* @param endingThreshold the billing period ending threshold
	*/
	public CloudSimBillingPeriodMonitor(String monitorName,
			int monitorInterval, int endingThreshold) {
		super(monitorName, monitorInterval, endingThreshold, endingThreshold);
	}

	/* (non-Javadoc) 
	* <p>Title: doMonitoring</p> 
	* <p>Description: </p>  
	* @see auto_scaling.monitor.BillingPeriodMonitor#doMonitoring() 
	*/
	@Override
	public void doMonitoring() {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		Collection<InstanceStatus> allInstances = systemStatus.getAllInstances();
		
		Date rightNow = TimeConverter.convertSimulationTimeToDate(CloudSim.clock());
		for (InstanceStatus instanceStatus : allInstances) {
			
			if (instanceStatus.getRunningStatus().equals(RunningStatus.RUNNING) ) {
				Date launchTime = instanceStatus.getLaunchTime();
				long runningTime = (rightNow.getTime() - launchTime.getTime()) / 60000;
				int passingMinutes = (int) (runningTime % 60);
				if (passingMinutes >= endingThreshold) {
					monitorLog.info(logFormatter.getMessage("In " + monitorName + " Thread"));
					
					EventGenerator eventGenerator = EventGenerator
							.getEventGenerator();
					Map<String, Object> data = new HashMap<String, Object>();
					data.put(EventDataName.INSTANCE_STATUS, instanceStatus);
					Event newEvent = eventGenerator.generateEvent(Events.INSTANCE_BILLING_PERIOD_ENDING_EVENT, data);
					Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
					eventQueue.add(newEvent);
					
					monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, instanceStatus.getId()));
				}
			}
			
		}
	}
}
