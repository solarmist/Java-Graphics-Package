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
	final int THREADS = 16;
	DoubleColor viewPort[][];
	Scene scene;
	PMesh[] shapes;
	Sphere[] spheres;
	Light[] lights;
	double xMin, xMax;
	double yMin, yMax;
	double vpWidth, vpHeight;
	double heightRatio, widthRatio;
	
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
		
		xMin = scene.camera.viewportLeft + 0.5;
		xMax = scene.camera.viewportRight;
		yMin = scene.camera.viewportBottom + 0.5;
		yMax = scene.camera.viewportTop;
		
		//Transform into camera coords
		vpWidth = xMax - xMin;
		vpHeight = yMax - yMin;
		widthRatio = (scene.camera.windowRight - scene.camera.windowLeft) / vpWidth;
		heightRatio = (scene.camera.windowTop - scene.camera.windowBottom) / vpHeight;
			
		//Init the buffer
		viewPort = new DoubleColor[(int)vpWidth+1][(int)vpHeight+1];
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
		
		for(int i = 0; i < THREADS; i++)
		{	
			//Rethink space partitioning for threads! 
			r[i] = new Renderer(i) ;
			t[i] = new Thread(r[i]);
			t[i].start();
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
		int startLine;
		int curX = 0, curY = 0;
		int checkerFreq = 200;
		int samples;
	    Double3D imageSamples[];
	    boolean sampled = true;
	    int depth = 0;
	    
	    MaterialCell black = new MaterialCell();
	    MaterialCell white = new MaterialCell();
	    
	    static final boolean DEBUG = false;
		static final int DEBUG_recursion = 5;
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
		
		Renderer(int line)
		{
			startLine = line;
			
			if(scene.antiAliasing)
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
			
			black.ka = new DoubleColor(0, 0, 0, 1);
			black.kd = new DoubleColor(0, 0, 0, 1);
			black.ks = new DoubleColor(0, 0, 0, 1);
			black.shiny = 0;
			black.refractiveIndex = 1;
			black.reflectivity = new DoubleColor(0, 0, 0, 1);
			black.refractivity = new DoubleColor(0, 0, 0, 1);
			
			white.ka = new DoubleColor(1, 1, 1, 1);
			white.kd = new DoubleColor(0, 0, 0, 1);
			white.ks = new DoubleColor(0, 0, 0, 1);
			white.shiny = 0;
			white.refractiveIndex = 1;
			white.reflectivity = new DoubleColor(0, 0, 0, 1);
			white.refractivity = new DoubleColor(0, 0, 0, 1);
			
			//Work though viewport (pixel) coordinates
			//Start in the middle of pixel 0
			double worldY = scene.camera.windowBottom + (yMin + startLine) * heightRatio;
			for(curY = (int)yMin + startLine; curY < yMax; curY = curY + THREADS){
				worldY += THREADS * heightRatio;
				//Start in the middle of pixel 0
				double worldX = scene.camera.windowLeft + (xMin) * widthRatio;
				
				for(curX = (int)xMin; curX < xMax; curX++)
				{
					worldX += widthRatio;
					Double3D dir = new Double3D(worldX, worldY, -scene.camera.near);
					dir.unitize();
					Ray ray = new Ray(origin, dir);
		
					viewPort[curX][curY] = trace(ray);	//Start at 0 recursive depth
				}//for y
			}//for x
			
			System.out.println("Thread " + startLine + " finished.");
		}
		
		//Display checker board background
		MaterialCell checkerBackgroundHit(Ray r, HitRecord hit)
		{	
			//Find the t value where z = scene.camera.far
			hit.t = -scene.camera.far / r.dir.z; 
			
			hit.hitP = r.pointAtParameter(hit.t);
			hit.normal = new Double3D(0,0,1);
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
		//Should take the refractive index of the material it is currently in.
		DoubleColor trace(Ray ray)
		{
			DoubleColor color = new DoubleColor(0.0, 0.0, 0.0, 1.0);
			HitRecord hit = new HitRecord();
			
			if(depth > Math.max(DEBUG_recursion, scene.maxRecursiveDepth))
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
										hit.normal = poly.normal;
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
				if(!sampled && depth == 0)
				{
					//Only sample once
					sampled = true;
					
					Double3D dir = ray.dir;
					DoubleColor antiAlias = new DoubleColor(0,0,0,1);
					
					for(int i = 0; i < samples; i++)
					{
						ray.dir.x = dir.x + imageSamples[i].x;
						ray.dir.y = dir.y + imageSamples[i].y;

						antiAlias.plus( trace(ray));
					}
					antiAlias.scale(1 / samples);
					
					color.plus(antiAlias);
				}
				else
					color = shade(ray, hit, shapes[hit.index].materials[hit.matIndex], false);
			}
			else//We hit nothing check for intersection with the far clip plane for checker board pattern.
				if(!scene.checkerBackground)
					color = shade(ray, hit, checkerBackgroundHit(ray, hit), true);
				
			return color;
		}
		
		boolean shadowTrace(Ray ray)
		{
			if(depth > Math.max(DEBUG_recursion, scene.maxRecursiveDepth))
				return false;
			
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
		DoubleColor shade(Ray ray, HitRecord hit, MaterialCell material, boolean background)
		{
			DoubleColor color = new DoubleColor(0.0, 0.0, 0.0, 1.0);
			
			//Add ambient light only once
			color.plus(new DoubleColor( (double)(lights[0].ambient[0] * material.ka.r), (double)(lights[0].ambient[1] * material.ka.g), 
										(double)(lights[0].ambient[2] * material.ka.b), (double)(lights[0].ambient[3] * material.ka.a) ));
			
			//Assign material color?
			//Local light or directional? If directional then we need to see if it's shining on the object
			if(!background)
			{
				for(int i = 0; i < lights.length ; i++){
					if(lights[i].lightSwitch == 1){
						Double3D L = new Double3D(	(double)lights[i].position[0], 
													(double)lights[i].position[1], 
													(double)lights[i].position[2]);
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
								double RdV = Math.max(0, -R.dot(ray.dir) );
								double d = L.distanceTo(hit.hitP);
								
								//double cosinePhi = reflec.dot()
								//If the light is free add the diffuse light
								//Intensity (Kd * (LdN) + Ks *(RdV)^(shiny)/(r + k)
								color.plus(new DoubleColor( (double)(lights[i].diffuse[0] * LdN + lights[i].specular[0] * Math.pow(RdV, material.shiny)),// / d,
															(double)(lights[i].diffuse[1] * LdN + lights[i].specular[1] * Math.pow(RdV, material.shiny)),// / d,
															(double)(lights[i].diffuse[2] * LdN + lights[i].specular[2] * Math.pow(RdV, material.shiny)),// / d,
															1.0) );//*/
								
								/*color.plus(new DoubleColor( (double)(lights[i].diffuse[0] * LdN) / d,
										(double)(lights[i].diffuse[1] * LdN ) / d,
										(double)(lights[i].diffuse[2] * LdN ) / d,
										1.0) );
								
								color.plus(new DoubleColor( (double)(lights[i].specular[0] * Math.pow(RdV, material.shiny)),
										(double)(lights[i].specular[1] * Math.pow(RdV, material.shiny)),
										(double)(lights[i].specular[2] * Math.pow(RdV, material.shiny)),
										1.0) );//*/
							}
						}
					}
				}
			
			//Shiny Phong
			//If IdN > 0 then we find a reflection
			//If IdN < 0 then we need -normal
			if(DEBUG || !scene.reflections && (hit.normal.dot(ray.dir) < 0)
					&& (material.reflectivity.r > 0 || material.reflectivity.g > 0 || material.reflectivity.b > 0))
			{
				hit.depth++;
				
				//R = I - 2 * (I.N)N
				Double3D R = new Double3D();
				R = ray.dir.plus( hit.normal.sMult( -2.0 * hit.normal.dot(ray.dir)) );
					
				Ray reflect = new Ray(hit.hitP, R.getUnit(), ray.n);
				DoubleColor reflection = trace(reflect);
				
				//Scale by distance?
				//reflection.scale( 1 / reflect.origin().distanceTo(hit.hitP));
				
				reflection.r = reflection.r * material.reflectivity.r;
				reflection.g = reflection.g * material.reflectivity.g;
				reflection.b = reflection.b * material.reflectivity.b;
				
				color.plus( reflection ); //trace(ray from iPoint in direction of reflected/refracted, rDepth + 1)
				
				hit.depth--;
			}
			
			if(DEBUG || scene.refractions //&& (hit.normal.dot(ray.data[1]) > 0) &&
					&& (material.refractivity.r > 0 || material.refractivity.g > 0 || material.refractivity.b > 0))
			{
				hit.depth++;

				Double3D refractDir = new Double3D(); 
				if(!ray.inside)
					ray.nt = material.refractiveIndex;
				
				if(transmissionDirection(ray, hit, refractDir))
				{
					Ray refract = new Ray(hit.hitP, refractDir.getUnit(), ray.n, ray.nt, ray.inside);
					DoubleColor refraction = trace(refract);
					
					refraction.r = refraction.r * material.refractivity.r;
					refraction.g = refraction.g * material.refractivity.g;
					refraction.b = refraction.b * material.refractivity.b;
					
					//Scale for distance?
					color.plus( refraction ); //trace(ray from iPoint in direction of reflected/refracted, rDepth + 1)
				}
				
				hit.depth--;
			}
			return color;
		}
		
		boolean transmissionDirection(Ray ray, HitRecord hit, Double3D transmission) 
		{
			
			/*// calculate refraction
			float refr = prim->GetMaterial()->GetRefraction();
			float nt = prim->GetMaterial()->GetRefrIndex();
			float nRatio = n / nt;
			
			
			vector3 N = prim->GetNormal( pi ) * (float)result;
			float cosI = -DOT( N, a_Ray.GetDirection() );
			float cosT2 = 1.0f - nRatio * nRatio * (1.0f - cosI * cosI);
			if (cosT2 > 0.0f)
				vector3 T = (nRatio * a_Ray.GetDirection()) + (nRatio * cosI - sqrtf( cosT2 )) * N;
				= nRatio *(D + cosI - sqrt(cosT2))*N
			*/
			double n = ray.n;
			double nt = ray.nt;
			
			Double3D N = hit.normal.sMult(-1);
			Double3D D = ray.dir;
			
			double cosine = -D.dot(N);
			if(n < nt)//We're inside, so reverse the normal
				cosine =  -D.dot(N.sMult(-1));
			
		    double nRatio = n / nt;

		    double cosinePSq = 1.0 - nRatio * nRatio * (1.0f - cosine * cosine);
		        
		    //check for total internal refraction here
		    if (cosinePSq < 0.0f)
		    	return false;   //total internal refraction
		    else
		    {
		    	//D - N(N.D)
		    	//Double3D pOne = D.minus( N.sMult(N.dot(D)) ).sMult(nRatio);
		    	double inside = nRatio * cosine - Math.sqrt(cosinePSq);
		    	Double3D temp = D.sMult(nRatio).plus(N.sMult(inside)).getUnit();
		       	transmission.dir.x = temp.x;
		       	transmission.dir.y = temp.y;
		       	transmission.dir.z = temp.z;
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
		
		gl.glClearColor(0.8f, 0.2f, 0.2f, 1.0f);
		
		gl.glClear(GL_DEPTH_BUFFER_BIT|GL_COLOR_BUFFER_BIT); 
		
		gl.glPointSize(1.0f);
		
		//Set the viewport
		glu.gluOrtho2D(xMin, xMax, yMin, yMax);
		
		gl.glBegin(GL_POINTS);
				
		for(int x =(int) (xMin); x < xMax; x++)
			for(int y =(int)(yMin); y < yMax; y++)
			{
				gl.glColor3d(viewPort[x][y].r, viewPort[x][y].g, viewPort[x][y].b);
				gl.glVertex2d(x + 0.5, y + 0.5);
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
