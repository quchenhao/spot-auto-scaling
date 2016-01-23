package auto_scaling.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import auto_scaling.cloud.ApplicationResourceUsageProfile;
import auto_scaling.cloud.DynamicCapacityInstanceTemplate;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.OSs;
import auto_scaling.cloud.ResourceType;
import auto_scaling.cloud.StaticCapacityInstanceTemplate;
import auto_scaling.configuration.IInstanceConfigurationLoader;
import auto_scaling.configuration.IllegalXMLFileException;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.core.SpotPricingManager;

/** 
* @ClassName: AWSXMLInstanceConfigurationLoader 
* @Description: loader to load Amazon AWS instance configuration from XML
* @author Chenhao Qu
* @date 05/06/2015 2:26:47 pm 
*  
*/
public class XMLInstanceConfigurationLoader implements IInstanceConfigurationLoader{

	/* (non-Javadoc) 
	* <p>Title: loadInstanceTemplateManager</p> 
	* <p>Description: </p> 
	* @param instream
	* @throws ParserConfigurationException
	* @throws SAXException
	* @throws IOException
	* @throws IllegalXMLFileException 
	* @see auto_scaling.configuration.IInstanceConfigurationLoader#loadInstanceTemplateManager(java.io.InputStream) 
	*/
	@Override
	public void loadInstanceTemplateManager(InputStream instream, boolean isDynamicCapacityEnabled)
			throws ParserConfigurationException, SAXException, IOException,
			IllegalXMLFileException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(instream);

		trim(document);

		NodeList nodeList = document.getDocumentElement().getChildNodes();

		if (nodeList.getLength() != 3) {
			throw new IllegalXMLFileException(
					"instance configuration file must contain resource_types, instance_types, and on_demand_instance_type");
		}

