package auto_scaling.scaling_strategy.bidding;

import auto_scaling.scaling_strategy.SpotBiddingInfo;

/** 
* @ClassName: IBiddingStrategy 
* @Description: the bidding strategy for the spot instances
* @author Chenhao Qu
* @date 07/06/2015 3:20:01 pm 
*  
*/
public interface IBiddingStrategy {
	/**
	 * @Title: getBidPrice 
	 * @Description: get the actual bidding price
	 * @param spotBiddingInfo the spot bidding info
	 * @return the actual bidding price
	 * @throws
	 */
	public double getBidPrice(SpotBiddingInfo spotBiddingInfo);
}
