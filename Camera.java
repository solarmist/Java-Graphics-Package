
import javax.media.opengl.glu.*;
import javax.media.opengl.*;
import static javax.media.opengl.GL2.*;
import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
/**
 * This camera is an implementation of a Camera for viewing a 3D scene in OpenGL.
 *
 * @author	Ryan Mauer
 * @version	20-Nov-2004
 */

public class Camera
{

///////////////////////////////////////////////////////////////////////////////
// STATIC VARIABLES

	/**
	 * Frustum viewing mode
	 */
	public static final int FRUSTUM_MODE = 0;

	/**
	 * Perspective viewing mode
	 */
	public static final int PERSPECTIVE_MODE = 1;
	public static final int ORTHOGRAPHIC_MODE = 2;

///////////////////////////////////////////////////////////////////////////////
// DATA MEMBERS

	/**
	 * position of the eye point
	 */
	public Double3D eye;

	/**
	 * position of the reference point
	 */
	public Double3D center;

	/**
	 * direction of the up vector
	 */
	public Double3D up;

	/**
	 * distance to near clipping plane
	 */
	public double near;

	/**
	 * distance to far clipping plane
	 */
	public double far;

	/**
	 * left extent of the window
	 */
	public double windowLeft;

	/**
	 * right extent of the window
	 */
	public double windowRight;

	/**
	 * top extent of the window
	 */
	public double windowTop;

	/**
	 * bottom extent of the window
	 */
	public double windowBottom;

	/**
	 * left extent of the viewport
	 */
	public double viewportLeft;

	/**
	 * right extent of the viewport
	 */
	public double viewportRight;

	/**
	 * top extent of the viewport
	 */
	public double viewportTop;

	/**
	 * bottom extent of the viewport
	 */
	public double viewportBottom;
	
	public double windowWidth;
	public double windowHeight;

	/**
	 * field of view used for perspective viewing
	 */
	public double fov;

	/**
	 * aspect ratio used for perspective viewing
	 */
	public double aspectRatio;

	/**
	 * whether or not the frustum has changed since the last call to look()
	 */
	public boolean frustumChanged;

	/**
	 * whether or not the perspective settings have changed since the last
	 * call to look()
	 */
	public boolean perspectiveChanged;
	
	public boolean orthoChanged;

	/**
	 * whether or not the camera has moved since the last call to look()
	 */
	public boolean cameraMoved;

	/**
	 * the current viewing mode (FRUSTUM_MODE or PERSPECTIVE MODE)
	 */
	public int viewingMode;

	/**
	 * holds information on which camera actions should be performed
	 */
	public boolean[] actions;

	/**
	 * the speed at which the camera moves
	 */
	public float moveSpeed;

	/**
	 * the speed at which the camera rotates
	 */
	public float rotateSpeed;

	/**
	 * the view matrix
	 */
	public double[] viewMat;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS

	public Camera(Double3D eye, Double3D center, Double3D up, double near, double far,
		double wLeft, double wRight, double wTop, double wBottom,
		double vLeft, double vRight, double vTop, double vBottom,
		double fov, double aspectRatio, int mode )
	{
		this.eye = eye;
		this.center = center;
		this.up = up;
		this.near = near;
		this.far = far;
		this.windowLeft = wLeft;
		this.windowRight = wRight;
		this.windowTop = wTop;
		this.windowBottom = wBottom;
		windowWidth = wRight - wLeft;
		windowHeight = wTop - wBottom;
		this.viewportLeft = vLeft;
		this.viewportRight = vRight;
		this.viewportTop = vTop;
		this.viewportBottom = vBottom;
		this.fov = fov;
		this.aspectRatio = aspectRatio;
		if(mode == FRUSTUM_MODE) {
			frustumChanged = true;
			perspectiveChanged = false;
		} // end if
		if(mode == PERSPECTIVE_MODE) {
			frustumChanged = false;
			perspectiveChanged = true;
		} // end if
		if(mode == ORTHOGRAPHIC_MODE){
			orthoChanged = false;
		}
		cameraMoved = true;
		viewingMode = mode;
		actions = new boolean[ CameraActions.NUM_ACTIONS ];
		moveSpeed = 0.5f;
		rotateSpeed = 0.025f;
		viewMat = MatrixOps.newIdentity();

	} // end constructor

///////////////////////////////////////////////////////////////////////////////
// OTHER METHODS

