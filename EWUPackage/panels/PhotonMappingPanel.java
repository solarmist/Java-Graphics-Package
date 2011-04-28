package EWUPackage.panels;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import EWUPackage.scene.*;

/**
 * @author	Ryan Mauer
 * @version 27-Feb-2004
 */
public class PhotonMappingPanel extends MasterPanel
{
	public static final long serialVersionUID = 1L;

	public JPanel rayPanel;
	public JLabel rayDepthLabel, raySamplesLabel;
	public JTextField rayDepthEdit, raySamplesEdit;
	public JButton rayTraceButton, rayTraceSpheresButton, setRayValuesButton;

	public JPanel photonPanel;
	public JLabel photonDepthLabel, photonSamplesLabel, photonNumLabel, maxDirectLabel, maxIndirectLabel, maxCausticLabel;
	public JTextField photonDepthEdit, photonSamplesEdit, photonNumEdit, maxDirectEdit, maxIndirectEdit, maxCausticEdit;
	public JButton photonMappingButton, photonCastButton, setPhotonValuesButton;
	public JCheckBox photonType, excludeDirect, useSavedMaps;
	public JLabel expPrimaryLabel, expDiffuseLabel, expShadowLabel, expSpecRefrLabel;
	public JLabel directRadiusLabel, directGatherNumLabel, indirectRadiusLabel, indirectGatherNumLabel, causticRadiusLabel, causticGatherNumLabel;
	public JTextField expPrimaryEdit, expDiffuseEdit, expShadowEdit, expSpecRefrEdit;
	public JTextField directRadiusEdit, directGatherNumEdit, indirectRadiusEdit, indirectGatherNumEdit, causticRadiusEdit, causticGatherNumEdit;
	
	
	
	public JButton saveImageButton;

	public static JProgressBar progress;
	public static JLabel status;

