package auto_scaling.scaling_strategy;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: StartOnDemandRequest 
* @Description: the request to start on demand instances
* @author Chenhao Qu
* @date 07/06/2015 2:52:28 pm 
*  
*/
public class StartOnDemandRequest extends StartVMsRequest {

	/** 
	* <p>Description: </p> 
	* @param instanceTemplate the instance type
	* @param num the number of instances to start
	*/
	public StartOnDemandRequest(InstanceTemplate instanceTemplate, int num) {
		super(instanceTemplate, num);
	}

	/* (non-Javadoc) 
	* <p>Title: toString</p> 
	* <p>Description: </p> 
	* @return 
	* @see java.lang.Object#toString() 
	*/
	@Override
	public String toString() {
		return "start on demand " + instanceTemplate.toString() + " " + num;
	}
}
