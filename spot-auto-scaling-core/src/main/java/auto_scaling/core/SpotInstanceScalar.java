package auto_scaling.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/** 
* @ClassName: SpotInstanceScalar 
* @Description: main class for real cloud
* @author Chenhao Qu
* @date 05/06/2015 3:00:48 pm 
*  
*/
public abstract class SpotInstanceScalar {
	
	protected static Logger mainLog = LogManager.getLogger(SpotInstanceScalar.class);
	
	/**
	 * @Title: start 
	 * @Description: 
	 * @param args the args that are needed to start the system
	 * @throws
	 */
	public abstract void start(String[] args);
}
