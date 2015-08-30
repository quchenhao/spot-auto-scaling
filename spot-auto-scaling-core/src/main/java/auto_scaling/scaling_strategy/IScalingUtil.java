package auto_scaling.scaling_strategy;

import java.util.Collection;
import java.util.Map;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.core.FaultTolerantLevel;

/** 
* @ClassName: IScalingUtil 
* @Description: the helper for the scaling policies
* @author Chenhao Qu
* @date 07/06/2015 12:33:13 pm 
*  
*/
public interface IScalingUtil {

	/**
	 * @Title: isSystemWideResoucesRequirementSatisfied 
	 * @Description: check whether the current provision can satisfy the current request rate
	 * @param totalNumOfRequest the current request rate
	 * @param onDemandInstances the on demand instances
	 * @param spotGroups the spot groups
	 * @param faultTolerantLevel the fault tolerant level
	 * @param isSpotEnable whether the spot mode is enabled
	 * @return whether the current provision can satisfy the current request rate
	 * @throws
	 */
	public boolean isSystemWideResoucesRequirementSatisfied(final long totalNumOfRequest, final Collection<InstanceStatus> onDemandInstances, final Map<InstanceTemplate, Collection<InstanceStatus>> spotGroups, final FaultTolerantLevel faultTolerantLevel, final boolean isSpotEnable);
	/**
	 * @Title: getEstimatedTotalCapacity 
	 * @Description: get the estimated total capacity for a list of instances
	 * @param group the instances
	 * @param ftlevel the fault tolerant level
	 * @return the estimated total capacity for a list of instances
	 * @throws
	 */
	public long getEstimatedTotalCapacity(Collection<InstanceStatus> group, FaultTolerantLevel ftlevel);
	/**
	 * @Title: getEstimatedTotalCapacity 
	 * @Description: get the estimated total capacity for the given type and the number of instances
	 * @param numOfInstances the number of instances
	 * @param instanceTemplate the instance type
	 * @param ftlevel the fault tolerant level
	 * @return the estimated total capacity for instances of certain type and number
	 * @throws
	 */
	public long getEstimatedTotalCapacity(int numOfInstances, InstanceTemplate instanceTemplate, FaultTolerantLevel ftlevel);
	/**
	 * @Title: getQuotaForEachSpotType 
	 * @Description: calculate the quota for each spot type
	 * @param totalNumOfRequests the current request rate
	 * @param onDemandInstances the on demand instances
	 * @param faultTolerantLevel the fault tolerant level 
	 * @param numOfChosenTypes the number of chosen types
	 * @return the quota for each spot types
	 * @throws
	 */
	public long getQuotaForEachSpotType(final long totalNumOfRequests, final Collection<InstanceStatus> onDemandInstances, final FaultTolerantLevel faultTolerantLevel, final int numOfChosenTypes);
	/**
	 * @Title: getQuotaForEachSpotType 
	 * @Description: calculate the quota for each spot type
	 * @param totalNumOfRequests the current request rate
	 * @param instanceTemplate the instance type
	 * @param numOfOnDemandInstances the number of on demand instances
	 * @param faultTolerantLevel the fault tolerant level
	 * @param numOfChosenTypes the number of chosen spot types
	 * @return the quota for each spot type
	 * @throws
	 */
	public long getQuotaForEachSpotType(final long totalNumOfRequests, final InstanceTemplate instanceTemplate, int numOfOnDemandInstances, final FaultTolerantLevel faultTolerantLevel, final int numOfChosenTypes);
	/**
	 * @Title: getCeilRequiredNumOfInstances 
	 * @Description: get the ceiling of the required number of instances
	 * @param numOfRequests the current request rate
	 * @param instanceTemplate the instance type
	 * @param ftlevel the fault tolerant level
	 * @return the ceiling of the required number of instances
	 * @throws
	 */
	public int getCeilRequiredNumOfInstances(final long numOfRequests, final InstanceTemplate instanceTemplate, FaultTolerantLevel ftlevel);
	/**
	 * @Title: getFloorRequiredNumOfInstances 
	 * @Description: get the floor of the required number of instances
	 * @param numOfRequests the current request rate
	 * @param instanceTemplate the instance type
	 * @param ftlevel the fault tolerant level
	 * @return the floor of the required number of instances
	 * @throws
	 */
	public int getFloorRequiredNumOfInstances(final long numOfRequests, final InstanceTemplate instanceTemplate, FaultTolerantLevel ftlevel);
	/**
	 * @Title: getUnsatisfiedRequestsNum 
	 * @Description: get the unsatisfied request rate
	 * @param numOfRequests the current request rate
	 * @param provisionedResources provisioned instances
	 * @param ftLevel the fault tolerant level
	 * @return the unsatisfied request rate
	 * @throws
	 */
	public long getUnsatisfiedRequestsNum(final long numOfRequests, Collection<InstanceStatus> provisionedResources, FaultTolerantLevel ftLevel);
	/**
	 * @Title: getUnsatisfiedRequestsNum 
	 * @Description: get the unsatisfied request rate
	 * @param numOfRequests the current request rate
	 * @param numOfInstances the number of instances
	 * @param instanceTemplate the instance type
	 * @param ftLevel the fault tolerant level
	 * @return the unsatisfied request rate
	 * @throws
	 */
	public long getUnsatisfiedRequestsNum(final long numOfRequests, int numOfInstances, InstanceTemplate instanceTemplate, FaultTolerantLevel ftLevel);
	
}
