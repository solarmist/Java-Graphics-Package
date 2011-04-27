
import java.io.*;
import java.nio.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.awt.event.KeyEvent;
//import javax.media.opengl.*;
//import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.gl2.*;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import java.nio.*;
import static javax.media.opengl.GL2.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

//import com.sun.opengl.util.*;

/**
 * This class represents the viewable scene containing polygonal objects.
 *
 * @version	5-Apr-2007 - JSR-231
 */
@SuppressWarnings("unused")

public class Scene implements GLEventListener
{

///////////////////////////////////////////////////////////////////////////////
// STATIC VARIABLES

	// These values are used to define default values for the Scene.
	// They are provided as a convenience to allow easy customization of the
	// default camera, default canvas size, and default light position.
	public static final float DEFAULT_EYE_X = 0.0f;
	public static final float DEFAULT_EYE_Y = 0.0f;
	public static final float DEFAULT_EYE_Z = 10.0f;
	public static final float DEFAULT_CTR_X = 0.0f;
	public static final float DEFAULT_CTR_Y = 0.0f;
	public static final float DEFAULT_CTR_Z = 0.0f;
	public static final double DEFAULT_NEAR = 9.0;
	public static final double DEFAULT_FAR = 1000.0;
	public static final double DEFAULT_WIN_EXTENT = 5.0;
	public static final double DEFAULT_VIEWPORT_EXTENT = 600.0;
	public static final double DEFAULT_FOV = 60.0;
	public static final double DEFAULT_ASPECT = 1.0;
	public static final int DEFAULT_WIDTH = 600;
	public static final int DEFAULT_HEIGHT = 600;
	public static final float DEFAULT_LIGHT_X = 0.0f;
	public static final float DEFAULT_LIGHT_Y = 0.0f;
	public static final float DEFAULT_LIGHT_Z = 200.0f;
	public static final float DEFAULT_LIGHT_W = 1.0f;

	//public static float[] GLOBAL_AMBIENT_LIGHT = {0.3f, 0.3f, 0.3f, 1.0f};
	public static DoubleColor GLOBAL_AMBIENT_LIGHT = new DoubleColor(0.3, 0.3, 0.3, 1.0);
	public static int numLoaded = 0;

	public static Document document; // for loading Scene
	
	public CS570 topFrame;

///////////////////////////////////////////////////////////////////////////////
// DATA MEMBERS

	/**
	 * The OpenGL canvas for rendering.
	 */
	public GLCanvas canvas;

	/**
	 * A vector containing the loaded PMesh objects.
	 */
	@SuppressWarnings("unchecked")
	public Vector objects;

	/**
	 * The current PMesh object.
	 */
	public PMesh curObject;

	/**
	 * The camera used to navigate the scene.
	 */
	public Camera camera;

	/**
	 * Used to animate the canvas when changes occur to the camera.
	 */
	public volatile CameraUpdateThread cameraUpdateThread;

	/**
	 * Holds information on which keys are held down.
	 */
	public boolean[] keys = new boolean[256];

	/**
	 * An array containing the scene's lights.
	 */
	public Light lights[];

	/**
	 * Specifies whether or not a light needs to be updated during display().
	 */
	public boolean updateLight;

	/**
	 * Specifies the lights to be updated during display().
	 */
	public boolean[] updateLights;

	/**
	 * Conveys to the scene whether or not an object has been picked.
	 */
	public boolean pickPending;

	/**
	 * Provides info about an object that has been picked.
	 */
	public Double3D pickInfo;

	public ControlPanel cntrlPanel;

	/**
	 * Determines whether or not objects are to be moved.
	 */
	public boolean moveObjects;

	/**
	 * Determines whether or not a world axis should be drawn.
	 */
	public boolean drawAxis;

	// FOG PANEL
	// values for drawing fog
	public boolean fog;
	public int Fred;
	public int Fgreen;
	public int Fblue;
	public float Falpha;
	public char Fhint;
	public char Fequ;
	public float Fstart;
	public float Fend;
	public double Fdensity;
	public float clearColorR;
	public float clearColorG;
	public float clearColorB;
	public float clearColorA;
	// END FOG PANEL

	// booleans for current Scene drawing options (modified from MiscPanel)
	public boolean fillMode; // filled / wireframe
	public boolean windCCW; // counter-clockwise / clockwise
	public boolean cull; // culling enabled / disabled
	public boolean cullBack; // back-facing / front-facing
	public boolean gouraudShading; // Gouraud shading / Flat shading
	public boolean maintainAspect; // maintain viewport/window aspect ratio
	public boolean lightingEnabled; // is glLighting on?
	public boolean projectionPersp;
	
	//booleans for use in Ray Tracing
	public boolean rayTrace = false;
	public boolean spheresOnly = false;
	public boolean antiAliasing = false;
	public boolean shadows = false;
	public boolean reflections = false;
	public boolean refractions = false;
	public boolean checkerBackground = false;
	public int raysPerPixel = 1;
	public int maxRecursiveDepth = 0;
	public boolean photonMapping;
	public boolean photonCastingOnly;
	public boolean drawPhotonType;
	public boolean excludeDirectPhotons;
	public boolean useSavedPhotonMaps;
	public int PHOTON_MAX_RECURSIVE_DEPTH;
	public int PHOTON_NUM_SAMPLES;
	public int PHOTON_NUM_PER_LIGHT;
	public int PHOTON_NUM_GATHER_DIRECT;
	public int PHOTON_NUM_GATHER_INDIRECT;
	public int PHOTON_NUM_GATHER_CAUSTIC;
	public float[] exposure;
	public int PHOTON_MAX_CAUSTIC;
	public int PHOTON_MAX_DIRECT;
	public int PHOTON_MAX_INDIRECT;
	public float PHOTON_RADIUS_CAUSTIC;
	public float PHOTON_RADIUS_DIRECT;
	public float PHOTON_RADIUS_INDIRECT;
	public int RAYTRACE_MAX_RECURSIVE_DEPTH;
	public int RAYTRACE_NUM_SAMPLES;

	
///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTORS

