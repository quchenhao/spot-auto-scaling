package auto_scaling.configuration;

import java.io.InputStream;
import java.util.Map;

import auto_scaling.capacity.ICapacityCalculator;
import auto_scaling.handler.EventHandler;
import auto_scaling.monitor.Monitor;
import auto_scaling.online.IOnlineTask;

/** 
* @ClassName: IEventHandlersLoader 
* @Description: loader to load event handlers
* @author Chenhao Qu
* @date 04/06/2015 5:04:28 pm 
*  
*/
public interface IEventHandlersLoader {

	static final String SPOT_PRICE_UPDATE_EVENT_HANDLER_INTERVAL = "spot_price_update_event_handler_interval";
	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param cloudConfiguration the cloud configuration
	 * @param capacityCalculator the capacity calculator
	 * @param scalingPoliciesConfiguration the scaling policies configuration
	 * @param monitors the monitors
	 * @param onlineTask the online tasks
	 * @param inputSteram the input stream
	 * @return the event handlers
	 * @throws Exception
	 * @throws
	 */
	public Map<String, EventHandler> load(ICloudConfiguration cloudConfiguration, ICapacityCalculator capacityCalculator, IScalingPoliciesConfiguration scalingPoliciesConfiguration, Map<String, Monitor> monitors, IOnlineTask onlineTask, InputStream inputSteram) throws Exception;
}
