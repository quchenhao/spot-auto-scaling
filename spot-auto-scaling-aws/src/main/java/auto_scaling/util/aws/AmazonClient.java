package auto_scaling.util.aws;

import auto_scaling.configuration.ICloudConfiguration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.ec2.AmazonEC2Client;

/** 
* @ClassName: AmazonClient 
* @Description: the helper class to get the Amazon class
* @author Chenhao Qu
* @date 07/06/2015 4:53:04 pm 
*  
*/
public class AmazonClient {

	/**
	 * @Title: getAmazonEC2Client 
	 * @Description: get the Amazon EC2 client
	 * @param configuration the cloud configuration
	 * @return the Amazon EC2 client
	 * @throws
	 */
	public static AmazonEC2Client getAmazonEC2Client(ICloudConfiguration configuration) {
		AWSCredentials credentials = (AWSCredentials)(configuration.getCrenditials().getCredentials());
		AmazonEC2Client ec2Client = new AmazonEC2Client(credentials);
		//ec2Client.setRegion(Region.getRegion(Regions.US_EAST_1));
		ec2Client.setEndpoint("ec2." + configuration.getRegion() + ".amazonaws.com");
		return ec2Client;
	}
	
	/**
	 * @Title: getAmazonCloudWatchClient 
	 * @Description: get the Amazon CloudWatch client
	 * @param configuration the cloud configuration
	 * @return the Amazon CloudWatch client
	 * @throws
	 */
	public static AmazonCloudWatchClient getAmazonCloudWatchClient(ICloudConfiguration configuration) {
		AWSCredentials credentials = (AWSCredentials)(configuration.getCrenditials().getCredentials());
		AmazonCloudWatchClient cloudWatchClient = new AmazonCloudWatchClient(credentials);
		//cloudWatchClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		cloudWatchClient.setEndpoint("monitoring." + configuration.getRegion() + ".amazonaws.com");
		return cloudWatchClient;
	}
}
