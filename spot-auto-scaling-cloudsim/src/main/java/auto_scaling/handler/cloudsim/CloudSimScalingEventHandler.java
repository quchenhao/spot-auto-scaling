package auto_scaling.handler.cloudsim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.ex.util.Id;
import org.cloudbus.cloudsim.ex.vm.MonitoredVMex;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.cloud.PendingSpotInstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.cloud.cloudsim.CloudSimPendingSpotInstanceStatus;
import auto_scaling.cloud.cloudsim.ISpotRequestFullfillmentDistribution;
import auto_scaling.cloud.cloudsim.IVMTerminateDistribution;
import auto_scaling.core.SystemStatus;
import auto_scaling.core.cloudsim.ApplicationBrokerManager;
import auto_scaling.core.cloudsim.CloudSimBroker;
import auto_scaling.event.Event;
import auto_scaling.event.ScalingEvent;
import auto_scaling.handler.ScalingEventHandler;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.scaling_strategy.ScalingPlan;
import auto_scaling.scaling_strategy.StartOnDemandRequest;
import auto_scaling.scaling_strategy.StartSpotRequest;
import auto_scaling.scaling_strategy.TerminateVMsRequest;
import auto_scaling.util.cloudsim.TimeConverter;
import auto_scaling.util.cloudsim.VmFactory;

/** 
* @ClassName: CloudSimScalingEventHandler 
* @Description: the scaling event handler for cloudSim implementation
* @author Chenhao Qu
* @date 06/06/2015 1:38:57 pm 
*  
*/
public class CloudSimScalingEventHandler extends ScalingEventHandler {

	/** 
	* @Fields spotRequestFullfillmentDistribution : the spot request fullfillment time distribution
	*/ 
	protected ISpotRequestFullfillmentDistribution spotRequestFullfillmentDistribution;
	/** 
	* @Fields vmTerminateDistribution : the vm termination time distribution
	*/ 
	protected IVMTerminateDistribution vmTerminateDistribution;

	/** 
	* <p>Description: empty initialization</p>  
	*/
	public CloudSimScalingEventHandler() {
	}

	/**
	 * @Title: setSpotRequestFullfillmentDistribution 
	 * @Description: set the spot request fullfillment time distribution
	 * @param spotRequestFullfillmentDistribution the new spot request fullfillment time distribution
	 * @throws
	 */
	public void setSpotRequestFullfillmentDistribution(
			ISpotRequestFullfillmentDistribution spotRequestFullfillmentDistribution) {
		if (spotRequestFullfillmentDistribution == null) {
			throw new NullPointerException(
					"spot request fullfillment distribution cannot be null");
		}
		this.spotRequestFullfillmentDistribution = spotRequestFullfillmentDistribution;
	}

	/**
	 * @Title: setVMTerminateDistribution 
	 * @Description: set the vm termination time distribution
	 * @param vmTerminateDistribution the new vm termination time distribution
	 * @throws
	 */
	public void setVMTerminateDistribution(
			IVMTerminateDistribution vmTerminateDistribution) {
		if (vmTerminateDistribution == null) {
			throw new NullPointerException(
					"vm terminate distribution cannot be null");
		}
		this.vmTerminateDistribution = vmTerminateDistribution;
	}

	/* (non-Javadoc) 
	* <p>Title: doHandling</p> 
	* <p>Description: </p> 
	* @param event 
	* @see auto_scaling.handler.EventHandler#doHandling(auto_scaling.event.Event) 
	*/
	@Override
	protected void doHandling(Event event) {
		//first start vms then shut down vms
		SystemStatus systemStatus = SystemStatus.getSystemStatus();

		ScalingEvent scalingEvent = (ScalingEvent) event;
		ScalingPlan scalingPlan = scalingEvent.getScalingPlan();

		Collection<StartSpotRequest> spotRequests = scalingPlan
				.getStartSpotRequests();

		if (spotRequests != null && spotRequests.size() > 0) {
			createSpotInstances(spotRequests);
		}

		StartOnDemandRequest onDemandRequest = scalingPlan
				.getStartOnDemandRequest();

		if (onDemandRequest != null && onDemandRequest.getNum() > 0) {
			createOnDemandInstances(onDemandRequest);
		}

		TerminateVMsRequest terminateVMsRequest = scalingPlan
				.getTerminateVMsRequest();

		if (terminateVMsRequest != null && terminateVMsRequest.getNum() > 0) {
			terminateInstances(terminateVMsRequest);
			eventHandlerLog.info(logFormatter
					.getMessage("total available capacity: "
							+ systemStatus.getAvailableCapacity()));
		}

		eventHandlerLog.info(logFormatter.getMessage("system status:\n"
				+ systemStatus.dumpStatus()));
		eventHandlerLog.info(logFormatter.getMessage("total nominal capacity: "
				+ systemStatus.getNominalCapacity()));
	}

