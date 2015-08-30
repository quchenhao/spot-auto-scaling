package auto_scaling.monitor.cloudsim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import org.cloudbus.cloudsim.ex.vm.MonitoredVMex;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.ResourceType;
import auto_scaling.core.SystemStatus;
import auto_scaling.core.cloudsim.ApplicationBrokerManager;
import auto_scaling.core.cloudsim.CloudSimBroker;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.ResourceMonitor;
import auto_scaling.util.InstanceFilter;

/** 
* @ClassName: CloudSimCPUUtilizationMonitor 
* @Description: the cpu utilization monitor implementation for cloudSim
* @author Chenhao Qu
* @date 06/06/2015 2:41:47 pm 
*  
*/
public class CloudSimCPUUtilizationMonitor extends ResourceMonitor{

	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param metric the metric name
	* @param monitorInterval the monitoring interval
	*/
	public CloudSimCPUUtilizationMonitor(String monitorName, String metric,
			int monitorInterval) {
		super(monitorName, metric, monitorInterval);
	}

	/* (non-Javadoc) 
	* <p>Title: doMonitoring</p> 
	* <p>Description: </p>  
	* @see auto_scaling.monitor.Monitor#doMonitoring() 
	*/
	@Override
	public void doMonitoring() {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		Collection<InstanceStatus> instances = InstanceFilter.getAttachedInstances(systemStatus.getAllInstances());
		
		ApplicationBrokerManager applicationBrokerManager = ApplicationBrokerManager.getApplicationBrokerManager();
		CloudSimBroker cloudSimBroker = applicationBrokerManager.getCloudSimBroker();
		
		String CPU = ResourceType.CPU.getName();
		for (InstanceStatus instanceStatus : instances) {
			String idString = instanceStatus.getId();
			MonitoredVMex vm = (MonitoredVMex)cloudSimBroker.getVmById(Integer.parseInt(idString));
			double utilization = vm.getCPUUtil();
			instanceStatus.setResourceConsumptionValue(CPU, utilization);
		}
		
		if (instances.size() > 0) {
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(EventDataName.RESOURCE_TYPE, ResourceType.CPU);
			Event newEvent = eventGenerator.generateEvent(
					Events.RESOURCE_REQUIREMENT_UPDATE_EVENT, data);

			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);
			monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, CPU));
		}
	}

}
