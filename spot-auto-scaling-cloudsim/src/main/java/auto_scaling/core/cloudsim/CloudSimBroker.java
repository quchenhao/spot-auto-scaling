package auto_scaling.core.cloudsim;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.ex.MonitoringBorkerEX;
import org.cloudbus.cloudsim.lists.VmList;

import auto_scaling.core.EventProcessor;
import auto_scaling.core.cloudsim.workload.IWorkloadGenerator;
import auto_scaling.monitor.Monitor;
import auto_scaling.util.LogFormatter;

/** 
* @ClassName: CloudSimBroker 
* @Description: the broker that initiate and shut dovms
* @author Chenhao Qu
* @date 05/06/2015 3:59:15 pm 
*  
*/
/** 
* @ClassName: CloudSimBroker 
* @Description: TODO
* @author Chenhao Qu
* @date 06/06/2015 12:24:47 pm 
*  
*/
/** 
* @ClassName: CloudSimBroker 
* @Description: TODO
* @author Chenhao Qu
* @date 06/06/2015 12:24:51 pm 
*  
*/
public class CloudSimBroker extends MonitoringBorkerEX {

	/** 
	* @Fields TIMER_TAG : the tag to submit cloudlets
	*/ 
	protected static final int TIMER_TAG = BROKER_MEASURE_UTIL_NOW + 20;
	/** 
	* @Fields APP_MONITORS : the tag to call monitors
	*/ 
	protected static final int APP_MONITORS = TIMER_TAG + 1;
	/** 
	* @Fields APP_EVENTS : the tag to process events
	*/ 
	protected static final int APP_EVENTS = APP_MONITORS + 1;

	/** 
	* @Fields offset : minimum time interval between events
	*/ 
	protected final double offset = Math.min(1,
			CloudSim.getMinTimeBetweenEvents());
	/** 
	* @Fields monitors : the monitors
	*/ 
	protected Map<Monitor, Double> monitors;

	/** 
	* @Fields stepPeriod : the time interval between cloudlets submission
	*/ 
	protected int stepPeriod;
	/** 
	* @Fields appMonitorsPeriod : the time interval between monitoring operations
	*/ 
	protected int appMonitorsPeriod;
	/** 
	* @Fields appEventPeriod : the time interval between events processing operations
	*/ 
	protected double appEventPeriod;
	/** 
	* @Fields workloadGenerator : the workload generator that generates cloudlet for each step period
	*/ 
	protected IWorkloadGenerator workloadGenerator;
	/** 
	* @Fields eventProcessor : the event processor
	*/ 
	protected EventProcessor eventProcessor;
	/** 
	* @Fields firstVMCreated : whether the first vm is cerated
	*/ 
	protected boolean firstVMCreated;
	/** 
	* @Fields logFormatter : the log formatter
	*/ 
	protected LogFormatter logFormatter;

	/** 
	* @Fields attachedVms : the weight of each vms attached to load balancer
	*/ 
	protected Map<Vm, Integer> attachedVms;

	/** 
	* @Fields cloudSimBrokerLog : the cloudSim log
	*/ 
	protected Logger cloudSimBrokerLog = LogManager
			.getLogger(CloudSimBroker.class);

	/** 
	* <p>Description: initialize with all required parameters</p> 
	* @param name the name of the broker
	* @param lifeLength the life length of the simulation
	* @param monitoringPeriod the monitoring period of the data center
	* @param stepPeriod the interval to submit cloudlets
	* @param appMonitorPeriod the interval to call monitoring modules
	* @param appEventPeriod the interval to call event processing module
	* @param workloadGenerator the workload generator
	* @param monitors the monitors
	* @param eventProcessor the event processor
	* @throws Exception 
	*/
	public CloudSimBroker(String name, double lifeLength, double monitoringPeriod, int stepPeriod,
			int appMonitorPeriod, int appEventPeriod,
			IWorkloadGenerator workloadGenerator, Collection<Monitor> monitors,
			EventProcessor eventProcessor) throws Exception {
		super(name, lifeLength, monitoringPeriod, -1);
		setStepPeriod(stepPeriod);
		setAppMonitorPeriod(appMonitorPeriod);
		setAppEventPeriod(appEventPeriod);
		setWorkloadGenerator(workloadGenerator);
		setMonitors(monitors);
		setEventProcessor(eventProcessor);
		this.logFormatter = LogFormatter.getLogFormatter();
	}

	/**
	 * @Title: setEventProcessor 
	 * @Description: set the event processor
	 * @param eventProcessor the new event processor
	 * @throws
	 */
	public void setEventProcessor(EventProcessor eventProcessor) {
		if (eventProcessor == null) {
			throw new NullPointerException("event provessor cannot be null");
		}
		this.eventProcessor = eventProcessor;
	}

	/**
	 * @Title: setAppEventPeriod 
	 * @Description: set the interval to process events
	 * @param appEventPeriod the interval to process events
	 * @throws
	 */
	public void setAppEventPeriod(int appEventPeriod) {
		if (appEventPeriod <= 0) {
			throw new IllegalArgumentException(
					"app event period shoud be greater than 0");
		}
		this.appEventPeriod = appEventPeriod;
	}

