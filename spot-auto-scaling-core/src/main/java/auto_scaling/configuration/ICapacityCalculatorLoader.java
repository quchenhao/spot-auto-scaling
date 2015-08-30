package auto_scaling.configuration;

import java.io.InputStream;

import auto_scaling.capacity.ICapacityCalculator;

/** 
* @ClassName: ICapacityCalculatorLoader 
* @Description: loader to load capacity calculator
* @author Chenhao Qu
* @date 04/06/2015 3:33:46 pm 
*  
*/
public interface ICapacityCalculatorLoader {

	/** 
	* @Fields CAPACITY_CALCULATOR : config item
	*/ 
	static final String CAPACITY_CALCULATOR = "capacity_calculator";
	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param inputStream the input stream
	 * @return the capacity calculator
	 * @throws Exception
	 * @throws
	 */
	public ICapacityCalculator load(InputStream inputStream) throws Exception;
}
