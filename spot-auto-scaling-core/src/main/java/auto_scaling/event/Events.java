package auto_scaling.event;

/** 
* @ClassName: Events 
* @Description: all the events names
* @author Chenhao Qu
* @date 05/06/2015 9:41:51 pm 
*  
*/
public class Events {
	public static final String RESOURCE_REQUIREMENT_UPDATE_EVENT = "resource_requirement_update_event";
	public static final String ABOUT_TO_SCALE_UP_EVENT = "about_to_scale_up_event";
	public static final String TARGET_SYSTEM_STATUS_EVENT = "target_system_status_event";
	public static final String SPOT_PRICE_UPDATE_EVENT = "spot_price_update_event";
	public static final String SCALING_EVENT = "scaling_event";
	public static final String INSTANCE_BILLING_PERIOD_ENDING_EVENT = "instance_billing_period_ending_event";
	public static final String INSTANCES_ONLINE_EVENT = "instances_online_event";
	public static final String INSTANCES_IMPAIRED_EVENT = "instances_impaired_event";
	public static final String SPOT_INSTANCES_TERMINATION_EVENT = "spot_instances_termination_event";
	public static final String SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT = "spot_requests_closed_before_fullfillment_event";
}
