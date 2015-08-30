package auto_scaling.cloud.cloudsim;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: IVMTerminateDistribution 
* @Description: distribution for vm termination time
* @author Chenhao Qu
* @date 04/06/2015 1:55:19 pm 
*  
*/
public interface IVMTerminateDistribution {

	/**
	 * @Title: getDelay 
	 * @Description: get delay for vm termination
	 * @param instanceTemplate
	 * @return delay for vm termiantion
	 * @throws
	 */
	public long getDelay(InstanceTemplate instanceTemplate);
}
