package auto_scaling.cloud.cloudsim;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: ConstantSpotRequestFullfillmentDistribution 
* @Description: constant time to fullfill spot requests
* @author Chenhao Qu
* @date 04/06/2015 1:42:15 pm 
*  
*/
public class ConstantSpotRequestFullfillmentDistribution implements ISpotRequestFullfillmentDistribution {
	
	/** 
	* @Fields constant : the constant time wait to fullfill spot requests
	*/ 
	protected long constant;

	/** 
	* <p>Description: initialize with constant</p> 
	* @param constant the constant time wait to fullfill spot requests
	*/
	public ConstantSpotRequestFullfillmentDistribution(long constant) {
		setConstant(constant);
	}
	
	/**
	 * @Title: setConstant 
	 * @Description: set the constant time wait to fullfill spot requests
	 * @param constant the new constant time wait to fullfill spot requests
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
	* <p>Description: return the constant </p> 
	* @param instanceTemplate
	* @return 
	* @see auto_scaling.cloud.cloudsim.ISpotRequestFullfillmentDistribution#getDelay(auto_scaling.cloud.InstanceTemplate) 
	*/
	@Override
	public long getDelay(InstanceTemplate instanceTemplate) {
		return constant;
	}
	

}
