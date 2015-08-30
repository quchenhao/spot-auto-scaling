package auto_scaling.cloud;

import java.util.Collections;
import java.util.Map;

/** 
* @ClassName: ApplicationResourceUsageProfile 
* @Description: data structure to store the average resources' usage of each request
* @author Chenhao Qu
* @date 01/06/2015 12:23:27 pm 
*  
*/
public class ApplicationResourceUsageProfile {

	/** 
	* @Fields resourceProfile : the resources' usage of each request
	*/ 
	protected Map<String, Number> resourceProfile;
	
	/** 
	* <p>Description: consturct with resources' usage</p> 
	* @param resourceProfile the resources' usage of each request
	*/
	public ApplicationResourceUsageProfile(Map<String, Number> resourceProfile) {
		this.resourceProfile = resourceProfile;
	}
	
	/**
	 * @Title: getApplicationResourceUsageProfile 
	 * @Description: get the resources' usage of each request
	 * @return the resources' usage of each request
	 * @throws
	 */
	public Map<String, Number> getApplicationResourceUsageProfile() {
		return Collections.unmodifiableMap(resourceProfile);
	}
	
}
