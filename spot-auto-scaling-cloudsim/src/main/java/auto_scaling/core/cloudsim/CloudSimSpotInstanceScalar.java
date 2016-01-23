package auto_scaling.core.cloudsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.ex.DatacenterEX;
import org.cloudbus.cloudsim.ex.util.Id;
import org.cloudbus.cloudsim.ex.vm.MonitoredVMex;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import auto_scaling.capacity.ICapacityCalculator;
import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.cloud.OnDemandInstanceStatus;
import auto_scaling.cloud.RunningStatus;
import auto_scaling.configuration.ICapacityCalculatorLoader;
import auto_scaling.configuration.IEventCriticalLevelLoader;
import auto_scaling.configuration.IEventHandlersLoader;
import auto_scaling.configuration.IInstanceConfigurationLoader;
import auto_scaling.configuration.ILimitsLoader;
import auto_scaling.configuration.ILoadBalancerLoader;
import auto_scaling.configuration.IMonitorsLoader;
import auto_scaling.configuration.IScalingPoliciesConfiguration;
import auto_scaling.configuration.IScalingPoliciesConfigurationLoader;
import auto_scaling.configuration.ISystemStatusLoader;
import auto_scaling.configuration.cloudsim.ICloudletFactoryLoader;
import auto_scaling.configuration.cloudsim.IDistributionSettingsLoader;
import auto_scaling.configuration.cloudsim.IWorkloadGeneratorLoader;
import auto_scaling.core.EventProcessor;
import auto_scaling.core.FaultTolerantLevel;
import auto_scaling.core.InstanceTemplateManager;
import auto_scaling.core.SpotInstanceScalar;
import auto_scaling.core.SystemStatus;
import auto_scaling.core.cloudsim.workload.IWorkloadGenerator;
import auto_scaling.event.EventGenerator;
import auto_scaling.handler.EventHandler;
import auto_scaling.handler.EventHandlerManager;
import auto_scaling.loadbalancer.LoadBalancer;
import auto_scaling.monitor.Monitor;
import auto_scaling.util.LogFormatter;
import auto_scaling.util.cloudsim.CloudSimLogFormatter;
import auto_scaling.util.cloudsim.CloudletFactory;
import auto_scaling.util.cloudsim.TimeConverter;
import auto_scaling.util.cloudsim.VmFactory;

/** 
* @ClassName: CloudSimSpotInstanceScalar 
* @Description: main class for cloudSim scalar
* @author Chenhao Qu
* @date 06/06/2015 12:28:47 pm 
*  
*/
public class CloudSimSpotInstanceScalar extends SpotInstanceScalar{

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
	private static final String WORKLOAD_GENERATOR_LOADER = "workload_generator_loader";
	private static final String WORKLOAD_GENERATOR_CONFIGURATION_FILE = "workload_generator_configuration_file";
	private static final String DISTRIBUTION_SETTINGS_LOADER = "distribution_settings_loader";
	private static final String DISTRIBUTION_SETTINGS_FILE = "distribution_settings_file";
	private static final String DC_MONITORING_PERIOD = "dc_monitoring_period";
	private static final String DC_STEP_PERIOD = "dc_step_period";
	private static final String APP_MONITORING_PERIOD = "app_monitoring_period";
	private static final String APP_EVENT_PERIOD = "app_event_period";
	private static final String SPOT_PRICE_SOURCE_HOME = "spot_price_source_home";
	private static final String CLOUDLET_FACTORY_LOADER = "cloudlet_factory_loader";
	private static final String CLOUDLET_FACTORY_CONFIGURATION_FILE = "cloudlet_factory_configuration_file";
	private static final String INITIAL_VM_NUM = "initial_vm_num";
	private static final String SUMMARY_PERIOD_LENGTH = "summary_period_length";
	private static final String LIFE_LENGTH = "life_length";
	private static final String REQUEST_TIMEOUT = "request_timeout";
	
	/**
	 * @Title: main 
	 * @Description: the main method
	 * @param args the file path to the configuration file folder and the start time
	 * @throws
	 */
	public static void main(String[] args) {
		CloudSimSpotInstanceScalar cloudSimSpotInstanceScalar = new CloudSimSpotInstanceScalar();
		cloudSimSpotInstanceScalar.start(args);
	}
	
