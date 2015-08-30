package auto_scaling.core;

/** 
* @ClassName: FaultTolerantLevel 
* @Description: the fault tolerant level
* @author Chenhao Qu
* @date 05/06/2015 2:48:21 pm 
*  
*/
public class FaultTolerantLevel {

	/** 
	* @Fields ZERO : fault tolerant level zero
	*/ 
	public static final FaultTolerantLevel ZERO = new FaultTolerantLevel(0);
	/** 
	* @Fields ONE : fault tolerant level one
	*/ 
	public static final FaultTolerantLevel ONE = new FaultTolerantLevel(1);
	/** 
	* @Fields TWO : fault tolerant level two
	*/ 
	public static final FaultTolerantLevel TWO = new FaultTolerantLevel(2);
	/** 
	* @Fields THREE : fault tolerant level three
	*/ 
	public static final FaultTolerantLevel THREE = new FaultTolerantLevel(3);
	/** 
	* @Fields MAX : fault tolerant level four
	*/ 
	public static final FaultTolerantLevel MAX = THREE;
	
	public static final String ZERO_STRING = "zero";
	public static final String ONE_STRING = "one";
	public static final String TWO_STRING = "two";
	public static final String THREE_STRING = "three";
	
	protected int level;
	
	/** 
	* <p>Description: </p> 
	* @param level the fault tolerant level in int
	*/
	public FaultTolerantLevel(int level) {
		if (level < 0) {
			throw new IllegalArgumentException("nmber of spot instance type must be nonnegative");
		}
		this.level = level;
	}
	
	/**
	 * @Title: getLevel 
	 * @Description: the fault tolerant level in int
	 * @return
	 * @throws
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * @Title: getFaultTolerantLevel 
	 * @Description: get fault tolerant level according to string
	 * @param level the fault tolerant level string
	 * @return the corresponding fault tolerant level (null if unknown)
	 * @throws
	 */
	public static FaultTolerantLevel getFaultTolerantLevel(String level) {
		if (level.equalsIgnoreCase(ZERO_STRING)) {
			return ZERO;
		}
		if (level.equalsIgnoreCase(ONE_STRING)) {
			return ONE;
		}
		if (level.equalsIgnoreCase(TWO_STRING)) {
			return TWO;
		}
		if (level.equalsIgnoreCase(THREE_STRING)) {
			return THREE;
		}
		
		return null;
	}
}
