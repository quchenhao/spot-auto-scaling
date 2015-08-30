package auto_scaling.cloud.cloudsim;

import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: ISpotRequestFullfillmentDistribution 
* @Description: the distribution for spot request fillfillment time
* @author Chenhao Qu
* @date 04/06/2015 1:54:22 pm 
*  
*/
public interface ISpotRequestFullfillmentDistribution {

	/**
	 * @Title: getDelay 
	 * @Description: get spot request fullfillment delay
	 * @param instanceTemplate
	 * @return spot request fullfillment delay
	 * @throws
	 */
	public long getDelay(InstanceTemplate instanceTemplate);
}
