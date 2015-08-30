package auto_scaling.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate; 
import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.cloud.SpotInstanceStatus;
import auto_scaling.util.InstanceFilter;

/** 
* @ClassName: SystemStatus 
* @Description: the system status of the current provision
* @author Chenhao Qu
* @date 05/06/2015 3:06:01 pm 
*  
*/
public class SystemStatus {

	/** 
	* @Fields systemStatus : the global system status
	*/ 
	private static SystemStatus systemStatus;

	/** 
	* @Fields allInstances : all the instances in the system
	*/ 
	protected Set<InstanceStatus> allInstances;
	/** 
	* @Fields onDemandInstances : all the on demand instances
	*/ 
	protected Set<InstanceStatus> onDemandInstances;

	/** 
	* @Fields spotInstances : all the spot instances
	*/ 
	protected Set<InstanceStatus> spotInstances;
	
	/** 
	* @Fields spotGroups : the spot groups
	*/ 
	protected Map<InstanceTemplate, Set<InstanceStatus>> spotGroups;
	
	/** 
	* @Fields orphans : spot instances not belonging to any spot group
	*/ 
	protected Queue<InstanceStatus> orphans;

	/** 
	* @Fields faultTolerantLevel : current fault tolerant level
	*/ 
	protected FaultTolerantLevel faultTolerantLevel;

	/** 
	* @Fields totalNumOfRequests : total estimated requests rate
	*/ 
	protected long totalNumOfRequests;

	/** 
	* @Fields systemStatusLog : system status log
	*/ 
	protected Logger systemStatusLog = LogManager.getLogger(SystemStatus.class);

	/** 
	* @Fields isSpotEnabled : is spot mode on
	*/ 
	protected boolean isSpotEnabled;
	
	/** 
	* @Fields overLoaded : whether the application is overloaded
	*/ 
	protected boolean overLoaded;

	/** 
	* <p>Description: empty initialization</p>  
	*/
	protected SystemStatus() {
		allInstances = new HashSet<InstanceStatus>();
		onDemandInstances = new HashSet<InstanceStatus>();
		spotInstances = new HashSet<InstanceStatus>();
		spotGroups = new HashMap<InstanceTemplate, Set<InstanceStatus>>();
		orphans = new LinkedList<InstanceStatus>();
		faultTolerantLevel = FaultTolerantLevel.ONE;
		isSpotEnabled = false;
		overLoaded = false;
	}

	/**
	 * @Title: initialize 
	 * @Description: initialize
	 * @param subClass the class extends system status
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws
	 */
	public static void initialize(Class<? extends SystemStatus> subClass)
			throws InstantiationException, IllegalAccessException {

		systemStatus = subClass.newInstance();
	}

	/**
	 * @Title: snapshot 
	 * @Description: copy a collection
	 * @param collection the collection needs to copy
	 * @return the copied collection
	 * @throws
	 */
	private Collection<InstanceStatus> snapshot(
			Collection<InstanceStatus> collection) {
		return new ArrayList<InstanceStatus>(collection);
	}

	/**
	 * @Title: getAllInstances 
	 * @Description: get all instances
	 * @return all instances
	 * @throws
	 */
	public synchronized Collection<InstanceStatus> getAllInstances() {
		return snapshot(allInstances);
	}

	/**
	 * @Title: getOnDemandInstances 
	 * @Description: get all on demand instances
	 * @return all on demand instances
	 * @throws
	 */
	public synchronized Collection<InstanceStatus> getOnDemandInstances() {
		return snapshot(onDemandInstances);
	}
	
	/**
	 * @Title: getSpotInstances 
	 * @Description: get all spot instances
	 * @return all spot instances
	 * @throws
	 */
	public synchronized Collection<InstanceStatus> getSpotInstances() {
		return snapshot(spotInstances);
	}

	/**
	 * @Title: getChosenSpotTypes 
	 * @Description: get the chosen spot types
	 * @return the chosen spot types
	 * @throws
	 */
	public synchronized Collection<InstanceTemplate> getChosenSpotTypes() {
		return new ArrayList<InstanceTemplate>(spotGroups.keySet());
	}

