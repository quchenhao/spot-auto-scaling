package auto_scaling.configuration;

import auto_scaling.scaling_strategy.IScalingUtil;
import auto_scaling.scaling_strategy.IScalingDownSystemStatusCalculator;
import auto_scaling.scaling_strategy.IScalingUpSystemStatusCalculator;
import auto_scaling.scaling_strategy.bidding.IBiddingStrategy;
import auto_scaling.scaling_strategy.bidding.ITruthfulBiddingPriceCalculator;

/** 
* @ClassName: DefaultScalingPoliciesConfiguration 
* @Description: default implementation for scaling policies configuration
* @author Chenhao Qu
* @date 04/06/2015 1:59:26 pm 
*  
*/
public class DefaultScalingPoliciesConfiguration implements IScalingPoliciesConfiguration{

	/** 
	* @Fields scalingUtil : the utility to help make scaling decisions
	*/ 
	protected IScalingUtil scalingUtil;
	
	/** 
	* @Fields truthfulBiddingPriceCalculator : the calculator that calculates truthful bidding prices for each vm type
	*/ 
	protected ITruthfulBiddingPriceCalculator truthfulBiddingPriceCalculator;
	/** 
	* @Fields biddingStartegy : the calculator that calculator that calculates the actual bidding price
	*/ 
	protected IBiddingStrategy biddingStartegy;
	/** 
	* @Fields scalingUpSystemStatusCalculator : the calculator that calculates the scaling up plan
	*/ 
	protected IScalingUpSystemStatusCalculator scalingUpSystemStatusCalculator;
	/** 
	* @Fields scalingDownSystemStatusCalculator : the calculator that calculates the scaling down plan
	*/ 
	protected IScalingDownSystemStatusCalculator scalingDownSystemStatusCalculator;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public DefaultScalingPoliciesConfiguration() {}
	
	/* (non-Javadoc) 
	* <p>Title: getScalingUtil</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.IScalingPoliciesConfiguration#getScalingUtil() 
	*/
	@Override
	public synchronized IScalingUtil getScalingUtil() {
		return scalingUtil;
	}
	public synchronized void setScalingUtil(
			IScalingUtil capacityCalculator) {
		if (capacityCalculator == null) {
			throw new NullPointerException("capacity calculator cannot be null");
		}
		this.scalingUtil = capacityCalculator;
	}
	/* (non-Javadoc) 
	* <p>Title: getScalingUpSystemStatusCalculator</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.IScalingPoliciesConfiguration#getScalingUpSystemStatusCalculator() 
	*/
	@Override
	public synchronized IScalingUpSystemStatusCalculator getScalingUpSystemStatusCalculator() {
		return scalingUpSystemStatusCalculator;
	}
	/* (non-Javadoc) 
	* <p>Title: setScalingUpSystemStatusCalculator</p> 
	* <p>Description: </p> 
	* @param scalingUpSystemStatusCalculator 
	* @see auto_scaling.configuration.IScalingPoliciesConfiguration#setScalingUpSystemStatusCalculator(auto_scaling.scaling_strategy.IScalingUpSystemStatusCalculator) 
	*/
	@Override
	public synchronized void setScalingUpSystemStatusCalculator(
			IScalingUpSystemStatusCalculator scalingUpSystemStatusCalculator) {
		if (scalingUpSystemStatusCalculator == null) {
			throw new NullPointerException("scaling up system status calculator cannot be null");
		}
		this.scalingUpSystemStatusCalculator = scalingUpSystemStatusCalculator;
	}
	/* (non-Javadoc) 
	* <p>Title: getScalingDownSystemStatusCalculator</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.IScalingPoliciesConfiguration#getScalingDownSystemStatusCalculator() 
	*/
	@Override
	public synchronized IScalingDownSystemStatusCalculator getScalingDownSystemStatusCalculator() {
		return scalingDownSystemStatusCalculator;
	}
	/* (non-Javadoc) 
	* <p>Title: setScalingDownSystemStatusCalculator</p> 
	* <p>Description: </p> 
	* @param scalingDownSystemStatusCalculator 
	* @see auto_scaling.configuration.IScalingPoliciesConfiguration#setScalingDownSystemStatusCalculator(auto_scaling.scaling_strategy.IScalingDownSystemStatusCalculator) 
	*/
	@Override
	public synchronized void setScalingDownSystemStatusCalculator(
			IScalingDownSystemStatusCalculator scalingDownSystemStatusCalculator) {
		if (scalingDownSystemStatusCalculator == null) {
			throw new NullPointerException("scaling down system status calculator cannot be null");
		}
		this.scalingDownSystemStatusCalculator = scalingDownSystemStatusCalculator;
	}
	/* (non-Javadoc) 
	* <p>Title: getTruthfulBiddingPriceCalculator</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.IScalingPoliciesConfiguration#getTruthfulBiddingPriceCalculator() 
	*/
	@Override
	public synchronized ITruthfulBiddingPriceCalculator getTruthfulBiddingPriceCalculator() {
		return truthfulBiddingPriceCalculator;
	}
	/* (non-Javadoc) 
	* <p>Title: setTruthfulBiddingPriceCalculator</p> 
	* <p>Description: </p> 
	* @param truthfulBiddingPriceCalculator 
	* @see auto_scaling.configuration.IScalingPoliciesConfiguration#setTruthfulBiddingPriceCalculator(auto_scaling.scaling_strategy.bidding.ITruthfulBiddingPriceCalculator) 
	*/
	@Override
	public synchronized void setTruthfulBiddingPriceCalculator(
			ITruthfulBiddingPriceCalculator truthfulBiddingPriceCalculator) {
		if (truthfulBiddingPriceCalculator == null) {
			throw new NullPointerException("truthful bidding calculator cannot be null");
		}
		this.truthfulBiddingPriceCalculator = truthfulBiddingPriceCalculator;
	}
	/* (non-Javadoc) 
	* <p>Title: getBiddingStartegy</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.IScalingPoliciesConfiguration#getBiddingStartegy() 
	*/
	@Override
	public synchronized IBiddingStrategy getBiddingStartegy() {
		return biddingStartegy;
	}
	/* (non-Javadoc) 
	* <p>Title: setBiddingStartegy</p> 
	* <p>Description: </p> 
	* @param biddingStartegy 
	* @see auto_scaling.configuration.IScalingPoliciesConfiguration#setBiddingStartegy(auto_scaling.scaling_strategy.bidding.IBiddingStrategy) 
	*/
	@Override
	public synchronized void setBiddingStartegy(IBiddingStrategy biddingStartegy) {
		if (biddingStartegy == null) {
			throw new NullPointerException("bidding strategy cannot be null");
		}
		this.biddingStartegy = biddingStartegy;
	}
	
}
