package auto_scaling.configuration.cloudsim;

import java.io.InputStream;

import auto_scaling.core.cloudsim.workload.IWorkloadGenerator;

/** 
* @ClassName: IWorkloadGeneratorLoader 
* @Description: loader to load workload generator
* @author Chenhao Qu
* @date 05/06/2015 2:42:02 pm 
*  
*/
public interface IWorkloadGeneratorLoader {

	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param input the input stream
	 * @return the workload generator
	 * @throws Exception
	 * @throws
	 */
	public IWorkloadGenerator load(InputStream input) throws Exception;
}
