package auto_scaling.configuration.aws;

import auto_scaling.configuration.ICredentials;

import com.amazonaws.auth.AWSCredentials;

/** 
* @ClassName: AWSCredentialsWrapper 
* @Description: the wrapper of the AWS credentials in AWS java sdk
* @author Chenhao Qu
* @date 05/06/2015 2:12:50 pm 
*  
*/
public class AWSCredentialsWrapper implements ICredentials{

	/** 
	* @Fields credentials : AWS credentials in AWS java sdk
	*/ 
	protected AWSCredentials credentials;
	
	/** 
	* <p>Description: initialize with AWS credentials</p> 
	* @param credentials the AWS credentials
	*/
	public AWSCredentialsWrapper(AWSCredentials credentials) {
		this.credentials = credentials;
	}
	
	/* (non-Javadoc) 
	* <p>Title: getCredentials</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.ICredentials#getCredentials() 
	*/
	@Override
	public Object getCredentials() {
		
		return credentials;
	}

}
