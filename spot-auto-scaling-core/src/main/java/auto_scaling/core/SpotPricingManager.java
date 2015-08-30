package auto_scaling.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.SpotPricingStatus;


/** 
* @ClassName: SpotPricingManager 
* @Description: manager that manages the spot market prices
* @author Chenhao Qu
* @date 05/06/2015 3:01:44 pm 
*  
*/
public class SpotPricingManager {

	/** 
	* @Fields pricings : the market prices for each spot type
	*/ 
	protected Map<InstanceTemplate, SpotPricingStatus> pricings;
	/** 
	* @Fields spotPricingStatus : the global spot pricing manager
	*/ 
	private static SpotPricingManager spotPricingStatus;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	private SpotPricingManager() {
		pricings = new HashMap<InstanceTemplate, SpotPricingStatus>();
		
	}
	
	/**
	 * @Title: getSpotPricingManager 
	 * @Description: get the spot pricing manager
	 * @return the spot pricing manager
	 * @throws
	 */
	public static SpotPricingManager getSpotPricingManager() {
		if (spotPricingStatus == null) {
			spotPricingStatus = new SpotPricingManager();
		}
		
		return spotPricingStatus;
	}
	
	/**
	 * @Title: getSpotPricingStatus 
	 * @Description: get spot pricing status for the given instance template
	 * @param instanceTemplate the instance template
	 * @return the corresponding spot pricing status
	 * @throws
	 */
	public SpotPricingStatus getSpotPricingStatus(InstanceTemplate instanceTemplate) {
		return pricings.get(instanceTemplate);
	}
	
	/**
	 * @Title: getAllSpotPricingStatuses 
	 * @Description: get spot pricing statuses for all inatance templates
	 * @return spot pricing statuses for all inatance templates
	 * @throws
	 */
	public Collection<SpotPricingStatus> getAllSpotPricingStatuses() {
		Collection<SpotPricingStatus> list = new ArrayList<SpotPricingStatus>(pricings.values());
		return list;
	}
	
	/**
	 * @Title: addSpotPricingStatus 
	 * @Description: add spot pricing status for instance template
	 * @param instanceTemplate the new instance template
	 * @param price the initial price
	 * @throws
	 */
	public void addSpotPricingStatus(InstanceTemplate instanceTemplate, double price) {
		if (pricings.containsKey(instanceTemplate)) {
			throw new IllegalArgumentException("spot pricing status for instance type " + instanceTemplate.getName() + " is already included");
		}
		
		SpotPricingStatus spotPricingStatus = new SpotPricingStatus(instanceTemplate);
		spotPricingStatus.setPrice(price);
		pricings.put(instanceTemplate, spotPricingStatus);
	}
}
