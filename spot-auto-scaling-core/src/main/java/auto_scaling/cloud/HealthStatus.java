package auto_scaling.cloud;

/** 
* @ClassName: HealthStatus 
* @Description: Status of instance health
* @author Chenhao Qu
* @date 01/06/2015 12:30:36 pm 
*  
*/
public class HealthStatus {
	
	public static final String SystemStatus_OK = "ok";
	public static final String SystemStatus_Impaired = "impaired";
	public static final String SystemStatus_INITIALIZING = "initializing";
	public static final String SystemStatus_INSUFFICIENT_DATA = "insufficient-data";
	public static final String SystemStatus_NOT_APPLICABLE = "not-applicable";
	
	public static final String REACHABILITY_PASSED = "passed";
	public static final String REACHABILITY_FAILED = "failed";
	public static final String REACHABILITY_INITIALIZING = "initializing";
	public static final String REACHABILITY_INSUFFICIENT_DATA = "insufficient-data";
}