	/**
	 * @Title: setMonitors 
	 * @Description: set the monitors
	 * @param monitors the monitors
	 * @throws
	 */
	private void setMonitors(Collection<Monitor> monitors) {
		if (monitors == null) {
			throw new NullPointerException("monitors cannot be null");
		}

		this.monitors = new HashMap<Monitor, Double>();
		for (Monitor monitor : monitors) {
			this.monitors.put(monitor, new Double(0));
		}
	}

	/**
	 * @Title: setAppMonitorPeriod 
	 * @Description: set the interval to call monitoring modules 
	 * @param appMonitorPeriod the interval to call monitoring modules
	 * @throws
	 */
	public void setAppMonitorPeriod(int appMonitorPeriod) {
		if (appMonitorPeriod <= 0) {
			throw new IllegalArgumentException(
					"app monitor period should be greater than 0");
		}
		this.appMonitorsPeriod = appMonitorPeriod;
	}

	/**
	 * @Title: setWorkloadGenerator 
	 * @Description: set the workload generator
	 * @param workloadGenerator the workload generator
	 * @throws
	 */
	public void setWorkloadGenerator(IWorkloadGenerator workloadGenerator) {
		if (workloadGenerator == null) {
			throw new NullPointerException("workload generator cannot be null");
		}
		this.workloadGenerator = workloadGenerator;
	}

	/**
	 * @Title: setStepPeriod 
	 * @Description: set the interval to submit cloudlets
	 * @param stepPeriod the interval to submit cloudlets
	 * @throws
	 */
	public void setStepPeriod(int stepPeriod) {
		if (stepPeriod <= 0) {
			throw new IllegalArgumentException(
					"step period should be greater than 0");
		}
		this.stepPeriod = stepPeriod;
	}

	/**
	 * @Title: rebalance 
	 * @Description: rebalance the weights of all attached instances
	 * @param weights the weights of all attached instances
	 * @return whether the operation is successful
	 * @throws
	 */
	public boolean rebalance(Map<Integer, Integer> weights) {
		attachedVms = new HashMap<Vm, Integer>();
		for (Integer id : weights.keySet()) {
			Vm vm = getVmById(id);
			if (vm != null) {
				attachedVms.put(vm, weights.get(id));
			}
		}

		return true;
	}

	/**
	 * @Title: getVmById 
	 * @Description: get the vm based on vm id
	 * @param id the id of the vm
	 * @return the vm (null if not found)
	 * @throws
	 */
	public Vm getVmById(int id) {
		for (Vm vm : getVmList()) {
			if (vm.getId() == id) {
				return vm;
			}
		}
		return null;
	}

	/* (non-Javadoc) 
	* <p>Title: processEvent</p> 
	* <p>Description: </p> 
	* @param ev 
	* @see org.cloudbus.cloudsim.ex.MonitoringBorkerEX#processEvent(org.cloudbus.cloudsim.core.SimEvent) 
	*/
	@Override
	public void processEvent(SimEvent ev) {

		//send the kick off simEvents
		if (!isStarted()) {
			send(getId(), offset, TIMER_TAG);
			send(getId(), offset, APP_MONITORS);
			send(getId(), offset, APP_EVENTS);
		}

		super.processEvent(ev);
	}

