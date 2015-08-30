package auto_scaling.handler.cloudsim;

import org.cloudbus.cloudsim.core.CloudSim;

import auto_scaling.handler.SpotPriceUpdateEventHandler;

/** 
* @ClassName: CloudSimSpotPriceUpdateEventHandler 
* @Description: the spot price update event handler implementation for cloudSim
* @author Chenhao Qu
* @date 06/06/2015 1:43:17 pm 
*  
*/
public class CloudSimSpotPriceUpdateEventHandler extends SpotPriceUpdateEventHandler{
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public CloudSimSpotPriceUpdateEventHandler() {
		lastOptimized = (long)CloudSim.clock();
	}
	
	/* (non-Javadoc) 
	* <p>Title: isOptimizationIntervalPassed</p> 
	* <p>Description: check with simulation time</p> 
	* @return 
	* @see auto_scaling.handler.SpotPriceUpdateEventHandler#isOptimizationIntervalPassed() 
	*/
	@Override
	protected boolean isOptimizationIntervalPassed() {
		long currentTime = (long)CloudSim.clock();
		if (currentTime - lastOptimized >= interval) {
			lastOptimized = currentTime;
			return true;
		}
		return false;
	}
}
