package auto_scaling.monitor.aws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.amazonaws.services.ec2.model.Tag;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.PendingSpotInstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.cloud.SpotInstanceStatus;
import auto_scaling.cloud.aws.AWSSpotRequestStatus;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.core.SystemStatus;
import auto_scaling.event.Event;
import auto_scaling.event.EventDataName;
import auto_scaling.event.EventGenerator;
import auto_scaling.event.EventQueueManager;
import auto_scaling.event.Events;
import auto_scaling.monitor.SpotRequestsMonitor;
import auto_scaling.util.aws.AmazonClient;

/** 
* @ClassName: AWSSpotRequestsMonitor 
* @Description: the spot requests monitor implementation for Amazon AWS
* @author Chenhao Qu
* @date 06/06/2015 2:35:37 pm 
*  
*/
public class AWSSpotRequestsMonitor extends SpotRequestsMonitor {

	/** 
	* @Fields ec2Client : Amazon EC2 client
	*/ 
	protected AmazonEC2Client ec2Client;
	/** 
	* @Fields cloudConfiguration : the cloud configuration
	*/ 
	protected ICloudConfiguration cloudConfiguration;

	/** 
	* <p>Description: </p> 
	* @param cloudConfiguration the cloud configuration
	* @param monitorName the monitor name
	* @param monitorInterval the monitoring interval
	*/
	public AWSSpotRequestsMonitor(ICloudConfiguration cloudConfiguration,
			String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
		this.cloudConfiguration = cloudConfiguration;
		this.ec2Client = AmazonClient.getAmazonEC2Client(cloudConfiguration);
	}

	/* (non-Javadoc) 
	* <p>Title: doMonitoring</p> 
	* <p>Description: </p>  
	* @see auto_scaling.monitor.Monitor#doMonitoring() 
	*/
	@Override
	public synchronized void doMonitoring() {
		SystemStatus systemStatus = SystemStatus.getSystemStatus();

		Collection<String> spotRequestsIds = systemStatus.getSpotRequestsIds();

		if (spotRequestsIds == null || spotRequestsIds.isEmpty()) {
			return;
		}
		
		DescribeSpotInstanceRequestsRequest describeRequest = new DescribeSpotInstanceRequestsRequest();
		describeRequest.setSpotInstanceRequestIds(spotRequestsIds);

		DescribeSpotInstanceRequestsResult describeResult = ec2Client
				.describeSpotInstanceRequests(describeRequest);
		List<SpotInstanceRequest> describeResponses = describeResult
				.getSpotInstanceRequests();
		List<InstanceStatus> markedTerminatingInstances = new ArrayList<InstanceStatus>();
		List<InstanceStatus> closedSpotRequestsBeforeFullfillment = new ArrayList<InstanceStatus>();
		boolean anyFailed = false;
		Map<String, SpotInstanceRequest> instanceIdsToSpotRequests = new HashMap<String, SpotInstanceRequest>();
		for (SpotInstanceRequest spotRequest : describeResponses) {
			String state = spotRequest.getState();
			com.amazonaws.services.ec2.model.SpotInstanceStatus status = spotRequest
					.getStatus();
			String statusCode = status.getCode();
			
			InstanceStatus instanceStatus = systemStatus
					.getSpotInstanceStatusByRequestId(spotRequest
							.getSpotInstanceRequestId());

			if (state.equals(AWSSpotRequestStatus.STATE_OPEN)) {
				continue;
			} else if (state.equals(AWSSpotRequestStatus.STATE_CANCELED)
					|| state.equals(AWSSpotRequestStatus.STATE_FAILED)
					|| state.equals(AWSSpotRequestStatus.STATE_CLOSED)) {
				anyFailed = true;
				monitorLog.warn(logFormatter.getMessage(state + " " + statusCode));
				
				if (statusCode
						.equals(AWSSpotRequestStatus.STATUS_MARK_FOR_TERMINATION) || statusCode.equals(AWSSpotRequestStatus.STATUS_INSTANCE_TERMINATED_BY_PRICE)) {
					markedTerminatingInstances.add(instanceStatus);
					monitorLog.warn(logFormatter.getMessage("provider terminated: " +
							instanceStatus.getId()));
				} else {
					closedSpotRequestsBeforeFullfillment.add(instanceStatus);
					monitorLog.warn(logFormatter.getMessage("spot request closed: "
							+ spotRequest.getSpotInstanceRequestId() + " "
							+ state + " " + statusCode));
				}

			} else if (state.equals(AWSSpotRequestStatus.STATE_ACTIVE)) {
				if (instanceStatus instanceof PendingSpotInstanceStatus) {
					String instanceId = spotRequest.getInstanceId();
					instanceIdsToSpotRequests.put(instanceId, spotRequest);
				}
			} else {
				monitorLog.error(logFormatter.getMessage("unknown spot request state: " + state));
			}
		}

		//if there are spot requests closed before fullfillment, fire a spot requests closed before fullfillment event
		if (closedSpotRequestsBeforeFullfillment.size() > 0) {
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(EventDataName.CLOSED_SPOT_REQUESTS, closedSpotRequestsBeforeFullfillment);
			Event newEvent = eventGenerator.generateEvent(Events.SPOT_REQUESTS_CLOSED_BEFORE_FULLFILLMENT_EVENT, data);
			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);
			
			String message = "closed spot requests:";
			for (InstanceStatus instanceStatus : closedSpotRequestsBeforeFullfillment) {
				message += " {" + instanceStatus.toString() + "};";
			}
			
			monitorLog.info(logFormatter.getGenerateEventLogString(newEvent, message));
		}

