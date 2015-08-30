package auto_scaling.util;

import auto_scaling.event.Event;

/** 
* @ClassName: DefaultLogFormatter 
* @Description: the default implementation for the log formatter
* @author Chenhao Qu
* @date 07/06/2015 4:35:30 pm 
*  
*/
public class DefaultLogFormatter extends LogFormatter{
	
	/* (non-Javadoc) 
	* <p>Title: getGenerateEventLogString</p> 
	* <p>Description: </p> 
	* @param event
	* @param message
	* @return 
	* @see auto_scaling.util.LogFormatter#getGenerateEventLogString(auto_scaling.event.Event, java.lang.String) 
	*/
	@Override
	public String getGenerateEventLogString(Event event, String message) {
		String string = "Generate event: id " + event.getEventId() + "; event name " + event.getEventName();
		if (message != null) {
			string += "; message \"" + message + "\"";
		}
		return string;
	}
	
	/* (non-Javadoc) 
	* <p>Title: getStartHandlingString</p> 
	* <p>Description: </p> 
	* @param event
	* @return 
	* @see auto_scaling.util.LogFormatter#getStartHandlingString(auto_scaling.event.Event) 
	*/
	@Override
	public String getStartHandlingString(Event event) {
		return "Handling event: id " + event.getEventId() + "; event name " + event.getEventName();
	}
	
	/* (non-Javadoc) 
	* <p>Title: getEndHandlingString</p> 
	* <p>Description: </p> 
	* @param event
	* @return 
	* @see auto_scaling.util.LogFormatter#getEndHandlingString(auto_scaling.event.Event) 
	*/
	@Override
	public String getEndHandlingString(Event event) {
		return "End Handling event: id " + event.getEventId() + "; event name " + event.getEventName();
	}
	
	/* (non-Javadoc) 
	* <p>Title: getExceptionString</p> 
	* <p>Description: </p> 
	* @param e
	* @return 
	* @see auto_scaling.util.LogFormatter#getExceptionString(java.lang.Exception) 
	*/
	@Override
	public String getExceptionString(Exception e) {
		StackTraceElement[] elements = e.getStackTrace();
		String message = "";
		message += e.toString() + "\n";
		for (StackTraceElement element : elements) {
			message += element.toString() + "\n";
		}
		return "catching exception:\n" + message;
	}

	/* (non-Javadoc) 
	* <p>Title: getMessage</p> 
	* <p>Description: </p> 
	* @param msg
	* @return 
	* @see auto_scaling.util.LogFormatter#getMessage(java.lang.String) 
	*/
	@Override
	public String getMessage(String msg) {
		return msg;
	}
}
