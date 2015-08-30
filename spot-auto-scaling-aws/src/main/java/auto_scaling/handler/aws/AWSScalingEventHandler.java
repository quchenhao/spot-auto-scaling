package auto_scaling.handler.aws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateChange;
import com.amazonaws.services.ec2.model.LaunchSpecification;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.RequestSpotInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.amazonaws.services.ec2.model.SpotPlacement;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.cloud.PendingSpotInstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.configuration.UnsupportedConfigurationException;
import auto_scaling.configuration.aws.AWSConfiguration;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.ScalingEvent;
import auto_scaling.handler.ScalingEventHandler;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.scaling_strategy.StartOnDemandRequest;
import auto_scaling.scaling_strategy.ScalingPlan;
import auto_scaling.scaling_strategy.StartSpotRequest;
import auto_scaling.scaling_strategy.TerminateVMsRequest;
import auto_scaling.util.aws.AmazonClient;

/** 
* @ClassName: AWSScalingEventHandler 
* @Description: the scaling event handler implementation for Amazon AWS 
* @author Chenhao Qu
* @date 06/06/2015 1:35:55 pm 
*  
*/
public class AWSScalingEventHandler extends ScalingEventHandler {
	
	/** 
	* @Fields ec2Client : the Amazon EC2 client
	*/ 
	protected AmazonEC2Client ec2Client;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	public AWSScalingEventHandler() {
		super();
	}

	/* (non-Javadoc) 
	* <p>Title: setCloudConfiguration</p> 
	* <p>Description: </p> 
	* @param cloudConfiguration 
	* @see auto_scaling.handler.ScalingEventHandler#setCloudConfiguration(auto_scaling.configuration.ICloudConfiguration) 
	*/
	@Override
	public void setCloudConfiguration(ICloudConfiguration cloudConfiguration) {
		super.setCloudConfiguration(cloudConfiguration);
		this.ec2Client = AmazonClient.getAmazonEC2Client(cloudConfiguration);
	}
	
	/* (non-Javadoc) 
	* <p>Title: doHandling</p> 
	* <p>Description: </p> 
	* @param event 
	* @see auto_scaling.handler.EventHandler#doHandling(auto_scaling.event.Event) 
	*/
	@Override
	protected synchronized void doHandling(Event event) {
		//first start vms then shut down vms
		ScalingEvent scalingEvent = (ScalingEvent) event;
		
		ScalingPlan scalingPlan = scalingEvent.getScalingPlan();
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		synchronized (systemStatus) {
			Collection<StartSpotRequest> spotRequests = scalingPlan.getStartSpotRequests();
			
			if (spotRequests != null && spotRequests.size() > 0) {
				try {
					createSpotInstances(spotRequests);
				} catch (UnsupportedConfigurationException e) {
					eventHandlerLog.error(logFormatter.getExceptionString(e));
				}
			}
			
			StartOnDemandRequest onDemandRequest = scalingPlan.getStartOnDemandRequest();
			if (onDemandRequest != null && onDemandRequest.getNum() > 0) {
				try {
					createOnDemandInstances(onDemandRequest);
				} catch (UnsupportedConfigurationException e) {
					eventHandlerLog.error(logFormatter.getExceptionString(e));
				}
			}
			
			TerminateVMsRequest terminateVMsRequest = scalingPlan.getTerminateVMsRequest();
			
			if (terminateVMsRequest != null && terminateVMsRequest.getNum() > 0) {
				terminateInstances(terminateVMsRequest);
				eventHandlerLog.info(logFormatter.getMessage("total available capacity: " + systemStatus.getAvailableCapacity()));
			}
			
			eventHandlerLog.info(logFormatter.getMessage("system status:\n" + systemStatus.dumpStatus()));
			eventHandlerLog.info(logFormatter.getMessage("total nominal capacity: " + systemStatus.getNominalCapacity()));
			
		}
		
		
	}

	/**
	 * @Title: terminateInstances 
	 * @Description: terminate instances
	 * @param terminateVMsRequest the terminate VMs request
	 * @throws
	 */
	private void terminateInstances(TerminateVMsRequest terminateVMsRequest) {
		
		LoadBalancer loadBalancer = LoadBalancer.getLoadBalancer();
		try {
			loadBalancer.detach(terminateVMsRequest.getTerminatingInstances());
		} catch (IOException e) {
			eventHandlerLog.error(logFormatter.getExceptionString(e));
		}
		
		TerminateInstancesRequest request = new TerminateInstancesRequest();
		request.setInstanceIds(terminateVMsRequest.getTerminatingInstancesIds());
		
		TerminateInstancesResult result = ec2Client.terminateInstances(request);
		List<InstanceStateChange> stateChanges = result.getTerminatingInstances();
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		for (InstanceStateChange stateChange : stateChanges) {
			String id = stateChange.getInstanceId();
			InstanceStatus instanceStatus = systemStatus.getInstanceStatusByInstanceId(id);
			InstanceState state = stateChange.getCurrentState();
			String runningStatus = state.getName();
			if (runningStatus.equals(RunningStatus.TERMINATED) || runningStatus.equals(RunningStatus.STOPPED) ) {
				systemStatus.removeInstance(instanceStatus);
				eventHandlerLog.info(logFormatter.getMessage("terminated or stopped: " + id));
			}
			else if (runningStatus.equals(RunningStatus.SHUTTING_DOWN)  || runningStatus.equals(RunningStatus.STOPPING)) {
				instanceStatus.setRunningStatus(RunningStatus.SHUTTING_DOWN);
				eventHandlerLog.info(logFormatter.getMessage("shutting down or stopping: " + id));
			}
			else {
				eventHandlerLog.error(logFormatter.getMessage("failed to shut down: " + id));
			}
		}
	}