	/**
	 * @Title: getSystemStatus 
	 * @Description: get the system status
	 * @return the system status
	 * @throws
	 */
	public static SystemStatus getSystemStatus() {
		if (systemStatus == null) {
			throw new NullPointerException(
					"SystemStatus hasn't been initialized");
		}

		return systemStatus;
	}

	/**
	 * @Title: dumpStatus 
	 * @Description: print current status
	 * @return the info of current status
	 * @throws
	 */
	public synchronized String dumpStatus() {
		String dump = "";

		for (InstanceStatus instanceStatus : allInstances) {
			dump += instanceStatus.toString() + "\n";
		}
		
		return dump;
	}

	/**
	 * @Title: addInstance 
	 * @Description: add a new instance
	 * @param instanceStatus the new instance
	 * @throws IllegalArgumentException
	 * @throws
	 */
	public synchronized void addInstance(InstanceStatus instanceStatus)
			throws IllegalArgumentException {
		if (instanceStatus == null) {
			throw new IllegalArgumentException("null");
		}

		if (instanceStatus instanceof SpotInstanceStatus) {
			InstanceTemplate type = instanceStatus.getType();
			Set<InstanceStatus> group = spotGroups.get(type);
			if (group == null) {
				orphans.add(instanceStatus);
			}
			else {
				group.add(instanceStatus);
			}
			spotInstances.add(instanceStatus);
			
		} else if (instanceStatus instanceof OnDemandInstanceStatus) {
			onDemandInstances.add(instanceStatus);
		} else {
			throw new IllegalArgumentException(instanceStatus.getClass()
					.getName());
		}

		allInstances.add(instanceStatus);
	}

	/**
	 * @Title: removeInstance 
	 * @Description: remove the instance
	 * @param instanceStatus the instance to remove
	 * @throws IllegalArgumentException
	 * @throws
	 */
	public synchronized void removeInstance(InstanceStatus instanceStatus)
			throws IllegalArgumentException {
		
		if (instanceStatus == null) {
			throw new IllegalArgumentException("null");
		}
		
		if (orphans.contains(instanceStatus)) {
			orphans.remove(instanceStatus);
			spotInstances.remove(instanceStatus);
		}
		else if (instanceStatus instanceof SpotInstanceStatus) {
			InstanceTemplate type = instanceStatus.getType();
			Set<InstanceStatus> group = spotGroups.get(type);
			
			if (group != null) {
				if (group.contains(instanceStatus)) {
					group.remove(instanceStatus);
				}
			}
			else {
				for (Set<InstanceStatus> tempGroup : spotGroups.values()) {
					if (tempGroup.contains(instanceStatus)) {
						tempGroup.remove(instanceStatus);
						break;
					}
				}
			}
			spotInstances.remove(instanceStatus);
			
		} else if (instanceStatus instanceof OnDemandInstanceStatus) {
			onDemandInstances.remove(instanceStatus);
		} else {
			throw new IllegalArgumentException(instanceStatus.getClass()
					.getName());
		}

		allInstances.remove(instanceStatus);

	}

	/**
	 * @Title: getTotalNumOfRequests 
	 * @Description: get most recent estimated request rate 
	 * @return most recent estimated request rate
	 * @throws
	 */
	public synchronized long getTotalNumOfRequests() {
		return totalNumOfRequests;
	}
	
	/**
	 * @Title: setTotalNumOfRequests 
	 * @Description: set estimated request rate
	 * @param totalNumOfRequests new estimated request rate
	 * @throws
	 */
	public synchronized void setTotalNumOfRequests(long totalNumOfRequests) {
		if (totalNumOfRequests < 0) {
			throw new IllegalArgumentException("total number of requests must be greater than 0");
		}
		this.totalNumOfRequests = totalNumOfRequests;
	}

	/**
	 * @Title: getInstanceStatus 
	 * @Description: get the instance by id
	 * @param instanceId the id of the instance
	 * @return the instance (null if not found)
	 * @throws
	 */
	public synchronized InstanceStatus getInstanceStatusByInstanceId(String instanceId) {
		if (instanceId == null) {
			throw new NullPointerException("instance id cannot be null");
		}
		Collection<InstanceStatus> instances = getAllInstances();
		for (InstanceStatus instanceStatus : instances) {
			if (instanceStatus.getId() != null && instanceStatus.getId().equals(instanceId)) {
				return instanceStatus;
			}
		}

		return null;
	}

