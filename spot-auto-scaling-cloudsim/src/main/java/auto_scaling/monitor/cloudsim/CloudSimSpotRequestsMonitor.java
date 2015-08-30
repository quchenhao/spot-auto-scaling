package auto_scaling.monitor.cloudsim;

import java.io.IOException;
import java.util.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.ex.vm.MonitoredVMex;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.cloud.SpotInstanceStatus;
import auto_scaling.cloud.cloudsim.CloudSimPendingSpotInstanceStatus;
import auto_scaling.cloud.cloudsim.IVMTerminateDistribution;
import auto_scaling.core.SystemStatus;
import auto_scaling.core.cloudsim.ApplicationBrokerManager;
import auto_scaling.core.cloudsim.CloudSimBroker;
import auto_scaling.core.cloudsim.CloudSimSpotPriceSource;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.SpotRequestsMonitor;
import auto_scaling.util.cloudsim.TimeConverter;
import auto_scaling.util.cloudsim.VmFactory;

/** 
* @ClassName: CloudSimSpotRequestsMonitor 
* @Description: the spot requests monitor implementation for cloudSime
* @author Chenhao Qu
* @date 06/06/2015 2:45:09 pm 
*  
*/
public class CloudSimSpotRequestsMonitor extends SpotRequestsMonitor{

	/** 
	* @Fields vmShutDownDelay : the vm shut down delay for the provider
	*/ 
	protected long vmShutDownDelay;
	/** 
	* @Fields vmTerminateDistribution : the vm termination time distribution
	*/ 
	protected IVMTerminateDistribution vmTerminateDistribution;
	
	public CloudSimSpotRequestsMonitor(String monitorName, int monitorInterval, long vmShutDownDelay, IVMTerminateDistribution vmTerminateDistribution) {
		super(monitorName, monitorInterval);
		this.vmShutDownDelay = vmShutDownDelay;
		this.vmTerminateDistribution = vmTerminateDistribution;
	}

	/* (non-Javadoc) 
	* <p>Title: doMonitoring</p> 
	* <p>Description: </p>  
	* @see auto_scaling.monitor.Monitor#doMonitoring() 
	*/
	@Override
	public void doMonitoring() {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		synchronized (systemStatus) {
			Collection<InstanceStatus> spotInstances = systemStatus.getSpotInstances();
			CloudSimSpotPriceSource cloudSimSpotPriceSource = CloudSimSpotPriceSource.getCloudSimSpotPriceSource();
			
			ApplicationBrokerManager applicationBrokerManager = ApplicationBrokerManager.getApplicationBrokerManager();
			CloudSimBroker broker = applicationBrokerManager.getCloudSimBroker();
			
			double time = CloudSim.clock();
			List<InstanceStatus> newlyFullfilledSpotVMs = new ArrayList<InstanceStatus>();
			List<InstanceStatus> newlyTerminatedSpotVMs = new ArrayList<InstanceStatus>();
			List<InstanceStatus> newlyClosedSpotRequests = new ArrayList<InstanceStatus>();
			boolean anyFailed = false;
			
			VmFactory vmFactory = VmFactory.getVmFactory();
			
			for (InstanceStatus instanceStatus : spotInstances) {
				if (instanceStatus instanceof CloudSimPendingSpotInstanceStatus) {
					CloudSimPendingSpotInstanceStatus cloudSimPendingSpotInstanceStatus = (CloudSimPendingSpotInstanceStatus)instanceStatus;
					InstanceTemplate instanceTemplate = cloudSimPendingSpotInstanceStatus.getType();
					try {
						double price = cloudSimSpotPriceSource.getCurrentSpotPrice(instanceTemplate, time);
						double biddingPrice = cloudSimPendingSpotInstanceStatus.getBiddingPrice();
						//if market price exceeds bidding price, terminates vm
						if (price > biddingPrice) {
							newlyClosedSpotRequests.add(cloudSimPendingSpotInstanceStatus);
							anyFailed = true;
							continue;
						}
						
						double fullfillTime = cloudSimPendingSpotInstanceStatus.getStartTime() + cloudSimPendingSpotInstanceStatus.getWaitingTime();
						//if fullfill time passed, start the vm
						if (fullfillTime < time) {
							newlyFullfilledSpotVMs.add(cloudSimPendingSpotInstanceStatus);
						}
						
					} catch (IOException | ParseException e) {
						monitorLog.error(logFormatter.getExceptionString(e));
						continue;
					}
				}
				else if (instanceStatus instanceof SpotInstanceStatus && instanceStatus.getRunningStatus().equals(RunningStatus.RUNNING)) {
					SpotInstanceStatus spotInstanceStatus = (SpotInstanceStatus)instanceStatus;
					InstanceTemplate instanceTemplate = spotInstanceStatus.getType();
					
					try {
						double price = cloudSimSpotPriceSource.getCurrentSpotPrice(instanceTemplate, time);
						if (price > spotInstanceStatus.getBiddingPrice()) {
							newlyTerminatedSpotVMs.add(spotInstanceStatus);
							anyFailed = true;
						}
					} catch (IOException | ParseException e) {
						monitorLog.error(logFormatter.getExceptionString(e));
						continue;
					}
				}
			}
			
			//if there are closed vm requests before fullfillment, fire a spot requests closed before fullfillment event
			if (newlyClosedSpotRequests.size() > 0) {
				for (InstanceStatus instanceStatus : newlyClosedSpotRequests) {
					monitorLog.warn(logFormatter.getMessage("spot request closed: " + ((SpotInstanceStatus)instanceStatus).getSpotRequestId()));
				}
				
				EventGenerator eventGenerator = EventGenerator.getEventGenerator();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put(EventDataName.CLOSED_SPOT_REQUESTS, newlyClosedSpotRequests);
				Event newEvent = eventGenerator.generateEvent(Events.SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT, data);
				Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
				eventQueue.add(newEvent);
				
				String message = "closed spot requests:";
				for (InstanceStatus instanceStatus : newlyClosedSpotRequests) {
					message += " {" + instanceStatus.toString() + "};";
				}
				
				monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, message));
				
			}
			
