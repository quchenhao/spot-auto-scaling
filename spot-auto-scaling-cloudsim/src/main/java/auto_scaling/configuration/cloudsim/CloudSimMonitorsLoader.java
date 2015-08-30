package auto_scaling.configuration.cloudsim;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import auto_scaling.cloud.cloudsim.IVMTerminateDistribution;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.configuration.IMonitorsLoader;
import auto_scaling.monitor.Metrics;
import auto_scaling.monitor.Monitor;
import auto_scaling.monitor.Monitors;
import auto_scaling.monitor.cloudsim.CloudSimBillingPeriodMonitor;
import auto_scaling.monitor.cloudsim.CloudSimCPUUtilizationMonitor;
import auto_scaling.monitor.cloudsim.CloudSimMemoryUtilizationMonitor;
import auto_scaling.monitor.cloudsim.CloudSimSpotPriceMonitor;
import auto_scaling.monitor.cloudsim.CloudSimSpotRequestsMonitor;
import auto_scaling.monitor.cloudsim.CloudSimVMStatusMonitor;

/** 
* @ClassName: CloudSimMonitorsLoader 
* @Description: loader to load monitors for cloudSim
* @author Chenhao Qu
* @date 05/06/2015 2:30:30 pm 
*  
*/
public class CloudSimMonitorsLoader implements IMonitorsLoader {

	protected static final String SPOT_SHUT_DOWN_DELAY = "spot_shut_down_delay";
	
	/* (non-Javadoc) 
	* <p>Title: loadMonitors</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @param cloudConfiguration
	* @return
	* @throws Exception 
	* @see auto_scaling.configuration.IMonitorsLoader#loadMonitors(java.io.InputStream, auto_scaling.configuration.ICloudConfiguration) 
	*/
	@Override
	public Map<String, Monitor> loadMonitors(InputStream inputStream,
			ICloudConfiguration cloudConfiguration) throws Exception {
		Properties properties = new Properties();
		properties.load(inputStream);
		
		Map<String, Monitor> monitors = new HashMap<String, Monitor>();
		
		int cpuUtilizationMonitorInterval = Integer.parseInt(properties.getProperty(CPU_UTILIZATION_MONITOR_INTERVAL));
		Monitor cpuUtilizationMonitor = new CloudSimCPUUtilizationMonitor(Monitors.CPU_UTILIZATION_MONITOR, Metrics.CPU_UTILIZATION, cpuUtilizationMonitorInterval);
		monitors.put(Monitors.CPU_UTILIZATION_MONITOR, cpuUtilizationMonitor);
		
		int memoryUtilizationMonitorInterval = Integer.parseInt(properties.getProperty(MEMORY_UTILIZATION_MONITOR_INTERVAL));
		Monitor memoryUtilizationMonitor = new CloudSimMemoryUtilizationMonitor(Monitors.MEMORY_UTILIZATION_MONITOR, Metrics.MEMORY_UTILIZATION, memoryUtilizationMonitorInterval);
		monitors.put(Monitors.MEMORY_UTILIZATION_MONITOR, memoryUtilizationMonitor);
		
		int billingPeriodMonitorInterval = Integer.parseInt(properties.getProperty(BILLING_PERIOD_MONITOR_INTERVAL));
		int billingPeriodMonitotEndingThreshold = Integer.parseInt(properties.getProperty(BILLING_PERIOD_MONITOR_ENDING_THRESHOLD));
		Monitor billingPeriodMonitor = new CloudSimBillingPeriodMonitor(Monitors.BILLING_PERIOD_MONITOR, billingPeriodMonitorInterval, billingPeriodMonitotEndingThreshold);
		monitors.put(Monitors.BILLING_PERIOD_MONITOR, billingPeriodMonitor);
		
		int spotPriceMonitorInterval = Integer.parseInt(properties.getProperty(SPOT_PRICE_MONITOR_INTERVAL));
		Monitor spotPriceMonitor = new CloudSimSpotPriceMonitor(Monitors.SPOT_PRICE_MONITOR, spotPriceMonitorInterval);
		monitors.put(Monitors.SPOT_PRICE_MONITOR, spotPriceMonitor);
		
		int spotRequestsMonitorInterval = Integer.parseInt(properties.getProperty(SPOT_REQUESTS_MONITOR_INTERVAL));
		int vmShutDownDelay = Integer.parseInt(properties.getProperty(SPOT_SHUT_DOWN_DELAY));
		DistributionSettings distributionSettings = DistributionSettings.getDistributionSettings();
		IVMTerminateDistribution vmTerminateDistribution = distributionSettings.getVmTerminateDistribution();
		Monitor spotRequestsMonitor = new CloudSimSpotRequestsMonitor(Monitors.SPOT_REQUESTS_MONITOR, spotRequestsMonitorInterval, vmShutDownDelay, vmTerminateDistribution);
		monitors.put(Monitors.SPOT_REQUESTS_MONITOR, spotRequestsMonitor);
		
		int vmStatusMonitorInterval = Integer.parseInt(properties.getProperty(VM_STATUS_MONITOR_INTERVAL));
		Monitor vmStatusMonitor = new CloudSimVMStatusMonitor(Monitors.VM_STATUS_MONITOR, vmStatusMonitorInterval);
		monitors.put(Monitors.VM_STATUS_MONITOR, vmStatusMonitor);
		
		return monitors;
	}

}
