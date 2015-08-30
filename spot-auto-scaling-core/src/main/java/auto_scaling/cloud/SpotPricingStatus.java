package auto_scaling.cloud;

import java.util.Date;

/** 
* @ClassName: SpotPricingStatus 
* @Description: the spot price status for each vm type
* @author Chenhao Qu
* @date 04/06/2015 12:16:49 pm 
*  
*/
public class SpotPricingStatus {
	
	/** 
	* @Fields instanceTemplate : the instance type
	*/ 
	protected final InstanceTemplate instanceTemplate;
	/** 
	* @Fields price : the most recently known market price
	*/ 
	protected double price;
	/** 
	* @Fields lastUpdateTimeStampDate : the time price updated
	*/ 
	protected Date lastUpdateTimeStampDate;
	
	/** 
	* <p>Description: initialize with instance type</p> 
	* @param instanceTemplate 
	*/
	public SpotPricingStatus(InstanceTemplate instanceTemplate) {
		if (instanceTemplate == null) {
			throw new NullPointerException("instance template cannot be null!");
		}
		this.instanceTemplate = instanceTemplate;
	}

	/**
	 * @Title: getPrice 
	 * @Description: get the most recently known market price
	 * @return the most recently known market price
	 * @throws
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @Title: setPrice 
	 * @Description: update the market price
	 * @param price the new price
	 * @throws
	 */
	public void setPrice(double price) {
		this.price = price;
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
	 * @Title: getLastUpdateTimeStamp 
	 * @Description: get last update time
	 * @return last update time
	 * @throws
	 */
	public Date getLastUpdateTimeStamp() {
		return lastUpdateTimeStampDate;
	}
	
	/**
	 * @Title: setLastUpdateTimeStamp 
	 * @Description: set last update time
	 * @param timeStamp new update time
	 * @throws
	 */
	public void setLastUpdateTimeStamp(Date timeStamp) {
		this.lastUpdateTimeStampDate = timeStamp;
	}

}
