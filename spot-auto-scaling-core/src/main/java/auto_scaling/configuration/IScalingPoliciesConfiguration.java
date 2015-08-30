package auto_scaling.configuration;

import auto_scaling.scaling_strategy.IScalingUtil;
import auto_scaling.scaling_strategy.IScalingDownSystemStatusCalculator;
import auto_scaling.scaling_strategy.IScalingUpSystemStatusCalculator;
import auto_scaling.scaling_strategy.bidding.IBiddingStrategy;
import auto_scaling.scaling_strategy.bidding.ITruthfulBiddingPriceCalculator;

/** 
* @ClassName: IScalingPoliciesConfiguration 
* @Description: defines the specific scaling polices in the auto scaling
* @author Chenhao Qu
* @date 05/06/2015 11:17:02 am 
*  
*/
public interface IScalingPoliciesConfiguration {
	/**
	 * @Title: getScalingUtil 
	 * @Description: get the utility class for auto scaling
	 * @return the utility class for auto scaling
	 * @throws
	 */
	public IScalingUtil getScalingUtil();
	/**
	 * @Title: setScalingUtil 
	 * @Description: set the utility class for auto scaling
	 * @param scalingUtil the new utility class for auto scaling 
	 * @throws
	 */
	public void setScalingUtil(IScalingUtil scalingUtil);
	/**
	 * @Title: getScalingUpSystemStatusCalculator 
	 * @Description: get the target system status calculator for scaling up
	 * @return the target system status calculator for scaling up
	 * @throws
	 */
	public IScalingUpSystemStatusCalculator getScalingUpSystemStatusCalculator();
	/**
	 * @Title: setScalingUpSystemStatusCalculator 
	 * @Description: set the target system status calculator for scaling up
	 * @param scalingUpSystemStatusCalculator the new target system status calculator for scaling up
	 * @throws
	 */
	public void setScalingUpSystemStatusCalculator(IScalingUpSystemStatusCalculator scalingUpSystemStatusCalculator);
	/**
	 * @Title: getScalingDownSystemStatusCalculator 
	 * @Description: get the target system status calculator for scaling down
	 * @return the target system status calculator for scaling down
	 * @throws
	 */
	public IScalingDownSystemStatusCalculator getScalingDownSystemStatusCalculator();
	/**
	 * @Title: setScalingDownSystemStatusCalculator 
	 * @Description: set the target system status calculator for scaling down
	 * @param scalingDownSystemStatusCalculator the new target system status calculator for scaling down
	 * @throws
	 */
	public void setScalingDownSystemStatusCalculator(IScalingDownSystemStatusCalculator scalingDownSystemStatusCalculator);
	/**
	 * @Title: getTruthfulBiddingPriceCalculator 
	 * @Description: get the calculator that calculators the truthful bidding price
	 * @return the calculator that calculators the truthful bidding price
	 * @throws
	 */
	public ITruthfulBiddingPriceCalculator getTruthfulBiddingPriceCalculator();
	/**
	 * @Title: setTruthfulBiddingPriceCalculator 
	 * @Description: set the calculator that calculators the truthful bidding price
	 * @param truthfulBiddingPriceCalculator the new calculator that calculators the truthful bidding price
	 * @throws
	 */
	public void setTruthfulBiddingPriceCalculator(ITruthfulBiddingPriceCalculator truthfulBiddingPriceCalculator);
	/**
	 * @Title: getBiddingStartegy 
	 * @Description: get the bidding strategy
	 * @return the bidding strategy
	 * @throws
	 */
	public IBiddingStrategy getBiddingStartegy();
	/**
	 * @Title: setBiddingStartegy 
	 * @Description: get the bidding strategy
	 * @param biddingStartegy the new bidding strategy
	 * @throws
	 */
	public void setBiddingStartegy(IBiddingStrategy biddingStartegy);
}
