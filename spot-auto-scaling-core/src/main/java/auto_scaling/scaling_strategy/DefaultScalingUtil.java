package auto_scaling.scaling_strategy;

import java.util.Collection;
import java.util.Map;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.core.FaultTolerantLevel;

/** 
* @ClassName: DefaultScalingUtil 
* @Description: the default implementation for the scaling util
* @author Chenhao Qu
* @date 07/06/2015 12:13:00 pm 
*  
*/
public class DefaultScalingUtil implements IScalingUtil {

	/** 
	* <p>Description: empty initialization</p>  
	*/
	public DefaultScalingUtil() {
	}

	/* (non-Javadoc) 
	* <p>Title: isSystemWideResoucesRequirementSatisfied</p> 
	* <p>Description: calculate the resource satisfaction according to satisfaction of spot quotas</p> 
	* @param totalNumOfRequests the current request rate
	* @param onDemandInstances the on demand instances
	* @param spotGroups the spot groups
	* @param faultTolerantLevel the fault tolerant level
	* @param isSpotEnabled whether spot mode is enabled
	* @return whether the current provision can satisfy the current request rate
	* @see auto_scaling.scaling_strategy.IScalingUtil#isSystemWideResoucesRequirementSatisfied(long, java.util.Collection, java.util.Map, auto_scaling.core.FaultTolerantLevel, boolean) 
	*/
	@Override
	public boolean isSystemWideResoucesRequirementSatisfied(final long totalNumOfRequests,
			final Collection<InstanceStatus> onDemandInstances,
			final Map<InstanceTemplate, Collection<InstanceStatus>> spotGroups,
			final FaultTolerantLevel faultTolerantLevel,
			final boolean isSpotEnabled) {
		
		if (isSpotEnabled) {
			int numOfChosenTypes = spotGroups.size();
			
			//if chosen type is zero, switch to on demand
			if (numOfChosenTypes == 0) {
				long onDemandCapacity = getEstimatedTotalCapacity(onDemandInstances, FaultTolerantLevel.ZERO);
				return onDemandCapacity > totalNumOfRequests;
			}
			
			if (faultTolerantLevel.getLevel() >= numOfChosenTypes) {
				return false;
			}
			
			long spotQuota = getQuotaForEachSpotType(totalNumOfRequests, onDemandInstances, faultTolerantLevel, numOfChosenTypes);
			Collection<InstanceTemplate> chosenGroups = spotGroups.keySet();
			//check whether all spot quotas are satisfied
			for (InstanceTemplate instanceTemplate : chosenGroups) {
				Collection<InstanceStatus> group = spotGroups
						.get(instanceTemplate);
				long spotCapacity = getEstimatedTotalCapacity(group, faultTolerantLevel);
				if (spotCapacity < spotQuota) {
					return false;
				}
			}
			
			return true;
		} else {
			//simply check whether the current request rate can be satisfied in on demand mode
			long onDemandCapacity = getEstimatedTotalCapacity(onDemandInstances, FaultTolerantLevel.ZERO);
			return onDemandCapacity > totalNumOfRequests;
		}

	}

	/* (non-Javadoc) 
	* <p>Title: getCeilRequiredNumOfInstances</p> 
	* <p>Description: default implementation based on instance capacity</p> 
	* @param numOfRequests
	* @param instanceTemplate
	* @param ftLevel
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingUtil#getCeilRequiredNumOfInstances(long, auto_scaling.cloud.InstanceTemplate, auto_scaling.core.FaultTolerantLevel) 
	*/
	@Override
	public int getCeilRequiredNumOfInstances(long numOfRequests,
			InstanceTemplate instanceTemplate, FaultTolerantLevel ftLevel) {
		
		long capacity = instanceTemplate.getCapacity(ftLevel);

		return (int)Math.ceil(numOfRequests/(capacity + 0.0));
	}
	
	/* (non-Javadoc) 
	* <p>Title: getUnsatisfiedRequestsNum</p> 
	* <p>Description: default implementation based on instance capacity</p> 
	* @param numOfRequests
	* @param provisionedResources
	* @param ftLevel
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingUtil#getUnsatisfiedRequestsNum(long, java.util.Collection, auto_scaling.core.FaultTolerantLevel) 
	*/
	@Override
	public long getUnsatisfiedRequestsNum(long numOfRequests,
			Collection<InstanceStatus> provisionedResources, FaultTolerantLevel ftLevel) {
		long capacity = getEstimatedTotalCapacity(provisionedResources, ftLevel);
		if (numOfRequests > capacity) {
			return numOfRequests - capacity;
		}
		return 0;
	}

	/* (non-Javadoc) 
	* <p>Title: getUnsatisfiedRequestsNum</p> 
	* <p>Description: default implementation based on instance capacity</p> 
	* @param numOfRequests
	* @param numOfInstances
	* @param instanceTemplate
	* @param ftLevel
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingUtil#getUnsatisfiedRequestsNum(long, int, auto_scaling.cloud.InstanceTemplate, auto_scaling.core.FaultTolerantLevel) 
	*/
	@Override
	public long getUnsatisfiedRequestsNum(long numOfRequests,
			int numOfInstances, InstanceTemplate instanceTemplate, FaultTolerantLevel ftLevel) {
		long capacity = getEstimatedTotalCapacity(numOfInstances, instanceTemplate, ftLevel);
		if (numOfRequests > capacity) {
			return numOfRequests - capacity;
		}
		return 0;
	}