			//if there are spot requests fullfilled, create the vm in the broker
			if (newlyFullfilledSpotVMs.size() > 0) {
				List<MonitoredVMex> vmLists = new ArrayList<MonitoredVMex>();
				for (InstanceStatus instanceStatus : newlyFullfilledSpotVMs) {
					CloudSimPendingSpotInstanceStatus cloudSimPendingSpotInstanceStatus = (CloudSimPendingSpotInstanceStatus)instanceStatus;
					InstanceTemplate instanceTemplate = cloudSimPendingSpotInstanceStatus.getType();
					MonitoredVMex vm = vmFactory.getVm(instanceTemplate);
					String id = vm.getId() + "";
					String spotRequestId = cloudSimPendingSpotInstanceStatus.getSpotRequestId();
					double fullfillTime = cloudSimPendingSpotInstanceStatus.getStartTime() + cloudSimPendingSpotInstanceStatus.getWaitingTime();
					double biddingPrice = cloudSimPendingSpotInstanceStatus.getBiddingPrice();
					Date launchTime = TimeConverter.convertSimulationTimeToDate(fullfillTime);
					SpotInstanceStatus spotInstanceStatus = new SpotInstanceStatus(id , spotRequestId, null, null, launchTime, instanceTemplate, biddingPrice);
					spotInstanceStatus.setRunningStatus(RunningStatus.PENDING);
					systemStatus.removeInstance(cloudSimPendingSpotInstanceStatus);
					systemStatus.addInstance(spotInstanceStatus);
					vmLists.add(vm);
					monitorLog.info(logFormatter.getMessage("spot instance launched: " + spotInstanceStatus.toString() + " " + fullfillTime));
				}
				
				broker.createVmsAfter(vmLists, 0);
			}
			
			//if there are vms terminated by provider, fire a spot instances termination event
			if (newlyTerminatedSpotVMs.size() > 0) {
				for (InstanceStatus instanceStatus : newlyTerminatedSpotVMs) {
					InstanceTemplate instanceTemplate = instanceStatus.getType();
					List<Vm> vms = new ArrayList<Vm>();
					int id = Integer.parseInt(instanceStatus.getId());
					Vm vm = broker.getVmById(id);
					vms.add(vm);
					long delay = vmShutDownDelay + vmTerminateDistribution.getDelay(instanceTemplate);
					broker.destroyVMsAfter(vms, delay);
					monitorLog.warn(logFormatter.getMessage("provider terminated: " + instanceStatus.getId()));
				}
				
				EventGenerator eventGenerator = EventGenerator.getEventGenerator();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put(EventDataName.TERMINATING_SPOT_INSTANCES, newlyTerminatedSpotVMs);
				Event newEvent = eventGenerator.generateEvent(Events.SPOT_INSTANCES_TERMINATION_EVENT, data);
				Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
				eventQueue.add(newEvent);
				
				String message = "spot terminating instances:";
				for (InstanceStatus instanceStatus : newlyTerminatedSpotVMs) {
					message += " {" + instanceStatus.toString() + "};";
				}
				
				monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, message));
			}
			
			//if there are any vm failures, fire an about to scale up event
			if (anyFailed) {
				EventGenerator eventGenerator = EventGenerator.getEventGenerator();
				Map<String, Object> data = new HashMap<String, Object>();
				Event newEvent = eventGenerator.generateEvent(Events.ABOUT_TO_SCALE_UP_EVENT, data);
				Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
				eventQueue.add(newEvent);
				
				monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, null));
			}
		}
	}

}
