package EWUPackage.GUI;

//Originally written by Maren Johnson
//Modified by Mark Haney to include Brent Heinz' object translation code
//and Jeremy Gross and Christine Talbot's camera translation code

import java.awt.*;
import java.awt.event.*;

import EWUPackage.panels.*;
import EWUPackage.scene.camera.*;
import EWUPackage.scene.primitives.*;

public class MouseControl implements MouseMotionListener, MouseListener, MouseWheelListener
{
	//create a MouseControl object wherever your canvas is created
	//			MouseControl mc = new MouseControl( refToObjectPanel )
	//ObjectPanel needs to stay pretty much the same so selecting w/ the mouse updates the object list
    //set mc as a mouse listener
	//			this.addMouseMotionListener( mc );
	//			this.addMouseListener( mc );
	//and the rest is taken care of here. :)
	//(All you need is my Camera class and to make the above additions to ObjectPanel)
	//written by: Maren Johnson

	ObjectPanel panel;
	Point beginCoord, endCoord;
	boolean isAnObjectSelected;
	//GL gl;
	//GLU glu;

	// INSERT for mouse object movement (Brent Heinz)
	//mousePosition variables
	public int startx, starty;
	public boolean moving = false;
	public boolean scaling = false;
	public boolean rotating = false;
	/*public boolean xLocked = false;
	public boolean yLocked = false;
	public boolean zLocked = false;*/
	public double angle1 = 0.0; /* in degrees */
	public double angle2 = 0.0; /* in degrees */
	public double xyScale = 32.0; // constant for XY movement scale value
	public double zScale = 0.5;
	// END INSERT

	// INSERT CAMERA MOVEMENT CODE (Jeremy Gross and Christine Talbot)
	public int mouseX;
	public int mouseY;
	// END INSERT

    // INSERT TRACKBALL CODE (Daniel Dawson)
    public static final int ONLY_X = 0, ONLY_Y = 1, ONLY_Z = 2, ROLL_XY = 3,
        TRACKBALL = 4;
    public int movMode = ROLL_XY;
    // END INSERT TRACKBALL CODE (Daniel Dawson)

	public MouseControl(ObjectPanel panelRef)
	{
		panel = panelRef;
	}


