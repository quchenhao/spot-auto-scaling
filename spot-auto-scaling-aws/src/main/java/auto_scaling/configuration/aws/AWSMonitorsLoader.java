package auto_scaling.configuration.aws;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import auto_scaling.cloud.UnSupportedResourceException;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.configuration.IMonitorsLoader;
import auto_scaling.monitor.BillingPeriodMonitor;
import auto_scaling.monitor.Metrics;
import auto_scaling.monitor.Monitor;
import auto_scaling.monitor.Monitors;
import auto_scaling.monitor.aws.AWSCloudWatchResourceMonitor;
import auto_scaling.monitor.aws.AWSHealthCheckMonitor;
import auto_scaling.monitor.aws.AWSSpotPriceMonitor;
import auto_scaling.monitor.aws.AWSSpotRequestsMonitor;
import auto_scaling.monitor.aws.AWSVMStatusMonitor;

/** 
* @ClassName: AWSMonitorsLoader 
* @Description: loaders to load monitors for Amazon AWS
* @author Chenhao Qu
* @date 05/06/2015 2:18:49 pm 
*  
*/
public class AWSMonitorsLoader implements IMonitorsLoader {

	/* (non-Javadoc) 
	* <p>Title: loadMonitors</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @param cloudConfiguration
	* @return
	* @throws IOException
	* @throws UnSupportedResourceException 
	* @see auto_scaling.configuration.IMonitorsLoader#loadMonitors(java.io.InputStream, auto_scaling.configuration.ICloudConfiguration) 
	*/
	@Override
	public Map<String, Monitor> loadMonitors(InputStream inputStream, ICloudConfiguration cloudConfiguration) throws IOException, UnSupportedResourceException{
		Properties properties = new Properties();
		properties.load(inputStream);
		
		List<String> statistics = new ArrayList<String>();
		statistics.add("Average");
		
		Map<String, Monitor> monitors = new HashMap<String, Monitor>();
		
		int cpuUtilizationMonitorInterval = Integer.parseInt(properties.getProperty(CPU_UTILIZATION_MONITOR_INTERVAL));
		int cpuUtilizationMonitorPeriod = Integer.parseInt(properties.getProperty(CPU_UTILIZATION_MONITOR_PERIOD));
		Monitor cpuUtilizationMonitor = new AWSCloudWatchResourceMonitor(cloudConfiguration, Monitors.CPU_UTILIZATION_MONITOR, Metrics.CPU_UTILIZATION, "AWS/EC2", statistics, cpuUtilizationMonitorInterval, cpuUtilizationMonitorPeriod);
		monitors.put(Monitors.CPU_UTILIZATION_MONITOR, cpuUtilizationMonitor);
		
		int memoryUtilizationMonitorInterval = Integer.parseInt(properties.getProperty(MEMORY_UTILIZATION_MONITOR_INTERVAL));
		int memoryUtilizationMonitorPeriod = Integer.parseInt(properties.getProperty(MEMORY_UTILIZATION_MONITOR_PERIOD));
		Monitor memoryUtilizationMonitor = new AWSCloudWatchResourceMonitor(cloudConfiguration, Monitors.MEMORY_UTILIZATION_MONITOR, Metrics.MEMORY_UTILIZATION, "System/Linux", statistics, memoryUtilizationMonitorInterval, memoryUtilizationMonitorPeriod);
		monitors.put(Monitors.MEMORY_UTILIZATION_MONITOR, memoryUtilizationMonitor);
		
		int billingPeriodMonitorInterval = Integer.parseInt(properties.getProperty(BILLING_PERIOD_MONITOR_INTERVAL));
		int billingPeriodMonitorEndingThreshold = Integer.parseInt(properties.getProperty(BILLING_PERIOD_MONITOR_ENDING_THRESHOLD));
		int billingPeriodMonitorSwithModeThreshold = Integer.parseInt(properties.getProperty(BILLING_PERIOD_SWITCH_MODE_THRESHOLD));
		Monitor billingPeriodMonitor = new BillingPeriodMonitor(Monitors.BILLING_PERIOD_MONITOR, billingPeriodMonitorInterval, billingPeriodMonitorEndingThreshold, billingPeriodMonitorSwithModeThreshold);
		monitors.put(Monitors.BILLING_PERIOD_MONITOR, billingPeriodMonitor);
		
		int healthCheckMonitorInterval = Integer.parseInt(properties.getProperty(HEALTH_CHECK_MONITOR_INTERVAL));
		Monitor healthCheckMonitor = new AWSHealthCheckMonitor(cloudConfiguration, Monitors.HEALTH_CHECK_MONITOR, healthCheckMonitorInterval);
		monitors.put(Monitors.HEALTH_CHECK_MONITOR, healthCheckMonitor);
		
		int spotPriceMonitorInterval = Integer.parseInt(properties.getProperty(SPOT_PRICE_MONITOR_INTERVAL));
		Monitor spotPriceMonitor = new AWSSpotPriceMonitor(cloudConfiguration, Monitors.SPOT_PRICE_MONITOR, spotPriceMonitorInterval);
		monitors.put(Monitors.SPOT_PRICE_MONITOR, spotPriceMonitor);
		
		int spotRequestsMonitorInterval = Integer.parseInt(properties.getProperty(SPOT_REQUESTS_MONITOR_INTERVAL));
		Monitor spotRequestsMonitor = new AWSSpotRequestsMonitor(cloudConfiguration, Monitors.SPOT_REQUESTS_MONITOR, spotRequestsMonitorInterval);
		monitors.put(Monitors.SPOT_REQUESTS_MONITOR, spotRequestsMonitor);
		
		int vmStatusMonitorInterval = Integer.parseInt(properties.getProperty(VM_STATUS_MONITOR_INTERVAL));
		Monitor vmStatusMonitor = new AWSVMStatusMonitor(cloudConfiguration, Monitors.VM_STATUS_MONITOR, vmStatusMonitorInterval);
		monitors.put(Monitors.VM_STATUS_MONITOR, vmStatusMonitor);
		
		return monitors;
	}

}
