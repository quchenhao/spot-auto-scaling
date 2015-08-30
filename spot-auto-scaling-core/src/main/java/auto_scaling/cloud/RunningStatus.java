package auto_scaling.cloud;

/** 
* @ClassName: RunningStatus 
* @Description: All the defined running status
* @author Chenhao Qu
* @date 04/06/2015 12:13:53 pm 
*  
*/
public final class RunningStatus {
	/** 
	* @Fields ASKED : instances pending fullfillment
	*/ 
	public static final String ASKED = "asked";
	/** 
	* @Fields IMPAIRED : instances impaired
	*/ 
	public static final String IMPAIRED = "imparied";
	/** 
	* @Fields PENDING : instances confirmed and waiting to boot up
	*/ 
	public static final String PENDING = "pending";
	/** 
	* @Fields RUNNING : instances running
	*/ 
	public static final String RUNNING = "running";
	/** 
	* @Fields SHUTTING_DOWN : instances shutting down
	*/ 
	public static final String SHUTTING_DOWN = "shutting-down";
	/** 
	* @Fields TERMINATED : instances already shut down
	*/ 
	public static final String TERMINATED = "terminated";
	/** 
	* @Fields STOPPING : instances stopping
	*/ 
	public static final String STOPPING = "stopping";
	/** 
	* @Fields STOPPED : instances alreay stopped
	*/ 
	public static final String STOPPED = "stopped";
}
