package auto_scaling.event;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/** 
* @ClassName: EventQueueManager 
* @Description: the manager managers the event queue
* @author Chenhao Qu
* @date 05/06/2015 9:40:17 pm 
*  
*/
public class EventQueueManager {

	/** 
	* @Fields eventsQueue : global event priority queue
	*/ 
	private static final Queue<Event> eventsQueue = new PriorityBlockingQueue<Event>();
	
	/** 
	* @Title: getEventsQueue 
	* @Description: get the event priority queue
	* @return the event priority queue
	*/
	public static Queue<Event> getEventsQueue() {
		return eventsQueue;
	}
}
