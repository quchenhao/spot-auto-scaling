package auto_scaling.event;

import java.util.Collection;

import auto_scaling.cloud.InstanceStatus;

/** 
* @ClassName: InstancesImpairedEvent 
* @Description: the event indicates that some instances are impaired
* @author Chenhao Qu
* @date 05/06/2015 9:46:26 pm 
*  
*/
public class InstancesImpairedEvent extends Event {

	/** 
	* @Fields impairedInstances : the impaired instances
	*/ 
	protected Collection<InstanceStatus> impairedInstances;
	
	/** 
	* <p>Description: </p> 
	* @param critical_level the critical level
	* @param impairedInstances the impaired instances 
	*/
	InstancesImpairedEvent(int critical_level, Collection<InstanceStatus> impairedInstances) {
		super(critical_level, Events.INSTANCES_IMPAIRED_EVENT);
		this.impairedInstances = impairedInstances;
	}
	
	/** 
	* @Title: getImpairedInstances 
	* @Description: get the impaired instances
	* @return
	*/
	public Collection<InstanceStatus> getImpairedInstances() {
		return impairedInstances;
	}

}
