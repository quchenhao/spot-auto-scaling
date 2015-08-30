package auto_scaling.configuration.cloudsim;

import org.cloudbus.cloudsim.ex.delay.IVMBootDelayDistribution;

import auto_scaling.cloud.cloudsim.ISpotRequestFullfillmentDistribution;
import auto_scaling.cloud.cloudsim.IVMTerminateDistribution;

/** 
* @ClassName: DistributionSettings 
* @Description: the distribution settings for cloudSim
* @author Chenhao Qu
* @date 05/06/2015 2:33:25 pm 
*  
*/
public class DistributionSettings {

	/** 
	* @Fields vmBootDelayDistribution : the vm boot up delay distribution
	*/ 
	protected IVMBootDelayDistribution vmBootDelayDistribution;
	/** 
	* @Fields vmTerminateDistribution : the vm termination delay distribution
	*/ 
	protected IVMTerminateDistribution vmTerminateDistribution;
	/** 
	* @Fields spotRequestFullfillmentDistribution : the spot request fullfillment delay distribution
	*/ 
	protected ISpotRequestFullfillmentDistribution spotRequestFullfillmentDistribution;
	/** 
	* @Fields distribtionSettings : the global distribution settings
	*/ 
	private static DistributionSettings distribtionSettings = new DistributionSettings();
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	private DistributionSettings() {}
	
	/**
	 * @Title: getDistributionSettings 
	 * @Description: get the distributed settings
	 * @return the distributed settings
	 * @throws
	 */
	public static DistributionSettings getDistributionSettings() {
		return distribtionSettings;
	}

	/**
	 * @Title: getVmBootDelayDistribution 
	 * @Description: get the vm boot up delay distribution
	 * @return the vm boot up delay distribution
	 * @throws
	 */
	public IVMBootDelayDistribution getVmBootDelayDistribution() {
		return vmBootDelayDistribution;
	}

	/**
	 * @Title: setVmBootDelayDistribution 
	 * @Description: set the vm boot up delay distribution
	 * @param vmBootDelayDistribution the new vm boot up delay distribution
	 * @throws
	 */
	public void setVmBootDelayDistribution(
			IVMBootDelayDistribution vmBootDelayDistribution) {
		if (vmBootDelayDistribution == null) {
			throw new NullPointerException("vm boot delay distribution cannot be null");
		}
		
		this.vmBootDelayDistribution = vmBootDelayDistribution;
	}

	/**
	 * @Title: getVmTerminateDistribution 
	 * @Description: get the vm termination delay distribution 
	 * @return the vm termination delay distribution
	 * @throws
	 */
	public IVMTerminateDistribution getVmTerminateDistribution() {
		return vmTerminateDistribution;
	}

	/**
	 * @Title: setVmTerminateDistribution 
	 * @Description: set the vm termination delay distribution
	 * @param vmTerminateDistribution the vm termination delay distribution
	 * @throws
	 */
	public void setVmTerminateDistribution(
			IVMTerminateDistribution vmTerminateDistribution) {
		if (vmTerminateDistribution == null) {
			throw new NullPointerException("vm termination distribution cannot be null");
		}
		
		this.vmTerminateDistribution = vmTerminateDistribution;
	}

	/**
	 * @Title: getSpotRequestFullfillmentDistribution 
	 * @Description: get the spot request fullfillment delay distribution
	 * @return the spot request fullfillment delay distribution
	 * @throws
	 */
	public ISpotRequestFullfillmentDistribution getSpotRequestFullfillmentDistribution() {
		return spotRequestFullfillmentDistribution;
	}

	/**
	 * @Title: setSpotRequestFullfillmentDistribution 
	 * @Description: set the spot request fullfillment delay distribution
	 * @param spotRequestFullfillmentDistribution the spot request fullfillment delay distribution
	 * @throws
	 */
	public void setSpotRequestFullfillmentDistribution(
			ISpotRequestFullfillmentDistribution spotRequestFullfillmentDistribution) {
		if (spotRequestFullfillmentDistribution == null) {
			throw new NullPointerException("spot request fullfillment distribution cannot be null");
		}
		
		this.spotRequestFullfillmentDistribution = spotRequestFullfillmentDistribution;
	}
	
	
}
