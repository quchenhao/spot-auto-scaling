package auto_scaling.cloud;

import auto_scaling.core.FaultTolerantLevel;

/** 
* @ClassName: DynamicCapacityInstanceTemplate 
* @Description: the instance template that returns the capacity according to current ft level
* @author Chenhao Qu
* @date 15/06/2015 2:36:18 pm 
*  
*/
public class DynamicCapacityInstanceTemplate extends InstanceTemplate{

	/** 
	* <p>Description: initialize with all constants fields</p> 
	* @param name the instance type name
	* @param vcpuNum the number of vcpus
	* @param ecuNum the processing capacity in amazon ecu
	* @param memoryNum the amount of memory in GB
	* @param os the operating system type
	* @param onDemandPrice the on demand price of the instance
	* @param isSupportHvm whether the vm type supports full virtualization
	* @param isSupportParavirtual whether the vm type supports para-virtualization
	*/
	public DynamicCapacityInstanceTemplate(String name, int vcpuNum,
			double ecuNum, double memoryNum, String os, double onDemandPrice,
			boolean isSupportHvm, boolean isSupportParavirtual) {
		super(name, vcpuNum, ecuNum, memoryNum, os, onDemandPrice, isSupportHvm,
				isSupportParavirtual);
	}

	/* (non-Javadoc) 
	* <p>Title: getCapacity</p> 
	* <p>Description: get capacity according to current ft level</p> 
	* @param ftLevel
	* @return 
	* @see auto_scaling.cloud.InstanceTemplate#getCapacity(auto_scaling.core.FaultTolerantLevel) 
	*/
	@Override
	public long getCapacity(FaultTolerantLevel ftLevel) {
		FaultTolerantLevel max = FaultTolerantLevel.MAX;
		return (maximumCapacity - basicCapacity)/max.getLevel() * ftLevel.getLevel() + basicCapacity;
	}

}
