package auto_scaling.loadbalancer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.loadbalancer.weightcalculator.IWeightCalculator;
import auto_scaling.util.InstanceFilter;
import auto_scaling.util.LogFormatter;

/** 
* @ClassName: LoadBalancer 
* @Description: the base class for load balancer
* @author Chenhao Qu
* @date 06/06/2015 1:45:18 pm 
*  
*/
public abstract class LoadBalancer {
	
	/** 
	* @Fields weightCalculator : the weight calculator
	*/ 
	protected IWeightCalculator weightCalculator;
	/** 
	* @Fields attachedInstances : the attached instances
	*/ 
	protected List<InstanceStatus> attachedInstances;
	/** 
	* @Fields lbLog : the load balancer log
	*/ 
	protected Logger lbLog;
	/** 
	* @Fields logFormatter : the log formatter
	*/ 
	protected LogFormatter logFormatter;
	
	/** 
	* @Fields loadBalancer : the global load balancer
	*/ 
	public static LoadBalancer loadBalancer;
	
	/** 
	* <p>Description: initialize with weight calculator</p> 
	* @param weightCalculator the weight calculator
	*/
	protected LoadBalancer(IWeightCalculator weightCalculator) {
		setWeightCalculator(weightCalculator);
		attachedInstances = Collections.synchronizedList(new ArrayList<InstanceStatus>());
		lbLog = LogManager.getLogger(LoadBalancer.class);
		logFormatter = LogFormatter.getLogFormatter();
	}

	/**
	 * @Title: rebalance 
	 * @Description: rebalance the load balancer
	 * @param weights
	 * @return whether the operation is successful
	 * @throws IOException
	 * @throws
	 */
	protected abstract boolean rebalance(Map<InstanceStatus, Integer> weights) throws IOException;
	
	/**
	 * @Title: attach 
	 * @Description: attach new instances to the load balancer
	 * @param instances the new instances
	 * @return whether the operation is successful
	 * @throws IOException
	 * @throws
	 */
	public boolean attach(Collection<InstanceStatus> instances) throws IOException {
		if (instances == null) {
			throw new NullPointerException("instance is null");
		}
		
		Collection<InstanceStatus> filteredInstances = InstanceFilter.getDetachedInstances(instances);
		
		if (filteredInstances.size() == 0) {
			return true;
		}
		
		List<InstanceStatus> temp = new ArrayList<InstanceStatus>();
		temp.addAll(attachedInstances);
		temp.addAll(filteredInstances);
		
		Map<InstanceStatus, Integer> weights;
		try {
			weights = weightCalculator.getWeight(temp.iterator());
		} catch (Exception e) {
			lbLog.error(logFormatter.getExceptionString(e));
			return false;
		}
		
		lbLog.trace(logFormatter.getMessage("Start attach trace"));
		
		boolean isSuccess = rebalance(weights);
		
		lbLog.trace(logFormatter.getMessage("End attach trace"));
		
		if (isSuccess) {
			for (InstanceStatus instanceStatus : filteredInstances) {
				instanceStatus.setAttached(true);
			}
			
			attachedInstances.addAll(filteredInstances);
		}
		
		return isSuccess;
	}
	
	/**
	 * @Title: detach 
	 * @Description: detach the instances from the load balancer
	 * @param instances the instances to detach
	 * @return whether the operation is successful
	 * @throws IOException
	 * @throws
	 */
	public boolean detach(Collection<InstanceStatus> instances) throws IOException {
		if (instances == null) {
			throw new NullPointerException("instance is null");
		}
		
		Collection<InstanceStatus> filteredInstances = InstanceFilter.getAttachedInstances(instances);
		
		List<InstanceStatus> temp = new ArrayList<InstanceStatus>();
		temp.addAll(attachedInstances);
		temp.removeAll(filteredInstances);
		
		Map<InstanceStatus, Integer> weights;
		try {
			weights = weightCalculator.getWeight(temp.iterator());
		} catch (Exception e) {
			lbLog.error(logFormatter.getExceptionString(e));
			return false;
		}
		
		lbLog.trace(logFormatter.getMessage("Start detach trace"));
		
		boolean isSuccess = rebalance(weights);
		
		lbLog.trace(logFormatter.getMessage("End detach trace"));
		
		if (isSuccess) {
			for (InstanceStatus instanceStatus : filteredInstances) {
				instanceStatus.setAttached(false);
			}
			
			attachedInstances.removeAll(filteredInstances);
		}
		
		return isSuccess;
	}

	/**
	 * @Title: setWeightCalculator 
	 * @Description: set the weight calculator
	 * @param weightCalculator the new weight calculator
	 * @throws
	 */
	public void setWeightCalculator(IWeightCalculator weightCalculator) {
		if (weightCalculator == null) {
			throw new NullPointerException("weight calculator cannot be null");
		}
		
		this.weightCalculator = weightCalculator;
	}
	
	/**
	 * @Title: getLoadBalancer 
	 * @Description: get the load balancer
	 * @return the load balancer
	 * @throws
	 */
	public static LoadBalancer getLoadBalancer() {
		if (loadBalancer == null) {
			throw new NullPointerException("Load Balancer hasn't been initialized");
		}
		
		return loadBalancer;
	}
	
	/**
	 * @Title: setLoadBalancer 
	 * @Description: set the load balancer
	 * @param newLoadBalancer the new load balancer
	 * @throws
	 */
	public synchronized static void setLoadBalancer(LoadBalancer newLoadBalancer) {
		loadBalancer = newLoadBalancer;
	}
}