	/**
	 * @Title: getFaultTolerantLevel 
	 * @Description: get current fault tolerant level
	 * @return current fault tolerant level
	 * @throws
	 */
	public synchronized FaultTolerantLevel getFaultTolerantLevel() {
		return faultTolerantLevel;
	}

	/**
	 * @Title: setFaultTolerantLevel 
	 * @Description: set fault tolerant level
	 * @param faultTolerantLevel the new fault tolerant level
	 * @throws
	 */
	public synchronized void setFaultTolerantLevel(
			FaultTolerantLevel faultTolerantLevel) {
		if (faultTolerantLevel == null) {
			throw new NullPointerException("fault tolerant level cannot be null");
		}
		
		this.faultTolerantLevel = faultTolerantLevel;
	}

	/**
	 * @Title: getConfirmedInstancesIds 
	 * @Description: get all confirmed instances' ids
	 * @return all confirmed instances' ids
	 * @throws
	 */
	public synchronized Collection<String> getConfirmedInstancesIds() {
		List<String> instanceIds = new ArrayList<String>();
		Collection<InstanceStatus> confirmedInstances = InstanceFilter.getConfirmedInstances(allInstances);

		for (InstanceStatus instanceStatus : confirmedInstances) {
			instanceIds.add(instanceStatus.getId());
		}

		return instanceIds;
	}

	/**
	 * @Title: getRunningInstancesIds 
	 * @Description: get all running instances' ids
	 * @return all running instances' ids
	 * @throws
	 */
	public synchronized Collection<String> getRunningInstancesIds() {
		List<String> instanceIds = new ArrayList<String>();
		Collection<InstanceStatus> runningInstances = InstanceFilter.getRunningInstances(allInstances);

		for (InstanceStatus instanceStatus : runningInstances) {
			instanceIds.add(instanceStatus.getId());
		}

		return instanceIds;
	}

	/**
	 * @Title: getSpotInstanceStatusByRequestId 
	 * @Description: get spot instances by spot request id
	 * @param spotInstanceRequestId the spot request id
	 * @return the spot instance (null if not found)
	 * @throws
	 */
	public synchronized InstanceStatus getSpotInstanceStatusByRequestId(
			String spotInstanceRequestId) {
		if (spotInstanceRequestId == null) {
			throw new NullPointerException("spot request id cannot be null");
		}
		Collection<InstanceStatus> instances = getSpotInstances();
		for (InstanceStatus instance : instances) {
			SpotInstanceStatus instanceStatus = (SpotInstanceStatus) instance;
			if (instanceStatus.getSpotRequestId().equals(spotInstanceRequestId)) {
				return instanceStatus;
			}
		}

		return null;
	}

	/**
	 * @Title: removeChosenSpotType 
	 * @Description: remove the chosen spot type
	 * @param type the spot type wants to remove
	 * @throws
	 */
	public synchronized void removeChosenSpotType(InstanceTemplate type) {
		if (isChosen(type)) {
			Set<InstanceStatus> removedTypes = spotGroups.remove(type);
			orphans.addAll(removedTypes);
		}
	}

	/**
	 * @Title: removeChosenSpotTypes 
	 * @Description: remove multiple spot types
	 * @param types the spot types wants to remove
	 * @throws
	 */
	public synchronized void removeChosenSpotTypes(Collection<InstanceTemplate> types) {
		for (InstanceTemplate instanceTemplate : types) {
			removeChosenSpotType(instanceTemplate);
		}
	}
	
