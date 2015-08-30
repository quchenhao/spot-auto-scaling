package auto_scaling.capacity;

import java.util.Map;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: ICapacityCalculator 
* @Description: the interface for estimating instance capacity and requests rate
* @author Chenhao Qu
* @date 01/06/2015 12:07:52 pm 
*  
*/
public interface ICapacityCalculator {

	/** 
	* @Title: getEstimatedBasicCapacity 
	* @Description: calculate the basic capacity for each instance type
	* @param instanceTemplate the instance type
	* @return the basic capacity for the instance type
	* @throws 
	*/
	public long getEstimatedBasicCapacity(InstanceTemplate instanceTemplate);
	/** 
	* @Title: getEstimatedNumOfRequestsByUtilization 
	* @Description: estimate the current number of requests rate in the instance
	* @param instanceTemplate the instance type
	* @param resourceConsumptionValues the resources' utilization of the instance
	* @return the estimated request rate in the instance
	* @throws 
	*/
	public long getEstimatedNumOfRequestsByUtilization(InstanceTemplate instanceTemplate, Map<String, Number> resourceConsumptionValues);
	
	/**
	 * @Title: getEstimatedMaximumCapacity 
	 * @Description: calculate the maximum capacity for each instance type
	 * @param instanceTemplate
	 * @return the maximum capacity for the instance type
	 * @throws
	 */
	public long getEstimatedMaximumCapacity(InstanceTemplate instanceTemplate);
}
