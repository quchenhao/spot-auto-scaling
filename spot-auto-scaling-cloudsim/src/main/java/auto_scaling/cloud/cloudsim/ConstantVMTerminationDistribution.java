package auto_scaling.cloud.cloudsim;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: ConstantVMTerminationDistribution 
* @Description: constant time to shut down a vm
* @author Chenhao Qu
* @date 04/06/2015 1:44:28 pm 
*  
*/
public class ConstantVMTerminationDistribution implements IVMTerminateDistribution {

	/** 
	* @Fields constant : the constant time to wait to shut down a vm
	*/ 
	protected long constant;
	
	/** 
	* <p>Description: initialize with constant</p> 
	* @param constant the constant time to wait to shut down a vm
	*/
	public ConstantVMTerminationDistribution(long constant) {
		setConstant(constant);
	}
	
	/**
	 * @Title: setConstant 
	 * @Description: set the constant time to wait to shut down a vm
	 * @param constant the new constant time to wait to shut down a vm
	 * @throws
	 */
	public void setConstant(long constant) {
		if (constant <= 0) {
			throw new IllegalArgumentException("constant should be postive");
		}
		
		this.constant = constant;
	}

	/* (non-Javadoc) 
	* <p>Title: getDelay</p> 
	* <p>Description: return the constant</p> 
	* @param instanceTemplate
	* @return 
	* @see auto_scaling.cloud.cloudsim.IVMTerminateDistribution#getDelay(auto_scaling.cloud.InstanceTemplate) 
	*/
	@Override
	public long getDelay(InstanceTemplate instanceTemplate) {
		return constant;
	}

}
