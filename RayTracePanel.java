import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
/**
 * This class provides a panel for manipulating lighting.
 *
 * @version 5-Feb-2005
 */
public class RayTracePanel extends JPanel
{
	public Scene theScene;
	public JPanel widgetPanel;
	public JCheckBox spheresOnly;
	public JCheckBox antiAliasing;
	public JLabel raysPerPixelLabel;
	public JSpinner raysPerPixel;
	public JCheckBox shadows;
	public JCheckBox reflections;
	public JCheckBox refractions;
	public JCheckBox checkerBackground;
	public java.text.DecimalFormat numFormat;
	public JLabel progress;
	public JButton rayTrace;
	JSpinner recursiveDepth;
	//public GridBagConstraints gbconstr;
	
	
	public RayTracePanel(Scene aSceneRef)
	{
		this.theScene = aSceneRef;
		this.setBorder(new TitledBorder("Ray Tracer Settings"));
		widgetPanel = new JPanel();
		//widgetPanel.setBorder(new TitledBorder("Ray Tracer Settings"));
		widgetPanel.setLayout(new GridLayout(3, 2));
		
		setLayout(new GridBagLayout());
		GridBagConstraints gBConstraints;
		numFormat = new java.text.DecimalFormat("#0.00");

		spheresOnly = new JCheckBox("Render Spheres Only", false);
		spheresOnly.setHorizontalAlignment(SwingConstants.LEADING);
		spheresOnly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (spheresOnly.isSelected()) {
					//set appropriate boolean in aSceneRef
					theScene.spheresOnly = (true);
				} // end if
				else {
					//set appropriate boolean in aSceneRef
					theScene.spheresOnly = (false);
				} // end else
				//theScene.refreshCanvas();
			} // end method mouseClicked
		});
		widgetPanel.add(spheresOnly);
		
		shadows = new JCheckBox("Shadows On", false);
		shadows.setHorizontalAlignment(SwingConstants.LEADING);
		shadows.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (shadows.isSelected()) {
					//set appropriate boolean in aSceneRef
					theScene.shadows = (true);
				} // end if
				else {
					//set appropriate boolean in aSceneRef
					theScene.shadows = (false);
				} // end else
				//theScene.refreshCanvas();
			} // end method mouseClicked
		});
		widgetPanel.add(shadows);		
		

		
		reflections = new JCheckBox("Reflections On", false);
		reflections.setHorizontalAlignment(SwingConstants.LEADING);
		reflections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (reflections.isSelected()) {
					//set appropriate boolean in aSceneRef
					theScene.reflections = (true);
				} // end if
				else {
					//set appropriate boolean in aSceneRef
					theScene.reflections = (false);
				} // end else
				//theScene.refreshCanvas();
			} // end method mouseClicked
		});
		widgetPanel.add(reflections);
		
		refractions = new JCheckBox("Refractions On", false);
		refractions.setHorizontalAlignment(SwingConstants.LEADING);
		refractions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (refractions.isSelected()) {
					//set appropriate boolean in aSceneRef
					theScene.refractions = (true);
				} // end if
				else {
					//set appropriate boolean in aSceneRef
					theScene.refractions = (false);
				} // end else
				//theScene.refreshCanvas();
			} // end method mouseClicked
		});
		widgetPanel.add(refractions);
		
		checkerBackground = new JCheckBox("Checkered Background On", false);
		checkerBackground.setHorizontalAlignment(SwingConstants.LEADING);
		checkerBackground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (checkerBackground.isSelected()) {
					//set appropriate boolean in aSceneRef
					theScene.checkerBackground = (true);
				} // end if
				else {
					//set appropriate boolean in aSceneRef
					theScene.checkerBackground = (false);
				} // end else
				//theScene.refreshCanvas();
			} // end method mouseClicked
		});
		widgetPanel.add(checkerBackground);
		
		gBConstraints = new GridBagConstraints();
		gBConstraints.weightx = 1.0;
		gBConstraints.weighty = 0.0;
		gBConstraints.gridwidth = 3;
//		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.anchor = GridBagConstraints.NORTH;
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 0;
		add(widgetPanel, gBConstraints);
		
		JPanel aaPanel = new JPanel();
		aaPanel.setBorder(new TitledBorder("Anti-Aliasing"));
		raysPerPixel = new JSpinner(new SpinnerNumberModel(1, 0,16,1));
		Dimension spinnerSize = raysPerPixel.getPreferredSize();
		spinnerSize.height += 15;
		spinnerSize.width +=10;
		raysPerPixel.setPreferredSize(spinnerSize);
		raysPerPixel.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				JSpinner theSpinner = (JSpinner)e.getSource();
				//Scene.something = theSpinner.getValue());
			}
		});
		
		antiAliasing = new JCheckBox("Anti-Aliasing On", theScene.antiAliasing);
		if(!theScene.antiAliasing)
			raysPerPixel.setEnabled(false);
		antiAliasing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (antiAliasing.isSelected()) {
					//set appropriate boolean in aSceneRef
					theScene.antiAliasing = (true);
					raysPerPixel.setEnabled(true);
				} // end if
				else {
					//set appropriate boolean in aSceneRef
					theScene.antiAliasing = (false);
					raysPerPixel.setEnabled(false);
				} // end else
				//theScene.refreshCanvas();
			} // end method mouseClicked
		});
		aaPanel.add(antiAliasing);
		raysPerPixelLabel = new JLabel("Rays per pixel:   ");
		aaPanel.add(raysPerPixelLabel);
		aaPanel.add(raysPerPixel);
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 1;
		gBConstraints.gridwidth = 1;
		gBConstraints.fill = GridBagConstraints.BOTH;
		//gBConstraints.anchor =GridBagConstraints.WEST;
		gBConstraints.insets = new Insets(10,0,10,0);

		add(aaPanel, gBConstraints);

