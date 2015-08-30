package auto_scaling.cloud;

import auto_scaling.core.FaultTolerantLevel;

/** 
* @ClassName: StaticCapacityInstanceTemplate 
* @Description: the instance template that always return constant capacity
* @author Chenhao Qu
* @date 15/06/2015 2:38:26 pm 
*  
*/
public class StaticCapacityInstanceTemplate extends InstanceTemplate {

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
	public StaticCapacityInstanceTemplate(String name, int vcpuNum,
			double ecuNum, double memoryNum, String os, double onDemandPrice,
			boolean isSupportHvm, boolean isSupportParavirtual) {
		super(name, vcpuNum, ecuNum, memoryNum, os, onDemandPrice, isSupportHvm,
				isSupportParavirtual);
	}

	/* (non-Javadoc) 
	* <p>Title: getCapacity</p> 
	* <p>Description: always return the basic capacity</p> 
	* @param ftLevel
	* @return 
	* @see auto_scaling.cloud.InstanceTemplate#getCapacity(auto_scaling.core.FaultTolerantLevel) 
	*/
	@Override
	public long getCapacity(FaultTolerantLevel ftLevel) {
		return basicCapacity;
	}

}
