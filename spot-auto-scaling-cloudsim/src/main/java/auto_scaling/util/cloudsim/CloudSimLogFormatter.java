package auto_scaling.util.cloudsim;

import org.cloudbus.cloudsim.core.CloudSim;

import auto_scaling.event.Event;
import auto_scaling.util.DefaultLogFormatter;

/** 
* @ClassName: CloudSimLogFormatter 
* @Description: the cloud sim log formatter
* @author Chenhao Qu
* @date 07/06/2015 5:18:41 pm 
*  
*/
public class CloudSimLogFormatter extends DefaultLogFormatter{
	
	/* (non-Javadoc) 
	* <p>Title: getGenerateEventLogString</p> 
	* <p>Description: </p> 
	* @param event
	* @param message
	* @return 
	* @see auto_scaling.util.DefaultLogFormatter#getGenerateEventLogString(auto_scaling.event.Event, java.lang.String) 
	*/
	@Override
	public String getGenerateEventLogString(Event event, String message) {
		return super.getGenerateEventLogString(event, message) + " " + CloudSim.clock();
	}

	/* (non-Javadoc) 
	* <p>Title: getStartHandlingString</p> 
	* <p>Description: </p> 
	* @param event
	* @return 
	* @see auto_scaling.util.DefaultLogFormatter#getStartHandlingString(auto_scaling.event.Event) 
	*/
	@Override
	public String getStartHandlingString(Event event) {
		return super.getStartHandlingString(event) + " " + CloudSim.clock();
	}
	
	/* (non-Javadoc) 
	* <p>Title: getMessage</p> 
	* <p>Description: </p> 
	* @param msg
	* @return 
	* @see auto_scaling.util.DefaultLogFormatter#getMessage(java.lang.String) 
	*/
	@Override
	public String getMessage(String msg) {
		return super.getMessage(msg) + " " + CloudSim.clock();
	}
}
