package auto_scaling.event;

import java.util.Collection;

import auto_scaling.cloud.InstanceStatus;

/** 
* @ClassName: SpotInstancesTerminationEvent 
* @Description: the event indicates that some spot instances are terminated by the provider
* @author Chenhao Qu
* @date 05/06/2015 9:53:13 pm 
*  
*/
public class SpotInstancesTerminationEvent extends Event {

	/** 
	* @Fields terminatingInstances : the instances are terminated  by the provider
	*/ 
	protected Collection<InstanceStatus> terminatingInstances;
	
	/** 
	* <p>Description: </p> 
	* @param critical_level the critical level
	* @param terminatingInstances the instances terminated by the provider
	*/
	SpotInstancesTerminationEvent(int critical_level, Collection<InstanceStatus> terminatingInstances) {
		super(critical_level, Events.SPOT_INSTANCES_TERMINATION_EVENT);
		this.terminatingInstances = terminatingInstances;
	}
	
	/** 
	* @Title: getTerminatingInstances 
	* @Description: get the instances terminated by the provider
	* @return the instances terminated by the provider
	*/
	public Collection<InstanceStatus> getTerminatingInstances() {
		return terminatingInstances;
	}

}
