package auto_scaling.configuration.cloudsim;

import java.io.InputStream;
import java.util.Properties;

import auto_scaling.util.cloudsim.CloudletFactory;

/** 
* @ClassName: DefaultCloudletFactoryLoader 
* @Description: loader to load cloudlet factory
* @author Chenhao Qu
* @date 05/06/2015 2:32:51 pm 
*  
*/
public class DefaultCloudletFactoryLoader implements ICloudletFactoryLoader {

	private static final String SEED = "seed";
	private static final String MEAN = "mean";
	private static final String SD = "sd";
	
	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @throws Exception 
	* @see auto_scaling.configuration.cloudsim.ICloudletFactoryLoader#load(java.io.InputStream) 
	*/
	@Override
	public void load(InputStream inputStream) throws Exception {
		Properties properties = new Properties();
		properties.load(inputStream);
		
		byte[] seed = properties.getProperty(SEED).getBytes();
		double mean = Double.parseDouble(properties.getProperty(MEAN));
		double sd = Double.parseDouble(properties.getProperty(SD));
		
		CloudletFactory.initialize(seed, mean, sd);
	}

}
