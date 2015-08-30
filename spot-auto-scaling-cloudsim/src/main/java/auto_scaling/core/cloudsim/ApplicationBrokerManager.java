package auto_scaling.core.cloudsim;

/** 
* @ClassName: ApplicationBrokerManager 
* @Description: manager that manages application broker
* @author Chenhao Qu
* @date 05/06/2015 3:56:25 pm 
*  
*/
public class ApplicationBrokerManager {

	/** 
	* @Fields datacenterBroker : the broker
	*/ 
	protected CloudSimBroker datacenterBroker;
	/** 
	* @Fields applicationBrokerManager : the global application broker manager
	*/ 
	private static ApplicationBrokerManager applicationBrokerManager;
	
	/**
	 * @Title: getApplicationBrokerManager 
	 * @Description: get the application broker manager
	 * @return the application broker manager
	 * @throws
	 */
	public static ApplicationBrokerManager getApplicationBrokerManager() {
		if (applicationBrokerManager == null) {
			applicationBrokerManager = new ApplicationBrokerManager();
		}
		
		return applicationBrokerManager;
	}
	
	/**
	 * @Title: setCloudSimBroker 
	 * @Description: set the cloudSim broker
	 * @param datacenterBroker the broker
	 * @throws
	 */
	public void setCloudSimBroker(CloudSimBroker datacenterBroker) {
		if (datacenterBroker == null) {
			throw new NullPointerException("CloudSim Broker cannot be null");
		}
		this.datacenterBroker = datacenterBroker;
	}
	
	/**
	 * @Title: getCloudSimBroker 
	 * @Description: get the cloudSim broker
	 * @return the cloudSim broker
	 * @throws
	 */
	public CloudSimBroker getCloudSimBroker() {
		return datacenterBroker;
	}
}
