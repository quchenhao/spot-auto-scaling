package auto_scaling.util.cloudsim;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.ex.util.Id;
import org.uncommons.maths.random.GaussianGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;

/** 
* @ClassName: CloudletFactory 
* @Description: the factory that generates cloudlets
* @author Chenhao Qu
* @date 07/06/2015 4:56:14 pm 
*  
*/
public class CloudletFactory {
	
	/** 
	* @Fields cloudletFactory : the global cloudlet factory
	*/ 
	private static CloudletFactory cloudletFactory;
	/** 
	* @Fields utilizationModel : the utilization model
	*/ 
	protected UtilizationModel utilizationModel;
	/** 
	* @Fields gaussianGenerator : the gaussian distribution
	*/ 
	protected GaussianGenerator gaussianGenerator;
	/** 
	* @Fields userId : the user id
	*/ 
	protected int userId;
	/** 
	* @Fields cloudletPool : the pool of cloudlet
	*/ 
	protected Queue<Cloudlet> cloudletPool;
	/** 
	* @Fields maxPoolSize : the maximum pool size
	*/ 
	protected int maxPoolSize;
	
	/** 
	* <p>Description: </p> 
	* @param seed the random
	* @param mean the mean of cloudlet length
	* @param standardDeviation  the sd of cloudlet length
	*/
	private CloudletFactory(byte[] seed, double mean, double standardDeviation) {
		Random merseneGenerator = new MersenneTwisterRNG(seed);
		gaussianGenerator = new GaussianGenerator(mean, standardDeviation, merseneGenerator);
		utilizationModel = new UtilizationModelFull();
		userId = -1;
		cloudletPool = new LinkedList<Cloudlet>();
		maxPoolSize = 1000000;
	}
	
	/**
	 * @Title: getCloudletFactory 
	 * @Description: get the cloudlet factory
	 * @return the cloudlet factory
	 * @throws
	 */
	public static CloudletFactory getCloudletFactory() {
		return cloudletFactory;
	}
	
	/**
	 * @Title: getCloudlet 
	 * @Description: get the cloudlet
	 * @return the cloudlet
	 * @throws
	 */
	public Cloudlet getCloudlet() {
		long cloudletLength = gaussianGenerator.nextValue().longValue();
		
		Cloudlet cloudlet = new Cloudlet(Id.pollId(Cloudlet.class), cloudletLength, 1, 1, 1, utilizationModel, utilizationModel, utilizationModel, false);
		cloudlet.setUserId(userId);
		return cloudlet;
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
	 * @Title: initialize 
	 * @Description: initialize the cloudlet
	 * @param seed the random seed
	 * @param mean the mean of cloudlet length
	 * @param standardDeviation the sd of the cloudlet length
	 * @throws
	 */
	public static void initialize(byte[] seed, double mean, double standardDeviation) {
		cloudletFactory = new CloudletFactory(seed, mean, standardDeviation);
	}
}
