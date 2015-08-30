package auto_scaling.configuration;

import java.io.IOException;
import java.io.InputStream;

/** 
* @ClassName: ICloudConfigurationLoader 
* @Description: loader to load cloud configuration
* @author Chenhao Qu
* @date 04/06/2015 4:30:55 pm 
*  
*/
public interface ICloudConfigurationLoader {
	
	static final String ACCESS_KEY = "access_key";
	static final String SECRET_ACCESS_KEY = "secret_access_key";
	static final String CREDENTIALS_FILE = "credentials_file";
	static final String REGION = "region";
	static final String AVILABILITY_ZONE = "availability_zone";
	static final String KEY_NAME = "key_name";
	static final String HVM_IMAGE_ID = "hvm_image_id";
	static final String PARAVIRTUAL_IMAGE_ID = "paravirtual_image_id";
	static final String SECURITY_GROUPS = "security_groups";
	static final String OS = "os";
	static final String NAME_TAG = "name_tag";

	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param inputStream the input stream
	 * @return the cloud configuration
	 * @throws IOException
	 * @throws UnsupportedConfigurationException
	 * @throws
	 */
	public ICloudConfiguration load(InputStream inputStream) throws IOException, UnsupportedConfigurationException;
}
