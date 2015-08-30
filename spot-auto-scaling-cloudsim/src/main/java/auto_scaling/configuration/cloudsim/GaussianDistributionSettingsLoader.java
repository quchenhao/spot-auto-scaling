package auto_scaling.configuration.cloudsim;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Pair;
import org.cloudbus.cloudsim.ex.delay.GaussianByTypeBootDelay;
import org.cloudbus.cloudsim.ex.delay.IVMBootDelayDistribution;

import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.cloudsim.GaussianSpotRequestFullfillmentDistribution;
import auto_scaling.cloud.cloudsim.GaussianVMTerminateDistribution;
import auto_scaling.cloud.cloudsim.ISpotRequestFullfillmentDistribution;
import auto_scaling.cloud.cloudsim.IVMTerminateDistribution;
import auto_scaling.core.InstanceTemplateManager;

/** 
* @ClassName: GaussianDistributionSettingsLoader 
* @Description: loader to load distributions using Gaussian distribution
* @author Chenhao Qu
* @date 05/06/2015 2:39:25 pm 
*  
*/
public class GaussianDistributionSettingsLoader implements IDistributionSettingsLoader {

	private static final String VM_BOOT_DELAY_MEAN = "vm_boot_delay_mean";
	private static final String VM_BOOT_DELAY_SD = "vm_boot_delay_sd";
	private static final String VM_BOOT_DELAY_SEED = "vm_boot_delay_seed";
	private static final String VM_TERMINATE_DELAY_MEAN = "vm_terminate_delay_mean";
	private static final String VM_TERMINATE_DELAY_SD = "vm_terminate_delay_sd";
	private static final String VM_TERMINATE_DELAY_SEED = "vm_terminate_delay_seed";
	private static final String SPOT_REQUEST_FULLFILLMENT_DELAY_MEAN = "spot_request_fullfillment_delay_mean";
	private static final String SPOT_REQUEST_FULLFILLMENT_DELAY_SD = "spot_request_fullfillment_delay_sd";
	private static final String SPOT_REQUEST_FULLFILLMENT_DELAY_SEED = "spot_request_fullfillment_delay_seed";
	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @throws Exception 
	* @see auto_scaling.configuration.cloudsim.IDistributionSettingsLoader#load(java.io.InputStream) 
	*/
	@Override
	public void load(InputStream inputStream) throws Exception {
		Properties properties = new Properties();
		properties.load(inputStream);
		
		DistributionSettings distributionSettings = DistributionSettings.getDistributionSettings();
		
		double vmBootDelayMean = Double.parseDouble(properties.getProperty(VM_BOOT_DELAY_MEAN));
		double vmBootDelaySD = Double.parseDouble(properties.getProperty(VM_BOOT_DELAY_SD));
		byte[] vmBootDelaySeed = properties.getProperty(VM_BOOT_DELAY_SEED).getBytes();
		
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
		Collection<InstanceTemplate> allInstanceTemplates = instanceTemplateManager.getAllInstanceTemplates();
		
		Pair<Double, Double> valuePair = Pair.of(vmBootDelayMean, vmBootDelaySD);
		Map<Pair<String, String>, Pair<Double, Double>> vmBootdelayDefs = new HashMap<Pair<String, String>, Pair<Double, Double>>();
		for (InstanceTemplate instanceTemplate : allInstanceTemplates) {
			Pair<String, String> key = Pair.of(instanceTemplate.getName(), instanceTemplate.getOs());
			vmBootdelayDefs.put(key, valuePair);
		}
		
		IVMBootDelayDistribution vmBootDelayDistribution = new GaussianByTypeBootDelay(vmBootdelayDefs, vmBootDelaySeed, vmBootDelayMean);
		distributionSettings.setVmBootDelayDistribution(vmBootDelayDistribution);
		
		double vmTerminateDelayMean = Double.parseDouble(properties.getProperty(VM_TERMINATE_DELAY_MEAN));
		double vmTerminateDelaySD = Double.parseDouble(properties.getProperty(VM_TERMINATE_DELAY_SD));
		byte[] vmTerminateDelaySeed = properties.getProperty(VM_TERMINATE_DELAY_SEED).getBytes();
		
		Map<InstanceTemplate, Pair<Double, Double>> vmTerminateDelayDefs = new HashMap<InstanceTemplate, Pair<Double, Double>>();
		Pair<Double, Double> valuePair2 = Pair.of(vmTerminateDelayMean, vmTerminateDelaySD);
		for (InstanceTemplate instanceTemplate : allInstanceTemplates) {
			vmTerminateDelayDefs.put(instanceTemplate, valuePair2);
		}
		
		IVMTerminateDistribution vmTerminateDistribution = new GaussianVMTerminateDistribution(vmTerminateDelayDefs, vmTerminateDelaySeed, (long)vmTerminateDelayMean);
		distributionSettings.setVmTerminateDistribution(vmTerminateDistribution);
		
		double spotRequestFullfillmentDelayMean = Double.parseDouble(properties.getProperty(SPOT_REQUEST_FULLFILLMENT_DELAY_MEAN));
		double spotRequestFullfillmentDelaySD = Double.parseDouble(properties.getProperty(SPOT_REQUEST_FULLFILLMENT_DELAY_SD));
		byte[] spotRequsetFullfillmentDelaySeed = properties.getProperty(SPOT_REQUEST_FULLFILLMENT_DELAY_SEED).getBytes();
		
		Map<InstanceTemplate, Pair<Double, Double>> spotRequestFullfillmentDelayDefs = new HashMap<InstanceTemplate, Pair<Double, Double>>();
		Pair<Double, Double> valuePair3 = Pair.of(spotRequestFullfillmentDelayMean, spotRequestFullfillmentDelaySD);
		for (InstanceTemplate instanceTemplate : allInstanceTemplates) {
			spotRequestFullfillmentDelayDefs.put(instanceTemplate, valuePair3);
		}
		
		ISpotRequestFullfillmentDistribution spotRequestFullfillmentDistribution = new GaussianSpotRequestFullfillmentDistribution(spotRequestFullfillmentDelayDefs, spotRequsetFullfillmentDelaySeed, (long)spotRequestFullfillmentDelayMean);
		distributionSettings.setSpotRequestFullfillmentDistribution(spotRequestFullfillmentDistribution);
	}
	
}
