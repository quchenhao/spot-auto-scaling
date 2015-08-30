package auto_scaling.core;

import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import auto_scaling.event.Event;
import auto_scaling.event.EventQueueManager;
import auto_scaling.handler.EventHandler;
import auto_scaling.handler.EventHandlerManager;

/** 
* @ClassName: EventProcessor 
* @Description: the event processing thread
* @author Chenhao Qu
* @date 05/06/2015 2:46:44 pm 
*  
*/
public class EventProcessor implements Runnable{

	/** 
	* @Fields eventProcessorLog : event processing log
	*/ 
	protected Logger eventProcessorLog;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public EventProcessor() {
		
		this.eventProcessorLog = LogManager.getLogger(EventProcessor.class);
	}

	/* (non-Javadoc) 
	* <p>Title: run</p> 
	* <p>Description: </p>  
	* @see java.lang.Runnable#run() 
	*/
	@Override
	public void run() {
		Queue<Event> eventsQueue = EventQueueManager.getEventsQueue();
		while(true) {
			if(eventsQueue.isEmpty()) {
				try {
					Thread.sleep(1000);
					continue;
				} catch (InterruptedException e) {
					eventProcessorLog.error("In Event Processing Thread");
					eventProcessorLog.catching(e);
				}
			}
			else {
				handleEvents();
			}
		}
	}

	/**
	 * @Title: handleEvents 
	 * @Description: handle events in the event queue
	 * @throws
	 */
	public void handleEvents() {
		
		Queue<Event> eventsQueue = EventQueueManager.getEventsQueue();
		
		while (!eventsQueue.isEmpty()) {
			Event event = eventsQueue.poll();
			EventHandlerManager eventHandlerManager = EventHandlerManager.getEventHandlerManager();
			EventHandler handler = eventHandlerManager.getEventHandler(event.getEventName());
			
			if (handler != null) {
				
				handler.handle(event);
			}
			else {
				eventProcessorLog.error("Unrecognized Event: " + event.getEventName());
			}
		}
	}
}