	public PhotonMappingPanel(Scene aSceneRef)
	{
		super(aSceneRef);
		name = "Photon Mapping";

		this.setLayout(new GridBagLayout());
		GridBagConstraints gBConstraints = new GridBagConstraints();
/*
		rayPanel = new JPanel();
		rayPanel.setLayout(new GridBagLayout());
		rayPanel.setBorder(new TitledBorder("Ray Tracing"));
		rayPanel.setMinimumSize(new Dimension(220, 180));
		rayPanel.setPreferredSize(new Dimension(240, 180));
		GridBagConstraints rayConstraints = new GridBagConstraints();

		rayTraceButton = new JButton("Ray Trace");
		rayTraceButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				System.out.println("Ray Tracing");
				theScene.rayTracing = true;
				theScene.rayTraceSpheres = false;

				theScene.spheresOnly = false;
				// run the RayTracer in a different thread so a progress bar can be updated
				//RayTracerThread thread = new RayTracerThread(theScene);
				//thread.start();
				//theScene.canvas.display();
				theScene.setRayTraceIsOn(true);
				theScene.refreshCanvas();
			}
		});
		rayConstraints.gridx = 0;
		rayConstraints.gridy = 0;
		rayConstraints.gridwidth = 2;
		rayConstraints.fill = GridBagConstraints.HORIZONTAL;
		rayConstraints.weightx = 1.0;
		rayPanel.add(rayTraceButton, rayConstraints);

		JButton rayTraceSphereButton = new JButton("Ray Trace - Sphere");
		rayTraceSphereButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				System.out.println("Ray Tracing Spheres");
				theScene.rayTracing = true;
				theScene.rayTraceSpheres = true;
				theScene.spheresOnly = true;
				theScene.setRayTraceIsOn(true);
				theScene.refreshCanvas();
				// run the RayTracer in a different thread so a progress bar can be updated
				//RayTracerThread thread = new RayTracerThread(theScene);
				//thread.start();
				//theScene.canvas.display();
			}
		});
		rayConstraints.gridx = 0;
		rayConstraints.gridy = 1;
		rayConstraints.gridwidth = 2;
		rayConstraints.insets = new Insets(0,0,5,0); //(0, 0, 15, 0);
		rayPanel.add(rayTraceSphereButton, rayConstraints);

		rayDepthLabel = new JLabel("Recursive Depth: ");
		rayDepthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		rayConstraints = new GridBagConstraints();
		rayConstraints.gridx = 0;
		rayConstraints.gridy = 2;
		rayConstraints.gridwidth = 1;
		rayConstraints.anchor = GridBagConstraints.EAST;
		rayConstraints.insets = new Insets(0,0,0,0); //(0, 10, 0, 0);
		rayPanel.add(rayDepthLabel, rayConstraints);

		raySamplesLabel = new JLabel("Supersampling: ");
		raySamplesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		rayConstraints = new GridBagConstraints();
		rayConstraints.gridx = 0;
		rayConstraints.gridy = 3;
		rayConstraints.insets = new Insets(0,0,0,0);
		rayConstraints.anchor = GridBagConstraints.EAST;
		rayPanel.add(raySamplesLabel, rayConstraints);

		rayDepthEdit = new JTextField();
		rayDepthEdit.setHorizontalAlignment(JTextField.RIGHT);
		rayDepthEdit.setText("1");
		rayDepthEdit.setMinimumSize(new Dimension(60, 20));
		rayDepthEdit.setPreferredSize(new Dimension(60, 20));
		rayConstraints = new GridBagConstraints();
		rayConstraints.gridx = 1;
		rayConstraints.gridy = 2;
		rayPanel.add(rayDepthEdit, rayConstraints);

		raySamplesEdit = new JTextField();
		raySamplesEdit.setHorizontalAlignment(JTextField.RIGHT);
		raySamplesEdit.setText("1");
		raySamplesEdit.setMinimumSize(new Dimension(60, 20));
		raySamplesEdit.setPreferredSize(new Dimension(60, 20));
		rayConstraints = new GridBagConstraints();
		rayConstraints.gridx = 1;
		rayConstraints.gridy = 3;
		rayPanel.add(raySamplesEdit, rayConstraints);

		setRayValuesButton = new JButton();
		setRayValuesButton.setText("Set Values");
		setRayValuesButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				try
				{
					int depth   = (rayDepthEdit.getText() != null) ? Integer.parseInt(rayDepthEdit.getText()) : 5;
					int samples = (raySamplesEdit.getText() != null) ? Integer.parseInt(raySamplesEdit.getText()) : 1;

					if(samples % 4 != 0) {
						samples = (samples / 4) * 4; // ensure a multiple of 4 using integer division
						if(samples == 0) samples = 1;
					} // end if
					theScene.RAYTRACE_MAX_RECURSIVE_DEPTH = depth;
					theScene.RAYTRACE_NUM_SAMPLES = samples;
					rayDepthEdit.setText(Integer.toString(theScene.RAYTRACE_MAX_RECURSIVE_DEPTH));
					raySamplesEdit.setText(Integer.toString(theScene.RAYTRACE_NUM_SAMPLES));
					System.out.println("Ray Tracing Settings\n--------------------------");
					System.out.println("Max Recursive Depth: " + theScene.RAYTRACE_MAX_RECURSIVE_DEPTH);
					System.out.println("Supersampling Number Samples Per Pixel: " + theScene.RAYTRACE_NUM_SAMPLES);
				} // end try
				catch(NumberFormatException e)
				{
					theScene.RAYTRACE_MAX_RECURSIVE_DEPTH = 5;
					theScene.RAYTRACE_NUM_SAMPLES = 1;
					rayDepthEdit.setText(Integer.toString(theScene.RAYTRACE_MAX_RECURSIVE_DEPTH));
					raySamplesEdit.setText(Integer.toString(theScene.RAYTRACE_NUM_SAMPLES));
				} // end catch
			}
		});
		rayConstraints = new GridBagConstraints();
		rayConstraints.gridx = 0;
		rayConstraints.gridy = 4;
		rayConstraints.fill = GridBagConstraints.HORIZONTAL;
		rayConstraints.gridwidth = 2;
		rayConstraints.gridheight = 1;
		rayConstraints.insets = new Insets(5, 0, 5, 0);
		rayPanel.add(setRayValuesButton, rayConstraints);

		gBConstraints.gridx = 0;
		gBConstraints.gridy = 0;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.ipady = 0;
		gBConstraints.anchor = GridBagConstraints.NORTH;
		gBConstraints.weightx = 1.0;

		add(rayPanel, gBConstraints);
*/
		// Photon Panel

		photonPanel = new JPanel();
		photonPanel.setLayout(new GridBagLayout());
		photonPanel.setBorder(new TitledBorder("Photon Mapping"));
		GridBagConstraints photonConstraints = new GridBagConstraints();

		photonMappingButton = new JButton("Photon Mapping");
		photonMappingButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				System.out.println("Photon Mapping");
				theScene.photonMapping = true;
				for(int i=0;i<=100;i++)
				{
					updateProgress(i, 100);
				}
				theScene.refreshCanvas();
			}
		});
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 0;
		photonConstraints.gridwidth = 2;
		photonConstraints.fill = GridBagConstraints.HORIZONTAL;
		photonConstraints.weightx = 1.0;
		photonPanel.add(photonMappingButton, photonConstraints);

		JButton photonCastButton = new JButton("Photon Casting Only");
		photonCastButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				System.out.println("Photon Casting Only");
				theScene.photonCastingOnly = true;
				// run the RayTracer in a different thread so a progress bar can be updated
				//RayTracerThread thread = new RayTracerThread(theScene);
				//thread.start();
			}
		});
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 1;
		photonConstraints.gridwidth = 2;
		photonConstraints.insets = new Insets(0,0,0,0); //(0, 0, 15, 0);
		photonPanel.add(photonCastButton, photonConstraints);

		photonDepthLabel = new JLabel("Recursive Depth: ");
		photonDepthLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 2;
		photonConstraints.gridwidth = 1;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonConstraints.insets = new Insets(0,0,0,0); //(0, 10, 0, 0);
		photonPanel.add(photonDepthLabel, photonConstraints);

		photonSamplesLabel = new JLabel("Supersampling: ");
		photonSamplesLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 3;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(photonSamplesLabel, photonConstraints);

		photonNumLabel = new JLabel("Photons Per Light: ");
		photonNumLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 4;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(photonNumLabel, photonConstraints);

		maxDirectLabel = new JLabel("Max Direct Photons: ");
		maxDirectLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 5;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(maxDirectLabel, photonConstraints);

		maxIndirectLabel = new JLabel("Max Indirect Photons: ");
		maxIndirectLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 6;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(maxIndirectLabel, photonConstraints);

		maxCausticLabel = new JLabel("Max Caustic Photons: ");
		maxCausticLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 7;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(maxCausticLabel, photonConstraints);

		expPrimaryLabel = new JLabel("Primary Exposure: ");
		expPrimaryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 8;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(expPrimaryLabel, photonConstraints);

		expDiffuseLabel = new JLabel("Diffuse Exposure: ");
		expDiffuseLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 9;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(expDiffuseLabel, photonConstraints);

		expShadowLabel = new JLabel("Shadow Exposure: ");
		expShadowLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 10;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(expShadowLabel, photonConstraints);

		expSpecRefrLabel = new JLabel("Caustic Exposure: ");
		expSpecRefrLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 11;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(expSpecRefrLabel, photonConstraints);

		directRadiusLabel = new JLabel("Direct Search Radius: ");
		directRadiusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 12;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(directRadiusLabel, photonConstraints);

		directGatherNumLabel = new JLabel("Direct Gather # Photons: ");
		directGatherNumLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 13;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(directGatherNumLabel, photonConstraints);

		indirectRadiusLabel = new JLabel("Indirect Search Radius: ");
		indirectRadiusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 14;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(indirectRadiusLabel, photonConstraints);

		indirectGatherNumLabel = new JLabel("Indirect Gather # Photons: ");
		indirectGatherNumLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 15;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(indirectGatherNumLabel, photonConstraints);

		causticRadiusLabel = new JLabel("Caustic Search Radius: ");
		causticRadiusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 16;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(causticRadiusLabel, photonConstraints);

		causticGatherNumLabel = new JLabel("Caustic Gather # Photons: ");
		causticGatherNumLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 17;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonPanel.add(causticGatherNumLabel, photonConstraints);

		photonDepthEdit = new JTextField();
		photonDepthEdit.setHorizontalAlignment(JTextField.RIGHT);
		photonDepthEdit.setText("5");
		photonDepthEdit.setMinimumSize(new Dimension(60, 20));
		photonDepthEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 2;
		photonPanel.add(photonDepthEdit, photonConstraints);

		photonSamplesEdit = new JTextField();
		photonSamplesEdit.setHorizontalAlignment(JTextField.RIGHT);
		photonSamplesEdit.setText("1");
		photonSamplesEdit.setMinimumSize(new Dimension(60, 20));
		photonSamplesEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 3;
		photonPanel.add(photonSamplesEdit, photonConstraints);

		photonNumEdit = new JTextField();
		photonNumEdit.setHorizontalAlignment(JTextField.RIGHT);
		photonNumEdit.setText("1000");
		photonNumEdit.setMinimumSize(new Dimension(60, 20));
		photonNumEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 4;
		photonPanel.add(photonNumEdit, photonConstraints);

		maxDirectEdit = new JTextField();
		maxDirectEdit.setHorizontalAlignment(JTextField.RIGHT);
		maxDirectEdit.setText("1000");
		maxDirectEdit.setMinimumSize(new Dimension(60, 20));
		maxDirectEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 5;
		photonPanel.add(maxDirectEdit, photonConstraints);

		maxIndirectEdit = new JTextField();
		maxIndirectEdit.setHorizontalAlignment(JTextField.RIGHT);
		maxIndirectEdit.setText("1000");
		maxIndirectEdit.setMinimumSize(new Dimension(60, 20));
		maxIndirectEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 6;
		photonPanel.add(maxIndirectEdit, photonConstraints);

		maxCausticEdit = new JTextField();
		maxCausticEdit.setHorizontalAlignment(JTextField.RIGHT);
		maxCausticEdit.setText("1000");
		maxCausticEdit.setMinimumSize(new Dimension(60, 20));
		maxCausticEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 7;
		photonPanel.add(maxCausticEdit, photonConstraints);

		expPrimaryEdit = new JTextField();
		expPrimaryEdit.setHorizontalAlignment(JTextField.RIGHT);
		expPrimaryEdit.setText("1.0");
		expPrimaryEdit.setMinimumSize(new Dimension(60, 20));
		expPrimaryEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 8;
		photonPanel.add(expPrimaryEdit, photonConstraints);

		expDiffuseEdit = new JTextField();
		expDiffuseEdit.setHorizontalAlignment(JTextField.RIGHT);
		expDiffuseEdit.setText("1.0");
		expDiffuseEdit.setMinimumSize(new Dimension(60, 20));
		expDiffuseEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 9;
		photonPanel.add(expDiffuseEdit, photonConstraints);

		expShadowEdit = new JTextField();
		expShadowEdit.setHorizontalAlignment(JTextField.RIGHT);
		expShadowEdit.setText("1.0");
		expShadowEdit.setMinimumSize(new Dimension(60, 20));
		expShadowEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 10;
		photonPanel.add(expShadowEdit, photonConstraints);

		expSpecRefrEdit = new JTextField();
		expSpecRefrEdit.setHorizontalAlignment(JTextField.RIGHT);
		expSpecRefrEdit.setText("1.0");
		expSpecRefrEdit.setMinimumSize(new Dimension(60, 20));
		expSpecRefrEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 11;
		photonPanel.add(expSpecRefrEdit, photonConstraints);

		// Radius / Gather num for direct lighting
		directRadiusEdit = new JTextField();
		directRadiusEdit.setHorizontalAlignment(JTextField.RIGHT);
		directRadiusEdit.setText("1.0");
		directRadiusEdit.setMinimumSize(new Dimension(60, 20));
		directRadiusEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 12;
		photonPanel.add(directRadiusEdit, photonConstraints);

		directGatherNumEdit = new JTextField();
		directGatherNumEdit.setHorizontalAlignment(JTextField.RIGHT);
		directGatherNumEdit.setText("100");
		directGatherNumEdit.setMinimumSize(new Dimension(60, 20));
		directGatherNumEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 13;
		photonPanel.add(directGatherNumEdit, photonConstraints);

		// Radius / Gather num for indirect lighting
		indirectRadiusEdit = new JTextField();
		indirectRadiusEdit.setHorizontalAlignment(JTextField.RIGHT);
		indirectRadiusEdit.setText("1.0");
		indirectRadiusEdit.setMinimumSize(new Dimension(60, 20));
		indirectRadiusEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 14;
		photonPanel.add(indirectRadiusEdit, photonConstraints);

		indirectGatherNumEdit = new JTextField();
		indirectGatherNumEdit.setHorizontalAlignment(JTextField.RIGHT);
		indirectGatherNumEdit.setText("100");
		indirectGatherNumEdit.setMinimumSize(new Dimension(60, 20));
		indirectGatherNumEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 15;
		photonPanel.add(indirectGatherNumEdit, photonConstraints);

		// Radius / Gather num for caustics
		causticRadiusEdit = new JTextField();
		causticRadiusEdit.setHorizontalAlignment(JTextField.RIGHT);
		causticRadiusEdit.setText("1.0");
		causticRadiusEdit.setMinimumSize(new Dimension(60, 20));
		causticRadiusEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 16;
		photonPanel.add(causticRadiusEdit, photonConstraints);

		causticGatherNumEdit = new JTextField();
		causticGatherNumEdit.setHorizontalAlignment(JTextField.RIGHT);
		causticGatherNumEdit.setText("100");
		causticGatherNumEdit.setMinimumSize(new Dimension(60, 20));
		causticGatherNumEdit.setPreferredSize(new Dimension(60, 20));
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 1;
		photonConstraints.gridy = 17;
		photonPanel.add(causticGatherNumEdit, photonConstraints);

		photonType = new JCheckBox("Draw Photon Type", false);
		photonType.setHorizontalAlignment(SwingConstants.RIGHT);
		photonType.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				theScene.drawPhotonType = photonType.isSelected();
			} // end method actionPerformed
		});
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 18;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonConstraints.insets = new Insets(0, 0, 0, 0);
		photonPanel.add(photonType, photonConstraints);

		excludeDirect = new JCheckBox("Exclude Direct Photons", false);
		excludeDirect.setHorizontalAlignment(SwingConstants.RIGHT);
		excludeDirect.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				theScene.excludeDirectPhotons = excludeDirect.isSelected();
			} // end method actionPerformed
		});
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 19;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonConstraints.insets = new Insets(0, 0, 0, 0);
		photonPanel.add(excludeDirect, photonConstraints);

		useSavedMaps = new JCheckBox("Use Previous Photon Maps", false);
		useSavedMaps.setHorizontalAlignment(SwingConstants.RIGHT);
		useSavedMaps.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				theScene.useSavedPhotonMaps = useSavedMaps.isSelected();
			} // end method actionPerformed
		});
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 20;
		photonConstraints.anchor = GridBagConstraints.EAST;
		photonConstraints.insets = new Insets(0, 0, 0, 0);
		photonPanel.add(useSavedMaps, photonConstraints);


		setPhotonValuesButton = new JButton();
		setPhotonValuesButton.setText("Set Values");
		setPhotonValuesButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				try
				{
					int depth   = (photonDepthEdit.getText() != null) ? Integer.parseInt(photonDepthEdit.getText()) : 5;
					int samples = (photonSamplesEdit.getText() != null) ? Integer.parseInt(photonSamplesEdit.getText()) : 1;
					int num		= (photonNumEdit.getText() != null) ? Integer.parseInt(photonNumEdit.getText()) : 10000;
					int gatherD = (directGatherNumEdit.getText() != null) ? Integer.parseInt(directGatherNumEdit.getText()) : 100;
					int gatherI = (indirectGatherNumEdit.getText() != null) ? Integer.parseInt(indirectGatherNumEdit.getText()) : 100;
					int gatherC = (causticGatherNumEdit.getText() != null) ? Integer.parseInt(causticGatherNumEdit.getText()) : 100;

					if(samples % 4 != 0) {
						samples = (samples / 4) * 4; // ensure a multiple of 4 using integer division
						if(samples == 0) samples = 1;
					} // end if
					theScene.PHOTON_MAX_RECURSIVE_DEPTH = depth;
					theScene.PHOTON_NUM_SAMPLES = samples;
					theScene.PHOTON_NUM_PER_LIGHT = num;
					theScene.PHOTON_NUM_GATHER_DIRECT = gatherD;
					theScene.PHOTON_NUM_GATHER_INDIRECT = gatherI;
					theScene.PHOTON_NUM_GATHER_CAUSTIC = gatherC;

					theScene.exposure[Photon.PRIMARY_PHOTON] = (expPrimaryEdit.getText() != null) ? Float.parseFloat(expPrimaryEdit.getText()) : 1.0f;
					theScene.exposure[Photon.DIFFUSE_REFLECT_PHOTON] = (expDiffuseEdit.getText() != null) ? Float.parseFloat(expDiffuseEdit.getText()) : 1.0f;
					theScene.exposure[Photon.SHADOW_PHOTON] = (expShadowEdit.getText() != null) ? Float.parseFloat(expShadowEdit.getText()) : 1.0f;
					theScene.exposure[Photon.CAUSTIC_PHOTON] = (expSpecRefrEdit.getText() != null) ? Float.parseFloat(expSpecRefrEdit.getText()) : 1.0f;
					theScene.PHOTON_RADIUS_DIRECT = (directRadiusEdit.getText() != null) ? Float.parseFloat(directRadiusEdit.getText()) : 1.0f;
					theScene.PHOTON_RADIUS_INDIRECT = (indirectRadiusEdit.getText() != null) ? Float.parseFloat(indirectRadiusEdit.getText()) : 1.0f;
					theScene.PHOTON_RADIUS_CAUSTIC = (causticRadiusEdit.getText() != null) ? Float.parseFloat(causticRadiusEdit.getText()) : 1.0f;
					//theScene.photonSearchRadius = (radiusEdit.getText() != null) ? Float.parseFloat(radiusEdit.getText()) : 1.0f;
					theScene.PHOTON_MAX_DIRECT = (maxDirectEdit.getText() != null) ? Integer.parseInt(maxDirectEdit.getText()) : 10000;
					theScene.PHOTON_MAX_INDIRECT = (maxIndirectEdit.getText() != null) ? Integer.parseInt(maxIndirectEdit.getText()) : 10000;
					theScene.PHOTON_MAX_CAUSTIC = (maxCausticEdit.getText() != null) ? Integer.parseInt(maxCausticEdit.getText()) : 10000;

					System.out.println("Photon Mapping Settings\n--------------------------");
					System.out.println("Max Recursive Depth: " + theScene.PHOTON_MAX_RECURSIVE_DEPTH);
					System.out.println("Supersampling Number Samples Per Pixel: " + theScene.PHOTON_NUM_SAMPLES);
					System.out.println("Photons Per Light: " + theScene.PHOTON_NUM_PER_LIGHT);
					System.out.println("Primary Exposure: " + theScene.exposure[Photon.PRIMARY_PHOTON]);
					System.out.println("Diffuse Exposure: " + theScene.exposure[Photon.DIFFUSE_REFLECT_PHOTON]);
					System.out.println("Shadow Exposure: " + theScene.exposure[Photon.SHADOW_PHOTON]);
					System.out.println("Specular Refract Exposure: " + theScene.exposure[Photon.CAUSTIC_PHOTON]);
					System.out.println("Direct Photon Gathering Number: " + theScene.PHOTON_NUM_GATHER_DIRECT);
					System.out.println("Indirect Photon Gathering Number: " + theScene.PHOTON_NUM_GATHER_INDIRECT);
					System.out.println("Caustic Photon Gathering Number: " + theScene.PHOTON_NUM_GATHER_CAUSTIC);
					System.out.println("Direct Search Radius: " + theScene.PHOTON_RADIUS_DIRECT);
					System.out.println("Indirect Search Radius: " + theScene.PHOTON_RADIUS_INDIRECT);
					System.out.println("Caustic Search Radius: " + theScene.PHOTON_RADIUS_CAUSTIC);

					photonDepthEdit.setText(Integer.toString(theScene.PHOTON_MAX_RECURSIVE_DEPTH));
					photonSamplesEdit.setText(Integer.toString(theScene.PHOTON_NUM_SAMPLES));
					photonNumEdit.setText(Integer.toString(theScene.PHOTON_NUM_PER_LIGHT));
					expPrimaryEdit.setText(Float.toString(theScene.exposure[Photon.PRIMARY_PHOTON] ));
					expDiffuseEdit.setText(Float.toString(theScene.exposure[Photon.DIFFUSE_REFLECT_PHOTON] ));
					expShadowEdit.setText(Float.toString(theScene.exposure[Photon.SHADOW_PHOTON] ));
					expSpecRefrEdit.setText(Float.toString(theScene.exposure[Photon.CAUSTIC_PHOTON] ));
					//radiusEdit.setText(Float.toString(theScene.photonSearchRadius ));
					directRadiusEdit.setText(Float.toString(theScene.PHOTON_RADIUS_DIRECT ));
					indirectRadiusEdit.setText(Float.toString(theScene.PHOTON_RADIUS_INDIRECT ));
					causticRadiusEdit.setText(Float.toString(theScene.PHOTON_RADIUS_CAUSTIC ));
					directGatherNumEdit.setText(Integer.toString(theScene.PHOTON_NUM_GATHER_DIRECT));
					indirectGatherNumEdit.setText(Integer.toString(theScene.PHOTON_NUM_GATHER_INDIRECT));
					causticGatherNumEdit.setText(Integer.toString(theScene.PHOTON_NUM_GATHER_CAUSTIC));

				} // end try
				catch(NumberFormatException e)
				{
					theScene.PHOTON_MAX_RECURSIVE_DEPTH = 5;
					theScene.PHOTON_NUM_SAMPLES = 1;
					theScene.PHOTON_NUM_PER_LIGHT = 10000;
					photonDepthEdit.setText(Integer.toString(theScene.PHOTON_MAX_RECURSIVE_DEPTH));
					photonSamplesEdit.setText(Integer.toString(theScene.PHOTON_NUM_SAMPLES));
					photonNumEdit.setText(Integer.toString(theScene.PHOTON_NUM_PER_LIGHT));
				} // end catch
			}
		});
		photonConstraints = new GridBagConstraints();
		photonConstraints.gridx = 0;
		photonConstraints.gridy = 21;
		photonConstraints.fill = GridBagConstraints.HORIZONTAL;
		photonConstraints.gridwidth = 2;
		photonConstraints.gridheight = 1;
		photonConstraints.insets = new Insets(0,0,0,0); //(15, 0, 15, 0);
		photonPanel.add(setPhotonValuesButton, photonConstraints);

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 0;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.ipady = 0;
		gBConstraints.weightx = 1.0;
		gBConstraints.weighty = 1.0;
		gBConstraints.gridy = GridBagConstraints.RELATIVE;
