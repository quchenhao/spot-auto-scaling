package auto_scaling.event;

import auto_scaling.cloud.InstanceStatus;

/** 
* @ClassName: InstanceBillingPeriodEndingEvent 
* @Description: the event indicates one instance is at the end of its billing period
* @author Chenhao Qu
* @date 05/06/2015 9:42:22 pm 
*  
*/
public class InstanceBillingPeriodEndingEvent extends Event{
	
	/** 
	* @Fields instanceStatus : the instance that is at the end of the billing period
	*/ 
	protected InstanceStatus instanceStatus;

	/** 
	* <p>Description: </p> 
	* @param critical_level the critical level
	* @param instanceStatus the instance that is at the end of the billing period
	*/
	InstanceBillingPeriodEndingEvent(int critical_level, InstanceStatus instanceStatus) {
		super(critical_level, Events.INSTANCE_BILLING_PERIOD_ENDING_EVENT);
		this.instanceStatus = instanceStatus;
	}
	
	/** 
	* @Title: getBillingPeirodEndingInstance 
	* @Description: get the instance that is at the end of the billing period
	* @return the instance that is at the end of the billing period
	*/
	public InstanceStatus getBillingPeirodEndingInstance() {
		return instanceStatus;
	}

}