	/**
	 * @Title: createOnDemandInstances 
	 * @Description: create on demand instances
	 * @param onDemandRequest the start on demand request
	 * @throws UnsupportedConfigurationException
	 * @throws
	 */
	private void createOnDemandInstances(StartOnDemandRequest onDemandRequest) throws UnsupportedConfigurationException {
		InstanceTemplate instanceTemplate = onDemandRequest.getInstanceTemplate();
		RunInstancesRequest request = new RunInstancesRequest();
		request.setInstanceType(instanceTemplate.getName());
		if (instanceTemplate.isSupportHvm()) {
			request.setImageId(cloudConfiguration.getHvmImageId());
		}
		else if (instanceTemplate.isSupportParavirtual()) {
			request.setImageId(cloudConfiguration.getParavirtualImageId());
		}
		else {
			throw new UnsupportedConfigurationException("no virtualization method supported");
		}
		request.setMinCount(onDemandRequest.getNum());
		request.setMaxCount(onDemandRequest.getNum());
		request.setKeyName(cloudConfiguration.getKeyName());
		request.setSecurityGroupIds(cloudConfiguration.getSecurityGroups());
		
		Placement placement = new Placement();
		placement.setAvailabilityZone(cloudConfiguration.getAvailabilityZone());
		
		request.setPlacement(placement);
		
		RunInstancesResult result = ec2Client.runInstances(request);
		
		Reservation reservation = result.getReservation();
		
		List<Instance> instances = reservation.getInstances();
		
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		
		for (Instance instance : instances) {
			String id = instance.getInstanceId();
			String publicUrl = instance.getPublicDnsName();
			String privateUrl = instance.getPrivateDnsName();
			Date launchTime = instance.getLaunchTime();
			InstanceState runngingStatus = instance.getState();
			
			CreateTagsRequest createTagsRequest = new CreateTagsRequest();
			Collection<String> instanceId = new ArrayList<String>();
			Collection<Tag> tags = new ArrayList<Tag>();
			tags.add(new Tag("Name", cloudConfiguration.getNameTag() + "-" + id));
			instanceId.add(id);
			createTagsRequest.setResources(instanceId);
			createTagsRequest.setTags(tags);
			ec2Client.createTags(createTagsRequest);
			
			InstanceStatus instanceStatus = new OnDemandInstanceStatus(id, publicUrl, privateUrl, launchTime, instanceTemplate);
			instanceStatus.setRunningStatus(runngingStatus.getName());
			
			systemStatus.addInstance(instanceStatus);
			eventHandlerLog.info(logFormatter.getMessage("create on demand instance: " + instanceStatus.toString()));
		}
	}

	/**
	 * @Title: createSpotInstances 
	 * @Description: create spot instances
	 * @param spotRequests the lists of start spot requests
	 * @throws UnsupportedConfigurationException
	 * @throws
	 */
	private void createSpotInstances(Collection<StartSpotRequest> spotRequests) throws UnsupportedConfigurationException {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		for (StartSpotRequest spotRequest : spotRequests) {
			InstanceTemplate instanceTemplate = spotRequest.getInstanceTemplate();
			RequestSpotInstancesRequest spotInstanceRequest = new RequestSpotInstancesRequest();
			spotInstanceRequest.setSpotPrice(spotRequest.getBiddingPrice() + "");
			spotInstanceRequest.setInstanceCount(spotRequest.getNum());
			
			LaunchSpecification launchSpecification = new LaunchSpecification();
			if (instanceTemplate.isSupportHvm()) {
				launchSpecification.setImageId(cloudConfiguration.getHvmImageId());
			}
			else if (instanceTemplate.isSupportParavirtual()) {
				launchSpecification.setImageId(cloudConfiguration.getParavirtualImageId());
			}
			else {
				throw new UnsupportedConfigurationException("no virutalizion method supported");
			}
			launchSpecification.setInstanceType(instanceTemplate.getName());
			launchSpecification.setSecurityGroups(cloudConfiguration.getSecurityGroups());
			launchSpecification.setKeyName(cloudConfiguration.getKeyName());
			launchSpecification.setMonitoringEnabled(((AWSConfiguration)cloudConfiguration).isMonitoringEnabled());
			launchSpecification.setEbsOptimized(((AWSConfiguration)cloudConfiguration).isEBSOptimized());
			
			SpotPlacement placement = new SpotPlacement();
			placement.setAvailabilityZone(cloudConfiguration.getAvailabilityZone());
			
			launchSpecification.setPlacement(placement);
			
			spotInstanceRequest.setLaunchSpecification(launchSpecification);
			
			RequestSpotInstancesResult requestResult = ec2Client.requestSpotInstances(spotInstanceRequest);
			
			List<SpotInstanceRequest> results = requestResult.getSpotInstanceRequests();
			
			for (SpotInstanceRequest request : results) {
				String id = request.getSpotInstanceRequestId();
				double biddingPrice = spotRequest.getBiddingPrice();
				PendingSpotInstanceStatus peindingSpotInstance = new PendingSpotInstanceStatus(id, instanceTemplate, biddingPrice);
				systemStatus.addInstance(peindingSpotInstance);
				eventHandlerLog.info(logFormatter.getMessage("create spot request: " + peindingSpotInstance.toString()));
			}
		}
	}

}
