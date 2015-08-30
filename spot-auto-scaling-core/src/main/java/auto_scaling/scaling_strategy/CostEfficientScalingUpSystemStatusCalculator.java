package auto_scaling.scaling_strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.SpotPricingStatus;
import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.configuration.Limits;
import auto_scaling.core.FaultTolerantLevel;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.core.SpotPricingManager;
import auto_scaling.core.SystemStatus;
import auto_scaling.scaling_strategy.bidding.ITruthfulBiddingPriceCalculator;

/** 
* @ClassName: CostEfficientScalingUpSystemStatusCalculator 
* @Description: the cost efficient scaling up policies
* @author Chenhao Qu
* @date 07/06/2015 12:03:01 pm 
*  
*/
public class CostEfficientScalingUpSystemStatusCalculator implements IScalingUpSystemStatusCalculator {
	
	/** 
	* @Fields scalingPoliciesConfiguration : the scaling policies configuration
	*/ 
	protected IScalingPoliciesConfiguration scalingPoliciesConfiguration;
	/** 
	* @Fields limits : the limitations
	*/ 
	protected Limits limits;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public CostEfficientScalingUpSystemStatusCalculator() {}
	
	/* (non-Javadoc) 
	* <p>Title: setLimits</p> 
	* <p>Description: </p> 
	* @param limits 
	* @see auto_scaling.scaling_strategy.IScalingUpSystemStatusCalculator#setLimits(auto_scaling.configuration.Limits) 
	*/
	public synchronized void setLimits(Limits limits) {
		if (limits == null) {
			throw new NullPointerException("limits cannot be null");
		}
		this.limits = limits;
	}
	
	/* (non-Javadoc) 
	* <p>Title: setScalingPoliciesConfiguration</p> 
	* <p>Description: </p> 
	* @param scalingPoliciesConfiguration 
	* @see auto_scaling.scaling_strategy.IScalingUpSystemStatusCalculator#setScalingPoliciesConfiguration(auto_scaling.configuration.IScalingPoliciesConfiguration) 
	*/
	@Override
	public synchronized void setScalingPoliciesConfiguration(IScalingPoliciesConfiguration scalingPoliciesConfiguration) {
		if (scalingPoliciesConfiguration == null) {
			throw new NullPointerException("scaling policies configuration cannot be null");
		}
		this.scalingPoliciesConfiguration = scalingPoliciesConfiguration;
	}

