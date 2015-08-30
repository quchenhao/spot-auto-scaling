package auto_scaling.event;

/** 
* @ClassName: SpotPriceUpdateEvent 
* @Description: the event indicates the spot market prices are updated
* @author Chenhao Qu
* @date 05/06/2015 9:55:56 pm 
*  
*/
public class SpotPriceUpdateEvent extends Event{

	/** 
	* <p>Description: </p> 
	* @param critical_level the critical level
	*/
	public SpotPriceUpdateEvent(int critical_level) {
		super(critical_level, Events.SPOT_PRICE_UPDATE_EVENT);
	}

}
