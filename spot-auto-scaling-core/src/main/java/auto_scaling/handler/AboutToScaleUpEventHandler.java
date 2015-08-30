package auto_scaling.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.core.FaultTolerantLevel;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.event.AboutToScaleUpEvent;
import auto_scaling.scaling_strategy.IScalingUtil;
import auto_scaling.scaling_strategy.IScalingUpSystemStatusCalculator;
import auto_scaling.scaling_strategy.TargetSystemStatus;

/** 
* @ClassName: AboutToScaleUpEventHandler 
* @Description: the handler that handles about to scale up event
* @author Chenhao Qu
* @date 05/06/2015 10:04:04 pm 
*  
*/
public class AboutToScaleUpEventHandler extends EventHandler {

	/** 
	* @Fields scalingPoliciesConfiguration : the scaling policies configuration
	*/ 
	protected IScalingPoliciesConfiguration scalingPoliciesConfiguration;

	public AboutToScaleUpEventHandler()
			throws IllegalArgumentException {
		super(AboutToScaleUpEvent.class);
	}

	/** 
	* @Title: setScalingPoliciesConfiguration 
	* @Description: set the scaling policies configuration
	* @param scalingPoliciesConfiguration the scaling policies configuration
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
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		IScalingUtil scalingUtil;
		IScalingUpSystemStatusCalculator scalingUpSystemStatusCalculator;
		synchronized (scalingPoliciesConfiguration) {
			scalingUtil = scalingPoliciesConfiguration.getScalingUtil();
			scalingUpSystemStatusCalculator = scalingPoliciesConfiguration.getScalingUpSystemStatusCalculator();
		}
		
		synchronized (systemStatus) {
			long totalRequiredResources = systemStatus.getTotalNumOfRequests();
			Collection<InstanceStatus> onDemandInstances = systemStatus.getNominalOnDemandInstances();
			Map<InstanceTemplate, Collection<InstanceStatus>> spotGroups = systemStatus.getNominalSpotGroups();
			FaultTolerantLevel faultTolerantLevel = systemStatus.getFaultTolerantLevel();
			boolean isSpotEnable = systemStatus.isSpotEnabled();
			//check whether the current resources is enough to process the current request rate
			if (!scalingUtil.isSystemWideResoucesRequirementSatisfied(totalRequiredResources, onDemandInstances, spotGroups, faultTolerantLevel, isSpotEnable)) {
				//if not enough, calculates the suitable target system status
				TargetSystemStatus targetSystemStatus = scalingUpSystemStatusCalculator
						.calculateTargetSystemStatus(systemStatus);
				//generate the target system status for further processing
				EventGenerator eventGenerator = EventGenerator
						.getEventGenerator();
				Map<String, Object> data = new HashMap<String, Object>();
				data.put(EventDataName.TARGET_SYSTEM_STATUS, targetSystemStatus);
				Event newEvent = eventGenerator.generateEvent(
						Events.TARGET_SYSTEM_STATUS_EVENT, data);
				Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
				eventQueue.add(newEvent);

				eventHandlerLog.info(logFormatter.getGenerateEventLogString(
						newEvent, targetSystemStatus.toString()));
			}
		}
	}
}
