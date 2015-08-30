package auto_scaling.event;

/** 
* @ClassName: EventDataName 
* @Description: list of parameter names that events may use 
* @author Chenhao Qu
* @date 05/06/2015 9:34:01 pm 
*  
*/
public class EventDataName {
	public static final String RESOURCE_TYPE = "resource_type";
	public static final String MERTIC_NAME = "metric_name";
	public static final String SCALING_PLAN = "scaling_plan";
	public static final String INSTANCE_STATUS = "instance_status";
	public static final String ONLINE_INSTANCES = "online_instances";
	public static final String IMPAIRED_INSTANCES = "impaired_instances";
	public static final String TERMINATING_SPOT_INSTANCES = "terminating spot instances";
	public static final String TARGET_SYSTEM_STATUS = "target system status";
	public static final String CLOSED_SPOT_REQUESTS = "closed spot requests";
}
