package auto_scaling.configuration.cloudsim;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import auto_scaling.capacity.ICapacityCalculator;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.configuration.IEventHandlersLoader;
import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.event.Events;
import auto_scaling.handler.AboutToScaleUpEventHandler;
import auto_scaling.handler.EventHandler;
import auto_scaling.handler.EventHandlers;
import auto_scaling.handler.InstanceBillingPeriodEndingEventHandler;
import auto_scaling.handler.InstancesOnlineEventHandler;
import auto_scaling.handler.ResourceRequirementUpdateEventHandler;
import auto_scaling.handler.SpotInstancesTerminationEventHandler;
import auto_scaling.handler.SpotPriceUpdateEventHandler;
import auto_scaling.handler.SpotRequestsClosedBeforeFullfillmentEventHandler;
import auto_scaling.handler.TargetSystemStatusEventHandler;
import auto_scaling.handler.cloudsim.CloudSimScalingEventHandler;
import auto_scaling.monitor.Monitor;
import auto_scaling.monitor.Monitors;
import auto_scaling.monitor.SpotPriceMonitor;
import auto_scaling.online.IOnlineTask;

/** 
* @ClassName: CloudSimEventHandlersLoader 
* @Description: loader to load event handlers for cloudSim
* @author Chenhao Qu
* @date 05/06/2015 2:29:16 pm 
*  
*/
public class CloudSimEventHandlersLoader implements IEventHandlersLoader {

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param cloudConfiguration
	* @param capacityCalculator
	* @param scalingPoliciesConfiguration
	* @param monitors
	* @param onlineTask
	* @param inputSteram
	* @return
	* @throws Exception 
	* @see auto_scaling.configuration.IEventHandlersLoader#load(auto_scaling.configuration.ICloudConfiguration, auto_scaling.capacity.ICapacityCalculator, auto_scaling.configuration.IScalingPoliciesConfiguration, java.util.Map, auto_scaling.online.IOnlineTask, java.io.InputStream) 
	*/
	@Override
	public Map<String, EventHandler> load(
			ICloudConfiguration cloudConfiguration,
			ICapacityCalculator capacityCalculator,
			IScalingPoliciesConfiguration scalingPoliciesConfiguration,
			Map<String, Monitor> monitors, IOnlineTask onlineTask,
			InputStream inputSteram) throws Exception {
		Properties properties = new Properties();
		
		properties.load(inputSteram);
		
		Map<String, EventHandler> eventHandlers = new HashMap<String, EventHandler>();
		
		String aboutToScaleUpEventHandlerClass = properties.getProperty(EventHandlers.ABOUT_TO_SCALE_UP_EVENT_HANDLER);
		AboutToScaleUpEventHandler aboutToScaleUpEventHandler = (AboutToScaleUpEventHandler)(Class.forName(aboutToScaleUpEventHandlerClass).newInstance());
		aboutToScaleUpEventHandler.setScalingPoliciesConfiguration(scalingPoliciesConfiguration);
		
		eventHandlers.put(Events.ABOUT_TO_SCALE_UP_EVENT, aboutToScaleUpEventHandler);
		
		String instanceBillingPeriodEndingEventHandlerClass = properties.getProperty(EventHandlers.INSTANCE_BILLING_PERIOD_ENDING_EVENT_HANDLER);
		InstanceBillingPeriodEndingEventHandler instanceBillingPeriodEndingEventHandler = (InstanceBillingPeriodEndingEventHandler)(Class.forName(instanceBillingPeriodEndingEventHandlerClass).newInstance());
		instanceBillingPeriodEndingEventHandler.setScalingPoliciesConfiguration(scalingPoliciesConfiguration);
		
		eventHandlers.put(Events.INSTANCE_BILLING_PERIOD_ENDING_EVENT, instanceBillingPeriodEndingEventHandler);
		
		String instancesOnlineEventHandlerClass = properties.getProperty(EventHandlers.INSTANCES_ONLINE_EVENT_HANDLER);
		InstancesOnlineEventHandler instancesOnlineEventHandler = (InstancesOnlineEventHandler)(Class.forName(instancesOnlineEventHandlerClass).newInstance());
		instancesOnlineEventHandler.setOnlineTask(onlineTask);
		
		eventHandlers.put(Events.INSTANCES_ONLINE_EVENT, instancesOnlineEventHandler);
		
		String resourceRequirementUpdateEventHandlerClass = properties.getProperty(EventHandlers.RESOURCE_REQUIREMENT_UPDATE_EVENT_HANDLER);
		ResourceRequirementUpdateEventHandler resourceRequirementUpdateEventHandler = (ResourceRequirementUpdateEventHandler)(Class.forName(resourceRequirementUpdateEventHandlerClass).newInstance());
		resourceRequirementUpdateEventHandler.setCapacityCalculator(capacityCalculator);
		eventHandlers.put(Events.RESOURCE_REQUIREMENT_UPDATE_EVENT, resourceRequirementUpdateEventHandler);
		
		String scalingEventHandlerClass = properties.getProperty(EventHandlers.SCALING_EVENT_HANDLER);
		CloudSimScalingEventHandler scalingEventHandler = (CloudSimScalingEventHandler)(Class.forName(scalingEventHandlerClass).newInstance());
		DistributionSettings distributionSettings = DistributionSettings.getDistributionSettings();
		scalingEventHandler.setSpotRequestFullfillmentDistribution(distributionSettings.getSpotRequestFullfillmentDistribution());
		scalingEventHandler.setVMTerminateDistribution(distributionSettings.getVmTerminateDistribution());
		eventHandlers.put(Events.SCALING_EVENT, scalingEventHandler);
		
		Monitor spotPriceMonitor = monitors.get(Monitors.SPOT_PRICE_MONITOR);
		String spotInstancesTerminationEventHandlerClass = properties.getProperty(EventHandlers.SPOT_INSTANCES_TERMINATION_EVENT_HANDLER);
		SpotInstancesTerminationEventHandler spotInstancesTerminationEventHandler = (SpotInstancesTerminationEventHandler)(Class.forName(spotInstancesTerminationEventHandlerClass).newInstance());
		spotInstancesTerminationEventHandler.setSpotPriceMonitor((SpotPriceMonitor)spotPriceMonitor);
		
		eventHandlers.put(Events.SPOT_INSTANCES_TERMINATION_EVENT, spotInstancesTerminationEventHandler);
		
		String spotPriceUpdateEventHandlerClass = properties.getProperty(EventHandlers.SPOT_PRICE_UPDATE_EVENT_HANDLER);
		SpotPriceUpdateEventHandler spotPriceUpdateEventHandler = (SpotPriceUpdateEventHandler)(Class.forName(spotPriceUpdateEventHandlerClass).newInstance());
		spotPriceUpdateEventHandler.setScalingPoliciesConfiguration(scalingPoliciesConfiguration);
		String spotPriceUpdateEventHandlerInterval = properties.getProperty(SPOT_PRICE_UPDATE_EVENT_HANDLER_INTERVAL);
		spotPriceUpdateEventHandler.setInterval(Integer.parseInt(spotPriceUpdateEventHandlerInterval));
		
		eventHandlers.put(Events.SPOT_PRICE_UPDATE_EVENT, spotPriceUpdateEventHandler);
		
		String targetSystemStatusEventHandlerClass = properties.getProperty(EventHandlers.TARGET_SYSTEM_STATUS_EVENT_HANDLER);
		TargetSystemStatusEventHandler targetSystemStatusEventHandler = (TargetSystemStatusEventHandler)(Class.forName(targetSystemStatusEventHandlerClass).newInstance());
		targetSystemStatusEventHandler.setScalingPoliciesConfiguration(scalingPoliciesConfiguration);
		
		eventHandlers.put(Events.TARGET_SYSTEM_STATUS_EVENT, targetSystemStatusEventHandler);
		
		String spotRequestsClosedBeforeFullfillmentEventHandlerClass = properties.getProperty(EventHandlers.SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT_HANDLER);
		SpotRequestsClosedBeforeFullfillmentEventHandler spotRequestsClosedBeforeFullfillmentEventHandler = (SpotRequestsClosedBeforeFullfillmentEventHandler)(Class.forName(spotRequestsClosedBeforeFullfillmentEventHandlerClass).newInstance());
		spotRequestsClosedBeforeFullfillmentEventHandler.setSpotPriceMonitor((SpotPriceMonitor)spotPriceMonitor);
		
		eventHandlers.put(Events.SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT, spotRequestsClosedBeforeFullfillmentEventHandler);
		
		return eventHandlers;
	}

}
