package auto_scaling.handler;

import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.event.ScalingEvent;

/** 
* @ClassName: ScalingEventHandler 
* @Description: handler that handles scaling event
* @author Chenhao Qu
* @date 05/06/2015 10:57:08 pm 
*  
*/
public abstract class ScalingEventHandler extends EventHandler{
	
	protected static final String TAG_NAME = "name";
	
	/** 
	* @Fields cloudConfiguration : the cloud configuration
	*/ 
	protected ICloudConfiguration cloudConfiguration;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public ScalingEventHandler() {
		super(ScalingEvent.class);
	}

	/** 
	* @Title: setCloudConfiguration 
	* @Description: set the cloud configuration
	* @param cloudConfiguration
	*/
	public synchronized void setCloudConfiguration(ICloudConfiguration cloudConfiguration) {
		if (cloudConfiguration == null) {
			throw new NullPointerException("cloud configuration cannot be null");
		}
		this.cloudConfiguration = cloudConfiguration;
	}
}
