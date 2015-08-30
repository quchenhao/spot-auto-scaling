package auto_scaling.core.cloudsim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import auto_scaling.cloud.InstanceTemplate;
import auto_scaling.util.cloudsim.TimeConverter;

/** 
* @ClassName: CloudSimSpotPriceSource 
* @Description: the source of spot market price in cloudSim
* @author Chenhao Qu
* @date 06/06/2015 12:35:35 pm 
*  
*/
public class CloudSimSpotPriceSource {

	/** 
	* @Fields spotTraceReaders : the reader for spot traces
	*/ 
	protected Map<InstanceTemplate, BufferedReader> spotTraceReaders;
	/** 
	* @Fields prevPoints : the prev points for each instance type
	*/ 
	protected Map<InstanceTemplate, SpotPricePoint> prevPoints;
	/** 
	* @Fields nextPoints : the next points for each instance type
	*/ 
	protected Map<InstanceTemplate, SpotPricePoint> nextPoints;
	/** 
	* @Fields cloudSimSpotPriceSource : the gloabl cloudSim spot price source
	*/ 
	private static CloudSimSpotPriceSource cloudSimSpotPriceSource = new CloudSimSpotPriceSource();
	
	/** 
	* <p>Description: empty initialization</p>  
	*/
	private CloudSimSpotPriceSource() {
		spotTraceReaders = new HashMap<InstanceTemplate, BufferedReader>();
		prevPoints = new HashMap<InstanceTemplate, SpotPricePoint>();
		nextPoints = new HashMap<InstanceTemplate, SpotPricePoint>();
	}
	
	/**
	 * @Title: getCloudSimSpotPriceSource 
	 * @Description: get the cloudSim spot price source
	 * @return the cloudSim spot price source
	 * @throws
	 */
	public static CloudSimSpotPriceSource getCloudSimSpotPriceSource() {
		
		return cloudSimSpotPriceSource;
	}
	
	/**
	 * @Title: initiateTraceReader 
	 * @Description: initiate trace reader
	 * @param instanceTemplate the instance type
	 * @param file the file path of the trace file
	 * @throws IOException
	 * @throws ParseException
	 * @throws
	 */
	public void initiateTraceReader(InstanceTemplate instanceTemplate, File file) throws IOException, ParseException {
		if (spotTraceReaders.containsKey(instanceTemplate)) {
			throw new IllegalArgumentException(instanceTemplate.toString() + " trace already initiated");
		}
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		
		spotTraceReaders.put(instanceTemplate, bufferedReader);
		
		SpotPricePoint prevPoint = getNextPoint(bufferedReader);
		prevPoints.put(instanceTemplate, prevPoint);
		
		SpotPricePoint nextPoint = getNextPoint(bufferedReader);
		nextPoints.put(instanceTemplate, nextPoint);
	}
	
	/**
	 * @Title: getNextPoint 
	 * @Description: get the next point
	 * @param bufferedReader the reader of the trace
	 * @return the market price history
	 * @throws IOException
	 * @throws ParseException
	 * @throws
	 */
	private SpotPricePoint getNextPoint(BufferedReader bufferedReader) throws IOException, ParseException {
		String line = bufferedReader.readLine();
		
		if (line == null) {
			return null;
		}
		
		String[] parts = line.split(" ");
		
		long time = Long.parseLong(parts[0]);
		Date timeStamp = new Date(time);
		double price = Double.parseDouble(parts[1]);
		
		return new SpotPricePoint(timeStamp, price);
	}
	
	/**
	 * @Title: getCurrentSpotPrice 
	 * @Description: get the current spot price
	 * @param instanceTemplate the instance type
	 * @param time the current time
	 * @return the spot market price
	 * @throws IOException
	 * @throws ParseException
	 * @throws
	 */
	public double getCurrentSpotPrice(InstanceTemplate instanceTemplate, double time) throws IOException, ParseException {
		Date currentTime = TimeConverter.convertSimulationTimeToDate(time);
		SpotPricePoint prevPoint = prevPoints.get(instanceTemplate);
		SpotPricePoint nextPoint = nextPoints.get(instanceTemplate);
		
		if (nextPoint == null) {
			return prevPoint.getPrice();
		}
		
		if (currentTime.after(nextPoint.getTimeStamp())) {
			BufferedReader bufferedReader = spotTraceReaders.get(instanceTemplate);
			while (true) {
				prevPoint = nextPoint;
				nextPoint = getNextPoint(bufferedReader);
				if (nextPoint == null) {
					bufferedReader.close();
					break;
				}
				if (currentTime.before(nextPoint.getTimeStamp())) {
					break;
				}
			}
			
			prevPoints.put(instanceTemplate, prevPoint);
			nextPoints.put(instanceTemplate, nextPoint);
		}
		
		return prevPoint.getPrice();
	}
	
	/** 
	* @ClassName: SpotPricePoint 
	* @Description: the spot price point
	* @author Chenhao Qu
	* @date 06/06/2015 1:27:50 pm 
	*  
	*/
	private class SpotPricePoint {
		
		/** 
		* @Fields timeStamp : the time stamp
		*/ 
		private Date timeStamp;
		/** 
		* @Fields price : the spot price
		*/ 
		private double price;
		
		/** 
		* <p>Description: </p> 
		* @param timeStamp the time stamp
		* @param price the spot price
		*/
		private SpotPricePoint(Date timeStamp, double price) {
			this.timeStamp = timeStamp;
			this.price = price;
		}
		
		/**
		 * @Title: getTimeStamp 
		 * @Description: get the time stamp
		 * @return the time stamp
		 * @throws
		 */
		public Date getTimeStamp() {
			return timeStamp;
		}
		
		/**
		 * @Title: getPrice 
		 * @Description: get the spot price
		 * @return the spot price
		 * @throws
		 */
		public double getPrice() {
			return price;
		}
	}
}