	/**
	 * Scene constructor.
	 */
	public Scene(CS570 topFrame)
	{
		this.topFrame = topFrame;
		init(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	} // end constructor

	/**
	 * Scene Constructor.
	 *
	 * @param w int width
	 * @param h int height
	 */
	public Scene(CS570 topFrame, int w, int h)
	{
		this.topFrame = topFrame;
		init(w, h);
	} // end constructor

	/**
	 * This method initializes the Scene.
	 *
	 * @param width int
	 * @param height int
	 * @param terrainFileName String
	 */
	@SuppressWarnings("unchecked")
	public void init(int width, int height)
	{
	    GLProfile.initSingleton(true);
    	GLProfile glp;
    	if(GLProfile.isGL3Available()){
    		System.out.printf("GL3 is available");
    		glp = GLProfile.get("GL2");
    	}
    	else{
    		System.out.printf("GL3 is NOT available\n");
            glp = GLProfile.getDefault();    	
        }
        System.out.printf("Profile: %s\n",glp.toString());
        GLCapabilities caps = new GLCapabilities(glp);
        canvas = new GLCanvas(caps);
		canvas.addGLEventListener(this);

		canvas.setSize(width,height);
		camera = Scene.createDefaultCamera();
		objects = new Vector();
		curObject = null;
		// configure lights
		lights = new Light[8];
		for (int i = 0; i < 8; i++) { // create 8 lights
			lights[i] = new Light();
			lights[i].lightName = lights[i].lightName + i;
			// Light constructor initializes lightName with GL_LIGHT0
		}
		lights[0].lightSwitch = Light.ON; // setup lights[0] with default settings
		lights[0].location = Light.LOCAL;
		lights[0].position = new float[]
			{ DEFAULT_LIGHT_X, DEFAULT_LIGHT_Y, DEFAULT_LIGHT_Z, DEFAULT_LIGHT_W };
		updateLight = true;
                updateLights = new boolean[8];
                for(int i = 0; i < updateLights.length; i++)
                    updateLights[i] = false;
		updateLights[0] = true;
		pickPending = false;
		pickInfo = null;
		cntrlPanel = null;
		moveObjects = true;
		drawAxis = true;
		// MiscPanel Default Settings:
		fillMode = true; // filled
		windCCW = true; // counter-clockwise
		cull = false; // enabled
		cullBack = true; // back-facing
		gouraudShading = true; // Gouraud shading
		lightingEnabled = true;
		maintainAspect = true;
		projectionPersp = true;
		// End MiscPanel
		cameraUpdateThread = new CameraUpdateThread(this);
		cameraUpdateThread.start();
	} // end method commonInit

///////////////////////////////////////////////////////////////////////////////
// GLEventListener METHODS

	/**
	 * This method is called whenever the canvas needs to be displayed. You can
	 * force this method to be called by using canvas.display().
	 *
	 * @param drawable GLDrawable
	 */
	public void display(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		if (updateLight) { // does a light need to be updated?
			for (int i = 0; i < updateLights.length; i++)
				if (updateLights[i]) {
					updateLight(i, gl, glu);
					updateLights[i] = false;
				} // end if
			updateLight = false;
		} // end if
		
		if(rayTrace)
		{
  			System.out.println("Begin RayTrace");
  			System.out.println("On "+ Thread.currentThread());
  			topFrame.statusArea.showStatus("Ray Tracing...");
 			//RayTracer rayTracer = new RayTracer(this, gl, glu);
  			System.out.println("End RayTrace");
			
			gl.glMatrixMode(gl.GL_PROJECTION);
	        gl.glPopMatrix();

			rayTrace = false;

		} // end if
		else {
			if (pickPending) { // was an object picked?
				int x = (int) pickInfo.x;
				int y = (int) pickInfo.y;
				pickObject(x, y, gl, glu);
				pickPending = false;
			} // end if

		camera.update();
		camera.look(gl, glu);

		gl.glClearColor(this.clearColorR, this.clearColorG, this.clearColorB,
						this.clearColorA); // FogPanel
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (this.fog) { // is fog enabled?
			setupFog(gl, glu);
		} // end if
		else {
			gl.glDisable(GL_FOG);
		} // end else

		if (drawAxis) {
			drawAxis(gl, glu);
		} // end if

		// Setup appropriate OpenGL state based on settings (fill, cull, etc.)
		setupOpenGLState(gl, glu);

		// Draw each PMesh object
		PMesh curObj = null;
		for(int i = 0; i < objects.size(); i++) {
			curObj = (PMesh) objects.get(i);
			curObj.draw(camera, gl, glu);
		} // end for
		}
	} // end method display
	/**
	 * Called by drawable to indicate mode or device has changed.
	 *
	 * @param drawable GLAutoDrawable
	 * @param modeChanged boolean
	 * @param deviceChanged boolean
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
							   boolean deviceChanged) {
		System.out.println("displayChanged()");
	} // end method displayChanged

	/**
	 * Called to initialize
	 *
	 * @param drawable GLAutoDrawable
	 */
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_NORMALIZE);
		gl.glMatrixMode(GL_MODELVIEW);
	} // end method init

	/**
	 * Called to indicate the drawing surface has been moved and/or resized
	 *
	 * @param drawable GLAutoDrawable
	 * @param x int
	 * @param y int
	 * @param width int
	 * @param height int
	 */
	public void reshape(GLAutoDrawable drawable,
						int x, int y, int width, int height) {
		System.out.println("reshape()");
		GL2 gl = drawable.getGL().getGL2();
		GLU glu =  new GLU();
		System.out.println("reshape: width: " + width + " height: " + height);
		if(width <= 0) // invalid width
		{
			System.out.println("reshape: invalid viewport width <" + width + "> changing to <1>");
			width = 1;
		}
		if(height <= 0) // invalid height
		{
			System.out.println("reshape: invalid viewport height <" + height + "> changing to <1>");
			height = 1;
		}
		// Reset The Current Viewport And Perspective Transformation
		gl.glViewport(0, 0, width, height);
		camera.setViewport(0.0, width, height, 0.0);
		if (maintainAspect) {
			adjustWindowAspect();
			cntrlPanel.cameraPanel.updateCameraFields();
		} // end if
	} // end method reshape
	public void dispose(GLAutoDrawable drawable) {

	}
