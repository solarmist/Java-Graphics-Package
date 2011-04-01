
/**
 * This class defines a set of possible camera actions. This allows the actions
 * to be considered by the Camera independent of their key mappings.  That is,
 * the camera doesn't care which keys are mapped to which action. Thus, it is
 * the program's responsibility to provide a key mapping to actions and use the
 * actions when communicating with the Camera.
 *
 * @author	Ryan Mauer
 * @version	20-Nov-2004
 */
public class CameraActions
{
	public static final int STRAFE_LEFT   = 0;
	public static final int STRAFE_RIGHT  = 1;
	public static final int MOVE_FORWARD  = 2;
	public static final int MOVE_BACKWARD = 3;
	public static final int ROTATE_LEFT   = 4;
	public static final int ROTATE_RIGHT  = 5;
	public static final int ROTATE_UP     = 6;
	public static final int ROTATE_DOWN   = 7;
	public static final int STRAFE_UP	  = 8;
	public static final int STRAFE_DOWN   = 9;

	public static final int NUM_ACTIONS = 10;

} // end class CameraActions