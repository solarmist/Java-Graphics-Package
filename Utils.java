/**
 * This class provides a place for miscellaneous utility methods.
 * All methods of this class should be static.
 *
 * @version 6-Mar-2005
 */
public class Utils
{

	/**
	 * This method is responsible for stripping the filename out of the entire
	 * path.  It is set up to work both in Windows (with the backslash) and
	 * Linux (with the forwardslash).
	 *
	 * @param filePath String
	 * @return String
	 */
	public static String fileFromPath(String filePath) {
		int lastBackSlash = filePath.lastIndexOf("\\"); // Find the last backslash in the path
		int lastForwardSlash = filePath.lastIndexOf("/"); // Find the last forwardslash in the path

		// This method assumes that whatever type of slash occurs last in the path
		// is the type of slash that is being used...in other words, the filename
		// itself comes after the last slash, no matter what type it is.
		if (lastBackSlash > lastForwardSlash)
			return filePath.substring(lastBackSlash + 1);
		else
			return filePath.substring(lastForwardSlash + 1);
	} // end method fileFromPath
	
	public static String dirFromPath(String filePath){
		int lastBackSlash = filePath.lastIndexOf("\\"); // Find the last backslash in the path
		int lastForwardSlash = filePath.lastIndexOf("/"); // Find the last forwardslash in the path

		// This method assumes that whatever type of slash occurs last in the path
		// is the type of slash that is being used...in other words, the filename
		// itself comes after the last slash, no matter what type it is.
		if (lastBackSlash > lastForwardSlash)
			return filePath.substring(0, lastBackSlash + 1);
		else
			return filePath.substring(0, lastForwardSlash + 1);		
	}

} // end method Utils