// End GLEventListener METHODS
/////////////////////////////////////////////////////////////////////

	/**
	 * This method sets the OpenGL state based on the current polygon mode,
	 * vertex winding order, and culling settings.
	 *
	 * @param gl GL
	 * @param glu GLU
	 */
	public void setupOpenGLState(GL2 gl, GLU glu)
	{
		if(lightingEnabled)
			gl.glEnable(GL_LIGHTING);
		else{
			gl.glDisable(GL_LIGHTING);
			gl.glColor3d(1.0, 0.0, 0.0);
			gl.glLineWidth(1.0f);
		}
		if(fillMode) // filled
			gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
		else // wireframe
			gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
		if( windCCW ) // counter-clockwise winding
			gl.glFrontFace(gl.GL_CCW);
		else // clockwise winding
			gl.glFrontFace(gl.GL_CW);
		if( cull ) {
			gl.glEnable(gl.GL_CULL_FACE);
			if( cullBack ) // back face culling
				gl.glCullFace(gl.GL_BACK);
			else // front face culling
				gl.glCullFace(gl.GL_FRONT);
		} // end if
		else
			gl.glDisable(gl.GL_CULL_FACE);
	} // end method setupOpenGLState


	/**
	 * This method loads an object of the specified type from the specified file
	 * and inserts it in the scene.
	 *
	 * @param fileName String
	 * @param fileType int
	 */
	public void addObject(String fileName, int fileType) {
		PMesh newObj = null;
		switch (fileType) {
			case ObjectTypes.TYPE_OBJ:
				newObj = new ObjLoaderBuffer(this);
				newObj.objNumber = ++numLoaded;
				break;
			case ObjectTypes.TYPE_3DS:
				newObj = new ThreeDSLoader(this);
				newObj.objNumber = ++numLoaded;
				break;
			case ObjectTypes.TYPE_DAE:
				newObj = new ColladaLoader(this);
				newObj.objNumber = ++numLoaded;
				break;
			default:
				System.out.println("Scene.addObject : undefined object type " +
								   fileType);
		} // end switch

		try { // load and insert the new object
			newObj.load(fileName);
			this.curObject = newObj;
			this.objects.add(newObj);
		} // end try
		catch (FileNotFoundException e) {
			System.out.println("Error loading object");
		} // end catch
	} // end method addObject

	/**
	 * This method removes the object with the specified name from the scene. In
	 * the case that one or more objects share the same name, the first one in the
	 * linked list will be deleted.
	 *
	 * @param name String
	 */
	public void deleteObject(String name) {
		PMesh curObj;
		for(int i = 0; i < objects.size(); i++) {
			curObj = (PMesh) objects.get(i);
			if(curObj.objName.compareTo(name) == 0) { // the name matches
				objects.remove(i);
				break;
			} // end if
		} // emd for
	} // end method deleteObject

	// The following methods are called from the interface code
	// thus they must extract their own gl and glu from the drawable

	/**
	 * This method updates the OpenGL state for the light at the specified index
	 * using its current settings.
	 *
	 * @param lightIndex int
	 */
	public void updateLight(int lightIndex, GL2 gl, GLU glu) {
		//GL gl = canvas.getGL();
		//GLU glu = canvas.getGLU();
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glMultMatrixd(camera.viewMat,0);
		// REMOVE FOR CLASS
		System.out.println("Updating light" + lightIndex);
		if (lights[lightIndex].lightSwitch == Light.ON) {
			if (lights[lightIndex].position[3] >= 0.5f) { // Local light
				lights[lightIndex].position[3] = 1.0f;
				lights[lightIndex].location = Light.LOCAL;
			} // end if
			else { // Directional light
				lights[lightIndex].position[3] = 0.0f;
				lights[lightIndex].location = Light.DIRECTIONAL;
			} // end else
			gl.glLightfv(lights[lightIndex].lightName, GL_SPECULAR,
						 lights[lightIndex].specular, 0);
			gl.glLightfv(lights[lightIndex].lightName, GL_AMBIENT,
						 lights[lightIndex].ambient, 0);
			gl.glLightfv(lights[lightIndex].lightName, GL_DIFFUSE,
						 lights[lightIndex].diffuse, 0);
			gl.glLightfv(lights[lightIndex].lightName, GL_POSITION,
						 lights[lightIndex].position, 0);
			if (lights[lightIndex].location == Light.LOCAL) {
				gl.glLightfv(lights[lightIndex].lightName, GL_SPOT_DIRECTION,
							 lights[lightIndex].direction, 0);
				if(lights[lightIndex].spotLight){
					gl.glLightf(lights[lightIndex].lightName, GL_SPOT_CUTOFF,
							lights[lightIndex].spotCutoff);
					gl.glLightf(lights[lightIndex].lightName, GL_SPOT_EXPONENT,
							lights[lightIndex].spotExponent);
				}
				else {// must be a point light
					gl.glLightf(lights[lightIndex].lightName, GL_SPOT_CUTOFF,180.0f);
					gl.glLightf(lights[lightIndex].lightName, GL_SPOT_EXPONENT,0.0f);
				}
				gl.glLightf(lights[lightIndex].lightName,
							GL_CONSTANT_ATTENUATION,
							lights[lightIndex].constAttenuation);
				gl.glLightf(lights[lightIndex].lightName,
							GL_LINEAR_ATTENUATION,
							lights[lightIndex].linearAttenuation);
				gl.glLightf(lights[lightIndex].lightName,
							GL_QUADRATIC_ATTENUATION,
							lights[lightIndex].quadraticAttenuation);
			}
			else {  // DIRECTIONAL LIGHT
				gl.glLightf(lights[lightIndex].lightName, GL_SPOT_CUTOFF,180.0f);
				gl.glLightf(lights[lightIndex].lightName, GL_SPOT_EXPONENT,0.0f);
			}

			gl.glEnable(lights[lightIndex].lightName);
		} // end if
		else {
			gl.glDisable(lights[lightIndex].lightName);
		} // end else
	} // end method updateCurLight

        /**
	 * This method adjusts the window width so that the window aspect ratio
	 * matches the aspect ratio of the viewport.
	 */
	public void adjustWindowAspect() {
		double viewportAspect = camera.getViewportWidth() / (double) camera.getViewportHeight();
		double winWidth = camera.getWindowWidth();
		double winHeight = camera.getWindowHeight();
		double winLeft = camera.windowLeft;
		double winRight = camera.windowRight;
		double winTop = camera.windowTop;
		double winBottom = camera.windowBottom;
		double windowAspect = winWidth / (double) winHeight;
		double newWinWidth = viewportAspect * winHeight;
		double widthChange = (newWinWidth - winWidth); // total change
		widthChange /= 2.0; // split width change between left and right
		winLeft -= widthChange;
		winRight += widthChange;
		camera.setFrustum(winLeft, winRight, winBottom, winTop,
						  camera.near, camera.far);
	} // end method adjustWindowAspect

