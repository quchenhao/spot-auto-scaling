package auto_scaling.monitor;

/** 
* @ClassName: MonitorThread 
* @Description: the thread to hold the monitors
* @author Chenhao Qu
* @date 06/06/2015 2:20:46 pm 
*  
*/
public class MonitorThread extends Thread{

	/** 
	* @Fields monitor : the monitor
	*/ 
	Monitor monitor;
	/** 
	* <p>Description: </p> 
	* @param monitor the monitor
	*/
	public MonitorThread(Monitor monitor) {
		super(monitor);
		if (monitor == null) {
			throw new NullPointerException("Monitor cannot be null");
		}
		this.monitor = monitor;
	}
	
	/**
	 * @Title: getMonitor 
	 * @Description: get the monitor
	 * @return the monitor
	 * @throws
	 */
	public Monitor getMonitor(){
		return monitor;
	}
}
