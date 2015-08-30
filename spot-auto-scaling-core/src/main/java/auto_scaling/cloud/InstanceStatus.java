package auto_scaling.cloud;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** 
* @ClassName: InstanceStatus 
* @Description: the data structure to store the status of each instance
* @author Chenhao Qu
* @date 01/06/2015 12:31:19 pm 
*  
*/
public abstract class InstanceStatus {

	
	/** 
	* @Fields id : the id of the instance
	*/ 
	protected final String id;
	/**
	* @Fields publicUrl : the url of the instance to access it from the Internet
	*/ 
	protected String publicUrl;
	/** 
	* @Fields privateUrl : the url of the instance to access it from within the cloud
	*/ 
	protected final String privateUrl;
	/** 
	* @Fields type : the instance type
	*/ 
	protected final InstanceTemplate type;
	/** 
	* @Fields runningStatus : the running status of the instance
	*/ 
	protected String runningStatus;
	/** 
	* @Fields attached : whether the instance is attached to the load balancer
	*/ 
	protected boolean attached;
	/** 
	* @Fields resourceConsumptionValues : the recently observed resources' utilization
	*/ 
	protected Map<String, Number> resourceConsumptionValues;
	/** 
	* @Fields launchTime : the launch time of the instance
	*/ 
	protected final Date launchTime;
	
	/** 
	* <p>Description: only construct the instance with complete values</p> 
	* @param id the id of the instance
	* @param publicUrl the url of the instance to access it from the Internet
	* @param privateUrl the url of the instance to access it from within the cloud
	* @param launchTime the launch time of the instance
	* @param type the instance type
	*/
	public InstanceStatus (String id, String publicUrl, String privateUrl, Date launchTime, InstanceTemplate type) {
		this.id = id;
		this.publicUrl = publicUrl;
		this.privateUrl = privateUrl;
		this.launchTime = launchTime;
		this.type = type;
		this.resourceConsumptionValues = new HashMap<String, Number>();
		setAttached(false);
		Collection<ResourceType> resourceTypes = ResourceType.getAllResourceTypes();
		
		//initially all resources' utilization should be 0
		for (ResourceType resourceType : resourceTypes)  {
			resourceConsumptionValues.put(resourceType.getName(), 0.0);
		}
	}

	/**
	 * @Title: getRunningStatus 
	 * @Description: get the running status of the instance
	 * @return the running status
	 * @throws
	 */
	public synchronized String getRunningStatus() {
		return runningStatus;
	}

	/**
	 * @Title: setRunningStatus 
	 * @Description: set the running status of the instance
	 * @param runningStatus the new running status
	 * @throws
	 */
	public synchronized void setRunningStatus(String runningStatus) {
		this.runningStatus = runningStatus;
	}

	/**
	 * @Title: isAttached 
	 * @Description: whether the instance is attached to the load balancer
	 * @return whether the system is attached
	 * @throws
	 */
	public synchronized boolean isAttached() {
		return attached;
	}

	/**
	 * @Title: setAttached 
	 * @Description: set the attachment of the instance 
	 * @param attached the new attachment
	 * @throws
	 */
	public synchronized void setAttached(boolean attached) {
		this.attached = attached;
	}

	/**
	 * @Title: getPublicUrl 
	 * @Description: get the url of the instance to access it from the Internet
	 * @return the url of the instance to access it from the Internet
	 * @throws
	 */
	public String getPublicUrl() {
		return publicUrl;
	}
	
	/**
	 * @Title: getPrivateUrl 
	 * @Description: get the url of the instance to access it from within the cloud
	 * @return the url of the instance to access it from within the cloud
	 * @throws
	 */
	public String getPrivateUrl() {
		return privateUrl;
	}

	/**
	 * @Title: getType 
	 * @Description: get the instance type
	 * @return the instance type
	 * @throws
	 */
	public InstanceTemplate getType() {
		return type;
	}

	/**
	 * @Title: getId 
	 * @Description: get the id of the instance
	 * @return the id of the instance
	 * @throws
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @Title: getLaunchTime 
	 * @Description: get the launch time of the instance
	 * @return the launch time of the instance
	 * @throws
	 */
	public Date getLaunchTime() {
		return (Date) launchTime.clone();
	}
	
	/* (non-Javadoc) 
	* <p>Title: hashCode</p> 
	* <p>Description: the hashCode is calculated as the hash of the instance id</p> 
	* @return the hashCode of the instance
	* @see java.lang.Object#hashCode() 
	*/
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	/**
	 * @Title: getResourceConsumptionValue 
	 * @Description: get the current resource consumption of the specified resource type
	 * @param resourceType the resource type
	 * @return the amount of resource consumption of the resource type. 
	 * null if the resource type is unknown
	 * @throws
	 */
	public synchronized Number getResourceConsumptionValue(String resourceType) {
		if (resourceConsumptionValues.containsKey(resourceType)) {
			return resourceConsumptionValues.get(resourceType);
		}
		return null;
	}
	
	/**
	 * @Title: getResourceConsumptionValues 
	 * @Description: get the amount of resource consumptions of all resource types
	 * @return the amount of resource consumptions of all resource types 
	 * @throws
	 */
	public synchronized Map<String, Number> getResourceConsumptionValues() {
		return Collections.unmodifiableMap(resourceConsumptionValues);
	}
	
	/**
	 * @Title: setResourceConsumptionValue 
	 * @Description: set the resource consumption of the specified resource type
	 * @param resourceType the resource type
	 * @param value the amount of resource consumption
	 * @throws
	 */
	public synchronized void setResourceConsumptionValue(String resourceType, Number value) {
		if (resourceConsumptionValues.containsKey(resourceType)) {
			resourceConsumptionValues.put(resourceType, value);
		}
		
	}
	
	/* (non-Javadoc) 
	* <p>Title: toString</p> 
	* <p>Description: the information of the instance</p> 
	* @return the information of the instance
	* @see java.lang.Object#toString() 
	*/
	@Override
	public synchronized String toString() {
		String dump = "id: " + id + " url: " + publicUrl + " type: " + type.getName() + " " + runningStatus + " attached: " + attached + " launch time: "
				+ launchTime;
		for(Entry<String, Number> entry: resourceConsumptionValues.entrySet()) {
			dump += " " + entry.getKey() + ": " + entry.getValue();
		}
		
		return dump;
	}

	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}
	
}
