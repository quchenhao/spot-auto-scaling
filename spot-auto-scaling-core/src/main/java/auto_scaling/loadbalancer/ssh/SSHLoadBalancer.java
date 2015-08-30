package auto_scaling.loadbalancer.ssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import auto_scaling.cloud.InstanceStatus;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.loadbalancer.weightcalculator.IWeightCalculator;

/** 
* @ClassName: WikiHaProxyLoadBalancer 
* @Description: the load balancer implementation using haproxy for wiki media application
* @author Chenhao Qu
* @date 06/06/2015 1:54:16 pm 
*  
*/
public class SSHLoadBalancer extends LoadBalancer{
	
	/** 
	* @Fields scriptPath : the path of the control script
	*/ 
	protected String scriptPath;
	/** 
	* @Fields address : the load balancer address
	*/ 
	protected String address;
	/** 
	* @Fields port : the ssh port
	*/ 
	protected int port;
	/** 
	* @Fields user : the user name for ssh connection
	*/ 
	protected String user;
	/** 
	* @Fields keyFile : the credential key file for ssh
	*/ 
	protected File keyFile;
	/** 
	* @Fields passPhrase : the pass phrase for the ssh key file
	*/ 
	protected String passPhrase;

	/** 
	* <p>Description: initialize with all fields</p> 
	* @param weightCalculator the weight calculator
	* @param address the load balancer address
	* @param port the ssh port
	* @param user the user name for ssh connection
	* @param keyFile the credential key file for ssh
	* @param passPhrase the pass phrase for the ssh key file
	* @param scriptPath the path of the control script
	* @throws IOException 
	*/
	public SSHLoadBalancer(IWeightCalculator weightCalculator, String address, int port, String user, File keyFile, String passPhrase, String scriptPath)
			throws IOException {
		super(weightCalculator);
		this.scriptPath = scriptPath;
		this.address = address;
		this.port = port;
		this.user = user;
		this.keyFile = keyFile;
		this.passPhrase = passPhrase;
		Connection sshConnection = new Connection(address, port);
		sshConnection.connect(null, 5000, 10000);
		boolean isAuth = sshConnection.authenticateWithPublicKey(user, keyFile, passPhrase);
		if (!isAuth) {
			sshConnection.close();
			throw new IOException("authentication failure");
		}
		sshConnection.close();
	}

	/* (non-Javadoc) 
	* <p>Title: rebalance</p> 
	* <p>Description: </p> 
	* @param weights
	* @return
	* @throws IOException 
	* @see auto_scaling.loadbalancer.LoadBalancer#rebalance(java.util.Map) 
	*/
	@Override
	protected boolean rebalance(Map<InstanceStatus, Integer> weights)
			throws IOException {
		
		Connection sshConnection = new Connection(address, port);
		sshConnection.connect(null, 5000, 10000);
		boolean isAuth = sshConnection.authenticateWithPublicKey(user, keyFile, passPhrase);
		if (!isAuth) {
			sshConnection.close();
			throw new IOException("authentication failure");
		}
		
		
		String command = "bash " + scriptPath;
		
		for (Entry<InstanceStatus, Integer> entry : weights.entrySet()) {
			command += " " + entry.getKey().getPrivateUrl() + " " + entry.getValue();
			
		}
		
		lbLog.info(logFormatter.getMessage(command));
		
		Session session = sshConnection.openSession();
		
		session.execCommand(command);
		
		InputStream instream = session.getStdout();
		InputStreamReader inreader = new InputStreamReader(instream);
		BufferedReader breader = new BufferedReader(inreader);
		
		String line;
		while ((line = breader.readLine()) != null) {
			lbLog.info(logFormatter.getMessage(line));
		}
		
		breader.close();
		inreader.close();
		instream.close();
		
		instream = session.getStderr();
		inreader = new InputStreamReader(instream);
		breader = new BufferedReader(inreader);
		
		String errorString = "";
		
		while ((line = breader.readLine()) != null) {
			lbLog.error(logFormatter.getMessage(line));
			errorString += line + "\n";
		}
		
		breader.close();
		inreader.close();
		instream.close();
		
		session.close();
		sshConnection.close();
		
		if (errorString.equals("")) {
			return true;
		}
		
		return false;
	}

}
