package auto_scaling.configuration;

import java.io.InputStream;

/** 
* @ClassName: IInstanceConfigurationLoader 
* @Description: loader to load instance configurations
* @author Chenhao Qu
* @date 04/06/2015 5:06:14 pm 
*  
*/
public interface IInstanceConfigurationLoader {
	
	static final String RESOURCE_TYPES = "resource_types";
	static final String INSTANCE_TYPES = "instance_types";
	static final String ON_DEMAND_INSTANCE_TYPE = "on_demand_instance_type";
	static final String CPU = "cpu";
	static final String MEMORY = "memory";
	static final String THRESHOLD = "threshold";
	static final String INSTANCE_TYPE = "instance_type";
	static final String TEMPLATE = "template";
	static final String APPLICATION_USAGE_PROFILE = "application_usage_profile";
	static final String TYPE = "type";
	static final String VCPU = "vcpu";
	static final String ECU = "ecu";
	static final String OS = "os";
	static final String ON_DEMAND_PRICE = "on_demand_price";
	static final String IS_SUPPORT_HVM = "is_support_hvm";
	static final String IS_SUPPORT_PARAVIRTUAL = "is_support_paravirtual";
	static final String MAX_TRHESHOLD = "max_threshold";
	
	/**
	 * @Title: loadInstanceTemplateManager 
	 * @Description: load from input stream
	 * @param instream the input stream
	 * @throws Exception
	 * @throws
	 */
	public void loadInstanceTemplateManager(InputStream instream, boolean isDyanicResourceMarginEnabled) throws Exception;
}
