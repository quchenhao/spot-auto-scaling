package auto_scaling.scaling_strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.cloud.SpotInstanceStatus;
import auto_scaling.cloud.SpotPricingStatus;
import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.configuration.Limits;
import auto_scaling.core.FaultTolerantLevel;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.core.SpotPricingManager;
import auto_scaling.core.SystemStatus;
import auto_scaling.scaling_strategy.bidding.IBiddingStrategy;
import auto_scaling.scaling_strategy.bidding.ITruthfulBiddingPriceCalculator;

/** 
* @ClassName: CostEfficientScalingDownSystemStatusCalculator 
* @Description: the cost efficient scaling down policies
* @author Chenhao Qu
* @date 06/06/2015 3:09:21 pm 
*  
*/
public class CostEfficientScalingDownSystemStatusCalculator implements IScalingDownSystemStatusCalculator{

	/** 
	* @Fields scalingPoliciesConfiguration : the scaling policies
	*/ 
	protected IScalingPoliciesConfiguration scalingPoliciesConfiguration;
	
	/** 
	* <p>Description: </p>  
	*/
	public CostEfficientScalingDownSystemStatusCalculator() {}
	
	/* (non-Javadoc) 
	* <p>Title: setScalingPoliciesConfiguration</p> 
	* <p>Description: </p> 
	* @param scalingPoliciesConfiguration 
	* @see auto_scaling.scaling_strategy.IScalingDownSystemStatusCalculator#setScalingPoliciesConfiguration(auto_scaling.configuration.IScalingPoliciesConfiguration) 
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
	* @param onDemandInstance
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingDownSystemStatusCalculator#calculateTargetSystemStatus(auto_scaling.core.SystemStatus, auto_scaling.cloud.OnDemandInstanceStatus) 
	*/
	@Override
	public synchronized TargetSystemStatus calculateTargetSystemStatus(
			SystemStatus systemStatus, OnDemandInstanceStatus onDemandInstance) {
		Limits limits = Limits.getLimits();
		IScalingUtil scalingUtil; 
		ITruthfulBiddingPriceCalculator truthfulBiddingPriceCalculator; 
		synchronized (scalingPoliciesConfiguration) {
			scalingUtil = scalingPoliciesConfiguration.getScalingUtil();
			truthfulBiddingPriceCalculator = scalingPoliciesConfiguration.getTruthfulBiddingPriceCalculator();
		}
		
		synchronized (systemStatus) {
			int numOfAttachedInstances = systemStatus.getNumOfAttachedInstances();
			if (numOfAttachedInstances == 1) {
				return null;
			}
			
			Collection<InstanceStatus> onDemandInstances = systemStatus.getNominalOnDemandInstances();
			int currentOnDemandNum = onDemandInstances.size();
			
			//check if the resource can be satisfied by the maximum capacity without the current instance
			if (systemStatus.isSpotEnabled() && (systemStatus.getMaximumAvaliableCapacity() - onDemandInstance.getType().getMaximunCapacity() < systemStatus.getTotalNumOfRequests())) {
				return null;
			}
			
			long totalNumOfRequests = systemStatus.getTotalNumOfRequests();
			InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
			InstanceTemplate onDemandTemplate = instanceTemplateManager.getOnDemandInstanceTemplate();
			
			Collection<InstanceStatus> temp = new ArrayList<InstanceStatus>(onDemandInstances);
			temp.remove(onDemandInstance);
			Map<InstanceTemplate, Collection<InstanceStatus>> spotInstanceGroups = systemStatus.getNominalSpotGroups();
			FaultTolerantLevel faultTolerantLevel = systemStatus.getFaultTolerantLevel();
			boolean isSpotEnable = systemStatus.isSpotEnabled();
			
			int minimumOnDemand = scalingUtil.getCeilRequiredNumOfInstances((long)(totalNumOfRequests * limits.getOnDemandCpapcityThreshold()), onDemandTemplate, FaultTolerantLevel.ZERO);
			
			if (minimumOnDemand >= currentOnDemandNum) {
				return null;
			}
			
			//check if the resource can be satisfied by the basic capacity without the current instance
			if (scalingUtil.isSystemWideResoucesRequirementSatisfied(totalNumOfRequests, temp, spotInstanceGroups, faultTolerantLevel, isSpotEnable)) {
				return new TargetSystemStatus(null, currentOnDemandNum - 1, onDemandTemplate, onDemandInstance, faultTolerantLevel, isSpotEnable);
			}
			
			//find the best provision with the original number of on demand instances
			int maxOnDemandNum = scalingUtil.getCeilRequiredNumOfInstances(totalNumOfRequests, onDemandTemplate, FaultTolerantLevel.ZERO);
			
			double onDemandCost = onDemandTemplate.getOnDemandPrice() * maxOnDemandNum;
			
			int numOfChosenTypes = systemStatus.getNumOfChosenSpotTypes();
			Collection<InstanceTemplate> chosenSpotTypes = systemStatus.getChosenSpotTypes();
			
			SpotPricingManager spotPricingManager = SpotPricingManager.getSpotPricingManager();
			
			synchronized(spotPricingManager) {
				Collection<SpotPricingStatus> spotPricingStatuses = spotPricingManager.getAllSpotPricingStatuses();
				int minNewSpotTypes = 0;
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
				
				
				double baseCost = Double.MAX_VALUE;
				
				if (currentOnDemandNum == maxOnDemandNum) {
					baseCost = onDemandCost;
				}
				else {
					
					for (int j = minNewSpotTypes; j <= maxNewSpotTypes; j++) {
						double cost = onDemandTemplate.getOnDemandPrice() * currentOnDemandNum;
						long quota = scalingUtil.getQuotaForEachSpotType(totalNumOfRequests, onDemandTemplate, currentOnDemandNum, faultTolerantLevel, numOfChosenTypes + j);
						Map<InstanceTemplate, SpotBiddingInfo> spotGroups = new HashMap<InstanceTemplate, SpotBiddingInfo>();
						
						for (InstanceTemplate instanceTemplate : chosenSpotTypes) {
							SpotPricingStatus spotPricingStatus = spotPricingManager.getSpotPricingStatus(instanceTemplate);
							int num = scalingUtil.getCeilRequiredNumOfInstances(quota, instanceTemplate, faultTolerantLevel);
							double marketPrice = spotPricingStatus.getPrice();
							double truthfulBiddingPrice = truthfulBiddingPriceCalculator.getTruthfulBiddingPrice(onDemandTemplate, maxOnDemandNum - currentOnDemandNum, instanceTemplate, numOfChosenTypes + j, num);
							SpotBiddingInfo spotBiddingInfo = new SpotBiddingInfo(instanceTemplate, num, marketPrice, truthfulBiddingPrice);
							spotGroups.put(instanceTemplate, spotBiddingInfo);
							cost += spotBiddingInfo.getTotalCostEachHour();
						}
						
						if (j == 0) {
							if (cost < baseCost) {
								baseCost = cost;
							}
							continue;
						}
						
						List<SpotBiddingInfo> costEfficiencyInfoList = new ArrayList<SpotBiddingInfo>(unChosenSpotPricingStatuses.size());
						for (SpotPricingStatus unchosenSpotPricingStatus : unChosenSpotPricingStatuses) {
							InstanceTemplate instanceTemplate = unchosenSpotPricingStatus.getInstanceTemplate();
							int num = scalingUtil.getCeilRequiredNumOfInstances(quota, instanceTemplate, faultTolerantLevel);
							double marketPrice = unchosenSpotPricingStatus.getPrice();
							double truthfulBiddingPrice = truthfulBiddingPriceCalculator.getTruthfulBiddingPrice(onDemandTemplate, maxOnDemandNum - currentOnDemandNum, instanceTemplate, numOfChosenTypes + j, num);
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
						
						
						if (cost < baseCost) {
							baseCost = cost;
						}
					}
					
					if (baseCost > onDemandCost) {
						baseCost = onDemandCost;
					}
				}
				
				//find the best provision with the decreased number of on demand instances 
				double newCost = Double.MAX_VALUE;
				TargetSystemStatus newTargetSystemStatus = null;
				for (int j = minNewSpotTypes; j <= maxNewSpotTypes; j++) {
					double cost = onDemandTemplate.getOnDemandPrice() * (currentOnDemandNum - 1);
					long quota = scalingUtil.getQuotaForEachSpotType(totalNumOfRequests, onDemandTemplate, currentOnDemandNum - 1, faultTolerantLevel, numOfChosenTypes + j);
					Map<InstanceTemplate, SpotBiddingInfo> spotGroups = new HashMap<InstanceTemplate, SpotBiddingInfo>();
					
					for (InstanceTemplate instanceTemplate : chosenSpotTypes) {
						SpotPricingStatus spotPricingStatus = spotPricingManager.getSpotPricingStatus(instanceTemplate);
						int num = scalingUtil.getCeilRequiredNumOfInstances(quota, instanceTemplate, faultTolerantLevel);
						double marketPrice = spotPricingStatus.getPrice();
						double truthfulBiddingPrice = truthfulBiddingPriceCalculator.getTruthfulBiddingPrice(onDemandTemplate, maxOnDemandNum - currentOnDemandNum + 1, instanceTemplate, numOfChosenTypes + j, num);
						SpotBiddingInfo spotBiddingInfo = new SpotBiddingInfo(instanceTemplate, num, marketPrice, truthfulBiddingPrice);
						spotGroups.put(instanceTemplate, spotBiddingInfo);
						cost += spotBiddingInfo.getTotalCostEachHour();
					}
					
					if (j == 0) {
						if (cost < newCost) {
							newCost = cost;
							newTargetSystemStatus = new TargetSystemStatus(spotGroups, currentOnDemandNum - 1, onDemandTemplate, onDemandInstance, faultTolerantLevel, true);
						}
						continue;
					}
					
					List<SpotBiddingInfo> costEfficiencyInfoList = new ArrayList<SpotBiddingInfo>(unChosenSpotPricingStatuses.size());
					for (SpotPricingStatus unchosenSpotPricingStatus : unChosenSpotPricingStatuses) {
						InstanceTemplate instanceTemplate = unchosenSpotPricingStatus.getInstanceTemplate();
						int num = scalingUtil.getCeilRequiredNumOfInstances(quota, instanceTemplate, faultTolerantLevel);
						double marketPrice = unchosenSpotPricingStatus.getPrice();
						double truthfulBiddingPrice = truthfulBiddingPriceCalculator.getTruthfulBiddingPrice(onDemandTemplate, maxOnDemandNum - currentOnDemandNum + 1, instanceTemplate, numOfChosenTypes + j, num);
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
					
					
					if (cost < newCost) {
						newCost = cost;
						newTargetSystemStatus = new TargetSystemStatus(spotGroups, currentOnDemandNum - 1, onDemandTemplate, onDemandInstance, faultTolerantLevel, true);
					}
				}
				
				//if provision with decreased number of on demand instances is more cost efficient, then return that provision
				if (newCost < baseCost) {
					if (!systemStatus.isSpotEnabled()) {
						return newTargetSystemStatus.convertToSwitchModeTargetSystemStatus();
					}
					return newTargetSystemStatus;
				}
				//else no change
				else {
					return null;
				}
			}	
		}
	}

	/* (non-Javadoc) 
	* <p>Title: calculateSpotScalingDownPlan</p> 
	* <p>Description: </p> 
	* @param systemStatus
	* @param spotInstance
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingDownSystemStatusCalculator#calculateSpotScalingDownPlan(auto_scaling.core.SystemStatus, auto_scaling.cloud.SpotInstanceStatus) 
	*/
	@Override
	public synchronized ScalingPlan calculateSpotScalingDownPlan(SystemStatus systemStatus,
			SpotInstanceStatus spotInstance) {
		InstanceTemplate instanceTemplate = spotInstance.getType();
		
		IScalingUtil scalingUtil; 
		ITruthfulBiddingPriceCalculator truthfulBiddingPriceCalculator;
		IBiddingStrategy biddingStrategy;
		synchronized (scalingPoliciesConfiguration) {
			scalingUtil = scalingPoliciesConfiguration.getScalingUtil();
			truthfulBiddingPriceCalculator = scalingPoliciesConfiguration.getTruthfulBiddingPriceCalculator();
			biddingStrategy = scalingPoliciesConfiguration.getBiddingStartegy();
		}
		
		synchronized (systemStatus) {
			long totalNumOfRequests = systemStatus.getTotalNumOfRequests();
			
			if (systemStatus.getMaximumAvaliableCapacity() - spotInstance.getType().getMaximunCapacity() < systemStatus.getTotalNumOfRequests()) {
				return null;
			}
			//if the instance is an orphan, just shut down
			if (systemStatus.isOrphan(spotInstance)) {
				TerminateVMsRequest terminateVMsRequest = getTerminateVMsRequest(spotInstance);
				return new ScalingPlan(null, null, terminateVMsRequest);
			}
			
			Collection<InstanceStatus> onDemandInstances = systemStatus.getNominalOnDemandInstances();
			FaultTolerantLevel faultTolerantLevel = systemStatus.getFaultTolerantLevel();
			int numOfChosenTypes = systemStatus.getNumOfChosenSpotTypes();
			long quota = scalingUtil.getQuotaForEachSpotType(totalNumOfRequests, onDemandInstances, faultTolerantLevel, numOfChosenTypes);
			//if the instance type is chosen, check if the quote can be satisfied without the instance
			if (systemStatus.isChosen(instanceTemplate)) {
				Collection<InstanceStatus> group = systemStatus.getNominalChosenSpotGroup(instanceTemplate);
				Collection<InstanceStatus> temp = new ArrayList<InstanceStatus>(group);
				temp.remove(spotInstance);
				long groupCapacityWithoutSpotInstance = scalingUtil.getEstimatedTotalCapacity(temp, faultTolerantLevel);
				if (quota <= groupCapacityWithoutSpotInstance) {
					TerminateVMsRequest terminateVMsRequest = getTerminateVMsRequest(spotInstance);
					return new ScalingPlan(null, null, terminateVMsRequest);
				}else {
					return null;
				}
			}
			else {
				//the instance is in spot group of different instance type
				InstanceTemplate groupTemplate = systemStatus.inGroup(spotInstance);
				if (groupTemplate != null) {
					Collection<InstanceStatus> group = systemStatus.getNominalChosenSpotGroup(groupTemplate);
					Collection<InstanceStatus> temp = new ArrayList<InstanceStatus>(group);
					temp.remove(spotInstance);
					long groupCapacityWithoutSpotInstance = scalingUtil.getEstimatedTotalCapacity(temp, faultTolerantLevel);
					//if the quote can be satisfied without the instance, just shut down
					if (quota <= groupCapacityWithoutSpotInstance) {
						TerminateVMsRequest terminateVMsRequest = getTerminateVMsRequest(spotInstance);
						return new ScalingPlan(null, null, terminateVMsRequest);
					}
					else {
						//shut down the instance, and start enough number of instances of the spot group type
						long deficiency = scalingUtil.getUnsatisfiedRequestsNum(quota, temp, faultTolerantLevel);
						int num = scalingUtil.getCeilRequiredNumOfInstances(deficiency, groupTemplate, faultTolerantLevel);
						InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
						InstanceTemplate onDemandTemplate = instanceTemplateManager.getOnDemandInstanceTemplate();
						long onDemandDeficiency = scalingUtil.getUnsatisfiedRequestsNum(totalNumOfRequests, onDemandInstances, faultTolerantLevel);
						int unprovisionedOnDemand = scalingUtil.getCeilRequiredNumOfInstances(onDemandDeficiency, onDemandTemplate, faultTolerantLevel);
						int numOfTargetInstances = scalingUtil.getCeilRequiredNumOfInstances(quota, groupTemplate, faultTolerantLevel);
						double truthfulBiddingPrice = truthfulBiddingPriceCalculator.getTruthfulBiddingPrice(onDemandTemplate, unprovisionedOnDemand, groupTemplate, numOfChosenTypes, numOfTargetInstances);
						
						Collection<StartSpotRequest> startSpotRequests = new ArrayList<StartSpotRequest>();
						
						SpotPricingManager spotPricingManager = SpotPricingManager.getSpotPricingManager();
						
						double marketPrice;
						synchronized (spotPricingManager) {
							marketPrice = spotPricingManager.getSpotPricingStatus(groupTemplate).getPrice();
						}
						
						double biddingPrice = biddingStrategy.getBidPrice(new SpotBiddingInfo(groupTemplate, num, marketPrice, truthfulBiddingPrice));
						StartSpotRequest startSpotRequest = new StartSpotRequest(groupTemplate, num, biddingPrice);
						startSpotRequests.add(startSpotRequest);
						
						TerminateVMsRequest terminateVMsRequest = getTerminateVMsRequest(spotInstance);
						return new ScalingPlan(startSpotRequests, null, terminateVMsRequest);
					}
				}
				else {
					TerminateVMsRequest terminateVMsRequest = getTerminateVMsRequest(spotInstance);
					return new ScalingPlan(null, null, terminateVMsRequest);
				}
			}
		}
		
	}

	/**
	 * @Title: getTerminateVMsRequest
	 * @Description: get the terminate instance vm request
	 * @param spotInstance the spot instance
	 * @return the terminate instance vm request
	 * @throws
	 */
	private TerminateVMsRequest getTerminateVMsRequest(SpotInstanceStatus spotInstance) {
		Collection<InstanceStatus> terminatingInstances = new ArrayList<InstanceStatus>();
		terminatingInstances.add(spotInstance);
		TerminateVMsRequest terminateVMsRequest = new TerminateVMsRequest(terminatingInstances);
		
		return terminateVMsRequest;
	}

	

}
