package auto_scaling.online;

/** 
* @ClassName: InstanceAuthenticationException 
* @Description: 
* @author Chenhao Qu
* @date 06/06/2015 2:57:54 pm 
*  
*/
@SuppressWarnings("serial")
public class InstanceAuthenticationException extends Exception{

	/** 
	* <p>Description: </p> 
	* @param instanceId the instance id
	*/
	public InstanceAuthenticationException(String instanceId) {
		super(instanceId);
	}
}
