package auto_scaling.cloud.aws;

/** 
* @ClassName: AWSSpotRequestStatus 
* @Description: important AWS spot request status
* @author Chenhao Qu
* @date 04/06/2015 12:29:52 pm 
*  
*/
public class AWSSpotRequestStatus {

	/** 
	* @Fields STATE_OPEN : spot request waiting fullfillment
	*/ 
	public static final String STATE_OPEN = "open";
	/** 
	* @Fields STATE_CLOSED : spot request closed
	*/ 
	public static final String STATE_CLOSED = "closed";
	/** 
	* @Fields STATE_CANCELED : spot request cancelled by user
	*/ 
	public static final String STATE_CANCELED = "cancelled";
	/** 
	* @Fields STATE_FAILED : spot request failed to fullfill or shut down by provider
	*/ 
	public static final String STATE_FAILED = "failed";
	/** 
	* @Fields STATE_ACTIVE : spot request fullfilled and running
	*/ 
	public static final String STATE_ACTIVE = "active";
	
	/** 
	* @Fields STATUS_MARK_FOR_TERMINATION : spot instances will soon be shut down by provider
	*/ 
	public static final String STATUS_MARK_FOR_TERMINATION = "marked-for-termination";
	/** 
	* @Fields STATUS_INSTANCE_TERMINATED_BY_PRICE : spot instances terminated by provider
	*/ 
	public static final String STATUS_INSTANCE_TERMINATED_BY_PRICE = "instance-terminated-by-price";
}
