package auto_scaling.event;

import auto_scaling.scaling_strategy.TargetSystemStatus;

/** 
* @ClassName: TargetSystemStatusEvent 
* @Description: the event indicates that a target system status is generated
* @author Chenhao Qu
* @date 05/06/2015 9:59:53 pm 
*  
*/
public class TargetSystemStatusEvent extends Event{
	
	/** 
	* @Fields targetSystemStatus : the generated target system status
	*/ 
	protected TargetSystemStatus targetSystemStatus;

	/** 
	* <p>Description: </p> 
	* @param critical_level the critical level
	* @param targetSystemStatus the generated target system status
	*/
	TargetSystemStatusEvent(int critical_level, TargetSystemStatus targetSystemStatus) {
		super(critical_level, Events.TARGET_SYSTEM_STATUS_EVENT);
		this.targetSystemStatus = targetSystemStatus;
	}

	/** 
	* @Title: getTargetSystemStatus 
	* @Description: get the generated target system status
	* @return the generated target system status
	*/
	public TargetSystemStatus getTargetSystemStatus() {
		return targetSystemStatus;
	}
}
