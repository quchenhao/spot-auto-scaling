package auto_scaling.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import auto_scaling.event.Event;
import auto_scaling.util.LogFormatter;

/** 
* @ClassName: EventHandler 
* @Description: the base class for event handler
* @author Chenhao Qu
* @date 05/06/2015 10:08:13 pm 
*  
*/
/** 
* @ClassName: EventHandler 
* @Description: TODO
* @author Chenhao Qu
* @date 05/06/2015 10:10:24 pm 
*  
*/
/** 
* @ClassName: EventHandler 
* @Description: TODO
* @author Chenhao Qu
* @date 05/06/2015 10:10:27 pm 
*  
*/
public abstract class EventHandler {

	/** 
	* @Fields eventHandlerLog : the event handler log
	*/ 
	protected Logger eventHandlerLog = LogManager.getLogger(EventHandler.class);
	/** 
	* @Fields logFormatter : the log formatter
	*/ 
	protected LogFormatter logFormatter;
	/** 
	* @Fields handleClass : the event class the the handler handles
	*/ 
	protected Class<? extends Event> handleClass;
	
	/** 
	* <p>Description: </p> 
	* @param handleClass the handled event class
	*/
	public EventHandler(Class<? extends Event> handleClass){
		this.handleClass = handleClass;
		logFormatter = LogFormatter.getLogFormatter();
	}
	
	/** 
	* @Title: handle 
	* @Description: handle the event
	* @param event the event
	* @throws IllegalArgumentException
	*/
	public void handle(Event event) throws IllegalArgumentException {
		eventHandlerLog.info(logFormatter.getMessage("In " + this.getClass().getName()));
		
		if (event  == null) {
			throw new IllegalArgumentException("event is null");
		}
		
		Class<? extends Event> eventClass = event.getClass();
		
		
		if (!handleClass.isAssignableFrom(eventClass)) {
			throw new IllegalArgumentException("Wrong event type: " + event.getClass().getName());
		}
		
		eventHandlerLog.info(logFormatter.getStartHandlingString(event));
		
		doHandling(event);
		
		eventHandlerLog.info(logFormatter.getEndHandlingString(event));
	}

	
	/** 
	* @Title: doHandling 
	* @Description: the specific handling logic
	* @param event the event
	*/
	protected abstract void doHandling(Event event);
}
