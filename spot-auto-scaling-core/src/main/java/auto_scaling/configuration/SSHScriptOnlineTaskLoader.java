package auto_scaling.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import auto_scaling.online.IOnlineTask;
import auto_scaling.online.SSHScriptOnlineTask;

/** 
* @ClassName: SSHScriptOnlineTaskLoader 
* @Description: the loader that loads online task performing ssh requests to the newly online instances
* @author Chenhao Qu
* @date 05/06/2015 2:01:10 pm 
*  
*/
public class SSHScriptOnlineTaskLoader implements IOnlineTaskLoader {

	private static final String USER = "user";
	private static final String PORT = "port";
	private static final String KEY_FILE = "key_file";
	private static final String KEY_PASSPHRASE = "key_passphrase";
	private static final String ONLINE_SCRIPTPATH = "online_scriptpath";
	private static final String PARAM = "param";
	private static final String ONLINE_TASK_CLASS = "online_task_class";
	
	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @return
	* @throws InstantiationException
	* @throws IllegalAccessException
	* @throws ClassNotFoundException
	* @throws IOException 
	* @see auto_scaling.configuration.IOnlineTaskLoader#load(java.io.InputStream) 
	*/
	@Override
	public IOnlineTask load(InputStream inputStream) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(inputStream);
		
		String onlineTaskClass = properties.getProperty(ONLINE_TASK_CLASS);
		SSHScriptOnlineTask onlineTask = (SSHScriptOnlineTask)(Class.forName(onlineTaskClass).newInstance());
		
		String user = properties.getProperty(USER);
		onlineTask.setUser(user);
		
		String port = properties.getProperty(PORT);
		onlineTask.setPort(Integer.parseInt(port));
		
		String keyFile = properties.getProperty(KEY_FILE);
		onlineTask.setKeyFile(new File(keyFile));
		
		String keyPassPhrase = properties.getProperty(KEY_PASSPHRASE);
		onlineTask.setKeyPassPhrase(keyPassPhrase);
		
		String onlineScript = properties.getProperty(ONLINE_SCRIPTPATH);
		onlineTask.setScriptPath(onlineScript);
		
		int i = 1;
		List<String> params = new ArrayList<String>();
		while (properties.containsKey(PARAM + "_" + i)) {
			params.add(properties.getProperty(PARAM + "_" + i));
			i++;
		}
		
		onlineTask.setParams(params);
		
		return onlineTask;
	}

}
