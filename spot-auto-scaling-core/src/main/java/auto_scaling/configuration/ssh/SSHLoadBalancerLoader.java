package auto_scaling.configuration.ssh;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import auto_scaling.configuration.ILoadBalancerLoader;
import auto_scaling.loadbalancer.LBProperties;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.loadbalancer.ssh.SSHLoadBalancer;
import auto_scaling.loadbalancer.weightcalculator.IWeightCalculator;

/** 
* @ClassName: WikiHaProxyLoadBalancerLoader 
* @Description: loader to load the load balancer using haproxy for wiki media
* @author Chenhao Qu
* @date 05/06/2015 2:43:24 pm 
*  
*/
public class SSHLoadBalancerLoader implements ILoadBalancerLoader {

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @return
	* @throws IOException
	* @throws InstantiationException
	* @throws IllegalAccessException
	* @throws ClassNotFoundException 
	* @see auto_scaling.configuration.ILoadBalancerLoader#load(java.io.InputStream) 
	*/
	@Override
	public LoadBalancer load(InputStream inputStream) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Properties properties = new Properties();
		properties.load(inputStream);

		String weightCalculatorName = properties
				.getProperty(LBProperties.LB_WEIGHTCALCULATOR);
		IWeightCalculator weightCalculator = (IWeightCalculator) Class.forName(
				weightCalculatorName).newInstance();

		String address = properties.getProperty(LBProperties.LB_ADDRESS);
		String port = properties.getProperty(LBProperties.LB_PORT);
		String user = properties.getProperty(LBProperties.LB_USER);
		String keyFile = properties.getProperty(LBProperties.LB_KEYFILE);
		String passPhrase = properties.getProperty(LBProperties.LB_PASSPHRASE);
		String scriptPath = properties.getProperty(LBProperties.LB_SCRIPTPATH);

		return new SSHLoadBalancer(weightCalculator, address,
				Integer.parseInt(port), user, new File(keyFile), passPhrase,
				scriptPath);

	}

}
