package auto_scaling.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.SpotPricingStatus;
import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.core.FaultTolerantLevel;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.core.SpotPricingManager;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.event.SpotPriceUpdateEvent;
import auto_scaling.scaling_strategy.IScalingUtil;
import auto_scaling.scaling_strategy.SpotBiddingInfo;
import auto_scaling.scaling_strategy.bidding.ITruthfulBiddingPriceCalculator;

/** 
* @ClassName: SpotPriceUpdateEventHandler 
* @Description: handler that handles spot price update event
* @author Chenhao Qu
* @date 05/06/2015 11:04:51 pm 
*  
*/
public class SpotPriceUpdateEventHandler extends EventHandler {

	/** 
	* @Fields scalingPoliciesConfiguration : the scaling polices configuration
	*/ 
	protected IScalingPoliciesConfiguration scalingPoliciesConfiguration;
	/** 
	* @Fields interval : the optimization interval
	*/ 
	protected int interval;
	/** 
	* @Fields lastOptimized : last optimized time
	*/ 
	protected long lastOptimized;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public SpotPriceUpdateEventHandler() {
		super(SpotPriceUpdateEvent.class);
		this.lastOptimized = System.currentTimeMillis();
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
	
	/** 
	* @Title: setInterval 
	* @Description: set the optimization interval
	* @param interval the optimization interval
	*/
	public synchronized void setInterval(int interval) {
		if (interval < 0) {
			throw new IllegalArgumentException("interval cannot be negative");
		}
		this.interval = interval;
	}

	/** 
	* @Title: isOptimizationIntervalPassed 
	* @Description: check whether the optimized interval is passed
	* @return whether the optimized interval is passed
	*/
	protected boolean isOptimizationIntervalPassed() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastOptimized >= interval * 1000) {
			lastOptimized = currentTime;
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc) 
	* <p>Title: doHandling</p> 
	* <p>Description: </p> 
	* @param event 
	* @see auto_scaling.handler.EventHandler#doHandling(auto_scaling.event.Event) 
	*/
	@Override
	protected synchronized void doHandling(Event event) {
		//if the optimized interval is not passed, do nothing
		if (!isOptimizationIntervalPassed()) {
			return;
		}
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		if (!systemStatus.isSpotEnabled()) {
			return;
		}
		
		SpotPricingManager spotPricingManager = SpotPricingManager.getSpotPricingManager();
		Collection<SpotPricingStatus> allSpotPricingStatus = spotPricingManager.getAllSpotPricingStatuses();
		
		IScalingUtil scalingUtil = null;
		ITruthfulBiddingPriceCalculator truthfulBiddingPriceCalculator = null;
		
		synchronized (scalingPoliciesConfiguration) {
			scalingUtil = scalingPoliciesConfiguration.getScalingUtil();
			truthfulBiddingPriceCalculator = scalingPoliciesConfiguration.getTruthfulBiddingPriceCalculator();
		}
		
		synchronized (systemStatus) {
			long totalNumOfRequests = systemStatus.getTotalNumOfRequests();
			Collection<InstanceStatus> onDemandInstances = systemStatus.getNominalOnDemandInstances();
			FaultTolerantLevel faultTolerantLevel = systemStatus.getFaultTolerantLevel();
			int numberOfChosenSpotTypes = systemStatus.getNumOfChosenSpotTypes();
			long quota = scalingUtil.getQuotaForEachSpotType(totalNumOfRequests, onDemandInstances, faultTolerantLevel, numberOfChosenSpotTypes);
			InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
			InstanceTemplate onDemandTemplate = instanceTemplateManager.getOnDemandInstanceTemplate();
			
			int maxOnDemand = scalingUtil.getCeilRequiredNumOfInstances(totalNumOfRequests, onDemandTemplate, FaultTolerantLevel.ZERO);
			int minOnDemand = onDemandInstances.size();
			
			if (minOnDemand >= maxOnDemand) {
				return;
			}
			
			int numOfChosenTypes = systemStatus.getNumOfChosenSpotTypes();
			//calculate cost efficiency of all spot instance types
			Map<InstanceTemplate, SpotBiddingInfo> costEfficiencyInfoMap = new HashMap<InstanceTemplate, SpotBiddingInfo>();
			for (SpotPricingStatus spotPricingStatus : allSpotPricingStatus) {
				InstanceTemplate instanceTemplate = spotPricingStatus.getInstanceTemplate();
				int num = scalingUtil.getCeilRequiredNumOfInstances(quota, instanceTemplate, faultTolerantLevel);
				double truthfulBiddingPrice = truthfulBiddingPriceCalculator.getTruthfulBiddingPrice(onDemandTemplate, maxOnDemand - minOnDemand, instanceTemplate, numOfChosenTypes, num);
				SpotBiddingInfo costEfficiencyInfo = new SpotBiddingInfo(instanceTemplate, num, spotPricingStatus.getPrice(), truthfulBiddingPrice);
				costEfficiencyInfoMap.put(instanceTemplate, costEfficiencyInfo);
			}
			
			List<SpotBiddingInfo> unchosenSpotCostEfficiencyInfos = new ArrayList<SpotBiddingInfo>();
			unchosenSpotCostEfficiencyInfos.addAll(costEfficiencyInfoMap.values());
			
			Collection<InstanceTemplate> allChosenSpotTypes = systemStatus.getChosenSpotTypes();
			List<SpotBiddingInfo> chosenSpotCostEfficiencyInfos = new ArrayList<SpotBiddingInfo>();
			for (InstanceTemplate instanceTemplate : allChosenSpotTypes) {
				SpotBiddingInfo costEfficiencyInfo = costEfficiencyInfoMap.get(instanceTemplate);
				chosenSpotCostEfficiencyInfos.add(costEfficiencyInfo);
			}
			
			//sort cost efficiency of chosen spot types and unchosen spot types
			unchosenSpotCostEfficiencyInfos.removeAll(chosenSpotCostEfficiencyInfos);
			Collections.sort(unchosenSpotCostEfficiencyInfos);
			
			Collections.sort(chosenSpotCostEfficiencyInfos);
			Collections.reverse(chosenSpotCostEfficiencyInfos);
			
			int i = 0;
			boolean removed = false;
			//replace chosen spot types with spot types that are more cost efficient.
			//remove chosen types whose prices exceeds the truthful bidding price.
			while (i < chosenSpotCostEfficiencyInfos.size()) {
				SpotBiddingInfo unchosenSpotCostEfficiencyInfo = null;
				if (i < unchosenSpotCostEfficiencyInfos.size()) {
					unchosenSpotCostEfficiencyInfo = unchosenSpotCostEfficiencyInfos.get(i);
				}
				SpotBiddingInfo chosenSpotCostEfficiencyInfo = chosenSpotCostEfficiencyInfos.get(i);
				if (unchosenSpotCostEfficiencyInfo != null && unchosenSpotCostEfficiencyInfo.getTotalCostEachHour() + 0.01 < chosenSpotCostEfficiencyInfo.getTotalCostEachHour()
						&& unchosenSpotCostEfficiencyInfo.getTruthfulBiddingPrice() > unchosenSpotCostEfficiencyInfo.getMarketPrice()) {
					systemStatus.handOver(chosenSpotCostEfficiencyInfo.getInstanceTemplate(), unchosenSpotCostEfficiencyInfo.getInstanceTemplate());
				}
				else if (chosenSpotCostEfficiencyInfo.getTruthfulBiddingPrice() < chosenSpotCostEfficiencyInfo.getMarketPrice()) {
					systemStatus.removeChosenSpotType(chosenSpotCostEfficiencyInfo.getInstanceTemplate());
					removed = true;
				}
				i++;
			}
			
			//if some types are removed, fire an about to scale up event for future processing
			if (removed) {
				EventGenerator eventGenerator = EventGenerator.getEventGenerator();
				Map<String, Object> data = new HashMap<String, Object>();
				Event newEvent = eventGenerator.generateEvent(Events.ABOUT_TO_SCALE_UP_EVENT, data);
				Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
				eventQueue.add(newEvent);
				
				eventHandlerLog.info(logFormatter.getGenerateEventLogString(newEvent, null));
			}
		}
	}
}
