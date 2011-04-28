package EWUPackage.panels;


import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import EWUPackage.scene.*;
import EWUPackage.scene.camera.*;
import EWUPackage.scene.primitives.*;

/**
 * This class provides a panel for manipulating camera settings.
 *
 * @version 1-Feb-2005
 */
public class CameraPanel extends MasterPanel implements FocusListener
{

///////////////////////////////////////////////////////////////////////////////
// DATA MEMBERS

	private static final long serialVersionUID = 1L;
	public JPanel camModeTogglePanel, glFrustumPanel, gluPerspectivePanel;
	public ButtonGroup cameraModeButtonGroup;
	public JRadioButton glFrustumButton, gluPerspectiveButton, glOrthoButton;
	public JCheckBox maintainAspectBox;
	public JLabel leftLabel, rightLabel, topLabel, bottomLabel, nearGLFLabel, farGLFLabel;
	public JTextField leftEdit, rightEdit, topEdit, bottomEdit, nearGLFEdit, farGLFEdit;
	public JTextField fovEdit, aspectEdit, nearGLUEdit, farGLUEdit;
	public JLabel fovLabel, aspectLabel, nearGLULabel, farGLULabel;
	public JPanel camOrientationPanel, camLocationPanel, camRefPanel, camUpPanel;
	public JLabel camLocXLabel, camLocYLabel, camLocZLabel, camRefXLabel, camRefYLabel, camRefZLabel,  camUpXLabel, camUpYLabel,camUpZLabel;
	public JTextField  camLocXEdit, camLocYEdit, camLocZEdit, camRefXEdit, camRefYEdit, camRefZEdit, camUpXEdit, camUpYEdit, camUpZEdit;
	public JButton setCamButton, setLensButton;

	public JSlider moveSpeedSlider;
	public JSlider rotateSpeedSlider;

	/**
	 * The maximum movement speed of the camera.
	 */
	public static final float MAX_MOVE_SPEED = 5.0f;

	/**
	 * The minimum movement speed of the camera.
	 */
	public static final float MIN_MOVE_SPEED = 0.1f;

	/**
	 * The maximum rotation speed of the camera.
	 */
	public static final float MAX_ROTATE_SPEED = 0.05f;

	/**
	 * The minimum rotation speed of the camera.
	 */
	public static final float MIN_ROTATE_SPEED = 0.001f;

	public java.text.DecimalFormat numFormat;

	public GridBagConstraints gBConstraints, ObjConstr;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR

