package EWUPackage.panels.obj.material;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.*;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLProfile;

import java.text.NumberFormat;
import java.util.Vector;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
//import com.jogamp.opengl.util.*;
import static javax.media.opengl.GL2.*;

import EWUPackage.loaders.*;
import EWUPackage.scene.*;
import EWUPackage.scene.primitives.*;

/**
 * This class provides a mechanism for editing materials.
 *
 * @author Don Bushnell
 * @version 6-Mar-2005
 */
public class MaterialEditor extends JDialog implements TreeSelectionListener
{
	private static final long serialVersionUID = 1L;
	Scene theScene;
	MaterialCanvas previewCanvas, editCanvas;
	final int BASE = 0;//for materials[]
	final int EDIT = 1;//for materials[]
	final int BLACK = 2;//for materials[]

	final int EDIT_MODE = 10;//for setMode()
	final int SELECTION_MODE = 11;//for setMode()
	final int ADD_MODE = 12;
	final int SELECTED_MODE = 13;
	int currentMode=0;

	MaterialCell materials[];//this array will be used for BASE, EDIT, and BLACK values while working with materials
	JPanel previewPanel, addEditPrevPanel, shinyPanel, otherPropsPanel;
	JTree tree;
	DefaultMutableTreeNode top, node;
	TitledBorder editBorder, addEditBorder, shinyBorder, previewBorder, addBorder;
	DefaultTreeModel treeModel;
	JButton renameButton, addMaterialButton, editMaterialButton, applyButton, cancelButton;
	JButton matSaveButton, matSaveAsButton;
	JCheckBox viewSelectedCheckbox, doubleSidedCheckbox;
	JSlider shinySlider;
	JLabel shinyLabel, mtlFileNameLabel;
	MaterialLevels matLevels;
	String potentialMaterialsName=null, matFileName = null;//for adding materials
	public JFileChooser matFileChooser;
	
	public JLabel IORLabel;
	public JFormattedTextField IORField;
	public NumberFormat dnum;
	

	public MaterialEditor(Scene aSceneRef)
	{
		theScene = aSceneRef;
		materials = new MaterialCell[3];
		materials[BASE]=new MaterialCell();
		materials[EDIT]=new MaterialCell();
		materials[BLACK]=new MaterialCell();
		DoubleColor nada = new DoubleColor(0.0, 0.0, 0.0, 1.0);
		materials[BLACK].ka=nada;
		materials[BLACK].kd=nada;
		materials[BLACK].ks=nada;
		materials[BLACK].emmColor=nada;
		Container contentPane = getContentPane();
		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());
		setResizable(false);//The awt canvas throws off adjustments so it's disabled here.
		setTitle("Materials Editor");

		//TREE STUFF
		/////////////////////////////////////////////////////////////////////////////////////////////////

		//Create the nodes.
		top = new DefaultMutableTreeNode("Materials by Object");
		calculateNodes(top);

		//Create a tree that allows one selection at a time.
		treeModel = new DefaultTreeModel(top);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.setShowsRootHandles(true);

		//Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		//Create the scroll pane and add the tree to it.
		JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setPreferredSize(new Dimension(400, 180));
		treeScrollPane.setMinimumSize(new Dimension(400, 180));//prevents it from collapsing on a resize (when sizing is enabled)
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.fill=GridBagConstraints.BOTH;
		contentPane.add(treeScrollPane, gbc);
		gbc.gridwidth=1;

		//PREVIEW STUFF
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		previewCanvas = new MaterialCanvas(200,200);
		editCanvas = new MaterialCanvas(200,200);

		previewCanvas.material=materials[BLACK];
		editCanvas.material=materials[BLACK];

		gbc.gridx=0;
		gbc.gridy=1;
		gbc.fill=GridBagConstraints.NONE;
		previewPanel=new JPanel();
		previewBorder = new TitledBorder("Preview of Material");
		previewPanel.setBorder(previewBorder);
		previewPanel.add(previewCanvas.canvas);
		previewPanel.setMinimumSize(new Dimension(210,210));
		contentPane.add(previewPanel, gbc);
		gbc.gridx=1;
		addEditPrevPanel=new JPanel();
		addEditBorder = new TitledBorder("Add / Edit");
		editBorder = new TitledBorder("Edit Material");
		addBorder = new TitledBorder("Add Material");
		addEditBorder.setTitleColor(Color.GRAY);
		addEditPrevPanel.setBorder(addEditBorder);
		addEditPrevPanel.add(editCanvas.canvas);
		addEditPrevPanel.setMinimumSize(new Dimension(210,210));
		contentPane.add(addEditPrevPanel, gbc);

