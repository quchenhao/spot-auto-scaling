package auto_scaling.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
* @ClassName: SpotPriceMonitor 
* @Description: the monitor that monitors the spot market price
* @author Chenhao Qu
* @date 06/06/2015 2:22:35 pm 
*  
*/
public abstract class SpotPriceMonitor extends Monitor {

	/** 
	* @Fields spotPriceLog : the spot price log
	*/ 
	protected Logger spotPriceLog;
	
	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param monitorInterval the monitor interval
	*/
	public SpotPriceMonitor(String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
		this.spotPriceLog = LogManager.getLogger(SpotPriceMonitor.class);
	}

}