	/**
	 * @Title: addChosenSpotType 
	 * @Description: add one spot type if not chosen
	 * @param type the spot type wants to choose
	 * @throws
	 */
	public synchronized void addChosenSpotType(InstanceTemplate type) {
		if (spotGroups.containsKey(type)) {
			return;
		}
		Set<InstanceStatus> group = new HashSet<InstanceStatus>();
		
		if (hasOrphan()) {
			List<InstanceStatus> orphansToMove = null;
			for (InstanceStatus instanceStatus : orphans) {
				if (instanceStatus.getType().equals(type)) {
					if (orphansToMove == null) {
						orphansToMove = new ArrayList<InstanceStatus>();
					}
					orphansToMove.add(instanceStatus);
				}
			}
			if (orphansToMove != null) {
				orphans.removeAll(orphansToMove);
				group.addAll(orphansToMove);
			}
		}
		
		
		for (Entry<InstanceTemplate, Set<InstanceStatus>> entry : spotGroups.entrySet()) {
			Set<InstanceStatus> set = entry.getValue();
			if (set.size() > 0) {
				List<InstanceStatus> instancesToMove = null;
				for (InstanceStatus instanceStatus : set) {
					InstanceTemplate instanceTemplate = instanceStatus.getType();
					if (instanceTemplate.equals(type)) {
						if (instancesToMove == null) {
							instancesToMove = new ArrayList<InstanceStatus>();
						}
						instancesToMove.add(instanceStatus);
					}
				}
				if (instancesToMove != null) {
					set.removeAll(instancesToMove);
					group.addAll(instancesToMove);
				}
			}
		}
		
		spotGroups.put(type, group);
	}

	/**
	 * @Title: addChosenSpotTypes 
	 * @Description: all multiple spot types if not chosen
	 * @param types the spot types wants to choose
	 * @throws
	 */
	public synchronized void addChosenSpotTypes(Collection<InstanceTemplate> types) {
		for (InstanceTemplate instanceTemplate : types) {
			addChosenSpotType(instanceTemplate);
		}
	}
	
	/**
	 * @Title: handOver 
	 * @Description: replace on chosen spot type with another unchosen spot type
	 * @param oldChosenType the old spot type
	 * @param newChosenType the new unchosen spot type
	 * @throws
	 */
	public synchronized void handOver(InstanceTemplate oldChosenType, InstanceTemplate newChosenType) {
		if (!spotGroups.containsKey(oldChosenType)) {
			throw new IllegalArgumentException(oldChosenType.toString() + " not chosen");
		}
		
		if(spotGroups.containsKey(newChosenType)) {
			throw new IllegalArgumentException(newChosenType.toString() + " already chosen");
		}
		
		Set<InstanceStatus> instances = spotGroups.remove(oldChosenType);
		spotGroups.put(newChosenType, instances);
	}
	
	/**
	 * @Title: getNumOfChosenSpotTypes 
	 * @Description: get the num of chosen spot types
	 * @return the num of chosen spot types
	 * @throws
	 */
	public synchronized int getNumOfChosenSpotTypes() {
		return spotGroups.size();
	}

	/**
	 * @Title: isSpotEnabled 
	 * @Description: get whether is in spot mode
	 * @return is in spot mode
	 * @throws
	 */
	public synchronized boolean isSpotEnabled() {
		return isSpotEnabled;
	}

	/**
	 * @Title: enableSpot 
	 * @Description: switch to spot mode
	 * @throws
	 */
	public synchronized void enableSpot() {
		isSpotEnabled = true;
	}
	
	/**
	 * @Title: disableSpot 
	 * @Description: swith to on demand mode
	 * @throws
	 */
	public synchronized void disableSpot() {
		isSpotEnabled = false;
		for (Set<InstanceStatus> temp : spotGroups.values()) {
			orphans.addAll(temp);
		}
		
		spotGroups.clear();
	}

	/**
	 * @Title: isChosen 
	 * @Description: check whether the spot type is chosen
	 * @param instanceTemplate the spot type
	 * @return whether the spot type is chosen
	 * @throws
	 */
	public synchronized boolean isChosen(InstanceTemplate instanceTemplate) {
		return spotGroups.containsKey(instanceTemplate);
	}
	
	/**
	 * @Title: moveOrphanToType 
	 * @Description: assign orphan in the spot type
	 * @param type the spot type to move to
	 * @return whether the operation is successful
	 * @throws
	 */
	public synchronized boolean moveOrphanToType(InstanceTemplate type) {
		Set<InstanceStatus> group = spotGroups.get(type);
		if (group == null) {
			throw new IllegalArgumentException("non chosen spot type " + type.toString());
		}
		
		if (hasOrphan()) {
			InstanceStatus instanceStatus = orphans.poll();
			group.add(instanceStatus);
			return true;
		}
		return false;
	}
	
