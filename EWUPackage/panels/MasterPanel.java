package EWUPackage.panels;

import javax.swing.*;

import EWUPackage.scene.Scene;

public class MasterPanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	
	public Scene theScene;
	public String name;
	
	public MasterPanel(Scene aSceneRef)
	{
		theScene = aSceneRef;
		name = "MasterPanel";
	}
}
