package auto_scaling.cloud;

/** 
* @ClassName: UnSupportedResourceException 
* @Description: for resources that not considered
* @author Chenhao Qu
* @date 04/06/2015 12:27:30 pm 
*  
*/
@SuppressWarnings("serial")
public class UnSupportedResourceException extends Exception {

	public UnSupportedResourceException(String resourceName) {
		super("Unsupported resource: " + resourceName);
	}
}
