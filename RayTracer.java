/*
 * This class starts and controls the ray tracer 
 * 
 * @author Joshua Olson
 * @version	1-Apr-2011
 */
import static javax.media.opengl.GL2.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

public class RayTracer implements Runnable
{
	final int THREADS = 1;
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
			
			//Rethink space partitioning for threads! 
			//r[i] = new Renderer(left, right,
			r[i] = new Renderer((int)scene.camera.viewportLeft, (int)scene.camera.viewportRight,
								(int)scene.camera.viewportBottom, (int)scene.camera.viewportTop, i) ;
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
		int startLine;
		int curX = 0, curY = 0;
		int checkerFreq = 200;
		int samples;
	    Double3D imageSamples[];
	    boolean sampled = true;
	    
	    DoubleColor black = new DoubleColor(0, 0, 0 ,1);
		DoubleColor white = new DoubleColor(1, 1, 1 ,1);
	    
	    static final boolean DEBUG = false;
		static final int DEBUG_recursion = 10;
		static final int DEBUG_samples = 5;
		
		Renderer()
		{
			xMin = (int)scene.camera.viewportLeft;
			xMax = (int)scene.camera.viewportRight; 
			yMin = (int)scene.camera.viewportBottom; 
			yMax = (int)scene.camera.viewportTop;
			
			if(scene.antiAliasing)
			{
				samples = Math.max(scene.raysPerPixel, DEBUG_samples);
				imageSamples = new Double3D[samples];
				for (int i = 0; i < samples; i++)
					imageSamples[i] = new Double3D(0,0,0);
				
				Sample.multiJitter(imageSamples, samples);
			    
			    //Samples are in the range [-2,2]
				Sample.cubicSplineFilter(imageSamples, samples);
				
				//Scale image samples to [-1,1]
			    for (int i = 0; i < samples; i++)
			    {
			        imageSamples[i].x = (imageSamples[i].x + 1.0) / 2.0;
			        imageSamples[i].y = (imageSamples[i].y + 1.0) / 2.0;
			    }
			}
		}
		
		Renderer(int _xMin, int _xMax, int _yMin, int _yMax, int line)
		{
			xMin = _xMin;
			xMax = _xMax; 
			yMin = _yMin; 
			yMax = _yMax;
			startLine = line;
			
			if(DEBUG || scene.antiAliasing)
			{
				sampled = false;
				
				samples = Math.max(scene.raysPerPixel, DEBUG_samples);
				imageSamples = new Double3D[samples];
				for (int i = 0; i < samples; i++)
					imageSamples[i] = new Double3D(0,0,0);
				
				Sample.nrooks(imageSamples, samples);
			    
			    //Samples are in the range [-2,2]
				//Sample.cubicSplineFilter(imageSamples, samples);
								
				//Scale image samples to [-1,1] and adjust to worldCoords
			    for (int i = 0; i < samples; i++)
			    {
			        imageSamples[i].x = widthRatio  * ((imageSamples[i].x + 1.0) / 2.0);
			        imageSamples[i].y = heightRatio * ((imageSamples[i].y + 1.0) / 2.0);
			    }
			}
		}
		
		@Override
		public void run()
		{
			//Always tracing from the origin here.
			Double3D origin = new Double3D(0.0, 0.0, 0.0);
			
			//Work though viewport (pixel) coordinates
			//Start in the middle of pixel 0
			double worldY = scene.camera.windowBottom + (yMin + startLine + 0.5) * heightRatio;
			for(curY = yMin + startLine; curY < yMax; curY = curY + THREADS){
				worldY += THREADS * heightRatio;
				//Start in the middle of pixel 0
				double worldX = scene.camera.windowLeft + (xMin + 0.5) * widthRatio;
				
				for(curX = xMin; curX < xMax; curX++)
				{
					worldX += widthRatio;
					Double3D dir = new Double3D(worldX, -worldY, -scene.camera.near);
					dir.unitize();
					Ray ray = new Ray(origin, dir);
								
					viewPort[curX][curY] = trace(ray, new HitRecord());	//Start at 0 recursive depth
				}//for y
			}//for x
		}
		
