package auto_scaling.scaling_strategy;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: StartVMsRequest 
* @Description: the request to start vms
* @author Chenhao Qu
* @date 07/06/2015 2:54:45 pm 
*  
*/
public abstract class StartVMsRequest {

	/** 
	* @Fields instanceTemplate : the instance type
	*/ 
	protected InstanceTemplate instanceTemplate;
	/** 
	* @Fields num : the number of instances to start
	*/ 
	protected int num;
	
	/** 
	* <p>Description: </p> 
	* @param instanceTemplate the instance type
	* @param num the number of instances to start
	*/
	public StartVMsRequest(InstanceTemplate instanceTemplate, int num) {
		this.instanceTemplate = instanceTemplate;
		this.num = num;
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
	 * @Title: getNum 
	 * @Description: get the number of instances to start
	 * @return the number of instances to start
	 * @throws
	 */
	public int getNum() {
		return num;
	}
	
	/**
	 * @Title: increaseNum 
	 * @Description: increase the number to start
	 * @throws
	 */
	public void increaseNum() {
		num++;
	}
}
