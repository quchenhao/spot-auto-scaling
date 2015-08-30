package auto_scaling.configuration;

import java.io.InputStream;

/** 
* @ClassName: IScalingPoliciesConfigurationLoader 
* @Description: loader to load sclaing policies configuration
* @author Chenhao Qu
* @date 05/06/2015 1:54:40 pm 
*  
*/
public interface IScalingPoliciesConfigurationLoader {
	
	static final String SCALING_UTIL = "scaling_util";
	static final String TRUTHFUL_BIDDING_PRICE_CALCULATOR = "truthful_billding_price_calculator";
	static final String BIDDING_STRATEGY = "bidding_strategy";
	static final String SCALING_UP_SYSTEM_STATUS_CALCULATOR_STRING = "scaling_up_system_status_calculator";
	static final String SCALING_DOWN_SYSTEM_STATUS_CALCULATOR_STRING = "scaling_down_system_status_calculator";
	static final String SCALING_POLICIES_CONFIGURATION = "scaling_policies_configuration";
	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param limits the limits
	 * @param inputStream the input stream
	 * @return the scaling policies configuration
	 * @throws Exception
	 * @throws
	 */
	public IScalingPoliciesConfiguration load(Limits limits, InputStream inputStream) throws Exception;
}
