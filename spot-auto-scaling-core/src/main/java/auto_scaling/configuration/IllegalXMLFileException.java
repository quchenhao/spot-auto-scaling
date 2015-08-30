package auto_scaling.configuration;

/** 
* @ClassName: IllegalXMLFileException 
* @Description: exception in loading an xml configuration file
* @author Chenhao Qu
* @date 04/06/2015 5:08:17 pm 
*  
*/
@SuppressWarnings("serial")
public class IllegalXMLFileException extends Exception {

	/** 
	* <p>Description: </p> 
	* @param msg the error message
	*/
	public IllegalXMLFileException(String msg) {
		super(msg);
	}
}
