package auto_scaling.event;

import java.util.UUID;

/** 
* @ClassName: Event 
* @Description: base class for event
* @author Chenhao Qu
* @date 05/06/2015 9:29:59 pm 
*  
*/
public abstract class Event implements Comparable<Event>{
	/** 
	* @Fields critical_level : the critical level
	*/ 
	protected int critical_level;
	/** 
	* @Fields eventName : the event name
	*/ 
	protected String eventName;
	/** 
	* @Fields eventId : the event id
	*/ 
	protected String eventId;
	/** 
	* <p>Description: initialize with critical level and event name </p> 
	* @param critical_level the critical level
	* @param eventName the event name
	*/
	Event(int critical_level, String eventName) {
		this.critical_level = critical_level;
		this.eventName = eventName;
		this.eventId = UUID.randomUUID().toString();
	}
	

	/* (non-Javadoc) 
	* <p>Title: compareTo</p> 
	* <p>Description: compare base on critical level</p> 
	* @param event
	* @return 
	* @see java.lang.Comparable#compareTo(java.lang.Object) 
	*/
	public int compareTo(Event event) {
		if (this.critical_level > event.critical_level) {
			return -1;
		}
		
		if (this.critical_level == event.critical_level) {
			return 0;
		}
		
		return 1;
	}

	/** 
	* @Title: getCritical_level 
	* @Description: get the critical level
	* @return the critical level
	*/
	public int getCritical_level() {
		return critical_level;
	}
	
	/** 
	* @Title: getEventName 
	* @Description: get the event name
	* @return the event name
	*/
	public String getEventName() {
		return eventName;
	}
	
	/** 
	* @Title: getEventId 
	* @Description: get the event id
	* @return the event id
	*/
	public String getEventId() {
		return eventId;
	}
	
	/* (non-Javadoc) 
	* <p>Title: toString</p> 
	* <p>Description: print event info</p> 
	* @return 
	* @see java.lang.Object#toString() 
	*/
	@Override
	public String toString() {
		return "Event Id: " + eventId + " ; Event Name: " + eventName + " ; critical_level: " + critical_level;
	}
}
