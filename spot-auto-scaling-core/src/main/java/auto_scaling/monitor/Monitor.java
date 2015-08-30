package auto_scaling.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import auto_scaling.util.LogFormatter;

/** 
* @ClassName: Monitor 
* @Description: the base class for monitor
* @author Chenhao Qu
* @date 06/06/2015 2:15:34 pm 
*  
*/
public abstract class Monitor implements Runnable{
	
	/** 
	* @Fields monitorName : the monitor name
	*/ 
	protected String monitorName;
	/** 
	* @Fields monitorInterval : the monitor interval
	*/ 
	protected int monitorInterval;
	/** 
	* @Fields monitorLog : the monitor log
	*/ 
	protected Logger monitorLog;
	/** 
	* @Fields logFormatter : the log formatter
	*/ 
	protected LogFormatter logFormatter;
	
	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param monitorInterval the monitor interval
	*/
	public Monitor(String monitorName, int monitorInterval) {
		this.monitorLog = LogManager.getLogger(Monitor.class);
		this.monitorName = monitorName;
		this.monitorInterval = monitorInterval;
		this.logFormatter = LogFormatter.getLogFormatter();
	}

	/* (non-Javadoc) 
	* <p>Title: run</p> 
	* <p>Description: </p>  
	* @see java.lang.Runnable#run() 
	*/
	public void run() {
		while(true) {
			try {
				//monitor and then sleep the monitor interval
				doMonitoring();
				Thread.sleep(monitorInterval * 1000);
			} catch (Exception e) {
				monitorLog.error(logFormatter.getMessage("In " + monitorName + " Thread"));
				monitorLog.error(logFormatter.getExceptionString(e));
			}
		}
	}
	
	/**
	 * @Title: doMonitoring 
	 * @Description: do the actual monitoring
	 * @throws
	 */
	public abstract void doMonitoring() throws Exception;
	
	/**
	 * @Title: refresh 
	 * @Description: manually refresh
	 * @throws
	 */
	public synchronized void refresh() {
		try {
			doMonitoring();
		} catch (Exception e) {
			monitorLog.error(logFormatter.getMessage("In " + monitorName + " Thread"));
			monitorLog.error(logFormatter.getExceptionString(e));
		}
	}
	
	/**
	 * @Title: getMonitorInterval 
	 * @Description: get the monitor interval
	 * @return the monitor interval
	 * @throws
	 */
	public int getMonitorInterval() {
		return monitorInterval;
	}
	
	/**
	 * @Title: getName 
	 * @Description: get the monitor name
	 * @return the monitor name
	 * @throws
	 */
	public String getName() {
		return monitorName;
	}
}
