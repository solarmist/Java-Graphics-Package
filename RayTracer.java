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
		Double3D origin = new Double3D(0.0, 0.0, 10.0);
		HitRecord hit = new HitRecord();
		
		//Clear GL state
		gl.glDisable(GL_LIGHTING);
		gl.glDisable(GL_DEPTH);
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glMatrixMode(GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glClear(GL_DEPTH_BUFFER);
		gl.glClear(GL_COLOR_ARRAY);
		
		gl.glPointSize(1.0f);
		
		//Set the viewport
		glu.gluOrtho2D(scene.camera.viewportLeft, scene.camera.viewportRight, scene.camera.viewportTop,scene.camera.viewportBottom);
		
		//Transform into camera coords
		double widthRatio = (scene.camera.windowWidth) / (scene.camera.viewportRight - scene.camera.viewportLeft);
		double heightRatio = (scene.camera.windowHeight) / (scene.camera.viewportTop - scene.camera.viewportBottom);
		
		//Loop through all objects in the scene
		gl.glBegin(GL_POINTS);{
			for(int objNum = 0; objNum < scene.objects.size(); objNum++){
				PMesh mesh = (PMesh) scene.objects.elementAt(objNum);
				
				//Set the object's color
				DoubleColor color = mesh.materials[mesh.surfHead.material].kd;
				gl.glColor4d(color.r, color.g, color.b, color.a);
				
				//Default to opposite of spheresOnly for now
				if(!scene.spheresOnly)
					System.out.println("Spheres!");
				
				Sphere shape = new Sphere(mesh.boundingSphere);
				shape.center = shape.center.preMultiplyMatrix(scene.camera.viewMat);
				
System.out.println("Sphere" + shape.toString());			

System.out.println("Width" + widthRatio);
System.out.println("Height" + heightRatio);
System.out.println("x" + scene.camera.viewportLeft  + " worldX " + scene.camera.windowLeft);
System.out.println("y" + scene.camera.viewportBottom  + " worldY " + scene.camera.windowBottom);

				//Work though viewport (pixel) coordinates
				for(int x = (int) scene.camera.viewportLeft; x < scene.camera.viewportRight; x++){
					//0,0 in viewport would be -5,-6.32 in world
					double worldX = x * widthRatio + scene.camera.windowLeft ;
//System.out.println("x" + x + " worldX " + worldX);
					for(int y = (int) scene.camera.viewportBottom; y < scene.camera.viewportTop; y++){
						double worldY = y * heightRatio + scene.camera.windowBottom;
						Double3D dir = new Double3D(worldX, worldY, -1.0);
						Ray ray = new Ray(origin, dir);
						if(shape.hit(ray, 0, 1000000, 0, hit))
							gl.glVertex2i(x,y);
					}//for y
				}//for x
			}//for objects
		}
		gl.glEnd();
		
		//Restore state
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glPopMatrix();
		gl.glMatrixMode(GL_PROJECTION);
		gl.glPopMatrix();
		
		gl.glEnable(GL_LIGHTING);
		gl.glEnable(GL_DEPTH);
	
	}
}
