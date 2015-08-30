package auto_scaling.monitor;

/** 
* @ClassName: SpotRequestsMonitor 
* @Description: the monitor that monitors spot requests
* @author Chenhao Qu
* @date 06/06/2015 2:23:35 pm 
*  
*/
public abstract class SpotRequestsMonitor extends Monitor{
	
	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param monitorInterval the monitor interval
	*/
	public SpotRequestsMonitor(String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
	}

}
