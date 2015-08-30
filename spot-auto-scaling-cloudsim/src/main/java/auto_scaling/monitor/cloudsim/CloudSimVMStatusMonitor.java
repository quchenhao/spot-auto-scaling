package auto_scaling.monitor.cloudsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.cloudbus.cloudsim.ex.vm.MonitoredVMex;
import org.cloudbus.cloudsim.ex.vm.VMStatus;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.core.SystemStatus;
import auto_scaling.core.cloudsim.ApplicationBrokerManager;
import auto_scaling.core.cloudsim.CloudSimBroker;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.VMStatusMonitor;
import auto_scaling.util.InstanceFilter;

/** 
* @ClassName: CloudSimVMStatusMonitor 
* @Description: the vm status monitor implementation for cloudSim
* @author Chenhao Qu
* @date 06/06/2015 2:56:59 pm 
*  
*/
public class CloudSimVMStatusMonitor extends VMStatusMonitor {

	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param monitorInterval the monitoring interval
	*/
	public CloudSimVMStatusMonitor(String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
	}

	/* (non-Javadoc) 
	* <p>Title: doMonitoring</p> 
	* <p>Description: </p>  
	* @see auto_scaling.monitor.Monitor#doMonitoring() 
	*/
	@Override
	public void doMonitoring() {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();

		Collection<InstanceStatus> confirmedInstances = InstanceFilter
				.getConfirmedInstances(systemStatus.getAllInstances());

		ApplicationBrokerManager applicationBrokerManager = ApplicationBrokerManager
				.getApplicationBrokerManager();
		CloudSimBroker cloudSimBroker = applicationBrokerManager
				.getCloudSimBroker();

		Collection<InstanceStatus> onlineInstances = new ArrayList<InstanceStatus>();
		for (InstanceStatus instanceStatus : confirmedInstances) {
			int id = Integer.parseInt(instanceStatus.getId());
			MonitoredVMex vm = (MonitoredVMex) cloudSimBroker.getVmById(id);
			if (vm != null) {
				String runningStatus = instanceStatus.getRunningStatus();
				VMStatus vmStatus = vm.getStatus();
				if (vmStatus.equals(VMStatus.RUNNING)
						&& runningStatus.equals(RunningStatus.PENDING) ) {
					instanceStatus.setRunningStatus(RunningStatus.RUNNING);
					onlineInstances.add(instanceStatus);
					monitorLog.info(logFormatter.getMessage("instance running: "
							+ instanceStatus.getId()));
				} else if (runningStatus.equals(RunningStatus.SHUTTING_DOWN) 
						|| runningStatus.equals(RunningStatus.STOPPING) ) {
					if (vmStatus.equals(VMStatus.TERMINATED)) {
						systemStatus.removeInstance(instanceStatus);
						monitorLog.info(logFormatter.getMessage("terminated or stopped: "
								+ instanceStatus.getId()));
					}
				}
			}
		}

		if (onlineInstances.size() > 0) {
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(EventDataName.ONLINE_INSTANCES, onlineInstances);
			Event newEvent = eventGenerator.generateEvent(
					Events.INSTANCES_ONLINE_EVENT, data);
			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);

			String instances = "";
			for (InstanceStatus instanceStatus : onlineInstances) {
				instances += " " + instanceStatus.getId();
			}

			monitorLog.info(logFormatter.getGenerateEventLogString(newEvent,
					instances));
		}
	}

}
