package auto_scaling.handler;

import java.util.HashMap;
import java.util.Map;

/** 
* @ClassName: EventHandlerManager 
* @Description: the manager that manages event handler
* @author Chenhao Qu
* @date 05/06/2015 10:11:15 pm 
*  
*/
public class EventHandlerManager {

	/** 
	* @Fields eventHandlerManager : the global event handler manager
	*/ 
	private static EventHandlerManager eventHandlerManager;
	/** 
	* @Fields eventHandlers : the map of events to event handlers
	*/ 
	private Map<String, EventHandler> eventHandlers;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	private EventHandlerManager() {
		eventHandlers = new HashMap<String, EventHandler>();
	}
	
	/** 
	* @Title: getEventHandlerManager 
	* @Description: get the event handler manager
	* @return
	*/
	public static EventHandlerManager getEventHandlerManager() {
		if (eventHandlerManager == null) {
			eventHandlerManager = new EventHandlerManager();
		}
		return eventHandlerManager;
	}
	
	/** 
	* @Title: getEventHandler 
	* @Description: get the event handler according to the event name
	* @param eventName the event name
	* @return
	*/
	public EventHandler getEventHandler(String eventName) {
		return eventHandlers.get(eventName);
	}
	
	/** 
	* @Title: setEventHandler 
	* @Description: set the event handler for the specific event
	* @param eventName the event name
	* @param eventHandler the new event handler
	*/
	public void setEventHandler(String eventName, EventHandler eventHandler) {
		eventHandlers.put(eventName, eventHandler);
	}
	
	/** 
	* @Title: setEventHandlers 
	* @Description: set the event handlers map
	* @param eventHandlers the event handlers map
	*/
	public void setEventHandlers(Map<String, EventHandler> eventHandlers) {
		this.eventHandlers.putAll(eventHandlers);
	}
}
