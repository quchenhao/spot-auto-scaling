package auto_scaling.cloud.cloudsim;

import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.PendingSpotInstanceStatus;

/** 
* @ClassName: CloudSimPendingSpotInstanceStatus 
* @Description: pending spot instance status for cloudsim
* @author Chenhao Qu
* @date 04/06/2015 1:35:41 pm 
*  
*/
public class CloudSimPendingSpotInstanceStatus extends PendingSpotInstanceStatus{

	/** 
	* @Fields waitingTime : the random time to wait for fullfillment
	*/ 
	protected long waitingTime;
	/** 
	* @Fields startTime : the random time to wait for boot up
	*/ 
	protected double startTime;
	
	/** 
	* <p>Description: initialize with all required parameters</p> 
	* @param requestId the spot request id
	* @param type the instance type
	* @param biddingPrice the bidding price
	* @param waitingTime the random time to wait for fullfillment
	* @param startTime the random time to wait for boot up
	*/
	public CloudSimPendingSpotInstanceStatus(String requestId,
			InstanceTemplate type, double biddingPrice, long waitingTime, double startTime) {
		super(requestId, type, biddingPrice);
		this.waitingTime = waitingTime;
		this.startTime = startTime;
	}

	/**
	 * @Title: getWaitingTime 
	 * @Description: get the random time to wait for boot up
	 * @return the random time to wait for boot up
	 * @throws
	 */
	public synchronized long getWaitingTime() {
		return waitingTime;
	}

	/**
	 * @Title: getStartTime 
	 * @Description: get the random time to wait for boot up
	 * @return the random time to wait for boot up
	 * @throws
	 */
	public synchronized double getStartTime() {
		return startTime;
	}

	
}