	/* (non-Javadoc) 
	* <p>Title: start</p> 
	* <p>Description: </p> 
	* @param args 
	* @see auto_scaling.core.SpotInstanceScalar#start(java.lang.String[]) 
	*/
	public void start(String[] args) {
		try {
			LogFormatter.initialize(CloudSimLogFormatter.class);
		} catch (InstantiationException | IllegalAccessException e) {
			System.out.println("initialize log failed");
			e.printStackTrace();
			System.exit(1);
		}
		LogFormatter logFormatter = LogFormatter.getLogFormatter();
		
		if (args.length < 2) {
			System.out.println("parameters properties_file start_simulation_time");
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
		
		mainLog.info(logFormatter.getMessage("initializing data center setting"));
		int numUser = 1;
		Calendar calendar = Calendar.getInstance();
		boolean traceFlag = false;
		CloudSim.init(numUser, calendar, traceFlag);
		
		try {
			@SuppressWarnings("unused")
			DatacenterEX datecenter = createDatacenter("Datacenter_0", 50);
			
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("end initializing data center setting"));
		
		mainLog.info(logFormatter.getMessage("Initializing Instance Template Manager"));
		String instanceConfigurationLoaderClass = properties.getProperty(INSTANCE_CONFIGURATION_LOADER);
		String instanceConfigurationFile = properties.getProperty(INSTANCE_CONFIGURATION_FILE);
		try {
			IInstanceConfigurationLoader instanceConfigurationLoader = (IInstanceConfigurationLoader)(Class.forName(instanceConfigurationLoaderClass).newInstance());
			File file = new File(directory, instanceConfigurationFile);
			InputStream instanceConfigurationStream = new FileInputStream(file);
			String dynamicResourceMargin = properties.getProperty(DYNAMIC_RESOURCE_MARGIN);
			boolean isDynamicCapacityEnabled = Boolean.parseBoolean(dynamicResourceMargin);
			instanceConfigurationLoader.loadInstanceTemplateManager(instanceConfigurationStream, isDynamicCapacityEnabled);
			instanceConfigurationStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Instance Template Manager"));
		
		mainLog.info(logFormatter.getMessage("Initializing Spot Price Source"));
		CloudSimSpotPriceSource cloudSimSpotPriceSource = CloudSimSpotPriceSource.getCloudSimSpotPriceSource();
		InstanceTemplateManager instanceTemplateManager = InstanceTemplateManager.getInstanceTemplateManager();
		Collection<InstanceTemplate> allInstanceTemplates = instanceTemplateManager.getAllInstanceTemplates();
		String spotPriceSourceHome = properties.getProperty(SPOT_PRICE_SOURCE_HOME);
		File spotPriceSourceDirectory = new File(spotPriceSourceHome);
		for (InstanceTemplate instanceTemplate : allInstanceTemplates) {
			String os = instanceTemplate.getOs();
			os = os.replace('/', '_');
			File file = new File(spotPriceSourceDirectory, instanceTemplate.getName() + "_" + os + ".txt");
			try {
				cloudSimSpotPriceSource.initiateTraceReader(instanceTemplate, file);
			} catch (Exception e) {
				mainLog.fatal(logFormatter.getExceptionString(e));
				System.exit(1);
			}
		}
		mainLog.info(logFormatter.getMessage("End Initializing Spot Price Source"));
		
		mainLog.info(logFormatter.getMessage("Initializing Distribution Settings"));
		String distributionSettingsLoaderClass = properties.getProperty(DISTRIBUTION_SETTINGS_LOADER);
		String distributionSettingsFile = properties.getProperty(DISTRIBUTION_SETTINGS_FILE);
		try {
			IDistributionSettingsLoader distributionSettingsLoader = (IDistributionSettingsLoader)(Class.forName(distributionSettingsLoaderClass).newInstance());
			File file = new File(directory, distributionSettingsFile);
			InputStream distributionSettingsStream = new FileInputStream(file);
			distributionSettingsLoader.load(distributionSettingsStream);
			distributionSettingsStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Distribution Settings"));
		
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
		for (InstanceTemplate instanceTemplate : allInstanceTemplates) {
			long capacity = capacityCalculator.getEstimatedBasicCapacity(instanceTemplate);
			long maximumCapacity = capacityCalculator.getEstimatedMaximumCapacity(instanceTemplate);
			instanceTemplate.setBasicCapacity(capacity);
			instanceTemplate.setMaximumCapacity(maximumCapacity);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Capacity Calculator"));
		
		mainLog.info(logFormatter.getMessage("Initializng System Status"));
		String systemStatusLoaderClass = properties.getProperty(SYSTEM_STATUS_LOADER);
		String faultTolerantLevelString = properties.getProperty(FAULT_TOLERANT_LEVEL);
		try {
			ISystemStatusLoader systemStatusLoader = (ISystemStatusLoader)(Class.forName(systemStatusLoaderClass).newInstance());
			systemStatusLoader.load(null);
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		FaultTolerantLevel faultTolerantLevel = FaultTolerantLevel.getFaultTolerantLevel(faultTolerantLevelString);
		if (faultTolerantLevel == null) {
			mainLog.fatal("Unsupported fault tolerant level");
			System.exit(1);
		}
		SystemStatus systemStatus = SystemStatus.getSystemStatus();
		systemStatus.setFaultTolerantLevel(faultTolerantLevel);
		mainLog.info(logFormatter.getMessage("End Initializing System Status"));
		
		mainLog.info(logFormatter.getMessage("Initializng Cloudlet Factory"));
		String cloudletFactoryLoaderClass = properties.getProperty(CLOUDLET_FACTORY_LOADER);
		String cloudletFactoryConfigurationFile = properties.getProperty(CLOUDLET_FACTORY_CONFIGURATION_FILE);
		try {
			ICloudletFactoryLoader cloudletFactoryLoader = (ICloudletFactoryLoader)(Class.forName(cloudletFactoryLoaderClass).newInstance());
			File file = new File(directory, cloudletFactoryConfigurationFile);
			InputStream inputStream = new FileInputStream(file);
			cloudletFactoryLoader.load(inputStream);
			inputStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializng Cloudlet Factory"));
		
		mainLog.info(logFormatter.getMessage("Initializing Workload Generator"));
		String workloadGeneratorLoaderClass = properties.getProperty(WORKLOAD_GENERATOR_LOADER);
		String workloadGeneratorLoaderFile = properties.getProperty(WORKLOAD_GENERATOR_CONFIGURATION_FILE);
		IWorkloadGenerator workloadGenerator = null;
		try {
			IWorkloadGeneratorLoader workloadGeneratorLoader = (IWorkloadGeneratorLoader)(Class.forName(workloadGeneratorLoaderClass).newInstance());
			File file = new File(directory, workloadGeneratorLoaderFile);
			InputStream workloadGeneratorStream = new FileInputStream(file);
			workloadGenerator = workloadGeneratorLoader.load(workloadGeneratorStream);
			workloadGeneratorStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		} 
		mainLog.info(logFormatter.getMessage("End Initializing Workload Generator"));
		
		mainLog.info(logFormatter.getMessage("Initializing Limits"));
		String limitsLoaderClass = properties.getProperty(LIMITS_CONFIGURATION_LOADER);
		String limitsConfigurationFile = properties.getProperty(LIMITS_CONFIGURATION_FILE);
		try {
			ILimitsLoader limitsLoader = (ILimitsLoader)(Class.forName(limitsLoaderClass).newInstance());
			File file = new File(directory, limitsConfigurationFile);
			InputStream limitsStream = new FileInputStream(file);
			limitsLoader.load(limitsStream);
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
			scalingPoliciesConfiguration = scalingPoliciesConfigurationLoader.load(scalingPoliciesStream);
			scalingPoliciesStream.close();
		}
		catch(Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Scaling Policies Configuration"));
		
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
		mainLog.info(logFormatter.getMessage("End Initializing Event Critical Level"));
		
		mainLog.info(logFormatter.getMessage("Initializing Monitors"));
		String monitorsLoaderClass = properties.getProperty(MONITORS_LOADER);
		String monitorsConfigurationFile = properties.getProperty(MONITORS_CONFIGURATION_FILE);
		Map<String, Monitor> monitors = null;
		try {
			IMonitorsLoader monitorsLoader = (IMonitorsLoader)(Class.forName(monitorsLoaderClass).newInstance());
			File file = new File(directory, monitorsConfigurationFile);
			InputStream monitorsStream = new FileInputStream(file);
			monitors = monitorsLoader.loadMonitors(monitorsStream, null);
			monitorsStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Monitors"));
		
		mainLog.info(logFormatter.getMessage("Initializing Event Handlers"));
		String eventHandlersLoaderClass = properties.getProperty(EVENT_HANDLERS_LOADER);
		String eventHandlersConfigurationFile = properties.getProperty(EVENT_HANDLERS_CONFIGURATION_FILE);
		Map<String, EventHandler> eventHandlers = null;
		try {
			IEventHandlersLoader eventHandlersLoader = (IEventHandlersLoader)(Class.forName(eventHandlersLoaderClass).newInstance());
			File file = new File(directory, eventHandlersConfigurationFile);
			InputStream eventHandlersStream = new FileInputStream(file);
			eventHandlers = eventHandlersLoader.load(null, capacityCalculator, scalingPoliciesConfiguration, monitors, null, eventHandlersStream);
			eventHandlersStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		EventHandlerManager eventHandlerManager = EventHandlerManager.getEventHandlerManager();
		eventHandlerManager.setEventHandlers(eventHandlers);
		mainLog.info(logFormatter.getMessage("End Initializing Event Handlers"));
		
		mainLog.info(logFormatter.getMessage("Initializing Event Processor"));
		EventProcessor eventProcessor = new EventProcessor();
		mainLog.info(logFormatter.getMessage("End Initializing Event Processor"));
		
		mainLog.info(logFormatter.getMessage("Initializing Cloud Sim Broker"));
		double monitoringPeriod = Double.parseDouble(properties.getProperty(DC_MONITORING_PERIOD));
		int stepPeriod = Integer.parseInt(properties.getProperty(DC_STEP_PERIOD));
		int appMonitoringPeriod = Integer.parseInt(properties.getProperty(APP_MONITORING_PERIOD));
		int appEventPeriod = Integer.parseInt(properties.getProperty(APP_EVENT_PERIOD));
		double lifeLength = Integer.parseInt(properties.getProperty(LIFE_LENGTH));
		CloudSimBroker cloudSimBroker = null;
		try {
			cloudSimBroker = getCloudSimBroker(lifeLength, monitoringPeriod, stepPeriod, appMonitoringPeriod, appEventPeriod, workloadGenerator, monitors.values(), eventProcessor);
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		ApplicationBrokerManager applicationBrokerManager = ApplicationBrokerManager.getApplicationBrokerManager();
		applicationBrokerManager.setCloudSimBroker(cloudSimBroker);
		
		VmFactory vmFactory = VmFactory.getVmFactory();
		vmFactory.setUserId(cloudSimBroker.getId());
		double summaryPeriodLength = Double.parseDouble(properties.getProperty(SUMMARY_PERIOD_LENGTH));
		vmFactory.setSummaryPeriodLength(summaryPeriodLength);
		double timeOut = Double.parseDouble(properties.getProperty(REQUEST_TIMEOUT));
		vmFactory.setTimeOut(timeOut);
		
		CloudletFactory cloudletFactory = CloudletFactory.getCloudletFactory();
		cloudletFactory.setUserId(cloudSimBroker.getId());
		mainLog.info(logFormatter.getMessage(logFormatter.getMessage("End Initializing Cloud Sim Broker")));
		
		mainLog.info(logFormatter.getMessage("Initializing Load Balancer"));
		String loadBalancerLoaderClass = properties.getProperty(LOAD_BALANCER_LOADER);
		String loadBalancerFile = properties.getProperty(LOAD_BALANCER_CONFIGURATION_FILE);
		try {
			ILoadBalancerLoader loadBalancerLoader = (ILoadBalancerLoader)(Class.forName(loadBalancerLoaderClass).newInstance());
			File file = new File(directory, loadBalancerFile);
			InputStream lbStream = new FileInputStream(file);
			LoadBalancer loadBalancer = loadBalancerLoader.load(lbStream);
			LoadBalancer.setLoadBalancer(loadBalancer);
			lbStream.close();
		} catch (Exception e) {
			mainLog.fatal(logFormatter.getExceptionString(e));
			System.exit(1);
		}
		mainLog.info(logFormatter.getMessage("End Initializing Load Balancer"));
		
		mainLog.info(logFormatter.getMessage("Starting the initial vms"));
		List<Vm> initialVms = new ArrayList<Vm>();
		InstanceTemplate onDemandTemplate = instanceTemplateManager.getOnDemandInstanceTemplate();
		
		cloudSimBroker.createVmsAfter(initialVms, 2);
		Date startTime = null;
		startTime = new Date(Long.parseLong(args[1]));
		
		mainLog.info(logFormatter.getMessage("Set start simulation time at " + startTime));
		//System.out.println(startTime.toGMTString());
		TimeConverter.setSimulationStartTime(startTime);
		
		int initialVmNumber = Integer.parseInt(properties.getProperty(INITIAL_VM_NUM));
		Logger eventLogger  = LogManager.getLogger(Monitor.class);
		for (int i = 0; i < initialVmNumber; i++) {
			MonitoredVMex vm = vmFactory.getVm(onDemandTemplate);
			initialVms.add(vm);
			OnDemandInstanceStatus onDemandInstanceStatus = new OnDemandInstanceStatus(vm.getId() + "", null, null, startTime, onDemandTemplate);
			onDemandInstanceStatus.setRunningStatus(RunningStatus.PENDING);
			systemStatus.addInstance(onDemandInstanceStatus);
			eventLogger.info(logFormatter.getMessage("create on demand instance: " + onDemandInstanceStatus.toString()));
		}
		
		eventLogger.info(logFormatter.getMessage("total nominal capacity: " + systemStatus.getNominalCapacity()));
		
		mainLog.info(logFormatter.getMessage("End Starting the initial vms"));
		CloudSim.startSimulation();
		CloudSim.stopSimulation();
		long endTime = (long)CloudSim.clock();
		Date endTimeDate = TimeConverter.convertSimulationTimeToDate(endTime);
		mainLog.info(logFormatter.getMessage("End Simulation Time " + endTimeDate));
	}

	/**
	 * @Title: getCloudSimBroker 
	 * @Description: get the cloudSim broker
	 * @param lifeLength the life length of simulation
	 * @param monitoringPeriod the monitoring period of the broker
	 * @param stepPeriod the interval to submit cloudlets
	 * @param appMonitorPeriod the interval to call monitoring modules
	 * @param appEventPeriod the interval to call event processing
	 * @param workloadGenerator the workload generator
	 * @param monitors the monitors
	 * @param eventProcessor the event processor
	 * @return the cloudSim broker
	 * @throws Exception
	 * @throws
	 */
	private static CloudSimBroker getCloudSimBroker(double lifeLength, double monitoringPeriod, int stepPeriod, int appMonitorPeriod, int appEventPeriod, IWorkloadGenerator workloadGenerator, Collection<Monitor> monitors, EventProcessor eventProcessor) throws Exception {
		CloudSimBroker cloudSimBroker = new CloudSimBroker("auto_scaling", lifeLength, monitoringPeriod, stepPeriod, appMonitorPeriod, appEventPeriod, workloadGenerator, monitors, eventProcessor);
		return cloudSimBroker;
	}

	/**
	 * @Title: getHost 
	 * @Description: get the data center host
	 * @param numOfCore the number of cpu core
	 * @param mips the mips for each core
	 * @param ram the memory in MB
	 * @param bw the bandwidth in MB
	 * @param storage the stoarge in MB
	 * @param id the id of the host
	 * @return the host
	 * @throws
	 */
	private static Host getHost(int numOfCore, int mips, int ram, int bw, long storage, int id) {
		List<Pe> peList = new ArrayList<Pe>();
		
		for (int i = 0; i < numOfCore; i++) {
			peList.add(new Pe(Id.pollId(Pe.class), new PeProvisionerSimple(mips)));
		}
		
		return new Host(Id.pollId(Host.class), new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw), storage, peList,
				new VmSchedulerTimeShared(peList));
	}
	
	/**
	 * @Title: createDatacenter 
	 * @Description: create the data center
	 * @param name the name of the data center
	 * @param numOfHost the number of hosts
	 * @return the data center
	 * @throws Exception
	 * @throws
	 */
	private static DatacenterEX createDatacenter(String name, int numOfHost) throws Exception {
		List<Host> hostList = new ArrayList<Host>();

		int mips = 4000;
		int ram = 110592; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 1000000;

		for (int i = 0; i < numOfHost; i++) {
			hostList.add(getHost(16, mips, ram, bw, storage, i));
		}

		String arch = "x86";
		String os = "Linux/Unix";
		String vmm = "Xen";
		double time_zone = 10.0;
		double cost = 3.0;
		double costPerMem = 0.05;
		double costPerStorage = 0.001;
		double costPerBw = 0.0;
		LinkedList<Storage> storageList = new LinkedList<Storage>();
		
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		DatacenterEX datacenter = null;
		
		datacenter = new DatacenterEX(name, characteristics,
					new VmAllocationPolicySimple(hostList), storageList, 0);
		
		return datacenter;
	}
}