	/**
	 * This method applies any changes to the projection and modelview matrices
	 * so that any rendering after this call will be viewed using the current
	 * camera settings. Note: there is no need to multiply by a camera view
	 * matrix after this call has been made.
	 */
	public void look(GL2 gl, GLU glu)
	{
		// apply any changes to the frustum
		if( viewingMode == FRUSTUM_MODE && frustumChanged )
		{
			gl.glMatrixMode(gl.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glFrustum(windowLeft, windowRight, windowBottom, windowTop, near, far);
			frustumChanged = false;
		} // end if

		// apply any changes to the perspective settings
		if( viewingMode == PERSPECTIVE_MODE && perspectiveChanged )
		{
			gl.glMatrixMode(gl.GL_PROJECTION);
			gl.glLoadIdentity();
			glu.gluPerspective(fov, aspectRatio, near, far);
			perspectiveChanged = false;
		} // end if
		if( viewingMode == ORTHOGRAPHIC_MODE && orthoChanged )
		{
			gl.glMatrixMode(gl.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(windowLeft, windowRight, windowBottom, windowTop, near, far);
			orthoChanged = false;
		} // end if

		if( cameraMoved )
		{
			gl.glMatrixMode(gl.GL_MODELVIEW);
			gl.glLoadIdentity();
			glu.gluLookAt(
				eye.x, eye.y, eye.z, // eye position
				center.x, center.y, center.z, // reference point
				up.x, up.y, up.z ); // up vector
			gl.glGetDoublev(gl.GL_MODELVIEW_MATRIX, viewMat,0); // save view matrix
			cameraMoved = false;
		} // end if

	} // end method look

	/**
	 * This method updates the camera.
	 */
	public void update()
	{
		performActions(); // move based on input
		// FIX ME: update up vector?

	} // end method update

	/**
	 * This method performs the camera actions that are currently enabled
	 * by moving, strafing, or rotating the camera.
	 */
	public void performActions()
	{

		if( actions[ CameraActions.STRAFE_LEFT ] ) {
			strafeCamera(-moveSpeed);
		} // end if

		if( actions[ CameraActions.STRAFE_RIGHT ] ) {
			strafeCamera(moveSpeed);
		} // end if

		if( actions[ CameraActions.MOVE_FORWARD ] ) {
			moveCamera(moveSpeed);
		} // end if

		if( actions[ CameraActions.MOVE_BACKWARD ] ) {
			moveCamera(-moveSpeed);
		} // end if

		if( actions[ CameraActions.ROTATE_LEFT ] ) {
			rotateCamera(-rotateSpeed, 1); // rotate around Y
		} // end if

		if( actions[ CameraActions.ROTATE_RIGHT ] ) {
			rotateCamera(rotateSpeed, 1); // rotate around Y
		} // end if

		if( actions[ CameraActions.ROTATE_UP ] ) {
			rotateCamera(rotateSpeed, 0); // rotate around X
		} // end if

		if( actions[ CameraActions.ROTATE_DOWN ] ) {
			rotateCamera(-rotateSpeed, 0); // rotate around X
		} // end if

		if( actions[ CameraActions.STRAFE_UP ] ) {
			strafeCameraUpDown(moveSpeed);
		} // end if

		if( actions[ CameraActions.STRAFE_DOWN ] ) {
			strafeCameraUpDown(-moveSpeed);
		} // end if

	} // end checkInput

	/**
	 * This method strafes the camera. The distance is determined by the
	 * speed parameter. The direction is determined by the sign of the speed
	 * (positive for right and negative for left).
	 */
	public void strafeCamera(float speed)
	{
		Double3D viewVec = calcViewVector();
		Double3D strafe = viewVec.cross(up);
		strafe.unitize();
		strafe = strafe.sMult(speed); // scalar multiply by speed
		eye = eye.plus(strafe);
		center = center.plus(strafe);
		cameraMoved = true;

	} // end method strafeCamera

	/**
	 * This method strafes the camera up and down. The distance is determined by
	 * the speed parameter. The direction is determined by the sign of the speed
	 * (positive for up and negative for down).
	 */
	public void strafeCameraUpDown(float speed)
	{
		Double3D strafe = up.sMult(speed); // scalar multiply by speed
		eye = eye.plus(strafe);
		center = center.plus(strafe);
		cameraMoved = true;

	} // end method strafeCameraUpDown

	/**
	 * This method moves the camera forward or backwards. The distance is
	 * determined by the speed and the direction is determined by the sign
	 * of the speed (positive for forward and negative for backward).
	 */
	public void moveCamera(float speed)
	{
		Double3D viewVec = calcViewVector();
		viewVec = viewVec.sMult(speed); // scalar multiply by speed
		eye = eye.plus(viewVec);
		center = center.plus(viewVec);
		cameraMoved = true;

	} // end method moveCamera

	/**
	 * This method rotates the camera.
	 *
	 * @param speed	the rotation speed, can be positive or negative
	 * @param axis	the axis of rotation, 0 for X or 1 for Y
	 */
	public void rotateCamera(float speed, int axis)
	{
		float deltaX = speed * axis;
		float deltaY = speed * (1 - axis);

		Double3D viewVec = calcViewVector();
		Double3D strafe = viewVec.cross(up);
		strafe.unitize();

		viewVec.x += (strafe.x  * deltaX) + (up.x * deltaY);
		viewVec.y += (up.y * deltaY);
		viewVec.z += (strafe.z * deltaX) + (up.z * deltaY);

		viewVec.unitize();
		center = eye.plus(viewVec);
		cameraMoved = true;

	} // end method rotateCamera

	/**
	 * This method calculates and returns a normalized view vector using the
	 * current view position and reference point.
	 */
	public Double3D calcViewVector()
	{
		Double3D viewVec = center.minus(eye);
		viewVec.unitize();
		return viewVec;
	} // end method calcViewVector


///////////////////////////////////////////////////////////////////////////////
// Set methods
	public void setCamera(
	float eyeX, float eyeY, float eyeZ, // eye position
	float refX, float refY, float refZ, // reference point
	float upX, float upY, float upZ ) // up vector
{
	eye.x = eyeX;
	eye.y = eyeY;
	eye.z = eyeZ;
	center.x = refX;
	center.y = refY;
	center.z = refZ;
	up.x = upX;
	up.y = upY;
	up.z = upZ;
	cameraMoved = true;

} // end method setCamera

/**
 * This method adds the indicated values to the eye position.
 */
public void addToEye(float addX, float addY, float addZ)
{
	eye.x += addX;
	eye.y += addY;
	eye.z += addZ;
	cameraMoved = true;
} // end method addToEye

/**
 * This method adds the indicated values to the reference point.
 */
public void addToCenter(float addX, float addY, float addZ)
{
	center.x += addX;
	center.y += addY;
	center.z += addZ;
	cameraMoved = true;
} // end method addToCenter

/**
 * This method adds the indicated values to the up vector.
 */
public void addToUp(float addX, float addY, float addZ)
{
	up.x += addX;
	up.y += addY;
	up.z += addZ;
	cameraMoved = true;
} // end method addToUp

/**
 * This method sets the camera eye position.
 */
public void setEye(float eyeX, float eyeY, float eyeZ)
{
	eye.x = eyeX;
	eye.y = eyeY;
	eye.z = eyeZ;
	cameraMoved = true;
} // end method setEye

/**
 * This method sets the camera reference point.
 */
public void setCenter(float ctrX, float ctrY, float ctrZ)
{
	center.x = ctrX;
	center.y = ctrY;
	center.z = ctrZ;
	cameraMoved = true;
} // end method setCenter

/**
 * This method sets the camera up vector.
 */
public void setUp(float upX, float upY, float upZ)
{
	up.x = upX;
	up.y = upY;
	up.z = upZ;
	cameraMoved = true;
} // end method setUp

/**
 * This method sets the frustum of the camera to the specified values and
 * switches the camera to FRUSTUM_MODE. The new frustum values will be used
 * in the next call to look().
 */
public void setFrustum(
	double left, double right, double bottom, double top, // window extends
	double nr, double fr ) // near and far clipping plane
{
	windowLeft = left;
	windowRight = right;
	windowBottom = bottom;
	windowTop = top;
	windowWidth = right - left;
	windowHeight = top - bottom;
	near = nr;
	far = fr;
	viewingMode = FRUSTUM_MODE;
	frustumChanged = true;

} // end method setFrustum

/**
 * This method sets the perspective settings of the camera to the specified
 * values and switches the camera to PERSPECTIVE_MODE. The new perspective
 * settings will be used in the next call to look().
 */
public void setPerspective(double fieldOfView, double ratio, double nr, double fr )
{
	fov = fieldOfView;
	aspectRatio = ratio;
	near = nr;
	far = fr;
	viewingMode = PERSPECTIVE_MODE;
	perspectiveChanged = true;
	// FIX ME: update frustum values?

} // end method setPerspective

/**
 * This method set a flag indicating whether or not the specified camera action
 * should be performed based on input. The possible actions are defined in
 * CameraActions.
 *
 * @returns  valid  whether or not the action was valid
 */
public boolean setAction(int action, boolean enabled)

{
	if( action >= 0 && action < actions.length ) {
		actions[action] = enabled;
		return true;
	}
	else
		return false; // invalid action

} // end method setAction

/**
 * This method sets the extents of the viewport.
 */
public void setViewport(double left, double right, double top, double bottom)
{
	viewportLeft = left;
	viewportRight = right;
	viewportTop = top;
	viewportBottom = bottom;
	cameraMoved = true;

} // end method setViewport
	
///////////////////////////////////////////////////////////////////////////////
// Get mothids
public double getViewportWidth() {
	return viewportRight - viewportLeft;
} // end method getViewportWidth

public double getViewportHeight() {
	return viewportTop - viewportBottom;
} // end method getViewportHeight
public double getWindowHeight() {
	return windowTop - windowBottom;
} // end method getWindowHeight

public double getWindowWidth() {
	return windowRight - windowLeft;
} // end method getWindowWidth
	
///////////////////////////////////////////////////////////////////////////////
// OTHER METHODS

	/**
	 * This method returns whether or not the camera currently has any movement
	 * to perform based on input.
	 */
	public boolean hasActionsToPerform()
	{
		for(int i = 0; i < actions.length; i++)
			if(actions[i])
				return true;
		return false; // none were true
	} // end method hasActionsToPerform

	/**
	 * This method discards all actions that the camera was planning to perform.
	 * This may be useful for example when the canvas loses focus and the camera
	 * movement is no longer desired.
	 */
	public void discardAllActions()
	{
		for(int i = 0; i < actions.length; i++)
			actions[i] = false;
	} // end method discardAllActions


} // end class Camera
