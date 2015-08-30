package auto_scaling.loadbalancer.weightcalculator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: ResourceCapacityWeightCalculator 
* @Description: the weight calculator according to the capacity of each instance
* @author Chenhao Qu
* @date 06/06/2015 1:53:28 pm 
*  
*/
public class ResourceCapacityWeightCalculator implements IWeightCalculator {
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public ResourceCapacityWeightCalculator () {
	}
	
	/* (non-Javadoc) 
	* <p>Title: getWeight</p> 
	* <p>Description: </p> 
	* @param attachedInstances
	* @return
	* @throws Exception 
	* @see auto_scaling.loadbalancer.weightcalculator.IWeightCalculator#getWeight(java.util.Iterator) 
	*/
	public Map<InstanceStatus, Integer> getWeight(
			Iterator<InstanceStatus> attachedInstances) throws Exception {
		
		Map<InstanceStatus, Integer> weights = new HashMap<InstanceStatus, Integer>();
		while (attachedInstances.hasNext()) {
			InstanceStatus instanceStatus = attachedInstances.next();
			InstanceTemplate template = instanceStatus.getType();
			
			long capacity = template.getBasicCapacity();
			
			weights.put(instanceStatus, (int)capacity);
		}
		
		return weights;
	}

}
