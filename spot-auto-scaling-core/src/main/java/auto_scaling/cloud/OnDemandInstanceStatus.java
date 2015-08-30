package auto_scaling.cloud;

import java.util.Date;

/** 
* @ClassName: OnDemandInstanceStatus 
* @Description: store the instance status for on demand instances
* @author Chenhao Qu
* @date 04/06/2015 11:22:24 am 
*  
*/
public class OnDemandInstanceStatus extends InstanceStatus{
	
	/** 
	* <p>Description: initialize with all constants fields</p> 
	* @param id the id of the instance
	* @param publicUrl the url of the instance to access it from the Internet
	* @param privateUrl the url of the instance to access it from within the cloud
	* @param launchTime the launch time of the instance
	* @param type the instance type
	*/
	public OnDemandInstanceStatus(String id, String publicUrl, String privateUrl, Date launchTime, InstanceTemplate type) {
		super(id, publicUrl, privateUrl, launchTime, type);
	}
}
