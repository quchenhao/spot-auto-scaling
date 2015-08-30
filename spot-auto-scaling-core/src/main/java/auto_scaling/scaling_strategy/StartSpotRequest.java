package auto_scaling.scaling_strategy;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: StartSpotRequest 
* @Description: the request to start spot instances of the certain type
* @author Chenhao Qu
* @date 07/06/2015 2:53:22 pm 
*  
*/
public class StartSpotRequest extends StartVMsRequest{
	
	/** 
	* @Fields biddingPrice : the submitted bidding price
	*/ 
	protected double biddingPrice;
	
	/** 
	* <p>Description: </p> 
	* @param instanceTemplate the instance type
	* @param num the number of spot instances to start
	* @param biddingPrice the submitted bidding price
	*/
	public StartSpotRequest(InstanceTemplate instanceTemplate, int num, double biddingPrice) {
		super(instanceTemplate, num);
		this.biddingPrice = biddingPrice;
	}

	/**
	 * @Title: getBiddingPrice 
	 * @Description: get the submitted bidding price
	 * @return the submitted bidding price
	 * @throws
	 */
	public double getBiddingPrice() {
		return biddingPrice;
	}

	/* (non-Javadoc) 
	* <p>Title: toString</p> 
	* <p>Description: </p> 
	* @return 
	* @see java.lang.Object#toString() 
	*/
	@Override
	public String toString() {
		return "start spot " + instanceTemplate.toString() + " " + num + " " + biddingPrice;
	}
}
