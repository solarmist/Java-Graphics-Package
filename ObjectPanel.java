import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.io.*;

/**
 * This class provides a panel for loading and manipulating PMesh objects.
 *
 * @version 24-Jan-2005
 */
public class ObjectPanel extends JPanel
{

///////////////////////////////////////////////////////////////////////////////
// DATA MEMBERS

	public Scene theScene; // ref to the rendering object
	public JPanel scenePanel, selectionPanel, transformsPanel;
	public ObjSpecsDialog objSpecsDialog;

	// Mouse transformation stuff
	public JPanel mousePanel;
	public JRadioButton moveObjects, moveCamera;
	public ButtonGroup mouseButtonGroup;
	public JSlider mouseScale;	//From Jeff Powell's code
	public double scaling;

    // TRACKBALL CODE
    public JRadioButton onlyX, onlyY, onlyZ, rollXY, trackball;
    public ButtonGroup modeGroup;

	// Scene and Selection components
	public JButton saveSceneButton, loadSceneButton, loadObjectButton, deleteObjectButton, saveasObjectButton, saveObjAndMatButton;
	public JButton showSpecsButton, rayTraceButton;
	public JComboBox objectList;
	// Transformation components;
	public JRadioButton moveButton, rotateButton, scaleButton;
	public ButtonGroup transformButtonGroup;
	public JTextField angleEdit, xEdit, yEdit, zEdit, sliderText;
	public JLabel xLabel, yLabel, zLabel, angleLabel;
	public JButton transformButton; // the Apply button
	// gridbag constraints
	public JFileChooser objChooser;
	public FileExtensionFilter filter;
	public GridBagConstraints gBConstraints, ObjConstr;
	public MouseControl mouseController;
	public ControlPanel ctrlPanel;

	public MaterialEditor materialEditor;
	public MaterialChooser materialChooser;

	ObjectPanel(Scene aSceneRef, ControlPanel aCtrlPanel)
	{
		objChooser = new JFileChooser("./Objects");
		filter = new FileExtensionFilter();
		filter.addExtension("obj");
		filter.addExtension("3ds");
		filter.setDescription("Supported Objects");
		objChooser.setFileFilter(filter);
		objChooser.setMultiSelectionEnabled(true);
		theScene = aSceneRef;
		ctrlPanel = aCtrlPanel;

		materialEditor = new MaterialEditor(aSceneRef);
		materialChooser = new MaterialChooser(aSceneRef, materialEditor);

		mouseController = new MouseControl( this );
		theScene.canvas.addMouseListener( mouseController );
		theScene.canvas.addMouseMotionListener( mouseController );
		theScene.canvas.addMouseWheelListener( mouseController );

		setLayout(new GridBagLayout());

		selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridBagLayout());
		selectionPanel.setBorder(new TitledBorder("Selection"));
		selectionPanel.setMinimumSize(new Dimension(220, 90));
		selectionPanel.setPreferredSize(new Dimension(240, 90));
		objectList = new JComboBox();
		objectList.setEditable(true);
		objectList.setMinimumSize(new Dimension(180, 25));
		objectList.setPreferredSize(new Dimension(200, 25));

