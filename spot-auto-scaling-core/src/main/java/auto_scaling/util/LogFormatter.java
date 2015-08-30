package auto_scaling.util;

import auto_scaling.event.Event;

/** 
* @ClassName: LogFormatter 
* @Description: the class format the log messages
* @author Chenhao Qu
* @date 07/06/2015 4:40:09 pm 
*  
*/
public abstract class LogFormatter {

	/** 
	* @Fields logFormatter : the global formatter
	*/ 
	private static LogFormatter logFormatter;
	
	/**
	 * @Title: getGenerateEventLogString 
	 * @Description: get the log message for generating event
	 * @param event the event generated
	 * @param message the message
	 * @return the log message for generating event
	 * @throws
	 */
	public abstract String getGenerateEventLogString(Event event, String message);
	/**
	 * @Title: getStartHandlingString 
	 * @Description: get the log message for start handling event
	 * @param event the handling event
	 * @return the log message for start handling event
	 * @throws
	 */
	public abstract String getStartHandlingString(Event event);
	/**
	 * @Title: getEndHandlingString 
	 * @Description: get the log message for end handling event
	 * @param event the handling event
	 * @return the log message for end handling event
	 * @throws
	 */
	public abstract String getEndHandlingString(Event event);
	/**
	 * @Title: getExceptionString 
	 * @Description: get the log message for exception
	 * @param e the exception
	 * @return the log message for exception
	 * @throws
	 */
	public abstract String getExceptionString(Exception e);
	/**
	 * @Title: getMessage 
	 * @Description: the log message for message
	 * @param msg the message
	 * @return the log message for message
	 * @throws
	 */
	public abstract String getMessage(String msg);
	
	/**
	 * @Title: getLogFormatter 
	 * @Description: get the log formatter
	 * @return the log formatter
	 * @throws
	 */
	public static LogFormatter getLogFormatter() {
		return logFormatter;
	}
	
	/**
	 * @Title: initialize 
	 * @Description: initialize with class
	 * @param logFormatterClass the class of the log formatter
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws
	 */
	public static void initialize(Class<? extends LogFormatter> logFormatterClass) throws InstantiationException, IllegalAccessException {
		logFormatter = logFormatterClass.newInstance();
	}
}