	/* (non-Javadoc) 
	* <p>Title: calculateTargetSystemStatus</p> 
	* <p>Description: </p> 
	* @param systemStatus
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingUpSystemStatusCalculator#calculateTargetSystemStatus(auto_scaling.core.SystemStatus) 
	*/
	@Override
	public synchronized TargetSystemStatus calculateTargetSystemStatus(SystemStatus systemStatus) {
		
		IScalingUtil scalingUtil; 
		ITruthfulBiddingPriceCalculator truthfulBiddingPriceCalculator; 
		synchronized (scalingPoliciesConfiguration) {
			scalingUtil = scalingPoliciesConfiguration.getScalingUtil();
			truthfulBiddingPriceCalculator = scalingPoliciesConfiguration.getTruthfulBiddingPriceCalculator();
		}
		
		synchronized(systemStatus) {
			long totalNumOfRequests = systemStatus.getTotalNumOfRequests();
			Collection<InstanceStatus> onDemandInstances = systemStatus.getNominalOnDemandInstances();
			FaultTolerantLevel faultTolerantLevel = systemStatus.getFaultTolerantLevel();
			InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
			InstanceTemplate onDemandTemplate = instanceTemplateManager.getOnDemandInstanceTemplate();
			Collection<InstanceTemplate> chosenSpotTypes = systemStatus.getChosenSpotTypes();
			int numOfChosenTypes = systemStatus.getNumOfChosenSpotTypes();
			//max on demand is the number of on demand instances for the on demand mode
			int maxOnDemand = scalingUtil.getCeilRequiredNumOfInstances(totalNumOfRequests, onDemandTemplate, FaultTolerantLevel.ZERO);
			
			int onDemandNumThreshold = scalingUtil.getCeilRequiredNumOfInstances((long)(totalNumOfRequests * limits.getOnDemandCpapcityThreshold()), onDemandTemplate, FaultTolerantLevel.ZERO);
			//min on demand equals the maximum value of current on demand instances or the minimum on demand instances required
			int minOnDemand = onDemandInstances.size() > onDemandNumThreshold ? onDemandInstances.size() : onDemandNumThreshold;
			SpotPricingManager spotPricingManager = SpotPricingManager.getSpotPricingManager();
			double minTotalCost = Double.MAX_VALUE;
			TargetSystemStatus targetSystemStatus = null;
			
			synchronized(spotPricingManager) {
				Collection<SpotPricingStatus> spotPricingStatuses = spotPricingManager.getAllSpotPricingStatuses();
				int minNewSpotTypes = 0;
				
				//calculate minimum chosen spot types
				//if current num of chosen types can satisfy the fault tolerant level, keep it as the current chosen num
				//it is the minimum num that can satisfy the fault tolerant level
				if (numOfChosenTypes <= faultTolerantLevel.getLevel()) {
					minNewSpotTypes = faultTolerantLevel.getLevel() + 1 - numOfChosenTypes;
				}
				
				List<SpotPricingStatus> unChosenSpotPricingStatuses = new ArrayList<SpotPricingStatus>();
				
				for (SpotPricingStatus spotPricingStatus : spotPricingStatuses) {
					InstanceTemplate instanceTemplate = spotPricingStatus.getInstanceTemplate();
					if (!systemStatus.isChosen(instanceTemplate)) {
						unChosenSpotPricingStatuses.add(spotPricingStatus);
					}
				}
				
				int maxNewSpotTypes = unChosenSpotPricingStatuses.size() <= (limits.getMaxChosenSpotTypesNum() - numOfChosenTypes)
						? unChosenSpotPricingStatuses.size() : (limits.getMaxChosenSpotTypesNum() - numOfChosenTypes);
				
				//find the provision that is most cost efficient
				for (int i = minOnDemand; i < maxOnDemand; i++) {
					for (int j = minNewSpotTypes; j <= maxNewSpotTypes; j++) {
						double cost = onDemandTemplate.getOnDemandPrice() * i;
						long quota = scalingUtil.getQuotaForEachSpotType(totalNumOfRequests, onDemandTemplate, i, faultTolerantLevel, numOfChosenTypes + j);
						Map<InstanceTemplate, SpotBiddingInfo> spotGroups = new HashMap<InstanceTemplate, SpotBiddingInfo>();
						for (InstanceTemplate instanceTemplate : chosenSpotTypes) {
							SpotPricingStatus spotPricingStatus = spotPricingManager.getSpotPricingStatus(instanceTemplate);
							int num = scalingUtil.getCeilRequiredNumOfInstances(quota, instanceTemplate, faultTolerantLevel);
							double marketPrice = spotPricingStatus.getPrice();
							double truthfulBiddingPrice = truthfulBiddingPriceCalculator.getTruthfulBiddingPrice(onDemandTemplate, maxOnDemand - i, instanceTemplate, numOfChosenTypes + j, num);
							SpotBiddingInfo spotBiddingInfo = new SpotBiddingInfo(instanceTemplate, num, marketPrice, truthfulBiddingPrice);
							spotGroups.put(instanceTemplate, spotBiddingInfo);
							cost += spotBiddingInfo.getTotalCostEachHour();
						}
						
						if (j == 0) {
							if (cost < minTotalCost) {
								minTotalCost = cost;
								targetSystemStatus = new TargetSystemStatus(spotGroups, i, onDemandTemplate, null, faultTolerantLevel, true);
							}
							continue;
						}
						
						List<SpotBiddingInfo> costEfficiencyInfoList = new ArrayList<SpotBiddingInfo>(unChosenSpotPricingStatuses.size());
						for (SpotPricingStatus unchosenSpotPricingStatus : unChosenSpotPricingStatuses) {
							InstanceTemplate instanceTemplate = unchosenSpotPricingStatus.getInstanceTemplate();
							int num = scalingUtil.getCeilRequiredNumOfInstances(quota, instanceTemplate, faultTolerantLevel);
							double marketPrice = unchosenSpotPricingStatus.getPrice();
							double truthfulBiddingPrice = truthfulBiddingPriceCalculator.getTruthfulBiddingPrice(onDemandTemplate, maxOnDemand - i, instanceTemplate, numOfChosenTypes + j, num);
							if (truthfulBiddingPrice > marketPrice) {
								SpotBiddingInfo templateCostEfficiencyInfo = new SpotBiddingInfo(instanceTemplate, num, marketPrice, truthfulBiddingPrice);
								costEfficiencyInfoList.add(templateCostEfficiencyInfo);
							}
						}
						
						if (costEfficiencyInfoList.size() < j) {
							continue;
						}
						
						Collections.sort(costEfficiencyInfoList);
						for (int k = 0 ; k < j ; k++) {
							SpotBiddingInfo templateCostEfficiencyInfo = costEfficiencyInfoList.get(k);
							InstanceTemplate instanceTemplate = templateCostEfficiencyInfo.getInstanceTemplate();
							spotGroups.put(instanceTemplate, templateCostEfficiencyInfo);
							cost += templateCostEfficiencyInfo.getTotalCostEachHour();
						}
						
						
						if (cost < minTotalCost) {
							minTotalCost = cost;
							targetSystemStatus = new TargetSystemStatus(spotGroups, i, onDemandTemplate, null, faultTolerantLevel, true);
						}
					}
				}
				
				double onDemandCost = onDemandTemplate.getOnDemandPrice() * maxOnDemand;
				
				//if no cost efficient provision found, switch to on demand mode
				if (minTotalCost > onDemandCost) {
					targetSystemStatus = new TargetSystemStatus(null, maxOnDemand, onDemandTemplate, null, faultTolerantLevel, false);
				}
			}
			
			return targetSystemStatus;
		}
	}
}
