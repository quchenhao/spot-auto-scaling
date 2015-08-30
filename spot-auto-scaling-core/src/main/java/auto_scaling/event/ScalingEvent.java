package auto_scaling.event;

import auto_scaling.scaling_strategy.ScalingPlan;


/** 
* @ClassName: ScalingEvent 
* @Description: the event indicates scaling operations
* @author Chenhao Qu
* @date 05/06/2015 9:51:17 pm 
*  
*/
public class ScalingEvent extends Event {

	/** 
	* @Fields scalingPlan : the planned scaling operations
	*/ 
	protected ScalingPlan scalingPlan;
	
	/** 
	* <p>Description: </p> 
	* @param critical_level the critical level
	* @param scalingPlan the planned scaling operations
	*/
	ScalingEvent(int critical_level, ScalingPlan scalingPlan) {
		super(critical_level, Events.SCALING_EVENT);
		this.scalingPlan = scalingPlan;
	}
	
	/** 
	* @Title: getScalingPlan 
	* @Description: get the planned scaling operations
	* @return
	*/
	public ScalingPlan getScalingPlan() {
		return scalingPlan;
	}

}
