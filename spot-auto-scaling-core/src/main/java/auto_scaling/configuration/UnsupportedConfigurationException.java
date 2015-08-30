package auto_scaling.configuration;

/** 
* @ClassName: UnsupportedConfigurationException 
* @Description:
* @author Chenhao Qu
* @date 05/06/2015 2:02:24 pm 
*  
*/
@SuppressWarnings("serial")
public class UnsupportedConfigurationException extends Exception{

	public UnsupportedConfigurationException (String message) {
		super(message);
	}
}
