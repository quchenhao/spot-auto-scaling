package auto_scaling.online;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import auto_scaling.cloud.InstanceStatus;

/** 
* @ClassName: IOnlineTask 
* @Description: configure the newly online instances before putting them into production
* @author Chenhao Qu
* @date 06/06/2015 2:58:12 pm 
*  
*/
public interface IOnlineTask {

	/** 
	* @Fields onlineTaskLog : the online task log
	*/ 
	static final Logger onlineTaskLog = LogManager.getLogger(IOnlineTask.class);
	/**
	 * @Title: performTask 
	 * @Description: perform the online task
	 * @param instanceStatus the instance
	 * @return whether the operation is successful
	 * @throws Exception
	 * @throws
	 */
	public boolean performTask(InstanceStatus instanceStatus) throws Exception;
}
