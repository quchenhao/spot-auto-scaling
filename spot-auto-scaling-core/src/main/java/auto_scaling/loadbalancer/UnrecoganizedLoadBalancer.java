package auto_scaling.loadbalancer;

/** 
* @ClassName: UnrecoganizedLoadBalancer 
* @Description: 
* @author Chenhao Qu
* @date 06/06/2015 1:50:35 pm 
*  
*/
@SuppressWarnings("serial")
public class UnrecoganizedLoadBalancer extends Exception {

	public UnrecoganizedLoadBalancer(String className) {
		super("Unrecoganized load balancer class: " + className);
	}
}