/*
		JScrollPane scrollPhoton = new JScrollPane(photonPanel);
		scrollPhoton.setMinimumSize(new Dimension(220, 350));
		scrollPhoton.setPreferredSize(new Dimension(240, 350));
		add(scrollPhoton, gBConstraints);
*/
		// Progress Bar
		add(photonPanel,gBConstraints);
		
		JPanel temp = new JPanel();
		temp.setLayout(new GridBagLayout());
		temp.setBorder(new TitledBorder("Progress..."));
		GridBagConstraints tempConstraints = new GridBagConstraints();
		tempConstraints.gridx = 0;
		tempConstraints.gridy = 0;
		tempConstraints.weightx = 1.0;
		tempConstraints.fill = GridBagConstraints.HORIZONTAL;
		tempConstraints.insets = new Insets(0, 0, 0, 0);

		status = new JLabel("");
		temp.add(status, tempConstraints);

		tempConstraints.gridy = 1;
		progress = new JProgressBar(0,100);
		progress.setStringPainted(true);
		temp.add(progress, tempConstraints);

		gBConstraints.insets = new Insets(0, 0, 0, 0);
		gBConstraints.gridy = GridBagConstraints.RELATIVE;
		add(temp, gBConstraints);
		
		



		
		// Save Image button

		/*JButton saveImageButton = new JButton("Save Image");
		saveImageButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				BMP.saveImage(theScene.canvas.getGL());
			}
		});
		gBConstraints.insets = new Insets(10, 0, 0, 0);
		gBConstraints.gridy = GridBagConstraints.RELATIVE;
		add(saveImageButton, gBConstraints);*/

		// Blank Space
		//JLabel blank = new JLabel();
		//gBConstraints.weighty = 1.0;
		//add(blank, gBConstraints);

	} // end constructor

	public void updateProgress(double currentValue, double maxValue)
	{
		double decimalPercent = currentValue/maxValue;
		double percentValue = 100*decimalPercent;
		
		//String info = new String("Progress: " + String.format(" %3.5f ", percentValue) + " % ");
		progress.setValue((int)percentValue);
		this.update(getGraphics());
		
	}
	
} // end class MiscPanel
