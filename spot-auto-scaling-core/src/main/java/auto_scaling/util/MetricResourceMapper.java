package auto_scaling.util;

import auto_scaling.cloud.ResourceType;
import auto_scaling.cloud.UnSupportedResourceException;
import auto_scaling.monitor.Metrics;

/** 
* @ClassName: MetricResourceMapper 
* @Description: the map from metric to resource type
* @author Chenhao Qu
* @date 07/06/2015 4:48:02 pm 
*  
*/
public class MetricResourceMapper {

	/**
	 * @Title: getResourceType 
	 * @Description: get the corresponding resource type for the metric
	 * @param resourceName the resource name
	 * @return the corresponding resource type for the metric
	 * @throws UnSupportedResourceException
	 * @throws
	 */
	public static ResourceType getResourceType(String resourceName) throws UnSupportedResourceException{
		if (resourceName == null) {
			throw new IllegalArgumentException("metricName is null");
		}
		
		if (resourceName.equals(Metrics.CPU_UTILIZATION)) {
			return ResourceType.CPU;
		}
		
		if (resourceName.equals(Metrics.MEMORY_UTILIZATION)) {
			return ResourceType.MEMORY;
		}
		
		throw new UnSupportedResourceException(resourceName);
	}
	
}
