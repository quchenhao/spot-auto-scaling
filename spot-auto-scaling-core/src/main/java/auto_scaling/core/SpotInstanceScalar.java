package auto_scaling.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import auto_scaling.capacity.ICapacityCalculator;
import auto_scaling.cloud.InstanceStatus;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.configuration.ICapacityCalculatorLoader;
import auto_scaling.configuration.ICloudConfiguration;
import auto_scaling.configuration.ICloudConfigurationLoader;
import auto_scaling.configuration.IEventCriticalLevelLoader;
import auto_scaling.configuration.IEventHandlersLoader;
import auto_scaling.configuration.IInstanceConfigurationLoader;
import auto_scaling.configuration.ILimitsLoader;
import auto_scaling.configuration.ILoadBalancerLoader;
import auto_scaling.configuration.IMonitorsLoader;
import auto_scaling.configuration.IOnlineTaskLoader;
import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.configuration.IScalingPoliciesConfigurationLoader;
import auto_scaling.configuration.ISystemStatusLoader;
import auto_scaling.configuration.Limits;
import auto_scaling.event.EventGenerator;
import auto_scaling.handler.EventHandler;
import auto_scaling.handler.EventHandlerManager;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.monitor.Monitor;
import auto_scaling.monitor.MonitorThread;
import auto_scaling.online.IOnlineTask;
import auto_scaling.util.DefaultLogFormatter;
import auto_scaling.util.LogFormatter;


/** 
* @ClassName: SpotInstanceScalar 
* @Description: main class for real cloud
* @author Chenhao Qu
* @date 05/06/2015 3:00:48 pm 
*  
*/
public class SpotInstanceScalar {
	
	protected static Logger mainLog = LogManager.getLogger(SpotInstanceScalar.class);
	protected static final String CLOUD_CONFIGURATION_LOADER = "cloud_configuration_loader";
	protected static final String CLOUD_CONFIGURATION_FILE = "cloud_configuration_file";
	protected static final String INSTANCE_CONFIGURATION_LOADER = "instance_configuration_loader";
	protected static final String INSTANCE_CONFIGURATION_FILE = "instance_configuration_file";
	protected static final String SYSTEM_STATUS_LOADER = "system_status_loader";
	protected static final String SCALING_POLICIES_CONFIGURATION_LOADER = "scaling_policies_configuration_loader";
	protected static final String SCALING_POLICIES_CONFIGURATION_FILE = "scaling_policies_configuration_file";
	protected static final String MONITORS_LOADER = "monitors_loader";
	protected static final String MONITORS_CONFIGURATION_FILE = "monitors_configuration_file";
	protected static final String EVENT_HANDLERS_LOADER = "event_handlers_loader";
	protected static final String EVENT_HANDLERS_CONFIGURATION_FILE = "event_handlers_configuration_file";
	protected static final String EVENT_CRITICAL_LEVEL_LOADER = "event_critical_level_loader";
	protected static final String EVENT_CRITICAL_LEVEL_CONFIGURATION_FILE = "event_critical_level_configuration_file";
	protected static final String FAULT_TOLERANT_LEVEL = "fault_tolerant_level";
	protected static final String LOAD_BALANCER_LOADER = "load_balancer_loader";
	protected static final String LOAD_BALANCER_CONFIGURATION_FILE = "load_balancer_configuration_file";
	protected static final String LIMITS_CONFIGURATION_LOADER = "limits_configuration_loader";
	protected static final String LIMITS_CONFIGURATION_FILE = "limits_configuration_file";
	protected static final String CAPACITY_CALCULATOR_LOADER = "capacity_calculator_loader";
	protected static final String CAPACITY_CALCULATOR_CONFIGURATION_FILE = "capacity_calculator_configuration_file";
	protected static final String ONLINE_TASK_LOADER = "online_task_loader";
	protected static final String ONLINE_TASK_CONFIGURATION_FILE = "online_task_configuration_file";
	protected static final String SYSTEM_CONF = "system.properties";
	protected static final String DYNAMIC_RESOURCE_MARGIN = "dynamic_resource_margin";
	