///////////////////////////////////////////////////////////////////////////////
// OBJECT PICKING METHODS

	/**
	 * This method picks an object.
	 *
	 * @param x int
	 * @param y int
	 * @param gl GL
	 * @param glu GLU
	 */
	public void pickObject(int x, int y, GL2 gl, GLU glu) {
		boolean isAnObjectSelected = false;
		if(objects.size() < 1)
			return;
		PMesh tmpObj = (PMesh) objects.get(0);
		int selectedObj = getObj_PickMatrix(x, y, gl, glu);
		if (selectedObj >= 0) {
			cntrlPanel.objectPanel.objectList.setSelectedIndex(selectedObj);
			curObject = (PMesh) objects.get(selectedObj);
			System.out.println(curObject.objName + " selected.");
			isAnObjectSelected = true;
		} // end if
		else {
			System.out.println("An object was not selected.");
			isAnObjectSelected = false;
		} // end else
		// if curObject is null, nothing was selected
	} // end method pickObject

	public int getObj_PickMatrix(int x, int y, GL2 gl, GLU glu)
	{
		int hits = 0; // Number of objects under the click
		int[] viewportCoords = new int[4]; // viewport info
		//int[] selectionBuffer = new int[32];
		IntBuffer selectionBuffer;		
		ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(32*4); // 4 bytes for 32-bit int
		directByteBuffer.order(ByteOrder.nativeOrder()); // use native byte order
		selectionBuffer = directByteBuffer.asIntBuffer();	
		PMesh tmpObj;

		gl.glSelectBuffer(32, selectionBuffer); // setup the selection buffer
		gl.glGetIntegerv(GL_VIEWPORT, viewportCoords, 0); // get the viewport coords
		gl.glMatrixMode(GL_PROJECTION);
		gl.glPushMatrix(); // push a new matrix so we don't mess the other one up
		// set the rendering mode to GL_SELECT so we don't change the frame buffer
		gl.glRenderMode(GL_SELECT);
		gl.glLoadIdentity(); // Reset our projection matrix
		// y coordinates are 'backwards' so subtract y from the height of the viewport
		glu.gluPickMatrix(x, viewportCoords[3] - y, 1, 1, viewportCoords, 0);
		// use the same values as when the objects on the screen were drawn
		if (camera.viewingMode == Camera.FRUSTUM_MODE) {
			gl.glFrustum(
				camera.windowLeft, camera.windowRight,
				camera.windowBottom, camera.windowTop,
				camera.near, camera.far);
		} // end if
		else if (camera.viewingMode == Camera.PERSPECTIVE_MODE) {
			glu.gluPerspective(camera.fov, camera.aspectRatio,
							   camera.near, camera.far);
		} // end if
		gl.glMatrixMode(GL_MODELVIEW);
		// drawing the entire scene into the selection buffer
		for(int i = 0; i < objects.size(); i++) {
			tmpObj = (PMesh) objects.get(i);
			gl.glPushName(i);
			tmpObj.draw(camera, gl, glu);
		} // end for
		// setting the render mode back to GL_RENDER returns information about
		// the objects that were drawn within our pickmatrix
		hits = gl.glRenderMode(GL_RENDER);
		// return these to 'normal'
		gl.glMatrixMode(GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL_MODELVIEW);

		int i, j, k;
		int namez, z1, z2;
		String linez, text, temp;
		linez = text = temp = "";
		int min, minName;
		minName = 0;
		text = "(PickMatrix) No. of hits: " + hits + "\n";
		j = 0;

		for (i = 0; i < hits; i++) {
			linez = "";
			//minName = namez = selectionBuffer[j];
			minName = namez = selectionBuffer.get(j);
			j++;
			//min = z1 = selectionBuffer[j];
			min = z1 = selectionBuffer.get(j);
			j++;
			//z2 = selectionBuffer[j];
			z2 = selectionBuffer.get(j);
			j++;
			temp = "Number of names: " + namez + " Min z: " + z1 + " Max z: " +
				z2 + "\n";
			if (z1 < min) {
				minName = namez;
				min = z1;
			} // end if
			for (k = 0; k < namez; k++) {
				//linez += "Object Name: " + selectionBuffer[j] + "\n";
				linez += "Object Name: " + selectionBuffer.get(j) + "\n";
				j++;
			} // end for
			text += temp;
			text += linez;
		} // end for
		// System.out.println(text);
		return minName-1;
	} // end method getObj_PickMatrix

///////////////////////////////////////////////////////////////////////////////
// SET METHODS

	

	public void setClearColor(float r, float g, float b, float alpha) {
		this.clearColorR = r;
		this.clearColorG = g;
		this.clearColorB = b;
		this.clearColorA = alpha;
	} // end method setClearColor

	// End Fog Set Methods

///////////////////////////////////////////////////////////////////////////////
// GET METHODS


	public PMesh getPMesh(int index) {
		return (PMesh) objects.get(index);
	} // end method getPMesh

	public PMesh getLastPMesh() {
		return (PMesh) objects.get( objects.size()-1 );
	} // end method getLastPMesh

	public int getNumPMeshObjects() {
		return objects.size();
	} // end method getNumPMeshObjects


