package auto_scaling.configuration;

import java.util.List;

/** 
* @ClassName: ICloudConfiguration 
* @Description: the configuration of the cloud
* @author Chenhao Qu
* @date 04/06/2015 3:35:24 pm 
*  
*/
public interface ICloudConfiguration {

	/**
	 * @Title: getCrenditials 
	 * @Description: get the crenditials for the cloud account
	 * @return the crenditials for the cloud account
	 * @throws
	 */
	public ICredentials getCrenditials();
	/**
	 * @Title: getRegion 
	 * @Description: get the data center region
	 * @return the data center region
	 * @throws
	 */
	public String getRegion();
	/**
	 * @Title: getAvailabilityZone 
	 * @Description: get the availability zone
	 * @return the availability zone
	 * @throws
	 */
	public String getAvailabilityZone();
	/**
	 * @Title: getHvmImageId 
	 * @Description: get the full virtualization image id
	 * @return the full virtualization image id
	 * @throws
	 */
	public String getHvmImageId();
	/**
	 * @Title: getParavirtualImageId 
	 * @Description: get the para-virtualization image id
	 * @return the para-virtualization image id
	 * @throws
	 */
	public String getParavirtualImageId();
	/**
	 * @Title: getSecurityGroups 
	 * @Description: get the security groups
	 * @return the security groups
	 * @throws
	 */
	public List<String> getSecurityGroups();
	/**
	 * @Title: getKeyName 
	 * @Description: get the key name
	 * @return the key name
	 * @throws
	 */
	public String getKeyName();
	/**
	 * @Title: setCredentials 
	 * @Description: set the crenditials for the cloud account
	 * @param credentials the crenditials for the cloud account
	 * @throws
	 */
	public void setCredentials(ICredentials credentials);
	/**
	 * @Title: setHvmImageId 
	 * @Description: set the full virtualiztion image id
	 * @param hvmImageId the full virtualiztion image id
	 * @throws
	 */
	public void setHvmImageId(String hvmImageId);
	/**
	 * @Title: setParavirtualImageId 
	 * @Description: set the para-virtualization image id
	 * @param paravirtualImageId the para-virtualization image id
	 * @throws
	 */
	public void setParavirtualImageId(String paravirtualImageId);
	/**
	 * @Title: setRegion 
	 * @Description: set the data center region
	 * @param region the data center region
	 * @throws
	 */
	public void setRegion(String region);
	/**
	 * @Title: setAvailabilityZone 
	 * @Description: set the availability zone
	 * @param availabilityZone the availability zone
	 * @throws
	 */
	public void setAvailabilityZone(String availabilityZone);
	/**
	 * @Title: setSecurityGroups 
	 * @Description: set the security groups
	 * @param securityGroups the security groups
	 * @throws
	 */
	public void setSecurityGroups(List<String> securityGroups);
	/**
	 * @Title: setKeyName 
	 * @Description: set the key name
	 * @param keyName the key name
	 * @throws
	 */
	public void setKeyName(String keyName);
	/**
	 * @Title: getNameTag 
	 * @Description: get the name tag
	 * @return the name tag
	 * @throws
	 */
	public String getNameTag();
	/**
	 * @Title: setNameTag 
	 * @Description: set the name tag
	 * @param nameTag the name tag
	 * @throws
	 */
	public void setNameTag(String nameTag);
	/**
	 * @Title: getOS 
	 * @Description: get the OS type
	 * @return the OS type
	 * @throws
	 */
	public String getOS();
	/**
	 * @Title: setOS 
	 * @Description: set the OS type
	 * @param OS the OS type
	 * @throws
	 */
	public void setOS(String OS);
	
}
