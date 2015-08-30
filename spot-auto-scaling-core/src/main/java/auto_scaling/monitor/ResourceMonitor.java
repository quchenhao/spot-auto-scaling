package auto_scaling.monitor;

/** 
* @ClassName: ResourceMonitor 
* @Description: the monitor that monitors resource consumption of instances
* @author Chenhao Qu
* @date 06/06/2015 2:21:32 pm 
*  
*/
public abstract class ResourceMonitor extends Monitor{

	/** 
	* @Fields metricName : the monitored metric name
	*/ 
	protected String metricName;
	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param metric the metric name
	* @param monitorInterval the monitor interval
	*/
	public ResourceMonitor(String monitorName, String metric, int monitorInterval) {
		super(monitorName, monitorInterval);
		this.metricName = metric;
	}
}