	/**
	 * @Title: terminateInstances 
	 * @Description: terminate instances
	 * @param terminateVMsRequest the terminate vms request
	 * @throws
	 */
	private void terminateInstances(TerminateVMsRequest terminateVMsRequest) {

		Collection<InstanceStatus> terminatingInstances = terminateVMsRequest
				.getTerminatingInstances();

		LoadBalancer loadBalancer = LoadBalancer.getLoadBalancer();
		try {
			loadBalancer.detach(terminatingInstances);
		} catch (IOException e) {
			eventHandlerLog.error(logFormatter.getExceptionString(e));
		}

		ApplicationBrokerManager applicationBrokerManager = ApplicationBrokerManager
				.getApplicationBrokerManager();
		CloudSimBroker cloudSimBroker = applicationBrokerManager
				.getCloudSimBroker();

		for (InstanceStatus instanceStatus : terminatingInstances) {
			InstanceTemplate instanceTemplate = instanceStatus.getType();
			int id = Integer.parseInt(instanceStatus.getId());
			Vm vm = cloudSimBroker.getVmById(id);
			instanceStatus.setRunningStatus(RunningStatus.SHUTTING_DOWN);
			List<Vm> vmList = new ArrayList<Vm>();
			vmList.add(vm);
			cloudSimBroker.destroyVMsAfter(vmList,
					vmTerminateDistribution.getDelay(instanceTemplate));
			eventHandlerLog.info(logFormatter
					.getMessage("shutting down or stopping: " + id));
			// System.out.println("shutting down or stopping: " + id + " " +
			// CloudSim.clock());
		}
	}

	private void createOnDemandInstances(StartOnDemandRequest onDemandRequest) {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();

		InstanceTemplate instanceTemplate = onDemandRequest
				.getInstanceTemplate();
		int num = onDemandRequest.getNum();

		double time = CloudSim.clock();
		Date startTime = TimeConverter.convertSimulationTimeToDate(time);
		VmFactory vmFactory = VmFactory.getVmFactory();
		List<MonitoredVMex> vms = vmFactory.getVMs(instanceTemplate, num);
		for (MonitoredVMex vm : vms) {
			String id = vm.getId() + "";
			OnDemandInstanceStatus onDemandInstanceStatus = new OnDemandInstanceStatus(
					id, null, null, startTime, instanceTemplate);
			onDemandInstanceStatus.setRunningStatus(RunningStatus.PENDING);
			systemStatus.addInstance(onDemandInstanceStatus);
			eventHandlerLog.info(logFormatter
					.getMessage("create on demand instance: "
							+ onDemandInstanceStatus.toString()));
//			System.out.println("create on demand instance: " +
//			onDemandInstanceStatus.toString() + " " + CloudSim.clock());
		}

		ApplicationBrokerManager applicationBrokerManager = ApplicationBrokerManager
				.getApplicationBrokerManager();
		CloudSimBroker cloudSimBroker = applicationBrokerManager
				.getCloudSimBroker();
		cloudSimBroker.createVmsAfter(vms, 0);
	}

	private void createSpotInstances(Collection<StartSpotRequest> spotRequests) {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		double startTime = CloudSim.clock();
		for (StartSpotRequest spotRequest : spotRequests) {
			InstanceTemplate instanceTemplate = spotRequest
					.getInstanceTemplate();
			double biddingPrice = spotRequest.getBiddingPrice();
			int num = spotRequest.getNum();
			for (int i = 0; i < num; i++) {
				long waitingTime = spotRequestFullfillmentDistribution
						.getDelay(instanceTemplate);
				CloudSimPendingSpotInstanceStatus cloudSimPendingSpotInstanceStatus = new CloudSimPendingSpotInstanceStatus(
						Id.pollId(PendingSpotInstanceStatus.class) + "",
						instanceTemplate, biddingPrice, waitingTime, startTime);
				systemStatus.addInstance(cloudSimPendingSpotInstanceStatus);
				eventHandlerLog
						.info(logFormatter.getMessage("create spot request: "
								+ cloudSimPendingSpotInstanceStatus.toString()));
			}
			 
		}

	}

}
