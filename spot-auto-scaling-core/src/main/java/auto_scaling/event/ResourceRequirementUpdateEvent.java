package auto_scaling.event;

import auto_scaling.cloud.ResourceType;

/** 
* @ClassName: ResourceRequirementUpdateEvent 
* @Description: the event indicates that the resource requirement is updated
* @author Chenhao Qu
* @date 05/06/2015 9:49:13 pm 
*  
*/
public class ResourceRequirementUpdateEvent extends Event {

	/** 
	* @Fields resourceType : the resource type whose measurement is updated
	*/ 
	protected ResourceType resourceType;
	ResourceRequirementUpdateEvent(int critical_level, ResourceType resourceType) {
		super(critical_level, Events.RESOURCE_REQUIREMENT_UPDATE_EVENT);
		this.resourceType = resourceType;
	}

	/** 
	* @Title: getResourceType 
	* @Description: get the resource type whose measurement is updated
	* @return the resource type whose measurement is updated
	*/
	public ResourceType getResourceType() {
		return resourceType;
	}
}