	/* (non-Javadoc) 
	* <p>Title: getEstimatedTotalCapacity</p> 
	* <p>Description: default implementation based on instance capacity</p> 
	* @param numOfInstances
	* @param instanceTemplate
	* @param ftLevel
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingUtil#getEstimatedTotalCapacity(int, auto_scaling.cloud.InstanceTemplate, auto_scaling.core.FaultTolerantLevel) 
	*/
	@Override
	public long getEstimatedTotalCapacity(int numOfInstances,
			InstanceTemplate instanceTemplate, FaultTolerantLevel ftLevel) {
		long capacity = instanceTemplate.getCapacity(ftLevel);
		return capacity * numOfInstances;
	}

	/* (non-Javadoc) 
	* <p>Title: getEstimatedTotalCapacity</p> 
	* <p>Description: default implementation based on instance capacity</p> 
	* @param group
	* @param ftLevel
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingUtil#getEstimatedTotalCapacity(java.util.Collection, auto_scaling.core.FaultTolerantLevel) 
	*/
	@Override
	public long getEstimatedTotalCapacity(Collection<InstanceStatus> group, FaultTolerantLevel ftLevel) {
		long totalCapacity = 0;
		for (InstanceStatus instanceStatus : group) {
			InstanceTemplate instanceTemplate = instanceStatus.getType();
			long capacity = instanceTemplate.getCapacity(ftLevel);
			totalCapacity += capacity;
		}
		return totalCapacity;
	}

	/* (non-Javadoc) 
	* <p>Title: getQuotaForEachSpotType</p> 
	* <p>Description: default implementation based on instance capacity</p> 
	* @param totalNumOfRequests
	* @param onDemandInstances
	* @param faultTolerantLevel
	* @param numOfChosenTypes
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingUtil#getQuotaForEachSpotType(long, java.util.Collection, auto_scaling.core.FaultTolerantLevel, int) 
	*/
	@Override
	public long getQuotaForEachSpotType(long totalNumOfRequests,
			Collection<InstanceStatus> onDemandInstances,
			FaultTolerantLevel faultTolerantLevel, int numOfChosenTypes) {
		if (numOfChosenTypes < 0) {
			throw new IllegalArgumentException(
					"number of chosen spot types cannot be negative");
		}

		if (faultTolerantLevel == null || faultTolerantLevel == null) {
			throw new NullPointerException("parameters cannot be null");
		}

		int ftLevel = faultTolerantLevel.getLevel();

		if (ftLevel >= numOfChosenTypes
				&& !(ftLevel == 0 && numOfChosenTypes == 0)) {
			throw new IllegalArgumentException(
					"fault tolerant level should not be larger than the number of chosen Types");
		}

		if (ftLevel == 0 && numOfChosenTypes == 0) {
			return 0;
		}
		
		long difficiency = getUnsatisfiedRequestsNum(totalNumOfRequests, onDemandInstances, FaultTolerantLevel.ZERO);
		
		return difficiency / (numOfChosenTypes - faultTolerantLevel.getLevel());
	}

	/* (non-Javadoc) 
	* <p>Title: getQuotaForEachSpotType</p> 
	* <p>Description: default implementation based on instance capacity</p> 
	* @param totalNumOfRequests
	* @param onDemandInstanceTemplate
	* @param numOfOnDemandInstances
	* @param faultTolerantLevel
	* @param numOfChosenTypes
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingUtil#getQuotaForEachSpotType(long, auto_scaling.cloud.InstanceTemplate, int, auto_scaling.core.FaultTolerantLevel, int) 
	*/
	@Override
	public long getQuotaForEachSpotType(long totalNumOfRequests,
			InstanceTemplate onDemandInstanceTemplate, int numOfOnDemandInstances,
			FaultTolerantLevel faultTolerantLevel, int numOfChosenTypes) {
		if (numOfChosenTypes < 0) {
			throw new IllegalArgumentException(
					"number of chosen spot types cannot be negative");
		}

		if (faultTolerantLevel == null || faultTolerantLevel == null) {
			throw new NullPointerException("parameters cannot be null");
		}

		int ftLevel = faultTolerantLevel.getLevel();

		if (ftLevel >= numOfChosenTypes
				&& !(ftLevel == 0 && numOfChosenTypes == 0)) {
			throw new IllegalArgumentException(
					"fault tolerant level should not be larger than the number of chosen Types");
		}

		if (ftLevel == 0 && numOfChosenTypes == 0) {
			return 0;
		}

		long difficiency = getUnsatisfiedRequestsNum(totalNumOfRequests, numOfOnDemandInstances, onDemandInstanceTemplate, FaultTolerantLevel.ZERO);

		return difficiency / (numOfChosenTypes - faultTolerantLevel.getLevel());
	}

	/* (non-Javadoc) 
	* <p>Title: getFloorRequiredNumOfInstances</p> 
	* <p>Description: default implementation based on instance capacity</p> 
	* @param numOfRequests
	* @param instanceTemplate
	* @param ftLevel
	* @return 
	* @see auto_scaling.scaling_strategy.IScalingUtil#getFloorRequiredNumOfInstances(long, auto_scaling.cloud.InstanceTemplate, auto_scaling.core.FaultTolerantLevel) 
	*/
	@Override
	public int getFloorRequiredNumOfInstances(long numOfRequests,
			InstanceTemplate instanceTemplate, FaultTolerantLevel ftLevel) {
		long capacity = instanceTemplate.getCapacity(ftLevel);

		return (int)Math.floor(numOfRequests/(capacity + 0.0));
	}
}
