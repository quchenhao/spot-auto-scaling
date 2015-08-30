package auto_scaling.handler;

/** 
* @ClassName: EventHandlers 
* @Description: all the event handlers
* @author Chenhao Qu
* @date 05/06/2015 10:15:12 pm 
*  
*/
public class EventHandlers {

	public static final String ABOUT_TO_SCALE_UP_EVENT_HANDLER = "about_to_scale_up_event_handler";
	public static final String INSTANCE_BILLING_PERIOD_ENDING_EVENT_HANDLER = "instance_billing_period_ending_event_handler";
	public static final String INSTANCES_IMPAIRED_EVENT_HANDLER = "instances_impaired_event_handler";
	public static final String INSTANCES_ONLINE_EVENT_HANDLER = "instances_online_event_handler";
	public static final String METRIC_UPDATE_EVENT_HANDLER = "metric_update_event_handler";
	public static final String RESOURCE_REQUIREMENT_UPDATE_EVENT_HANDLER = "resource_requirement_update_event_handler";
	public static final String SCALING_EVENT_HANDLER = "scaling_event_handler";
	public static final String SPOT_INSTANCES_TERMINATION_EVENT_HANDLER = "spot_instances_termination_event_handler";
	public static final String SPOT_PRICE_UPDATE_EVENT_HANDLER = "spot_price_update_event_handler";
	public static final String TARGET_SYSTEM_STATUS_EVENT_HANDLER = "target_system_status_event_handler";
	public static final String SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT_HANDLER = "spot_requests_closed_before_fullfillment_event_handler";
	
}
