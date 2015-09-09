package auto_scaling.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.SpotInstancesTerminationEvent;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.monitor.SpotPriceMonitor;

/** 
* @ClassName: SpotInstancesTerminationEventHandler 
* @Description: handler that handles spot instances termination event
* @author Chenhao Qu
* @date 05/06/2015 11:00:24 pm 
*  
*/
public class SpotInstancesTerminationEventHandler extends EventHandler {

	/** 
	* @Fields spotPriceMonitor : the spot price monitor
	*/ 
	protected SpotPriceMonitor spotPriceMonitor;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public SpotInstancesTerminationEventHandler() {
		super(SpotInstancesTerminationEvent.class);
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
	protected synchronized void doHandling(Event event) {
		SpotInstancesTerminationEvent spotTerminationEvent = (SpotInstancesTerminationEvent)event;
		Collection<InstanceStatus> terminatingInstances = spotTerminationEvent.getTerminatingInstances();
		
		//detach the instances to the load balancer
		LoadBalancer loadBalancer = LoadBalancer.getLoadBalancer();
		try {
			loadBalancer.detach(terminatingInstances);
		} catch (IOException e) {
			eventHandlerLog.error(logFormatter.getExceptionString(e));
		}
		
		//remove the corresponding chosen spot types
		Set<InstanceTemplate> types = new HashSet<InstanceTemplate>();
		
		for (InstanceStatus instanceStatus : terminatingInstances) {
			types.add(instanceStatus.getType());
		}
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		//remove the spot types and set overloaded if overloading happens
		synchronized (systemStatus) {
			systemStatus.removeChosenSpotTypes(types);
			systemStatus.removeInstances(terminatingInstances);
			eventHandlerLog.info(logFormatter.getMessage("total available capacity: " + systemStatus.getAvailableCapacity()));
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
