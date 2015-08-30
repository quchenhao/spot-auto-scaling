package auto_scaling.configuration;

import java.io.InputStream;
import java.util.Properties;

/** 
* @ClassName: DefaultLimitsLoader 
* @Description: default loader to load limits
* @author Chenhao Qu
* @date 04/06/2015 1:58:45 pm 
*  
*/
public class DefaultLimitsLoader implements ILimitsLoader {

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @return
	* @throws Exception 
	* @see auto_scaling.configuration.ILimitsLoader#load(java.io.InputStream) 
	*/
	@Override
	public Limits load(InputStream inputStream) throws Exception {
		Limits limits = new Limits();
		Properties properties = new Properties();
		properties.load(inputStream);
		
		String minimumOnDemandCapacity = properties.getProperty(MINIMUM_ON_DEMAND_CAPACITY);
		limits.setOnDemandCapacityThreshold(Double.parseDouble(minimumOnDemandCapacity));
		
		String maximumChosenSpotTypes = properties.getProperty(MAXIMUM_CHOSEN_SPOT_TYPES_LIMIT);
		limits.setMaxChosenSpotTypesNum(Integer.parseInt(maximumChosenSpotTypes));
		
		return limits;
	}

}
