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
		Double3D origin = new Double3D(0.0, 0.0, 0.0);
		HitRecord rec = new HitRecord();
		
		//Clear GL state
		gl.glDisable(GL_LIGHTING);
		gl.glDisable(GL_DEPTH);
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glMatrixMode(GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		
		gl.glClear(GL_DEPTH_BUFFER_BIT|GL_COLOR_BUFFER_BIT); 
		
		gl.glPointSize(1.0f);
		
		//Set the viewport
		glu.gluOrtho2D(scene.camera.viewportLeft, scene.camera.viewportRight, scene.camera.viewportTop,scene.camera.viewportBottom);
		
		//Transform into camera coords
		double widthRatio = (scene.camera.windowWidth) / (scene.camera.viewportRight - scene.camera.viewportLeft);
		double heightRatio = (scene.camera.windowHeight) / (scene.camera.viewportTop - scene.camera.viewportBottom);
		Sphere [] shapes = new Sphere[scene.objects.size()];
		DoubleColor [] colors = new DoubleColor[scene.objects.size()];
		//Loop through all objects in the scene
		
			for(int objNum = 0; objNum < scene.objects.size(); objNum++){
				PMesh mesh = (PMesh) scene.objects.elementAt(objNum);

				//Default to opposite of spheresOnly for now
				if(!scene.spheresOnly)
					System.out.println("Spheres!");
				
				//Set the object's color
				colors[objNum] = mesh.materials[mesh.surfHead.material].kd;
				
				shapes[objNum] = new Sphere(mesh.boundingSphere);
				
				//Apply the camera transform the point
				shapes[objNum].center = shapes[objNum].center.preMultiplyMatrix(scene.camera.viewMat);
				System.out.println("Sphere" + shapes[objNum].toString());
			}
			
		gl.glBegin(GL_POINTS);{		
			
System.out.println("Width" + widthRatio);
System.out.println("Height" + heightRatio);
System.out.println("x" + scene.camera.viewportLeft  + " worldX " + scene.camera.windowLeft);
System.out.println("y" + scene.camera.viewportBottom  + " worldY " + scene.camera.windowBottom);

				//Work though viewport (pixel) coordinates
			for(int x = (int) scene.camera.viewportLeft; x < scene.camera.viewportRight; x++){
				//0,0 in viewport would be -6.90,-5 in world
				double worldX = x * widthRatio + scene.camera.windowLeft ;
//System.out.println("x" + x + " worldX " + worldX);
				for(int y = (int) scene.camera.viewportBottom; y < scene.camera.viewportTop; y++){
					double worldY = y * heightRatio + scene.camera.windowBottom;
					double tMax = 10000000.0;
					Double3D dir = new Double3D(worldX, worldY, -1.0);
					dir = dir.getUnit();
					Ray ray = new Ray(origin, dir);
					for(int objNum = 0; objNum < scene.objects.size() ; objNum++)
						if(shapes[objNum].hit(ray, 0, tMax, 0, rec)){
							gl.glColor4d(colors[objNum].r, colors[objNum].g, colors[objNum].b, colors[objNum].a);
							gl.glVertex2i(x,y);
							tMax = rec.t;
					}//for objects
				}//for y
			}//for x
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
