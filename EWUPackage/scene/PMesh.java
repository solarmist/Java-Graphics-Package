package EWUPackage.scene;

import java.io.*;
import java.nio.*;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.*;
import javax.media.opengl.*;
import java.util.ArrayList;
import static javax.media.opengl.GL2.*;

import EWUPackage.raytracer.*;
import EWUPackage.scene.primitives.*;
import EWUPackage.scene.camera.*;

/**
 * This class provides a polygonal mesh for storing and rendering polygonal
 * objects. This class is abstract because no input data format is specified.
 * An extending class must overload the load method to read from a file into
 * the provided data structures.
 *
 * @version 24-Jan-2005
 */
public abstract class PMesh implements Serializable
{

///////////////////////////////////////////////////////////////////////////////
// DATA MEMBERS

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String fileType;
	public String filePath;
	public String objName;
	public Scene theScene;
	public int objNumber;
	public int numVerts;
	public int numNorms;
	public int numTex;
	public int numSurf;
	public int numPolys;
	public int numMats;
	public ArrayList<VertCell> vertArray;
	public ArrayList<SurfCell> vertUsedArray;
	//public ArrayList<Double3D> vertNormArray;
	public ArrayList<Double3D> texVertArray;
	public ArrayList<Double3D> viewNormArray;
	public Double3D vertNormArray[];
	public SurfCell surfHead;
	public MaterialCell materials[];
	public double modelMat[];
	public Double3D center;
	public Double3D viewCenter;
	public Transform pendingTransform;
	public boolean transformPending;
	public Sphere boundingSphere;
	public boolean active;
	public PMesh next;


///////////////////////////////////////////////////////////////////////////////
// INNER CLASSES

	/**
	 * Vertex
	 */
	public class VertCell implements Serializable
	{
	private static final long serialVersionUID = 1L;
		public Double3D worldPos;
		public Double3D viewPos;
		public Double3D screenPos;
		public PolyListCell polys;

		public VertCell()
		{
			worldPos = new Double3D();
			viewPos = new Double3D();
			screenPos = new Double3D();
			polys = null;
		} // end constructor
		public VertCell(VertCell from) //copy constructor
		{
			worldPos = new Double3D(from.worldPos);
			viewPos = new Double3D(from.viewPos);
			screenPos = new Double3D(from.screenPos);
			polys = null;
		}
	} // end class VertCell

	/**
	 * Vertex List
	 */
	public class VertListCell implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public int vert;			// index of vertex in the array
		public int norm;			// index of normal for this vertex
		public int tex;			// index of texture vertex for this vertex
		public VertListCell next;	// pointer to the rest of the vertices in the polygon

		public VertListCell()
		{
			vert = -1;
			norm = -1;
			tex = -1;
			next = null;
		} // end constructor
	} // end class VertListCell

	/**
	 * Polygon
	 */
	public class PolyCell implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public int numVerts;			// total number of vertices in the polygon
		public VertListCell vert;		// pointer to the first vertex
		public Double3D normal;		// polygon normal
		public Double3D viewNorm;		// viewpoint transformed normal
		boolean culled;			// culling flag
		public SurfCell parentSurf;	// pointer to the original surface cell for this polygon
		public PolyCell next;			// pointer to the next polygon in the surface

		public PolyCell()
		{
			numVerts = 0;
			vert = null;
			normal = new Double3D();
			culled = false;
			parentSurf = null;
			next = null;
		} // end constructor
	} // end class PolyCell

	/**
	 * Polygon List
	 */
	public class PolyListCell implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public PolyCell poly;		// pointer to polygon
		public PolyListCell next;	// pointer to the next polygon in the list

		public PolyListCell()
		{
			poly = null;
			next = null;
		} // end constructor
	} // end class PolyListCell

	/**
	 * Surface
	 */
	public class SurfCell implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public int numPoly;			/* Total number of polygons in this surface */
		public PolyCell polyHead;		/* pointer to first polygon */
		public int material;			/* index of the material to apply to this surface */
		public SurfCell next;			/* pointer to the next surface */
		//public boolean smooth;
		public String name;
		public int numVerts;
		public FloatBuffer vertexBuffer;
		public FloatBuffer normalBuffer;
		public int [] buffers = new int [2];

		public SurfCell(String name)
		{
			this.name = name;
			//this.smooth  = smoothed;
			numPoly = 0;
			polyHead = null;
			material = 0;
			next = null;
			numVerts=0;
		} // end constructor
		//vert normals are only calculated for smooth surfaces - surface by surface
	} // end class SurfCell

