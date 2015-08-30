package auto_scaling.event;

import java.util.Collection;

import auto_scaling.cloud.InstanceStatus;

/** 
* @ClassName: SpotRequestsClosedBeforeFullfillmentEvent 
* @Description: the event indicates that some spot requests are closed before fullfillment
* @author Chenhao Qu
* @date 05/06/2015 9:56:51 pm 
*  
*/
public class SpotRequestsClosedBeforeFullfillmentEvent extends Event {

	/** 
	* @Fields closedSpotRequests : the closed spot requests
	*/ 
	protected Collection<InstanceStatus> closedSpotRequests;
	/** 
	* <p>Description: </p> 
	* @param critical_level the critical level
	* @param closedSpotRequests the closed spot requests
	*/
	SpotRequestsClosedBeforeFullfillmentEvent(int critical_level, Collection<InstanceStatus> closedSpotRequests) {
		super(critical_level, Events.SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT);
		this.closedSpotRequests = closedSpotRequests;
	}

	/** 
	* @Title: getClosedSpotRequests
	* @Description: get the closed spot requests
	* @return the closed spot requests
	*/
	public Collection<InstanceStatus> getClosedSpotRequests() {
		return closedSpotRequests;
	}
}
