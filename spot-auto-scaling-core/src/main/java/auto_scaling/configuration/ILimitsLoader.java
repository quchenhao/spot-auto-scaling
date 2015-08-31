package auto_scaling.configuration;

import java.io.InputStream;

/** 
* @ClassName: ILimitsLoader 
* @Description: loader to load limits
* @author Chenhao Qu
* @date 04/06/2015 5:07:24 pm 
*  
*/
public interface ILimitsLoader {

	static final String MINIMUM_ON_DEMAND_CAPACITY = "minimum_on_demand_limit";
	static final String MAXIMUM_CHOSEN_SPOT_TYPES_LIMIT = "maximum_chosen_spot_types_limit";
	
	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param inputStream
	 * @throws Exception
	 * @throws
	 */
	public void load(InputStream inputStream) throws Exception;
}
