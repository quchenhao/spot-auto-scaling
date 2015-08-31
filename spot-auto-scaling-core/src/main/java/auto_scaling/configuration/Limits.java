package auto_scaling.configuration;

import auto_scaling.core.FaultTolerantLevel;
import auto_scaling.core.SystemStatus;

/** 
* @ClassName: Limits 
* @Description: the limits of the provision
* @author Chenhao Qu
* @date 05/06/2015 1:56:50 pm 
*  
*/
public class Limits {
	/** 
	* @Fields onDemandCapacityThreshold : the minimum percentage of required capacity provisioned by on demand resources
	*/ 
	protected double onDemandCapacityThreshold;
	/** 
	* @Fields maxChosenSpotTypesNum : the maximum number of chosen spot types 
	*/ 
	protected int maxChosenSpotTypesNum;
	
	/** 
	* @Fields limits : the singlton limits
	*/ 
	protected static Limits limits = new Limits();
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	private Limits(){}
	
	/**
	 * @Title: getMaxChosenSpotTypesNum 
	 * @Description: get the maximum number of chosen spot types 
	 * @return the maximum number of chosen spot types 
	 * @throws
	 */
	public int getMaxChosenSpotTypesNum() {
		return maxChosenSpotTypesNum;
	}
	
	/**
	 * @Title: setMaxChosenSpotTypesNum 
	 * @Description: set the maximum number of chosen spot types 
	 * @param maxChosenSpotTypesNum the new maximum number of chosen spot types 
	 * @throws
	 */
	public void setMaxChosenSpotTypesNum(int maxChosenSpotTypesNum) {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		FaultTolerantLevel faultTolerantLevel = systemStatus.getFaultTolerantLevel();
		if (maxChosenSpotTypesNum <= faultTolerantLevel.getLevel() && !(maxChosenSpotTypesNum == 0 && faultTolerantLevel.getLevel() == 0)) {
			throw new IllegalArgumentException("maximum chosen spot types must be greater than fault tolerance level");
		}
		
		this.maxChosenSpotTypesNum = maxChosenSpotTypesNum;
	}
	
	/**
	 * @Title: getOnDemandCpapcityThreshold 
	 * @Description: get the minimum percentage of required capacity provisioned by on demand resources
	 * @return the minimum percentage of required capacity provisioned by on demand resources
	 * @throws
	 */
	public double getOnDemandCpapcityThreshold() {
		return onDemandCapacityThreshold;
	}
	
	/**
	 * @Title: setOnDemandCapacityThreshold 
	 * @Description: set the minimum percentage of required capacity provisioned by on demand resources
	 * @param onDemandCapacityThreshold the new minimum percentage of required capacity provisioned by on demand resources
	 * @throws
	 */
	public void setOnDemandCapacityThreshold(double onDemandCapacityThreshold) {
		if (onDemandCapacityThreshold < 0 || onDemandCapacityThreshold > 1) {
			throw new IllegalArgumentException("minimum on demand capacity threshold should be between 0 and 1: " + onDemandCapacityThreshold);
		}
		this.onDemandCapacityThreshold = onDemandCapacityThreshold;
	}
	
	/**
	 * @Title: getLimits 
	 * @Description: get the limits
	 * @return the limits
	 * @throws
	 */
	public static Limits getLimits() {
		return limits;
	}
}