		// LEVELS SLIDERS AND BUTTONS for AMBIENT DIFFUSE SPECULAR EMISSIVE
		////////////////////////////////////////////////////////////////////////////////////////
		gbc.gridx=1;
		gbc.gridy=2;
		gbc.gridheight=9;
		matLevels = new MaterialLevels(this);
		matLevels.setMinimumSize(new Dimension(400,400));
		contentPane.add(matLevels, gbc);

		//RENAME BUTTON
		////////////////////////////////////////////////////////////////////////////////
		gbc.gridx=0;
		gbc.gridy=2;
		gbc.gridheight=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		renameButton = new JButton("Rename");
		renameButton.setFocusable(false);
		contentPane.add(renameButton, gbc);
		renameButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Object nodeInfo = node.getUserObject();
				MaterialNode matNode = (MaterialNode) nodeInfo;
				potentialMaterialsName = (String) JOptionPane.showInputDialog(null,
					"Enter a new name for " +
					matNode.mesh.materials[matNode.index].materialName,
					"RENAME MATERIAL",
					JOptionPane.PLAIN_MESSAGE, null, null, "");
				if (potentialMaterialsName == null)
					return;
				else {
					matNode.mesh.materials[matNode.index].materialName =
						potentialMaterialsName;
					tree.validate();
					tree.updateUI();
				} // end else
			} // end method mouseClicked
		});

		//ADD MATERIAL BUTTON
		////////////////////////////////////////////////////////////////////////////////
		gbc.gridy=3;
		addMaterialButton = new JButton("Add Material");
		addMaterialButton.setFocusable(false);
		contentPane.add(addMaterialButton, gbc);
		addMaterialButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Object nodeInfo = node.getUserObject();
				MaterialNode matNode = (MaterialNode) nodeInfo;
				materials[EDIT] = new MaterialCell(); //just to make sure theres nothing attached
				updateMaterialFromMaterial(materials[EDIT], matNode.mesh.materials[matNode.index]);
				editCanvas.material = materials[EDIT];
				updateMaterialFromMaterial(materials[BASE], matNode.mesh.materials[matNode.index]);
				potentialMaterialsName = (String) JOptionPane.showInputDialog(null,
					"Enter a name for new material", "ADD MATERIAL",
					JOptionPane.PLAIN_MESSAGE, null, null, "NewMaterial");
				if (potentialMaterialsName == null)
					return;
				setMode(ADD_MODE);
				addEditPrevPanel.setBorder(addBorder);
				// add a potentially new material to editable materials used by object
				theScene.cntrlPanel.objectPanel.materialChooser.calculateAvailableMaterialTree();
				updateLevels();
				updatePreview();
			} // end method mouseClicked
		});

		//EDIT MATERIAL BUTTON
		////////////////////////////////////////////////////////////////////////////////
		gbc.gridy=4;
		editMaterialButton = new JButton("Edit Material");
		editMaterialButton.setFocusable(false);
		contentPane.add(editMaterialButton, gbc);
		editMaterialButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Object nodeInfo = node.getUserObject();
				MaterialNode matNode = (MaterialNode) nodeInfo;
				//materials[EDIT]=new MaterialCell();//just to make sure theres nothing attached
				materials[EDIT] = matNode.mesh.materials[matNode.index];
				editCanvas.material = materials[EDIT]; //editScene and materials[EDIT] are now references to objects mat
				updateMaterialFromMaterial(materials[BASE], matNode.mesh.materials[matNode.index]);
				setMode(EDIT_MODE);
				addEditPrevPanel.setBorder(editBorder);
				updateLevels();
				updatePreview();
			} // end method mouseClicked
		});

		//APPLY BUTTON
		////////////////////////////////////////////////////////////////////////////////
		gbc.gridy=5;
		applyButton = new JButton("Apply");
		applyButton.setFocusable(false);
		contentPane.add(applyButton, gbc);
		applyButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Object nodeInfo = node.getUserObject();
				MaterialNode matNode = (MaterialNode) nodeInfo;
				if (currentMode == ADD_MODE) {
					MaterialCell[] newmats = new MaterialCell[matNode.mesh.materials.length + 1];
					int i;
					for (i = 0; i < newmats.length - 1; i++) {
						newmats[i] = matNode.mesh.materials[i];
					} // end for
					newmats[i] = new MaterialCell();
					updateMaterialFromMaterial(newmats[i], editCanvas.material);
					newmats[i].materialName = potentialMaterialsName;
					matNode.mesh.materials = newmats;
					DefaultMutableTreeNode parent = null;
					TreePath parentPath = tree.getSelectionPath().getParentPath();
					parent = (DefaultMutableTreeNode) (parentPath.
						getLastPathComponent());

					DefaultMutableTreeNode child = new DefaultMutableTreeNode(new
						MaterialNode(matNode.mesh, i));
					newmats = null;
					treeModel.insertNodeInto(child, parent,
											 parent.getChildCount());
					TreePath childPath = new TreePath(child.getPath());

					//TODO:FIND OUT WHY THIS LINE MAKES PREVIEW VISIBLE AGAIN
					//if the line isn't commented the added material will automatically be selected
					//tree.setSelectionPath(childPath);
					tree.makeVisible(childPath);
				} // end if
				else if (currentMode == EDIT_MODE) {
					materials[EDIT] = null;
					//we've been updating it already
				} // end if
				setMode(SELECTED_MODE);
				updateLevels();
			} // end method mouseClicked
		});

		//CANCEL BUTTON
		////////////////////////////////////////////////////////////////////////////////
		gbc.gridy=6;
		cancelButton = new JButton("Cancel");
		cancelButton.setFocusable(false);
		contentPane.add(cancelButton, gbc);
		cancelButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				Object nodeInfo = node.getUserObject();
				MaterialNode matNode = (MaterialNode) nodeInfo;
				updateMaterialFromMaterial(matNode.mesh.materials[matNode.index],
										   materials[BASE]);
				materials[EDIT] = null;
				setMode(SELECTED_MODE);
				updateLevels();
			} // end method mouseClicked
		});
		


		// create the otherPropsPanel, populate it and add it
		////////////////////////////////////////////////////////////////////////////////
		otherPropsPanel = new JPanel();
		GridBagConstraints otherPropsgbc = new GridBagConstraints();
		otherPropsPanel.setLayout(new GridBagLayout());
		otherPropsgbc.weightx = 0.5;
		otherPropsgbc.weighty = 0.5;
		otherPropsgbc.fill=GridBagConstraints.BOTH;
		otherPropsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));

		otherPropsPanel.setPreferredSize(new Dimension(160, 275));
		shinyLabel = new JLabel("Shiny Value:",SwingConstants.LEFT);
		shinyLabel.setFont(new Font("Arial", Font.BOLD, 13));
		shinyLabel.setEnabled(false);
		shinySlider = new JSlider(JSlider.HORIZONTAL,0, 128, 0);
		shinySlider.setMajorTickSpacing(32);
		shinySlider.setMinorTickSpacing(8);
		shinySlider.setPaintTicks(true);
		shinySlider.setPaintLabels(false);
		//shinySlider.setFocusable(false);
		
		shinySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					editCanvas.material.shiny = (((int)source.getValue()));
					updateLevels();
					updatePreview();
				} // end if
			} // end method stateChanged
		});
		otherPropsgbc.gridx = 0;
		otherPropsgbc.gridy = 0;
		otherPropsgbc.weightx = 0.1;
		
		otherPropsPanel.add(shinyLabel,otherPropsgbc);
		otherPropsgbc.gridx = 1;
		otherPropsgbc.weightx = 0.9;
		otherPropsPanel.add(shinySlider,otherPropsgbc);
		shinySlider.setEnabled(false);
		
		IORLabel = new JLabel("Refractive Index");
		IORLabel.setFont(new Font("Arial", Font.BOLD, 13));
		IORField = new JFormattedTextField(dnum);
		IORField.setHorizontalAlignment(JTextField.RIGHT);
		IORField.setValue(new Double(1.0));
		IORField.setMinimumSize(new Dimension(60, 20));
		IORField.setPreferredSize(new Dimension(60, 20));
		IORField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				JTextField jt = (JTextField)e.getSource();
				String str = jt.getText();
				Double doubleVal = new Double(str);
				System.out.printf("IOR value: %.3f\n", doubleVal.doubleValue());
				editCanvas.material.refractiveIndex = doubleVal;
				updateLevels();
				updatePreview();
			}
		});
		otherPropsgbc.gridx = 0;
		otherPropsgbc.gridy = 1;
		otherPropsgbc.gridheight = 1;
		otherPropsgbc.fill=GridBagConstraints.HORIZONTAL;
		otherPropsgbc.gridwidth =1;
		otherPropsgbc.weightx = 0.1;
		otherPropsPanel.add(IORLabel, otherPropsgbc);
		IORLabel.setEnabled(false);
		otherPropsgbc.weightx = 1.0;
		otherPropsgbc.weighty = 0.8;
		otherPropsgbc.gridx = 1;
		otherPropsgbc.gridy = 1;
		otherPropsgbc.weightx = 0.9;
		otherPropsPanel.add(IORField, otherPropsgbc);
		IORField.setEnabled(false);

		//DOUBLE SIDED BUTTON (disabled)
		////////////////////////////////////////////////////////////////////////////////
		doubleSidedCheckbox = new JCheckBox("DoubleSided");
		//doubleSidedCheckbox.setFocusable(false);
		doubleSidedCheckbox.setEnabled(false);
		doubleSidedCheckbox.setFont(new Font("Arial", Font.BOLD, 13));
		doubleSidedCheckbox.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
			} // end method mouseClicked
		});
		otherPropsgbc.gridx=0;
		otherPropsgbc.gridy=2;
		otherPropsgbc.fill=GridBagConstraints.BOTH;
		//otherPropsgbc.weightx = 0.5;
		otherPropsgbc.anchor=GridBagConstraints.WEST;
		otherPropsgbc.weightx = 0.1;
		otherPropsPanel.add(doubleSidedCheckbox, otherPropsgbc);
		
		mtlFileNameLabel = new JLabel("File: "+matFileName);
		otherPropsgbc.gridx=0;
		otherPropsgbc.gridy=3;
		otherPropsgbc.gridwidth = 2;
		otherPropsgbc.fill=GridBagConstraints.NONE;
		//otherPropsgbc.weightx = 0.5;
		otherPropsgbc.anchor=GridBagConstraints.CENTER;
		otherPropsgbc.weightx = 0.1;
		otherPropsPanel.add(mtlFileNameLabel, otherPropsgbc);
		mtlFileNameLabel.setEnabled(false);
		mtlFileNameLabel.setFont(new Font("Arial", Font.BOLD, 13));

		matSaveButton = new JButton("Material Save");
		matFileChooser = new JFileChooser("./Objects");

		matSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				//Object nodeInfo = node.getUserObject();
				//MaterialNode matNode = (MaterialNode)nodeInfo;
				MaterialNode matNode = (MaterialNode)node.getUserObject();
				ObjLoaderBuffer anObj = (ObjLoaderBuffer)matNode.mesh;
				String finalPath = matNode.mesh.filePath+anObj.mtlFileName;
				anObj.saveMaterials(finalPath);
				updateLevels();
				updatePreview();
			}
		});		
		otherPropsgbc.gridx=0;
		otherPropsgbc.gridy=4;
		otherPropsgbc.gridwidth = 2;
		otherPropsgbc.fill=GridBagConstraints.NONE;
		//otherPropsgbc.weightx = 0.5;
		otherPropsgbc.anchor=GridBagConstraints.CENTER;
		otherPropsgbc.weightx = 0.1;
		otherPropsPanel.add(matSaveButton, otherPropsgbc);
		matSaveButton.setEnabled(false);
		
		matSaveAsButton = new JButton("Material SaveAs...");
		matSaveAsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				matFileChooser.setSelectedFile(null);
				int returnVal = matFileChooser.showSaveDialog(addEditPrevPanel);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					java.io.File theFile = matFileChooser.getSelectedFile();
					MaterialNode matNode = (MaterialNode)node.getUserObject();
					ObjLoaderBuffer anObj = (ObjLoaderBuffer)matNode.mesh;
					String fileName = theFile.getPath();
					anObj.saveMaterials(fileName);

				} // end if				
				updateLevels();
				updatePreview();
			}
		});		
		otherPropsgbc.gridx=0;
		otherPropsgbc.gridy=5;
		otherPropsgbc.gridwidth = 2;
		otherPropsgbc.fill=GridBagConstraints.NONE;
		//otherPropsgbc.weightx = 0.5;
		otherPropsgbc.anchor=GridBagConstraints.CENTER;
		otherPropsgbc.weightx = 0.1;
		otherPropsPanel.add(matSaveAsButton, otherPropsgbc);
		matSaveAsButton.setEnabled(false);

		gbc.gridy=7;
		gbc.gridheight =1;
		gbc.anchor = GridBagConstraints.NORTH;
		//gbc.fill=GridBagConstraints.VERTICAL;
		gbc.weighty =0.5;
		contentPane.add(otherPropsPanel, gbc);

		setMode(SELECTION_MODE);
		addMaterialButton.setEnabled(false);
		editMaterialButton.setEnabled(false);
		pack();

		previewCanvas.refresh();
		editCanvas.refresh();
	}//End MaterialEditor Constructor

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e)
	{
		node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if (node == null || node==top || !node.isLeaf())
		{
			renameButton.setEnabled(false);
			addMaterialButton.setEnabled(false);
			editMaterialButton.setEnabled(false);
			previewCanvas.material=materials[BLACK];
			previewBorder.setTitleColor(Color.GRAY);
			previewPanel.updateUI();
			updatePreview();
			return;
		} // end if
		else if (node.isLeaf()&& node!=top)
		{
			Object nodeInfo = node.getUserObject();
			MaterialNode matNode = (MaterialNode)nodeInfo;
			setMode(SELECTED_MODE);
			previewBorder.setTitleColor(Color.BLACK);
			previewPanel.updateUI();
			previewCanvas.material=matNode.mesh.materials[matNode.index];
			ObjLoaderBuffer anObj = (ObjLoaderBuffer)matNode.mesh;
			matFileName = anObj.mtlFileName;
			mtlFileNameLabel.setText("File: "+matFileName);
			updatePreview();
		} // end if
		return;
	} // end method valueChanged

	public void EnableMaterialWidgets(boolean value	){
		if(otherPropsPanel != null){
			otherPropsPanel.setEnabled(value);
			shinyLabel.setEnabled(value);
			shinySlider.setEnabled(value);
			IORLabel.setEnabled(value);
			IORField.setEnabled(value);
			matSaveButton.setEnabled(value);
			matSaveAsButton.setEnabled(value);
			mtlFileNameLabel.setEnabled(value);
			//doubleSidedCheckbox.setEnabled(value);
		}
	}

	/**
	 * Will hold all the material information in the tree based on
	 * PMesh.materials[]
	 */
	public class MaterialNode
	{
		PMesh mesh = null;
		int index = 0;

		MaterialNode(PMesh tmpMesh, int i)
		{
			mesh=tmpMesh;
			index=i;
		} // end constructor

		public String toString()//Needed for DefaultMutableTreeNode
		{
			return (index+" "+mesh.materials[index].materialName);
		} // end method toString
	} // end class MaterialNode

	public void calculateNodes(DefaultMutableTreeNode top)
	{
		DefaultMutableTreeNode object = null;
		//DefaultMutableTreeNode surfaces = null;
		DefaultMutableTreeNode material = null;
		//DefaultMutableTreeNode child = null;
		Vector<PMesh> meshes = theScene.objects;
		PMesh mesh = null;
		//PMesh.SurfCell surf = null;
		for(int i = 0; i < meshes.size(); i++)
		{
			mesh = (PMesh) meshes.get(i);
			object = new DefaultMutableTreeNode(mesh);
			top.add(object);
			for(int j = 0; j < mesh.materials.length; j++)
			{
				material=new DefaultMutableTreeNode(new MaterialNode(mesh,j));
				object.add(material);
			} // end for
		} // end for
	} // end method calcaulteNodes

	public void updateLevels()
	{
		if(currentMode==EDIT_MODE || currentMode==ADD_MODE)
		{
			matLevels.updateLevels(materials[EDIT]);
			shinySlider.setValue((int)(materials[EDIT].shiny));
			shinyLabel.setText("Shiny Level: "+Double.toString(materials[EDIT].shiny));
			doubleSidedCheckbox.setSelected(materials[EDIT].doubleSided);
			IORField.setText(Double.toString(materials[EDIT].refractiveIndex));
			EnableMaterialWidgets(true);
		} // end if
		else
		{
			matLevels.updateLevels(null);
			shinySlider.setValue(0);
		} // end else
	} // end method updateLevels

	public void updatePreview()
	{
		previewCanvas.refresh();
		editCanvas.refresh();
		theScene.refreshCanvas();
	} // end method updatePreview

	public void recalculateMaterialTree()
	{
		top.removeAllChildren();
		treeModel.reload();
		calculateNodes(top);
		setMode(SELECTION_MODE);
		previewCanvas.material=materials[BLACK];
		editCanvas.material=materials[BLACK];
		addMaterialButton.setEnabled(false);
		editMaterialButton.setEnabled(false);
	} // end method recalculateMaterialTree

	public void resetPreview()//needed when an object is deleted from objectPanel
	{
		previewCanvas.material=materials[BLACK];
		editCanvas.material=materials[BLACK];
		updateLevels();
		updatePreview();
	} // end method resetPreview

	public void setMode(int mode)
	{
		currentMode=mode;
		if(mode==EDIT_MODE || mode==ADD_MODE)
		{
			renameButton.setEnabled(false);
			addMaterialButton.setEnabled(false);
			editMaterialButton.setEnabled(false);
			applyButton.setEnabled(true);
			cancelButton.setEnabled(true);
			//viewSelectedCheckbox.setEnabled(false); when it's implemented
			shinySlider.setEnabled(true);
			mtlFileNameLabel.setEnabled(true);
			matLevels.setEnabled(true);
			//shinyBorder.setTitleColor(Color.BLACK);
			//shinyPanel.updateUI();
			previewBorder.setTitleColor(Color.GRAY);
			previewCanvas.material=materials[BLACK];
			previewPanel.updateUI();
			if(mode==EDIT_MODE)
			{
				addEditPrevPanel.setBorder(editBorder);
				addEditPrevPanel.updateUI();
			} // end if
			else
			{
				renameButton.setEnabled(false);
				addEditPrevPanel.setBorder(addBorder);
				addEditPrevPanel.updateUI();
			} // end else
			tree.setEnabled(false);
		} // end if
		else if(mode==SELECTION_MODE)
		{
			tree.setEnabled(true);
			renameButton.setEnabled(false);
			applyButton.setEnabled(false);
			cancelButton.setEnabled(false);
			//viewSelectedCheckbox.setEnabled(true); when it's implemented
			shinySlider.setEnabled(false);
			matLevels.setEnabled(false);
			previewCanvas.material=materials[BLACK];
			editCanvas.material=materials[BLACK];
			updatePreview();
			previewBorder.setTitleColor(Color.GRAY);
			previewPanel.updateUI();
			addEditPrevPanel.setBorder(addEditBorder);
			addEditPrevPanel.updateUI();
			//shinyBorder.setTitleColor(Color.GRAY);
			//shinyPanel.updateUI();
		} // end if
		else if(mode==SELECTED_MODE)
		{
			Object nodeInfo = node.getUserObject();
			MaterialNode matNode = (MaterialNode)nodeInfo;
			previewCanvas.material=matNode.mesh.materials[matNode.index];

			renameButton.setEnabled(true);
			addMaterialButton.setEnabled(true);
			editMaterialButton.setEnabled(true);
			applyButton.setEnabled(false);
			cancelButton.setEnabled(false);
			//viewSelectedCheckbox.setEnabled(true); when it's implemented
			shinySlider.setEnabled(false);
			matLevels.setEnabled(false);
			EnableMaterialWidgets(false);
			//previewCanvas.material=materials[BLACK];
			editCanvas.material=materials[BLACK];
			updatePreview();
			previewBorder.setTitleColor(Color.GRAY);
			previewPanel.updateUI();
			addEditPrevPanel.setBorder(addEditBorder);
			addEditPrevPanel.updateUI();
			//shinyBorder.setTitleColor(Color.GRAY);
			//shinyPanel.updateUI();
			tree.setEnabled(true);
		} // end if
		else
			System.out.println("Error with mode selection in MaterialEditor");
	} // end method setMode

	public void updateMaterialFromMaterial(MaterialCell target, MaterialCell source)
	{
		//for(int i=0; i<3; i++)
		//{
			target.ka=source.ka;
			target.ks=source.ks;
			target.kd=source.kd;
			target.emmColor=source.emmColor;
		//} // end for
		target.shiny=source.shiny;
		target.doubleSided=source.doubleSided;
	} // end method updateMaterialFromMaterial

} // end class MaterialEditor

