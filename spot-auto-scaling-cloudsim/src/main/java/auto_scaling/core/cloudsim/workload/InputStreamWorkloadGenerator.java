package auto_scaling.core.cloudsim.workload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;

import auto_scaling.util.cloudsim.CloudletFactory;

/** 
* @ClassName: InputStreamWorkloadGenerator 
* @Description: work load generator from input stream
* @author Chenhao Qu
* @date 05/06/2015 9:04:58 pm 
*  
*/
public class InputStreamWorkloadGenerator implements IWorkloadGenerator{

	/** 
	* @Fields bufferSize : the buffer size
	*/ 
	protected int bufferSize;
	/** 
	* @Fields breader : the reader from the stream
	*/ 
	protected BufferedReader breader;
	/** 
	* @Fields cloudletInfos : the buffer
	*/ 
	protected Queue<CloudletInfo> cloudletInfos;
	/** 
	* @Fields baseTime : the start time of the workload
	*/ 
	protected long baseTime;
	/** 
	* @Fields waitingTime : waiting time after the simulation begins
	*/ 
	protected long waitingTime;
	
	/** 
	* <p>Description: initialize with all necessary parameters</p> 
	* @param inputStream the input stream
	* @param waitingTime the waiting time before the simulation begins
	* @param bufferSize the buffer size
	*/
	public InputStreamWorkloadGenerator(InputStream inputStream, long waitingTime, int bufferSize) {
		breader = new BufferedReader(new InputStreamReader(inputStream));
		cloudletInfos = new LinkedList<CloudletInfo>();
		this.bufferSize = bufferSize;
		baseTime = -1;
		this.waitingTime = waitingTime;
	}
	
	/** 
	* <p>Description: initialization with default buffer size</p> 
	* @param inputStream the input stream
	* @param waitingTime the waiting time before the simulation begins
	*/
	public InputStreamWorkloadGenerator(InputStream inputStream, long waitingTime) {
		this(inputStream, waitingTime, 100000);
	}

	/* (non-Javadoc) 
	* <p>Title: generateWorkload</p> 
	* <p>Description: </p> 
	* @param stepPeroid
	* @return 
	* @see auto_scaling.core.cloudsim.workload.IWorkloadGenerator#generateWorkload(int) 
	*/
	@Override
	public List<Cloudlet> generateWorkload(int stepPeroid) {
		
		List<Cloudlet> cloudlets = new ArrayList<Cloudlet>();
		
		double currentTime = CloudSim.clock();
		
		if (currentTime < waitingTime) {
			//System.out.println("waiting");
			return cloudlets;
		}
		
		double endTime = currentTime + stepPeroid;
		while (true) {
			if (cloudletInfos.isEmpty()) {
				prefetch();
				if (cloudletInfos.isEmpty()) {
					return null;
				}
			}
			CloudletInfo cloudletInfo = cloudletInfos.peek();
			double timeStamp = cloudletInfo.getTimeStamp();
			//System.out.println(timeStamp + " " + endTime);
			if (timeStamp > endTime) {
				break;
			}
			cloudlets.add(cloudletInfos.poll().getCloudlet());
		}
		
		//System.out.println(cloudlets.size());
		
		return cloudlets;
	}
	
	/** 
	* @Title: prefetch 
	* @Description: prefecth from the stream to the buffer
	*/
	private void prefetch() {
		CloudletFactory cloudletFactory = CloudletFactory.getCloudletFactory();
		for (int i = 0; i < bufferSize; i++) {
			try {
				String string = breader.readLine();
				if (string == null) {
					break;
				}
				String[] parts = string.split(" ");
				long time = Long.parseLong(parts[0])/1000;
				if (baseTime < 0) {
					baseTime = time;
					
				}
				
				double timeStamp = (time - baseTime) + waitingTime;
				//System.out.println(baseTime + " " + time);
				Cloudlet cloudlet = cloudletFactory.getCloudlet();
				
				cloudletInfos.add(new CloudletInfo(timeStamp, cloudlet));
			} catch (IOException e) {
				continue;
			}
		}
	}
	
	/* (non-Javadoc) 
	* <p>Title: close</p> 
	* <p>Description: </p>  
	* @see auto_scaling.core.cloudsim.workload.IWorkloadGenerator#close() 
	*/
	@Override
	public void close() {
		try {
			breader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 
	* @ClassName: CloudletInfo 
	* @Description: the data structure store cloudlet and its meta data
	* @author Chenhao Qu
	* @date 05/06/2015 9:22:36 pm 
	*  
	*/
	private class CloudletInfo {
		/** 
		* @Fields timeStamp : the submission time of the cloudlet
		*/ 
		private double timeStamp;
		/** 
		* @Fields cloudlet : the cloudlet
		*/ 
		private Cloudlet cloudlet;
		
		/** 
		* <p>Description: initialize with all fields</p> 
		* @param timeStamp the submission time of the cloudlet
		* @param cloudlet the cloudlet
		*/
		private CloudletInfo(double timeStamp, Cloudlet cloudlet) {
			this.timeStamp = timeStamp;
			this.cloudlet = cloudlet;
		}
		
		/** 
		* @Title: getTimeStamp 
		* @Description: get the submission time of the cloudlet
		* @return the submission time of the cloudlet
		*/
		public double getTimeStamp() {
			return timeStamp;
		}
		
		/** 
		* @Title: getCloudlet 
		* @Description: get the cloudlet
		* @return the cloudlet
		*/
		public Cloudlet getCloudlet() {
			return cloudlet;
		}
	}
}
