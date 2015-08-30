package auto_scaling.handler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.SpotRequestsClosedBeforeFullfillmentEvent;
import auto_scaling.monitor.SpotPriceMonitor;

/** 
* @ClassName: SpotRequestsClosedBeforeFullfillmentEventHandler 
* @Description: handler that handles spot request closed before fullfillment
* @author Chenhao Qu
* @date 05/06/2015 11:13:32 pm 
*  
*/
public class SpotRequestsClosedBeforeFullfillmentEventHandler extends EventHandler{

	/** 
	* @Fields spotPriceMonitor : the spot price monitor
	*/ 
	protected SpotPriceMonitor spotPriceMonitor;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public SpotRequestsClosedBeforeFullfillmentEventHandler() {
		super(SpotRequestsClosedBeforeFullfillmentEvent.class);
	}

	/** 
	* @Title: setSpotPriceMonitor 
	* @Description: set the spot price monitor
	* @param spotPriceMonitor the spot price monitor
	*/
	public synchronized void setSpotPriceMonitor(SpotPriceMonitor spotPriceMonitor) {
		if (spotPriceMonitor == null) {
			throw new NullPointerException("spot price monitor cannot be null!");
		}
		this.spotPriceMonitor = spotPriceMonitor;
	}
	
	/* (non-Javadoc) 
	* <p>Title: doHandling</p> 
	* <p>Description: </p> 
	* @param event 
	* @see auto_scaling.handler.EventHandler#doHandling(auto_scaling.event.Event) 
	*/
	@Override
	protected void doHandling(Event event) {
		SpotRequestsClosedBeforeFullfillmentEvent spotRequestsClosedBeforeFullfillmentEvent = (SpotRequestsClosedBeforeFullfillmentEvent)event;
		Collection<InstanceStatus> closedSpotRequests = spotRequestsClosedBeforeFullfillmentEvent.getClosedSpotRequests();
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		Set<InstanceTemplate> types = new HashSet<InstanceTemplate>();
		
		for (InstanceStatus instanceStatus : closedSpotRequests) {
			types.add(instanceStatus.getType());
		}
		
		//remove the instances and set overloaded if overloading happens
		synchronized (systemStatus) {
			systemStatus.removeInstances(closedSpotRequests);
			systemStatus.removeChosenSpotTypes(types);
			eventHandlerLog.info(logFormatter.getMessage("total nominal capacity: " + systemStatus.getNominalCapacity()));
			
			long maximumAvailableCapacity = systemStatus.getMaximumAvaliableCapacity();
			if (maximumAvailableCapacity < systemStatus.getTotalNumOfRequests()) {
				systemStatus.setOverLoaded(true);
			}
		}
		
		//refresh the spot price immediately
		spotPriceMonitor.refresh();
	}

}
