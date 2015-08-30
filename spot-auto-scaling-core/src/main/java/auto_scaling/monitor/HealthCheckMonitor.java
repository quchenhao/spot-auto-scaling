package auto_scaling.monitor;

/** 
* @ClassName: HealthCheckMonitor 
* @Description: the monitor that monitors the health of instances
* @author Chenhao Qu
* @date 06/06/2015 2:09:35 pm 
*  
*/
public abstract class HealthCheckMonitor extends Monitor{

	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param monitorInterval the monitor interval
	*/
	public HealthCheckMonitor(String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
	}

}
