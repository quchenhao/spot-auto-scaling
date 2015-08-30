package auto_scaling.loadbalancer.cloudsim;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.core.cloudsim.ApplicationBrokerManager;
import auto_scaling.core.cloudsim.CloudSimBroker;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.loadbalancer.weightcalculator.IWeightCalculator;

/** 
* @ClassName: CloudSimLoadBalancer 
* @Description: the load balancer implementation for cloudSim
* @author Chenhao Qu
* @date 06/06/2015 1:50:51 pm 
*  
*/
public class CloudSimLoadBalancer extends LoadBalancer {

	/** 
	* <p>Description: initialize with weight calculator</p> 
	* @param weightCalculator the weight calculator
	*/
	public CloudSimLoadBalancer(IWeightCalculator weightCalculator) {
		super(weightCalculator);
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
		ApplicationBrokerManager applicationBrokerManager = ApplicationBrokerManager.getApplicationBrokerManager();
		CloudSimBroker cloudSimBroker = applicationBrokerManager.getCloudSimBroker();
		
		Map<Integer, Integer> newWeights = new HashMap<Integer, Integer>();
		String logMsg = "";
		for (InstanceStatus instanceStatus : weights.keySet()) {
			int id = Integer.parseInt(instanceStatus.getId());
			int weight = weights.get(instanceStatus);
			newWeights.put(id, weight);
			logMsg += id + ": " + weight + ", ";
		}
		lbLog.info(logFormatter.getMessage(logMsg));
		return cloudSimBroker.rebalance(newWeights);
	}

	

}
