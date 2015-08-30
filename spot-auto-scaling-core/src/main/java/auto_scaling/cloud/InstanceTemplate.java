package auto_scaling.cloud;

import auto_scaling.core.FaultTolerantLevel;

/** 
* @ClassName: InstanceTemplate 
* @Description: the data structure store the information about instance type
* @author Chenhao Qu
* @date 03/06/2015 3:03:42 pm 
*  
*/
public abstract class InstanceTemplate {
	
	/** 
	* @Fields name : the instance type name
	*/ 
	protected final String name;
	/** 
	* @Fields vcpuNum : the number of vcpus
	*/ 
	protected final int vcpuNum;
	/** 
	* @Fields ecuNum : the processing capacity in amazon ecu
	*/ 
	protected final double ecuNum;
	/** 
	* @Fields memoryNum : the amount of memory in GB
	*/ 
	protected final double memoryNum;
	/** 
	* @Fields os : the operating system type
	*/ 
	protected final String os;
	/** 
	* @Fields onDemandPrice : the on demand price of the instance
	*/ 
	protected final double onDemandPrice;
	/** 
	* @Fields isSupportHvm : whether the vm type supports full virtualization
	*/ 
	protected final boolean isSupportHvm;
	/** 
	* @Fields isSupportParavirtual : whether the vm type supports para-virtualization
	*/ 
	protected final boolean isSupportParavirtual;
	/** 
	* @Fields basicCapacity : the basic capacity of the vm type
	*/ 
	protected long basicCapacity;
	/** 
	* @Fields maximumCapacity : the maximum capacity of the vm type
	*/ 
	protected long maximumCapacity;
	
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
	public InstanceTemplate(String name, int vcpuNum, double ecuNum, double memoryNum, String os, double onDemandPrice, boolean isSupportHvm, boolean isSupportParavirtual) {
		this.name = name;
		this.vcpuNum = vcpuNum;
		this.ecuNum = ecuNum;
		this.memoryNum = memoryNum;
		this.os = os;
		this.onDemandPrice = onDemandPrice;
		this.isSupportHvm = isSupportHvm;
		this.isSupportParavirtual = isSupportParavirtual;
	}
	
	/**
	 * @Title: getOnDemandPrice 
	 * @Description: get the on demand price of the instance
	 * @return the on demand price of the instance
	 * @throws
	 */
	public double getOnDemandPrice() {
		return onDemandPrice;
	}
	
	/**
	 * @Title: getName 
	 * @Description: get the instance type name
	 * @return the instance type name
	 * @throws
	 */
	public String getName() {
		return name;
	}
	/**
	 * @Title: getVcpuNum 
	 * @Description: get the number of vcpus
	 * @return the number of vcpus
	 * @throws
	 */
	public int getVcpuNum() {
		return vcpuNum;
	}
	/**
	 * @Title: getEcuNum 
	 * @Description: get the processing capacity in amazon ecu
	 * @return the processing capacity in amazon ecu
	 * @throws
	 */
	public double getEcuNum() {
		return ecuNum;
	}
	/**
	 * @Title: getMemoryNum 
	 * @Description: get the amount of memory in GB
	 * @return the amount of memory in GB
	 * @throws
	 */
	public double getMemoryNum() {
		return memoryNum;
	}

	/**
	 * @Title: getOs 
	 * @Description: get the operating system type
	 * @return the operating system type
	 * @throws
	 */
	public String getOs() {
		return os;
	}
	
	/**
	 * @Title: isSupportHvm 
	 * @Description: get whether the vm type supports full virtualization
	 * @return whether the vm type supports full virtualization
	 * @throws
	 */
	public boolean isSupportHvm() {
		return isSupportHvm;
	}
	
	/**
	 * @Title: isSupportParavirtual 
	 * @Description: get whether the vm type supports para-virtualization
	 * @return whether the vm type supports para-virtualization
	 * @throws
	 */
	public boolean isSupportParavirtual() {
		return isSupportParavirtual;
	}

	/**
	 * @Title: getResourceAmount 
	 * @Description: get the amount of resources given the resource type
	 * @param resource the resource type
	 * @return the amount of resource; null if the resource type is unknown
	 * @throws
	 */
	public Number getResourceAmount(String resource) {
		if (resource == null) {
			throw new IllegalArgumentException("metricName is null");
		}
		
		if (resource.equals(ResourceType.CPU.toString())) {
			return ecuNum;
		}
		
		if (resource.equals(ResourceType.MEMORY.toString())) {
			return memoryNum;
		}
		
		return null;
	}
	
	/* (non-Javadoc) 
	* <p>Title: hashCode</p> 
	* <p>Description: the hash of the type name and the os</p> 
	* @return 
	* @see java.lang.Object#hashCode() 
	*/
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	/* (non-Javadoc) 
	* <p>Title: toString</p> 
	* <p>Description: the type name and the os</p> 
	* @return 
	* @see java.lang.Object#toString() 
	*/
	@Override
	public String toString() {
		return name + " " + os;
	}
	
	/* (non-Javadoc) 
	* <p>Title: equals</p> 
	* <p>Description: equal when all constant fields are the same</p> 
	* @param obj
	* @return 
	* @see java.lang.Object#equals(java.lang.Object) 
	*/
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof InstanceTemplate) {
			
			InstanceTemplate template = (InstanceTemplate)obj;
			if (name.equals(template.getName()) && os.equals(template.getName()) && 
					vcpuNum == template.getVcpuNum() && ecuNum == template.getEcuNum() && memoryNum == template.getMemoryNum()) {
				return true;
			}
		}
		return false;
	}
	
	public long getMaximunCapacity() {
		return maximumCapacity;
	}
	
	public void setMaximumCapacity(long maximumCapacity) {
		if (maximumCapacity < basicCapacity) {
			throw new IllegalArgumentException("capacity should be greater equal than capacity");
		}
		this.maximumCapacity = maximumCapacity;
	}
	
	public void setBasicCapacity(long capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity should be greater than 0");
		}
		this.basicCapacity = capacity;
	}
	
	/**
	 * @Title: getBasicCapacity 
	 * @Description: get the basic capacity
	 * @return the basic capacity
	 * @throws
	 */
	public long getBasicCapacity() {
		return basicCapacity;
	}
	
	/**
	 * @Title: getCapacity 
	 * @Description: get the capacity based on the fault tolerant level
	 * @param ftLevel the fault tolerant level
	 * @return the capacity of the instance
	 * @throws
	 */
	public abstract long getCapacity(FaultTolerantLevel ftLevel);
}