		objectList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (objectList.getItemCount() > 0) { // If there are objects in the list
					PMesh curObj = theScene.getPMesh(objectList.
						getSelectedIndex());
					if (curObj != null) {
						curObj.objName = (String) objectList.getSelectedItem();
						theScene.curObject = (curObj);
					} // end if
				} // end if
				if (theScene.hasCurObject())
					System.out.println("Current object: " + theScene.curObject.objName);
			} // end method actionPerformed
		});

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridwidth = 3;
		gBConstraints.insets = new Insets(10, 0, 10, 0);
		selectionPanel.add(objectList, gBConstraints);

		loadObjectButton = new JButton("Load");
		loadObjectButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				objChooser.setSelectedFile(null);
				int returnVal = objChooser.showOpenDialog(selectionPanel);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					java.io.File [] fileList = objChooser.getSelectedFiles();
					for(int i = 0; i < fileList.length; i++)
					{
						int fileType = 0;
						if(filter.getExtension(fileList[i]).equalsIgnoreCase("obj"))
							fileType = ObjectTypes.TYPE_OBJ;
						if(filter.getExtension(fileList[i]).equalsIgnoreCase("3ds"))
							fileType = ObjectTypes.TYPE_3DS;
						theScene.addObject(fileList[i].getPath(), fileType);
						objectList.addItem(theScene.curObject.objName);
						objectList.setSelectedIndex(objectList.getItemCount()-1);
						// Update Material Editor
						materialEditor.recalculateMaterialTree();
						materialEditor.resetPreview();
						materialChooser.recalculateMaterialTree();
						theScene.refreshCanvas();
					} // end for
				} // end if
			} // end method actionPerformed
		});

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 1;
		selectionPanel.add(loadObjectButton, gBConstraints);

		deleteObjectButton = new JButton("Delete");

		deleteObjectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				theScene.deleteObject( (String) objectList.getSelectedItem());
				objectList.removeAllItems();
				for (int i = 0; i < theScene.getNumPMeshObjects(); i++) {
					objectList.addItem(theScene.getPMesh(i).objName);
				} // end for
				if (objectList.getItemCount() > 0) {
					objectList.setSelectedIndex(0);
				} // end if
				theScene.refreshCanvas();
				if (theScene.hasCurObject())
					System.out.println("Current object: " + theScene.curObject.objName);
				// Update Material Editor
				materialEditor.recalculateMaterialTree();
				materialEditor.resetPreview();
				materialChooser.recalculateMaterialTree();
			} // end method actionPerformed
		});

		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 1;
		gBConstraints.gridy = 1;
		selectionPanel.add(deleteObjectButton, gBConstraints);

		showSpecsButton = new JButton("Details");
		showSpecsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(theScene.hasCurObject()) {
					objSpecsDialog = new ObjSpecsDialog(theScene.curObject);
					objSpecsDialog.show();
				} // end if
			} // end method actionPerformed
		});
		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 2;
		gBConstraints.gridy = 1;
		selectionPanel.add(showSpecsButton, gBConstraints);

		saveObjAndMatButton = new JButton("Save OBJ File");
		saveObjAndMatButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if(theScene.hasCurObject())
					theScene.curObject.save(theScene.curObject.filePath+theScene.curObject.objName);
					//theScene.saveObjectAndMaterials(theScene.curObject);
			}
		});
		gBConstraints = new GridBagConstraints();
		gBConstraints.fill = GridBagConstraints.HORIZONTAL;
		gBConstraints.insets = new Insets(0, 0, 10, 0);
		gBConstraints.gridwidth = 3;
		gBConstraints.gridx=0;
		gBConstraints.gridy=2;
		selectionPanel.add(saveObjAndMatButton, gBConstraints);

		ObjConstr = new GridBagConstraints();
		ObjConstr.gridx = 0;
		ObjConstr.gridy = 1;
		ObjConstr.fill = GridBagConstraints.BOTH;
		ObjConstr.ipady = 25;
		ObjConstr.anchor = GridBagConstraints.NORTH;
		add(selectionPanel, ObjConstr);

		transformsPanel = new JPanel();
		transformsPanel.setLayout(new GridBagLayout());
		transformsPanel.setBorder(new TitledBorder("Transformations"));
		// move, rotate, scale radio buttons
		transformButtonGroup = new ButtonGroup();

		moveButton = new JRadioButton("Move");
		moveButton.setSelected(true);

		moveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				angleEdit.setEnabled(false);
				angleLabel.setEnabled(false);
			} // end method actionPerformed
		});

		transformButtonGroup.add(moveButton);
		gBConstraints = new GridBagConstraints();
		gBConstraints.insets = new Insets(0, 0, 5, 0);
		transformsPanel.add(moveButton, gBConstraints);

		rotateButton = new JRadioButton("Rotate");
		rotateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				angleEdit.setEnabled(true);
				angleLabel.setEnabled(true);
			} // end method actionPerformed
		});

		transformButtonGroup.add(rotateButton);
		transformsPanel.add(rotateButton, gBConstraints);

		scaleButton = new JRadioButton("Scale");
		scaleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				angleEdit.setEnabled(false);
				angleLabel.setEnabled(false);
			} // end method actionPerformed
		});

		gBConstraints.anchor = GridBagConstraints.WEST;
		transformsPanel.add(scaleButton, gBConstraints);
		transformButtonGroup.add(scaleButton);

		// Rotation stuff - angle and axis
		angleEdit = new JTextField();
		angleEdit.setHorizontalAlignment(JTextField.RIGHT);
		angleEdit.setText("0.00");
		angleEdit.setPreferredSize(new Dimension(75, 20));
		angleEdit.setEnabled(false);
		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 1;
		gBConstraints.gridy = 1;
		gBConstraints.insets = new Insets(0, 0, 5, 0);
		transformsPanel.add(angleEdit, gBConstraints);

		xEdit = new JTextField();
		xEdit.setHorizontalAlignment(JTextField.RIGHT);
		xEdit.setText("0.00");
		xEdit.setPreferredSize(new Dimension(75, 20));
		gBConstraints.gridx = 1;
		gBConstraints.gridy = 2;
		transformsPanel.add(xEdit, gBConstraints);

		yEdit = new JTextField();
		yEdit.setHorizontalAlignment(JTextField.RIGHT);
		yEdit.setText("0.00");
		yEdit.setPreferredSize(new Dimension(75, 20));
		gBConstraints.gridx = 1;
		gBConstraints.gridy = 3;
		transformsPanel.add(yEdit, gBConstraints);

		zEdit = new JTextField();
		zEdit.setHorizontalAlignment(JTextField.RIGHT);
		zEdit.setText("0.00");
		zEdit.setPreferredSize(new Dimension(75, 20));
		gBConstraints = new GridBagConstraints();
		gBConstraints.gridx = 1;
		gBConstraints.gridy = 4;
		transformsPanel.add(zEdit, gBConstraints);

		angleLabel = new JLabel();
		angleLabel.setText("Angle:");
		angleLabel.setEnabled(false);
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 1;
		gBConstraints.anchor = GridBagConstraints.EAST;
		transformsPanel.add(angleLabel, gBConstraints);

		xLabel = new JLabel("X:");
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 2;
		gBConstraints.anchor = GridBagConstraints.EAST;
		transformsPanel.add(xLabel, gBConstraints);

		yLabel = new JLabel("Y:");
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 3;
		gBConstraints.anchor = GridBagConstraints.EAST;
		transformsPanel.add(yLabel, gBConstraints);

		zLabel = new JLabel("Z:");
		gBConstraints.gridx = 0;
		gBConstraints.gridy = 4;
		gBConstraints.anchor = GridBagConstraints.EAST;
		transformsPanel.add(zLabel, gBConstraints);

		// the Apply button
		transformButton = new JButton("Apply");
		transformButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt)
		{
			if(!theScene.hasCurObject())
				return;
			if(moveButton.isSelected()){
				try{
					double x = (xEdit.getText() != null) ?
					Double.parseDouble(xEdit.getText()) : 0.0;
					double y = (yEdit.getText() != null) ?
					Double.parseDouble(yEdit.getText()) : 0.0;
					double z = (zEdit.getText() != null) ?
					Double.parseDouble(zEdit.getText()) : 0.0;
					theScene.curObject.translate(x, y, z);
				} // end try
				catch(NumberFormatException e)
				{
					xEdit.setText("0.00");
					yEdit.setText("0.00");
					zEdit.setText("0.00");
					System.out.println("Please enter numbers only");
				} // end catch
				theScene.refreshCanvas();
			} // end if
			else if(rotateButton.isSelected())
			{
				try{
					double a = (angleEdit.getText() != null) ?
					Double.parseDouble(angleEdit.getText()) : 0.0;
					double x = (xEdit.getText() != null) ?
					Double.parseDouble(xEdit.getText()) : 0.0;
					double y = (yEdit.getText() != null) ?
					Double.parseDouble(yEdit.getText()) : 0.0;
					double z = (zEdit.getText() != null) ?
					Double.parseDouble(zEdit.getText()) : 0.0;
						theScene.curObject.rotate(a, x, y, z);
				} // end try
				catch(NumberFormatException e)
				{
					angleEdit.setText("0.00");
					xEdit.setText("0.00");
					yEdit.setText("0.00");
					zEdit.setText("0.00");
					System.out.println("Please enter numbers only");
				} // end catch
				theScene.refreshCanvas();
			} // end else if
			else // Scaling object
			{
				try{
					double x = (xEdit.getText() != null) ?
					Double.parseDouble(xEdit.getText()) : 0.0;
					double y = (yEdit.getText() != null) ?
					Double.parseDouble(yEdit.getText()) : 0.0;
					double z = (zEdit.getText() != null) ?
					Double.parseDouble(zEdit.getText()) : 0.0;
					theScene.curObject.scale(x, y, z);
				} // end try
				catch(NumberFormatException e){
					xEdit.setText("0.00");
					yEdit.setText("0.00");
					zEdit.setText("0.00");
					System.out.println("Please enter numbers only");
				} // end catch
				theScene.refreshCanvas();
			} // end else
		} // end method actionPerformed
		});

		gBConstraints.gridx = 2;
		gBConstraints.gridy = 2;
		gBConstraints.insets = new Insets(0, 25, 0, 0);
		gBConstraints.anchor = GridBagConstraints.EAST;
		transformsPanel.add(transformButton, gBConstraints);

		ObjConstr = new GridBagConstraints();
		ObjConstr.gridx = 0;
		ObjConstr.gridy = 2;
		ObjConstr.fill = GridBagConstraints.HORIZONTAL;
		ObjConstr.ipady = 35;
		ObjConstr.anchor = GridBagConstraints.NORTH;
		add(transformsPanel, ObjConstr);

		mousePanel = new JPanel();
		mousePanel.setBorder(new TitledBorder("Mouse Transforms"));
		mousePanel.setLayout(new BorderLayout());

		sliderText = new JTextField("Movement multiplier (0.0 - 5.0):  1.00");
		sliderText.setEditable(false);
		sliderText.setBackground(Color.lightGray);

		mouseScale = new JSlider(JSlider.HORIZONTAL,1,100,20);
		mouseScale.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e) {
				DecimalFormat fmt = new DecimalFormat("0.00");
				scaling = (double)mouseScale.getValue() / 20.0;
				sliderText.setText("Movement multiplier (0.0 - 100.0):  " + fmt.format(scaling));
			} // end method stateChanged
		});
		scaling = (double)mouseScale.getValue() / 20.0;

		// TRACKBALL CODE
        onlyX = new JRadioButton("Rotate / Scale - X Only",false);
        onlyX.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mouseController.movMode = MouseControl.ONLY_X;
            } // end method actionPerformed
        }); // End onlyX button

        onlyY = new JRadioButton("Rotate / Scale - Y Only",false);
        onlyY.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mouseController.movMode = MouseControl.ONLY_Y;
            } // end method actionPerformed
        }); // End onlyY button

        onlyZ = new JRadioButton("Rotate / Scale - Z Only",false);
        onlyZ.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mouseController.movMode = MouseControl.ONLY_Z;
            } // end method actionPerformed
        }); // End onlyZ button

        rollXY = new JRadioButton("Rotate X and Y / Scale Uniform",true);
        rollXY.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mouseController.movMode = MouseControl.ROLL_XY;
            } // end method actionPerformed
        }); // End rollXY button

        trackball = new JRadioButton("Trackball",false);
        trackball.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mouseController.movMode = MouseControl.TRACKBALL;
            }
        }); // End trackball button

        modeGroup = new ButtonGroup();
        modeGroup.add(onlyX);
        modeGroup.add(onlyY);
        modeGroup.add(onlyZ);
        modeGroup.add(rollXY);
        modeGroup.add(trackball);
        // END TRACKBALL CODE

		JPanel checkBoxes = new JPanel();
		checkBoxes.setLayout(new GridLayout(7,1));
		checkBoxes.add(sliderText);
		checkBoxes.add(mouseScale);
        checkBoxes.add(onlyX);
        checkBoxes.add(onlyY);
        checkBoxes.add(onlyZ);
        checkBoxes.add(rollXY);
        checkBoxes.add(trackball);
		mousePanel.add(checkBoxes, BorderLayout.CENTER);
		ObjConstr = new GridBagConstraints();
		ObjConstr.gridx = 0;
		ObjConstr.gridy = 3;
		ObjConstr.fill = GridBagConstraints.HORIZONTAL;
		ObjConstr.anchor = GridBagConstraints.NORTH;
		add(mousePanel, ObjConstr);

		// Material Editor
		JPanel materialsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints matConstr = new GridBagConstraints();

		JButton materialsEditButton = new JButton("Materials Editor");
		materialsEditButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.out.println("MaterialsEditor");
				materialEditor.show();
			} // end actionPerformed
		});
		matConstr.gridx = 0;
		matConstr.weightx = 0.5;
		matConstr.fill = GridBagConstraints.HORIZONTAL;
		materialsPanel.add(materialsEditButton, matConstr);

		JButton materialsChooseButton = new JButton("Materials Chooser");
		materialsChooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.out.println("MaterialsChooser");
				materialChooser.setVisible(true);
			} // end actionPerformed
		});
		matConstr.gridx = 1;
		materialsPanel.add(materialsChooseButton, matConstr);

		ObjConstr.weightx = 0.01;
		ObjConstr.weighty = 0.01;
		ObjConstr.gridx = 0;
		ObjConstr.gridy = 4;
		add(materialsPanel, ObjConstr);
		
		rayTraceButton = new JButton("Ray Trace");
		rayTraceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
	  			System.out.println("On "+ Thread.currentThread());
				theScene.topFrame.statusArea.showStatus("Raytracing...");
				System.out.println("Add a raytracer");
				theScene.rayTrace =true;
				theScene.refreshCanvas();			
			} // end actionPerformed
		});
		ObjConstr.gridx = 0;
		ObjConstr.gridy = 5;
		ObjConstr.ipady = 5;
		ObjConstr.fill = GridBagConstraints.HORIZONTAL;
		ObjConstr.anchor = GridBagConstraints.NORTH;
		add(rayTraceButton, ObjConstr);

	} // end constructor

        /**
	 * This method updates all GUI components in the ObjectPanel to reflect
	 * the changes caused by loading a new Scene.
	 */
	public void changeScenes() {
		// update the objectList ComboBox
		objectList.removeAllItems(); // removes old objects
		for (int i = 0; i < theScene.getNumPMeshObjects(); i++)
			objectList.addItem(theScene.getPMesh(i).objName);
		// Update Material Editor
		materialEditor.recalculateMaterialTree();
		materialEditor.resetPreview();
		materialChooser.recalculateMaterialTree();
	} // end method changeScenes

} // end ObjectPanel
