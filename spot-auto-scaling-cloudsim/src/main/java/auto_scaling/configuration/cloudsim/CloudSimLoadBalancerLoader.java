package auto_scaling.configuration.cloudsim;

import java.io.InputStream;
import java.util.Properties;

import auto_scaling.configuration.ILoadBalancerLoader;
import auto_scaling.loadbalancer.LBProperties;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.loadbalancer.cloudsim.CloudSimLoadBalancer;
import auto_scaling.loadbalancer.weightcalculator.IWeightCalculator;

/** 
* @ClassName: CloudSimLoadBalancerLoader 
* @Description: loader to load load balancer for cloudSim
* @author Chenhao Qu
* @date 05/06/2015 2:29:49 pm 
*  
*/
public class CloudSimLoadBalancerLoader implements ILoadBalancerLoader {

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @return
	* @throws Exception 
	* @see auto_scaling.configuration.ILoadBalancerLoader#load(java.io.InputStream) 
	*/
	@Override
	public LoadBalancer load(InputStream inputStream) throws Exception {
		Properties properties = new Properties();
		properties.load(inputStream);

		String weightCalculatorName = properties
				.getProperty(LBProperties.LB_WEIGHTCALCULATOR);
		IWeightCalculator weightCalculator = (IWeightCalculator) Class.forName(
				weightCalculatorName).newInstance();
		return new CloudSimLoadBalancer(weightCalculator);
	}

}
