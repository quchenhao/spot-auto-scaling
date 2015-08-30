package auto_scaling.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
import auto_scaling.event.TargetSystemStatusEvent;
import auto_scaling.scaling_strategy.IScalingUtil;
import auto_scaling.scaling_strategy.ScalingPlan;
import auto_scaling.scaling_strategy.SpotBiddingInfo;
import auto_scaling.scaling_strategy.StartOnDemandRequest;
import auto_scaling.scaling_strategy.StartSpotRequest;
import auto_scaling.scaling_strategy.TargetSystemStatus;
import auto_scaling.scaling_strategy.TerminateVMsRequest;
import auto_scaling.scaling_strategy.bidding.IBiddingStrategy;

/** 
* @ClassName: TargetSystemStatusEventHandler 
* @Description: handlers that handles target system status event
* @author Chenhao Qu
* @date 05/06/2015 11:14:49 pm 
*  
*/
public class TargetSystemStatusEventHandler extends EventHandler {

	/** 
	* @Fields scalingPoliciesConfiguration : the scaling policies configuration
	*/ 
	protected IScalingPoliciesConfiguration scalingPoliciesConfiguration;

	public TargetSystemStatusEventHandler() {
		super(TargetSystemStatusEvent.class);
	}

	/** 
	* @Title: setScalingPoliciesConfiguration 
	* @Description: set the scaling policies configuration
	* @param scalingPoliciesConfiguration the new scaling policies configuration
	*/
	public synchronized void setScalingPoliciesConfiguration(
			IScalingPoliciesConfiguration scalingPoliciesConfiguration) {
		if (scalingPoliciesConfiguration == null) {
			throw new NullPointerException(
					"scaling policies configuration cannot be null");
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
		TargetSystemStatusEvent targetSystemStatusEvent = (TargetSystemStatusEvent) event;
		TargetSystemStatus targetSystemStatus = targetSystemStatusEvent
				.getTargetSystemStatus();

		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		ScalingPlan scalingPlan;
		
		//generate the scaling plan and switch mode
		synchronized (systemStatus) {
			if (targetSystemStatus.isSpotEnabled()
					&& !systemStatus.isSpotEnabled()) {
				systemStatus.enableSpot();
				scalingPlan = calculateScalingPlanForSpotMode(
						targetSystemStatus, systemStatus);
			} else if (!targetSystemStatus.isSpotEnabled()
					&& systemStatus.isSpotEnabled()) {
				systemStatus.disableSpot();
				scalingPlan = calculateScalingPlanForOnDemandMode(
						targetSystemStatus, systemStatus);
			} else if (targetSystemStatus.isSpotEnabled()
					&& systemStatus.isSpotEnabled()) {
				scalingPlan = calculateScalingPlanForSpotMode(
						targetSystemStatus, systemStatus);
			} else {
				scalingPlan = calculateScalingPlanForOnDemandMode(
						targetSystemStatus, systemStatus);
			}
		}

		//fire an scaling plan event for further processing
		EventGenerator eventGenerator = EventGenerator.getEventGenerator();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(EventDataName.SCALING_PLAN, scalingPlan);
		Event newEvent = eventGenerator.generateEvent(Events.SCALING_EVENT,
				data);
		Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
		eventQueue.add(newEvent);
		eventHandlerLog.info(logFormatter.getGenerateEventLogString(newEvent,
				scalingPlan.toString()));
	}

	/** 
	* @Title: calculateScalingPlanForOnDemandMode 
	* @Description: generate scaling plan for on demand mode
	* @param targetSystemStatus the target system status
	* @param systemStatus the system status
	* @return the scaling plan
	*/
	private ScalingPlan calculateScalingPlanForOnDemandMode(
			TargetSystemStatus targetSystemStatus, SystemStatus systemStatus) {
		StartOnDemandRequest onDemandRequest = getStartOnDemandRequest(
				targetSystemStatus, systemStatus, FaultTolerantLevel.ZERO);
		TerminateVMsRequest terminateVMsRequest = getTerminateOnDemandVMsRequest(targetSystemStatus);
		return new ScalingPlan(null, onDemandRequest, terminateVMsRequest);
	}

	/** 
	* @Title: calculateScalingPlanForSpotMode 
	* @Description: calculate the scaling plan for spot mode
	* @param targetSystemStatus the target system status
	* @param systemStatus the system status
	* @return the scaling plan
	*/
	private ScalingPlan calculateScalingPlanForSpotMode(
			TargetSystemStatus targetSystemStatus, SystemStatus systemStatus) {
		Collection<InstanceTemplate> targetChosenTypes = targetSystemStatus
				.getChosenTypes();
		if (targetChosenTypes != null) {
			for (InstanceTemplate instanceTemplate : targetChosenTypes) {
				if (!systemStatus.isChosen(instanceTemplate)) {
					systemStatus.addChosenSpotType(instanceTemplate);
				}
			}
		}

		FaultTolerantLevel faultTolerantLevel = systemStatus.getFaultTolerantLevel();
		
		Set<StartSpotRequest> startSpotRequests = getStartSpotRequests(
				targetSystemStatus, systemStatus, faultTolerantLevel);
		StartOnDemandRequest onDemandRequest = getStartOnDemandRequest(
				targetSystemStatus, systemStatus, faultTolerantLevel);
		TerminateVMsRequest terminateVMsRequest = getTerminateOnDemandVMsRequest(targetSystemStatus);
		return new ScalingPlan(startSpotRequests, onDemandRequest,
				terminateVMsRequest);
	}

	/** 
	* @Title: getTerminateOnDemandVMsRequest 
	* @Description: terminate on demand instance
	* @param targetSystemStatus the target system status
	* @return the terminate vms request
	*/
	private TerminateVMsRequest getTerminateOnDemandVMsRequest(
			TargetSystemStatus targetSystemStatus) {
		InstanceStatus terminateOnDemandInstance = targetSystemStatus
				.getTerminatingOnDemandInstance();
		if (terminateOnDemandInstance != null) {
			Collection<InstanceStatus> terminatingInstances = new ArrayList<InstanceStatus>();
			terminatingInstances.add(terminateOnDemandInstance);
			return new TerminateVMsRequest(terminatingInstances);
		}
		return null;
	}

	/** 
	* @Title: getStartOnDemandRequest 
	* @Description: get the start on demand request
	* @param targetSystemStatus the target system status
	* @param systemStatus the system status
	* @param ftLevel the fault tolerant level
	* @return the start on demand request
	*/
	private StartOnDemandRequest getStartOnDemandRequest(
			TargetSystemStatus targetSystemStatus, SystemStatus systemStatus, FaultTolerantLevel ftLevel) {

		//simply start the short number of on demand instances
		IScalingUtil scalingUtil = scalingPoliciesConfiguration
				.getScalingUtil();
		
		StartOnDemandRequest onDemandRequest = null;
		Collection<InstanceStatus> onDemandInstances = systemStatus.getNominalOnDemandInstances();
		long onDemandCapacity = scalingUtil
				.getEstimatedTotalCapacity(onDemandInstances, ftLevel);
		InstanceTemplate onDemandInstanceTemplate = targetSystemStatus
				.getOnDemandInstanceTemplate();
		int num = scalingUtil.getCeilRequiredNumOfInstances(onDemandCapacity,
				onDemandInstanceTemplate, ftLevel);
		int targetOnDemandNum = targetSystemStatus.getNumOfOnDemandInstances();
		if (num < targetOnDemandNum) {
			onDemandRequest = new StartOnDemandRequest(
					onDemandInstanceTemplate, targetOnDemandNum - num);
		}
		return onDemandRequest;
	}

	/** 
	* @Title: getStartSpotRequests 
	* @Description: get start spot requests for each chosen spot types
	* @param targetSystemStatus the target system status
	* @param systemStatus the system status
	* @param ftlevel the fault tolerant level
	* @return start spot requests for each chosen spot types
	*/
	private Set<StartSpotRequest> getStartSpotRequests(
			TargetSystemStatus targetSystemStatus, SystemStatus systemStatus, FaultTolerantLevel ftlevel) {
		Collection<InstanceTemplate> targetChosenTypes = targetSystemStatus
				.getChosenTypes();

		if (targetChosenTypes == null) {
			return null;
		}

		IScalingUtil scalingUtil;
		IBiddingStrategy biddingStrategy;
		synchronized (scalingPoliciesConfiguration) {
			scalingUtil = scalingPoliciesConfiguration.getScalingUtil();
			biddingStrategy = scalingPoliciesConfiguration.getBiddingStartegy();
		}

		Set<StartSpotRequest> startSpotRequests = null;
		//get start spot request for each chosen spot types
		for (InstanceTemplate instanceTemplate : targetChosenTypes) {
			SpotBiddingInfo spotBiddingInfo = targetSystemStatus
					.getSpotBiddingInfo(instanceTemplate);
			int targetNum = spotBiddingInfo.getNum();
			
			Collection<InstanceStatus> group = systemStatus.getNominalChosenSpotGroup(instanceTemplate);
			long capacity = scalingUtil.getEstimatedTotalCapacity(group, ftlevel);
			
			int numOfInstance = scalingUtil.getFloorRequiredNumOfInstances(capacity, instanceTemplate, ftlevel);
			//first move orphans to unsatisfied group
			while (systemStatus.hasOrphan() && numOfInstance < targetNum) {
				systemStatus.moveOrphanToType(instanceTemplate);
				group = systemStatus.getNominalChosenSpotGroup(instanceTemplate);
				capacity = scalingUtil.getEstimatedTotalCapacity(group, ftlevel);
				numOfInstance = scalingUtil.getCeilRequiredNumOfInstances(capacity,
						instanceTemplate, ftlevel);
			}
			
			//if still not satisfied, start the short number of instances
			if (numOfInstance < targetNum) {
				double price = biddingStrategy.getBidPrice(spotBiddingInfo);
				StartSpotRequest startSpotRequest = new StartSpotRequest(
						instanceTemplate, targetNum - numOfInstance, price);
				if (startSpotRequests == null) {
					startSpotRequests = new HashSet<StartSpotRequest>();
				}
				startSpotRequests.add(startSpotRequest);
			}

		}
		return startSpotRequests;
	}

}
