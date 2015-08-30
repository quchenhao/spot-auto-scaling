package auto_scaling.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import auto_scaling.event.CriticalLevel;
import auto_scaling.event.Events;
import auto_scaling.event.UnrecoganizedCriticalLevel;

/** 
* @ClassName: DefaultEventCriticalLevelLoader 
* @Description: default loader to load event critical level
* @author Chenhao Qu
* @date 04/06/2015 1:57:48 pm 
*  
*/
public class DefaultEventCriticalLevelLoader implements IEventCriticalLevelLoader{

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @return
	* @throws IOException
	* @throws UnrecoganizedCriticalLevel 
	* @see auto_scaling.configuration.IEventCriticalLevelLoader#load(java.io.InputStream) 
	*/
	@Override
	public Map<String, Integer> load(InputStream inputStream) throws IOException, UnrecoganizedCriticalLevel {
		Properties properties = new Properties();
		
		properties.load(inputStream);
		
		Map<String, Integer> criticalLevels = new HashMap<String, Integer>();
		
		String aboutToScaleUpEvent = properties.getProperty(Events.ABOUT_TO_SCALE_UP_EVENT);
		int aboutToScaleUpEventCriticalLevel = getCriticalLevel(aboutToScaleUpEvent);
		criticalLevels.put(Events.ABOUT_TO_SCALE_UP_EVENT, aboutToScaleUpEventCriticalLevel);
		
		String instanceBillingPeriodEndingEvent = properties.getProperty(Events.INSTANCE_BILLING_PERIOD_ENDING_EVENT);
		int instanceBillingPeriodEndingEventCriticalLevel = getCriticalLevel(instanceBillingPeriodEndingEvent);
		criticalLevels.put(Events.INSTANCE_BILLING_PERIOD_ENDING_EVENT, instanceBillingPeriodEndingEventCriticalLevel);
		
		String instancesImpairedEvent = properties.getProperty(Events.INSTANCES_IMPAIRED_EVENT);
		int instancesImpairedEventCriticalLevel = getCriticalLevel(instancesImpairedEvent);
		criticalLevels.put(Events.INSTANCES_IMPAIRED_EVENT, instancesImpairedEventCriticalLevel);
		
		String instancesOnlineEvent = properties.getProperty(Events.INSTANCES_ONLINE_EVENT);
		int instancesOnlineEventCriticalLevel = getCriticalLevel(instancesOnlineEvent);
		criticalLevels.put(Events.INSTANCES_ONLINE_EVENT, instancesOnlineEventCriticalLevel);
		
		String resourceRequirementUpdateEvent = properties.getProperty(Events.RESOURCE_REQUIREMENT_UPDATE_EVENT);
		int resourceRequirementUpdateEventCriticalLevel = getCriticalLevel(resourceRequirementUpdateEvent);
		criticalLevels.put(Events.RESOURCE_REQUIREMENT_UPDATE_EVENT, resourceRequirementUpdateEventCriticalLevel);
		
		String scalingEvent = properties.getProperty(Events.SCALING_EVENT);
		int scalingEventCriticalLevel = getCriticalLevel(scalingEvent);
		criticalLevels.put(Events.SCALING_EVENT, scalingEventCriticalLevel);
		
		String spotInstancesTerminationEvent = properties.getProperty(Events.SPOT_INSTANCES_TERMINATION_EVENT);
		int spotInstancesTerminationEventCriticalLevel = getCriticalLevel(spotInstancesTerminationEvent);
		criticalLevels.put(Events.SPOT_INSTANCES_TERMINATION_EVENT, spotInstancesTerminationEventCriticalLevel);
		
		String spotPriceUpdateEvent = properties.getProperty(Events.SPOT_PRICE_UPDATE_EVENT);
		int spotPriceUpdateEventCriticalLevel = getCriticalLevel(spotPriceUpdateEvent);
		criticalLevels.put(Events.SPOT_PRICE_UPDATE_EVENT, spotPriceUpdateEventCriticalLevel);
		
		String targetSystemStatusEvent = properties.getProperty(Events.TARGET_SYSTEM_STATUS_EVENT);
		int targetSystemStatusEventCriticalLevel = getCriticalLevel(targetSystemStatusEvent);
		criticalLevels.put(Events.TARGET_SYSTEM_STATUS_EVENT, targetSystemStatusEventCriticalLevel);
		
		String spotRequestsClosedBeforeFullfillmentEvent = properties.getProperty(Events.SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT);
		int spotRequestsClosedEventCriticalLevel = getCriticalLevel(spotRequestsClosedBeforeFullfillmentEvent);
		criticalLevels.put(Events.SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT, spotRequestsClosedEventCriticalLevel);
		
		return criticalLevels;
	}

	/**
	 * @Title: getCriticalLevel 
	 * @Description: get the critical level
	 * @param eventLevel the critical level of the event in String
	 * @return the critical level
	 * @throws UnrecoganizedCriticalLevel
	 * @throws
	 */
	private int getCriticalLevel(String eventLevel) throws UnrecoganizedCriticalLevel {
		if (eventLevel.equalsIgnoreCase(NORMAL)) {
			return CriticalLevel.NORMAL;
		}
		
		if (eventLevel.equalsIgnoreCase(PRIORITIZED)) {
			return CriticalLevel.PRIORITIZED;
		}
		
		if (eventLevel.equalsIgnoreCase(URGENT)) {
			return CriticalLevel.URGENT;
		}
		
		if (eventLevel.equalsIgnoreCase(CRITICAL)) {
			return CriticalLevel.CRITICAL;
		}
		
		throw new UnrecoganizedCriticalLevel(eventLevel);
	}

}
