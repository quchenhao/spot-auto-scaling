package auto_scaling.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import auto_scaling.capacity.ICapacityCalculator;

/** 
* @ClassName: DefaultCapacityCalculatorLoader 
* @Description: default loader to load capacity calculator
* @author Chenhao Qu
* @date 04/06/2015 1:56:58 pm 
*  
*/
public class DefaultCapacityCalculatorLoader implements ICapacityCalculatorLoader{

	/* (non-Javadoc) 
	* <p>Title: load</p> 
	* <p>Description: </p> 
	* @param inputStream
	* @return
	* @throws InstantiationException
	* @throws IllegalAccessException
	* @throws ClassNotFoundException
	* @throws IOException 
	* @see auto_scaling.configuration.ICapacityCalculatorLoader#load(java.io.InputStream) 
	*/
	@Override
	public ICapacityCalculator load(InputStream inputStream) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(inputStream);
		
		String capacityCalculatorClass = properties.getProperty(CAPACITY_CALCULATOR);
		ICapacityCalculator capacityCalculator = (ICapacityCalculator)(Class.forName(capacityCalculatorClass).newInstance());
		return capacityCalculator;
	}

}
