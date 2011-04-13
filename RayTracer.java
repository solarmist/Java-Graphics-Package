/*
 * This class starts and controls the ray tracer 
 * 
 * @author Joshua Olson
 * @version	1-Apr-2011
 */
import static javax.media.opengl.GL2.*;

import java.util.Vector;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;

public class RayTracer implements Runnable
{
	final int THREADS = 8;
	DoubleColor viewPort[][];
	Scene scene;
	PMesh[] shapes;
	Sphere[] spheres;
	Light[] lights;
	double heightRatio, widthRatio;
	double vpWidth, vpHeight;
	int numObjects;
	GLU glu;
	GL2 gl;
	
	RayTracer()
	{}
	
	RayTracer(Scene theScene, GL2 _gl, GLU _glu)//Threaded?
	{
		gl = _gl;
		glu = _glu;
		scene = theScene;
		lights = scene.lights;
		
		//Transform into camera coords
		vpWidth = scene.camera.viewportRight - scene.camera.viewportLeft;
		vpHeight = scene.camera.viewportTop - scene.camera.viewportBottom;
		widthRatio = scene.camera.windowWidth / vpWidth;
		heightRatio = scene.camera.windowHeight / vpHeight;
				
		viewPort = new DoubleColor[(int)vpWidth][(int)vpHeight];
		for(int i = 0; i < vpWidth; i++)
			for(int j = 0; j < vpHeight; j++)
				viewPort[i][j] = new DoubleColor(0.0, 0.0, 0.0, 1.0);
		
		numObjects = scene.objects.size();
		shapes = new PMesh[numObjects];
		spheres = new Sphere[numObjects];
		
		//Loop through all objects in the scene and store them in the object so we don't need to access it from generic objects
		for(int objNum = 0; objNum < numObjects; objNum++){
			shapes[objNum] = (PMesh) scene.objects.elementAt(objNum);

			spheres[objNum] = new Sphere(shapes[objNum].boundingSphere);
			//Apply the camera transform the point
			spheres[objNum].center = spheres[objNum].center.preMultiplyMatrix(scene.camera.viewMat);
		}		
	}
	
	public void run()
	{	
		Renderer r[] = new Renderer[THREADS];
		Thread t[] = new Thread[THREADS];
		
		int widthOfThread = (int)vpWidth / THREADS;
		int left = (int) scene.camera.viewportLeft;
		int right = left + widthOfThread;
		for(int i = 0; i < THREADS; i++)
		{
			if(i + 1 == THREADS)
				right = (int) scene.camera.viewportRight;
		
			r[i] = new Renderer(left, right,
								(int)scene.camera.viewportBottom, (int)scene.camera.viewportTop ) ;
			t[i] = new Thread(r[i]);
			t[i].start();

			left = right;
			right += widthOfThread;
		}
			
		boolean finishedRendering = false;
		while(!finishedRendering)
		{
			finishedRendering = true;
			
			for(int i = 0; i < THREADS; i++)
				if(t[i].isAlive())
					finishedRendering = false;
		}
		draw(); //Once all threads have finished draw
	}
	
	public class Renderer implements Runnable
	{
		//Portion of the viewport to render
		int xMin, xMax;
		int yMin, yMax;
		int curX = 0, curY = 0;
		
		Renderer()
		{
			xMin = (int)scene.camera.viewportLeft;
			xMax = (int)scene.camera.viewportRight; 
			yMin = (int)scene.camera.viewportBottom; 
			yMax = (int)scene.camera.viewportTop;
		}
		
		Renderer(int _xMin, int _xMax, int _yMin, int _yMax)
		{
			xMin = _xMin;
			xMax = _xMax; 
			yMin = _yMin; 
			yMax = _yMax;
		}
		
		@Override
		public void run()
		{
			//Always tracing from the origin here.
			Double3D origin = new Double3D(0.0, 0.0, 0.0);
			
			//Work though viewport (pixel) coordinates
			double worldY = scene.camera.windowBottom + yMin * heightRatio;
			for(curY = yMin; curY < yMax; curY++){
				worldY += heightRatio;
				double worldX = scene.camera.windowLeft + xMin * widthRatio;
				for(curX = xMin; curX < xMax; curX++){
					//0,0 in viewport would be -6.90,-5 in world
					worldX += widthRatio;
					Double3D dir = new Double3D(worldX, -worldY, -scene.camera.near);
					dir = dir.getUnit();
					Ray ray = new Ray(origin, dir);
								
					viewPort[curX][curY] = trace(ray, 0);	//Start at 0 recursive depth
				}//for y
			}//for x
		}
		
