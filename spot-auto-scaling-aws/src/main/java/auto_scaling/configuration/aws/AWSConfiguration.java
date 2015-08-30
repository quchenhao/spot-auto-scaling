package auto_scaling.configuration.aws;

import java.util.List;

import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.configuration.ICredentials;

/** 
* @ClassName: AWSConfiguration 
* @Description: Cloud Configuration for Amazon AWS
* @author Chenhao Qu
* @date 05/06/2015 2:03:04 pm 
*  
*/
public class AWSConfiguration implements ICloudConfiguration{

	/** 
	* @Fields awsCredentials : the user's aws credentials
	*/ 
	protected ICredentials awsCredentials;
	/** 
	* @Fields region : aws region
	*/ 
	protected String region;
	/** 
	* @Fields availabilityZone : the availabilty zone
	*/ 
	protected String availabilityZone;
	/** 
	* @Fields hvmImageId : full virtualization image id
	*/ 
	protected String hvmImageId;
	/** 
	* @Fields paravirtualImageId : para-virtualization image id
	*/ 
	protected String paravirtualImageId;
	/** 
	* @Fields securityGroups : the security groups
	*/ 
	protected List<String> securityGroups;
	/** 
	* @Fields keyName : the key name
	*/ 
	protected String keyName;
	/** 
	* @Fields isMonitoringEnabled : is detailed cloud watch monitoring enabled
	*/ 
	protected boolean isMonitoringEnabled;
	/** 
	* @Fields isEBSOptimized : is the instance EBS optimized
	*/ 
	protected boolean isEBSOptimized;
	/** 
	* @Fields nameTag : the name tag
	*/ 
	protected String nameTag;
	/** 
	* @Fields OS : the OS type
	*/ 
	protected String OS;
	
	AWSConfiguration() {}
	
	/* (non-Javadoc) 
	* <p>Title: getCrenditials</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.ICloudConfiguration#getCrenditials() 
	*/
	@Override
	public ICredentials getCrenditials() {
		
		return awsCredentials;
	}

	/* (non-Javadoc) 
	* <p>Title: getRegion</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.ICloudConfiguration#getRegion() 
	*/
	@Override
	public String getRegion() {
		return region;
	}

	/* (non-Javadoc) 
	* <p>Title: getAvailabilityZone</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.ICloudConfiguration#getAvailabilityZone() 
	*/
	@Override
	public String getAvailabilityZone() {
		return availabilityZone;
	}

	/* (non-Javadoc) 
	* <p>Title: getSecurityGroups</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.ICloudConfiguration#getSecurityGroups() 
	*/
	@Override
	public List<String> getSecurityGroups() {
		
		return securityGroups;
	}
	
	/* (non-Javadoc) 
	* <p>Title: getKeyName</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.ICloudConfiguration#getKeyName() 
	*/
	@Override
	public String getKeyName() {
		return keyName;
	}

	/* (non-Javadoc) 
	* <p>Title: setCredentials</p> 
	* <p>Description: </p> 
	* @param awsCredentials 
	* @see auto_scaling.configuration.ICloudConfiguration#setCredentials(auto_scaling.configuration.ICredentials) 
	*/
	@Override
	public void setCredentials(ICredentials awsCredentials) {
		this.awsCredentials = awsCredentials;
	}

	/* (non-Javadoc) 
	* <p>Title: setRegion</p> 
	* <p>Description: </p> 
	* @param region 
	* @see auto_scaling.configuration.ICloudConfiguration#setRegion(java.lang.String) 
	*/
	@Override
	public void setRegion(String region) {
		this.region = region;
	}

	/* (non-Javadoc) 
	* <p>Title: setAvailabilityZone</p> 
	* <p>Description: </p> 
	* @param availabilityZone 
	* @see auto_scaling.configuration.ICloudConfiguration#setAvailabilityZone(java.lang.String) 
	*/
	@Override
	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	/* (non-Javadoc) 
	* <p>Title: setSecurityGroups</p> 
	* <p>Description: </p> 
	* @param securityGroups 
	* @see auto_scaling.configuration.ICloudConfiguration#setSecurityGroups(java.util.List) 
	*/
	@Override
	public void setSecurityGroups(List<String> securityGroups) {
		this.securityGroups = securityGroups;
	}

