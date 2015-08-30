package auto_scaling.event;

import java.util.Collection;
import java.util.Map;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.ResourceType;
import auto_scaling.scaling_strategy.ScalingPlan;
import auto_scaling.scaling_strategy.TargetSystemStatus;

/** 
* @ClassName: EventGenerator 
* @Description: the generator that produces events according to inputs
* @author Chenhao Qu
* @date 05/06/2015 9:34:49 pm 
*  
*/
public class EventGenerator {

	/** 
	* @Fields eventGenerator : the global event generator
	*/ 
	private static EventGenerator eventGenerator;
	/** 
	* @Fields criticalLevels : the map of event types to critical levels
	*/ 
	private Map<String, Integer> criticalLevels;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	private EventGenerator() {}
	
	/** 
	* @Title: getEventGenerator 
	* @Description: get the event generator
	* @return the event generator
	*/
	public static EventGenerator getEventGenerator() {
		if(eventGenerator == null) {
			eventGenerator = new EventGenerator();
		}
		
		return eventGenerator;
	}
	
	/** 
	* @Title: setCriticalLevels 
	* @Description: set the critical levels map
	* @param criticalLevels the new critical levels map
	*/
	public void setCriticalLevels(Map<String, Integer> criticalLevels) {
		if (criticalLevels == null) {
			throw new NullPointerException("critical levels cannot be null");
		}
		this.criticalLevels = criticalLevels;
	}
	
	/** 
	* @Title: generateEvent 
	* @Description: generate the event according to event type and its parameters
	* @param eventName the event name
	* @param data all input parameters
	* @return the generated event
	* @throws IllegalArgumentException
	*/
	public Event generateEvent(String eventName, Map<String, Object> data) throws IllegalArgumentException{
		if (eventName == null || data == null) {
			throw new IllegalArgumentException(eventName + " " + data);
		}
		
		if (eventName.equals(Events.RESOURCE_REQUIREMENT_UPDATE_EVENT)) {
			if (!data.containsKey(EventDataName.RESOURCE_TYPE)) {
				throw new IllegalArgumentException(data + " doesn't containt " + EventDataName.RESOURCE_TYPE);
			}
			return new ResourceRequirementUpdateEvent(criticalLevels.get(Events.RESOURCE_REQUIREMENT_UPDATE_EVENT), (ResourceType)data.get(EventDataName.RESOURCE_TYPE));
		}
		
		if (eventName.equals(Events.ABOUT_TO_SCALE_UP_EVENT)) {
			return new AboutToScaleUpEvent(criticalLevels.get(Events.ABOUT_TO_SCALE_UP_EVENT));
		}
		
		if (eventName.equals(Events.SPOT_PRICE_UPDATE_EVENT)) {
			return new SpotPriceUpdateEvent(criticalLevels.get(Events.SPOT_PRICE_UPDATE_EVENT));
		}
		
		if (eventName.equals(Events.SCALING_EVENT)) {
			if (!data.containsKey(EventDataName.SCALING_PLAN)) {
				throw new IllegalArgumentException(data + " doesn't containt " + EventDataName.SCALING_PLAN);
			}
			
			return new ScalingEvent(criticalLevels.get(Events.SCALING_EVENT), (ScalingPlan)data.get(EventDataName.SCALING_PLAN));
		}
		
		if (eventName.equals(Events.INSTANCE_BILLING_PERIOD_ENDING_EVENT)) {
			if (!data.containsKey(EventDataName.INSTANCE_STATUS)) {
				throw new IllegalArgumentException(data + " doesn't containt " + EventDataName.INSTANCE_STATUS);
			}
			
			return new InstanceBillingPeriodEndingEvent(criticalLevels.get(Events.INSTANCE_BILLING_PERIOD_ENDING_EVENT), (InstanceStatus) data.get(EventDataName.INSTANCE_STATUS));
		}
		
		if (eventName.equals(Events.INSTANCES_ONLINE_EVENT)) {
			if (!data.containsKey(EventDataName.ONLINE_INSTANCES)) {
				throw new IllegalArgumentException(data + " doesn't containt " + EventDataName.ONLINE_INSTANCES);
			}
			
			@SuppressWarnings("unchecked")
			Collection<InstanceStatus> onlineInstances = (Collection<InstanceStatus>) data.get(EventDataName.ONLINE_INSTANCES);
			
			return new InstancesOnlineEvent(criticalLevels.get(Events.INSTANCES_ONLINE_EVENT), onlineInstances);
		}
		
		if (eventName.equals(Events.INSTANCES_IMPAIRED_EVENT)) {
			if (!data.containsKey(EventDataName.IMPAIRED_INSTANCES)) {
				throw new IllegalArgumentException(data + " doesn't containt " + EventDataName.IMPAIRED_INSTANCES);
			}
			
			@SuppressWarnings("unchecked")
			Collection<InstanceStatus> impairedInstances = (Collection<InstanceStatus>) data.get(EventDataName.IMPAIRED_INSTANCES);
			
			return new InstancesImpairedEvent(criticalLevels.get(Events.INSTANCES_IMPAIRED_EVENT), impairedInstances);
		}
		
		if (eventName.equals(Events.SPOT_INSTANCES_TERMINATION_EVENT)) {
			if (!data.containsKey(EventDataName.TERMINATING_SPOT_INSTANCES)) {
				throw new IllegalArgumentException(data + " doesn't containt " + EventDataName.TERMINATING_SPOT_INSTANCES);
			}
			
			@SuppressWarnings("unchecked")
			Collection<InstanceStatus> terminatedInstances = (Collection<InstanceStatus>) data.get(EventDataName.TERMINATING_SPOT_INSTANCES);
			
			return new SpotInstancesTerminationEvent(criticalLevels.get(Events.SPOT_INSTANCES_TERMINATION_EVENT), terminatedInstances);
		}
		
		if (eventName.equals(Events.TARGET_SYSTEM_STATUS_EVENT)) {
			if (!data.containsKey(EventDataName.TARGET_SYSTEM_STATUS)) {
				throw new IllegalArgumentException(data + " doesn't containt " + EventDataName.TARGET_SYSTEM_STATUS);
			}
			
			TargetSystemStatus targetSystemStatus = (TargetSystemStatus)data.get(EventDataName.TARGET_SYSTEM_STATUS);
			
			return new TargetSystemStatusEvent(criticalLevels.get(Events.TARGET_SYSTEM_STATUS_EVENT), targetSystemStatus);
		}
		
		if (eventName.equals(Events.SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT)) {
			if (!data.containsKey(EventDataName.CLOSED_SPOT_REQUESTS)) {
				throw new IllegalArgumentException(data + " doesn't containt " + EventDataName.CLOSED_SPOT_REQUESTS);
			}
			
			@SuppressWarnings("unchecked")
			Collection<InstanceStatus> closedSpotRequests = (Collection<InstanceStatus>)data.get(EventDataName.CLOSED_SPOT_REQUESTS);
			
			return new SpotRequestsClosedBeforeFullfillmentEvent(criticalLevels.get(Events.SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT), closedSpotRequests);
		}
		
		return null;
	}
	
}
