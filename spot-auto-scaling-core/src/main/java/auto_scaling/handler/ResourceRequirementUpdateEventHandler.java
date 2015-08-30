package auto_scaling.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import auto_scaling.capacity.ICapacityCalculator;
import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.ResourceType;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.event.ResourceRequirementUpdateEvent;

/** 
* @ClassName: ResourceRequirementUpdateEventHandler 
* @Description: handler handles resource requirement event handler
* @author Chenhao Qu
* @date 05/06/2015 10:51:50 pm 
*  
*/
public class ResourceRequirementUpdateEventHandler extends EventHandler {

	/** 
	* @Fields capacityCalculator : the capacity calculator
	*/ 
	protected ICapacityCalculator capacityCalculator;
	
	/** 
	* @Fields resourceTypes : the updated resource types
	*/ 
	protected Set<ResourceType> resourceTypes;

	public ResourceRequirementUpdateEventHandler() {
		super(ResourceRequirementUpdateEvent.class);
		reset();
		//threshold = 0;
	}

	/** 
	* @Title: reset 
	* @Description: reset updated resource types
	*/
	private void reset() {
		resourceTypes = new HashSet<ResourceType>(ResourceType.getAllResourceTypes());
	}

	/** 
	* @Title: setCapacityCalculator 
	* @Description: set the capacity calculator
	* @param capacityCalculator the capacity calculator
	*/
	public synchronized void setCapacityCalculator(ICapacityCalculator capacityCalculator) {
		this.capacityCalculator = capacityCalculator;
	}

	/* (non-Javadoc) 
	* <p>Title: doHandling</p> 
	* <p>Description: </p> 
	* @param event 
	* @see auto_scaling.handler.EventHandler#doHandling(auto_scaling.event.Event) 
	*/
	@Override
	protected synchronized void doHandling(Event event) {
		ResourceRequirementUpdateEvent resourceRequirementUpdateEvent = (ResourceRequirementUpdateEvent) event;
		ResourceType resourceType = resourceRequirementUpdateEvent
				.getResourceType();
		
		eventHandlerLog.info(logFormatter.getMessage(resourceType.getName() + " updated"));
		resourceTypes.remove(resourceType);
		
		//if resource type not all updated, then wait
		if (!resourceTypes.isEmpty()) {
			return;
		}
		
		//reset updated resource types
		reset();

		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		//update the estimated request rate
		synchronized (systemStatus) {
			Collection<InstanceStatus> allInstances = systemStatus
					.getAllInstances();
			long totalNumOfRequests = 0;
			for (InstanceStatus instanceStatus : allInstances) {

				if (instanceStatus.isAttached()
						&& instanceStatus.getRunningStatus().equals(RunningStatus.RUNNING) ) {
					Map<String, Number> resourceConsumptionValues = instanceStatus
							.getResourceConsumptionValues();
					InstanceTemplate instanceTemplate = instanceStatus
							.getType();
					long numOfrequests = capacityCalculator
							.getEstimatedNumOfRequestsByUtilization(
									instanceTemplate, resourceConsumptionValues);
					
					totalNumOfRequests += numOfrequests;
				}
			}
			
			long oldTotalNumOfRequests = systemStatus.getTotalNumOfRequests();
			
			//do not update estimated request rate when the system is overloaded and the new estimation is
			//smaller than the older one
			if (oldTotalNumOfRequests <= totalNumOfRequests && systemStatus.isOverLoaded()) {
				
				//reset overload when the system backs to normal
				if (totalNumOfRequests < systemStatus.getAvailableCapacity()) {
					systemStatus.setOverLoaded(false);
				}
			}
			else if (oldTotalNumOfRequests > totalNumOfRequests && systemStatus.isOverLoaded()) {
				return;
			}
			
			systemStatus.setTotalNumOfRequests(totalNumOfRequests);
			eventHandlerLog.info(logFormatter.getMessage("total estimated number of requests: " + totalNumOfRequests));
		}
		
		//fire an about to scale up event for further processing
		EventGenerator eventGenerator = EventGenerator.getEventGenerator();
		Event newEvent = eventGenerator.generateEvent(
				Events.ABOUT_TO_SCALE_UP_EVENT, new HashMap<String, Object>());

		Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
		eventQueue.add(newEvent);

		eventHandlerLog.info(logFormatter.getGenerateEventLogString(newEvent,
				null));

	}
}
