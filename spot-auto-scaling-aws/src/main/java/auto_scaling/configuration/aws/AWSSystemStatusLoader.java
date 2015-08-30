package auto_scaling.configuration.aws;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.amazonaws.services.ec2.model.Tag;

import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.cloud.PendingSpotInstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.cloud.SpotInstanceStatus;
import auto_scaling.cloud.aws.AWSSpotRequestStatus;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.configuration.ISystemStatusLoader;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.core.SystemStatus;
import auto_scaling.util.aws.AmazonClient;

/** 
* @ClassName: AWSSystemStatusLoader 
* @Description: loader to load inital system status for Amazon AWS
* @author Chenhao Qu
* @date 05/06/2015 2:19:28 pm 
*  
*/
public class AWSSystemStatusLoader implements ISystemStatusLoader{

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param cloudConfiguration
	* @throws Exception 
	* @see auto_scaling.configuration.ISystemStatusLoader#load(auto_scaling.configuration.ICloudConfiguration) 
	*/
	@Override
	public void load(ICloudConfiguration cloudConfiguration) throws Exception {
		SystemStatus.initialize(SystemStatus.class);
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		systemStatus.disableSpot();
		
		AmazonEC2Client ec2Client = AmazonClient.getAmazonEC2Client(cloudConfiguration);
		
		DescribeSpotInstanceRequestsRequest describeSpotRequest = new DescribeSpotInstanceRequestsRequest();
		
		DescribeSpotInstanceRequestsResult describeSpotResult = ec2Client.describeSpotInstanceRequests(describeSpotRequest);
		
		Collection<SpotInstanceRequest> spotRequests = describeSpotResult.getSpotInstanceRequests();
		Collection<SpotInstanceRequest> runningSpotRequests = new ArrayList<SpotInstanceRequest>();
		
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
		
		for (SpotInstanceRequest spotInstanceRequest : spotRequests) {
			String state = spotInstanceRequest.getState();
			if (state.equals(AWSSpotRequestStatus.STATE_OPEN)) {
				String requestId = spotInstanceRequest.getSpotInstanceRequestId();
				InstanceTemplate type = instanceTemplateManager.getInstanceTemplate(spotInstanceRequest.getType(), cloudConfiguration.getOS());
				double biddingPrice = Double.parseDouble(spotInstanceRequest.getSpotPrice());
				PendingSpotInstanceStatus pendingSpotInstanceStatus = new PendingSpotInstanceStatus(requestId, type, biddingPrice);
				systemStatus.addInstance(pendingSpotInstanceStatus);
			}
			else if (state.equals(AWSSpotRequestStatus.STATE_ACTIVE)) {
				runningSpotRequests.add(spotInstanceRequest);
			}
		}
		
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		DescribeInstancesResult result = ec2Client.describeInstances(request);
		
		String OS = cloudConfiguration.getOS();
		
		for (Reservation reservation : result.getReservations()) {
			for (Instance instance : reservation.getInstances()) {
				InstanceState state = instance.getState();
				String runningState = state.getName();
				
				if (isInAutoScaling(instance, cloudConfiguration) && runningState.equals(RunningStatus.RUNNING)) {
					InstanceStatus instanceStatus = null;
					if (instance.getInstanceLifecycle() != null) {
						String spotRequestId = instance.getSpotInstanceRequestId();
						SpotInstanceRequest spotInstanceRequest = getSpotInstanceRequest(spotRequestId, runningSpotRequests);
						if (spotInstanceRequest != null) {
							instanceStatus = getSpotInsatnceStatus(instance, spotInstanceRequest, OS);
							systemStatusLoaderLogger.info("load spot instance: " + instanceStatus.toString());
						}
					}
					else {
						instanceStatus = getOnDemandInstanceStatus(instance, OS);
						systemStatusLoaderLogger.info("load on demand instance: " + instanceStatus.toString());
					}
					
					if (instanceStatus != null) {
						systemStatus.addInstance(instanceStatus);
					}
				}
				
			}
		}
	}

	/**
	 * @Title: getOnDemandInstanceStatus 
	 * @Description: get the on demand instance status
	 * @param instance the instance in AWS java sdk
	 * @param OS the OS type
	 * @return the instance status object
	 * @throws
	 */
	private InstanceStatus getOnDemandInstanceStatus(Instance instance, String OS) {
		String id = instance.getInstanceId();
		String publicUrl = instance.getPublicDnsName();
		String privateUrl = instance.getPrivateDnsName();
		Date launchTime = instance.getLaunchTime();
		
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
		InstanceTemplate onDmemand = instanceTemplateManager.getOnDemandInstanceTemplate();
		InstanceTemplate type = instanceTemplateManager.getInstanceTemplate(instance.getInstanceType(), OS);
		if (type != null && type.equals(onDmemand)) {
			InstanceStatus onDemandInstanceStatus = new OnDemandInstanceStatus(id, publicUrl, privateUrl, launchTime, type);
			onDemandInstanceStatus.setRunningStatus(RunningStatus.PENDING);
			return onDemandInstanceStatus;
		}
		return null;
	}

	/**
	 * @Title: getSpotInsatnceStatus 
	 * @Description: get the spot instance status object
	 * @param instance the instance in AWS java sdk
	 * @param spotInstanceRequest the spot instance request in AWS java sdk
	 * @param OS the OS type
	 * @return the instance status object
	 * @throws
	 */
	private InstanceStatus getSpotInsatnceStatus(Instance instance,
			SpotInstanceRequest spotInstanceRequest, String OS) {
		String id = instance.getImageId();
		String requestId = spotInstanceRequest.getSpotInstanceRequestId();
		String publicUrl = instance.getPublicDnsName();
		String privateUrl = instance.getPrivateDnsName();
		Date launchTime = instance.getLaunchTime();
		String instanceType = instance.getInstanceType();
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
		InstanceTemplate instanceTemplate = instanceTemplateManager.getInstanceTemplate(instanceType, OS);
		double biddingPrice = Double.parseDouble(spotInstanceRequest.getSpotPrice());
		return new SpotInstanceStatus(id, requestId, publicUrl, privateUrl, launchTime, instanceTemplate, biddingPrice);
	}

	/**
	 * @Title: isInAutoScaling 
	 * @Description: get whether the instance is in auto scaling group of this program
	 * @param instance the instance object in AWS java sdk
	 * @param cloudConfiguration the cloud configuration
	 * @return whether the instance is in auto scaling group of this program
	 * @throws
	 */
	private boolean isInAutoScaling(Instance instance, ICloudConfiguration cloudConfiguration) {
		Collection<Tag> tags = instance.getTags();
		for (Tag tag : tags) {
			if (tag.getKey().equals("Name")) {
				return tag.getValue().startsWith(cloudConfiguration.getNameTag());
			}
		}
		return false;
	}

	/**
	 * @Title: getSpotInsatnceRequest 
	 * @Description: get the spot instance request by spot request id
	 * @param spotRequestId spot request id
	 * @param runningSpotRequests all the running spot requests
	 * @return the spot instance request
	 * @throws
	 */
	private SpotInstanceRequest getSpotInstanceRequest(String spotRequestId,
			Collection<SpotInstanceRequest> runningSpotRequests) {
		for (SpotInstanceRequest spotInstanceRequest : runningSpotRequests) {
			if (spotInstanceRequest.getSpotInstanceRequestId().equals(spotRequestId)) {
				return spotInstanceRequest;
			}
		}
		return null;
	}

}
