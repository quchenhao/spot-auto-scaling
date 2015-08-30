package auto_scaling.online;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.util.LogFormatter;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

/** 
* @ClassName: SSHScriptOnlineTask 
* @Description: the online task implementation using ssh script
* @author Chenhao Qu
* @date 06/06/2015 3:00:01 pm 
*  
*/
public abstract class SSHScriptOnlineTask implements IOnlineTask{
	
	/** 
	* @Fields port : the ssh port
	*/ 
	protected int port;
	/** 
	* @Fields keyFile : the credential key
	*/ 
	protected File keyFile;
	/** 
	* @Fields keyPassPhrase : the passphrase for the credential key
	*/ 
	protected String keyPassPhrase;
	/** 
	* @Fields user : the user for the ssh connection
	*/ 
	protected String user;
	/** 
	* @Fields timeout : the timeout of ssh connection
	*/ 
	protected int timeout;
	/** 
	* @Fields kexTimeout : the kex timeout
	*/ 
	protected int kexTimeout;
	/** 
	* @Fields scriptPath : the script path
	*/ 
	protected String scriptPath;
	/** 
	* @Fields params : the params to the script
	*/ 
	protected List<String> params;
	/** 
	* @Fields logFormatter : the log formatter
	*/ 
	protected LogFormatter logFormatter;
	
	
	/** 
	* <p>Description: </p>  
	*/
	public SSHScriptOnlineTask() {
		logFormatter = LogFormatter.getLogFormatter();
	}
	
	
	/**
	 * @Title: getPort 
	 * @Description: get the ssh port
	 * @return the ssh port
	 * @throws
	 */
	public synchronized int getPort() {
		return port;
	}

	/**
	 * @Title: setPort 
	 * @Description: set the ssh port
	 * @param port the new ssh port
	 * @throws
	 */
	public synchronized void setPort(int port) {
		this.port = port;
	}

	/**
	 * @Title: getKeyFile 
	 * @Description: get the key credential
	 * @return the key credential
	 * @throws
	 */
	public synchronized File getKeyFile() {
		return keyFile;
	}

	/**
	 * @Title: setKeyFile 
	 * @Description: set the key credential
	 * @param keyFile the key credential
	 * @throws
	 */
	public synchronized void setKeyFile(File keyFile) {
		this.keyFile = keyFile;
	}

	/**
	 * @Title: getKeyPassPhrase 
	 * @Description: get the passphrase for key
	 * @return the passphrase for key
	 * @throws
	 */
	public synchronized String getKeyPassPhrase() {
		return keyPassPhrase;
	}

	/**
	 * @Title: setKeyPassPhrase 
	 * @Description: set the key passphrase
	 * @param keyPassPhrase the new key passphrase
	 * @throws
	 */
	public synchronized void setKeyPassPhrase(String keyPassPhrase) {
		this.keyPassPhrase = keyPassPhrase;
	}

	/**
	 * @Title: getUser 
	 * @Description: get the ssh user
	 * @return the ssh user
	 * @throws
	 */
	public synchronized String getUser() {
		return user;
	}

	/**
	 * @Title: setUser 
	 * @Description: set the ssh user
	 * @param user the ssh user
	 * @throws
	 */
	public synchronized void setUser(String user) {
		this.user = user;
	}

	/**
	 * @Title: getTimeout 
	 * @Description: get the timeout
	 * @return the timeout
	 * @throws
	 */
	public synchronized int getTimeout() {
		return timeout;
	}

	/**
	 * @Title: setTimeout 
	 * @Description: set the timeout
	 * @param timeout the new timeout
	 * @throws
	 */
	public synchronized void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @Title: getKexTimeout 
	 * @Description: get the kex timeout
	 * @return the kex timeout
	 * @throws
	 */
	public synchronized int getKexTimeout() {
		return kexTimeout;
	}

	/**
	 * @Title: setKexTimeout 
	 * @Description: set the kex timeout
	 * @param kexTimeout the kex timeout
	 * @throws
	 */
	public synchronized void setKexTimeout(int kexTimeout) {
		this.kexTimeout = kexTimeout;
	}

	/**
	 * @Title: getScriptPath 
	 * @Description: get the script path
	 * @return the script path
	 * @throws
	 */
	public synchronized String getScriptPath() {
		return scriptPath;
	}

	/**
	 * @Title: setScriptPath 
	 * @Description: set the script path
	 * @param scriptPath the new script path
	 * @throws
	 */
	public synchronized void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	/**
	 * @Title: getParams 
	 * @Description: get the params
	 * @return the params
	 * @throws
	 */
	public synchronized List<String> getParams() {
		return params;
	}

	/**
	 * @Title: setParams 
	 * @Description: set the params
	 * @param params the new params
	 * @throws
	 */
	public synchronized void setParams(List<String> params) {
		this.params = params;
	}

	/**
	 * @Title: performTask 
	 * @Description: do online task
	 * @param instanceStatus the instance
	 * @param params the params
	 * @return whether the operation is successful
	 * @throws IOException
	 * @throws InstanceAuthenticationException
	 * @throws
	 */
	protected boolean performTask(InstanceStatus instanceStatus, List<String> params) throws IOException, InstanceAuthenticationException{
		Connection connection = new Connection(instanceStatus.getPublicUrl(), port);
		connection.connect(null, timeout, kexTimeout);
		boolean auth = connection.authenticateWithPublicKey(user, keyFile, keyPassPhrase);
		if (!auth) {
			connection.close();
			throw new InstanceAuthenticationException(instanceStatus.getId());
		}
		
		String command = "bash " + scriptPath;
		
		for (String param : params) {
			command += " " + param.trim();
		}
		
		Session session = connection.openSession();
		session.execCommand(command);
		
		InputStream instream = session.getStderr();
		InputStreamReader inreader = new InputStreamReader(instream);
		BufferedReader breader = new BufferedReader(inreader);
		
		String errorString = "";
		
		String line;
		while ((line = breader.readLine()) != null) {
			onlineTaskLog.error(logFormatter.getMessage(line));
			errorString += line + "\n";
		}
		
		breader.close();
		inreader.close();
		instream.close();
		
		session.close();
		connection.close();
		
		if (errorString.equals("")) {
			return true;
		}
		
		return false;
	}

}
