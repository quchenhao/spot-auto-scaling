package auto_scaling.loadbalancer.weightcalculator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import auto_scaling.cloud.InstanceStatus;

/** 
* @ClassName: EqualWeightCalculator 
* @Description: all instances have the same weight
* @author Chenhao Qu
* @date 06/06/2015 1:51:45 pm 
*  
*/
public class EqualWeightCalculator implements IWeightCalculator {

	/* (non-Javadoc) 
	* <p>Title: getWeight</p> 
	* <p>Description: </p> 
	* @param attachedInstances
	* @return 
	* @see auto_scaling.loadbalancer.weightcalculator.IWeightCalculator#getWeight(java.util.Iterator) 
	*/
	public Map<InstanceStatus, Integer> getWeight(
			Iterator<InstanceStatus> attachedInstances) {
		Map<InstanceStatus, Integer> weights = new HashMap<InstanceStatus, Integer>();
		
		while (attachedInstances.hasNext()) {
			InstanceStatus instanceStatus = attachedInstances.next();
			weights.put(instanceStatus, 1);
		}
		
		return weights;
	}

}
