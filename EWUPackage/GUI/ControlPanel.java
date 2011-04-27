package EWUPackage.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import EWUPackage.scene.*;
import EWUPackage.panels.*;

/**
 * This class provides a control panel for working with objects, cameras,
 * lights, and fog.
 *
 * Make the panel dynamic.  Load all the Panels that are available.
 *
 * @version 25-Jan-2004
 * @version 27-Apr-2011
 * @author Joshua Olson
 */
public class ControlPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	public Scene theScene; // ref to the rendering object
	public JTabbedPane tabPane;
	public ArrayList<JPanel> panels;
	public ObjectPanel objectPanel;
	public CameraPanel cameraPanel;
	public LightPanel lightPanel;
	public FogPanel fogPanel;
	public RayTracePanel rayTracePanel;
	public PhotonMappingPanel rtp;
	
	
	
	public ControlPanel(Scene aSceneRef)
	{
		theScene = aSceneRef;

		setLayout(new BorderLayout());
		tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		
		objectPanel = new ObjectPanel(theScene, this);
		tabPane.addTab("Objects", objectPanel);
		
		rayTracePanel = new RayTracePanel(theScene);
		tabPane.addTab("RayTracing", rayTracePanel);
		
		rtp = new PhotonMappingPanel(theScene);
		tabPane.addTab("Photon Mapping", rtp);
		
		lightPanel = new LightPanel(theScene);
		tabPane.addTab("Lights", lightPanel);
		
		rtp = new PhotonMappingPanel(theScene);
		tabPane.addTab("Photon Mapping", rtp);
		
		cameraPanel = new CameraPanel(theScene);
		tabPane.addTab("Camera", cameraPanel);
		
		
		fogPanel = new FogPanel(theScene);
		tabPane.addTab("Fog", fogPanel);
		
		add(tabPane, BorderLayout.CENTER);
		
 	} // end constructor

} // end class ControlPanel
