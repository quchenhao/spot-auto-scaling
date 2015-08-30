package auto_scaling.scaling_strategy.bidding;

import auto_scaling.scaling_strategy.SpotBiddingInfo;

/** 
* @ClassName: TruthfulBiddingStrategy 
* @Description: the bidding strategy that always bids the truthful bidding price
* @author Chenhao Qu
* @date 07/06/2015 4:26:01 pm 
*  
*/
public class TruthfulBiddingStrategy implements IBiddingStrategy {

	/* (non-Javadoc) 
	* <p>Title: getBidPrice</p> 
	* <p>Description: </p> 
	* @param spotBiddingInfo
	* @return 
	* @see auto_scaling.scaling_strategy.bidding.IBiddingStrategy#getBidPrice(auto_scaling.scaling_strategy.SpotBiddingInfo) 
	*/
	@Override
	public double getBidPrice(SpotBiddingInfo spotBiddingInfo) {
		
		return spotBiddingInfo.getTruthfulBiddingPrice();
	}

	

}
