package auto_scaling.configuration;

import java.io.InputStream;
import java.util.Map;

/** 
* @ClassName: IEventCriticalLevelLoader 
* @Description: loader to load critical level
* @author Chenhao Qu
* @date 04/06/2015 5:03:17 pm 
*  
*/
public interface IEventCriticalLevelLoader {

	static final String NORMAL = "normal";
	static final String PRIORITIZED = "prioritized";
	static final String URGENT = "urgent";
	static final String CRITICAL = "critical";
	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param inputStream the input stream
	 * @return the critical level
	 * @throws Exception
	 * @throws
	 */
	public Map<String, Integer> load(InputStream inputStream) throws Exception;
}
