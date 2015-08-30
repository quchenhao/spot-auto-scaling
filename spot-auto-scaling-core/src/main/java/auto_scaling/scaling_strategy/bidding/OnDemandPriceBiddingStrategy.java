package auto_scaling.scaling_strategy.bidding;

import auto_scaling.scaling_strategy.SpotBiddingInfo;

/** 
* @ClassName: OnDemandPriceBiddingStrategy 
* @Description: the bidding strategy implementation that always bids the on demand price of the instance type
* @author Chenhao Qu
* @date 07/06/2015 4:25:12 pm 
*  
*/
public class OnDemandPriceBiddingStrategy implements IBiddingStrategy {

	/* (non-Javadoc) 
	* <p>Title: getBidPrice</p> 
	* <p>Description: </p> 
	* @param spotBiddingInfo
	* @return 
	* @see auto_scaling.scaling_strategy.bidding.IBiddingStrategy#getBidPrice(auto_scaling.scaling_strategy.SpotBiddingInfo) 
	*/
	@Override
	public double getBidPrice(SpotBiddingInfo spotBiddingInfo) {
		
		return spotBiddingInfo.getInstanceTemplate().getOnDemandPrice();
	}

	

}
