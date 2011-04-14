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
	boolean calcViewCoords = true;
	GLU glu;
	GL2 gl;
	
	RayTracer()
	{}
	
	RayTracer(Scene theScene, GL2 _gl, GLU _glu)//Threaded?
	{
		System.out.println("Init RayTracer");
		gl = _gl;
		glu = _glu;
		scene = theScene;
		lights = scene.lights;
		
		//Transform into camera coords
		vpWidth = scene.camera.viewportRight - scene.camera.viewportLeft;
		vpHeight = scene.camera.viewportTop - scene.camera.viewportBottom;
		widthRatio = scene.camera.windowWidth / vpWidth;
		heightRatio = scene.camera.windowHeight / vpHeight;
			
		//Init the buffer
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
			//Fill viewCoords of vertices
			for(PMesh.SurfCell s = shapes[objNum].surfHead;s != null; s = s.next)
				for(PMesh.PolyCell poly = s.polyHead; poly != null; poly = poly.next)
				{
					Double3D v[] = new Double3D[3];
					int j = 0;
					for(PMesh.VertListCell vert = poly.vert; vert != null; vert = vert.next)
					{
						//v[j] = shapes[objNum].vertArray.get(vert.vert).viewPos;
						//Short circuit if possible
						//if(calcViewCoords || (v[j].x == v[j].y && v[j].y == v[j].z && v[j].z == 0))
						v[j] = shapes[objNum].vertArray.get(vert.vert).worldPos.preMultiplyMatrix(shapes[objNum].modelMat);
						v[j] = v[j].preMultiplyMatrix(scene.camera.viewMat);
						shapes[objNum].vertArray.get(vert.vert).viewPos = v[j];
						j++;
					}
				}
			
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
		static final boolean DEBUG = false;
		static final int DEBUG_recursion = 1;
		
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
								
					viewPort[curX][curY] = trace(ray, new HitRecord());	//Start at 0 recursive depth
				}//for y
			}//for x
		}
		
		//All rays we deal with here are in world coordinates.
		DoubleColor trace(Ray ray, HitRecord hit)
		{
			DoubleColor color = new DoubleColor(0.0, 0.0, 0.0, 1.0);
			
			if(hit.depth > Math.max(DEBUG_recursion, scene.maxRecursiveDepth))
				return color;
			
			double tMin = 0.00001;
			double tMax = 10000000;

			//Spheres only for now
			for(int i = 0; i < numObjects; i++)
				//Did I hit the bounding sphere for an object?
				if(spheres[i].hit(ray, tMin, tMax, 0, hit))
					if(DEBUG || !scene.spheresOnly)
					{
						for(PMesh.SurfCell s = shapes[i].surfHead;s != null; s = s.next)
							for(PMesh.PolyCell poly = s.polyHead; poly != null; poly = poly.next)
								//Triangles only for now
								if(poly.numVerts == 3)
								{
									Double3D v[] = new Double3D[3];
									int j = 0;
									for(PMesh.VertListCell vert = poly.vert; vert != null; vert = vert.next)
										v[j++] = shapes[i].vertArray.get(vert.vert).viewPos;
										//Increment j in the line post access
									
									//Check for a hit on this polygon
									if(Triangle.hit(v[0],v[1],v[2],ray, tMin, tMax, 0, hit))
									{ 
										tMax = hit.t;
										hit.matIndex = s.material;
										hit.index = i;
									}
								}
								else
									System.out.println("Need to intersect polygon with " + poly.numVerts + " vertices.");
					}
					else
					{
						tMax = hit.t;
						hit.matIndex = i;	//May cause an error if object 10 and it only has 3 materials.
						hit.index = i;
					}
			//Go check for intersection with the bounding sphere, then check for all triangles
			
			//Find nearest intersection with scene
			//Compute intersection point and normal
			if(hit.index >= 0 )//If it intersects then multi-sample
				color = shade(ray, hit, shapes[hit.index].materials[hit.matIndex]);

			return color;
		}
		
		boolean shadowTrace(Ray ray)
		{
			for(int i = 0; i < numObjects; i++)
				//Spheres only for now
				if(spheres[i].shadowHit(ray, 0.00001, 10000000, 0))
					if(DEBUG || !scene.spheresOnly)
					{
						for(PMesh.SurfCell s = shapes[i].surfHead;s != null; s = s.next)
							for(PMesh.PolyCell poly = s.polyHead; poly != null; poly = poly.next)
								if(poly.numVerts == 3)
								{
									Double3D v[] = new Double3D[3];
									int j = 0;
									for(PMesh.VertListCell vert = poly.vert; vert != null; vert = vert.next)
										v[j++] = shapes[i].vertArray.get(vert.vert).viewPos;
										//Increment j in the line post access
									
									if(Triangle.shadowHit(v[0], v[1], v[2], ray, 0.00001, 10000000, 0))
										return false;
								}
								else
									System.out.println("Need to intersect polygon with " + poly.numVerts + " vertices.");
					}
					else
						return false;	
			return true;
		}
		
		//iPoint is the point of intersection with the surface.
		DoubleColor shade(Ray ray, HitRecord hit, MaterialCell material)
		{
			DoubleColor color = new DoubleColor(0.0, 0.0, 0.0, 0.0);
			
			//Add ambient light only once
			color.plus(new DoubleColor( (double)(lights[0].ambient[0] * material.ka.r), (double)(lights[0].ambient[1] * material.ka.g), 
										(double)(lights[0].ambient[2] * material.ka.b), (double)(lights[0].ambient[3] * material.ka.a) ));
			
			//Assign material color?
			//Local light or directional? If directional then we need to see if it's shining on the object
			for(int i = 0; i < lights.length ; i++){
				if(lights[i].lightSwitch == 1){
					Double3D L = new Double3D((double)lights[i].position[0], (double)lights[i].position[1], (double)lights[i].position[2]);
					L = L.minus(hit.hitP).getUnit();
					Ray shadowRay = new Ray(hit.hitP, L);
					//trace shadow ray to light source
					
					//Turn shadows on and shadowRay hit nothing
					if(!scene.shadows || shadowTrace(shadowRay))
					{	
						double LdN = Math.max(0, hit.normal.dot(L));
						if(LdN > 0)
						{
							//-2(-L.N)N + -L
							Double3D R = hit.normal.sMult( -2 * hit.normal.dot( L.sMult(-1)) ).plus( L.sMult(-1) );
							double RdV = Math.max(0, -R.dot(ray.data[1]) );
							double d = L.distanceTo(hit.hitP);
							
							//double cosinePhi = reflec.dot()
							//If the light is free add the diffuse light
							//Intensity (Kd * (LdN) + Ks *(RdV)^(shiny)/(r + k)
							color.plus(new DoubleColor( (double)(lights[i].diffuse[0] * LdN + lights[i].specular[0] * Math.pow(RdV, material.shiny)) / d,
														(double)(lights[i].diffuse[1] * LdN + lights[i].specular[1] * Math.pow(RdV, material.shiny)) / d,
														(double)(lights[i].diffuse[2] * LdN + lights[i].specular[2] * Math.pow(RdV, material.shiny)) / d,
														1.0) );
						}
					}//*/
				}
			}
			//Shiny Phong
			//If IdN > 0 then we find a reflection
			if(scene.reflections && (hit.normal.dot(ray.data[1]) < 0) &&
					(material.reflectivity.r > 0 || material.reflectivity.g > 0 || material.reflectivity.b > 0))
			{
				hit.depth++;
				
				//Double3D I = ray.data[1];
				//R = I - 2 * (I.N)N
				Double3D R = hit.normal.sMult( -2 * hit.normal.dot(ray.data[1]) ).plus(ray.data[1]).getUnit();
				Ray reflect = new Ray(hit.hitP, R);
				DoubleColor reflection = trace(reflect, hit);
				
				//Scale by distance?
				//reflection.scale( 1 / reflect.origin().distanceTo(hit.hitP));
				
				reflection.r = reflection.r * material.reflectivity.r;
				reflection.g = reflection.g * material.reflectivity.g;
				reflection.b = reflection.b * material.reflectivity.b;
				
				color.plus( reflection ); //trace(ray from iPoint in direction of reflected/refracted, rDepth + 1)
				hit.depth--;
			}
			
			if(scene.refractions &&// (hit.normal.dot(ray.data[1]) > 0) &&
					(material.refractivity.r > 0 || material.refractivity.g > 0 || material.refractivity.b > 0))
			{
				hit.depth++;

				double n = 1.0; //From air
				double nt = material.refractiveIndex; //1.5; //Into glass
				Double3D D = ray.data[1].sMult(1);
				Double3D N = hit.normal;
				
				double DdN = N.dot(D); //If this is < 0 then nothing
				double cosineThetaSq = 1 -(n*n * (1 - DdN*DdN)) / nt;
				Double3D temp = D.plus(N.sMult(-1 * DdN)).sMult(n / nt);
				Double3D refractDir = N.sMult(Math.sqrt(cosineThetaSq)).plus(temp);
				
				Ray refract = new Ray(hit.hitP, refractDir);
				DoubleColor refraction = trace(refract, hit);
				
				refraction.r = refraction.r * material.refractivity.r;
				refraction.g = refraction.g * material.refractivity.g;
				refraction.b = refraction.b * material.refractivity.b;
				
				//reflection.scale(material.refractivity.r);
				color.plus( refraction ); //trace(ray from iPoint in direction of reflected/refracted, rDepth + 1)
				
				hit.depth--;
			}
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
