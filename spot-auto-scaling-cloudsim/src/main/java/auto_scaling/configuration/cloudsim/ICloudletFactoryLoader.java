package auto_scaling.configuration.cloudsim;

import java.io.InputStream;

/** 
* @ClassName: ICloudletFactoryLoader 
* @Description: loader to load cloudlet factory
* @author Chenhao Qu
* @date 05/06/2015 2:40:46 pm 
*  
*/
public interface ICloudletFactoryLoader {

	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param inputStream the input stream
	 * @throws Exception
	 * @throws
	 */
	public void load(InputStream inputStream) throws Exception;
}