	/**
	 * @Title: main 
	 * @Description: main method
	 * @param args the_home_directory_of_configuration_files
	 * @throws
	 */
	public static void main(String[] args) {
		
		try {
			LogFormatter.initialize(DefaultLogFormatter.class);
		} catch (InstantiationException | IllegalAccessException e) {
			System.out.println("initialize log failed");
			e.printStackTrace();
			System.exit(1);
		}
		LogFormatter logFormatter = LogFormatter.getLogFormatter();
		
		if (args.length < 1) {
			System.out.println("parameter properties_file");
			System.exit(1);
		}
		
		mainLog.info(logFormatter.getMessage("Start Spot Instance Scalar"));
		
		Properties properties = null;
		File directory =null;
		try {
			directory = new File(args[0]);
			File file = new File(directory, SYSTEM_CONF);
			InputStream fileInputStream = new FileInputStream(file);
			properties = new Properties();
			properties.load(fileInputStream);
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		
		mainLog.info(logFormatter.getMessage("Initialize Cloud Configuration"));
		String cloudCongigurationLoaderClass = properties.getProperty(CLOUD_CONFIGURATION_LOADER);
		String cloudConfigurationFile = properties.getProperty(CLOUD_CONFIGURATION_FILE);
		ICloudConfiguration cloudConfiguration = null;
		try {
			ICloudConfigurationLoader cloudConfigurationLoader = (ICloudConfigurationLoader)(Class.forName(cloudCongigurationLoaderClass).newInstance());
			File file = new File(directory, cloudConfigurationFile);
			InputStream cloudConfigurationStream = new FileInputStream(file);
			cloudConfiguration = cloudConfigurationLoader.load(cloudConfigurationStream);
			cloudConfigurationStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Cloud Configuration"));
		
		mainLog.info(logFormatter.getMessage("Initializing Instance Template Manager"));
		String instanceConfigurationLoaderClass = properties.getProperty(INSTANCE_CONFIGURATION_LOADER);
		String instanceConfigurationFile = properties.getProperty(INSTANCE_CONFIGURATION_FILE);
		try {
			IInstanceConfigurationLoader instanceConfigurationLoader = (IInstanceConfigurationLoader)(Class.forName(instanceConfigurationLoaderClass).newInstance());
			File file = new File(directory, instanceConfigurationFile);
			InputStream instanceConfigurationStream = new FileInputStream(file);
			boolean isDynamicResourceMarginEnabled = Boolean.parseBoolean(properties.getProperty(DYNAMIC_RESOURCE_MARGIN));
			instanceConfigurationLoader.loadInstanceTemplateManager(instanceConfigurationStream, isDynamicResourceMarginEnabled);
			instanceConfigurationStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Instance Template Manager"));
		
		mainLog.info(logFormatter.getMessage("Initializing Capacity Calculator"));
		String capacityCalculatorLoaderClass = properties.getProperty(CAPACITY_CALCULATOR_LOADER);
		String capacityCalculatorConfigurationFile = properties.getProperty(CAPACITY_CALCULATOR_CONFIGURATION_FILE);
		ICapacityCalculator capacityCalculator = null;
		try {
			ICapacityCalculatorLoader capacityCalculatorLoader = (ICapacityCalculatorLoader)(Class.forName(capacityCalculatorLoaderClass).newInstance());
			File file = new File(directory, capacityCalculatorConfigurationFile);
			InputStream capacityCalculatorInputStream = new FileInputStream(file);
			capacityCalculator = capacityCalculatorLoader.load(capacityCalculatorInputStream);
			capacityCalculatorInputStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
		Collection<InstanceTemplate> allInstanceTemplates = instanceTemplateManager.getAllInstanceTemplates();
		for (InstanceTemplate instanceTemplate : allInstanceTemplates) {
			long capacity = capacityCalculator.getEstimatedBasicCapacity(instanceTemplate);
			long maximumCapacity = capacityCalculator.getEstimatedMaximumCapacity(instanceTemplate);
			instanceTemplate.setBasicCapacity(capacity);
			instanceTemplate.setMaximumCapacity(maximumCapacity);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Capacity Calculator"));
		
		mainLog.info(logFormatter.getMessage("Initializing System Status"));
		String systemStatusLoaderClass = properties.getProperty(SYSTEM_STATUS_LOADER);
		String faultTolerantLevelString = properties.getProperty(FAULT_TOLERANT_LEVEL);
		try {
			ISystemStatusLoader systemStatusLoader = (ISystemStatusLoader)(Class.forName(systemStatusLoaderClass).newInstance());
			systemStatusLoader.load(cloudConfiguration);
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		FaultTolerantLevel faultTolerantLevel = FaultTolerantLevel.getFaultTolerantLevel(faultTolerantLevelString);
		if (faultTolerantLevel == null) {
			mainLog.fatal(logFormatter.getMessage("Unsupported fault tolerant level"));
			System.exit(1);
		}
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		systemStatus.setFaultTolerantLevel(faultTolerantLevel);
		mainLog.info(logFormatter.getMessage("End Initializing System Status"));
		
		mainLog.info(logFormatter.getMessage("Initializing Limits"));
		String limitsLoaderClass = properties.getProperty(LIMITS_CONFIGURATION_LOADER);
		String limitsConfigurationFile = properties.getProperty(LIMITS_CONFIGURATION_FILE);
		Limits limits = null;
		try {
			ILimitsLoader limitsLoader = (ILimitsLoader)(Class.forName(limitsLoaderClass).newInstance());
			File file = new File(directory, limitsConfigurationFile);
			InputStream limitsStream = new FileInputStream(file);
			limits = limitsLoader.load(limitsStream);
			limitsStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Limits"));
		
		mainLog.info(logFormatter.getMessage("Initializing Scaling Policies Configuration"));
		String scalingPoliciesConfigurationClass = properties.getProperty(SCALING_POLICIES_CONFIGURATION_LOADER);
		String scalingPoliciesConfigurationFile = properties.getProperty(SCALING_POLICIES_CONFIGURATION_FILE);
		IScalingPoliciesConfiguration scalingPoliciesConfiguration = null;
		try {
			IScalingPoliciesConfigurationLoader scalingPoliciesConfigurationLoader = (IScalingPoliciesConfigurationLoader)(Class.forName(scalingPoliciesConfigurationClass).newInstance());
			File file = new File(directory, scalingPoliciesConfigurationFile);
			InputStream scalingPoliciesStream = new FileInputStream(file);
			scalingPoliciesConfiguration = scalingPoliciesConfigurationLoader.load(limits, scalingPoliciesStream);
			scalingPoliciesStream.close();
		}
		catch(Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Scaling Policies Configuration"));
		
		mainLog.info(logFormatter.getMessage("Initializing Online Task"));
		String onlineTaskLoaderClass = properties.getProperty(ONLINE_TASK_LOADER);
		String onlineTaskConfigurationFile = properties.getProperty(ONLINE_TASK_CONFIGURATION_FILE);
		IOnlineTask onlineTask = null;
		try {
			IOnlineTaskLoader onlineTaskLoader = (IOnlineTaskLoader)(Class.forName(onlineTaskLoaderClass).newInstance());
			File file = new File(directory, onlineTaskConfigurationFile);
			InputStream onlineTaskStream = new FileInputStream(file);
			onlineTask = onlineTaskLoader.load(onlineTaskStream);
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Online Task"));
		
		mainLog.info(logFormatter.getMessage("Initializing Event Critical Level"));
		String eventCriticalLevelLoaderClass = properties.getProperty(EVENT_CRITICAL_LEVEL_LOADER);
		String eventCriticalLevelLoaderFile = properties.getProperty(EVENT_CRITICAL_LEVEL_CONFIGURATION_FILE);
		try {
			IEventCriticalLevelLoader eventCriticalLevelLoader = (IEventCriticalLevelLoader)(Class.forName(eventCriticalLevelLoaderClass).newInstance());
			File file = new File(directory, eventCriticalLevelLoaderFile);
			InputStream eventCriticalLevelStream = new FileInputStream(file);
			Map<String, Integer> criticalLevels = eventCriticalLevelLoader.load(eventCriticalLevelStream);
			eventCriticalLevelStream.close();
			
			EventGenerator eventGenerator = EventGenerator.getEventGenerator();
			eventGenerator.setCriticalLevels(criticalLevels);
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage(logFormatter.getMessage("End Initializing Event Critical Level")));
		
		mainLog.trace(logFormatter.getMessage("Initializing Monitors"));
		String monitorsLoaderClass = properties.getProperty(MONITORS_LOADER);
		String monitorsFile = properties.getProperty(MONITORS_CONFIGURATION_FILE);
		Map<String, Monitor> monitors = null;
		try {
			IMonitorsLoader monitorsLoader = (IMonitorsLoader)(Class.forName(monitorsLoaderClass).newInstance());
			File file = new File(directory, monitorsFile);
			InputStream monitorsStream = new FileInputStream(file);
			monitors = monitorsLoader.loadMonitors(monitorsStream, cloudConfiguration);
			monitorsStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Monitors"));
		
		mainLog.info(logFormatter.getMessage("Initializing Event Handlers"));
		String eventHandlerLoaderClass = properties.getProperty(EVENT_HANDLERS_LOADER);
		String eventHandlerFile = properties.getProperty(EVENT_HANDLERS_CONFIGURATION_FILE);
		Map<String, EventHandler> eventHandlers = null;
		
		try {
			IEventHandlersLoader eventHandlerLoader = (IEventHandlersLoader)(Class.forName(eventHandlerLoaderClass).newInstance());
			File file = new File(directory, eventHandlerFile);
			InputStream eventHandlerStream = new FileInputStream(file);
			eventHandlers = eventHandlerLoader.load(cloudConfiguration, capacityCalculator, scalingPoliciesConfiguration, monitors, onlineTask, eventHandlerStream);
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		EventHandlerManager eventHandlerManager = EventHandlerManager.getEventHandlerManager();
		eventHandlerManager.setEventHandlers(eventHandlers);
		mainLog.trace(logFormatter.getMessage("End Initializing Event Handlers"));
		
		mainLog.info(logFormatter.getMessage("Initializing Event Processor"));
		EventProcessor eventProcessor = new EventProcessor();
		Thread processorThread = new Thread(eventProcessor);
		mainLog.info(logFormatter.getMessage("End Initializing Event Processor"));
		
		mainLog.info(logFormatter.getMessage("Initializing Load Balancer"));
		String loadBalancerLoaderClass = properties.getProperty(LOAD_BALANCER_LOADER);
		String loadBalancerFile = properties.getProperty(LOAD_BALANCER_CONFIGURATION_FILE);
		try {
			ILoadBalancerLoader loadBalancerLoader = (ILoadBalancerLoader)(Class.forName(loadBalancerLoaderClass).newInstance());
			File file = new File(directory, loadBalancerFile);
			InputStream lbStream = new FileInputStream(file);
			LoadBalancer loadBalancer = loadBalancerLoader.load(lbStream);
			LoadBalancer.setLoadBalancer(loadBalancer);
			Collection<InstanceStatus> allInstances = systemStatus.getAllInstances();
			loadBalancer.attach(allInstances);
			lbStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Load Balancer"));
		
		mainLog.info(logFormatter.getMessage("Start Event Processor"));
		processorThread.start();
		
		mainLog.info(logFormatter.getMessage("Start Monitors"));
		Map<String, MonitorThread> monitorThreads = new HashMap<String, MonitorThread>();
		for (String monitorName : monitors.keySet()) {
			Monitor monitor = monitors.get(monitorName);
			MonitorThread monitorThread = new MonitorThread(monitor);
			monitorThreads.put(monitorName, monitorThread);
			monitorThread.start();
		}
		
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.println("Please input your command");
			try {
				String line = bReader.readLine();
				
				String[] parts = line.split(" ");
				
				if (parts[0].equalsIgnoreCase("quit")) {
					mainLog.info("System Exit");
					System.exit(0);
				}
				else {
					System.out.println("Unimplemented Commands");
				}
				
			} catch (IOException e) {
				mainLog.error(logFormatter.getExceptionString(e));
			}
		}
	}
}
