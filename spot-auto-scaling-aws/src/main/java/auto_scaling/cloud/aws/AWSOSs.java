package auto_scaling.cloud.aws;

/** 
* @ClassName: AWSOSs 
* @Description: all AWS supported OS types
* @author Chenhao Qu
* @date 04/06/2015 12:27:55 pm 
*  
*/
public class AWSOSs {

	/** 
	* @Fields LINUX_UNIX : linux and unix
	*/ 
	public static final String LINUX_UNIX = "Linux/UNIX";
	/** 
	* @Fields SUSE_LINUX : suse linux
	*/ 
	public static final String SUSE_LINUX = "SUSE Linux";
	/** 
	* @Fields WINDOWS : windows
	*/ 
	public static final String WINDOWS = "Windows";
	
	/**
	 * @Title: isSupported 
	 * @Description: whether the os is supported by AWS
	 * @param os the os type
	 * @return whether the os is supported by AWS
	 * @throws
	 */
	public static boolean isSupported(String os) {
		if (os.equalsIgnoreCase(LINUX_UNIX) || os.equalsIgnoreCase(SUSE_LINUX) || os.equalsIgnoreCase(WINDOWS)) {
			return true;
		}
		return false;
	}
}