	public void mouseDragged(MouseEvent evt)
	{
		int x = evt.getX();
		int y = evt.getY();
		double dx = x - startx, dy = y - starty;

		//Begin rotate objects code
		if (panel.theScene.isObjectMovementEnabled() && rotating)
		{
			if(panel.theScene.hasCurObject())
			{

				angle1 = (x - startx);
				while (angle1 < 0.0)
					angle1 += 360.0;
				while (angle1 >= 360.0)
					angle1 -= 360.0;

				angle2 = dy;
				if (angle2 < -180.0)
					angle2 = -180.0;
				else if (angle2 > 180.0)
					angle2 = 180.0;

				// INSERT TRACKBALL CODE (Daniel Dawson)
				Camera cam = panel.theScene.camera;
				/* FIX ME: remove unused variables
				Double3D eye = cam.getEye();
				Double3D ctr = panel.theScene.curObject.center;
				Double3D up = cam.getUp();*/

				if (movMode == ONLY_X)
					panel.theScene.curObject.rotate(angle2*panel.scaling, 1.0, 0.0, 0.0);
				else if (movMode == ONLY_Y)
					panel.theScene.curObject.rotate(angle1*panel.scaling, 0.0, 1.0, 0.0);
				else if (movMode == ONLY_Z)
					panel.theScene.curObject.rotate(angle1*panel.scaling, 0.0, 0.0, 1.0);
				// These ones are completely my own.
				else if (movMode == ROLL_XY) {
					// Find the distance the mouse has moved along the viewport
					double absDist = Math.sqrt(dx*dx + dy*dy);
					// Construct the rotation axis (perp of mouse delta vector)
					Double3D viewAxis = new Double3D(dy, dx, 0.0);
					// Get the corresponding axis in world coordinates
					Double3D axis = viewingToWorld( viewAxis );
					panel.theScene.curObject.rotate(
									absDist*panel.scaling, axis.x, axis.y,
									axis.z);
				} else {  // movMode == TRACKBALL
					// Get height of viewport
					//double vpHeight = cam.getViewportHeight();
					// Find the rotation axis in viewing coordinates
					Double3D viewAxis = trackballDragToAxis(
									  startx, (int)cam.getViewportHeight() - starty,
									  x, (int)cam.getViewportHeight() - y, 200.0);
					// Get the rotation angle from viewAxis
					double angle = Math.toDegrees(
								  Math.asin(Math.sqrt(viewAxis.dot(viewAxis))));
					// Rotate the axis into world coordinates
					Double3D axis = viewingToWorld( viewAxis );
					panel.theScene.curObject.rotate(
									angle, axis.x, axis.y, axis.z);
				}
			}
			startx = x;
        	starty = y;
        	// END INSERT TRACKBALL CODE (Daniel Dawson)
		}
		//End rotate objects code

		//Begin translate objects code
		if (panel.theScene.isObjectMovementEnabled() && moving)
		{
			Camera curCamera = panel.theScene.camera;
			Double3D upVect = curCamera.up;
			Double3D viewVect = curCamera.calcViewVector();
			Double3D rightVect = viewVect.cross(upVect);
			/*Double3D upVect = new Double3D(curCamera.upX, curCamera.upY, curCamera.upZ);
			Double3D viewVect = new Double3D(curCamera.ctrX - curCamera.eyeX,
										curCamera.ctrY - curCamera.eyeY,
										curCamera.ctrZ - curCamera.eyeZ);
			viewVect.unitize();
			Double3D rightVect = viewVect.cross(upVect);*/

			//Double3D xVect = new Double3D(1.0, 0.0, 0.0);
			//Double3D yVect = new Double3D(0.0, 1.0, 0.0);
			//Double3D zVect = new Double3D(0.0, 0.0, -1.0);
			double xVal = 0.0;
			double yVal = 0.0;
			double zVal = 0.0;

			dx = (double) ( (x - startx)) / xyScale;
			dy = (double) ( (starty - y)) / xyScale;

			startx = x;
			starty = y;

			xVal = rightVect.x*dx + upVect.x*dy;
			yVal = rightVect.y*dx + upVect.y*dy;
			zVal = rightVect.z*dx + upVect.z*dy;

			if(panel.theScene.curObject != null)
			{
				panel.theScene.curObject.translate(xVal*panel.scaling, yVal*panel.scaling, zVal*panel.scaling);
			}
		}
		//End translate objects code

		//Begin scale objects code
		if (panel.theScene.isObjectMovementEnabled() && scaling)
		{
			dx = (double)x / startx;
			dy = (double)starty / y;

			startx = x;
			starty = y;

			if(panel.theScene.hasCurObject() && Math.abs(dx) > 0.01 && Math.abs(dy) > 0.01)
			{
				System.out.println("DX = " + dx + "\tDY = " + dy);
				if (movMode == ONLY_X)
					panel.theScene.curObject.scale(dx*panel.scaling, 1.0, 1.0);
				else if (movMode == ONLY_Y)
					panel.theScene.curObject.scale(1.0, dy*panel.scaling, 1.0);
				else if (movMode == ONLY_Z)
					panel.theScene.curObject.scale(1.0, 1.0, dy*panel.scaling);
				else   // movMode == ROLL_XY || movMode == TRACKBALL
					panel.theScene.curObject.scale(dx*panel.scaling,
								   dx*panel.scaling,
								   dx*panel.scaling);
			}
		}
		//End scale objects code

		//Begin camera rotating code
		if(!panel.theScene.isObjectMovementEnabled() && rotating)
		{
			// INSERT CAMERA MOVEMENT CODE (Jeremy Gross and Christine Talbot)
			//double camMat[] = new double[16];
			Double3D rightVec = new Double3D();
			Double3D upVec = panel.theScene.camera.up; //new Double3D();
			Double3D viewVec = panel.theScene.camera.calcViewVector(); //new Double3D();			//Added by Mark Haney for Z-rotation
			//upVec.x = panel.theScene.curCamera.upX;
			//upVec.y = /*Math.abs(*/ panel.theScene.curCamera.upY /*)*/;
			//upVec.z = panel.theScene.curCamera.upZ;
			//upVec.unitize();
			//viewVec.x = panel.theScene.curCamera.ctrX-panel.theScene.curCamera.eyeX;
			//viewVec.y = panel.theScene.curCamera.ctrY-panel.theScene.curCamera.eyeY;
			//viewVec.z = panel.theScene.curCamera.ctrZ-panel.theScene.curCamera.eyeZ;
			//viewVec.unitize();
			rightVec = viewVec.cross(upVec);
			rightVec.unitize();

System.out.println("Rotating Camera:\tup = " + upVec.toString() + "\tview = " + viewVec.toString() + "\tright = " + rightVec.toString());

				// INSERT TRACKBALL CODE (Daniel Dawson)
//				if (movMode == ONLY_X)
//				{
					//Double3D axis;// = new Double3D(1.0,0.0,0.0);
					//axis = rightVec.preMultiplyMatrix( panel.theScene.curCamera.viewMat );
					//axis.unitize();
					/*Double3D axis;
					if(upVec.y >= 0.0) {
						//if(upVec.z <= 0.0)
							axis = new Double3D( 1.0, 0.0, 0.0 );
						//else
						//	axis = new Double3D( -1.0, 0.0, 0.0 );
					}
					else {
						//if(upVec.z <= 0.0)
						//	axis =
						//else
						axis = new Double3D( -1.0, 0.0, 0.0 );
					}*/
/* FIX ME: RRM make trackball code work with new camera
					panel.theScene.curCamera.pendingCameraRotation = new Transform();
					panel.theScene.curCamera.pendingCameraRotation.rotate(
									  (mouseY-evt.getY())*panel.scaling,
									   rightVec.x, rightVec.y, rightVec.z);
					panel.theScene.curCamera.cameraRotated = true;
System.out.println("\t" + (mouseY-evt.getY())*panel.scaling + " Degrees around Axis " + rightVec.toString());
				}
				else if (movMode == ONLY_Y)
				{
					Double3D axis;// = new Double3D(0.0, 1.0, 0.0);
					axis = viewingToWorld( upVec ); // upVec.preMultiplyMatrix( panel.theScene.curCamera.viewMat );
					panel.theScene.curCamera.pendingCameraRotation = new Transform();
					panel.theScene.curCamera.pendingCameraRotation.rotate(
									  (mouseX-evt.getX())*panel.scaling,
									   upVec.x, upVec.y, upVec.z);
					panel.theScene.curCamera.cameraRotated = true;
System.out.println("\t" + (mouseY-evt.getY())*panel.scaling + " Degrees around Axis " + upVec.toString());
				}
				else if (movMode == ONLY_Z)
				{
					panel.theScene.curCamera.pendingCameraRotation = new Transform();
					panel.theScene.curCamera.pendingCameraRotation.rotate(
									  (mouseX-evt.getX())*panel.scaling,
									   viewVec.x, viewVec.y, viewVec.z);
					panel.theScene.curCamera.cameraRotated = true;
System.out.println("\t" + (mouseY-evt.getY())*panel.scaling + " Degrees around Axis " + viewVec.toString());
				}
END FIX ME RRM */
				/*else
				{  // movMode == ROLL_XY || movMode == TRACKBALL
					// *** Not working properly; hmm... ***
					Camera cam = panel.theScene.curCamera;
					Double3D eye = new Double3D(cam.eyeX, cam.eyeY, cam.eyeZ);
					Double3D ctr = new Double3D(cam.ctrX, cam.ctrY, cam.ctrZ);
					Double3D up = new Double3D(cam.upX, cam.upY, cam.upZ);
					dx = mouseX-evt.getX();
					dy = mouseY-evt.getY();
					double absAngle = Math.sqrt(dx*dx + dy*dy);
					Double3D viewAxis = new Double3D(dy, dx, 0.0);
					Double3D axis = viewingToWorld( viewAxis );
					panel.theScene.curCamera.pendingCameraRotation =
						new Transform();
					panel.theScene.curCamera.pendingCameraRotation.rotate(
									  absAngle, axis.x, axis.y, axis.z);
					panel.theScene.curCamera.cameraRotated = true;
				}*/
				// END INSERT

			mouseX=evt.getX();
			mouseY=evt.getY();

			System.out.println("MouseControl-camera rotation updateCameraFields()");
			panel.ctrlPanel.cameraPanel.updateCameraFields();
			//panel.theScene.updateLightsPosition();
		}
		//End camera rotating code

		//Begin camera panning code
		if(!panel.theScene.isObjectMovementEnabled() && moving)
		{
			Camera curCamera = panel.theScene.camera;
			Double3D eyeVect = curCamera.eye;
			Double3D ctrVect = curCamera.center;
			Double3D upVect = curCamera.up;
			Double3D viewVect = curCamera.calcViewVector();

			Double3D rightVect = viewVect.cross(upVect);

			dx = (double) ( (x - startx)) / xyScale;
			dy = (double) ( (starty - y)) / xyScale;

			startx = x;
			starty = y;

			rightVect.x *= dx*panel.scaling;
			rightVect.y *= dx*panel.scaling;
			rightVect.z *= dx*panel.scaling;
			upVect.x *= dy*panel.scaling;
			upVect.y *= dy*panel.scaling;
			upVect.z *= dy*panel.scaling;

			float newEyeX = (float)(eyeVect.x + rightVect.x + upVect.x);
			float newEyeY = (float)(eyeVect.y + rightVect.y + upVect.y);
			float newEyeZ = (float)(eyeVect.z + rightVect.z + upVect.z);

			float newCtrX = (float)(ctrVect.x + rightVect.x + upVect.x);
			float newCtrY = (float)(ctrVect.y + rightVect.y + upVect.y);
			float newCtrZ = (float)(ctrVect.z + rightVect.z + upVect.z);

			/*curCamera.eyeX += rightVect.x + upVect.x;
			curCamera.eyeY += rightVect.y + upVect.y;
			curCamera.eyeZ += rightVect.z + upVect.z;

			curCamera.ctrX += rightVect.x + upVect.x;
			curCamera.ctrY += rightVect.y + upVect.y;
			curCamera.ctrZ += rightVect.z + upVect.z;*/

			curCamera.setEye(newEyeX, newEyeY, newEyeZ);
			curCamera.setCenter(newCtrX, newCtrY, newCtrZ);

			//curCamera.setCamera(curCamera.eyeX, curCamera.eyeY, curCamera.eyeZ,
			//					curCamera.ctrX, curCamera.ctrY, curCamera.ctrZ,
			//					curCamera.upX, curCamera.upY, curCamera.upZ);
			System.out.println("MouseControl-camera panning updateCameraFields()");
			panel.ctrlPanel.cameraPanel.updateCameraFields();
			//panel.theScene.updateLightsPosition();
            System.out.println("updateLightsPosition() NOT CALLED from mouseDragged");
		}
		//End camera panning code

		panel.theScene.refreshCanvas();
	} //end mouseDragged

