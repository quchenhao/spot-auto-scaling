package auto_scaling.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.cloud.SpotInstanceStatus;
import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.event.InstanceBillingPeriodEndingEvent;
import auto_scaling.scaling_strategy.IScalingDownSystemStatusCalculator;
import auto_scaling.scaling_strategy.ScalingPlan;
import auto_scaling.scaling_strategy.TargetSystemStatus;

/** 
* @ClassName: InstanceBillingPeriodEndingEventHandler 
* @Description: the handler that handles instance billing period ending event
* @author Chenhao Qu
* @date 05/06/2015 10:16:45 pm 
*  
*/
public class InstanceBillingPeriodEndingEventHandler extends EventHandler {

	/** 
	* @Fields scalingPoliciesConfiguration : the scaling policies configuration
	*/ 
	protected IScalingPoliciesConfiguration scalingPoliciesConfiguration;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public InstanceBillingPeriodEndingEventHandler() {
		super(InstanceBillingPeriodEndingEvent.class);
	}

	/** 
	* @Title: setScalingPoliciesConfiguration 
	* @Description: set the scaling policies configuration
	* @param scalingPoliciesConfiguration
	*/
	public synchronized void setScalingPoliciesConfiguration(IScalingPoliciesConfiguration scalingPoliciesConfiguration) {
		if (scalingPoliciesConfiguration == null) {
			throw new NullPointerException("scaling policies configuration cannot be null");
		}
		this.scalingPoliciesConfiguration = scalingPoliciesConfiguration;
	}
	
	/* (non-Javadoc) 
	* <p>Title: doHandling</p> 
	* <p>Description: </p> 
	* @param event 
	* @see auto_scaling.handler.EventHandler#doHandling(auto_scaling.event.Event) 
	*/
	@Override
	protected synchronized void doHandling(Event event) {
		InstanceBillingPeriodEndingEvent billingPeriodEndingEvent = (InstanceBillingPeriodEndingEvent)event;
		InstanceStatus instanceStatus = billingPeriodEndingEvent.getBillingPeirodEndingInstance();
		
		if (instanceStatus instanceof SpotInstanceStatus) {
			handleSpotInstance((SpotInstanceStatus)instanceStatus);
		}
		else if (instanceStatus instanceof OnDemandInstanceStatus) {
			handleOnDemandInstance((OnDemandInstanceStatus)instanceStatus);
		}
		
		
	}

	/** 
	* @Title: handleOnDemandInstance 
	* @Description: handle on demand instance billing period ending
	* @param instanceStatus the on demand instance
	*/
	private void handleOnDemandInstance(OnDemandInstanceStatus instanceStatus) {
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		IScalingDownSystemStatusCalculator scalingDownSystemStatusCalculator = scalingPoliciesConfiguration.getScalingDownSystemStatusCalculator();
		synchronized (systemStatus) {
			//calculate the new target system status
			TargetSystemStatus targetSystemStatus = scalingDownSystemStatusCalculator.calculateTargetSystemStatus(systemStatus, instanceStatus);
			
			//if new target system status is generated, then file a target system status event for further processing
			if (targetSystemStatus != null) {
				
				EventGenerator eventGenerator = EventGenerator.getEventGenerator();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put(EventDataName.TARGET_SYSTEM_STATUS, targetSystemStatus);
				Event newEvent = eventGenerator.generateEvent(Events.TARGET_SYSTEM_STATUS_EVENT, data);
				
				Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
				eventQueue.add(newEvent);
				
				eventHandlerLog.info(logFormatter.getGenerateEventLogString(newEvent, null));
			}
		}
		
	}

	/** 
	* @Title: handleSpotInstance 
	* @Description: handle spot instance billing period ending
	* @param instanceStatus the spot instance
	*/
	private void handleSpotInstance(SpotInstanceStatus instanceStatus) {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		IScalingDownSystemStatusCalculator scalingDownSystemStatusCalculator = scalingPoliciesConfiguration.getScalingDownSystemStatusCalculator();
		synchronized (systemStatus) {
			//check whether the spot instance can be terminated 
			ScalingPlan scalingPlan = scalingDownSystemStatusCalculator.calculateSpotScalingDownPlan(systemStatus, instanceStatus);
		
			//if can, fire a scaling event to terminate the instance
			if (scalingPlan != null) {
				
				EventGenerator eventGenerator = EventGenerator.getEventGenerator();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put(EventDataName.SCALING_PLAN, scalingPlan);
				Event newEvent = eventGenerator.generateEvent(Events.SCALING_EVENT, data);
			
				Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
				eventQueue.add(newEvent);
			
				eventHandlerLog.info(logFormatter.getGenerateEventLogString(newEvent, null));
			}
		}
		
		
		
	}

}
