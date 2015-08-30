package auto_scaling.scaling_strategy;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.core.FaultTolerantLevel;

/** 
* @ClassName: TargetSystemStatus 
* @Description: the target provision
* @author Chenhao Qu
* @date 07/06/2015 2:59:16 pm 
*  
*/
public class TargetSystemStatus {

	/** 
	* @Fields spotGroups : the target spot groups
	*/ 
	protected Map<InstanceTemplate, SpotBiddingInfo> spotGroups;
	/** 
	* @Fields numOfOnDemandInstances : the number of on demand instances
	*/ 
	protected int numOfOnDemandInstances;
	/** 
	* @Fields onDemandInstanceTemplate : the on demand instance type
	*/ 
	protected InstanceTemplate onDemandInstanceTemplate;
	/** 
	* @Fields isSpotEnabled : whether the spot mode is enabled
	*/ 
	protected boolean isSpotEnabled;
	/** 
	* @Fields terminatingOnDemandInstance : the on demand instance to terminate
	*/ 
	protected OnDemandInstanceStatus terminatingOnDemandInstance;
	/** 
	* @Fields faultTolerantLevel : the fault tolerant level
	*/ 
	protected FaultTolerantLevel faultTolerantLevel;
	
	/** 
	* <p>Description: </p> 
	* @param spotGroups the target spot groups
	* @param numOfOnDemandInstances the number of on demand instances
	* @param onDemandInstanceTemplate the on demand instance type
	* @param terminatingOnDemandInstance the on demand instance to terminate
	* @param faultTolerantLevel the fault tolerant level
	* @param isSpotEnabled whether the spot mode is enabled
	*/
	public TargetSystemStatus (Map<InstanceTemplate, SpotBiddingInfo> spotGroups, int numOfOnDemandInstances,
			InstanceTemplate onDemandInstanceTemplate, OnDemandInstanceStatus terminatingOnDemandInstance, FaultTolerantLevel faultTolerantLevel, boolean isSpotEnabled) {
		this.spotGroups = spotGroups;
		this.numOfOnDemandInstances = numOfOnDemandInstances;
		this.onDemandInstanceTemplate = onDemandInstanceTemplate;
		this.terminatingOnDemandInstance = terminatingOnDemandInstance;
		this.isSpotEnabled = isSpotEnabled;
		this.faultTolerantLevel = faultTolerantLevel;
	}
	
	/**
	 * @Title: getNumOfOnDemandInstances 
	 * @Description: get the number of on demand instances
	 * @return the number of on demand instances
	 * @throws
	 */
	public int getNumOfOnDemandInstances() {
		return numOfOnDemandInstances;
	}
	
	/**
	 * @Title: isSpotEnabled 
	 * @Description: check whether the spot mode is enabled
	 * @return whether the spot mode is enabled
	 * @throws
	 */
	public boolean isSpotEnabled() {
		return isSpotEnabled;
	}
	
	/**
	 * @Title: getChosenTypes 
	 * @Description: get the chosen spot types
	 * @return the chosen spot types
	 * @throws
	 */
	public Collection<InstanceTemplate> getChosenTypes() {
		if (spotGroups == null) {
			return null;
		}
		return Collections.unmodifiableCollection(spotGroups.keySet());
	}
	
	/**
	 * @Title: getOnDemandInstanceTemplate 
	 * @Description: get the on demand instance type
	 * @return the on demand instance type
	 * @throws
	 */
	public InstanceTemplate getOnDemandInstanceTemplate() {
		return onDemandInstanceTemplate;
	}
	
	/**
	 * @Title: getSpotBiddingInfo 
	 * @Description: get the spot bidding info for the instance type
	 * @param instanceTemplate the instance type
	 * @return the corresponding spot bidding info
	 * @throws
	 */
	public SpotBiddingInfo getSpotBiddingInfo(InstanceTemplate instanceTemplate) {
		if (spotGroups == null) {
			return null;
		}
		return spotGroups.get(instanceTemplate);
	}
	
	/**
	 * @Title: getTerminatingOnDemandInstance 
	 * @Description: get the on demand instance to terminate if there is one
	 * @return the on demand instance to terminate (null if no terminating on demand instance)
	 * @throws
	 */
	public OnDemandInstanceStatus getTerminatingOnDemandInstance() {
		return terminatingOnDemandInstance;
	}

	/**
	 * @Title: getTotoalCapacity 
	 * @Description: get the total capacity of the target provision
	 * @return the total capacity of the target provision
	 * @throws
	 */
	public long getTotoalCapacity() {
		FaultTolerantLevel faultTolerantLevel = FaultTolerantLevel.ZERO;
		
		if (isSpotEnabled) {
			faultTolerantLevel = this.faultTolerantLevel;
		}
		
		long totalCapacity = onDemandInstanceTemplate.getCapacity(faultTolerantLevel) * numOfOnDemandInstances;
		if (!isSpotEnabled) {
			return totalCapacity;
		}
		
		if (isSpotEnabled && spotGroups == null) {
			return -1;
		}
		
		for (Entry<InstanceTemplate, SpotBiddingInfo> entry : spotGroups.entrySet()) {
			InstanceTemplate instanceTemplate = entry.getKey();
			SpotBiddingInfo spotBiddingInfo = entry.getValue();
			totalCapacity += instanceTemplate.getCapacity(faultTolerantLevel) * spotBiddingInfo.getNum();
		}
		return totalCapacity;
	}

	/**
	 * @Title: convertToSwitchModeTargetSystemStatus 
	 * @Description: the temporary system status during the transition of mode
	 * @return the transition target system status
	 * @throws
	 */
	public TargetSystemStatus convertToSwitchModeTargetSystemStatus() {
		
		return new TargetSystemStatus(spotGroups, numOfOnDemandInstances + 1, onDemandInstanceTemplate, null, faultTolerantLevel, isSpotEnabled);
	}
}
