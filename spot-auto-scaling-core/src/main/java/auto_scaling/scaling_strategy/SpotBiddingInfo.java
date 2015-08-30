package auto_scaling.scaling_strategy;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: SpotBiddingInfo 
* @Description: the bidding info of each spot type
* @author Chenhao Qu
* @date 07/06/2015 2:47:14 pm 
*  
*/
public class SpotBiddingInfo implements Comparable<SpotBiddingInfo> {

	/** 
	* @Fields instanceTemplate : the instance type
	*/ 
	protected InstanceTemplate instanceTemplate;
	/** 
	* @Fields num : the number of instance
	*/ 
	protected int num;
	/** 
	* @Fields totalCost : the total cost of one billing hour for the instance type
	*/ 
	protected double totalCost;
	/** 
	* @Fields truthfulBiddingPrice : the truthful bidding price for the instance type
	*/ 
	protected double truthfulBiddingPrice;
	/** 
	* @Fields marketPrice : the current market price
	*/ 
	protected double marketPrice;
	
	/** 
	* <p>Description: </p> 
	* @param instanceTemplate the instance type
	* @param num the number of instance
	* @param marketPrice the current market price
	* @param truthfulBiddingPrice the truthful bidding price for the instance type
	*/
	public SpotBiddingInfo (InstanceTemplate instanceTemplate, int num, double marketPrice, double truthfulBiddingPrice) {
		this.instanceTemplate = instanceTemplate;
		this.marketPrice = marketPrice;
		this.totalCost = num * marketPrice;
		this.truthfulBiddingPrice = truthfulBiddingPrice;
		this.num = num;
	}

	/* (non-Javadoc) 
	* <p>Title: compareTo</p> 
	* <p>Description: compare cost efficiency based on total cost</p> 
	* @param arg
	* @return 
	* @see java.lang.Comparable#compareTo(java.lang.Object) 
	*/
	public int compareTo(SpotBiddingInfo arg) {
		if (totalCost > arg.getTotalCostEachHour()) {
			return 1;
		}
		else if (totalCost == arg.getTotalCostEachHour()) {
			return 0;
		}
		else {
			return -1;
		}
	}
	
	/**
	 * @Title: getNum 
	 * @Description: get the number of instance
	 * @return the number of instance
	 * @throws
	 */
	public int getNum() {
		return num;
	}
	
	/**
	 * @Title: getTotalCostEachHour 
	 * @Description: get the total cost of one billing hour for the instance type
	 * @return the total cost of one billing hour for the instance type
	 * @throws
	 */
	public double getTotalCostEachHour() {
		return totalCost;
	}
	
	/**
	 * @Title: getInstanceTemplate 
	 * @Description: get the instance type
	 * @return the instance type
	 * @throws
	 */
	public InstanceTemplate getInstanceTemplate() {
		return instanceTemplate;
	}

	/**
	 * @Title: getTruthfulBiddingPrice 
	 * @Description: get the truthful bidding price for the instance type
	 * @return the truthful bidding price for the instance type
	 * @throws
	 */
	public double getTruthfulBiddingPrice() {
		return truthfulBiddingPrice;
	}

	/**
	 * @Title: getMarketPrice 
	 * @Description: get the current market price
	 * @return the current market price
	 * @throws
	 */
	public double getMarketPrice() {
		return marketPrice;
	}
	
}