	/**
	 * @Title: hasOrphan 
	 * @Description: current system status has any orphan or not
	 * @return whether current system has any orphan or not
	 * @throws
	 */
	public synchronized boolean hasOrphan() {
		return !orphans.isEmpty();
	}
	
	/**
	 * @Title: getNominalChosenSpotGroup 
	 * @Description: get all instances can be potential capacity in a spot group
	 * @param instanceTemplate the spot group
	 * @return all instances can be potential capacity in a spot group
	 * @throws
	 */
	public synchronized Collection<InstanceStatus> getNominalChosenSpotGroup(InstanceTemplate instanceTemplate) {
		Set<InstanceStatus> group = spotGroups.get(instanceTemplate);
		if (group != null) {
			return InstanceFilter.getNominalInstances(snapshot(group));
		}
		
		return null;
	}
	
	/**
	 * @Title: getChosenSpotGroup 
	 * @Description: get all instances in a spot group
	 * @param instanceTemplate the spot group
	 * @return all instances in a spot group
	 * @throws
	 */
	public synchronized Collection<InstanceStatus> getChosenSpotGroup(InstanceTemplate instanceTemplate) {
		Set<InstanceStatus> group = spotGroups.get(instanceTemplate);
		if (group != null) {
			return snapshot(group);
		}
		
		return null;
	}

	/**
	 * @Title: isOrphan 
	 * @Description: check whether the instance is orphan
	 * @param instanceStatus
	 * @return whether the instance is orphan
	 * @throws
	 */
	public synchronized boolean isOrphan(InstanceStatus instanceStatus) {
		return orphans.contains(instanceStatus);
	}

