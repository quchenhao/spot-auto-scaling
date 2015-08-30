package auto_scaling.monitor;

import java.util.Arrays;
import java.util.Collection;

/** 
* @ClassName: Metrics 
* @Description: the metrics used in the monitors
* @author Chenhao Qu
* @date 06/06/2015 2:14:25 pm 
*  
*/
public final class Metrics {

	public static final String CPU_UTILIZATION = "CPUUtilization";
	public static final String MEMORY_UTILIZATION = "MemoryUtilization";
	public static final String[] metrics = {CPU_UTILIZATION, MEMORY_UTILIZATION};
	
	/**
	 * @Title: metrics 
	 * @Description: get all the metrics
	 * @return all the metrics
	 * @throws
	 */
	public static Collection<String> metrics() {
		return Arrays.asList(metrics);
	}
}
