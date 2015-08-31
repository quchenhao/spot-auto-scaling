package auto_scaling.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import auto_scaling.scaling_strategy.IScalingUtil;
import auto_scaling.scaling_strategy.IScalingDownSystemStatusCalculator;
import auto_scaling.scaling_strategy.IScalingUpSystemStatusCalculator;
import auto_scaling.scaling_strategy.bidding.IBiddingStrategy;
import auto_scaling.scaling_strategy.bidding.ITruthfulBiddingPriceCalculator;

/** 
* @ClassName: DefaultScalingPoliciesConfigurationLoader 
* @Description: default loader to load scaling policies configuration
* @author Chenhao Qu
* @date 04/06/2015 3:31:18 pm 
*  
*/
public class DefaultScalingPoliciesConfigurationLoader implements IScalingPoliciesConfigurationLoader{

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param limits
	* @param inputStream
	* @return
	* @throws InstantiationException
	* @throws IllegalAccessException
	* @throws ClassNotFoundException
	* @throws IllegalArgumentException
	* @throws InvocationTargetException
	* @throws NoSuchMethodException
	* @throws SecurityException
	* @throws IOException 
	* @see auto_scaling.configuration.IScalingPoliciesConfigurationLoader#load(auto_scaling.configuration.Limits, java.io.InputStream) 
	*/
	@Override
	public IScalingPoliciesConfiguration load(InputStream inputStream)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		Properties properties = new Properties();
		properties.load(inputStream);
		
		String scalingUtilClass = properties.getProperty(SCALING_UTIL);
		String truthfulBiddingPriceCalculatorClass = properties.getProperty(TRUTHFUL_BIDDING_PRICE_CALCULATOR);
		String biddingStrategyClass = properties.getProperty(BIDDING_STRATEGY);
		String scalingUpSystemStatusCalculcatorClass = properties.getProperty(SCALING_UP_SYSTEM_STATUS_CALCULATOR_STRING);
		String scalingDownSystemStatusCalculcatorClass = properties.getProperty(SCALING_DOWN_SYSTEM_STATUS_CALCULATOR_STRING);
		String scalingPoliciesConfigurationClass = properties.getProperty(SCALING_POLICIES_CONFIGURATION);
		
		IScalingPoliciesConfiguration scalingPoliciesConfiguration = (IScalingPoliciesConfiguration)(Class.forName(scalingPoliciesConfigurationClass).newInstance());
		
		IScalingUtil scalingUtil = (IScalingUtil)(Class.forName(scalingUtilClass).newInstance());
		ITruthfulBiddingPriceCalculator truthfulBiddingPriceCalculator = (ITruthfulBiddingPriceCalculator)(Class.forName(truthfulBiddingPriceCalculatorClass).newInstance());
		IBiddingStrategy biddingStrategy = (IBiddingStrategy)(Class.forName(biddingStrategyClass).newInstance());
		
		IScalingUpSystemStatusCalculator scalingUpSystemStatusCalculator = (IScalingUpSystemStatusCalculator)(Class.forName(scalingUpSystemStatusCalculcatorClass).newInstance());
		scalingUpSystemStatusCalculator.setScalingPoliciesConfiguration(scalingPoliciesConfiguration);
		
		IScalingDownSystemStatusCalculator scalingDownSystemStatusCalculator = (IScalingDownSystemStatusCalculator)(Class.forName(scalingDownSystemStatusCalculcatorClass).newInstance());
		scalingDownSystemStatusCalculator.setScalingPoliciesConfiguration(scalingPoliciesConfiguration);
		
		synchronized(scalingPoliciesConfiguration) {
			scalingPoliciesConfiguration.setScalingUtil(scalingUtil);
			scalingPoliciesConfiguration.setBiddingStartegy(biddingStrategy);
			scalingPoliciesConfiguration.setScalingDownSystemStatusCalculator(scalingDownSystemStatusCalculator);
			scalingPoliciesConfiguration.setScalingUpSystemStatusCalculator(scalingUpSystemStatusCalculator);
			scalingPoliciesConfiguration.setTruthfulBiddingPriceCalculator(truthfulBiddingPriceCalculator);
		}
		return scalingPoliciesConfiguration;
	}

}
