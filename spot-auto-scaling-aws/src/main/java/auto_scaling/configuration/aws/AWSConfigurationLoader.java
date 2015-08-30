package auto_scaling.configuration.aws;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.configuration.ICloudConfigurationLoader;
import auto_scaling.configuration.ICredentials;
import auto_scaling.configuration.UnsupportedConfigurationException;

/** 
* @ClassName: AWSConfigurationLoader 
* @Description: loader that loads AWS cloud configuration
* @author Chenhao Qu
* @date 05/06/2015 2:12:19 pm 
*  
*/
public class AWSConfigurationLoader implements ICloudConfigurationLoader{
	
	public static final String IS_MONITORING_ENABLED = "is_monitoring_enabled";
	public static final String IS_EBS_OPTIMIZED = "is_EBS_optimized";

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @return
	* @throws IOException
	* @throws UnsupportedConfigurationException 
	* @see auto_scaling.configuration.ICloudConfigurationLoader#load(java.io.InputStream) 
	*/
	public ICloudConfiguration load(InputStream inputStream) throws IOException, UnsupportedConfigurationException {
		Properties properties = new Properties();
		AWSConfiguration cloudConfiguration = new AWSConfiguration();
		
		properties.load(inputStream);
		
		AWSCredentials awsCredentials = null;
		
		if (properties.containsKey(CREDENTIALS_FILE)) {
			String filePath = properties.getProperty(CREDENTIALS_FILE);
			awsCredentials = new PropertiesCredentials(new File(filePath));
		}
		else if (properties.containsKey(ACCESS_KEY) && properties.containsKey(SECRET_ACCESS_KEY)) {
			String accessKey = properties.getProperty(ACCESS_KEY);
			String secretKey = properties.getProperty(SECRET_ACCESS_KEY);
			
			awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		}
		else {
			throw new UnsupportedConfigurationException("cannot find " + CREDENTIALS_FILE +  " or " + ACCESS_KEY + ", " + SECRET_ACCESS_KEY);
		}
		
		ICredentials credentials = new AWSCredentialsWrapper(awsCredentials);
		
		cloudConfiguration.setCredentials(credentials);
		
		if (!properties.containsKey(REGION)) {
			throw new UnsupportedConfigurationException("cannot find " + REGION);
		}
		
		cloudConfiguration.setRegion(properties.getProperty(REGION));
		
		if (!properties.containsKey(AVILABILITY_ZONE)) {
			throw new UnsupportedConfigurationException("cannot find " + AVILABILITY_ZONE);
		}
		
		cloudConfiguration.setAvailabilityZone(properties.getProperty(AVILABILITY_ZONE));
		
		if (!properties.containsKey(HVM_IMAGE_ID)) {
			throw new UnsupportedConfigurationException("cannot find " + HVM_IMAGE_ID);
		}
		
		cloudConfiguration.setHvmImageId(properties.getProperty(HVM_IMAGE_ID));
		
		if (!properties.containsKey(PARAVIRTUAL_IMAGE_ID)) {
			throw new UnsupportedConfigurationException("cannot find " + PARAVIRTUAL_IMAGE_ID);
		}
		
		cloudConfiguration.setParavirtualImageId(properties.getProperty(PARAVIRTUAL_IMAGE_ID));
		
		if (!properties.containsKey(KEY_NAME)) {
			throw new UnsupportedConfigurationException("cannot find " + KEY_NAME);
		}
		
		cloudConfiguration.setKeyName(properties.getProperty(KEY_NAME));
		
		if (!properties.containsKey(SECURITY_GROUPS)) {
			throw new UnsupportedConfigurationException("cannot find " + SECURITY_GROUPS);
		}
		
		String[] values = properties.getProperty(SECURITY_GROUPS).split(",");
		List<String> security_groups = new ArrayList<String>();
		for (String value : values) {
			security_groups.add(value.trim());
		}
		
		cloudConfiguration.setSecurityGroups(security_groups);
		
		if (!properties.containsKey(NAME_TAG)) {
			throw new UnsupportedConfigurationException("cannot find " + NAME_TAG);
		}
		
		String nameTag = properties.getProperty(NAME_TAG);
		cloudConfiguration.setNameTag(nameTag);
		
		if (!properties.containsKey(OS)) {
			throw new UnsupportedConfigurationException("cannot find " + OS);
			
		}
		
		String os = properties.getProperty(OS);
		cloudConfiguration.setOS(os);
		
		if (!properties.containsKey(IS_MONITORING_ENABLED)) {
			cloudConfiguration.setMonitoringEnabled(false);
		}
		else {
			boolean value = Boolean.getBoolean(properties.getProperty(IS_MONITORING_ENABLED));
			cloudConfiguration.setMonitoringEnabled(value);
		}
		
		if (!properties.containsKey(IS_EBS_OPTIMIZED)) {
			cloudConfiguration.setMonitoringEnabled(false);
		}
		else {
			boolean value = Boolean.getBoolean(properties.getProperty(IS_EBS_OPTIMIZED));
			cloudConfiguration.setEBSOptimized(value);
		}
		
		return cloudConfiguration;
	}

	
}
