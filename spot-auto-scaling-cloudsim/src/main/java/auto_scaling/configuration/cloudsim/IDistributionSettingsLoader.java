package auto_scaling.configuration.cloudsim;

import java.io.InputStream;

/** 
* @ClassName: IDistributionSettingsLoader 
* @Description: loader to load the distribution settings
* @author Chenhao Qu
* @date 05/06/2015 2:41:26 pm 
*  
*/
public interface IDistributionSettingsLoader {
	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param inputStream the input stream
	 * @throws Exception
	 * @throws
	 */
	public void load(InputStream inputStream) throws Exception;
}