	public void mouseWheelMoved(MouseWheelEvent evt)
	{
		//wheel zooms (translates on z axis - up = neg z, dwn = pos z)
		if(panel.theScene.isObjectMovementEnabled())
		{
			Double3D viewVect = panel.theScene.camera.calcViewVector();
			/*Double3D viewVect = new Double3D(panel.theScene.curCamera.ctrX - panel.theScene.curCamera.eyeX,
										panel.theScene.curCamera.ctrY - panel.theScene.curCamera.eyeY,
										panel.theScene.curCamera.ctrZ - panel.theScene.curCamera.eyeZ);
			viewVect.unitize();*/

			viewVect.x *= evt.getWheelRotation()*zScale*panel.scaling;
			viewVect.y *= evt.getWheelRotation()*zScale*panel.scaling;
			viewVect.z *= evt.getWheelRotation()*zScale*panel.scaling;

			if(panel.theScene.hasCurObject())
				panel.theScene.curObject.translate(viewVect.x, viewVect.y, viewVect.z);
				//panel.theScene.curObject.translate(0.0, 0.0, (double) (evt.getWheelRotation()) * zScale);
		}
		else
		{
			// INSERT CAMERA MOVEMENT CODE (Jeremy Gross and Christine Talbot)
			Double3D zoom = panel.theScene.camera.calcViewVector();
			/*Double3D zoom=new Double3D();
			zoom.x=panel.theScene.curCamera.ctrX-panel.theScene.curCamera.eyeX;
			zoom.y=panel.theScene.curCamera.ctrY-panel.theScene.curCamera.eyeY;
			zoom.z=panel.theScene.curCamera.ctrZ-panel.theScene.curCamera.eyeZ;*/
			zoom=zoom.sMult(evt.getUnitsToScroll()*(0-1));
			if(zoom.x==0.0&&zoom.y==0.0&&zoom.z==0.0)
				zoom.z=1;
			zoom.unitize();
			zoom=zoom.sMult(50.0f);

			panel.theScene.camera.addToEye((float)zoom.x, (float)zoom.y, (float)zoom.z);
			/*panel.theScene.getCamera().setCamera(
				panel.theScene.curCamera.eyeX+zoom.x,
					panel.theScene.curCamera.eyeY+zoom.y,
						panel.theScene.curCamera.eyeZ+zoom.z,
				panel.theScene.curCamera.ctrX,
					panel.theScene.curCamera.ctrY,
						panel.theScene.curCamera.ctrZ,
				panel.theScene.curCamera.upX,
					panel.theScene.curCamera.upY,
						panel.theScene.curCamera.upZ
			);*/
			// END INSERT

			System.out.println("MouseControl-camera movement updateCameraFields()");
			panel.ctrlPanel.cameraPanel.updateCameraFields();
			//panel.theScene.updateLightsPosition();
            System.out.println("updateLightsPosition() NOT CALLED from mouseWheelMoved");
		}

		panel.theScene.refreshCanvas();
	} //end mouseWheelMoved

