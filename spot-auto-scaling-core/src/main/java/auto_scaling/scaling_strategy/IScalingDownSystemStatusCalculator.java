package auto_scaling.scaling_strategy;

import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.cloud.SpotInstanceStatus;
import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.configuration.Limits;
import auto_scaling.core.SystemStatus;

/** 
* @ClassName: IScalingDownSystemStatusCalculator 
* @Description: the calculator for the scaling down policies
* @author Chenhao Qu
* @date 07/06/2015 12:22:47 pm 
*  
*/
public interface IScalingDownSystemStatusCalculator {
	
	/**
	 * @Title: calculateTargetSystemStatus 
	 * @Description: calculate the provision when an on demand instance is at the end of its billing period
	 * @param systemStatus the system status
	 * @param onDemandInstance the on demand instance at the end of its billing period
	 * @return the calculated target provision
	 * @throws
	 */
	public TargetSystemStatus calculateTargetSystemStatus(final SystemStatus systemStatus, final OnDemandInstanceStatus onDemandInstance);
	/**
	 * @Title: calculateSpotScalingDownPlan 
	 * @Description: calculate the scaling plan directly for spot instance at the end of its billing period
	 * @param systemStatus the system status
	 * @param spotInstance the spot instance at the end of its billing period
	 * @return the scaling plan
	 * @throws
	 */
	public ScalingPlan calculateSpotScalingDownPlan(final SystemStatus systemStatus, final SpotInstanceStatus spotInstance);
	/**
	 * @Title: setScalingPoliciesConfiguration 
	 * @Description: set the scaling policies configuration
	 * @param scalingPoliciesConfiguration the new scaling policies configuration
	 * @throws
	 */
	public void setScalingPoliciesConfiguration(IScalingPoliciesConfiguration scalingPoliciesConfiguration);
	/**
	 * @Title: setLimits 
	 * @Description: set the instance limitations
	 * @param limits the new instance limitations
	 * @throws
	 */
	public void setLimits(Limits limits);
}
