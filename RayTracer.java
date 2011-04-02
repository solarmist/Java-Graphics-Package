/*
 * This class starts and controls the ray tracer 
 * 
 * @author Joshua Olson
 * @version	1-Apr-2011
 */
import static javax.media.opengl.GL2.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;



public class RayTracer 
{
	RayTracer()
	{
		  
	}
	
	RayTracer(Scene scene, GL2 gl, GLU glu)
	{
		gl.glPointSize(100);
		gl.glColor3f(0.2f, 0.2f, 0.8f);
		gl.glBegin(GL.GL_POINTS);
		gl.glVertex2f(100, 100);
		gl.glEnd();
	}
}