	public void mouseMoved   (MouseEvent event)
	{
	}

	public void mouseReleased(MouseEvent evt)
	{
		if (evt.getButton() == MouseEvent.BUTTON1) { //ie left click
			moving = false;
		} //end if stopped moving
		else if (evt.getButton() == MouseEvent.BUTTON2)
		{
			scaling = false;
		}
		else if (evt.getButton() == MouseEvent.BUTTON3) { //ie right click
			rotating = false;
		} //end if stopped rotating
	} //end mouseReleased

	public void mouseClicked (MouseEvent event)
	{
	}

	public void mouseEntered (MouseEvent event) { /*panel.theScene.requestFocus(); */}
	public void mouseExited  (MouseEvent event) {  }

	public void mousePressed(MouseEvent evt)
	{
		// BEGIN OBJECT MOVING CODE (Brent Heinz)
		//left button moves the object
		//right button rotates the object
		if (evt.getButton() == MouseEvent.BUTTON1) {
			moving = true;
			startx = evt.getX();
			starty = evt.getY();
		} //end if left button
		else if (evt.getButton() == MouseEvent.BUTTON2)
		{
			scaling = true;
			startx = evt.getX();
			starty = evt.getY();
		}
		else if (evt.getButton() == MouseEvent.BUTTON3) {
			rotating = true;
			startx = evt.getX();
			starty = evt.getY();
		}
		else
		{
			moving = false;
			scaling = false;
			rotating = false;
		} //end else stop moving and rotating
		// END OBJECT MOVING CODE

		// INSERT CAMERA MOVEMENT CODE (Jeremy Gross and Christine Talbot)
		mouseX = evt.getX();
		mouseY = evt.getY();
		panel.theScene.pickPending = (true);
		panel.theScene.pickInfo = (new Double3D((double)mouseX, (double)mouseY, 0.0));
		panel.theScene.refreshCanvas();
	}

