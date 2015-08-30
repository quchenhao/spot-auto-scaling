package auto_scaling.event;

/** 
* @ClassName: AboutToScaleUpEvent 
* @Description: the event to indicate scaling up
* @author Chenhao Qu
* @date 05/06/2015 9:28:12 pm 
*  
*/
public class AboutToScaleUpEvent extends Event {

	/** 
	* <p>Description: initialize with specified critical level</p> 
	* @param critical_level the specified critical level
	*/
	public AboutToScaleUpEvent(int critical_level) {
		super(critical_level, Events.ABOUT_TO_SCALE_UP_EVENT);
	}

}
