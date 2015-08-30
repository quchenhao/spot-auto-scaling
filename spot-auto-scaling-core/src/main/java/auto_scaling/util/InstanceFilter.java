package auto_scaling.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.RunningStatus;

/** 
* @ClassName: InstanceFilter 
* @Description: the helper class for filtering instances
* @author Chenhao Qu
* @date 07/06/2015 4:36:05 pm 
*  
*/
public class InstanceFilter {

	/**
	 * @Title: getAttachedInstances 
	 * @Description: get the instances attached to the load balancer
	 * @param instances the instances to filter
	 * @return the instances attached to the load balancer
	 * @throws
	 */
	public static Collection<InstanceStatus> getAttachedInstances(Collection<InstanceStatus> instances) {
		List<InstanceStatus> filteredInstances = new ArrayList<InstanceStatus>();
		for (InstanceStatus instanceStatus : instances) {
			if (instanceStatus.isAttached()) {
				filteredInstances.add(instanceStatus);
			}
		}
		return filteredInstances;
	}
	
	/**
	 * @Title: getDetachedInstances 
	 * @Description: get the instances detached to the load balancer
	 * @param instances the instances to filter
	 * @return the instances attached to the load balancer
	 * @throws
	 */
	public static Collection<InstanceStatus> getDetachedInstances(Collection<InstanceStatus> instances) {
		List<InstanceStatus> filteredInstances = new ArrayList<InstanceStatus>();
		
		for (InstanceStatus instanceStatus : instances) {
			if (!instanceStatus.isAttached()) {
				filteredInstances.add(instanceStatus);
			}
		}
		return filteredInstances;
	}
	
	/**
	 * @Title: getRunningInstances 
	 * @Description: get the instances that are running
	 * @param instances the instances to filter
	 * @return the instances that are running
	 * @throws
	 */
	public static Collection<InstanceStatus> getRunningInstances(Collection<InstanceStatus> instances) {
		List<InstanceStatus> filteredInstances = new ArrayList<InstanceStatus>();
		for (InstanceStatus instanceStatus : instances) {
			if (instanceStatus.getRunningStatus().equals(RunningStatus.RUNNING)) {
				filteredInstances.add(instanceStatus);
			}
		}
		return filteredInstances;
	}
	
	/**
	 * @Title: getNominalInstances 
	 * @Description: get the instances that are asked, pending or running
	 * @param instances the instances to filter
	 * @return the instances that are asked, pending or running
	 * @throws
	 */
	public static Collection<InstanceStatus> getNominalInstances(Collection<InstanceStatus> instances) {
		List<InstanceStatus> filteredInstances = new ArrayList<InstanceStatus>();
		for (InstanceStatus instanceStatus : instances) {
			String runningStatus = instanceStatus.getRunningStatus();
			if (runningStatus.equals(RunningStatus.ASKED) || runningStatus.equals(RunningStatus.RUNNING)  || runningStatus.equals(RunningStatus.PENDING)) {
				filteredInstances.add(instanceStatus);
			}
		}
		return filteredInstances;
	}
	
	/**
	 * @Title: getConfirmedInstances 
	 * @Description: get the instances that are not in asked status
	 * @param instances the instances to filter
	 * @return the instances that are not in asked status
	 * @throws
	 */
	public static Collection<InstanceStatus> getConfirmedInstances(Collection<InstanceStatus> instances) {
		List<InstanceStatus> filteredInstances = new ArrayList<InstanceStatus>();
		for (InstanceStatus instanceStatus : instances) {
			String runningStatus = instanceStatus.getRunningStatus();
			if (!runningStatus.equals(RunningStatus.ASKED)) {
				filteredInstances.add(instanceStatus);
			}
		}
		return filteredInstances;
	}
	
}
