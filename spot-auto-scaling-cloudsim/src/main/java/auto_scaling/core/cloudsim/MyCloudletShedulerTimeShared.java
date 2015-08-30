package auto_scaling.core.cloudsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;

/** 
* @ClassName: MyCloudletShedulerTimeShared 
* @Description: the cloudlet time scheduler time shared
* @author Chenhao Qu
* @date 06/06/2015 1:30:10 pm 
*  
*/
public class MyCloudletShedulerTimeShared extends CloudletSchedulerTimeShared{

	/** 
	* @Fields timeout : the timeout of the cloudlet
	*/ 
	protected double timeout;
	
	/** 
	* <p>Description: </p> 
	* @param timeout the timeout of the cloudlet
	*/
	public MyCloudletShedulerTimeShared(double timeout) {
		this.timeout = timeout;
	}
	
	/* (non-Javadoc) 
	* <p>Title: updateVmProcessing</p> 
	* <p>Description: </p> 
	* @param currentTime
	* @param mipsShare
	* @return 
	* @see org.cloudbus.cloudsim.CloudletSchedulerTimeShared#updateVmProcessing(double, java.util.List) 
	*/
	@Override
	public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
		
		setCurrentMipsShare(mipsShare);
		double timeSpam = currentTime - getPreviousTime();

		double capacity = getCapacity(mipsShare);
		for (ResCloudlet rcl : getCloudletExecList()) {
			rcl.updateCloudletFinishedSoFar((long) (capacity * timeSpam * rcl.getNumberOfPes() * Consts.MILLION));
		}

		if (getCloudletExecList().size() == 0) {
			setPreviousTime(currentTime);
			return 0.0;
		}

		// check finished cloudlets
		double nextEvent = Double.MAX_VALUE;
		List<ResCloudlet> toRemove = new ArrayList<ResCloudlet>();
		for (ResCloudlet rcl : getCloudletExecList()) {
			long remainingLength = rcl.getRemainingCloudletLength();
			if (remainingLength == 0) {// finished: remove from the list
				toRemove.add(rcl);
				cloudletFinish(rcl);
				continue;
			}
		}
		
		getCloudletExecList().removeAll(toRemove);

		toRemove = new ArrayList<ResCloudlet>();
		double timeOutPoint = timeout + currentTime;
		capacity = getCapacity(mipsShare);
		// estimate finish time of cloudlets
		for (ResCloudlet rcl : getCloudletExecList()) {
			double estimatedFinishTime = currentTime
					+ (rcl.getRemainingCloudletLength() / (capacity * rcl.getNumberOfPes()));
			if (estimatedFinishTime - currentTime < CloudSim.getMinTimeBetweenEvents()) {
				estimatedFinishTime = currentTime + CloudSim.getMinTimeBetweenEvents();
			}

			//remove timeout cloudlets
			if (estimatedFinishTime > timeOutPoint) {
				toRemove.add(rcl);
				cloudletFailed(rcl);
				continue;
			}
			
			if (estimatedFinishTime < nextEvent) {
				nextEvent = estimatedFinishTime;
			}
		}
		
		getCloudletExecList().removeAll(toRemove);
		
		setPreviousTime(currentTime);
		return nextEvent;
	}
	
	
	/**
	 * @Title: cloudletFailed 
	 * @Description: update failed cloudlets
	 * @param rcl the resCloudlet
	 * @throws
	 */
	public void cloudletFailed(ResCloudlet rcl) {
		rcl.setCloudletStatus(Cloudlet.FAILED);
		rcl.finalizeCloudlet();
		getCloudletFinishedList().add(rcl);
	}
}