		//All rays we deal with here are in world coordinates.
		DoubleColor trace(Ray ray, int rDepth)
		{
			//if(rDepth > scene.maxRecursiveDepth)
			//	return new DoubleColor(0.1, 0.1, 0.1, 1.0);
			
			DoubleColor color = new DoubleColor(0.1, 0.1, 0.1, 1.0);
			double tMin = 0.00001;
			double tMax = 10000000;
			HitRecord hit = new HitRecord();
			PMesh obj;
			
			//Spheres only for now
			//if(!scene.spheresOnly)
				for(int i = 0; i < numObjects; i++)
					if(spheres[i].hit(ray, tMin, tMax, 0, hit))
					{
						tMax = hit.t;
						hit.index = i;
					}
			//Go check for intersection with the bounding sphere, then check for all triangles
			//else
			//	return color;
		
			//Find nearest intersection with scene
			//Compute intersection point and normal
			if(hit.index >= 0)
			{
				obj = shapes[hit.index];
				//If it intersects then multi-sample
				color = shade(hit.hitP, hit.normal, rDepth, obj.materials[obj.surfHead.material]);
			}
			return color;
		}
		
		boolean shadowTrace(Ray ray)
		{
			//Spheres only for now
			if(!scene.spheresOnly)
				for(int i = 0; i < numObjects; i++)
					//Does it ever hit anything? No? Then the path to the light is clear
					if(spheres[i].shadowHit(ray, 0.00001, 10000000, 0))
						return false;
			//Go check for intersection with the bounding sphere, then check for all triangles
			else
				return false;
			
			return true;
		}
		
		//iPoint is the point of intersection with the surface.
		DoubleColor shade(Double3D iPoint, Double3D surfNormal, int rDepth, MaterialCell material)
		{
			DoubleColor color = new DoubleColor(0.0, 0.0, 0.0, 0.0);
			
			//Add ambient light only once
			color.plus(new DoubleColor( (double)(lights[0].ambient[0] * material.ka.r), (double)(lights[0].ambient[1] * material.ka.g), 
										(double)(lights[0].ambient[2] * material.ka.b), (double)(lights[0].ambient[3] * material.ka.a) ));
			
			//Assign material color?
			//Local light or directional? If directional then we need to see if it's shining on the object
			for(int i = 0; i < lights.length ; i++){
				if(lights[i].lightSwitch == 1){
					Ray light = new Ray(iPoint, new Double3D((double)lights[i].direction[0], (double)lights[i].direction[1], (double)lights[i].direction[2]) );
				//trace shadow ray to light source
					
					/*if(shadowTrace(light))
					{
						double cosineTheta = surfNormal.dot(light.data[1]);
						Double3D Pr = surfNormal.sMult(surfNormal.dot(light.data[1].sMult(-1)));
						Double3D reflect = light.data[1].minus(surfNormal.sMult(2 * surfNormal.dot(light.data[1])));
						//double cosinePhi = reflec.dot()
						//If the light is free add the diffuse light
						//Intensity (Kd * (L . N) + Ks *(R.V)^n/(r + k)
						color.plus(new DoubleColor( (double)lights[i].diffuse[0], (double)lights[i].diffuse[1], 
													(double)lights[i].diffuse[2], (double)lights[i].diffuse[3]) );
					}//*/
				}
			}
			//if specular
			//color = color + trace(ray from iPoint in direction of reflected/refracted, rDepth + 1)
			
			return color;
		}
	}
	
	public void draw()
	{
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
		glu.gluOrtho2D(scene.camera.viewportLeft, scene.camera.viewportRight, scene.camera.viewportTop, scene.camera.viewportBottom);
		
		gl.glBegin(GL_POINTS);
		
		for(int x = (int)scene.camera.viewportLeft; x < (int)scene.camera.viewportRight; x++)
			for(int y = (int)scene.camera.viewportBottom; y < (int)scene.camera.viewportTop; y++)
			{
				gl.glColor3d(viewPort[x][y].r, viewPort[x][y].g, viewPort[x][y].b);
				gl.glVertex2i(x,y);
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
