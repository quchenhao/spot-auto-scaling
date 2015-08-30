package auto_scaling.scaling_strategy.bidding;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: ITruthfulBiddingPriceCalculator 
* @Description: the calculator for the truthful bidding price
* @author Chenhao Qu
* @date 07/06/2015 3:21:01 pm 
*  
*/
public interface ITruthfulBiddingPriceCalculator {

	/**
	 * @Title: getTruthfulBiddingPrice 
	 * @Description: get the truthful bidding price for the given instance type
	 * @param onDemandTemplate the on demand type
	 * @param unprovisionedOnDemand the unprovisioned on demand instances
	 * @param spotInstanceTemplate the spot instance type
	 * @param numOfChosenTypes the number of chosen types
	 * @param numOfTargetInstances the number of instances of the instance type
	 * @return the truthful bidding price for the given instance type
	 * @throws
	 */
	public double getTruthfulBiddingPrice(InstanceTemplate onDemandTemplate, int unprovisionedOnDemand, InstanceTemplate spotInstanceTemplate, int numOfChosenTypes, int numOfTargetInstances);
}