	/* (non-Javadoc) 
	* <p>Title: setKeyName</p> 
	* <p>Description: </p> 
	* @param keyName 
	* @see auto_scaling.configuration.ICloudConfiguration#setKeyName(java.lang.String) 
	*/
	@Override
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	/**
	 * @Title: isMonitoringEnabled 
	 * @Description: get whether detailed cloud watch monitoring is enabled
	 * @return is detailed cloud watch monitoring enabled
	 * @throws
	 */
	public boolean isMonitoringEnabled() {
		return isMonitoringEnabled;
	}

	/**
	 * @Title: setMonitoringEnabled 
	 * @Description: set whether detailed cloud watch monitoring is enabled
	 * @param isMonitoringEnabled is detailed cloud watch monitoring enabled
	 * @throws
	 */
	public void setMonitoringEnabled(boolean isMonitoringEnabled) {
		this.isMonitoringEnabled = isMonitoringEnabled;
	}

	/**
	 * @Title: isEBSOptimized 
	 * @Description: get whether the instance EBS is optimized
	 * @return is the instance EBS optimized
	 * @throws
	 */
	public boolean isEBSOptimized() {
		return isEBSOptimized;
	}

	/**
	 * @Title: setEBSOptimized 
	 * @Description: set whether the instance EBS is optimized
	 * @param isEBSOptimized is the instance EBS optimized
	 * @throws
	 */
	public void setEBSOptimized(boolean isEBSOptimized) {
		this.isEBSOptimized = isEBSOptimized;
	}

	/* (non-Javadoc) 
	* <p>Title: getNameTag</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.ICloudConfiguration#getNameTag() 
	*/
	@Override
	public String getNameTag() {
		return nameTag;
	}

	/* (non-Javadoc) 
	* <p>Title: setNameTag</p> 
	* <p>Description: </p> 
	* @param nameTag 
	* @see auto_scaling.configuration.ICloudConfiguration#setNameTag(java.lang.String) 
	*/
	@Override
	public void setNameTag(String nameTag) {
		this.nameTag = nameTag;
	}

	public String getOS() {
		return OS;
	}

	/* (non-Javadoc) 
	* <p>Title: setOS</p> 
	* <p>Description: </p> 
	* @param OS 
	* @see auto_scaling.configuration.ICloudConfiguration#setOS(java.lang.String) 
	*/
	@Override
	public void setOS(String OS) {
		this.OS = OS;
	}

	/* (non-Javadoc) 
	* <p>Title: getHvmImageId</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.ICloudConfiguration#getHvmImageId() 
	*/
	@Override
	public String getHvmImageId() {
		return hvmImageId;
	}

	/* (non-Javadoc) 
	* <p>Title: getParavirtualImageId</p> 
	* <p>Description: </p> 
	* @return 
	* @see auto_scaling.configuration.ICloudConfiguration#getParavirtualImageId() 
	*/
	@Override
	public String getParavirtualImageId() {
		return paravirtualImageId;
	}

	/* (non-Javadoc) 
	* <p>Title: setHvmImageId</p> 
	* <p>Description: </p> 
	* @param hvmImageId 
	* @see auto_scaling.configuration.ICloudConfiguration#setHvmImageId(java.lang.String) 
	*/
	@Override
	public void setHvmImageId(String hvmImageId) {
		this.hvmImageId = hvmImageId;
	}

	/* (non-Javadoc) 
	* <p>Title: setParavirtualImageId</p> 
	* <p>Description: </p> 
	* @param paravirtualImageId 
	* @see auto_scaling.configuration.ICloudConfiguration#setParavirtualImageId(java.lang.String) 
	*/
	@Override
	public void setParavirtualImageId(String paravirtualImageId) {
		this.paravirtualImageId = paravirtualImageId;
	}
	
	
}
