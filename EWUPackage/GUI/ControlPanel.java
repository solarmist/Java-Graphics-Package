package EWUPackage.GUI;

import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.io.File;
import java.lang.reflect.*;

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
	public ArrayList<MasterPanel> panels;
	public ObjectPanel objectPanel;
	public CameraPanel cameraPanel;
	public LightPanel lightPanel;
	//public FogPanel fogPanel;
	//public RayTracePanel rayTracePanel;
	//public PhotonMappingPanel rtp;
	
	
	
	public ControlPanel(Scene aSceneRef)
	{
		theScene = aSceneRef;

		setLayout(new BorderLayout());
		tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

		try
		{
			//Get the path of the package relative to the project directory
			String packageName = "EWUPackage.panels";
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	        assert classLoader != null;
	        String path = "./" + packageName.replace('.', '/');

	        //Check all the files in the package directory for Panel.class files 
	        //If there are any add them to this panel as tabs
	        File dir = new File(path);
	        File[] files = dir.listFiles();
	        for(File curFile : files)
	        	
	        	if(curFile.getName().endsWith("Panel.class") && !curFile.getName().endsWith("MasterPanel.class"))
	        	{
	        		String curPanel = curFile.getName();
	        		int index = curPanel.indexOf('.');
	        		curPanel = curPanel.substring(0, index);

	        		@SuppressWarnings("unchecked")
					Class<MasterPanel> loadPanel = (Class<MasterPanel>) Class.forName(packageName + "." + curPanel);
					Constructor<MasterPanel> constructPanel = loadPanel.getConstructor(new Class[]{Scene.class});
					
					MasterPanel newPanel = constructPanel.newInstance(new Object[]{theScene});
					if (newPanel != null)
					{
						if(newPanel.name == "Camera")
							cameraPanel = (CameraPanel) newPanel;
						if(newPanel.name == "Objects")
							objectPanel = (ObjectPanel) newPanel;
						if(newPanel.name == "Lights")
							lightPanel = (LightPanel) newPanel;
						
						tabPane.addTab(newPanel.name, newPanel);
					}//*/
	        	}
	        
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		add(tabPane, BorderLayout.CENTER);
 	} // end constructor

} // end class ControlPanel