		//if there are instances terminated by provider, fire a spot instances termination event
		if (markedTerminatingInstances.size() > 0) {
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(EventDataName.TERMINATING_SPOT_INSTANCES,
					markedTerminatingInstances);
			Event newEvent = eventGenerator.generateEvent(
					Events.SPOT_INSTANCES_TERMINATION_EVENT, data);
			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);

			String message = "spot terminating instances:";
			for (InstanceStatus instanceStatus : markedTerminatingInstances) {
				message += " {" + instanceStatus.toString() + "};";
			}

			monitorLog.info(logFormatter.getGenerateEventLogString(newEvent,
					message));
		}

		//if there are spot requests fullfilled, update the instance
		if (instanceIdsToSpotRequests.size() > 0) {
			updateSystemStatus(instanceIdsToSpotRequests);
		}

		//if there are vms failed, fire an about to scale up event
		if (anyFailed) {
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			Event newEvent = eventGenerator.generateEvent(
					Events.ABOUT_TO_SCALE_UP_EVENT, data);
			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);

			monitorLog.info(logFormatter.getGenerateEventLogString(newEvent,
					null));
		}
	}

	/**
	 * @Title: updateSystemStatus 
	 * @Description: update system status after fullfillment
	 * @param instanceIdsToSpotRequests the map from instance ids to spot requests
	 * @throws
	 */
	private void updateSystemStatus(
			Map<String, SpotInstanceRequest> instanceIdsToSpotRequests) {
		DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
		describeInstancesRequest.setInstanceIds(instanceIdsToSpotRequests
				.keySet());

		DescribeInstancesResult describeInstancesResult = ec2Client
				.describeInstances(describeInstancesRequest);

		List<Reservation> reservations = describeInstancesResult
				.getReservations();
		SystemStatus systemStatus = SystemStatus.getSystemStatus();

		List<InstanceStatus> runningInstances = new ArrayList<InstanceStatus>();

		for (Reservation reservation : reservations) {
			for (Instance instance : reservation.getInstances()) {
				String instanceId = instance.getInstanceId();
				String publicUrl = instance.getPublicDnsName();
				String privateUrl = instance.getPrivateDnsName();
				Date launchTime = instance.getLaunchTime();
				String type = instance.getInstanceType();
				InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager
						.getInstanceTemplateManager();
				InstanceTemplate instanceTemplate = instanceTemplateManager
						.getInstanceTemplate(type, cloudConfiguration.getOS());
				String runningStatus = instance.getState().getName();

				SpotInstanceRequest spotRequest = instanceIdsToSpotRequests
						.get(instanceId);
				String requestId = spotRequest.getSpotInstanceRequestId();
				
				SpotInstanceStatus pendingInstanceStatus = (SpotInstanceStatus)systemStatus.getSpotInstanceStatusByRequestId(requestId);
				double biddingPrice = pendingInstanceStatus.getBiddingPrice();

				CreateTagsRequest createTagsRequest = new CreateTagsRequest();
				Collection<String> idCollection = new ArrayList<String>();
				Collection<Tag> tags = new ArrayList<Tag>();
				tags.add(new Tag("Name", cloudConfiguration.getNameTag() + "-"
						+ instanceId));
				idCollection.add(instanceId);
				createTagsRequest.setResources(idCollection);
				createTagsRequest.setTags(tags);
				ec2Client.createTags(createTagsRequest);

				InstanceStatus instanceStatus = new SpotInstanceStatus(
						instanceId, requestId, publicUrl, privateUrl,
						launchTime, instanceTemplate, biddingPrice);
				instanceStatus.setRunningStatus(runningStatus);

				synchronized (systemStatus) {
					systemStatus.removeInstance(pendingInstanceStatus);
					systemStatus.addInstance(instanceStatus);
				}

				if (runningStatus.equals(RunningStatus.RUNNING)) {
					runningInstances.add(instanceStatus);
				}

				monitorLog.info(logFormatter.getMessage("spot instance launched: "
						+ instanceStatus.toString()));
			}
		}

		if (runningInstances.size() > 0) {
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put(EventDataName.ONLINE_INSTANCES, runningInstances);
			Event newEvent = eventGenerator.generateEvent(
					Events.INSTANCES_ONLINE_EVENT, data);
			Queue<Event> eventQueue = EventQueueManager.getEventsQueue();
			eventQueue.add(newEvent);

			String instances = "";
			for (InstanceStatus instanceStatus : runningInstances) {
				instances += " " + instanceStatus.getId();
			}

			monitorLog.info(logFormatter.getGenerateEventLogString(newEvent,
					instances));
		}
	}

}