	/* (non-Javadoc) 
	* <p>Title: processVmCreate</p> 
	* <p>Description: </p> 
	* @param ev 
	* @see org.cloudbus.cloudsim.DatacenterBroker#processVmCreate(org.cloudbus.cloudsim.core.SimEvent) 
	*/
	@Override
	protected void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		if (result == CloudSimTags.TRUE) {
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
			Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": VM #",
					vmId, " has been created in Datacenter #", datacenterId,
					", Host #", VmList.getById(getVmsCreatedList(), vmId)
							.getHost().getId());
		} else {
			Log.printConcatLine(CloudSim.clock(), ": ", getName(),
					": Creation of VM #", vmId, " failed in Datacenter #",
					datacenterId);
		}
	}
	
	/* (non-Javadoc) 
	* <p>Title: createVmsInDatacenter</p> 
	* <p>Description: </p> 
	* @param datacenterId 
	* @see org.cloudbus.cloudsim.DatacenterBroker#createVmsInDatacenter(int) 
	*/
	@Override
	protected void createVmsInDatacenter(int datacenterId) {
		String datacenterName = CloudSim.getEntityName(datacenterId);
		for (Vm vm : getVmList()) {
			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": Trying to Create VM #" + vm.getId()
						+ " in " + datacenterName);
				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
			}
		}

		getDatacenterRequestedIdsList().add(datacenterId);
	}
	
	/* (non-Javadoc) 
	* <p>Title: submitCloudletList</p> 
	* <p>Description: simple implementation for weighted round robin load balancing</p> 
	* @param cloudlets 
	* @see org.cloudbus.cloudsim.DatacenterBroker#submitCloudletList(java.util.List) 
	*/
	@Override
	public void submitCloudletList(List<? extends Cloudlet> cloudlets) {
		
		int size = cloudlets.size();

		int denominator = 0;
		for (Integer weight : attachedVms.values()) {
			denominator += weight;
		}

		double time = CloudSim.clock();
		if (attachedVms.isEmpty()) {
			for (int i = 0; i < size; i++) {
				cloudSimBrokerLog.info(logFormatter.getMessage(time + " FAILED " + 0));
			}
			return;
		}
		
		//split workload according to the weights
		int index = 0;
		for (Vm vm : attachedVms.keySet()) {
			int weight = attachedVms.get(vm);
			int quota = (int) Math.ceil((weight + 0.0) / denominator * size);

			for (int i = 0; i < quota && index < size; i++, index++) {
				Cloudlet cloudlet = cloudlets.get(index);
				cloudlet.setVmId(vm.getId());
				sendNow(getVmsToDatacentersMap().get(vm.getId()),
						CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
				cloudletsSubmitted++;
			}
		}
	}

	
	/* (non-Javadoc) 
	* <p>Title: processCloudletReturn</p> 
	* <p>Description: log the cloudlet results</p> 
	* @param ev 
	* @see org.cloudbus.cloudsim.ex.DatacenterBrokerEX#processCloudletReturn(org.cloudbus.cloudsim.core.SimEvent) 
	*/
	@Override
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		cloudSimBrokerLog.info(logFormatter.getMessage(cloudlet.getSubmissionTime() + " " + cloudlet.getCloudletStatusString() + " " + (cloudlet.getFinishTime() - cloudlet.getSubmissionTime())));
		cloudletsSubmitted--;
	}
	
	/* (non-Javadoc) 
	* <p>Title: destroyVMList</p> 
	* <p>Description: </p> 
	* @param vms 
	* @see org.cloudbus.cloudsim.ex.DatacenterBrokerEX#destroyVMList(java.util.List) 
	*/
	@Override
	public void destroyVMList(final List<? extends Vm> vms) {
       
        for (final Vm vm : vms) {
            if (vm.getHost() == null || vm.getHost().getDatacenter() == null) {
                Log.print("VM " + vm.getId() + " has not been assigned in a valid way and can not be terminated.");
                continue;
            }

            // Update the cloudlets before we send the kill event
            vm.getHost().updateVmsProcessing(CloudSim.clock());

            int datacenterId = vm.getHost().getDatacenter().getId();
            String datacenterName = vm.getHost().getDatacenter().getName();

            Log.printConcatLine(CloudSim.clock(), ": ", getName(), ": Trying to Destroy VM #", vm.getId(), " in ",
                    datacenterName);

            // Tell the data centre to destroy it
            sendNow(datacenterId, CloudSimTags.VM_DESTROY_ACK, vm);
        }
    }
	
	/* (non-Javadoc) 
	* <p>Title: processOtherEvent</p> 
	* <p>Description: process the new tags</p> 
	* @param ev 
	* @see org.cloudbus.cloudsim.ex.MonitoringBorkerEX#processOtherEvent(org.cloudbus.cloudsim.core.SimEvent) 
	*/
	@Override
	protected void processOtherEvent(final SimEvent ev) {
		double time = CloudSim.clock();
		switch (ev.getTag()) {
		case TIMER_TAG:
			//generate and submit workloads
			if (time < getLifeLength()) {
				List<Cloudlet> cloudlets = workloadGenerator
						.generateWorkload(stepPeriod);
				if (cloudlets != null && cloudlets.size() > 0) {
					submitCloudletList(cloudlets);
					send(getId(), stepPeriod, TIMER_TAG);
				} else if (cloudlets != null) {
					send(getId(), stepPeriod, TIMER_TAG);
				} else {
					workloadGenerator.close();
					finishExecution();
				}
			}
			break;
		case APP_MONITORS:
			//do monitoring
			if (time < getLifeLength()) {
				send(getId(), appMonitorsPeriod, APP_MONITORS);
				doAppMonitoring();
			}
			break;
		case APP_EVENTS:
			//do processing events
			if (time < getLifeLength()) {
				send(getId(), appEventPeriod, APP_EVENTS);
				doAppEventProcessing();
			}
			break;
		default:
			super.processOtherEvent(ev);
		}
	}

	/**
	 * @Title: doAppEventProcessing 
	 * @Description: do processing events
	 * @throws
	 */
	protected void doAppEventProcessing() {
		eventProcessor.handleEvents();
	}

	/**
	 * @Title: doAppMonitoring 
	 * @Description: do monitoring
	 * @throws
	 */
	protected void doAppMonitoring() {
		double time = CloudSim.clock();
		for (Monitor monitor : monitors.keySet()) {
			Double lastMonitoringTime = monitors.get(monitor);
			int monitorInterval = monitor.getMonitorInterval();
			// call monitoring when monitoring interval is passed
			if (time >= monitorInterval + lastMonitoringTime) {
				try {
					monitor.doMonitoring();
				} catch (Exception e) {
					
				}
				monitors.put(monitor, time);
			}
		}
	}
}