	public CameraPanel(Scene aSceneRef)
	{
		super(aSceneRef);
		name = "Camera";
		
		Camera cam = theScene.camera;
		numFormat = new java.text.DecimalFormat("#0.00");
		setLayout(new GridBagLayout());
		camModeTogglePanel = new JPanel();
		camModeTogglePanel.setBorder(new TitledBorder("Perspective Setting Mode"));
		camModeTogglePanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc_camModeTogglePanel = new GridBagConstraints();
		cameraModeButtonGroup = new ButtonGroup();

		glFrustumButton = new JRadioButton();
		glFrustumButton.setSelected(true);
		glFrustumButton.setText("glFrustum");
		glFrustumButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				leftLabel.setEnabled(true);
				leftEdit.setEnabled(true);
				rightLabel.setEnabled(true);
				rightEdit.setEnabled(true);
				topLabel.setEnabled(true);
				topEdit.setEnabled(true);
				bottomLabel.setEnabled(true);
				bottomEdit.setEnabled(true);
				nearGLFLabel.setEnabled(true);
				nearGLFEdit.setEnabled(true);
				farGLFLabel.setEnabled(true);
				farGLFEdit.setEnabled(true);

				fovLabel.setEnabled(false);
				fovEdit.setEnabled(false);
				aspectLabel.setEnabled(false);
				aspectEdit.setEnabled(false);
				nearGLULabel.setEnabled(false);
				nearGLUEdit.setEnabled(false);
				farGLULabel.setEnabled(false);
				farGLUEdit.setEnabled(false);
			} // end method actionPerformed
		});
		cameraModeButtonGroup.add(glFrustumButton);
		gbc_camModeTogglePanel.gridx = 0;
		gbc_camModeTogglePanel.gridy = 0;
		camModeTogglePanel.add(glFrustumButton, gbc_camModeTogglePanel);

		gluPerspectiveButton = new JRadioButton();
		gluPerspectiveButton.setText("gluPerspective");
		gluPerspectiveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				leftLabel.setEnabled(false);
				leftEdit.setEnabled(false);
				rightLabel.setEnabled(false);
				rightEdit.setEnabled(false);
				topLabel.setEnabled(false);
				topEdit.setEnabled(false);
				bottomLabel.setEnabled(false);
				bottomEdit.setEnabled(false);
				nearGLFLabel.setEnabled(false);
				nearGLFEdit.setEnabled(false);
				farGLFLabel.setEnabled(false);
				farGLFEdit.setEnabled(false);

				fovLabel.setEnabled(true);
				fovEdit.setEnabled(true);
				aspectLabel.setEnabled(true);
				aspectEdit.setEnabled(true);
				nearGLULabel.setEnabled(true);
				nearGLUEdit.setEnabled(true);
				farGLULabel.setEnabled(true);
				farGLUEdit.setEnabled(true);
			} // end method actionPerformed
		});
		cameraModeButtonGroup.add(gluPerspectiveButton);
		gbc_camModeTogglePanel.gridx = 1;
		gbc_camModeTogglePanel.gridy = 0;		
		camModeTogglePanel.add(gluPerspectiveButton, gbc_camModeTogglePanel);

		glOrthoButton = new JRadioButton();
		glOrthoButton.setSelected(true);
		glOrthoButton.setText("glOrtho");
		glOrthoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				leftLabel.setEnabled(true);
				leftEdit.setEnabled(true);
				rightLabel.setEnabled(true);
				rightEdit.setEnabled(true);
				topLabel.setEnabled(true);
				topEdit.setEnabled(true);
				bottomLabel.setEnabled(true);
				bottomEdit.setEnabled(true);
				nearGLFLabel.setEnabled(true);
				nearGLFEdit.setEnabled(true);
				farGLFLabel.setEnabled(true);
				farGLFEdit.setEnabled(true);

				fovLabel.setEnabled(false);
				fovEdit.setEnabled(false);
				aspectLabel.setEnabled(false);
				aspectEdit.setEnabled(false);
				nearGLULabel.setEnabled(false);
				nearGLUEdit.setEnabled(false);
				farGLULabel.setEnabled(false);
				farGLUEdit.setEnabled(false);
			} // end method actionPerformed
		});
		cameraModeButtonGroup.add(glOrthoButton);
		gbc_camModeTogglePanel.gridx = 2;
		gbc_camModeTogglePanel.gridy = 0;		
		camModeTogglePanel.add(glOrthoButton, gbc_camModeTogglePanel);
		
		maintainAspectBox = new JCheckBox("Maintain Viewport/Window Aspect Ratio");
		maintainAspectBox.setSelected(theScene.maintainAspect);
		maintainAspectBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				theScene.maintainAspect = maintainAspectBox.isSelected();
				if (theScene.maintainAspect) {
					glFrustumButton.setSelected(true);
					leftLabel.setEnabled(true);
					leftEdit.setEnabled(true);
					rightLabel.setEnabled(true);
					rightEdit.setEnabled(true);
					topLabel.setEnabled(true);
					topEdit.setEnabled(true);
					bottomLabel.setEnabled(true);
					bottomEdit.setEnabled(true);
					nearGLFLabel.setEnabled(true);
					nearGLFEdit.setEnabled(true);
					farGLFLabel.setEnabled(true);
					farGLFEdit.setEnabled(true);

					gluPerspectiveButton.setSelected(false);
					gluPerspectiveButton.setEnabled(false);
					fovLabel.setEnabled(false);
					fovEdit.setEnabled(false);
					aspectLabel.setEnabled(false);
					aspectEdit.setEnabled(false);
					nearGLULabel.setEnabled(false);
					nearGLUEdit.setEnabled(false);
					farGLULabel.setEnabled(false);
					farGLUEdit.setEnabled(false);

					// adjust the window settings to match aspect ratio of viewport
					theScene.adjustWindowAspect();
					updateCameraFields();
					theScene.refreshCanvas();
				} else { // end if
					gluPerspectiveButton.setEnabled(true);
				} // end else
			} // end method actionPerformed
                });
		gbc_camModeTogglePanel.gridx = 0;
		gbc_camModeTogglePanel.gridy = 1;		
		gbc_camModeTogglePanel.gridwidth = 3;		
		camModeTogglePanel.add(maintainAspectBox, gbc_camModeTogglePanel);

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridwidth = 1;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.anchor = GridBagConstraints.NORTH;
		gBConstraints.insets = new Insets(0, 0, 10, 0);
		add(camModeTogglePanel, gBConstraints);

		/// glFrustum Panel
		glFrustumPanel = new JPanel();
		glFrustumPanel.setLayout(new GridLayout(3, 4));
		glFrustumPanel.setBorder(new TitledBorder("glFrustum Mode"));
		leftLabel = new JLabel();
		leftLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		leftLabel.setText("Left:");
		glFrustumPanel.add(leftLabel);

		leftEdit = new JTextField();
		leftEdit.setHorizontalAlignment(JTextField.RIGHT);
		leftEdit.setText((new Double(cam.windowLeft)).toString());
		glFrustumPanel.add(leftEdit);

		rightLabel = new JLabel();
		rightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		rightLabel.setText("Right:");
		glFrustumPanel.add(rightLabel);

		rightEdit = new JTextField();
		rightEdit.setHorizontalAlignment(JTextField.RIGHT);
		rightEdit.setText((new Double(cam.windowRight)).toString());
		glFrustumPanel.add(rightEdit);

		topLabel = new JLabel();
		topLabel.setHorizontalAlignment(JTextField.RIGHT);
		topLabel.setText("Top:");
		glFrustumPanel.add(topLabel);

		topEdit = new JTextField();
		topEdit.setHorizontalAlignment(JTextField.RIGHT);
		topEdit.setText((new Double(cam.windowTop)).toString());
		glFrustumPanel.add(topEdit);

		bottomLabel = new JLabel();
		bottomLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		bottomLabel.setText("Bottom:");
		glFrustumPanel.add(bottomLabel);

		bottomEdit = new JTextField();
		bottomEdit.setHorizontalAlignment(JTextField.RIGHT);
		bottomEdit.setText((new Double(cam.windowBottom)).toString());
		glFrustumPanel.add(bottomEdit);

		nearGLFLabel = new JLabel();
		nearGLFLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nearGLFLabel.setText("Near:");
		glFrustumPanel.add(nearGLFLabel);

		nearGLFEdit = new JTextField();
		nearGLFEdit.setHorizontalAlignment(JTextField.RIGHT);
		nearGLFEdit.setEditable(true);
		nearGLFEdit.setText((new Double(cam.near)).toString());
		glFrustumPanel.add(nearGLFEdit);

		farGLFLabel = new JLabel();
		farGLFLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		farGLFLabel.setText("Far:");
		glFrustumPanel.add(farGLFLabel);

		farGLFEdit = new JTextField();
		farGLFEdit.setHorizontalAlignment(JTextField.RIGHT);
		farGLFEdit.setText((new Double(cam.far)).toString());
		glFrustumPanel.add(farGLFEdit);

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 1;
		gBConstraints.gridwidth = 1;
		gBConstraints.weightx = 1.0;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.anchor = GridBagConstraints.NORTH;
		add(glFrustumPanel, gBConstraints);

		/// gluPerspective Panel
		gluPerspectivePanel = new JPanel();
		gluPerspectivePanel.setLayout(new GridLayout(4, 2));
		gluPerspectivePanel.setBorder(new TitledBorder("gluPerspective Mode"));

		fovLabel = new JLabel();
		fovLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		fovLabel.setText("Field of View:");
		fovLabel.setEnabled(false);
		gluPerspectivePanel.add(fovLabel);

		fovEdit = new JTextField();
		fovEdit.setHorizontalAlignment(JTextField.RIGHT);
		fovEdit.setText(String.valueOf(cam.fov));
		fovEdit.setEnabled(false);
		gluPerspectivePanel.add(fovEdit);

		aspectLabel = new JLabel();
		aspectLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		aspectLabel.setText("Aspect Ratio:");
		aspectLabel.setEnabled(false);
		gluPerspectivePanel.add(aspectLabel);

		aspectEdit = new JTextField();
		aspectEdit.setHorizontalAlignment(JTextField.RIGHT);
		aspectEdit.setText(String.valueOf(cam.aspectRatio));
		aspectEdit.setEnabled(false);
		gluPerspectivePanel.add(aspectEdit);

		nearGLULabel = new JLabel();
		nearGLULabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nearGLULabel.setText("Near:");
		nearGLULabel.setEnabled(false);
		gluPerspectivePanel.add(nearGLULabel);

		nearGLUEdit = new JTextField();
		nearGLUEdit.setHorizontalAlignment(JTextField.RIGHT);
		nearGLUEdit.setText((new Double(cam.near)).toString());
		nearGLUEdit.setEnabled(false);
		gluPerspectivePanel.add(nearGLUEdit);

		farGLULabel = new JLabel();
		farGLULabel.setHorizontalAlignment(SwingConstants.RIGHT);
		farGLULabel.setText("Far:");
		farGLULabel.setEnabled(false);
		gluPerspectivePanel.add(farGLULabel);

		farGLUEdit = new JTextField();
		farGLUEdit.setHorizontalAlignment(JTextField.RIGHT);
		farGLUEdit.setText((new Double(cam.far)).toString());
		farGLUEdit.setEnabled(false);
		gluPerspectivePanel.add(farGLUEdit);

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 2;
		gBConstraints.gridwidth = 1;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.anchor = GridBagConstraints.NORTH;
		gBConstraints.insets = new Insets(0, 0, 10, 0);
		add(gluPerspectivePanel, gBConstraints);

		setLensButton = new JButton("Set Lens");
		setLensButton.setHorizontalTextPosition(SwingConstants.CENTER);
		setLensButton.setMaximumSize(new Dimension(95, 30));
		setLensButton.setMinimumSize(new Dimension(95, 30));
		setLensButton.setPreferredSize(new Dimension(95, 30));
		setLensButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if(glFrustumButton.isSelected())
				{
					try
					{
						double left = (leftEdit.getText() != null) ? Double.parseDouble(leftEdit.getText()) : 0.0;
						double right = (rightEdit.getText() != null) ? Double.parseDouble(rightEdit.getText()) : 0.0;
						double top = (topEdit.getText() != null) ? Double.parseDouble(topEdit.getText()) : 0.0;
						double bottom = (bottomEdit.getText() != null) ? Double.parseDouble(bottomEdit.getText()) : 0.0;
						double near = (nearGLFEdit.getText() != null) ? Double.parseDouble(nearGLFEdit.getText()) : 0.0;
						double far = (farGLFEdit.getText() != null) ? Double.parseDouble(farGLFEdit.getText()) : 0.0;
						//At this point, all of the numbers have read correctly
						theScene.camera.setFrustum(left, right, bottom, top, near, far);
					} // end try
					catch(NumberFormatException e)
					{
						Camera camera = theScene.camera;
						leftEdit.setText(String.valueOf(numFormat.format(camera.windowLeft)));
						rightEdit.setText(String.valueOf(numFormat.format(camera.windowRight)));
						topEdit.setText(String.valueOf(numFormat.format(camera.windowTop)));
						bottomEdit.setText(String.valueOf(numFormat.format(camera.windowBottom)));
						nearGLFEdit.setText(String.valueOf(numFormat.format(camera.near)));
						farGLFEdit.setText(String.valueOf(numFormat.format(camera.far)));
						System.out.println("Please enter numbers only");
					} // end catch
				} // end if
				else // gluPerspective
				{
					try
					{
						double fov = (leftEdit.getText() != null) ? Double.parseDouble(fovEdit.getText()) : 0.0;
						double aspect = (rightEdit.getText() != null) ? Double.parseDouble(aspectEdit.getText()) : 0.0;
						double near = (nearGLFEdit.getText() != null) ? Double.parseDouble(nearGLUEdit.getText()) : 0.0;
						double far = (farGLFEdit.getText() != null) ? Double.parseDouble(farGLUEdit.getText()) : 0.0;
						//At this point, all of the numbers have read correctly
						theScene.camera.setPerspective(fov, aspect, near, far);
					} // end try
					catch(NumberFormatException e)
					{
						Camera camera = theScene.camera;
						fovEdit.setText(String.valueOf(numFormat.format(2*Math.atan(0.5*
							(camera.getWindowHeight())/
							camera.near)*180/Math.PI)));
						aspectEdit.setText(String.valueOf(numFormat.format(
							(camera.getWindowWidth())/
							(camera.getWindowHeight()))));
						nearGLUEdit.setText(String.valueOf(numFormat.format(camera.near)));
						farGLUEdit.setText(String.valueOf(numFormat.format(camera.far)));
						System.out.println("Please enter numbers only");
					} // end catch
				} // end else
				// adjust the window aspect ratio if necessary
				if (theScene.maintainAspect) {
					theScene.adjustWindowAspect();
				} // end if
				updateCameraFields();
				theScene.refreshCanvas();
			} // end method actionPerformed
		});
		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 3;
		gBConstraints.gridwidth = 1;
		gBConstraints.weightx = 1.0;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.insets = new Insets(0, 0, 10, 0);
		gBConstraints.anchor = GridBagConstraints.NORTH;
		add(setLensButton, gBConstraints);

		camOrientationPanel = new JPanel();
		camOrientationPanel.setLayout(new GridBagLayout());
		camOrientationPanel.setBorder(new TitledBorder("Camera Orientation"));

		// Camera Location sub Panel
		Double3D eye = cam.eye;
		camLocationPanel = new JPanel();
		camLocationPanel.setLayout(new GridLayout(3, 2));
		camLocationPanel.setBorder(new TitledBorder("Camera Location"));

		camLocXLabel = new JLabel("X:");
		camLocXLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		camLocationPanel.add(camLocXLabel);

		camLocXEdit = new JTextField();
		camLocXEdit.setHorizontalAlignment(JTextField.RIGHT);
		camLocXEdit.setText((new Double(eye.x)).toString());
		camLocXEdit.setMinimumSize(new Dimension(50, 20));
		camLocXEdit.setPreferredSize(new Dimension(50, 20));
		camLocationPanel.add(camLocXEdit);

		camLocYLabel = new JLabel("Y:");
		camLocYLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		camLocationPanel.add(camLocYLabel);

		camLocYEdit = new JTextField();
		camLocYEdit.setHorizontalAlignment(JTextField.RIGHT);
		camLocYEdit.setText((new Double(eye.y)).toString());
		camLocYEdit.setMinimumSize(new Dimension(50, 20));
		camLocYEdit.setPreferredSize(new Dimension(50, 20));
		camLocationPanel.add(camLocYEdit);

		camLocZLabel = new JLabel("Z:");
		camLocZLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		camLocationPanel.add(camLocZLabel);

		camLocZEdit = new JTextField();
		camLocZEdit.setHorizontalAlignment(JTextField.RIGHT);
		camLocZEdit.setText((new Double(eye.z)).toString());
		camLocZEdit.setMinimumSize(new Dimension(50, 20));
		camLocZEdit.setPreferredSize(new Dimension(50, 20));
		camLocationPanel.add(camLocZEdit);

		gBConstraints = new GridBagConstraints();
		camOrientationPanel.add(camLocationPanel, gBConstraints);

		// Camera Reference Point sub panel
		Double3D center = cam.center;
		camRefPanel = new JPanel();
		camRefPanel.setLayout(new GridLayout(3, 2));
		camRefPanel.setBorder(new TitledBorder("Reference Point"));

		camRefXLabel = new JLabel("X:");
		camRefXLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		camRefPanel.add(camRefXLabel);

		camRefXEdit = new JTextField();
		camRefXEdit.setHorizontalAlignment(JTextField.RIGHT);
		camRefXEdit.setText((new Double(center.x)).toString());
		camRefXEdit.setMinimumSize(new Dimension(50, 20));
		camRefXEdit.setPreferredSize(new Dimension(50, 20));
		camRefPanel.add(camRefXEdit);

		camRefYLabel = new JLabel("Y:");
		camRefYLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		camRefPanel.add(camRefYLabel);

		camRefYEdit = new JTextField();
		camRefYEdit.setHorizontalAlignment(JTextField.RIGHT);
		camRefYEdit.setText((new Double(center.y)).toString());
		camRefYEdit.setMinimumSize(new Dimension(50, 20));
		camRefYEdit.setPreferredSize(new Dimension(50, 20));
		camRefPanel.add(camRefYEdit);

		camRefZLabel = new JLabel("Z:");
		camRefZLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		camRefPanel.add(camRefZLabel);

		camRefZEdit = new JTextField();
		camRefZEdit.setHorizontalAlignment(JTextField.RIGHT);
		camRefZEdit.setText((new Double(center.z)).toString());
		camRefZEdit.setMinimumSize(new Dimension(50, 20));
		camRefZEdit.setPreferredSize(new Dimension(50, 20));
		camRefPanel.add(camRefZEdit);

		camOrientationPanel.add(camRefPanel, gBConstraints);

		// Camera up vector
		Double3D up = cam.up;
		camUpPanel = new JPanel();
		camUpPanel.setLayout(new GridLayout(3, 2));
		camUpPanel.setBorder(new TitledBorder("Up Direction"));

		camUpXLabel = new JLabel("X:");
		camUpXLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		camUpPanel.add(camUpXLabel);

		camUpXEdit = new JTextField();
		camUpXEdit.setHorizontalAlignment(JTextField.RIGHT);
		camUpXEdit.setText((new Double(up.x)).toString());
		camUpXEdit.setMinimumSize(new Dimension(50, 20));
		camUpXEdit.setPreferredSize(new Dimension(50, 20));
		camUpPanel.add(camUpXEdit);

		camUpYLabel = new JLabel("Y:");
		camUpYLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		camUpPanel.add(camUpYLabel);

		camUpYEdit = new JTextField();
		camUpYEdit.setHorizontalAlignment(JTextField.RIGHT);
		camUpYEdit.setText((new Double(up.y)).toString());
		camUpYEdit.setMinimumSize(new Dimension(50, 20));
		camUpYEdit.setPreferredSize(new Dimension(50, 20));
		camUpPanel.add(camUpYEdit);

		camUpZLabel = new JLabel("Z:");
		camUpZLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		camUpPanel.add(camUpZLabel);

		camUpZEdit = new JTextField();
		camUpZEdit.setHorizontalAlignment(JTextField.RIGHT);
		camUpZEdit.setText((new Double(up.z)).toString());
		camUpZEdit.setMinimumSize(new Dimension(50, 20));
		camUpZEdit.setPreferredSize(new Dimension(50, 20));
		camUpPanel.add(camUpZEdit);

		gBConstraints.gridx = 0;
		gBConstraints.gridy = 1;
		camOrientationPanel.add(camUpPanel, gBConstraints);

		setCamButton = new JButton("Set Camera");
		setCamButton.setMaximumSize(new Dimension(110, 30));
		setCamButton.setMinimumSize(new Dimension(110, 30));
		setCamButton.setPreferredSize(new Dimension(110, 30));
		setCamButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				try
				{
					float eyeX = (float) ((camLocXEdit.getText() != null) ? Double.parseDouble(camLocXEdit.getText()) : 0.0);
					float eyeY = (float) ((camLocYEdit.getText() != null) ? Double.parseDouble(camLocYEdit.getText()) : 0.0);
					float eyeZ = (float) ((camLocZEdit.getText() != null) ? Double.parseDouble(camLocZEdit.getText()) : 0.0);
					float refX = (float) ((camRefXEdit.getText() != null) ? Double.parseDouble(camRefXEdit.getText()) : 0.0);
					float refY = (float) ((camRefYEdit.getText() != null) ? Double.parseDouble(camRefYEdit.getText()) : 0.0);
					float refZ = (float) ((camRefZEdit.getText() != null) ? Double.parseDouble(camRefZEdit.getText()) : 0.0);
					float upX = (float) ((camUpXEdit.getText() != null) ? Double.parseDouble(camUpXEdit.getText()) : 0.0);
					float upY = (float) ((camUpYEdit.getText() != null) ? Double.parseDouble(camUpYEdit.getText()) : 0.0);
					float upZ = (float) ((camUpZEdit.getText() != null) ? Double.parseDouble(camUpZEdit.getText()) : 0.0);
					//At this point, all of the numbers have read correctly
					theScene.camera.setCamera(eyeX, eyeY, eyeZ, refX, refY, refZ, upX, upY, upZ);
					theScene.refreshCanvas();
				} // end try
				catch(NumberFormatException e)
				{
					Camera camera = theScene.camera;
					Double3D eye = camera.eye;
					Double3D ctr = camera.center;
					Double3D up  = camera.up;
					camLocXEdit.setText(String.valueOf(numFormat.format(eye.x)));
					camLocYEdit.setText(String.valueOf(numFormat.format(eye.y)));
					camLocZEdit.setText(String.valueOf(numFormat.format(eye.z)));
					camRefXEdit.setText(String.valueOf(numFormat.format(ctr.x)));
					camRefYEdit.setText(String.valueOf(numFormat.format(ctr.y)));
					camRefZEdit.setText(String.valueOf(numFormat.format(ctr.z)));
					camUpXEdit.setText(String.valueOf(numFormat.format(up.x)));
					camUpYEdit.setText(String.valueOf(numFormat.format(up.y)));
					camUpZEdit.setText(String.valueOf(numFormat.format(up.z)));
					System.out.println("Please enter numbers only");
				} // end catch
			} // end method actionPerformed
		});
		gBConstraints.gridx = 1;
		gBConstraints.gridy = 1;
		camOrientationPanel.add(setCamButton, gBConstraints);

		gBConstraints.gridx = 0;
		gBConstraints.gridy = 4;
		gBConstraints.gridwidth = 1;
		gBConstraints.weightx = 1.0;
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.anchor = GridBagConstraints.NORTH;
		gBConstraints.insets = new Insets(0, 0, 10, 0);
		add(camOrientationPanel, gBConstraints);

		// Camera Movement/Rotation Speeds
		JPanel tempPanel;
		int initialValue;
		float initialPercent;
		final int sliderMin = 0, sliderMax = 100;

		initialPercent = (theScene.camera.moveSpeed - MIN_MOVE_SPEED) / (MAX_MOVE_SPEED-MIN_MOVE_SPEED);
		initialValue = (int)((initialPercent * (sliderMax-sliderMin)) + sliderMin);
		moveSpeedSlider = new JSlider(JSlider.HORIZONTAL, sliderMin, sliderMax, initialValue);
		moveSpeedSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				float percent = ((float)moveSpeedSlider.getValue() - sliderMin) / (float)(sliderMax-sliderMin);
				theScene.camera.moveSpeed = ( (percent * (MAX_MOVE_SPEED-MIN_MOVE_SPEED)) + MIN_MOVE_SPEED);
			} // end method stateChanged
		});
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 5;
		gBConstraints.gridwidth = 1;
		gBConstraints.weighty = 0.0;
		gBConstraints.insets = new Insets(0, 0, 10, 0);
		gBConstraints.anchor = GridBagConstraints.NORTH;
		tempPanel = new JPanel();
		tempPanel.setBorder( BorderFactory.createTitledBorder("Movement Speed") );
		tempPanel.add(moveSpeedSlider, gBConstraints);
		this.add(tempPanel, gBConstraints);

		initialPercent = (theScene.camera.rotateSpeed - MIN_ROTATE_SPEED) / (MAX_ROTATE_SPEED-MIN_ROTATE_SPEED);
		initialValue = (int)((initialPercent * (sliderMax-sliderMin)) + sliderMin);
		rotateSpeedSlider = new JSlider(JSlider.HORIZONTAL, sliderMin, sliderMax, initialValue );
		rotateSpeedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				float percent = ((float)rotateSpeedSlider.getValue() - sliderMin) / (float)(sliderMax-sliderMin);
				theScene.camera.rotateSpeed = ( (percent * (MAX_ROTATE_SPEED-MIN_ROTATE_SPEED)) + MIN_ROTATE_SPEED);
			} // end method stateChanged
		});
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 6;
		gBConstraints.gridwidth = 1;
		gBConstraints.weighty = 0.01;
		gBConstraints.insets = new Insets(0, 0, 10, 0);
		gBConstraints.anchor = GridBagConstraints.NORTH;
		tempPanel = new JPanel();
		tempPanel.setBorder( BorderFactory.createTitledBorder("Rotation Speed") );
		tempPanel.add(rotateSpeedSlider, gBConstraints);
		this.add(tempPanel, gBConstraints);

		// setup canvas focus listener
		theScene.canvas.addFocusListener(this);

	} // end CameraPanel()

	/**
	 * This method is called when the canvas gains the keyboard focus.
	 *
	 * @param e info about the focus gain
	 */
	public void focusGained(FocusEvent e) {
		// System.out.println("Canvas GAINED focus");
	} // end method focusGained

	/**
	 * This method is called when the canvas loses the keyboard focus.
	 *
	 * @param e info about the focus lost
	 */
	public synchronized void focusLost(FocusEvent e)
	{
		System.out.println("Canvas LOST focus: Updating Camera Fields");
		updateCameraFields();
	} // end method focusLost

	public void updateCameraFields()
	{
		Camera cam = theScene.camera;
		Double3D eye = cam.eye;
		Double3D ctr = cam.center;
		Double3D up  = cam.up;
		DecimalFormat fmt = new DecimalFormat("0.00");

		camLocXEdit.setText(fmt.format(eye.x));
		camLocYEdit.setText(fmt.format(eye.y));
		camLocZEdit.setText(fmt.format(eye.z));
		camRefXEdit.setText(fmt.format(ctr.x));
		camRefYEdit.setText(fmt.format(ctr.y));
		camRefZEdit.setText(fmt.format(ctr.z));
		camUpXEdit.setText(fmt.format(up.x));
		camUpYEdit.setText(fmt.format(up.y));
		camUpZEdit.setText(fmt.format(up.z));

		if (glFrustumButton.isSelected()) {
			leftEdit.setText(fmt.format(cam.windowLeft));
			rightEdit.setText(fmt.format(cam.windowRight));
			topEdit.setText(fmt.format(cam.windowTop));
			bottomEdit.setText(fmt.format(cam.windowBottom));
			nearGLFEdit.setText(fmt.format(cam.near));
			farGLFEdit.setText(fmt.format(cam.far));

			//Now set the gluPerspective fields to match
			double top = cam.windowTop;
			double bottom = cam.windowBottom;
			double left = cam.windowLeft;
			double right = cam.windowRight;
			double difY = 0;
			if (top < 0 && bottom < 0) {
				difY = Math.abs(bottom) - Math.abs(top);
			} else if (top > 0 && bottom < 0) { // end if
				difY = top + Math.abs(bottom);
			} else { // end if
				difY = top - bottom;
			} // end else
			double difX = 0;
			if (right < 0 && left < 0) {
				difX = Math.abs(left) - Math.abs(right);
			} else if (right > 0 && left < 0) { // end if
				difX = right + Math.abs(left);
			} else { // end if
				difX = right - left;
			} // end else
			double aspect = difX / difY;
			double fov = Math.toDegrees(Math.atan((difY / 2) / cam.near));
			System.out.println(fov);
			fov = 2 * fov;
			System.out.println(fov);
			aspectEdit.setText(String.valueOf(numFormat.format(aspect)));
			fovEdit.setText(String.valueOf(numFormat.format(fov)));
			farGLUEdit.setText(farGLFEdit.getText());
			nearGLUEdit.setText(nearGLFEdit.getText());
		} // end if

		else if (gluPerspectiveButton.isSelected()) {
			fovEdit.setText(fmt.format(cam.fov));
			aspectEdit.setText(fmt.format(cam.aspectRatio)); // width/height
			nearGLUEdit.setText(fmt.format(cam.near));
			farGLUEdit.setText(fmt.format(cam.far));

			// Now set the glFrustum fields to match
			double near = cam.near;
			double fov = cam.fov;
			double aspect = cam.aspectRatio;
			double difY = near * Math.tan(0.5 * fov * Math.PI / 180.0);
			System.out.println(difY);
			System.out.println(Double.parseDouble(camLocYEdit.getText()));
			double top = difY;
			double bottom = -difY;
			double difX = aspect * difY;
			double right = difX;
			double left = -difX;
			leftEdit.setText(String.valueOf(numFormat.format(left)));
			rightEdit.setText(String.valueOf(numFormat.format(right)));
			topEdit.setText(String.valueOf(numFormat.format(top)));
			bottomEdit.setText(String.valueOf(numFormat.format(bottom)));
			nearGLFEdit.setText(nearGLUEdit.getText());
			farGLFEdit.setText(farGLUEdit.getText());
		} // end if
	} // end method updateCameraFields

        /**
	 * This method performs all GUI updates necessary when changing scenes.
	 */
	public void changeScenes() {
		updateCameraFields();
	} // end method changeScene

} // end class CameraPanel
