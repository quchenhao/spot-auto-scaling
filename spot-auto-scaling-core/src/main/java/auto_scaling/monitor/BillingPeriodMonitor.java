package auto_scaling.monitor;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.configuration.Limits;
import auto_scaling.core.FaultTolerantLevel;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.BillingPeriodMonitor;

/** 
* @ClassName: BillingPeriodMonitor 
* @Description: the monitor that monitors instances' billing time
* @author Chenhao Qu
* @date 06/06/2015 2:05:16 pm 
*  
*/
public class BillingPeriodMonitor extends Monitor{

	/** 
	* @Fields endingThreshold : the threshold that considers the billing period is ending
	*/ 
	protected int endingThreshold;
	/** 
	* @Fields modeSwitchThreshold : the threshold to convert to spot mode from on demand mode
	*/ 
	protected int modeSwitchThreshold;
	
	/** 
	* <p>Description: </p> 
	* @param monitorName the monitor name
	* @param monitorInterval the monitor interval
	* @param endingThreshold the threshold that considers the billing period is ending
	* @param modeSwitchThreshold the threshold to convert to spot mode from on demand mode
	*/
	public BillingPeriodMonitor(String monitorName, int monitorInterval, int endingThreshold, int modeSwitchThreshold) {
		super(monitorName, monitorInterval);
		setEndingThreshold(endingThreshold);
		setModeSwitchThreshold(modeSwitchThreshold);
	}

	/**
	 * @Title: setModeSwitchThreshold 
	 * @Description: set the mode switch threshold
	 * @param modeSwitchThreshold the threshold to convert to spot mode from on demand mode
	 * @throws
	 */
	public void setModeSwitchThreshold(int modeSwitchThreshold) {
		if (modeSwitchThreshold > 50 || modeSwitchThreshold < 30) {
			throw new IllegalArgumentException("mode switch threshold should be between 30 to 50");
		}
		
		this.modeSwitchThreshold = modeSwitchThreshold;
	}

	/* (non-Javadoc) 
	* <p>Title: doMonitoring</p> 
	* <p>Description: </p>  
	* @see auto_scaling.monitor.Monitor#doMonitoring() 
	*/
	@Override
	public synchronized void doMonitoring() {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		Collection<InstanceStatus> allInstances = systemStatus.getAllInstances();
		Limits limits = Limits.getLimits();
		FaultTolerantLevel ftLevel = systemStatus.getFaultTolerantLevel();
		long rightNow = System.currentTimeMillis()/60000;
		
		for (InstanceStatus instanceStatus : allInstances) {
			//simply check the billing hours from the launch time
			if (instanceStatus.getRunningStatus().equals(RunningStatus.RUNNING)) {
				Date launchTime = instanceStatus.getLaunchTime();
				long runningTime = rightNow - launchTime.getTime() / 60000;
				int passingMinutes = (int) (runningTime % 60);
				if (passingMinutes >= endingThreshold || (!systemStatus.isSpotEnabled() && passingMinutes >= modeSwitchThreshold && limits.getMaxChosenSpotTypesNum() > ftLevel.getLevel())) {
					
					monitorLog.info(logFormatter.getMessage("In " + monitorName + " Thread"));
					
					EventGenerator eventGenerator = EventGenerator
							.getEventGenerator();
					Map<String, Object> data = new HashMap<String, Object>();
					data.put(EventDataName.INSTANCE_STATUS, instanceStatus);
					Event newEvent = eventGenerator.generateEvent(Events.INSTANCE_BILLING_PERIOD_ENDING_EVENT, data);
					Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
					eventQueue.add(newEvent);
					
					monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, instanceStatus.getId()));
				}
			}
			
		}
	}

	/**
	 * @Title: getEndingTime 
	 * @Description: get the threshold that considers the billing period is ending
	 * @return the threshold that considers the billing period is ending
	 * @throws
	 */
	public int getEndingTime() {
		return endingThreshold;
	}

	/**
	 * @Title: setEndingThreshold 
	 * @Description: set the threshold that considers the billing period is ending
	 * @param endingThreshold the new threshold that considers the billing period is ending
	 * @throws IllegalArgumentException
	 * @throws
	 */
	public void setEndingThreshold(int endingThreshold) throws IllegalArgumentException{
		if (endingThreshold > 58 || endingThreshold <50) {
			throw new IllegalArgumentException("Ending time should between 50 to 58");
		}
		this.endingThreshold = endingThreshold;
	}
}
