package auto_scaling.event;

/** 
* @ClassName: UnrecoganizedCriticalLevel 
* @Description: 
* @author Chenhao Qu
* @date 05/06/2015 10:02:18 pm 
*  
*/
@SuppressWarnings("serial")
public class UnrecoganizedCriticalLevel extends Exception {

	/** 
	* <p>Description: </p> 
	* @param eventLevel the unrecognized critival level
	*/
	public UnrecoganizedCriticalLevel(String eventLevel) {
		super("Unrecoganized critical level: " + eventLevel);
	}
}
