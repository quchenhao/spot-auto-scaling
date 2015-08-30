package auto_scaling.scaling_strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: TerminateVMsRequest 
* @Description: the reques to terminate vms
* @author Chenhao Qu
* @date 07/06/2015 3:13:58 pm 
*  
*/
public class TerminateVMsRequest {
	
	/** 
	* @Fields terminatingInstances : the instances to terminate
	*/ 
	protected Collection<InstanceStatus> terminatingInstances;
	
	/** 
	* <p>Description: </p> 
	* @param terminatingInstances  the instances to terminate
	*/
	public TerminateVMsRequest(Collection<InstanceStatus> terminatingInstances) {
		this.terminatingInstances = terminatingInstances;
	}
	
	/**
	 * @Title: getTerminatingInstances 
	 * @Description: get the instances to terminate
	 * @return the instances to terminate
	 * @throws
	 */
	public Collection<InstanceStatus> getTerminatingInstances() {
		return terminatingInstances;
	}
	
	/**
	 * @Title: getTerminatingInstancesIds 
	 * @Description: get the ids of the terminating instances
	 * @return the ids of the terminating instances
	 * @throws
	 */
	public Collection<String> getTerminatingInstancesIds() {
		List<String> instanceIds = new ArrayList<String>();
		for (InstanceStatus instanceStatus : terminatingInstances) {
			instanceIds.add(instanceStatus.getId());
		}
		return instanceIds;
	}
	
	/**
	 * @Title: getNum 
	 * @Description: get the number of instances to terminate
	 * @return the number of instances to terminate
	 * @throws
	 */
	public int getNum() {
		return terminatingInstances.size();
	}
	
	/* (non-Javadoc) 
	* <p>Title: toString</p> 
	* <p>Description: </p> 
	* @return 
	* @see java.lang.Object#toString() 
	*/
	@Override
	public String toString() {
		String string = "terminating:" ;
		for (InstanceStatus instanceStatus : terminatingInstances) {
			InstanceTemplate template = instanceStatus.getType();
			string += " {" + instanceStatus.getId() + " " + template.toString() + "};";
		}
		return string;
	}
}