		//Display checker board background
		DoubleColor checkerBackgroundHit(Ray r, HitRecord hit)
		{	
			//Find the t value where z = scene.camera.far
			double t = -scene.camera.far / r.data[1].z; 
			
			hit.hitP = r.pointAtParameter(t);
			hit.hitP.x = (hit.hitP.x > 0)? hit.hitP.x : -hit.hitP.x + checkerFreq / 2;
			hit.hitP.y = (hit.hitP.y > 0)? hit.hitP.y : -hit.hitP.y + checkerFreq / 2;
			
			if( hit.hitP.x % checkerFreq < checkerFreq / 2 )//&& p.x < scene.camera.windowRight)
				if( hit.hitP.y % checkerFreq < checkerFreq / 2 )// && p.y < scene.camera.windowTop)
					return white;
				else
					return black;
			else
				if( hit.hitP.y % checkerFreq < checkerFreq / 2 )// && p.y < scene.camera.windowTop)
					return black;
				else
					return white;
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
					if(scene.spheresOnly)
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
			
			if(hit.index >= 0 )//If it intersects then multi-sample
			{
				if(!sampled && hit.depth == 0)
				{
					//Only sample once
					sampled = true;
					
					Double3D dir = ray.data[1];
					DoubleColor antiAlias = new DoubleColor(0,0,0,1);
					
					for(int i = 0; i < samples; i++)
					{
						ray.data[1].x = dir.x + imageSamples[i].x;
						ray.data[1].y = dir.y + imageSamples[i].y;

						antiAlias.plus( trace(ray, new HitRecord()));
					}
					antiAlias.scale(1 / samples);
					
					color.plus(antiAlias);
				}
				else
					color = shade(ray, hit, shapes[hit.index].materials[hit.matIndex]);
			}
			else//We hit nothing check for intersection with the far clip plane for checker board pattern.
				if(!scene.checkerBackground)
					color = checkerBackgroundHit(ray, hit);
				
			return color;
		}
		
