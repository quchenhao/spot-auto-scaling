package auto_scaling.capacity;

import java.util.Map;
import java.util.Map.Entry;

import auto_scaling.cloud.ApplicationResourceUsageProfile;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.ResourceType;
import auto_scaling.core.InstanceTemplateManager;

/** 
* @ClassName: ScalingThresholdCapacityCalculator 
* @Description: Decide capacity of each VM type according to the threshold of resources
* @author Chenhao Qu
* @date 01/06/2015 11:54:26 am 
*  
*/
public class ScalingThresholdCapacityCalculator implements ICapacityCalculator{

	/* (non-Javadoc) 
	* <p>Title: getEstimatedBasicCapacity</p> 
	* <p>Description: calculate estimated basic capacity based on basic capacity threshold for each resources</p> 
	* @param instanceTemplate
	* @return the basic capacity of the instance type
	* @see auto_scaling.capacity.ICapacityCalculator#getEstimatedCapacity(auto_scaling.cloud.InstanceTemplate) 
	*/
	@Override
	public long getEstimatedBasicCapacity(InstanceTemplate instanceTemplate) {
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager
				.getInstanceTemplateManager();
		ApplicationResourceUsageProfile applicationResourceUsageProfile = instanceTemplateManager
				.getApplicationResourceUsageProfile(instanceTemplate);
		Map<String, Number> resourceProfile = applicationResourceUsageProfile
				.getApplicationResourceUsageProfile();

		long capacity = Long.MAX_VALUE;
		for (Entry<String, Number> entry : resourceProfile.entrySet()) {
			String resourceTypeString = entry.getKey();
			ResourceType resourceType = ResourceType.getByName(resourceTypeString);
			long temp_capacity = (long)(instanceTemplate.getResourceAmount(resourceTypeString).doubleValue() * resourceType.getScalingThreshold() /
					entry.getValue().doubleValue());
			
			if (temp_capacity < capacity) {
				capacity = temp_capacity;
			}
		}
		return capacity;
	}

	/* (non-Javadoc) 
	* <p>Title: getEstimatedNumOfRequestsByUtilization</p> 
	* <p>Description: estimate the number of requests rate based on recently observed resources' utilization</p> 
	* @param instanceTemplate
	* @param resourceUtilizations
	* @return estimated number of requests rate in the instance
	* @see auto_scaling.capacity.ICapacityCalculator#getEstimatedNumOfRequestsByUtilization(auto_scaling.cloud.InstanceTemplate, java.util.Map) 
	*/
	@Override
	public long getEstimatedNumOfRequestsByUtilization(
			InstanceTemplate instanceTemplate,
			Map<String, Number> resourceUtilizations) {
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager
				.getInstanceTemplateManager();
		ApplicationResourceUsageProfile applicationResourceUsageProfile = instanceTemplateManager
				.getApplicationResourceUsageProfile(instanceTemplate);
		Map<String, Number> resourceProfile = applicationResourceUsageProfile
				.getApplicationResourceUsageProfile();

		long request = 0;
		for (Entry<String, Number> entry : resourceProfile.entrySet()) {
			String resourceTypeString = entry.getKey();
			double utilization = resourceUtilizations.get(resourceTypeString).doubleValue();
			double unitResourceConsumption = entry.getValue().doubleValue();
			long temp_request = (long)(instanceTemplate.getResourceAmount(resourceTypeString).doubleValue() * utilization / unitResourceConsumption);
			if (temp_request > request) {
				request = temp_request;
			}
		}
		
		return request;
	}
	
	/* (non-Javadoc) 
	* <p>Title: getEstimatedMaximumCapacity</p> 
	* <p>Description: calculate maximum capacity based on maximum capacity threshold for each resource </p> 
	* @param instanceTemplate
	* @return the maximum capacity for the instance type
	* @see auto_scaling.capacity.ICapacityCalculator#getEstimatedMaximumCapacity(auto_scaling.cloud.InstanceTemplate) 
	*/
	@Override
	public long getEstimatedMaximumCapacity(InstanceTemplate instanceTemplate) {
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager
				.getInstanceTemplateManager();
		ApplicationResourceUsageProfile applicationResourceUsageProfile = instanceTemplateManager
				.getApplicationResourceUsageProfile(instanceTemplate);
		Map<String, Number> resourceProfile = applicationResourceUsageProfile
				.getApplicationResourceUsageProfile();

		long capacity = Long.MAX_VALUE;
		for (Entry<String, Number> entry : resourceProfile.entrySet()) {
			String resourceTypeString = entry.getKey();
			ResourceType resourceType = ResourceType.getByName(resourceTypeString);
			long temp_capacity = (long)(instanceTemplate.getResourceAmount(resourceTypeString).doubleValue() *  resourceType.getMaxThreshold() /
					entry.getValue().doubleValue());
			
			if (temp_capacity < capacity) {
				capacity = temp_capacity;
			}
		}
		return capacity;
	}

}
