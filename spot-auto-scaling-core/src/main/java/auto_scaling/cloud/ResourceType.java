package auto_scaling.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/** 
* @ClassName: ResourceType 
* @Description: data structure to store the resource type. Currently on CPU and memory are considered
* @author Chenhao Qu
* @date 04/06/2015 11:26:50 am 
*  
*/
public class ResourceType {

	/** 
	* @Fields CPU : CPU resource
	*/ 
	public static ResourceType CPU;
	/** 
	* @Fields MEMORY : memory source
	*/ 
	public static ResourceType MEMORY;
	/** 
	* @Fields allResourceTypes : all the considered resource type
	*/ 
	private static Collection<ResourceType> allResourceTypes = new ArrayList<ResourceType>();
	
	/** 
	* @Fields name : the name of the resource type
	*/ 
	private final String name;
	/** 
	* @Fields scalingThreshold : the utilization threshold for the resource to be saturated for basic capacity 
	*/ 
	private double scalingThreshold;
	/** 
	* @Fields maxThreshold : the utilization threshold for the resource to be saturated form maximum capacity
	*/ 
	private double maxThreshold;
	
	/** 
	* <p>Description: initialize with all fields</p> 
	* @param name the name of the resource type
	* @param scalingThreshold the utilization threshold for the resource to be saturated for basic capacity
	* @param maxThreshold the utilization threshold for the resource to be saturated form maximum capacity
	*/
	public ResourceType(String name, double scalingThreshold, double maxThreshold) {
		this.name = name;
		if (scalingThreshold < 0 || scalingThreshold > 1) {
			throw new IllegalArgumentException("scaling threshold should be between 0 and 1");
		}
		if (maxThreshold < 0 || maxThreshold > 1) {
			throw new IllegalArgumentException("max threshold should be between 0 and 1");
		}
		if (scalingThreshold > maxThreshold) {
			throw new IllegalArgumentException("scaling threshold cannot be larger than max threshold");
		}
		this.scalingThreshold = scalingThreshold;
		this.maxThreshold = maxThreshold;
		allResourceTypes.add(this);
	}


	/**
	 * @Title: getScalingThreshold 
	 * @Description: get the utilization threshold for the resource to be saturated for basic capacity
	 * @return the utilization threshold for the resource to be saturated for basic capacity
	 * @throws
	 */
	public double getScalingThreshold() {
		return scalingThreshold;
	}

	/**
	 * @Title: getMaxThreshold 
	 * @Description: the utilization threshold for the resource to be saturated form maximum capacity
	 * @return the utilization threshold for the resource to be saturated form maximum capacity
	 * @throws
	 */
	public double getMaxThreshold() {
		return maxThreshold;
	}
	
	/**
	 * @Title: setScalingThreshold 
	 * @Description: set the utilization threshold for the resource to be saturated for basic capacity
	 * @param scalingThreshold the utilization threshold for the resource to be saturated for basic capacity
	 * The new threshold should be smaller or equal to current maxThreshold
	 * @throws
	 */
	public void setScalingThreshold(double scalingThreshold) {
		
		if (scalingThreshold > 1 || scalingThreshold <= 0) {
			throw new IllegalArgumentException("Scaling Threshold must between 0 to 1! " + scalingThreshold);
		}
		
		this.scalingThreshold = scalingThreshold;
	}


	/**
	 * @Title: getName 
	 * @Description: get the name of the resource type
	 * @return the name of the resource type
	 * @throws
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @Title: getByName 
	 * @Description: get resource type by resource name
	 * @param name the name of the resource
	 * @return the resource type
	 * @throws
	 */
	public static ResourceType getByName(String name) {
		if (name.equalsIgnoreCase(CPU.getName())) {
			return CPU;
		}
		
		if (name.equalsIgnoreCase(MEMORY.getName())) {
			return MEMORY;
		}
		
		return null;
	}
	
	/**
	 * @Title: getAllResourceTypes 
	 * @Description: get all the considered resource types
	 * @return all the considered resource types
	 * @throws
	 */
	public static Collection<ResourceType> getAllResourceTypes() {
		return Collections.unmodifiableCollection(allResourceTypes);
	}
	
	/* (non-Javadoc) 
	* <p>Title: toString</p> 
	* <p>Description: the resource name</p> 
	* @return 
	* @see java.lang.Object#toString() 
	*/
	@Override
	public String toString() {
		return name;
	}
	
	/* (non-Javadoc) 
	* <p>Title: hashCode</p> 
	* <p>Description: the hash of the resource name</p> 
	* @return 
	* @see java.lang.Object#hashCode() 
	*/
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
