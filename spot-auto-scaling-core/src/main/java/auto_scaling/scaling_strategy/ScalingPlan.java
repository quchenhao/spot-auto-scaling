package auto_scaling.scaling_strategy;

import java.util.Collection;

/** 
* @ClassName: ScalingPlan 
* @Description: the plan of how to scale the provision
* @author Chenhao Qu
* @date 07/06/2015 2:38:45 pm 
*  
*/
public class ScalingPlan {
	/** 
	* @Fields startSpotRequests : the requests to start different types of spot instances
	*/ 
	protected Collection<StartSpotRequest> startSpotRequests;
	/** 
	* @Fields startOnDemandRequest : the request to start on demand instances
	*/ 
	protected StartOnDemandRequest startOnDemandRequest;
	/** 
	* @Fields terminateVMsRequest : the request to terminate instances
	*/ 
	protected TerminateVMsRequest terminateVMsRequest;
	
	/** 
	* <p>Description: initialize with all the fields</p> 
	* @param startSpotRequests the requests to start different types of spot instances
	* @param startOnDemandRequest the request to start on demand instances
	* @param terminateVMsRequest the request to terminate instances
	*/
	public ScalingPlan(Collection<StartSpotRequest> startSpotRequests, StartOnDemandRequest startOnDemandRequest, TerminateVMsRequest terminateVMsRequest) {
		this.startSpotRequests = startSpotRequests;
		this.startOnDemandRequest = startOnDemandRequest;
		this.terminateVMsRequest = terminateVMsRequest;
	}

	/**
	 * @Title: getStartSpotRequests 
	 * @Description: get the start spot requests
	 * @return the start spot requests
	 * @throws
	 */
	public Collection<StartSpotRequest> getStartSpotRequests() {
		return startSpotRequests;
	}

	/**
	 * @Title: getStartOnDemandRequest 
	 * @Description: get the start on demand request
	 * @return get the start on demand request
	 * @throws
	 */
	public StartOnDemandRequest getStartOnDemandRequest() {
		return startOnDemandRequest;
	}
	
	/**
	 * @Title: getTerminateVMsRequest 
	 * @Description: get the terminate vms request
	 * @return the terminate vms request
	 * @throws
	 */
	public TerminateVMsRequest getTerminateVMsRequest() {
		return terminateVMsRequest;
	}
	
	/* (non-Javadoc) 
	* <p>Title: toString</p> 
	* <p>Description: </p> 
	* @return 
	* @see java.lang.Object#toString() 
	*/
	@Override
	public String toString() {
		String string = startOnDemandRequest == null? "start no on demand" : startOnDemandRequest.toString() + "\n";
		
		if (startSpotRequests == null) {
			string += "start no spot vm\n";
		}
		else {
			for (StartSpotRequest startSpotRequest : startSpotRequests) {
				string += startSpotRequest.toString() + "\n";
			}
		}
		
		string += terminateVMsRequest == null? "terminate no vm" : terminateVMsRequest.toString();
		return string;
	}
}
