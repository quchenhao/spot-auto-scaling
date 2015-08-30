package auto_scaling.cloud;

/** 
* @ClassName: PendingSpotInstanceStatus 
* @Description: transient instance status for spot instances that have not been fulfilled
* @author Chenhao Qu
* @date 04/06/2015 11:24:22 am 
*  
*/
public class PendingSpotInstanceStatus extends SpotInstanceStatus{

	/** 
	* <p>Description: initialize with all constants fields</p> 
	* @param requestId the spot request id
	* @param type the instance type
	* @param biddingPrice the bidding price of the instance
	*/
	public PendingSpotInstanceStatus(String requestId, InstanceTemplate type, double biddingPrice) {
		//instance id should be null as no instance has been assigned
		super(null, requestId, null, null, null, type, biddingPrice);
		setRunningStatus(RunningStatus.ASKED);
	}

}
