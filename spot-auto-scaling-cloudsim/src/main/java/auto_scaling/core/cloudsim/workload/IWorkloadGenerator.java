package auto_scaling.core.cloudsim.workload;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;

/** 
* @ClassName: IWorkloadGenerator 
* @Description: workload generator for the simulation
* @author Chenhao Qu
* @date 05/06/2015 9:25:09 pm 
*  
*/
public interface IWorkloadGenerator {

	/** 
	* @Title: generateWorkload 
	* @Description: generate the workload within each step period
	* @param stepPeriod the step time period
	* @return the generated cloudlets
	*/
	public List<Cloudlet> generateWorkload(int stepPeriod);
	/** 
	* @Title: close 
	* @Description: close the workload generator
	*/
	public void close();
}