///////////////////////////////////////////////////////////////////////////////
// PMESH METHODS

	public PMesh(Scene aScene)
	{
		theScene = aScene;
		objNumber = -1;
		numVerts = 0;
		numNorms = 0;
		numTex = 0;
		numSurf = 0;
		numPolys = 0;
		numMats = 1; // Default to 1 because there will always at least be the default material
		surfHead = null;
		vertArray = null;
		texVertArray = null;
		vertNormArray = null;
		viewNormArray = null; //only need for Ray Tracing - instantiated in RayTracer:doViewTrans()
		materials = null;
		active = true;
		next = null;
		pendingTransform = null;
		transformPending = false;
		center = new Double3D();
		viewCenter = new Double3D();
	} // end constructor

	/**
	 * This method loads the PMesh data structure from the specified file.
	 * This method must be overridden by derived classes.
	 *
	 * @param filepath String
	 * @return boolean
	 * @throws FileNotFoundException
	 */
	public abstract boolean load(String filepath) throws FileNotFoundException;

	/**
	 * This method saves the PMesh data structure to the specified file.
	 * This method should be overridden by any derived classes that wish
	 * to save their data to a file. The saved file should be able to be
	 * loaded by the load() method.
	 *
	 * @param String - the name of the file to save the mesh to
	 * @param String - the name of the file (without the path but with the extension)
	 * 					containing the materials used by the mesh
	 */
	public boolean save(String filepath)
	{
		return false;
	}

	/**
	 * This method draws the polygonal mesh contained in this object.
	 *
	 * @param camera Camera The current camera in the scene
	 * @param gl GL
	 * @param glu GLU
	 */
	public void draw(Camera camera, GL2 gl, GLU glu)
	{
		SurfCell curSurf;
		//PolyCell curPoly;
		//VertListCell curVert;

		//Double3D vertNormal; // vertex normal vector (used for Gouraud shading)
		//Double3D polyNormal; // polygon normal vector (used for flat shading)

		// Check for pending object transformations
		if(transformPending && pendingTransform != null){
			switch(pendingTransform.type){
			case Transform.TRANSLATE:
				gl.glMatrixMode(GL_MODELVIEW);
				gl.glLoadIdentity();
				gl.glTranslated(pendingTransform.x, pendingTransform.y, pendingTransform.z);
				gl.glMultMatrixd(modelMat,0);
				gl.glGetDoublev(GL_MODELVIEW_MATRIX, modelMat,0);
				transformPending = false;
				pendingTransform = null;
				break;
			case Transform.SCALE:

				gl.glMatrixMode(GL_MODELVIEW);
				gl.glLoadIdentity();
				gl.glTranslated(center.x, center.y, center.z);
				gl.glScaled(pendingTransform.x, pendingTransform.y, pendingTransform.z);
				gl.glTranslated(-center.x, -center.y, -center.z);
				gl.glMultMatrixd(modelMat,0);
				gl.glGetDoublev(GL_MODELVIEW_MATRIX, modelMat,0);
				transformPending = false;
				pendingTransform = null;
				break;
			case Transform.ROTATE:
				gl.glMatrixMode(GL_MODELVIEW);
				gl.glLoadIdentity();
				gl.glTranslated(center.x, center.y, center.z);
				gl.glRotated(pendingTransform.angle, pendingTransform.x, pendingTransform.y, pendingTransform.z);
				gl.glTranslated(-center.x, -center.y, -center.z);
				gl.glMultMatrixd(modelMat,0);
				gl.glGetDoublev(GL_MODELVIEW_MATRIX, modelMat,0);
				transformPending = false;
				pendingTransform = null;
				break;
			} // end switch
		} // end if

		if(active)
		{
			//boolean flat;
			gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glMultMatrixd(camera.viewMat,0);
			gl.glMultMatrixd(modelMat,0);

			curSurf = surfHead;
			while(curSurf != null)
			{
				gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT,
						materials[curSurf.material].ka.toFloatv(),0 );
				gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_DIFFUSE,
						materials[curSurf.material].kd.toFloatv(),0);
				gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_SPECULAR,
						materials[curSurf.material].ks.toFloatv(),0 );
				gl.glMaterialf(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_SHININESS,
						(float)materials[curSurf.material].shiny );

				gl.glEnableClientState (GL_VERTEX_ARRAY);
				gl.glEnableClientState (GL_NORMAL_ARRAY);

				gl.glVertexPointer (3, GL_FLOAT, 0, curSurf.vertexBuffer);
				gl.glNormalPointer (GL_FLOAT, 0, curSurf.normalBuffer);

				gl.glDrawArrays(GL_TRIANGLES, 0 , 3*curSurf.numPoly);

				gl.glDisableClientState (GL_VERTEX_ARRAY);
				gl.glDisableClientState (GL_NORMAL_ARRAY);

				curSurf = curSurf.next;
			} // end while(curSurf != null)

		}//end "if active" section

	} // end method draw

	public void translate(double x, double y, double z)
	{
		pendingTransform = new Transform();
		pendingTransform.translate(x, y, z);
		transformPending = true;

		center.x += x;
		center.y += y;
		center.z += z;
	} // end method translate

	public void rotate(double angle, double x, double y, double z)
	{
		pendingTransform = new Transform();
		pendingTransform.rotate(angle, x, y, z);
		transformPending = true;
	} // end method rotate

	public void showMat(double [] mat){
		System.out.println(mat[0]+"  "+mat[1]+"  "+mat[2]+"  "+mat[3]);
		System.out.println(mat[4]+"  "+mat[5]+"  "+mat[6]+"  "+mat[7]);
		System.out.println(mat[8]+"  "+mat[9]+"  "+mat[10]+"  "+mat[11]);
		System.out.println(mat[12]+"  "+mat[13]+"  "+mat[14]+"  "+mat[15]);
	} // end method showMat

	public void scale(double x, double y, double z)
	{
		pendingTransform = new Transform();
		pendingTransform.scale(x, y, z);
		transformPending = true;
	} // end method scale

	/**
	 * This method calculates polygon normals.
	 */
	public void calcPolyNorms()
	{
		Double3D vector1, vector2, cross;
		SurfCell curSurf = surfHead;
		PolyCell curPoly;
		VertListCell curVert, temp;

		while(curSurf != null){
			curPoly = curSurf.polyHead;
			while(curPoly != null){
				temp = curVert = curPoly.vert;
				while (temp != null)
				{
					temp.norm = temp.vert;
					temp = temp.next;
				} // end while
				if(curVert !=null){
					if(curVert.next != null){
						if(curVert.next.next != null){
							vector1 = new Double3D(vertArray.get(curVert.next.vert).worldPos);
							vector1 = new Double3D(vector1.minus(vertArray.get(curVert.vert).worldPos));
							vector2 = new Double3D(vertArray.get(curVert.next.next.vert).worldPos);
							vector2 = new Double3D(vector2.minus(vertArray.get(curVert.vert).worldPos));
							cross = new Double3D(vector1.cross(vector2));
							curPoly.normal = new Double3D(cross.getUnit());
						} // end if
					} // end if
				} // end if
				else curPoly.normal = new Double3D();
				curPoly = curPoly.next;
			} // end while curPoly != null
			curSurf = curSurf.next;
		}// end while curSurf != null
		System.out.println("Polygon Normals calculated");
	} // end method calcPolyNorms

	/**
	 * This method calculates vertex normals.
	 */
	public void calcVertNorms()
	{
		Double3D norm;
		PolyListCell curPolyLC;
		vertNormArray = new Double3D[this.numVerts];
		//vertNormArray.ensureCapacity(this.numVerts);
		SurfCell curSurf = surfHead;
		while(curSurf != null){
			//System.out.printf("calcVertNorms: surface: %s smooth: %b\n", curSurf.name, curSurf.smooth);
			System.out.printf("calcVertNorms: surface: %s\n", curSurf.name);
			//if(curSurf.smooth){ // do NOT calculate vertex normals for flat surfs
				PolyCell curPoly = curSurf.polyHead;
				while(curPoly != null){
					VertListCell curVertLC = curPoly.vert;
					while(curVertLC != null){
						
						
						curPolyLC = vertArray.get(curVertLC.vert).polys;
						if(curPolyLC != null){
							//if(curSurf.smooth){
							norm = new Double3D();
							curPolyLC = vertArray.get(curVertLC.vert).polys;
							while(curPolyLC != null)
							{
								if(curPolyLC.poly != null)
									norm = norm.plus(curPolyLC.poly.normal);
								curPolyLC = curPolyLC.next;
							} //end while curPolyLC

							//System.out.printf("calcVertNorms: index: %d out of bounds",curVertLC.vert);
							vertNormArray[curVertLC.vert] = new Double3D(norm.getUnit());
							//vertNormArray.set(curVertLC.vert, new Double3D(norm.getUnit()));	
						}
						else { // still need a vert norm for a non-smooth face
							vertNormArray[curVertLC.vert] = new Double3D(curPoly.normal);
						}
						curVertLC = curVertLC.next;
					}
					curPoly = curPoly.next;
				}//end while curPoly
			//}//end if smooth
			curSurf = curSurf.next;
		}
		System.out.println("Vertex Normals calculated");
	} // end method calcVertNorms

	/**
	 * This method calculates and returns a bounding sphere for this polygonal
	 * mesh.
	 *
	 * @return Sphere
	 */
	public Sphere calcBoundingSphere()
	{
		//PolyCell curPoly;
		//VertListCell curVert;
		//PolyListCell curPolyList;

		double greatest = 0.0;
		double dist;
		for(int i = 0; i < numVerts; i++) {
			dist = vertArray.get(i).worldPos.distanceTo(center);
			if (dist > greatest)
				greatest = dist;
		}
		System.out.println("\nSphere: radius = "+greatest);
		Sphere retVal = new Sphere(center, greatest,this);
		return retVal;
	} // end method calcBoundingSphere

	public void calcViewPolyNorms()
	{
		SurfCell curSurf;
		PolyCell curPoly;
		VertListCell curVert;

		Double3D p1; // The coordinates of the first vertex in the polygon
		Double3D p2; // The coordinates of the second vertex in the polygon
		Double3D p3; // The coordinates of the third vertex in the polygon
		Double3D v1;	// A vector from p1 to p2
		Double3D v2;	// A vector from p2 to p3
		Double3D norm; // The polygon normal

		curSurf = surfHead;

		while(curSurf != null)
		{
			curPoly = curSurf.polyHead;
			while(curPoly != null)
			{
				curVert = curPoly.vert;

				p1 = vertArray.get(curVert.vert).viewPos;
				curVert = curVert.next;
				p2 = vertArray.get(curVert.vert).viewPos;
				curVert = curVert.next;
				p3 = vertArray.get(curVert.vert).viewPos;

				v1 = p2.minus(p1);
				v2 = p3.minus(p2);

				norm = v1.cross(v2);

				norm.unitize();

				curPoly.viewNorm = norm;

				curPoly = curPoly.next;
			}//end polygon loop
			curSurf = curSurf.next;
		}//end surface loop
	} // end method calcViewPolyNorms

	public void calcViewVertNorms()
	{
	} // end method calcViewVertNorms

	public int getMaterialIndexByName(String Name)
	{
		for(int i = 0; i < materials.length; i++){
			if(Name.compareTo(materials[i].materialName) == 0)
				return i;
		} // end for
		return 0;
	} // end method getMaterialIndexByName

	public String toString()
	{
		return objName;
	} // end method toString

} // end class PMesh
