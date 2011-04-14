/*
 *
 * CS570.java
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 *
 * @author  Bill Clark
 * This package version uses a GL2 framework, implements vertex arrays 
 * raytracer removed
 */
public class CS570 extends JFrame implements Runnable
{
	JPanel mainPanel;
	JPanel canvasPanel;
	ControlPanel cntrlPanel;
	GUIMenuBar menubar;
	GUIToolBar toolbar;
	public GUIStatusArea statusArea;
	public Scene theScene;

	public CS570()
	{
		theScene = new Scene(this, 800, 600); // Create the Scene
		//init();
	} // end constructor

	public void run()
	{
		Container contentPane = getContentPane();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(gridbag);

		// MenuBar
		menubar = new GUIMenuBar(this);
		this.setJMenuBar(menubar);

		// Toolbar
                // TO DO: add buttons to toolbar
		toolbar = new GUIToolBar(this);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		contentPane.add(toolbar, gbc);

		// Control Panel
		cntrlPanel = new ControlPanel(theScene);
		theScene.cntrlPanel = cntrlPanel;
		// Canvas Panel
		canvasPanel = new JPanel();
		canvasPanel.setBorder(BorderFactory.createEtchedBorder());
		canvasPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.gridx = 0; gbc1.gridy = 0;
		gbc1.gridwidth = GridBagConstraints.REMAINDER;
		gbc1.gridheight = GridBagConstraints.REMAINDER;
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.anchor = GridBagConstraints.NORTHWEST;
		gbc1.weightx = 1; gbc1.weighty = 1;
		canvasPanel.add(theScene.canvas, gbc1);
		// Main Panel - contains Control Panel and Canvas Panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.gridx = 0;
		gbc2.gridy = 0;
		gbc2.fill = GridBagConstraints.VERTICAL;
		gbc2.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(cntrlPanel, gbc2);
		gbc2.gridx = GridBagConstraints.RELATIVE;
		gbc2.gridwidth = GridBagConstraints.REMAINDER;
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.weightx = 1;
		gbc2.weighty = 1;
		mainPanel.add(canvasPanel, gbc2);
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		contentPane.add(mainPanel, gbc);

		// StatusArea
		statusArea = new GUIStatusArea(this.getWidth());
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0.0;
		contentPane.add(statusArea, gbc);

		setTitle("CS570");
		// setTheme(new GUITheme());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Setup Frame Size
		setExtendedState(JFrame.MAXIMIZED_BOTH); // maximize frame
		Dimension maxSize = getSize(); // get maximized dimensions
		System.out.println("Maximized Size: " + maxSize.width + " x " + maxSize.height);
		setBounds(0,0,maxSize.width-5,maxSize.height-5); // set for when not maximized (slightly smaller)
		// force the canvas to refresh to make sure everything draws right initially
		theScene.refreshCanvas();
		setVisible(true);

	} // end method init
	/**
	 * Override the addNotify() method to allow the frame to be sized appropriately
	 */
	public void addNotify()
	{
		super.addNotify();
		this.setSize(1024,768);
		setLocation(0,0);
		// maximize the window if supported
		if(Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
			setExtendedState(JFrame.MAXIMIZED_BOTH);
	}


	public GUIStatusArea getStatusArea() {
		return statusArea;
	} // end method getStatusArea

	/**
	 * This method performs all GUI component updates required when loading a new
	 * Scene.
	 */
	public void changeScenes()
	{
		cntrlPanel.objectPanel.changeScenes();
		cntrlPanel.lightPanel.changeScenes();
                //cntrlPanel.cameraPanel.changeScenes(); // Camera settings not currently loaded
		theScene.refreshCanvas();
	} // end method changeScenes

	public void setTheme(javax.swing.plaf.metal.MetalTheme theme)
	{
		javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme(theme);
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
		} // end try
		catch(Exception e) {
			System.out.println(e);
		} // end catch
	} // end method setTheme

	/**
	 * The main method.
	 *
	 * @param args String[]
	 */
	public static void main(String[] args) {
		// show splash screen and dispose automatically after specified number of milliseconds
		SplashThread st = new SplashThread(5000);
		st.start();
		changeDefaultFont();
		//CS471 app = new CS471();
		SwingUtilities.invokeLater(new CS570());
		//app.show();
	} // end method main

	/**
	 * This method changes default fonts for the GUI components.
	 */
	public static void changeDefaultFont()
	{
		Font defaultFont = new Font("Arial", Font.PLAIN, 12);
		Font boldFont = new Font("Arial", Font.BOLD, 13);
		UIManager.put("Button.font", defaultFont);
		UIManager.put("ToggleButton.font", defaultFont);
		UIManager.put("RadioButton.font", defaultFont);
		UIManager.put("CheckBox.font", defaultFont);
		UIManager.put("ColorChooser.font", defaultFont);
		UIManager.put("ComboBox.font", defaultFont);
		UIManager.put("Label.font", defaultFont);
		UIManager.put("List.font", defaultFont);
		UIManager.put("MenuBar.font", defaultFont);
		UIManager.put("MenuItem.font", defaultFont);
		UIManager.put("RadioButtonMenuItem.font", defaultFont);
		UIManager.put("CheckBoxMenuItem.font", defaultFont);
		UIManager.put("Menu.font", defaultFont);
		UIManager.put("PopupMenu.font", defaultFont);
		UIManager.put("OptionPane.font", defaultFont);
		UIManager.put("Panel.font", defaultFont);
		UIManager.put("ProgressBar.font", defaultFont);
		UIManager.put("ScrollPane.font", defaultFont);
		UIManager.put("Viewport.font", defaultFont);
		UIManager.put("TabbedPane.font", defaultFont);
		UIManager.put("Table.font", defaultFont);
		UIManager.put("TableHeader.font", defaultFont);
		UIManager.put("TextField.font", defaultFont);
		UIManager.put("PasswordField.font", defaultFont);
		UIManager.put("TextArea.font", defaultFont);
		UIManager.put("TextPane.font", defaultFont);
		UIManager.put("EditorPane.font", defaultFont);
		UIManager.put("TitledBorder.font", boldFont);
		UIManager.put("ToolBar.font", defaultFont);
		UIManager.put("ToolTip.font", defaultFont);
		UIManager.put("Tree.font", defaultFont);
	} // end method changeDefaultFont

} // end class CS471

/**
 * This class implements a thread for showing a splash screen for a
 * specified number of milliseconds and then disposing it.
 */
class SplashThread extends Thread {
	public long sleepMillis;
	public SplashThread(long sleepMillis) {
		this.sleepMillis = sleepMillis;
	} // end constructor

        /**
	 * This method shows a splash window and disposes it after a
	 * pre-determined number of milliseconds.
	 */
	public void run() {
		SplashWindow w = SplashWindow.splash(
			Toolkit.getDefaultToolkit().createImage("splash.jpg"),
			"", 12, 275, Color.WHITE);
		try {
			this.sleep(sleepMillis);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			w.dispose();
		} // end catch
		w.dispose();
	} // end method run
} // end class SplashThread
