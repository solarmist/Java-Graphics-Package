package EWUPackage.scene;

import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.*;
import javax.media.opengl.*;

import EWUPackage.scene.camera.*;
import EWUPackage.scene.primitives.*;


/**
 * This class provides a sphere that is drawable using OpenGL.
 *
 * @version 5-Feb-2005
 */
public class DrawingSphere
{
	public int detail;
	public double radius;
	public float color [];
	public Double3D center;

	public DrawingSphere()
	{
		detail = 20;
		radius = 1.0;
		color = new float[3];
		color[0] = 0.8f;
		color[1] = 0.4f;
		color[2] = 0.4f;
		color[3] = 1.0f;
		center = new Double3D(0.0f, 0.0f, 0.0f);
	} // end constructor

	public DrawingSphere(int detail, double radius, float red, float green, float blue, Double3D center)
	{
		this.detail = detail;
		this.radius = radius;
		color = new float[4];
		color[0] = red;
		color[1] = green;
		color[2] = blue;
		color[3] = 1.0f;
		this.center = center;
	} // end constructor

	public void draw(Camera camera, GL2 gl, GLU glu)
	{
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glMultMatrixd(camera.viewMat,0);
		GLUquadric sphere = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(sphere, GLU.GLU_FILL);
		gl.glPushMatrix();
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, color,0);
		gl.glTranslated(center.x, center.y, center.z);
		glu.gluSphere(sphere, radius, detail, detail);
		gl.glPopMatrix();
	} // end method draw

} // end class DrawingSphere
