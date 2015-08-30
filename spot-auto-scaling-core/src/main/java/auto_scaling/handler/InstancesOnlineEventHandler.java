package auto_scaling.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.event.InstancesOnlineEvent;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.online.IOnlineTask;

/** 
* @ClassName: InstancesOnlineEventHandler 
* @Description: the handler that handles instances online event
* @author Chenhao Qu
* @date 05/06/2015 10:33:40 pm 
*  
*/
public class InstancesOnlineEventHandler extends EventHandler {
	
	/** 
	* @Fields onlineTask : the online tasks to perform on the online instances
	*/ 
	protected IOnlineTask onlineTask;
	/** 
	* @Fields attemptsLimit : the number of online task attempts to perform before give up
	*/ 
	protected int attemptsLimit;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public InstancesOnlineEventHandler() {
		super(InstancesOnlineEvent.class);
		attemptsLimit = 5;
	}
	
	/** 
	* @Title: setAttemptsLimit 
	* @Description: set the attempts to perform online task
	* @param attemptsLimit
	*/
	public synchronized void setAttemptsLimit(int attemptsLimit) {
		if (attemptsLimit < 1) {
			throw new IllegalArgumentException("attempts limit should be at least one");
		}
		this.attemptsLimit = attemptsLimit;
	}
	
	/** 
	* @Title: setOnlineTask 
	* @Description: set the online task
	* @param onlineTask the online task
	*/
	public synchronized void setOnlineTask(IOnlineTask onlineTask) {
		this.onlineTask = onlineTask;
	}

	/* (non-Javadoc) 
	* <p>Title: doHandling</p> 
	* <p>Description: </p> 
	* @param event 
	* @see auto_scaling.handler.EventHandler#doHandling(auto_scaling.event.Event) 
	*/
	@Override
	protected synchronized void doHandling(Event event) {
		InstancesOnlineEvent instancesOnlineEvent = (InstancesOnlineEvent)event;
		
		Collection<InstanceStatus> onlineInstances = instancesOnlineEvent.getOnlineInstances();
		
		Queue<OnlineTaskAttempts> queue = new LinkedList<OnlineTaskAttempts>();
		
		for (InstanceStatus instanceStatus : onlineInstances) {
			queue.add(new OnlineTaskAttempts(instanceStatus));
		}
		
		Collection<InstanceStatus> healthyInstances = new ArrayList<InstanceStatus>();
		Collection<InstanceStatus> unhealthyInstances = new ArrayList<InstanceStatus>();
		//perform online task
		if (onlineTask != null) {
			while (!queue.isEmpty()) {
				OnlineTaskAttempts onlineTaskAttempts = queue.poll();
				InstanceStatus instanceStatus = onlineTaskAttempts.getInstanceStatus();
				if (onlineTaskAttempts.getAttempts() < attemptsLimit) {
					try {
						onlineTaskAttempts.increaseAttempts();
						boolean success = onlineTask.performTask(instanceStatus);
						if (!success) {
							eventHandlerLog.error(logFormatter.getMessage(instanceStatus.getId() + " fail online task at " + onlineTaskAttempts.getAttempts() + " attempts"));
							queue.add(onlineTaskAttempts);
						}
						else {
							healthyInstances.add(instanceStatus);
						}
					} catch (Exception e) {
						eventHandlerLog.error(logFormatter.getMessage(instanceStatus.getId() + " fail online task at " + onlineTaskAttempts.getAttempts() + " attempts"));
						eventHandlerLog.error(logFormatter.getExceptionString(e));
						queue.add(onlineTaskAttempts);
					}
				}
				else {
					unhealthyInstances.add(instanceStatus);
				}
			}
		}
		else {
			healthyInstances.addAll(onlineInstances);
		}
		
		//attach the healthy instances to the load balancer
		LoadBalancer loadBalancer = LoadBalancer.getLoadBalancer();
		try {
			loadBalancer.attach(healthyInstances);
		} catch (IOException e) {
			eventHandlerLog.error(logFormatter.getExceptionString(e));
		}
		
		//fire an impaired instances event and an above to scale up event if some online instances are unhealthy
		if (unhealthyInstances.size() > 0) {
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(EventDataName.IMPAIRED_INSTANCES, unhealthyInstances);
			
			Event newEvent = eventGenerator.generateEvent(Events.INSTANCES_IMPAIRED_EVENT, data);
			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);
			String instances = "";
			
			for (InstanceStatus instanceStatus : unhealthyInstances) {
				instances += " " + instanceStatus.getId();
			}
			
			eventHandlerLog.info(logFormatter.getGenerateEventLogString(newEvent, instances));
			
			Event newEvent2 = eventGenerator.generateEvent(Events.ABOUT_TO_SCALE_UP_EVENT, new HashMap<String, Object>());
			eventQueue.add(newEvent2);
			eventHandlerLog.info(logFormatter.getGenerateEventLogString(newEvent2, null));
		}
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		eventHandlerLog.info(logFormatter.getMessage("total available capacity: " + systemStatus.getAvailableCapacity()));
	}

	/** 
	* @ClassName: OnlineTaskAttempts 
	* @Description: records the online task attempts
	* @author Chenhao Qu
	* @date 05/06/2015 10:49:58 pm 
	*  
	*/
	private class OnlineTaskAttempts {
		/** 
		* @Fields attempts : the number of attempts
		*/ 
		private int attempts;
		/** 
		* @Fields instanceStatus : the online instance
		*/ 
		private InstanceStatus instanceStatus;
		
		/** 
		* <p>Description: </p> 
		* @param instanceStatus the online instance
		*/
		private OnlineTaskAttempts(InstanceStatus instanceStatus) {
			this.instanceStatus = instanceStatus;
			attempts = 0;
		}
		
		/** 
		* @Title: getInstanceStatus 
		* @Description: get the online instance
		* @return
		*/
		public InstanceStatus getInstanceStatus() {
			return instanceStatus;
		}
		
		/** 
		* @Title: getAttempts 
		* @Description: get the attempts
		* @return
		*/
		public int getAttempts() {
			return attempts;
		}
		
		/** 
		* @Title: increaseAttempts 
		* @Description: count the attempts
		*/
		public void increaseAttempts() {
			attempts++;
		}
	}
}
