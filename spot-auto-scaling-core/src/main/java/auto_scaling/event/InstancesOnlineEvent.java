package auto_scaling.event;

import java.util.Collection;

import auto_scaling.cloud.InstanceStatus;

/** 
* @ClassName: InstancesOnlineEvent 
* @Description: the event indicates some instances are online and ready to use
* @author Chenhao Qu
* @date 05/06/2015 9:47:54 pm 
*  
*/
public class InstancesOnlineEvent extends Event{
	
	/** 
	* @Fields onlineInstances : the instances are just online
	*/ 
	protected Collection<InstanceStatus> onlineInstances;

	InstancesOnlineEvent(int critical_level, Collection<InstanceStatus> onlineInstances) {
		super(critical_level, Events.INSTANCES_ONLINE_EVENT);
		this.onlineInstances = onlineInstances;
	}

	/** 
	* @Title: getOnlineInstances 
	* @Description: get the online instances
	* @return
	*/
	public Collection<InstanceStatus> getOnlineInstances() {
		return onlineInstances;
	}
}
