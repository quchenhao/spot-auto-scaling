package auto_scaling.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** 
* @ClassName: ISystemStatusLoader 
* @Description: loader to load initial system status
* @author Chenhao Qu
* @date 05/06/2015 1:55:53 pm 
*  
*/
public interface ISystemStatusLoader {
	static Logger systemStatusLoaderLogger = LogManager.getLogger(ISystemStatusLoader.class); 
	/**
	 * @Title: load 
	 * @Description: load using cloud configuration
	 * @param cloudConfiguration
	 * @throws Exception
	 * @throws
	 */
	public void load(ICloudConfiguration cloudConfiguration) throws Exception;
}