///////////////////////////////////////////////////////////////////////////////
// OTHER METHODS

	public boolean hasCurObject() {
		return curObject != null;
	} // end method hasCurObject

	public Light getLight(int index) {
		return lights[index];
	} // end method getLight

	/**
	 * This method refreshes the canvas by calling canvas.display() to redraw
	 * the scene on the canvas.
	 */
	public void refreshCanvas() {
		canvas.display();
	} // end method refreshCanvas

	/**
	 * This method sets a flag indicating that the light at the specified index
	 * should be updated next time the scene is drawn.
	 *
	 * @param lightIndex int
	 */
	public void updateLightNextFrame(int lightIndex) {
		if (lightIndex >= 0 && lightIndex < updateLights.length) {
			updateLight = true;
			updateLights[lightIndex] = true;
		} // end if
	} // end method updateLightNextFrame

	/**
	 * This method enables objects movement.
	 */
	public void enableObjectMovement() {
		moveObjects = true;
	} // end method enableObjectMovement

	/**
	 * This method disables object movement.
	 */
	public void disableObjectMovement() {
		moveObjects = false;
	} // end method disableObjectMovement

	/**
	 * This method returns whether or not objects are currently allowed to move.
	 *
	 * @return boolean
	 */
	public boolean isObjectMovementEnabled() {
		return moveObjects;
	} // end method isObjectMovementEnabled

	/**
	 * This method creates and returns a camera using default settings.
	 *
	 * @return Camera
	 */
	public static Camera createDefaultCamera() {
		Double3D eye = new Double3D(DEFAULT_EYE_X, DEFAULT_EYE_Y, DEFAULT_EYE_Z);
		Double3D center = new Double3D(DEFAULT_CTR_X, DEFAULT_CTR_Y,
									 DEFAULT_CTR_Z);
		Double3D up = new Double3D(0.0, 1.0, 0.0);
		Camera cam = new Camera(eye, center, up,
								DEFAULT_NEAR, DEFAULT_FAR, // near, far
								-1 * DEFAULT_WIN_EXTENT, DEFAULT_WIN_EXTENT, // window extents
								DEFAULT_WIN_EXTENT, -1 * DEFAULT_WIN_EXTENT, // window extents
								0.0, DEFAULT_VIEWPORT_EXTENT,
								DEFAULT_VIEWPORT_EXTENT, 0.0, // viewport extents
								DEFAULT_FOV, DEFAULT_ASPECT, // fov, aspect ratio
								Camera.FRUSTUM_MODE);
		return cam;
	} // end method createDefaultCamera

	/**
	 * This method enables fog with the current fog settings. It should be called
	 * from display().
	 *
	 * @param gl GL
	 * @param glu GLU
	 */
	public void setupFog(GL2 gl, GLU glu) {
		float fogColor[] = {
			 (float) (this.Fred) / 256, (float) (this.Fgreen) / 256,
			(float) (this.Fblue) / 256, this.Falpha};
		gl.glFogfv(GL_FOG_COLOR, fogColor, 0);
		if (this.Fhint == 'd')
			gl.glHint(GL_FOG_HINT, GL_DONT_CARE);
		else if (this.Fhint == 'n')
			gl.glHint(GL_FOG_HINT, GL_NICEST);
		else
			gl.glHint(GL_FOG_HINT, GL_FASTEST);

		if (this.Fequ == 'l')
			gl.glFogi(GL_FOG_MODE, GL_LINEAR);
		else {
			if (this.Fequ == 'e')
				gl.glFogi(GL_FOG_MODE, GL_EXP);
			else
				gl.glFogi(GL_FOG_MODE, GL_EXP2);
			gl.glFogf(GL_FOG_DENSITY, (float)this.Fdensity);
		} // end else

		gl.glFogf(GL_FOG_START, this.Fstart);
		gl.glFogf(GL_FOG_END, this.Fend);
		gl.glEnable(GL_FOG);
	} // end method setupFog

	/**
	 * This method draws an axis at the origin. It should be called from display()
	 * if the axis is desired.
	 *
	 * @param gl GL
	 * @param glu GLU
	 */
	public void drawAxis(GL2 gl, GLU glu) {
		gl.glLineWidth(2.0f);
		gl.glMatrixMode(gl.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glMultMatrixd(camera.viewMat, 0);
		if(lightingEnabled)
			gl.glDisable(gl.GL_LIGHTING);
		gl.glBegin(gl.GL_LINES);
		gl.glColor3d(1.0, 0.0, 0.0);
		gl.glVertex3d(0.0, 0.0, 0.0);
		gl.glVertex3d(10000.0, 0.0, 0.0);
		gl.glColor3d(0.0, 1.0, 0.0);
		gl.glVertex3d(0.0, 0.0, 0.0);
		gl.glVertex3d(0.0, 10000.0, 0.0);
		gl.glColor3d(0.0, 0.0, 1.0);
		gl.glVertex3d(0.0, 0.0, 0.0);
		gl.glVertex3d(0.0, 0.0, 10000.0);
		gl.glEnd();
		if(lightingEnabled)
			gl.glEnable(gl.GL_LIGHTING);
		gl.glLineWidth(1.0f);
	} // end method drawAxis

///////////////////////////////////////////////////////////////////////////////
// KEYBOARD INPUT

	/**
	 * This method maps keyboard keys to their corresponding camera actions.
	 *
	 * @param keyCode int
	 * @return int
	 */
	public int getAction(int keyCode) {
		switch (keyCode) {
			case 'A':
				return CameraActions.STRAFE_LEFT;
			case 'D':
				return CameraActions.STRAFE_RIGHT;
			case 'W':
				return CameraActions.MOVE_FORWARD;
			case 'S':
				return CameraActions.MOVE_BACKWARD;
			case 'Q':
				return CameraActions.STRAFE_UP;
			case 'E':
				return CameraActions.STRAFE_DOWN;
			case KeyEvent.VK_UP:
				return CameraActions.ROTATE_UP;
			case KeyEvent.VK_DOWN:
				return CameraActions.ROTATE_DOWN;
			case KeyEvent.VK_LEFT:
				return CameraActions.ROTATE_LEFT;
			case KeyEvent.VK_RIGHT:
				return CameraActions.ROTATE_RIGHT;
			default:
				return -1; // unrecognized key
		} // end switch
	} // end method getAction

///////////////////////////////////////////////////////////////////////////////
// SAVING / LOADING

	/**
	 * This method saves the current Scene settings to the specified file using XML.
	 *
	 * @param fileName String
	 * @return boolean
	 */
	public boolean save(String fileName)
	{
		try
		{
			int i, j, k;
			BufferedWriter fout = new BufferedWriter(new FileWriter(fileName));

			fout.write("<?xml version=\"1.0\" standalone=\"no\"?>\n");
			fout.write("<!DOCTYPE Scene SYSTEM \"scene.dtd\">\n");

			fout.write("<Scene>\n"); //opening scene tag
			fout.write("<Objects>\n"); //opening objects tag


			PMesh curObject = null;

			for(int n = 0; n < objects.size(); n++)
			{
				curObject = (PMesh) objects.get(n);

				fout.write("<PMesh>\n");
				fout.write(writeTag("fileType", curObject.fileType));
				fout.write(writeTag("objName", curObject.objName));

				fout.write("<modelMat>\n");
				k = 0; //ensure initialization of k
				for (i = 0; i < 4; i++)
					for (j = 0; j < 4; j++)
						fout.write(writeTag("x" + i + j, Double.toString(curObject.modelMat[k++])));

				fout.write("</modelMat>\n");
				fout.write("<center>\n");
				fout.write("<X>" + curObject.center.x + "</X>");
				fout.write("<Y>" + curObject.center.y + "</Y>");
				fout.write("<Z>" + curObject.center.z + "</Z>");
				fout.write("</center>\n");

				fout.write("</PMesh>\n");

			} //end for

			fout.write("</Objects>\n"); //closing objects tag

			fout.write("<Lights>\n"); //opening all lights tag
			Light cLt;
			for (i = 0; i < 8; i++)
			{
				cLt = getLight(i);
				fout.write("<Light>\n"); //opening light tag
				fout.write(writeTag("Number", Integer.toString(i)));

				fout.write(writeTag("lightSwitch", Integer.toString(cLt.lightSwitch)));

				fout.write("<ambient>\n");
				fout.write(writeTag("R", Float.toString(cLt.ambient[0])));
				fout.write(writeTag("G", Float.toString(cLt.ambient[1])));
				fout.write(writeTag("B", Float.toString(cLt.ambient[2])));
				fout.write(writeTag("A", Float.toString(cLt.ambient[3])));
				fout.write("</ambient>\n");

				fout.write("<diffuse>\n");
				fout.write(writeTag("R", Float.toString(cLt.diffuse[0])));
				fout.write(writeTag("G", Float.toString(cLt.diffuse[1])));
				fout.write(writeTag("B", Float.toString(cLt.diffuse[2])));
				fout.write(writeTag("A", Float.toString(cLt.diffuse[3])));
				fout.write("</diffuse>\n");

				fout.write("<specular>\n");
				fout.write(writeTag("R", Float.toString(cLt.specular[0])));
				fout.write(writeTag("G", Float.toString(cLt.specular[1])));
				fout.write(writeTag("B", Float.toString(cLt.specular[2])));
				fout.write(writeTag("A", Float.toString(cLt.specular[3])));
				fout.write("</specular>\n");

				//end rgba, goto xyzw
				fout.write("<position>\n");
				fout.write(writeTag("X", Float.toString(cLt.position[0])));
				fout.write(writeTag("Y", Float.toString(cLt.position[1])));
				fout.write(writeTag("Z", Float.toString(cLt.position[2])));
				fout.write(writeTag("W", Float.toString(cLt.position[3])));
				fout.write("</position>\n");

				fout.write("<viewPos>\n");
				fout.write(writeTag("X", Float.toString(cLt.viewPos[0])));
				fout.write(writeTag("Y", Float.toString(cLt.viewPos[1])));
				fout.write(writeTag("Z", Float.toString(cLt.viewPos[2])));
				fout.write(writeTag("W", Float.toString(cLt.viewPos[3])));
				fout.write("</viewPos>\n");

				fout.write("<direction>\n");
				fout.write(writeTag("X", Float.toString(cLt.direction[0])));
				fout.write(writeTag("Y", Float.toString(cLt.direction[1])));
				fout.write(writeTag("Z", Float.toString(cLt.direction[2])));
				fout.write(writeTag("W", Float.toString(cLt.direction[3])));
				fout.write("</direction>\n");

				fout.write(writeTag("spotCutoff", Float.toString(cLt.spotCutoff)));
				fout.write(writeTag("spotExponent", Float.toString(cLt.spotExponent)));

				fout.write("<spotDirection>\n");
				fout.write(writeTag("X", Float.toString(cLt.spotDirection[0])));
				fout.write(writeTag("Y", Float.toString(cLt.spotDirection[1])));
				fout.write(writeTag("Z", Float.toString(cLt.spotDirection[2])));
				fout.write("</spotDirection>\n");

				fout.write(writeTag("constAttenuation", Float.toString(cLt.constAttenuation)));
				fout.write(writeTag("linearAttenuation", Float.toString(cLt.linearAttenuation)));
				fout.write(writeTag("quadraticAttenuation", Float.toString(cLt.quadraticAttenuation)));

				fout.write(writeTag("location", Integer.toString(cLt.location)));

				fout.write("</Light>\n"); //closing this light tag
			} //end writing out each light
			fout.write("</Lights>\n"); //closing all lights tag

			Camera curCam = camera;
			fout.write("<Camera>\n");

			fout.write("<viewMat>\n");
			k=0;    //ensure initialization of k
			double[] viewMat = curCam.viewMat;
			for (i=0; i<4; i++)
				for (j=0; j<4; j++)
					fout.write(writeTag("x"+i+j, Double.toString(viewMat[k++])));
			fout.write("</viewMat>\n");

			fout.write(writeTag("near", Double.toString(curCam.near)));
			fout.write(writeTag("far", Double.toString(curCam.far)));
			fout.write(writeTag("upX", Double.toString(curCam.up.x)));
			fout.write(writeTag("upY", Double.toString(curCam.up.y)));
			fout.write(writeTag("upZ", Double.toString(curCam.up.z)));

			fout.write(writeTag("eyeX", Double.toString(curCam.eye.x)));
			fout.write(writeTag("eyeY", Double.toString(curCam.eye.y)));
			fout.write(writeTag("eyeZ", Double.toString(curCam.eye.z)));

			fout.write(writeTag("ctrX", Double.toString(curCam.center.x)));
			fout.write(writeTag("ctrY", Double.toString(curCam.center.y)));
			fout.write(writeTag("ctrZ", Double.toString(curCam.center.z)));

			fout.write(writeTag("wLeft", Double.toString(curCam.windowLeft)));
			fout.write(writeTag("wRight", Double.toString(curCam.windowRight)));
			fout.write(writeTag("wBottom", Double.toString(curCam.windowBottom)));
			fout.write(writeTag("wTop", Double.toString(curCam.windowTop)));

			fout.write("</Camera>\n");

			fout.write("</Scene>\n"); //closing scene tag
			fout.close();
			return true;
		} //end try block
		catch (IOException ev) {
			System.out.println("Could not open file for output, operation terminated");
			return false;
		} //end catch IOException
	} //end of save

	/**
	 * This method returns a properly formatted XML tag using the specified tag
	 * and value.
	 *
	 * @param t String
	 * @param val String
	 * @return String
	 */
	public String writeTag(String t, String val) {
		return "<" + t + ">" + val + "</" + t + ">\n";
	} //end writeTag

	/**
	 * This method loads a scene from the specified file into the specified
         * Scene. The current objects, light, and camera settings of the scene
         * are removed and replaced with the settings contained in the file.
         * If the scene could not be loaded, the Scene parameter is not modified
         * and false is returned.
	 *
	 * @param fileName String
	 * @param Scene The object to load the scene into
         * @return boolean Whether or not the Scene was loaded
	 */
	public static boolean load(String fileName, Scene newScene)
	{
		String str;
		int fileType = 0;
		File file;
		Node node = null, node2;
		DocumentBuilderFactory factory;

		file = new File(fileName);
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		DtdOk warningCheck = new DtdOk();
		DtdHandler dtdHandler = new DtdHandler(warningCheck);

		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(dtdHandler);
			document = builder.parse(file);
		} // end try
		catch (SAXParseException spe)
		{
			// Error generated by the parser
			System.out.println("\n** Parsing error"
                               + ", line " + spe.getLineNumber()
                               + ", uri " + spe.getSystemId());
			System.out.println("   " + spe.getMessage());

			// Use the contained exception, if any
			Exception x = spe;
			if (spe.getException() != null)
				x = spe.getException();
			x.printStackTrace();
			warningCheck.dtdOk = false;
		} // end catch
		catch (SAXException sxe) {
			// Error generated during parsing)
			Exception x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();
			warningCheck.dtdOk = false;
		} // end catch
		catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
			warningCheck.dtdOk = false;
		} // end catch
		catch (IOException ioe) {
			// I/O error
			ioe.printStackTrace();
			warningCheck.dtdOk = false;
		} // enc catch

		if (warningCheck.dtdOk == false) {
			System.out.println("Problem with dtd");
			return false;
		} // end if

                // clear out the current Scene's settings
                newScene.objects.clear(); // clear the object list

		// Get Scene Node
		if (document.hasChildNodes())
			node = document.getFirstChild();

		// Nodes below Scene
		while (node != null) {
			if (node.getNodeName().equals("Scene") &&
				node.getNodeType() == node.ELEMENT_NODE)
				break;
			node = node.getNextSibling();
		} // end while

		if (node != null && node.hasChildNodes())
			node = node.getFirstChild();

		// Let's Handle the Objects Under Scene
		while (node != null) {
			// ****** Handling Objects Stuff ***********************
			if (node.getNodeName().equals("Objects")) {
				// if PMesh's Exist move node2 down to it
				if (node.hasChildNodes()) {
					node2 = node.getFirstChild();

					// Process Each PMesh
					while (node2 != null) {
						// to Avoid java complaining
						PMesh pmesh = null;
						Node node3 = null;

						if (node2.hasChildNodes() &&
							node2.getNodeName().equals("PMesh"))
							node3 = node2.getFirstChild();

						// Processing Stuff in PMesh
						while (node3 != null) {
							Node node4 = null;
							if (node3.getNodeName().equals("objName")) {
								node4 = node3.getFirstChild();
								newScene.addObject("Objects/" +
									node4.getNodeValue(), fileType);
								pmesh = newScene.getLastPMesh();
							}
							else if (node3.getNodeName().equals("modelMat")) {
								int index = 0;
								node4 = node3.getFirstChild();
								while (node4 != null) {
									if (!node4.getNodeName().equals("#text")) {
										Node node5;
										node5 = node4.getFirstChild();
										pmesh.modelMat[index] =
											(new Double(node5.getNodeValue())).
											doubleValue();
										index++;
									}
									node4 = node4.getNextSibling();
								}
							}
							else if (node3.getNodeName().equals("center")) {
								node4 = node3.getFirstChild();
								while (node4 != null) {
									//if(! node4.getNodeName().equals("#text"))
									if (node4.getNodeName().equals("X")) {
										Node node5;
										node5 = node4.getFirstChild();
										if (node5 != null)
											pmesh.center.x = new Double(node5.
												getNodeValue()).doubleValue();
									}
									else if (node4.getNodeName().equals("Y")) {
										Node node5;
										node5 = node4.getFirstChild();
										if (node5 != null)
											pmesh.center.y = new Double(node5.
												getNodeValue()).doubleValue();
									}
									else if (node4.getNodeName().equals("Z")) {
										Node node5;
										node5 = node4.getFirstChild();
										if (node5 != null)
											pmesh.center.z = new Double(node5.
												getNodeValue()).doubleValue();
									}

									node4 = node4.getNextSibling();
								}

							} // end else if
							else if (node3.getNodeName().equals("fileType")) {
								node4 = null;
								node4 = node3.getFirstChild();
								str = node4.getNodeValue();
								if (str.equalsIgnoreCase("OBJ"))
									fileType = 1;
								else if (str.equalsIgnoreCase("3DS"))
									fileType = 2;
								else
									fileType = 0;
								System.out.println("\n***FileType: " + fileType);
							} // end else if

							node3 = node3.getNextSibling();
						} // end Processing of Content of PMesh
						node2 = node2.getNextSibling();
					} // end Processing PMeshes
				} // end if There are PMeshes to process
			} // end Handling Objects Stuff

			else if (node.getNodeName().equals("Lights")) {
				// if PMesh's Exist move node2 down to it
				if (node.hasChildNodes()) {
					node2 = node.getFirstChild();

					// Process Each PMesh
					while (node2 != null) {
						Node node3 = null;
						int lightIndex = 0;

						if (node2.hasChildNodes() &&
							node2.getNodeName().equals("Light"))
							node3 = node2.getFirstChild();
						// Processing Stuff in Light
						while (node3 != null) {
							Node node4;
							int index = 0;

							if (node3.getNodeName().equals("Number")) {
								node4 = node3.getFirstChild();
								lightIndex = (new Integer(node4.getNodeValue())).
									intValue();

							}
							else if (node3.getNodeName().equals("lightSwitch")) {
								node4 = node3.getFirstChild();
								newScene.getLight(lightIndex).lightSwitch = (new
									Integer(node4.getNodeValue())).intValue();
							}
							else if (node3.getNodeName().equals("ambient")) {
								node4 = node3.getFirstChild();
								while (node4 != null) {
									if (!node4.getNodeName().equals("#text")) {
										Node node5;
										node5 = node4.getFirstChild();
										newScene.getLight(lightIndex).ambient[index] =
											(new Float(node5.getNodeValue())).
											floatValue();
										index++;
									}
									node4 = node4.getNextSibling();
								}
							}
							else if (node3.getNodeName().equals("diffuse")) {
								node4 = node3.getFirstChild();
								while (node4 != null) {
									if (!node4.getNodeName().equals("#text")) {
										Node node5;
										node5 = node4.getFirstChild();
										newScene.getLight(lightIndex).diffuse[index] =
											(new Float(node5.getNodeValue())).
											floatValue();
										index++;
									}
									node4 = node4.getNextSibling();
								}
							}
							else if (node3.getNodeName().equals("specular")) {
								node4 = node3.getFirstChild();
								while (node4 != null) {
									if (!node4.getNodeName().equals("#text")) {
										Node node5;
										node5 = node4.getFirstChild();
										newScene.getLight(lightIndex).specular[index] =
											(new Float(node5.getNodeValue())).floatValue();
										index++;
									}
									node4 = node4.getNextSibling();
								}
							}
							else if (node3.getNodeName().equals("position")) {
								node4 = node3.getFirstChild();
								while (node4 != null) {
									if (!node4.getNodeName().equals("#text")) {
										Node node5;
										node5 = node4.getFirstChild();
										newScene.getLight(lightIndex).position[index] =
											(new Float(node5.getNodeValue())).floatValue();
										index++;
									}
									node4 = node4.getNextSibling();
								}
							}
							else if (node3.getNodeName().equals("viewPos")) {
								node4 = node3.getFirstChild();
								while (node4 != null) {
									if (!node4.getNodeName().equals("#text")) {
										Node node5;
										node5 = node4.getFirstChild();
										newScene.getLight(lightIndex).viewPos[
											index] =
											(new Float(node5.getNodeValue())).
											floatValue();
										index++;
									}
									node4 = node4.getNextSibling();
								}
							}
							else if (node3.getNodeName().equals("direction")) {
								node4 = node3.getFirstChild();
								while (node4 != null) {
									if (!node4.getNodeName().equals("#text")) {
										Node node5;
										node5 = node4.getFirstChild();
										newScene.getLight(lightIndex).direction[
											index] =
											(new Float(node5.getNodeValue())).
											floatValue();
										index++;
									}
									node4 = node4.getNextSibling();
								}
							}
							else if (node3.getNodeName().equals("spotCutoff")) {
								node4 = node3.getFirstChild();
								newScene.getLight(lightIndex).spotCutoff = (new
									Float(node4.getNodeValue())).floatValue();
							}
							else if (node3.getNodeName().equals("spotExponent")) {
								node4 = node3.getFirstChild();
								newScene.getLight(lightIndex).spotExponent = (new
									Float(node4.getNodeValue())).floatValue();
							}
							else if (node3.getNodeName().equals("spotDirection")) {
								node4 = node3.getFirstChild();
								while (node4 != null) {
									if (!node4.getNodeName().equals("#text")) {
										Node node5;
										node5 = node4.getFirstChild();
										newScene.getLight(lightIndex).
											spotDirection[index] =
											(new Float(node5.getNodeValue())).
											floatValue();
										index++;
									}
									node4 = node4.getNextSibling();
								}
							}
							else if (node3.getNodeName().equals(
								"constAttenuation")) {
								node4 = node3.getFirstChild();
								newScene.getLight(lightIndex).constAttenuation = (new
									Float(node4.getNodeValue())).floatValue();
							}
							else if (node3.getNodeName().equals(
								"linearAttenuation")) {
								node4 = node3.getFirstChild();
								newScene.getLight(lightIndex).linearAttenuation = (new
									Float(node4.getNodeValue())).floatValue();
							}
							else if (node3.getNodeName().equals(
								"quadraticAttenuation")) {
								node4 = node3.getFirstChild();
								newScene.getLight(lightIndex).
									quadraticAttenuation = (new Float(node4.
									getNodeValue())).floatValue();
							}
							else if (node3.getNodeName().equals("location")) {
								node4 = node3.getFirstChild();
								newScene.getLight(lightIndex).spotExponent = (new
									Integer(node4.getNodeValue())).intValue();
							}
							node3 = node3.getNextSibling();
						}
						node2 = node2.getNextSibling();
					}
				}
			} // end if Lights
			node = node.getNextSibling();
		} // end Handle the Objects Under Scene
		return true;
	} //end method load



	/**
	 * This method saves the specified object to a file and all of its
	 * materials to a .mtl file.
	 
	public void saveObjectAndMaterials(PMesh mesh)
	{
		javax.swing.JFileChooser saveAs = new javax.swing.JFileChooser("Objects");
		saveAs.setToolTipText("Extension .obj and .mtl will automatically be added");//This is clunky. I realize.
		saveAs.showSaveDialog(cntrlPanel);
		File file = saveAs.getSelectedFile();
		if(file==null)
			return;
		String fileName = file.getPath();	//with full path
		String name=file.getName();			//without path

		mesh.save(fileName+".obj", name+".mtl"); // save the PMesh

		try
		{
			//start writing *.mtl
			BufferedWriter fout = new BufferedWriter(new FileWriter(fileName+".mtl"));
			MaterialCell mat[];
			mat=mesh.materials;
			for(int i=1; i<mat.length ; i++)//make i intialized to 1 if you want to avoid saving default material
			{
				fout.write("newmtl "+mat[i].materialName+"\n");
				fout.write("Ka "+mat[i].ka.toString()+"\n");
				fout.write("Kd "+mat[i].kd.toString()+"\n");
				fout.write("Ks "+mat[i].ks.toString()+"\n");
				if(!(mat[i].emmColor.r == 0.0 && mat[i].emmColor.g ==0.0 && mat[i].emmColor.b == 0.0))
					fout.write("e "+mat[i].emmColor.toString()+"\n");
				fout.write("Ns "+(1000*(mat[i].shiny/128))+"\n");
				fout.write("Ni "+mat[i].refractiveIndex+"\n");
				fout.write("Lc "+ mat[i].lineColor.toString());
				if(!(mat[i].transmissionFilter.r == 0.0 && mat[i].transmissionFilter.g ==0.0 && mat[i].transmissionFilter.b == 0.0))
					fout.write("Tf "+mat[i].transmissionFilter.toString()+"\n");
				if(!(mat[i].reflectivity.r == 0.0 && mat[i].reflectivity.g ==0.0 && mat[i].reflectivity.b == 0.0))
					fout.write("Ir "+mat[i].reflectivity.toString()+"\n");
				if(!(mat[i].refractivity.r == 0.0 && mat[i].refractivity.g ==0.0 && mat[i].refractivity.b == 0.0))
					fout.write("It "+mat[i].refractivity.toString()+"\n");
				
				fout.write("\n");
			}
			fout.close();
		}
		catch (IOException ex)
		{
			System.out.println("Could not open file for material output, operation terminated");
			ex.printStackTrace();
		}
	} // end method saveObjectAndMaterials
*/

} // end class Scene

class DtdOk {
	public boolean dtdOk = true; // assume ok
} // end class DtdOk

class DtdHandler implements ErrorHandler
{
	DtdOk check;

	public DtdHandler( DtdOk check){
		this.check = check;
	}
	public void error(SAXParseException exception) {
		check.dtdOk = false;
	}
	public void fatalError(SAXParseException exception) {
		check.dtdOk = false;
	}
	public void warning(SAXParseException exception){
		check.dtdOk = false;
	}
} // end class DtdHandler