	/**
	 * This method transforms a vector in viewing coordinates into world coordinates
	 * by pre-multiplying the vector by the inverse transpose of the camera view matrix.
	 * In the case that an inverse matrix does not exist, the original vector is returned
	 * without being transformed.
	 */
	public Double3D viewingToWorld( Double3D vector ) {
		Double3D world;
		try {
			world = vector.preMultiplyMatrix( MatrixOps.inverseTranspose(panel.theScene.camera.viewMat) );
		} // end try
		catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			world = vector;
			System.err.println("View axis not transformed into world space");
		} // end catch
		return world;
	} // end method viewingToWorld

	// INSERT TRACKBALL CODE (Daniel Dawson)

    /**
     * Maps two successive mouse positions to a sphere and returns the cross
     * product of the normalized vectors from the center to each of the mapped
     * points (whose magnitude is the sine of the angle of the arc of a great
     * circle determined by those points). Note that the sphere does not exist
     * in the world; it is merely an imaginary construct over the viewport.
     *
     * @param prevX     previous mouse X coordinate
     * @param prevY     previous mouse Y coordinate
     * @param curX      current mouse X coordinate
     * @param curY      current mouse Y coordinate
     * @param radius    radius of sphere in screen coordinates
     * @return {@link Double3D Double3D} - the cross product of the vectors
     *                  determined from the center and each of the points on
     *                  the sphere mapped from the mouse positions, in world
     *                  coordinates
     */
    public Double3D trackballDragToAxis (int prevX, int prevY,
                                       int curX, int curY, double radius) {
        // Set up
        Camera cam = panel.theScene.camera;

	// Here is something I tried that did not work out: the intent was to
	// map the center of the object being rotated to its proper place on
	// the viewport so mouse positions can be mapped WRT the object visible
	// on the screen as opposed to being mapped WRT a fixed point on the
	// screen, as the former seems more natural to me. However, something
	// is wrong with the projection part (I think). Maybe someone in the
	// future can try to get it right. Until then, this code is commented
	// out.

        //double winScaleX = cam.vpWidth/(cam.wRight - cam.wLeft);
        //double winScaleY = cam.vpHeight/(cam.wTop - cam.wBottom);
	//
        //// Build a model-view-projection matrix and apply to object's center
        //double[] matrix = MatrixOps.makePerspectiveMatrix(cam);
        //matrix = MatrixOps.multMat(matrix, cam.viewMat);
        //matrix = MatrixOps.multMat(matrix, obj.modelMat);
        //Double3D viewCenter = center.preMultiplyMatrix(matrix);
        //// Change to screen coordinates
        //double vpCenterX = (viewCenter.getX() - cam.wLeft)*winScaleX;
        //double vpCenterY = (viewCenter.getY() - cam.wBottom)*winScaleY;


	// Get the center of the viewport
	double vpCenterX = cam.getViewportWidth()/2.0, vpCenterY = cam.getViewportHeight()/2.0;

	/*
	 * Map the mouse points onto the sphere - this is just an inverse
	 * orthographic projection: X and Y are just translated based on
	 * vpCenterX and vpCenterY, and Z is calculated using the sphere
	 * equation.
	 *
	 * If a point is outside the radius, we just set Z = 0.0; this results
	 * in radial motion being ignored, but angular motion about the viewing
	 * Z-axis still has an effect (specifically, the object rotates purely
	 * about the viewing Z-axis).
	 */

	// Map X and Y coordinates to coordinates relative to the center
	// (Some of this is just using more variables to simplify later exprs.)
        double spherePrevX = prevX - vpCenterX;
        double spherePrevY = prevY - vpCenterY;
        double spherePrevDistSqr =
	    spherePrevX*spherePrevX + spherePrevY*spherePrevY;

        double sphereCurX = curX - vpCenterX;
        double sphereCurY = curY - vpCenterY;
        double sphereCurDistSqr =
	    sphereCurX*sphereCurX + sphereCurY*sphereCurY;

        double radiusSqr = radius*radius;

	// Figure out Z coordinates
	// If spherePrevDistSqr <= radiusSqr, we are inside the radius, and can
	// thus get a meaningful result from the sphere eq.
	//  x^2 + y^2 + z^2 = r^2
	//  solving for z, we get z = sqrt( r^2 - (x^2 + y^2) )
	// Outside the radius, z = 0
        double zPrev, zCur;
        if (spherePrevDistSqr <= radiusSqr)  // within sphere radius
        // map onto sphere
            zPrev = Math.sqrt(radiusSqr - spherePrevDistSqr);
        else                                 // outside sphere radius
        // set Z=0
            zPrev = 0.0;

        if (sphereCurDistSqr <= radiusSqr)   // within sphere radius
        // map onto sphere
            zCur = Math.sqrt(radiusSqr - sphereCurDistSqr);
        else                                 // outside sphere radius
        // set Z=0
            zCur = 0.0;

	// Construct vectors from coordinates
        Double3D spherePrev = new Double3D(spherePrevX, spherePrevY, zPrev);
        spherePrev.unitize();
        Double3D sphereCur = new Double3D(sphereCurX, sphereCurY, zCur);
        sphereCur.unitize();

        // Calculate cross product
        return spherePrev.cross(sphereCur);
	// Note: by the properties of cross product, this vector's direction is
	// the positive direction (by right-hand rule) of the rotation vector
	// determined by the vectors, while its magnitude is the sine of the
	// angle subtending the arc of a great circle on the sphere determined
	// by the mapped points.
    } // end trackballDragToAxis()

// END INSERT TRACKBALL CODE (Daniel Dawson)

}
