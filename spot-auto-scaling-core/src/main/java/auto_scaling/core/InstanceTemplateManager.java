package auto_scaling.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import auto_scaling.cloud.ApplicationResourceUsageProfile;
import auto_scaling.cloud.InstanceTemplate;

/** 
* @ClassName: InstanceTemplateManager 
* @Description: manager manages all instance templates loaded
* @author Chenhao Qu
* @date 05/06/2015 2:51:45 pm 
*  
*/
public class InstanceTemplateManager {

	/** 
	* @Fields instanceTemplates : all instance templates
	*/ 
	protected Collection<InstanceTemplate> instanceTemplates;
	/** 
	* @Fields onDemandInstanceTemplate : the on demand instance template
	*/ 
	protected InstanceTemplate onDemandInstanceTemplate;
	/** 
	* @Fields onDemandResourceUsageProfile : resource usage profile for on demand type
	*/ 
	protected ApplicationResourceUsageProfile onDemandResourceUsageProfile;
	/** 
	* @Fields applicationResourceUsageProfile : resource usage profile for spot types
	*/ 
	protected Map<InstanceTemplate, ApplicationResourceUsageProfile> applicationResourceUsageProfile;
	
	/** 
	* @Fields instanceTemplateManager : the global instance template manager
	*/ 
	private static InstanceTemplateManager instanceTemplateManager;
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	private InstanceTemplateManager() {
		this.instanceTemplates = new HashSet<InstanceTemplate>();
		this.applicationResourceUsageProfile = new HashMap<InstanceTemplate, ApplicationResourceUsageProfile>();
	}
	
	/**
	 * @Title: getInstanceTemplateManager 
	 * @Description: get the instance template manager
	 * @return the instance template manager
	 * @throws
	 */
	public static InstanceTemplateManager getInstanceTemplateManager() {
		if (instanceTemplateManager == null) {
			instanceTemplateManager = new InstanceTemplateManager();
		}
		return instanceTemplateManager;
	}
	
	/**
	 * @Title: getInstanceTemplate 
	 * @Description: get the instance template according type name and OS type
	 * @param instanceType the type name
	 * @param os the OS type
	 * @return the instance template
	 * @throws
	 */
	public InstanceTemplate getInstanceTemplate(String instanceType, String os) {
		for(InstanceTemplate instanceTemplate : instanceTemplates) {
			if (instanceTemplate.getName().equals(instanceType) && instanceTemplate.getOs().equals(os)) {
				return instanceTemplate;
			}
		}
		
		return null;
	}
	
	/**
	 * @Title: getAllInstanceTemplates 
	 * @Description: get all the available instance templates
	 * @return all the available instance templates
	 * @throws
	 */
	public Collection<InstanceTemplate> getAllInstanceTemplates() {
		return Collections.unmodifiableCollection(instanceTemplates);
	}
	
	/**
	 * @Title: addInstanceTemplate 
	 * @Description: add an instance template
	 * @param instanceTemplate the new instance template
	 * @param resourceUsageProfile the corresponding resource usage profile
	 * @throws
	 */
	public void addInstanceTemplate(InstanceTemplate instanceTemplate, ApplicationResourceUsageProfile resourceUsageProfile) {
		if (instanceTemplate == null) {
			throw new NullPointerException("instance template cannot be null!");
		}
		if (resourceUsageProfile == null) {
			throw new NullPointerException("resource usage profile cannot be null!");
		}
		
		this.instanceTemplates.add(instanceTemplate);
		this.applicationResourceUsageProfile.put(instanceTemplate, resourceUsageProfile);
	}
	
	/**
	 * @Title: setInstanceTemplate 
	 * @Description: set the instance template
	 * @param instanceTemplate the instance template wants to change
	 * @param resourceUsageProfile the corresponding new resource uage profile
	 * @throws
	 */
	public void setInstanceTemplate(InstanceTemplate instanceTemplate, ApplicationResourceUsageProfile resourceUsageProfile) { if (instanceTemplate == null) {
			throw new NullPointerException("instance template cannot be null!");
		}
		if (resourceUsageProfile == null) {
			throw new NullPointerException("resource usage profile cannot be null!");
		}
		this.applicationResourceUsageProfile.put(instanceTemplate, resourceUsageProfile);
	}
	
	/**
	 * @Title: removeInstanceTemplate 
	 * @Description: remove an instance template
	 * @param instanceTemplate the removed instance template
	 * @throws
	 */
	public void removeInstanceTemplate(InstanceTemplate instanceTemplate) {
		this.instanceTemplates.remove(instanceTemplate);
		this.applicationResourceUsageProfile.remove(instanceTemplate);
	}
	
	/**
	 * @Title: setOnDemandInstanceTemplate 
	 * @Description: set the on demand instance template
	 * @param onDemandInstanceTemplate the on demand instance template
	 * @param onDemandResourceUsageProfile the corresponding application usage profile
	 * @throws
	 */
	public void setOnDemandInstanceTemplate(InstanceTemplate onDemandInstanceTemplate, ApplicationResourceUsageProfile onDemandResourceUsageProfile) {
		if (onDemandInstanceTemplate == null) {
			throw new NullPointerException("instance template cannot be null!");
		}
		if (onDemandResourceUsageProfile == null) {
			throw new NullPointerException("resource usage profile cannot be null!");
		}
		this.onDemandInstanceTemplate = onDemandInstanceTemplate;
		this.onDemandResourceUsageProfile = onDemandResourceUsageProfile;
	}
	
	/**
	 * @Title: getOnDemandInstanceTemplate 
	 * @Description: get the on demand instance template
	 * @return the on demand instance template
	 * @throws
	 */
	public InstanceTemplate getOnDemandInstanceTemplate() {
		return onDemandInstanceTemplate;
	}
	
	/**
	 * @Title: getOnDemandApplicationResourceUsageProfile 
	 * @Description: get the on demand application usage profile
	 * @return the on demand application usage profile
	 * @throws
	 */
	public ApplicationResourceUsageProfile getOnDemandApplicationResourceUsageProfile() {
		return onDemandResourceUsageProfile;
	}
	
	/**
	 * @Title: getApplicationResourceUsageProfile 
	 * @Description: get the application usage profile for instance template
	 * @param instanceTemplate the instance template
	 * @return the application usage profile
	 * @throws
	 */
	public ApplicationResourceUsageProfile getApplicationResourceUsageProfile(InstanceTemplate instanceTemplate) {
		return this.applicationResourceUsageProfile.get(instanceTemplate);
	}
	
}
