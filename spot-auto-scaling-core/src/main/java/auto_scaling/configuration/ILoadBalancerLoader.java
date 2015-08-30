package auto_scaling.configuration;

import java.io.InputStream;

import auto_scaling.loadbalancer.LoadBalancer;

/** 
* @ClassName: ILoadBalancerLoader 
* @Description: loader to load load balancer
* @author Chenhao Qu
* @date 04/06/2015 5:09:04 pm 
*  
*/
public interface ILoadBalancerLoader {
	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param inputStream the input stream
	 * @return the input stream
	 * @throws Exception
	 * @throws
	 */
	public LoadBalancer load(InputStream inputStream) throws Exception;
}