	/**
	 * @Title: inGroup 
	 * @Description: which spot group the spot instance is in
	 * @param instanceStatus the spot instance
	 * @return
	 * @throws
	 */
	public synchronized InstanceTemplate inGroup(InstanceStatus instanceStatus) {
		InstanceTemplate instanceTemplate = instanceStatus.getType();
		if (isChosen(instanceTemplate)) {
			return instanceTemplate;
		}
		
		for (Entry<InstanceTemplate, Set<InstanceStatus>> entry : spotGroups.entrySet()) {
			Set<InstanceStatus> group = entry.getValue();
			if (group.contains(instanceStatus)) {
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	/**
	 * @Title: getNominalOnDemandInstances 
	 * @Description: get all on demand instances can be potential capacity
	 * @return all on demand instances can be potential capacity
	 * @throws
	 */
	public synchronized Collection<InstanceStatus> getNominalOnDemandInstances() {
		return InstanceFilter.getNominalInstances(new ArrayList<InstanceStatus>(onDemandInstances));
	}
	
	/**
	 * @Title: getNominalSpotGroups 
	 * @Description: get all instances in spot groups that can be potential capacity
	 * @return all instances in spot groups that can be potential capacity
	 * @throws
	 */
	public synchronized Map<InstanceTemplate, Collection<InstanceStatus>> getNominalSpotGroups() {
		Map<InstanceTemplate, Collection<InstanceStatus>> nominalSpotGroupsMap = new HashMap<InstanceTemplate, Collection<InstanceStatus>>();
		Map<InstanceTemplate, Set<InstanceStatus>> spotGroups = getSpotGroups();
		for (Entry<InstanceTemplate, Set<InstanceStatus>> entry : spotGroups.entrySet()) {
			nominalSpotGroupsMap.put(entry.getKey(), InstanceFilter.getNominalInstances(entry.getValue()));
		}
		return nominalSpotGroupsMap;
	}
	
	/**
	 * @Title: getSpotGroups 
	 * @Description: get all instances in spot groups
	 * @return all instances in spot groups
	 * @throws
	 */
	public synchronized Map<InstanceTemplate, Set<InstanceStatus>> getSpotGroups() {
		HashMap<InstanceTemplate, Set<InstanceStatus>> cloneGroups = new HashMap<InstanceTemplate, Set<InstanceStatus>>();
		for (Entry<InstanceTemplate, Set<InstanceStatus>> entry : spotGroups.entrySet()) {
			cloneGroups.put(entry.getKey(), new HashSet<InstanceStatus>(entry.getValue()));
		}
		return cloneGroups;
	}

	/**
	 * @Title: getAvailableCapacity 
	 * @Description: get the number of current capacity
	 * @return the number of current capacity
	 * @throws
	 */
	public synchronized long getAvailableCapacity() {
		FaultTolerantLevel ftLevel = FaultTolerantLevel.ZERO;
		if (isSpotEnabled()) {
			ftLevel = this.faultTolerantLevel;
		}
		
		long capacity = 0;
		Collection<InstanceStatus> attachedInstances = InstanceFilter.getAttachedInstances(allInstances);
		for (InstanceStatus instanceStatus : attachedInstances) {
			
			InstanceTemplate instanceTemplate = instanceStatus.getType();
			if (instanceStatus instanceof OnDemandInstanceStatus) {
				capacity += instanceTemplate.getCapacity(FaultTolerantLevel.ZERO);
			}
			else {
				capacity += instanceTemplate.getCapacity(ftLevel);
			}
		}
		return capacity;
	}

	/**
	 * @Title: getMaximumAvaliableCapacity 
	 * @Description: get the number of maximum capacity
	 * @return the number of maximum capacity
	 * @throws
	 */
	public synchronized long getMaximumAvaliableCapacity() {
		long capacity = 0;
		Collection<InstanceStatus> attachedInstances = InstanceFilter.getAttachedInstances(allInstances);
		for (InstanceStatus instanceStatus : attachedInstances) {
			
			InstanceTemplate instanceTemplate = instanceStatus.getType();
			capacity += instanceTemplate.getMaximunCapacity();
		}
		return capacity;
	}
	
	/**
	 * @Title: getNominalCapacity 
	 * @Description: get the number of capacity if all nominal instances are online
	 * @return
	 * @throws
	 */
	public synchronized long getNominalCapacity() {
		FaultTolerantLevel ftLevel = FaultTolerantLevel.ZERO;
		if (isSpotEnabled()) {
			ftLevel = this.faultTolerantLevel;
		}
		
		long capacity = 0;
		Collection<InstanceStatus> nominalInstances = InstanceFilter.getNominalInstances(allInstances);
		
		for (InstanceStatus instanceStatus : nominalInstances) {
			InstanceTemplate instanceTemplate = instanceStatus.getType();
			capacity += instanceTemplate.getCapacity(ftLevel);
		}
		return capacity;
	}

	/**
	 * @Title: getNumOfAttachedInstances 
	 * @Description: get the number of instances attached to the load balancer
	 * @return
	 * @throws
	 */
	public synchronized int getNumOfAttachedInstances() {
		Collection<InstanceStatus> attachedInstances = InstanceFilter.getAttachedInstances(allInstances);
		return attachedInstances.size();
	}
	
	/**
	 * @Title: getSpotRequestsIds 
	 * @Description: get spot request ids for all spot instances
	 * @return
	 * @throws
	 */
	public synchronized Collection<String> getSpotRequestsIds() {
		Collection<InstanceStatus> spotInstances = getSpotInstances();
		List<String> requestsIds = new ArrayList<String>();
		for (InstanceStatus instanceStatus : spotInstances) {
			SpotInstanceStatus spotInstanceStatus = (SpotInstanceStatus)instanceStatus;
			requestsIds.add(spotInstanceStatus.getSpotRequestId());
		}
		
		return requestsIds;
	}

	/**
	 * @Title: removeInstances 
	 * @Description: remove multiple instances at once
	 * @param instances the instances wants to remove
	 * @throws
	 */
	public synchronized void removeInstances(Collection<InstanceStatus> instances) {
		for (InstanceStatus instanceStatus : instances) {
			removeInstance(instanceStatus);
		}
	}

	/**
	 * @Title: isOverLoaded 
	 * @Description: get whether the application is overloaded
	 * @return whether the application is overloaded
	 * @throws
	 */
	public synchronized boolean isOverLoaded() {
		return overLoaded;
	}

	
	/**
	 * @Title: setOverLoaded 
	 * @Description: set whether the application is overloaded
	 * @param overLoaded whether the application is overloaded
	 * @throws
	 */
	public synchronized void setOverLoaded(boolean overLoaded) {
		this.overLoaded = overLoaded;
	}
}
