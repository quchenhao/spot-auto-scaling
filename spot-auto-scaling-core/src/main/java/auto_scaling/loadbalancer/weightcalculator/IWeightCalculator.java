package auto_scaling.loadbalancer.weightcalculator;

import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import auto_scaling.cloud.InstanceStatus;

/** 
* @ClassName: IWeightCalculator 
* @Description: the weight calculator
* @author Chenhao Qu
* @date 06/06/2015 1:52:07 pm 
*  
*/
public interface IWeightCalculator {
	
	/** 
	* @Fields weighterCalculatorLog : the weight calculator calculator
	*/ 
	Logger weighterCalculatorLog = LogManager.getLogger(IWeightCalculator.class);

	/**
	 * @Title: getWeight 
	 * @Description: get the weight for each attached instances
	 * @param attachedInstances the attached instances
	 * @return the weight for each attached instances
	 * @throws Exception
	 * @throws
	 */
	public Map<InstanceStatus, Integer> getWeight(Iterator<InstanceStatus> attachedInstances) throws Exception;
}
