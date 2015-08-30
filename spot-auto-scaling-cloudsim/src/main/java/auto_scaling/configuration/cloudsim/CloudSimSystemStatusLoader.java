package auto_scaling.configuration.cloudsim;

import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.configuration.ISystemStatusLoader;
import auto_scaling.core.SystemStatus;

/** 
* @ClassName: CloudSimSystemStatusLoader 
* @Description: loader to load initial system status for cloudSim
* @author Chenhao Qu
* @date 05/06/2015 2:30:59 pm 
*  
*/
public class CloudSimSystemStatusLoader implements ISystemStatusLoader {

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param cloudConfiguration
	* @throws Exception 
	* @see auto_scaling.configuration.ISystemStatusLoader#load(auto_scaling.configuration.ICloudConfiguration) 
	*/
	@Override
	public void load(ICloudConfiguration cloudConfiguration) throws Exception {
		SystemStatus.initialize(SystemStatus.class);
	}

}