/*		raysPerPixel = new JTextField("1");
		raysPerPixel.setColumns(5);
		theScene.raysPerPixel = (1);
		raysPerPixel.setEnabled(false);
		//raysPerPixel.setHorizontalAlignment(SwingConstants.RIGHT);
		raysPerPixel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					int raysPP = (raysPerPixel.getText() != null)? Integer.parseInt(raysPerPixel.getText()) : 0;
					// set appropriate variable in aSceneRef
					theScene.raysPerPixel = (raysPP);
				}
				catch(NumberFormatException e) {
					raysPerPixel.setText("1");
					// set appropriate variable in aSceneRef
					theScene.raysPerPixel = (1);
				}
			} 
		});
*/		
		
		JPanel recDepthPanel = new JPanel();
		recDepthPanel.setBorder(new TitledBorder("Recursive Depth"));	

		JLabel recursDepthLabel1 = new JLabel("Number of Ray Recursions allowed: ");
		recursiveDepth = new JSpinner(new SpinnerNumberModel(theScene.maxRecursiveDepth, 0,10,1));
		spinnerSize = recursiveDepth.getPreferredSize();
		spinnerSize.height += 15;
		spinnerSize.width += 10;
		recursiveDepth.setPreferredSize(spinnerSize);
//		JLabel recursDepthLabel2 = new JLabel("Depth of Ray Recursion: ");
		recDepthPanel.add(recursDepthLabel1);
		recDepthPanel.add(recursiveDepth);
		recursiveDepth.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e){
				JSpinner theSpinner = (JSpinner)e.getSource();
				theScene.maxRecursiveDepth =((Integer)theSpinner.getValue()).intValue();
				System.out.printf("maxRecursiveDepth: %d\n", theScene.maxRecursiveDepth);
			}
		});
		
		recDepthPanel.add(recursDepthLabel1);
		recDepthPanel.add(recursiveDepth);

		gBConstraints.gridx = 0;
		gBConstraints.gridy = 2;
		add(recDepthPanel,gBConstraints);
		//add(recursiveDepth,gBConstraints);
		//gBConstraints.fill = GridBagConstraints.NONE;
		//gBConstraints.gridx = 1;
		//gBConstraints.gridy = 2;
		
		
		
		rayTrace = new JButton("Ray Trace");
		//Font buttonFont = new Font(rayTrace.getFont().getName(),Font.BOLD,25);
		//rayTrace.setFont(buttonFont);
		
		//rayTrace.setPreferredSize(new Dimension(200,50));
		//rayTrace.setHorizontalAlignment(SwingConstants.LEADING);
		rayTrace.setVerticalTextPosition(AbstractButton.CENTER);
		rayTrace.setHorizontalTextPosition(AbstractButton.CENTER);
		rayTrace.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				System.out.println("Ray Tracing");
				theScene.rayTrace = true;
				//theScene.rayTraceSpheres = false;
				//******************************************************************************************
				//following for loop and variables are for debugging of the update function for the progress.
				/*
				long start,end;
				double incc=0,max=86;
				for(int i=0;i<=max;i++)
				{	
					updateProgress(incc,max);
					start = System.currentTimeMillis(); 
					do{
						end = System.currentTimeMillis();
					}while(end - start < 10);
					incc++;
					
				}
				*/
				//*******************************************************************************************
				/**/
				//theScene.spheresOnly = false;
				// run the RayTracer in a different thread so a progress bar can be updated
				//RayTracerThread thread = new RayTracerThread(theScene);
				//thread.start();
				//theScene.canvas.display();
				theScene.refreshCanvas();
			}
		});
//		gBConstraints = new GridBagConstraints();
		gBConstraints.weightx = 1.0;
		gBConstraints.weighty = 0.1;
		
//		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.anchor = GridBagConstraints.CENTER;
		gBConstraints.fill = GridBagConstraints.NONE;
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 3;
		add(rayTrace,gBConstraints);
		
		/*
		progress = new JLabel("Progress: 000.00000 %");
		progress.setFont(buttonFont);
		progress.setPreferredSize(new Dimension(300,100));
		gBConstraints = new GridBagConstraints();
		gBConstraints.weightx = 1.0;
		gBConstraints.weighty = 1.0;
//		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.anchor = GridBagConstraints.NORTH;
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 2;
		add(progress,gBConstraints);
		*/
		
	} // end constructor


	public void updateProgress(double currentValue, double maxValue)
	{
		double decimalPercent = currentValue/maxValue;
		double percentValue = 100*decimalPercent;
		
		String info = new String("Progress: " + String.format(" %3.5f ", percentValue) + " % ");
		progress.setText(info);
		this.update(getGraphics());
		
	}
	
} // end class RayTracePanel