		boolean shadowTrace(Ray ray)
		{
			for(int i = 0; i < numObjects; i++)
				//Spheres only for now
				if(spheres[i].shadowHit(ray, 0.00001, 10000000, 0))
					if(scene.spheresOnly)
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
			DoubleColor color = new DoubleColor(0.0, 0.0, 0.0, 1.0);
			
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
					if(DEBUG || !scene.shadows || shadowTrace(shadowRay))
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
							/*color.plus(new DoubleColor( (double)(lights[i].diffuse[0] * LdN + lights[i].specular[0] * Math.pow(RdV, material.shiny)),// / d,
														(double)(lights[i].diffuse[1] * LdN + lights[i].specular[1] * Math.pow(RdV, material.shiny)),// / d,
														(double)(lights[i].diffuse[2] * LdN + lights[i].specular[2] * Math.pow(RdV, material.shiny)),// / d,
														1.0) );*/
							
							color.plus(new DoubleColor( (double)(lights[i].diffuse[0] * LdN) / d,
									(double)(lights[i].diffuse[1] * LdN ) / d,
									(double)(lights[i].diffuse[2] * LdN ) / d,
									1.0) );
							
							color.plus(new DoubleColor( (double)(lights[i].specular[0] * Math.pow(RdV, 50/*material.shiny*/)),
									(double)(lights[i].specular[1] * Math.pow(RdV, material.shiny)),
									(double)(lights[i].specular[2] * Math.pow(RdV, material.shiny)),
									1.0) );
						}
					}//*/
				}
			}
			//Shiny Phong
			//If IdN > 0 then we find a reflection
			if(DEBUG || scene.reflections && (hit.normal.dot(ray.data[1]) < 0))
					//&& (material.reflectivity.r > 0 || material.reflectivity.g > 0 || material.reflectivity.b > 0))
			{
				hit.depth++;
				
				Double3D I = ray.data[1];
				Double3D N = hit.normal;
				//R = I - 2 * (I.N)N
				Double3D R = new Double3D();
				R = I.minus( N.sMult( 2* N.dot(I)) );
				//if(specularDirection(1.0, 1.5, hit.normal, ray.data[1], R))
				{
					
					Ray reflect = new Ray(hit.hitP, R.getUnit());
					DoubleColor reflection = trace(reflect, hit);
					
					//Scale by distance?
					//reflection.scale( 1 / reflect.origin().distanceTo(hit.hitP));
					
					reflection.r = reflection.r * .1;//material.reflectivity.r;
					reflection.g = reflection.g * .1;//material.reflectivity.g;
					reflection.b = reflection.b * .1;//material.reflectivity.b;
					
					color.plus( reflection ); //trace(ray from iPoint in direction of reflected/refracted, rDepth + 1)
				}
				hit.depth--;
			}
			
			if(DEBUG || !scene.refractions) //&& (hit.normal.dot(ray.data[1]) > 0) &&
					//&& (material.refractivity.r > 0 || material.refractivity.g > 0 || material.refractivity.b > 0))
			{
				hit.depth++;

				Double3D refractDir = new Double3D(); 
				double n = 1;
				double nt = 1.5;
				
				if(transmissionDirection(n, nt, ray, hit, refractDir))
				{
					Ray refract = new Ray(hit.hitP, ray.data[1].getUnit());
					DoubleColor refraction = trace(refract, hit);
					
					refraction.r = refraction.r * .8;//material.refractivity.r;
					refraction.g = refraction.g * .8;//material.refractivity.g;
					refraction.b = refraction.b * .8;//material.refractivity.b;
					
					//reflection.scale(material.refractivity.r);
					color.plus( refraction ); //trace(ray from iPoint in direction of reflected/refracted, rDepth + 1)
				}
				
				hit.depth--;
			}
			return color;
		}
		
		boolean specularDirection(double n, double nt, Double3D N, Double3D vIn, Double3D reflection)
		{
			double scale;
			double R0 = 0;
			DoubleColor ratio = new DoubleColor();
			double cosine = vIn.dot(N);
			
			if(cosine < 0.0)//Ray is incoming
			{
				reflection = vIn.minus(N.sMult(2 * vIn.dot(N)));
				
				//Since we are assuming dielectrics are embedded in air no need to
				//check for the total internal reflection here
				double temp1 = 1.0 - cosine;
				scale = R0 + (1.0 - R0) * temp1*temp1*temp1*temp1*temp1;
			}
			else//Ray is outgoing
			{
				reflection = vIn.minus(N.sMult(2 * vIn.dot(N)));
				double temp2 = -vIn.dot(N.sMult(-1.0));
				double root = 1.0 - nt*nt * (1.0 - temp2*temp2);
				
				if(root < 0.0)//Total internal refraction
					scale = 1.0;
				else
				{
					double temp3 = 1.0 - cosine;
					scale = R0 + (1.0 - R0) * temp3*temp3*temp3*temp3*temp3;
				}
				
				double temp1 = 1.0 - cosine;
				scale = R0 + (1.0 - R0) * temp1*temp1*temp1*temp1*temp1;
			}
			return true;
		}
		
		boolean transmissionDirection(double n, double nt, Ray ray, HitRecord hit, Double3D transmission) 
		{
			if(hit.inMat)
			{
				double temp = n;
				n = nt;
				nt = temp;
			}
			
			hit.inMat = !hit.inMat;
			
			Double3D normal = hit.normal.sMult(-1);
			Double3D d = ray.direction();
			
			double cosine = d.dot(normal);
		    double nRatio = n / nt;
		    
		    if (cosine < 0.0f) //Ray is incoming 
		    {
		        cosine = -cosine;
		        double cosinePrimeSq = 1.0 - nRatio * nRatio * (1.0f - cosine * cosine);
		        
		        //Since assuming dielectrics are embedded in air no need to 
		        //check for total internal refraction here
		        if (cosinePrimeSq < 0.0f)
		            return false;   //total internal refraction
		        else
		        {
		        	Double3D pOne = d.minus(normal.sMult(normal.dot(d))).sMult(nRatio);
		        	transmission = pOne.plus( normal.sMult( nRatio * cosine - Math.sqrt(cosinePrimeSq)) );
		        }
		        
		    }
		    else{   //Ray is outgoing
		    	double temp2 = -d.dot(normal.sMult(-1.0));
		    	//cosine = -vIn.dot(normal.sMult(-1.0));
		        //cosine = vIn.dot(normal);
		    	nRatio =  1 / nRatio;
		        double cosinePrimeSq = 1.0 - nRatio * nRatio * (1.0f - temp2*temp2);
		        
		        if (cosinePrimeSq < 0.0f)
		            return false;   //total internal refraction
		        else
		        {
		        	Double3D pOne = d.minus( normal.sMult(normal.dot(d)) ).sMult(nRatio);
			        transmission = pOne.minus( normal.sMult( nRatio * cosine - Math.sqrt(cosinePrimeSq)) );    
		        }
		    }
		    return true;
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
