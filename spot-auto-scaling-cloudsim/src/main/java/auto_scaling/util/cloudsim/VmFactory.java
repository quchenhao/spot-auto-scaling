package auto_scaling.util.cloudsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.ex.vm.MonitoredVMex;
import org.cloudbus.cloudsim.ex.vm.VMMetadata;

import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.core.cloudsim.MyCloudletShedulerTimeShared;

/** 
* @ClassName: VmFactory 
* @Description: the factory that produces vms
* @author Chenhao Qu
* @date 07/06/2015 5:20:36 pm 
*  
*/
public class VmFactory {
	
	/** 
	* @Fields summaryPeriodLength : the summary period
	*/ 
	protected double summaryPeriodLength;
	/** 
	* @Fields vmFactory : the global vm factory
	*/ 
	private static VmFactory vmFactory = new VmFactory();
	/** 
	* @Fields userId : the user id
	*/ 
	private int userId;
	/** 
	* @Fields timeOut : the response time timeout
	*/ 
	private double timeOut;
	
	/** 
	* <p>Description: </p>  
	*/
	private VmFactory() {
		summaryPeriodLength = 1;
		timeOut = 30;
	}
	
	/**
	 * @Title: setSummaryPeriodLength 
	 * @Description: set the summary period length
	 * @param summaryPeriodLength the new summary period length
	 * @throws
	 */
	public void setSummaryPeriodLength(double summaryPeriodLength) {
		if (summaryPeriodLength <= 0) {
			throw new IllegalArgumentException("summary period length should be greater than 0");
		}
		
		this.summaryPeriodLength = summaryPeriodLength;
	}
	
	/**
	 * @Title: getVm 
	 * @Description: get the vm
	 * @param instanceTemplate the instance type
	 * @return the vm
	 * @throws
	 */
	public MonitoredVMex getVm(InstanceTemplate instanceTemplate) {
		
		int numberOfPes = instanceTemplate.getVcpuNum();
		double mips = instanceTemplate.getEcuNum() / numberOfPes * 1000;
		
		int ram = (int)(instanceTemplate.getMemoryNum() * 1024);
		
		VMMetadata vmMetadata = new VMMetadata();
		vmMetadata.setType(instanceTemplate.getName());
		vmMetadata.setOS(instanceTemplate.getOs());
		
		return new MonitoredVMex("app", userId, mips, numberOfPes, ram, 0, 0, "Xen", new MyCloudletShedulerTimeShared(timeOut), vmMetadata, summaryPeriodLength);
	}
	
	/**
	 * @Title: getVMs 
	 * @Description: get vms
	 * @param instanceTemplate the instance type
	 * @param num the num of vms
	 * @return num of vms
	 * @throws
	 */
	public List<MonitoredVMex> getVMs(InstanceTemplate instanceTemplate, int num) {
		List<MonitoredVMex> vms = new ArrayList<MonitoredVMex>();
		for (int i = 0; i < num; i++) {
			vms.add(getVm(instanceTemplate));
		}
		return vms;
	}
	
	/**
	 * @Title: getVmFactory 
	 * @Description: get the vm factory
	 * @return the vm factory
	 * @throws
	 */
	public static VmFactory getVmFactory() {
		return vmFactory;
	}
	
	/**
	 * @Title: setUserId 
	 * @Description: set the user id
	 * @param userId the user id
	 * @throws
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	/**
	 * @Title: setTimeOut 
	 * @Description: set timeout
	 * @param timeOut the timeout
	 * @throws
	 */
	public void setTimeOut(double timeOut) {
		this.timeOut = timeOut;
	}
}
