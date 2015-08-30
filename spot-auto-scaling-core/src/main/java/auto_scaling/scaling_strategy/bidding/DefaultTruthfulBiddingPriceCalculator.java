package auto_scaling.scaling_strategy.bidding;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: DefaultTruthfulBiddingPriceCalculator 
* @Description: the default implementation for the truthful bidding price calculator
* @author Chenhao Qu
* @date 07/06/2015 3:17:33 pm 
*  
*/
public class DefaultTruthfulBiddingPriceCalculator implements ITruthfulBiddingPriceCalculator{

	/* (non-Javadoc) 
	* <p>Title: getTruthfulBiddingPrice</p> 
	* <p>Description: calculate based on the on demand provision cost</p> 
	* @param onDemandTemplate
	* @param unprovisionedOnDemand
	* @param spotInstanceTemplate
	* @param numOfChosenTypes
	* @param numOfTargetInstances
	* @return 
	* @see auto_scaling.scaling_strategy.bidding.ITruthfulBiddingPriceCalculator#getTruthfulBiddingPrice(auto_scaling.cloud.InstanceTemplate, int, auto_scaling.cloud.InstanceTemplate, int, int) 
	*/
	public double getTruthfulBiddingPrice(InstanceTemplate onDemandTemplate,
			int unprovisionedOnDemand, InstanceTemplate spotInstanceTemplate,
			int numOfChosenTypes, int numOfTargetInstances) {
		return onDemandTemplate.getOnDemandPrice() * unprovisionedOnDemand / numOfChosenTypes / numOfTargetInstances;
	}
	
}
