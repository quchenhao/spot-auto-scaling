package auto_scaling.configuration.cloudsim;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import auto_scaling.core.cloudsim.workload.IWorkloadGenerator;
import auto_scaling.core.cloudsim.workload.InputStreamWorkloadGenerator;

/** 
* @ClassName: GZIPFileWorkloadGeneratorLoader 
* @Description: loader to workload generator from a gzip file 
* @author Chenhao Qu
* @date 05/06/2015 2:40:10 pm 
*  
*/
public class GZIPFileWorkloadGeneratorLoader implements IWorkloadGeneratorLoader {

	protected static final String BUFFER_SIZE = "buffer_size";
	protected static final String TRACE_FILE = "trace_file";
	protected static final String WAITING_TIME = "waiting_time";
	
	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param input
	* @return
	* @throws IOException 
	* @see auto_scaling.configuration.cloudsim.IWorkloadGeneratorLoader#load(java.io.InputStream) 
	*/
	@Override
	public IWorkloadGenerator load(InputStream input) throws IOException {
		Properties properties = new Properties();
		
		properties.load(input);
		
		String traceFile = properties.getProperty(TRACE_FILE);
		InputStream inputStream = new GZIPInputStream(new FileInputStream(traceFile));
		
		long waitingTime = Long.parseLong(properties.getProperty(WAITING_TIME));
		
		if (properties.containsKey(BUFFER_SIZE)) {
			String bufferSize = properties.getProperty(BUFFER_SIZE);
			return new InputStreamWorkloadGenerator(inputStream, waitingTime , Integer.parseInt(bufferSize));
		}
		
		return new InputStreamWorkloadGenerator(inputStream, waitingTime);
	}

}
