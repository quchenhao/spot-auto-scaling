package auto_scaling.online.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.ethz.ssh2.Connection;
import auto_scaling.cloud.ApplicationResourceUsageProfile;
import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.ResourceType;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.online.InstanceAuthenticationException;
import auto_scaling.online.SSHScriptOnlineTask;

/** 
* @ClassName: WikiOnlineTask 
* @Description: the online task implementation for wiki media
* @author Chenhao Qu
* @date 06/06/2015 3:08:42 pm 
*  
*/
public class ExampleOnlineTask extends SSHScriptOnlineTask {

	/** 
	* <p>Description: </p>  
	*/
	public ExampleOnlineTask() {}
	
	/* (non-Javadoc) 
	* <p>Title: performTask</p> 
	* <p>Description: </p> 
	* @param instanceStatus
	* @return
	* @throws InstanceAuthenticationException
	* @throws IOException 
	* @see auto_scaling.online.IOnlineTask#performTask(auto_scaling.cloud.InstanceStatus) 
	*/
	public boolean performTask(InstanceStatus instanceStatus) throws InstanceAuthenticationException, IOException {
		Connection connection = new Connection(instanceStatus.getPublicUrl(), port);
		connection.connect(null, timeout, kexTimeout);
		boolean auth = connection.authenticateWithPublicKey(user, keyFile, keyPassPhrase);
		if (!auth) {
			connection.close();
			throw new InstanceAuthenticationException(instanceStatus.getId());
		}
		
		InstanceTemplate instanceTemplate = instanceStatus.getType();
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
		ApplicationResourceUsageProfile applicationResourceUsageProfile = instanceTemplateManager.getApplicationResourceUsageProfile(instanceTemplate);
		Map<String, Number> resourceProfile = applicationResourceUsageProfile.getApplicationResourceUsageProfile();
		int maxRequestWorkers = (int)(instanceTemplate.getMemoryNum() / resourceProfile.get(ResourceType.MEMORY.getName()).doubleValue());
		int serverLimit = maxRequestWorkers;
		
		List<String> newParams = new ArrayList<String>();
		newParams.add(maxRequestWorkers + "");
		newParams.add(serverLimit + "");
		newParams.addAll(this.params);
		
		return performTask(instanceStatus, newParams);
	}

	
}
