import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author  Ryan Mauer
 * @version 23-Jan-2005
 */

public class GUIMenuBar extends JMenuBar
{
    public CS570 app;
    public Scene theScene;

    public JMenu file;
    public JMenuItem saveScene, loadScene, exitItem;

    public JMenu settings;
    public JMenuItem polygonMode;
    public JCheckBoxMenuItem filled, wireframe;
    public JMenuItem shadingMode;
    public JCheckBoxMenuItem gouraud, flat, none;
	public ButtonGroup shadingGroup;
    public JMenuItem polygonCulling;
    public JCheckBoxMenuItem cullFront, cullBack, cullDisable;
    public JMenuItem vertexWindingOrder;
    public JCheckBoxMenuItem windCW, windCCW;
    public JCheckBoxMenuItem drawAxis;

    public JMenu help;
    public JMenuItem about;

    public GUIMenuBar(CS570 app)
    {
        this.app = app;
        this.theScene = app.theScene;
        init();

    } // end constructor

    public void init()
    {
        file = new JMenu("File");
		settings = new JMenu("Settings");
		help = new JMenu("Help");

		buildFileMenu();
		buildSettingsMenu();
		buildHelpMenu();

        this.add(file);
        this.add(settings);
		this.add(help);

    } // end method init

	public void buildFileMenu()
	{
		loadScene = new JMenuItem("Load Scene...");
		loadScene.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser open = new JFileChooser("Scenes");
				open.showOpenDialog(app);
				java.io.File file = open.getSelectedFile();
				if(file != null) {
					String fileName = file.getPath();
					boolean loaded = Scene.load(fileName, theScene);
				if(loaded)
					app.changeScenes();
				else
					app.statusArea.showStatus("Load Scene Failed...");
				} // end if
			} // end method actionPerformed
		}); // end new ActionListener

		saveScene = new JMenuItem("Save Scene...");
		saveScene.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser saveAs = new JFileChooser("Scenes");
				saveAs.showSaveDialog(app);
				java.io.File file = saveAs.getSelectedFile();
				if(file != null) {
					String fileName = file.getPath();
					boolean saved = app.theScene.save(fileName);
					if (saved)
						app.getStatusArea().showStatus("Scene Saved Successfully");
					else
						app.getStatusArea().showStatus("Scene Saved Failed");
				} // end if
			} // end method actionPerformed
		}); // end new ActionListener

		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			} // end method actionPerformed
		});

		file.add(loadScene);
		file.add(saveScene);
		file.addSeparator();
		file.add(exitItem);

	} // end method buildFileMenu

	public void buildSettingsMenu()
	{
		polygonMode = new JMenu("Polygon Mode");
		filled = new JCheckBoxMenuItem("Filled");
		filled.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				wireframe.setSelected(false);
				theScene.fillMode = (true);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		filled.setSelected(theScene.fillMode);
		wireframe = new JCheckBoxMenuItem("Wireframe");
		wireframe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				filled.setSelected(false);
				theScene.fillMode = (false);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		wireframe.setSelected(!theScene.fillMode);
		polygonMode.add(filled);
		polygonMode.add(wireframe);
		settings.add(polygonMode);

		shadingMode = new JMenu("Shading Mode");
		shadingGroup = new ButtonGroup();
		gouraud = new JCheckBoxMenuItem("Gouraud");
		gouraud.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				theScene.gouraudShading = (true);
				theScene.lightingEnabled = (true);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		shadingGroup.add(gouraud);
		flat = new JCheckBoxMenuItem("Flat");
		flat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				theScene.gouraudShading = (false);
				theScene.lightingEnabled = (true);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		shadingGroup.add(flat);
		none = new JCheckBoxMenuItem("None");
		none.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				theScene.lightingEnabled = (false);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		if(!theScene.lightingEnabled)
			none.setSelected(true);
		else
		//none.setSelected(!theScene.getLightingEnabled());
			gouraud.setSelected(theScene.gouraudShading);
		//flat.setSelected(!theScene.getGouraudShading());
		shadingGroup.add(none);
		shadingMode.add(gouraud);
		shadingMode.add(flat);
		shadingMode.add(none);
		settings.add(shadingMode);

		polygonCulling = new JMenu("Polygon Culling");
		cullFront = new JCheckBoxMenuItem("Front Face Culling");
		cullFront.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cullBack.setSelected(false);
				cullDisable.setSelected(false);
				theScene.cull = (true);
				theScene.cullBack = (false);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		cullFront.setSelected(theScene.cull && !theScene.cullBack);
		cullBack = new JCheckBoxMenuItem("Back Face Culling");
		cullBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cullFront.setSelected(false);
				cullDisable.setSelected(false);
				theScene.cull = (true);
				theScene.cullBack = (true);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		cullBack.setSelected(theScene.cull && theScene.cullBack);
		cullDisable = new JCheckBoxMenuItem("Disable Culling");
		cullDisable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cullFront.setSelected(false);
				cullBack.setSelected(false);
				theScene.cull = (false);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		cullDisable.setSelected(!theScene.cull);
		polygonCulling.add(cullFront);
		polygonCulling.add(cullBack);
		polygonCulling.add(cullDisable);
		settings.add(polygonCulling);
		vertexWindingOrder = new JMenu("Vertex Winding Order");
		windCCW = new JCheckBoxMenuItem("Counter-clockwise");
		windCCW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				windCW.setSelected(false);
				theScene.windCCW = (true);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		windCCW.setSelected(theScene.windCCW);
		windCW = new JCheckBoxMenuItem("Clockwise");
		windCW.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				windCCW.setSelected(false);
				theScene.windCCW = (false);
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		windCW.setSelected(!theScene.windCCW);
		vertexWindingOrder.add(windCW);
		vertexWindingOrder.add(windCCW);
		settings.add(vertexWindingOrder);

		drawAxis = new JCheckBoxMenuItem("Draw Axis");
		drawAxis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				theScene.drawAxis = ( drawAxis.isSelected() );
				theScene.refreshCanvas();
			} // end method actionPerformed
		}); // end new ActionListener
		drawAxis.setSelected(theScene.drawAxis);
        settings.add(drawAxis);

	} // end method buildSettingsMenu

	public void buildHelpMenu()
	{
		about = new JMenuItem("About...");

                about.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        AboutDialog d = new AboutDialog(app);
                        d.show();
                    } // end method actionPerformed
                });

		help.add(about);

	} // end method buildHelpMenu

} // end class GUIMenuBar
