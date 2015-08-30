package auto_scaling.configuration;

import java.io.InputStream;

import auto_scaling.online.IOnlineTask;

/** 
* @ClassName: IOnlineTaskLoader 
* @Description: loader to load online task
* @author Chenhao Qu
* @date 05/06/2015 11:15:50 am 
*  
*/
public interface IOnlineTaskLoader {
	/**
	 * @Title: load 
	 * @Description: load from input stream
	 * @param inputStream the input stream
	 * @return the input stream
	 * @throws Exception
	 * @throws
	 */
	public IOnlineTask load(InputStream inputStream) throws Exception;
}
