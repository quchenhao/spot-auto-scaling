package auto_scaling.monitor;

/** 
* @ClassName: VMStatusMonitor 
* @Description: the monitor that monitors the vm status
* @author Chenhao Qu
* @date 06/06/2015 2:26:32 pm 
*  
*/
public abstract class VMStatusMonitor extends Monitor{

	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param monitorInterval the monitor interval
	*/
	public VMStatusMonitor(String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
	}

}
