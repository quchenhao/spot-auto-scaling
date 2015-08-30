package auto_scaling.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.cloud.SpotInstanceStatus;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.event.InstancesImpairedEvent;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.scaling_strategy.ScalingPlan;
import auto_scaling.scaling_strategy.StartOnDemandRequest;
import auto_scaling.scaling_strategy.StartSpotRequest;
import auto_scaling.scaling_strategy.TerminateVMsRequest;

/** 
* @ClassName: InstancesImpairedEventHandler 
* @Description: the handler that handles inatances impaired event
* @author Chenhao Qu
* @date 05/06/2015 10:22:57 pm 
*  
*/
public class InstancesImpairedEventHandler extends EventHandler{
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public InstancesImpairedEventHandler() {
		super(InstancesImpairedEvent.class);
	}

	/* (non-Javadoc) 
	* <p>Title: doHandling</p> 
	* <p>Description: </p> 
	* @param event 
	* @see auto_scaling.handler.EventHandler#doHandling(auto_scaling.event.Event) 
	*/
	@Override
	protected void doHandling(Event event) {
		InstancesImpairedEvent instancesImpairedEvent = (InstancesImpairedEvent)event;
		Collection<InstanceStatus> impairedInstances = instancesImpairedEvent.getImpairedInstances();
		//detach the impaired instances from the load balancer
		LoadBalancer loadBalancer = LoadBalancer.getLoadBalancer();
		try {
			loadBalancer.detach(impairedInstances);
		} catch (IOException e) {
			eventHandlerLog.error(logFormatter.getExceptionString(e));
		}
		
		
		//repair the impaired instances by creating a scaling plan
		Map<InstanceTemplate, StartSpotRequest> spotRequests = new HashMap<InstanceTemplate, StartSpotRequest>();
		StartOnDemandRequest onDemandRequest = null;
		
		TerminateVMsRequest terminateRequest = new TerminateVMsRequest(impairedInstances);
		
		for (InstanceStatus impairedInstance : impairedInstances) {
			if (impairedInstance instanceof OnDemandInstanceStatus) {
				if (onDemandRequest == null) {
					onDemandRequest = new StartOnDemandRequest(impairedInstance.getType(), 1);
				}
				else {
					onDemandRequest.increaseNum();
				}
			}
			else if (impairedInstance instanceof SpotInstanceStatus) {
				InstanceTemplate type = impairedInstance.getType();
				if (spotRequests.containsKey(type)) {
					StartSpotRequest spotRequest = spotRequests.get(type);
					spotRequest.increaseNum();
				}
				else {
					double biddingPrice = ((SpotInstanceStatus)impairedInstance).getBiddingPrice();
					StartSpotRequest spotRequest = new StartSpotRequest(type, 1, biddingPrice);
					spotRequests.put(type, spotRequest);
				}
			}
		}
		
		ScalingPlan scalingPlan = new ScalingPlan(spotRequests.values(), onDemandRequest, terminateRequest);
		
		//fire a scaling plan event for further processing
		EventGenerator eventGenerator = EventGenerator.getEventGenerator();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(EventDataName.SCALING_PLAN, scalingPlan);
		Event newEvent = eventGenerator.generateEvent(Events.SCALING_EVENT, data);
		Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
		eventQueue.add(newEvent);
		
		eventHandlerLog.info(logFormatter.getGenerateEventLogString(newEvent, null));
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		synchronized (systemStatus) {
			eventHandlerLog.info(logFormatter.getMessage("total available capacity: " + systemStatus.getAvailableCapacity()));
			eventHandlerLog.info(logFormatter.getMessage("total nominal capacity: " + systemStatus.getNominalCapacity()));
		}
	}

}
