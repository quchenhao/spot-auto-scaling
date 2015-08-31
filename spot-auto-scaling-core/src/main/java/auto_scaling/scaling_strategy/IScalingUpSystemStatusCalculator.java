package auto_scaling.scaling_strategy;

import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.core.SystemStatus;

/** 
* @ClassName: IScalingUpSystemStatusCalculator 
* @Description: the calculator for scaling up policies
* @author Chenhao Qu
* @date 07/06/2015 12:27:57 pm 
*  
*/
public interface IScalingUpSystemStatusCalculator {

	/**
	 * @Title: calculateTargetSystemStatus 
	 * @Description: calculate for the provision when the current provision cannot satisfy the current request rate
	 * @param systemStatus the system status
	 * @return the target provision
	 * @throws
	 */
	public TargetSystemStatus calculateTargetSystemStatus(SystemStatus systemStatus);
	/**
	 * @Title: setScalingPoliciesConfiguration 
	 * @Description: set the scaling policies configuration
	 * @param scalingPoliciesConfiguration the scaling policies configuration
	 * @throws
	 */
	public void setScalingPoliciesConfiguration(IScalingPoliciesConfiguration scalingPoliciesConfiguration);
}