		Node resourceTypes = null;
		Node instanceTypes = null;
		Node onDemandInstanceType = null;
		for (int i = 0; i < 3; i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equalsIgnoreCase(RESOURCE_TYPES)) {
				resourceTypes = node;
			} else if (node.getNodeName().equalsIgnoreCase(INSTANCE_TYPES)) {
				instanceTypes = node;
			} else if (node.getNodeName().equalsIgnoreCase(
					ON_DEMAND_INSTANCE_TYPE)) {
				onDemandInstanceType = node;
			} else {
				throw new IllegalXMLFileException("unrecoganized node name: "
						+ node.getNodeName());
			}
		}

		loadResourceTypes(resourceTypes);
		
		loadInstanceTypes(instanceTypes, isDynamicCapacityEnabled);

		setOnDemandInstanceType(onDemandInstanceType);
	}

	/**
	 * @Title: trim 
	 * @Description: remove empty nodes and comments
	 * @param node the root node
	 * @throws
	 */
	private void trim(Node node) {

		NodeList childNodes = node.getChildNodes();

		for (int i = childNodes.getLength() - 1; i >= 0; i--) {
			Node child = childNodes.item(i);
			short nodeType = child.getNodeType();

			if (nodeType == Node.ELEMENT_NODE)
				trim(child);
			else if (nodeType == Node.TEXT_NODE) {
				String trimmedNodeVal = child.getNodeValue().trim();
				if (trimmedNodeVal.length() == 0)
					node.removeChild(child);
				else
					child.setNodeValue(trimmedNodeVal);
			} else if (nodeType == Node.COMMENT_NODE)
				node.removeChild(child);
		}
	}

	/**
	 * @Title: setOnDemandInstanceType 
	 * @Description: set the on demand instance type
	 * @param onDemandInstanceType the on demand instance type node
	 * @throws IllegalXMLFileException
	 * @throws
	 */
	private void setOnDemandInstanceType(Node onDemandInstanceType)
			throws IllegalXMLFileException {
		String type = onDemandInstanceType.getAttributes().getNamedItem(TYPE)
				.getNodeValue();
		String os = onDemandInstanceType.getAttributes().getNamedItem(OS)
				.getNodeValue();
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager
				.getInstanceTemplateManager();
		InstanceTemplate template = instanceTemplateManager
				.getInstanceTemplate(type, os);
		ApplicationResourceUsageProfile onDemandResourceUsageProfile = instanceTemplateManager
				.getApplicationResourceUsageProfile(template);
		if (template == null || onDemandResourceUsageProfile == null) {
			throw new IllegalXMLFileException(
					"on demand instance type must be included in instance types");
		}

		instanceTemplateManager.setOnDemandInstanceTemplate(template,
				onDemandResourceUsageProfile);
	}

	/**
	 * @Title: loadInstanceTypes 
	 * @Description: load the instance templates
	 * @param instanceTypes the instance types node
	 * @param isDynamicCapacityEnable whether dynamic capacity is enabled
	 * @throws IllegalXMLFileException
	 * @throws
	 */
	private void loadInstanceTypes(Node instanceTypes, boolean isDynamicResourceMarginEnabled)
			throws IllegalXMLFileException {
		if (!instanceTypes.hasChildNodes()) {
			throw new IllegalXMLFileException("empty " + INSTANCE_TYPES);
		}

		NodeList instanceTypesList = instanceTypes.getChildNodes();
		for (int i = 0; i < instanceTypesList.getLength(); i++) {
			Node node = instanceTypesList.item(i);
			if (!node.getNodeName().equalsIgnoreCase(INSTANCE_TYPE)) {
				throw new IllegalXMLFileException("unrecognzied tag: "
						+ node.getNodeName());
			}

			NodeList childNodes = node.getChildNodes();

			if (childNodes.getLength() != 2) {
				throw new IllegalXMLFileException(INSTANCE_TYPE
						+ " must caintain tag " + TEMPLATE + " "
						+ APPLICATION_USAGE_PROFILE);
			}

			Node template = null;
			Node applicationUsageProfile = null;

			InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager
					.getInstanceTemplateManager();

			for (int j = 0; j < 2; j++) {
				Node child = childNodes.item(j);
				if (child.getNodeName().equalsIgnoreCase(TEMPLATE)) {
					template = child;
				} else if (child.getNodeName().equalsIgnoreCase(
						APPLICATION_USAGE_PROFILE)) {
					applicationUsageProfile = child;
				} else {
					throw new IllegalXMLFileException("unrecognzied tag: "
							+ node.getNodeName());
				}
			}

			if (template == null) {
				throw new IllegalXMLFileException(TEMPLATE + " cannot be empty");
			}

			if (applicationUsageProfile == null) {
				throw new IllegalXMLFileException(APPLICATION_USAGE_PROFILE
						+ " cannot be empty");
			}

			String name = template.getAttributes().getNamedItem(TYPE)
					.getNodeValue();
			String vcpu = template.getAttributes().getNamedItem(VCPU)
					.getNodeValue();
			String ecu = template.getAttributes().getNamedItem(ECU)
					.getNodeValue();
			String memory = template.getAttributes().getNamedItem(MEMORY)
					.getNodeValue();
			String os = template.getAttributes().getNamedItem(OS)
					.getNodeValue();
			String onDemandPrice = template.getAttributes()
					.getNamedItem(ON_DEMAND_PRICE).getNodeValue();
			String isSupportHvm = template.getAttributes().getNamedItem(IS_SUPPORT_HVM).getNodeValue();
			String isSupportParavirtual = template.getAttributes().getNamedItem(IS_SUPPORT_PARAVIRTUAL).getNodeValue();

			if (!OSs.isSupported(os)) {
				throw new IllegalXMLFileException("unsupported " + OS + ": "
						+ os);
			}

			InstanceTemplate instanceTemplate; 
			if (isDynamicResourceMarginEnabled) {
				instanceTemplate = new DynamicCapacityInstanceTemplate(name,
					Integer.parseInt(vcpu), Double.parseDouble(ecu),
					Double.parseDouble(memory), os,
					Double.parseDouble(onDemandPrice),
					Boolean.parseBoolean(isSupportHvm),
					Boolean.parseBoolean(isSupportParavirtual));
			}
			else {
				instanceTemplate = new StaticCapacityInstanceTemplate(name,
						Integer.parseInt(vcpu), Double.parseDouble(ecu),
						Double.parseDouble(memory), os,
						Double.parseDouble(onDemandPrice),
						Boolean.parseBoolean(isSupportHvm),
						Boolean.parseBoolean(isSupportParavirtual));
			}
			

			Map<String, Number> resourceProfile = new HashMap<String, Number>();
			Collection<ResourceType> allResourceTypes = ResourceType
					.getAllResourceTypes();
			for (ResourceType resourceType : allResourceTypes) {
				String value = applicationUsageProfile.getAttributes()
						.getNamedItem(resourceType.getName()).getNodeValue();
				resourceProfile.put(resourceType.getName(),
						Double.parseDouble(value));
			}

			ApplicationResourceUsageProfile resourceUsageProfile = new ApplicationResourceUsageProfile(
					resourceProfile);
			instanceTemplateManager.addInstanceTemplate(instanceTemplate,
					resourceUsageProfile);
			
			SpotPricingManager spotPricingManager = SpotPricingManager.getSpotPricingManager();
			spotPricingManager.addSpotPricingStatus(instanceTemplate, Double.MAX_VALUE);
		}

	}

	/**
	 * @Title: loadResourceTypes 
	 * @Description: load the resource types
	 * @param resourceTypes the resource types node
	 * @throws IllegalXMLFileException
	 * @throws
	 */
	private void loadResourceTypes(Node resourceTypes)
			throws IllegalXMLFileException {
		if (!resourceTypes.hasChildNodes()) {
			throw new IllegalXMLFileException("empty " + RESOURCE_TYPES);
		}

		NodeList resourceTypesList = resourceTypes.getChildNodes();
		for (int i = 0; i < resourceTypesList.getLength(); i++) {
			Node node = resourceTypesList.item(i);
			String threshold = node.getAttributes().getNamedItem(THRESHOLD)
					.getNodeValue();
			String maxThreshold = node.getAttributes().getNamedItem(MAX_TRHESHOLD)
					.getNodeValue();
			if (node.getNodeName().equalsIgnoreCase(CPU)) {
				ResourceType.CPU = new ResourceType(CPU,
						Double.parseDouble(threshold), Double.parseDouble(maxThreshold));
			} else if (node.getNodeName().equalsIgnoreCase(MEMORY)) {
				ResourceType.MEMORY = new ResourceType(MEMORY,
						Double.parseDouble(threshold), Double.parseDouble(maxThreshold));
			} else {
				throw new IllegalXMLFileException("unsupported resource type: "
						+ node.getNodeName());
			}
		}
	}
}