///////////////////////////////////////////////////////////////////////////////
// MaterialCanvas Class

/**
 * This class provides a canvas that can be used to preview a material applied
 * to a sphere.
 *
 * @author Ryan Mauer
 * @version 6-Mar-2005
 */
class MaterialCanvas implements GLEventListener
{
	public GLCanvas canvas;
	public MaterialCell material;
	public Light light;
	public MaterialCanvas(int width, int height)
	{
	    GLProfile.initSingleton(true);
    	GLProfile glp;
    	if(GLProfile.isGL3Available()){
    		System.out.printf("GL3 is available");
    		glp = GLProfile.get("GL2");
    	}
    	else{
    		System.out.printf("GL3 is NOT available\n");
            glp = GLProfile.getDefault();    	
        }
		GLCapabilities capabilities = new GLCapabilities(glp);
		//canvas = GLDrawableFactory.getFactory().createGLCanvas(capabilities);
		canvas = new GLCanvas(capabilities);
		canvas.setSize(width, height);
		canvas.addGLEventListener(this);
	} // end constructor

	public void display(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();
//		GLU glu = drawable.getGLU();
		GLU glu = new GLU();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustum(-4.0,4.0,-4.0,4.0,9,100);

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(0,10,10,0,0,0,0,1,0);

		// Use the current material
		gl.glMaterialfv(GL_FRONT, GL_AMBIENT, material.ka.toFloatv(), 0 );
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE, material.kd.toFloatv(),0 );
		gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, material.ks.toFloatv(),0 );
		gl.glMaterialf(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, (float)material.shiny);

		glu.gluSphere(glu.gluNewQuadric(), 4.0, 20, 20);
	} // end method display

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { }
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		GL gl = drawable.getGL();
		//GLU glu = drawable.getGLU();
		//GLU glu = new GLU();
		
		// Reset The Current Viewport And Perspective Transformation
		gl.glViewport(0, 0, width, height);
	} // end method reshape

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		//GLU glu = drawable.getGLU();
		GLU glu = new GLU();
		
		gl.glViewport(0, 0, 200, 200);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		gl.glFrontFace(GL_CCW);
		gl.glCullFace(GL_BACK);
		gl.glEnable(GL_CULL_FACE);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_NORMALIZE);
		gl.glEnable(GL_LIGHTING);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustum(-4.0,4.0,-4.0,4.0,9,100);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		setupLight(gl,glu);
		gl.glMatrixMode(GL_MODELVIEW);
	} // end method init

	public void refresh() {
		canvas.display();
	} // end method refresh

	public void setupLight(GL2 gl, GLU glu)
	{
		float globalAmbientLight[] = {0.3f, 0.3f, 0.3f, 1.0f};
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(0,10,10,0,0,0,0,1,0);

		gl.glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, 1);
		gl.glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, 1);
		gl.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, globalAmbientLight,0);

		light = new Light();
		light.lightName = GL_LIGHT0;
		light.lightSwitch = Light.ON;
		light.location = Light.LOCAL;
		gl.glLightfv(GL_LIGHT0, GL_SPECULAR, light.specular,0);
		gl.glLightfv(GL_LIGHT0, GL_AMBIENT, light.ambient,0);
		gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, light.diffuse,0);
		light.position[0] = 10.0f;
		light.position[1] = 10.0f;
		light.position[2] = 50.0f;
		light.position[3] = 1.0f;
		gl.glLightfv(GL_LIGHT0, GL_POSITION, light.position,0);
		gl.glEnable(GL_LIGHT0);
	} // end method setupLight
	public void dispose(GLAutoDrawable drawable) {

	}
} // end class MaterialCanvas
