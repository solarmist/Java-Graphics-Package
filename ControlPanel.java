import javax.swing.*;
import java.awt.*;

/**
 * This class provides a control panel for working with objects, cameras,
 * lights, and fog.
 *
 * @version 25-Jan-2004
 */
public class ControlPanel extends JPanel
{
	public Scene theScene; // ref to the rendering object
	public JTabbedPane tabPane;
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
		cameraPanel = new CameraPanel(theScene);
		tabPane.addTab("Camera", cameraPanel);
		lightPanel = new LightPanel(theScene);
		tabPane.addTab("Lights", lightPanel);
		fogPanel = new FogPanel(theScene);
		tabPane.addTab("Fog", fogPanel);
		rayTracePanel = new RayTracePanel(theScene);
		tabPane.addTab("RayTracing", rayTracePanel);
		rtp = new PhotonMappingPanel(theScene);
		tabPane.addTab("Photon Mapping", rtp);
		add(tabPane, BorderLayout.CENTER);
 	} // end constructor

} // end class ControlPanel
