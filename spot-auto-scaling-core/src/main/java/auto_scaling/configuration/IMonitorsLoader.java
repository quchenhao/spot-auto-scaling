package auto_scaling.configuration;

import java.io.InputStream;
import java.util.Map;

import auto_scaling.monitor.Monitor;

/** 
* @ClassName: IMonitorsLoader 
* @Description: loader to load monitors
* @author Chenhao Qu
* @date 04/06/2015 5:13:02 pm 
*  
*/
public interface IMonitorsLoader {
	static final String	CPU_UTILIZATION_MONITOR_INTERVAL = "cpu_utilization_monitor_interval";
	static final String CPU_UTILIZATION_MONITOR_PERIOD = "cpu_utilization_monitor_period";
	static final String MEMORY_UTILIZATION_MONITOR_INTERVAL = "memory_utilization_monitor_interval";
	static final String MEMORY_UTILIZATION_MONITOR_PERIOD = "memory_utilization_monitor_period";
	static final String BILLING_PERIOD_MONITOR_INTERVAL = "billing_period_monitor_interval";
	static final String BILLING_PERIOD_MONITOR_ENDING_THRESHOLD = "billing_period_monitor_ending_threshold";
	static final String BILLING_PERIOD_SWITCH_MODE_THRESHOLD = "billing_period_monitor_switch_mode_threshold";
	static final String HEALTH_CHECK_MONITOR_INTERVAL = "health_check_monitor_interval";
	static final String SPOT_PRICE_MONITOR_INTERVAL = "spot_price_monitor_interval";
	static final String SPOT_REQUESTS_MONITOR_INTERVAL = "spot_requests_monitor_interval";
	static final String SPOT_REQUESTS_MONITOR_OS = "spot_requests_monitor_os";
	static final String VM_STATUS_MONITOR_INTERVAL = "vm_status_monitor_interval";
	
	/**
	 * @Title: loadMonitors 
	 * @Description: load from input stream
	 * @param inputStream the input stream
	 * @param cloudConfiguration the cloud configuration
	 * @return the monitors
	 * @throws Exception
	 * @throws
	 */
	public Map<String, Monitor> loadMonitors(InputStream inputStream, ICloudConfiguration cloudConfiguration) throws Exception;
}
