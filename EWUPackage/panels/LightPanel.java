package EWUPackage.panels;

import javax.swing.*;
import javax.swing.border.*;

import EWUPackage.scene.Light;
import EWUPackage.scene.Scene;

import java.awt.*;
import java.awt.event.*;


/**
 * This class provides a panel for manipulating lighting.
 *
 * @version 5-Feb-2005
 */
public class LightPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	public Scene theScene;
	public JPanel lightSelectPanel, typePanel, colorPanel, locationPanel, attribPanel;
	public JComboBox  lightList;
	public JCheckBox onCheckBox;
	public JRadioButton directionalButton, spotButton, pointButton;
	public JButton ambientButton, diffuseButton, specularButton;
	public JLabel ambientLabel, diffuseLabel, specularLabel;
	public JLabel posLabel, dirLabel, posXLabel, posYLabel, posZLabel, dirXLabel, dirYLabel, dirZLabel;
	public JTextField posXEdit, posYEdit, posZEdit, dirXEdit, dirYEdit, dirZEdit;
	public JButton setLightButton;
	public JLabel posWLabel, cutoffLabel, expLabel, constAttnLabel, linAttnLabel, quadAttnLabel;
	public JTextField posWEdit, cutoffEdit, expEdit,constAttnEdit, linAttnEdit,  quadAttnEdit;
	public JButton	setAttribButton;
	public ButtonGroup lightTypeButtonGroup;
	public java.text.DecimalFormat numFormat;

	public LightPanel(Scene aSceneRef)
	{
		this.theScene = aSceneRef;
		setLayout(new GridBagLayout());
		GridBagConstraints gBConstraints;
		numFormat = new java.text.DecimalFormat("#0.00");
		String[] lights = {
			"Light 0", "Light 1", "Light 2", "Light 3",
			"Light 4", "Light 5", "Light 6", "Light 7"};

		JPanel lightSelectPanel = new JPanel();
		lightSelectPanel.setBorder(new TitledBorder("Light Selection"));

		lightList = new JComboBox(lights);
		lightList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.out.println("Current light is now: " +
								   lightList.getSelectedIndex());
				updateLightPanel( getCurLight() );
			} // end method actionPerformed
		});
		lightSelectPanel.add(lightList);

		onCheckBox = new JCheckBox("On");
		onCheckBox.setSelected(false);
		onCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (onCheckBox.isSelected()) {
					getCurLight().lightSwitch = Light.ON;
				} // end if
				else {
					getCurLight().lightSwitch = Light.OFF;
				} // end else
				updateCurLight();
				theScene.refreshCanvas();
			} // end method mouseClicked
		});
		lightSelectPanel.add(onCheckBox);
		lightSelectPanel.add(lightList);

		gBConstraints = new GridBagConstraints();
		gBConstraints.weightx = 1.0;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.anchor = GridBagConstraints.NORTH;
		add(lightSelectPanel, gBConstraints);

		// Type Panel
		typePanel = new JPanel();
		typePanel.setLayout(new GridLayout(3, 1));
		typePanel.setBorder(new TitledBorder("Light Type"));
		lightTypeButtonGroup = new ButtonGroup();

		directionalButton = new JRadioButton();
		directionalButton.setSelected(false);
		directionalButton.setText("Directional");
		lightTypeButtonGroup.add(directionalButton);
		directionalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				getCurLight().spotLight = false;
				posWEdit.setText("0.00");
				getCurLight().position[3] = 0.0f;
				cutoffEdit.setEditable(false);
				expEdit.setEditable(false);
				cutoffEdit.setText("180.00");
				expEdit.setText("0.0");
				posLabel.setText("Direction");
				dirLabel.setText("Not Used");
				dirLabel.setEnabled(false);
				dirXLabel.setEnabled(false);
				dirYLabel.setEnabled(false);
				dirZLabel.setEnabled(false);
				dirXEdit.setEnabled(false);
				dirYEdit.setEnabled(false);
				dirZEdit.setEnabled(false);
				getCurLight().location = Light.DIRECTIONAL;
				updateCurLight();
				theScene.refreshCanvas();
			} // end method mouseClicked
		});
		typePanel.add(directionalButton);

		spotButton = new JRadioButton();
		directionalButton.setSelected(false);
		spotButton.setText("Spot Light");
		lightTypeButtonGroup.add(spotButton);
		spotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				getCurLight().spotLight = true;
				posWEdit.setText("1.00");
				getCurLight().position[3] = 1.0f;
				cutoffEdit.setEditable(true);
				expEdit.setEditable(true);
				cutoffEdit.setText(Float.toString(getCurLight().spotCutoff));
				expEdit.setText(Float.toString(getCurLight().spotExponent));
				posLabel.setText("Position");
				dirLabel.setText("Direction");
				dirLabel.setEnabled(true);
				dirXLabel.setEnabled(true);
				dirYLabel.setEnabled(true);
				dirZLabel.setEnabled(true);
				dirXEdit.setEnabled(true);
				dirYEdit.setEnabled(true);
				dirZEdit.setEnabled(true);
				getCurLight().location = Light.LOCAL;
				updateCurLight();
				theScene.refreshCanvas();
			} // end method mouseClicked
		});
		typePanel.add(spotButton);

		pointButton = new JRadioButton();
		pointButton.setSelected(false);
		pointButton.setText("Point Source");
		lightTypeButtonGroup.add(pointButton);
		pointButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				getCurLight().spotLight = false;
				posWEdit.setText("1.00");
				getCurLight().position[3] = 1.0f;
				cutoffEdit.setEditable(false);
				expEdit.setEditable(false);
				cutoffEdit.setText("180.00");
				expEdit.setText("0.0");
				posLabel.setText("Position");
				dirLabel.setText("Not Used");
				dirLabel.setEnabled(false);
				dirXLabel.setEnabled(false);
				dirYLabel.setEnabled(false);
				dirZLabel.setEnabled(false);
				dirXEdit.setEnabled(false);
				dirYEdit.setEnabled(false);
				dirZEdit.setEnabled(false);
				getCurLight().location = Light.LOCAL;
				updateCurLight();
				theScene.refreshCanvas();
			} // end method mouseClicked
		});
		typePanel.add(pointButton);

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 1;
		gBConstraints.gridwidth = 2;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(typePanel, gBConstraints);

		colorPanel = new JPanel();
		colorPanel.setLayout(new GridLayout(2, 3, 10, 0));
		colorPanel.setBorder(new TitledBorder("Color"));

		ambientButton = new JButton();
		ambientButton.setBackground(new Color(76, 76, 76));
		ambientButton.setMaximumSize(new Dimension(40, 25));
		ambientButton.setMinimumSize(new Dimension(40, 25));
		ambientButton.setPreferredSize(new Dimension(40, 25));
		ambientButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Color ambient = JColorChooser.showDialog(null, "Ambient Light Color",
											   ambientButton.getBackground());
				if (ambient != null) {
					getCurLight().ambient[0] = ambient.getRed() / 255.0f;
					getCurLight().ambient[1] = ambient.getGreen() / 255.0f;
					getCurLight().ambient[2] = ambient.getBlue() / 255.0f;
					ambientButton.setBackground(ambient);
					updateCurLight();
				} // end if
				theScene.refreshCanvas();
			} // end method mouseClicked
		});
		colorPanel.add(ambientButton);

		diffuseButton = new JButton();
		diffuseButton.setBackground(new Color(127, 127, 127));
		diffuseButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Color diffuse = JColorChooser.showDialog(null, "Diffuse Light Color",
											   diffuseButton.getBackground());
				if (diffuse != null) {
					getCurLight().diffuse[0] = diffuse.getRed() / 255.0f;
					getCurLight().diffuse[1] = diffuse.getGreen() / 255.0f;
					getCurLight().diffuse[2] = diffuse.getBlue() / 255.0f;
					diffuseButton.setBackground(diffuse);
					updateCurLight();
				} // end if
				theScene.refreshCanvas();
			} // end method mouseClicked
		});
		colorPanel.add(diffuseButton);

		specularButton = new JButton();
		specularButton.setBackground(Color.white);
		specularButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Color specular = JColorChooser.showDialog(null, "Specular Light Color",
												specularButton.getBackground());
				if (specular != null) {
					getCurLight().specular[0] = specular.getRed() / 255.0f;
					getCurLight().specular[1] = specular.getGreen() / 255.0f;
					getCurLight().specular[2] = specular.getBlue() / 255.0f;
					specularButton.setBackground(specular);
					updateCurLight();
				} // end if
				theScene.refreshCanvas();
			} // end mouseClicked
		});
		colorPanel.add(specularButton);

		ambientLabel = new JLabel("Ambient");
		ambientLabel.setHorizontalAlignment(SwingConstants.CENTER);
		colorPanel.add(ambientLabel);

		diffuseLabel = new JLabel("Diffuse");
		diffuseLabel.setHorizontalAlignment(SwingConstants.CENTER);
		colorPanel.add(diffuseLabel);

		specularLabel = new JLabel("Specular");
		specularLabel.setHorizontalAlignment(SwingConstants.CENTER);
		colorPanel.add(specularLabel);

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 2;
		gBConstraints.gridwidth = 2;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(colorPanel, gBConstraints);

		locationPanel = new JPanel();
		locationPanel.setLayout(new GridBagLayout());
		locationPanel.setBorder(new TitledBorder("Location"));
		GridBagConstraints locationConstraints;

		posLabel = new JLabel("Direction");
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridwidth = 2;
		locationPanel.add(posLabel, locationConstraints);

		dirLabel = new JLabel("Not Used");
		dirLabel.setEnabled(false);
		locationConstraints.gridx = 2;
		locationConstraints.gridy = 0;
		locationConstraints.gridwidth = 2;
		locationConstraints.insets = new Insets(0, 10, 0, 0);
		locationPanel.add(dirLabel, locationConstraints);

		posXLabel = new JLabel("X:");
		posXLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 0;
		locationConstraints.gridy = 1;
		locationConstraints.anchor = GridBagConstraints.EAST;
		locationPanel.add(posXLabel, locationConstraints);

		posYLabel = new JLabel("Y:");
		posYLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 0;
		locationConstraints.gridy = 2;
		locationConstraints.anchor = GridBagConstraints.EAST;
		locationPanel.add(posYLabel, locationConstraints);

		posZLabel = new JLabel("Z:");
		posZLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 0;
		locationConstraints.gridy = 3;
		locationConstraints.anchor = GridBagConstraints.EAST;
		locationPanel.add(posZLabel, locationConstraints);

		dirXLabel = new JLabel("X:");
		dirXLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		dirXLabel.setEnabled(false);
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 2;
		locationConstraints.gridy = 1;
		locationConstraints.insets = new Insets(0, 10, 0, 0);
		locationConstraints.anchor = GridBagConstraints.EAST;
		locationPanel.add(dirXLabel, locationConstraints);

		dirYLabel = new JLabel("Y:");
		dirYLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		dirYLabel.setEnabled(false);
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 2;
		locationConstraints.gridy = 2;
		locationConstraints.insets = new Insets(0, 10, 0, 0);
		locationConstraints.anchor = GridBagConstraints.EAST;
		locationPanel.add(dirYLabel, locationConstraints);

		dirZLabel = new JLabel("Z:");
		dirZLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		dirZLabel.setEnabled(false);
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 2;
		locationConstraints.gridy = 3;
		locationConstraints.insets = new Insets(0, 10, 0, 0);
		locationConstraints.anchor = GridBagConstraints.EAST;
		locationPanel.add(dirZLabel, locationConstraints);

		posXEdit = new JTextField();
		posXEdit.setHorizontalAlignment(JTextField.RIGHT);
		posXEdit.setText("0.00");
		posXEdit.setMinimumSize(new Dimension(60, 20));
		posXEdit.setPreferredSize(new Dimension(60, 20));
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 1;
		locationConstraints.gridy = 1;
		locationPanel.add(posXEdit, locationConstraints);

		posYEdit = new JTextField();
		posYEdit.setHorizontalAlignment(JTextField.RIGHT);
		posYEdit.setText("0.00");
		posYEdit.setMinimumSize(new Dimension(60, 20));
		posYEdit.setPreferredSize(new Dimension(60, 20));
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 1;
		locationConstraints.gridy = 2;
		locationPanel.add(posYEdit, locationConstraints);

		posZEdit = new JTextField();
		posZEdit.setHorizontalAlignment(JTextField.RIGHT);
		posZEdit.setText("200.00");
		posZEdit.setMinimumSize(new Dimension(60, 20));
		posZEdit.setPreferredSize(new Dimension(60, 20));
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 1;
		locationConstraints.gridy = 3;
		locationPanel.add(posZEdit, locationConstraints);

		dirXEdit = new JTextField();
		dirXEdit.setHorizontalAlignment(JTextField.RIGHT);
		dirXEdit.setText("0.00");
		dirXEdit.setMinimumSize(new Dimension(60, 20));
		dirXEdit.setPreferredSize(new Dimension(60, 20));
		dirXEdit.setEnabled(false);
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 3;
		locationConstraints.gridy = 1;
		locationPanel.add(dirXEdit, locationConstraints);

		dirYEdit = new JTextField();
		dirYEdit.setHorizontalAlignment(JTextField.RIGHT);
		dirYEdit.setText("0.00");
		dirYEdit.setMinimumSize(new Dimension(60, 20));
		dirYEdit.setPreferredSize(new Dimension(60, 20));
		dirYEdit.setEnabled(false);
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 3;
		locationConstraints.gridy = 2;
		locationPanel.add(dirYEdit, locationConstraints);

		dirZEdit = new JTextField();
		dirZEdit.setHorizontalAlignment(JTextField.RIGHT);
		dirZEdit.setText("-1.00");
		dirZEdit.setMinimumSize(new Dimension(60, 20));
		dirZEdit.setPreferredSize(new Dimension(60, 20));
		dirZEdit.setEnabled(false);
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 3;
		locationConstraints.gridy = 3;
		locationPanel.add(dirZEdit, locationConstraints);

		setLightButton = new JButton();
		setLightButton.setText("Set Light");
		setLightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					float posX = (posXEdit.getText() != null) ?
						Float.parseFloat(posXEdit.getText()) : 0.0f;
					float posY = (posYEdit.getText() != null) ?
						Float.parseFloat(posYEdit.getText()) : 0.0f;
					float posZ = (posZEdit.getText() != null) ?
						Float.parseFloat(posZEdit.getText()) : 0.0f;
					float posW = (posWEdit.getText() != null) ?
						Float.parseFloat(posWEdit.getText()) : 0.0f;
					float dirX = (dirXEdit.getText() != null) ?
						Float.parseFloat(dirXEdit.getText()) : 0.0f;
					float dirY = (dirYEdit.getText() != null) ?
						Float.parseFloat(dirYEdit.getText()) : 0.0f;
					float dirZ = (dirZEdit.getText() != null) ?
						Float.parseFloat(dirZEdit.getText()) : 0.0f;
					getCurLight().position[0] = posX;
					getCurLight().position[1] = posY;
					getCurLight().position[2] = posZ;
					if (posW >= 0.5) {
						getCurLight().position[3] = 1.0f;
						getCurLight().location = Light.LOCAL;
						posWEdit.setText("1.00");
					} // end if
					else {
						getCurLight().position[3] = 0.0f;
						getCurLight().location = Light.DIRECTIONAL;
						posWEdit.setText("0.00");
					} // end else
					getCurLight().direction[0] = dirX;
					getCurLight().direction[1] = dirY;
					getCurLight().direction[2] = dirZ;
				} // end try
				catch (NumberFormatException e) {
					posXEdit.setText(String.valueOf(numFormat.format(
						getCurLight().position[0])));
					posYEdit.setText(String.valueOf(numFormat.format(
						getCurLight().position[1])));
					posZEdit.setText(String.valueOf(numFormat.format(
						getCurLight().position[2])));
					posWEdit.setText(String.valueOf(numFormat.format(
						getCurLight().position[3])));
					dirXEdit.setText(String.valueOf(numFormat.format(
						getCurLight().direction[0])));
					dirYEdit.setText(String.valueOf(numFormat.format(
						getCurLight().direction[1])));
					dirZEdit.setText(String.valueOf(numFormat.format(
						getCurLight().direction[2])));
					System.out.println("Please enter numbers only");
				} // end catch
				updateCurLight();
				theScene.refreshCanvas();
			} // end method mouseClicked
		});
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 4;
		locationConstraints.gridy = 1;
		locationConstraints.gridheight = 2;
		locationConstraints.insets = new Insets(0, 15, 0, 0);
		locationPanel.add(setLightButton, locationConstraints);

		posWLabel = new JLabel();
		posWLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		posWLabel.setText("W:");
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 0;
		locationConstraints.gridy = 4;
		locationConstraints.insets = new Insets(0, 2, 0, 0);
		locationConstraints.anchor = GridBagConstraints.EAST;
		locationPanel.add(posWLabel, locationConstraints);

		posWEdit = new JTextField();
		posWEdit.setEditable(false);
		posWEdit.setHorizontalAlignment(JTextField.RIGHT);
		posWEdit.setText("0.00");
		posWEdit.setMinimumSize(new Dimension(60, 20));
		posWEdit.setPreferredSize(new Dimension(60, 20));
		locationConstraints = new GridBagConstraints();
		locationConstraints.gridx = 1;
		locationConstraints.gridy = 4;
		locationPanel.add(posWEdit, locationConstraints);

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 3;
		gBConstraints.gridwidth = 2;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(locationPanel, gBConstraints);

		attribPanel = new JPanel();
		attribPanel.setLayout(new GridBagLayout());
		GridBagConstraints attribConstraints;
		attribPanel.setBorder(new TitledBorder("Attributes"));

		cutoffLabel = new JLabel();
		cutoffLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		cutoffLabel.setText("Cutoff:");
		attribConstraints = new GridBagConstraints();
		attribConstraints.anchor = GridBagConstraints.EAST;
		attribPanel.add(cutoffLabel, attribConstraints);

		cutoffEdit = new JTextField();
		cutoffEdit.setHorizontalAlignment(JTextField.RIGHT);
		cutoffEdit.setText("45.00");
		cutoffEdit.setMaximumSize(new Dimension(60, 2147483647));
		cutoffEdit.setMinimumSize(new Dimension(60, 20));
		cutoffEdit.setPreferredSize(new Dimension(60, 20));
		attribConstraints = new GridBagConstraints();
		attribPanel.add(cutoffEdit, attribConstraints);

		expLabel = new JLabel();
		expLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		expLabel.setText("Exponent:");
		attribConstraints = new GridBagConstraints();
		attribConstraints.gridx = 0;
		attribConstraints.gridy = 1;
		attribConstraints.anchor = GridBagConstraints.EAST;
		attribPanel.add(expLabel, attribConstraints);

		expEdit = new JTextField();
		expEdit.setHorizontalAlignment(JTextField.RIGHT);
		expEdit.setText("0.00");
		expEdit.setMaximumSize(new Dimension(60, 2147483647));
		expEdit.setMinimumSize(new Dimension(60, 20));
		expEdit.setPreferredSize(new Dimension(60, 20));
		attribConstraints = new GridBagConstraints();
		attribConstraints.gridx = 1;
		attribConstraints.gridy = 1;
		attribPanel.add(expEdit, attribConstraints);

		constAttnLabel = new JLabel();
		constAttnLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		constAttnLabel.setText("Const. Attenuation:");
		attribConstraints = new GridBagConstraints();
		attribConstraints.gridx = 0;
		attribConstraints.gridy = 2;
		attribConstraints.anchor = GridBagConstraints.EAST;
		attribPanel.add(constAttnLabel, attribConstraints);

		constAttnEdit = new JTextField();
		constAttnEdit.setHorizontalAlignment(JTextField.RIGHT);
		constAttnEdit.setText("1.00");
		constAttnEdit.setMaximumSize(new Dimension(60, 2147483647));
		constAttnEdit.setMinimumSize(new Dimension(60, 20));
		constAttnEdit.setPreferredSize(new Dimension(60, 20));
		attribConstraints = new GridBagConstraints();
		attribConstraints.gridx = 1;
		attribConstraints.gridy = 2;
		attribPanel.add(constAttnEdit, attribConstraints);

		linAttnLabel = new JLabel();
		linAttnLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		linAttnLabel.setText("Linear Attenuation:");
		attribConstraints = new GridBagConstraints();
		attribConstraints.gridx = 0;
		attribConstraints.gridy = 3;
		attribConstraints.anchor = GridBagConstraints.EAST;
		attribPanel.add(linAttnLabel, attribConstraints);

		linAttnEdit = new JTextField();
		linAttnEdit.setHorizontalAlignment(JTextField.RIGHT);
		linAttnEdit.setText("0.00");
		linAttnEdit.setMaximumSize(new Dimension(60, 2147483647));
		linAttnEdit.setMinimumSize(new Dimension(60, 20));
		linAttnEdit.setPreferredSize(new Dimension(60, 20));
		attribConstraints = new GridBagConstraints();
		attribConstraints.gridx = 1;
		attribConstraints.gridy = 3;
		attribPanel.add(linAttnEdit, attribConstraints);

		quadAttnLabel = new JLabel();
		quadAttnLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		quadAttnLabel.setText("Quadratic Attenuation:");
		attribConstraints = new GridBagConstraints();
		attribConstraints.gridx = 0;
		attribConstraints.gridy = 4;
		attribConstraints.anchor = GridBagConstraints.EAST;
		attribPanel.add(quadAttnLabel, attribConstraints);

		quadAttnEdit = new JTextField();
		quadAttnEdit.setHorizontalAlignment(JTextField.RIGHT);
		quadAttnEdit.setText("0.00");
		quadAttnEdit.setMaximumSize(new Dimension(60, 2147483647));
		quadAttnEdit.setMinimumSize(new Dimension(60, 20));
		quadAttnEdit.setPreferredSize(new Dimension(60, 20));
		attribConstraints = new GridBagConstraints();
		attribConstraints.gridx = 1;
		attribConstraints.gridy = 4;
		attribPanel.add(quadAttnEdit, attribConstraints);

		setAttribButton = new JButton();
		setAttribButton.setText("Set Attributes");
		setAttribButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				try {
					float cutoff = (cutoffEdit.getText() != null) ?
						Float.parseFloat(cutoffEdit.getText()) : 0.0f;
					float exponent = (expEdit.getText() != null) ?
						Float.parseFloat(expEdit.getText()) : 0.0f;
					float constAttn = (constAttnEdit.getText() != null) ?
						Float.parseFloat(constAttnEdit.getText()) : 0.0f;
					float linAttn = (linAttnEdit.getText() != null) ?
						Float.parseFloat(linAttnEdit.getText()) : 0.0f;
					float quadAttn = (quadAttnEdit.getText() != null) ?
						Float.parseFloat(quadAttnEdit.getText()) : 0.0f;
					if (cutoff >= 90.0) {
						getCurLight().spotCutoff = 90.0f;
						cutoffEdit.setText("90.00");
					} // end if
					else {
						getCurLight().spotCutoff = cutoff;
					} // end else
					getCurLight().spotExponent = exponent;
					getCurLight().constAttenuation = constAttn;
					getCurLight().linearAttenuation = linAttn;
					getCurLight().quadraticAttenuation = quadAttn;
				} // end try
				catch (NumberFormatException e) {
					cutoffEdit.setText(String.valueOf(numFormat.format(
						getCurLight().spotCutoff)));
					expEdit.setText(String.valueOf(numFormat.format(
						getCurLight().spotExponent)));
					constAttnEdit.setText(String.valueOf(numFormat.format(
						getCurLight().constAttenuation)));
					linAttnEdit.setText(String.valueOf(numFormat.format(
						getCurLight().linearAttenuation)));
					quadAttnEdit.setText(String.valueOf(numFormat.format(
						getCurLight().quadraticAttenuation)));
					System.out.println("Please enter numbers only");
				} // end catch
				updateCurLight();
				theScene.refreshCanvas();
			} // end method mouseClicked
		});
		attribConstraints = new GridBagConstraints();
		attribConstraints.gridx = 0;
		attribConstraints.gridy = 5;
		attribConstraints.gridwidth = 2;
		attribConstraints.insets = new Insets(15, 0, 0, 0);
		attribPanel.add(setAttribButton, attribConstraints);

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 4;
		gBConstraints.gridwidth = 2;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.anchor = GridBagConstraints.NORTH;
		gBConstraints.weighty = 0.01;
		add(attribPanel, gBConstraints);

		updateLightPanel( getCurLight() ); // set all GUI components to match selected light
	    updateCurLight(); // force the initial light settings to be used when rendering

	} // end constructor

	/**
	 * This method updates all GUI components of this panel using the settings
	 * in the light parameter.
	 *
	 * @param light Light
	 */
	public void updateLightPanel(Light light)
	{
		onCheckBox.setSelected(light.lightSwitch == Light.ON ? true : false);
		if (light.location == Light.DIRECTIONAL) {
			directionalButton.setSelected(true);
			spotButton.setSelected(false);
			pointButton.setSelected(false);
			cutoffEdit.setEditable(true);
			posLabel.setText("Direction");
			dirLabel.setText("Not Used");
			dirLabel.setEnabled(false);
			dirXLabel.setEnabled(false);
			dirYLabel.setEnabled(false);
			dirZLabel.setEnabled(false);
			dirXEdit.setEnabled(false);
			dirYEdit.setEnabled(false);
			dirZEdit.setEnabled(false);
		} // end if
		else {
			if(light.spotCutoff == 180.0f) { // point source
				directionalButton.setSelected(false);
				spotButton.setSelected(false);
				pointButton.setSelected(true);
				cutoffEdit.setEditable(false);
				posLabel.setText("Position");
				dirLabel.setText("Not Used");
				dirLabel.setEnabled(false);
				dirXLabel.setEnabled(false);
				dirYLabel.setEnabled(false);
				dirZLabel.setEnabled(false);
				dirXEdit.setEnabled(false);
				dirYEdit.setEnabled(false);
				dirZEdit.setEnabled(false);
			} // end else
			else {
				directionalButton.setSelected(false);
				spotButton.setSelected(true);
				pointButton.setSelected(false);
				cutoffEdit.setEditable(true);
				posLabel.setText("Position");
				dirLabel.setText("Direction");
				dirLabel.setEnabled(true);
				dirXLabel.setEnabled(true);
				dirYLabel.setEnabled(true);
				dirZLabel.setEnabled(true);
				dirXEdit.setEnabled(true);
				dirYEdit.setEnabled(true);
				dirZEdit.setEnabled(true);
			} // end if
		} // end else
		ambientButton.setBackground(
			new Color(light.ambient[0],
					  light.ambient[1],
					  light.ambient[2]));
		diffuseButton.setBackground(
			new Color(light.diffuse[0],
					  light.diffuse[1],
					  light.diffuse[2]));
		specularButton.setBackground(
			new Color(light.specular[0],
					  light.specular[1],
					  light.specular[2]));
		posXEdit.setText(String.valueOf(numFormat.format(
			light.position[0])));
		posYEdit.setText(String.valueOf(numFormat.format(
			light.position[1])));
		posZEdit.setText(String.valueOf(numFormat.format(
			light.position[2])));
		posWEdit.setText(String.valueOf(numFormat.format(
			light.position[3])));
		dirXEdit.setText(String.valueOf(numFormat.format(
			light.direction[0])));
		dirYEdit.setText(String.valueOf(numFormat.format(
			light.direction[1])));
		dirZEdit.setText(String.valueOf(numFormat.format(
			light.direction[2])));
		cutoffEdit.setText(String.valueOf(numFormat.format(
			light.spotCutoff)));
		expEdit.setText(String.valueOf(numFormat.format(
			light.spotExponent)));
		constAttnEdit.setText(String.valueOf(numFormat.format(
			light.constAttenuation)));
		linAttnEdit.setText(String.valueOf(numFormat.format(
			light.linearAttenuation)));
		quadAttnEdit.setText(String.valueOf(numFormat.format(
		    light.quadraticAttenuation)));
	} // end method updateLightPanel

	/**
	 * This method returns the currently selected Light from the Scene.
	 */
	public Light getCurLight() {
		return theScene.getLight( lightList.getSelectedIndex() );
	} // end method getCurLight

	/**
	 * This method sets a flag in the Scene so the currently selected light gets
	 * updated the next time the scene is rendered.
	 */
	public void updateCurLight() {
		theScene.updateLightNextFrame( lightList.getSelectedIndex() );
	} // end method updateCurLight

    /**
     * This method performs all GUI updates necessary when changing scenes.
     */
    public void changeScenes()
    {
        for(int i = 0; i < 8; i++) {
            updateLightPanel( theScene.getLight(i) );
            theScene.updateLightNextFrame(i);
        } // end for
    } // end method changeScene

} // end class LightPanel
