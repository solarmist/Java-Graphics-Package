package EWUPackage.panels.obj.material;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.Vector;

import EWUPackage.scene.*;


/**
 * This class provides a mechanism for choosing materials and applying them
 * to the surfaces of an object.
 *
 * @author Don Bushnell
 * @version 6-Mar-2005
 */
public class MaterialChooser extends JDialog
{
	private static final long serialVersionUID = 1L;
	boolean validObj = false, validMat = false;
	Scene theScene;
	JTree treeL, treeR;
	DefaultMutableTreeNode topL, topR, nodeL, nodeR;
	DefaultTreeModel treeModelL, treeModelR;
	JButton assignButton;
	PMesh  selectedObject;
	MaterialEditor editor;

	public MaterialChooser(Scene aScene,final MaterialEditor editor)
	{
		theScene=aScene;
		this.editor=editor;
		Container contentPane = getContentPane();
		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());
		setTitle("Materials Chooser");

		//LEFT SCROLL PANE WITH TREE
		////////////////////////////////////////////////////////////////////////////////////////////////

		topL = new DefaultMutableTreeNode("Surfaces by Object");
	//	calculateSurfaceNodes(topL);
		treeModelL = new DefaultTreeModel(topL);
		treeL = new JTree(treeModelL);
		treeL.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeL.setShowsRootHandles(true);
		treeL.setRootVisible(false);
		treeL.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e)
			{
				nodeL = (DefaultMutableTreeNode)treeL.getLastSelectedPathComponent();
				if (nodeL == null || nodeL == topL || !nodeL.isLeaf())
				{
					validObj=false;
					assignButton.setEnabled(false);
					selectedObject=null;
					return;
				}

				Object nodeInfo = nodeL.getUserObject();
				SurfaceNode surfNode= null;

				if (nodeL.isLeaf() && nodeL != topL)
				{
					if(nodeInfo instanceof SurfaceNode)
					{
						validObj=true;
						if(validMat && validObj)
							assignButton.setEnabled(true);
						surfNode= (SurfaceNode)nodeInfo;
						if(selectedObject != surfNode.mesh)
						{
							selectedObject=surfNode.mesh;
							calculateAvailableMaterialTree();
						}
					}
					else
						assignButton.setEnabled(false);
				}
				return;
			}
		});

		JScrollPane treeScrollPaneL = new JScrollPane(treeL);
		treeScrollPaneL.setBorder(new TitledBorder("Surfaces by Object"));
		treeScrollPaneL.setPreferredSize(new Dimension(300, 180));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx=0.5;
		gbc.weighty=0.5;
		gbc.fill=GridBagConstraints.BOTH;
		contentPane.add(treeScrollPaneL, gbc);

		//ASSIGN BUTTON
		/////////////////////////////////////////////////////////////////////////////////////////////
		assignButton=new JButton("Assign");
		assignButton.addMouseListener(new MouseAdapter()
				{
					public void mouseClicked(MouseEvent evt)
					{
						Object nodeInfoL = nodeL.getUserObject();
						Object nodeInfoR = nodeR.getUserObject();
						SurfaceNode surfNode = (SurfaceNode)nodeInfoL;
						MaterialNode matNode = (MaterialNode)nodeInfoR;
						//surfNode.mesh.materials[surfNode.surf.material]=matNode.mesh.materials[matNode.index];
						surfNode.surfRef.material=matNode.index; // just change the SurfCell material index, don't alter material
						surfNode.meshRef=matNode.mesh; // here so the name will update
						surfNode.surfIndex=matNode.index;
						treeL.updateUI();
						editor.updatePreview();
					}
				});
		assignButton.setEnabled(false);
		gbc.gridx=1;
		gbc.weightx=0.0;
		gbc.fill=GridBagConstraints.VERTICAL;
		contentPane.add(assignButton, gbc);

		//RIGHT SCROLL PANE WITH TREE
		///////////////////////////////////////////////////////////////////////////////////////////////
		topR = new DefaultMutableTreeNode("Available Materials by Object");
		treeModelR = new DefaultTreeModel(topR);
		treeR = new JTree(treeModelR);
		treeR.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeR.setShowsRootHandles(true);
		treeR.setRootVisible(false);
		treeR.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e)
			{
				nodeR = (DefaultMutableTreeNode)treeR.getLastSelectedPathComponent();
				if (nodeR == null || nodeR == topR)
				{
					validMat=false;
					assignButton.setEnabled(false);
					return;
				}

				Object nodeInfo = nodeR.getUserObject();
				//MaterialNode matNode= null;
				//SurfaceNode surfNode= null;

				if (nodeR.isLeaf() && nodeR != topR)
				{
					if(nodeInfo instanceof MaterialNode)
					{
						validMat=true;
						if(validMat && validObj)
							assignButton.setEnabled(true);
					}
					else
					{
						validMat=false;
						assignButton.setEnabled(false);
					}

				}
				return;
			}
		});
		JScrollPane treeScrollPaneR = new JScrollPane(treeR);
		treeScrollPaneR.setPreferredSize(new Dimension(200, 180));
		treeScrollPaneR.setBorder(new TitledBorder("Materials Available by Object"));
		gbc.gridx = 2;
		gbc.weightx= 0.5;
		gbc.fill=GridBagConstraints.BOTH;
		gbc.fill=GridBagConstraints.BOTH;
		contentPane.add(treeScrollPaneR, gbc);
		pack();
	}
	/////////////////////////////////////////////////////////////////////////////////////////////
	//END CONSTRUCTOR

	/**
	 * Takes given node and puts a tree of the object surfaces under it.
	 */
	public void calculateSurfaceNodes(DefaultMutableTreeNode top)
	{
		DefaultMutableTreeNode object = null;
		DefaultMutableTreeNode surfaces = null;
		//DefaultMutableTreeNode material = null;
		//DefaultMutableTreeNode child = null;
		Vector<PMesh> meshes = theScene.objects;
		PMesh mesh = null;
		PMesh.SurfCell surf = null;
		for(int i = 0; i < meshes.size(); i++)
		{
			mesh = meshes.get(i);
			object = new DefaultMutableTreeNode(mesh);
			top.add(object);
			surf = mesh.surfHead;
			int s = 0;
			while (surf != null) {
				surfaces = new DefaultMutableTreeNode(new SurfaceNode(s, mesh,
					surf)); //will calculate the toString based off of these arguments
				object.add(surfaces);
				surf = surf.next;
				s++;
			} // end while
		} // end for
		treeL.expandPath(new TreePath(topL));
	} // end method calculateSurfaceNodes

	public void calculateAvailableMaterialTree()
	{
		topR.removeAllChildren();
		treeModelR.reload();

		if(selectedObject != null)
		{
			//DefaultMutableTreeNode object = null;
			DefaultMutableTreeNode material = null;
			//DefaultMutableTreeNode surfaces = null;
			//DefaultMutableTreeNode child = null;
			//PMesh.SurfCell surf = null;

			for(int j = 0; j<selectedObject.materials.length; j++)
			{
				material=new DefaultMutableTreeNode(new MaterialNode(selectedObject,j));
				topR.add(material);
			}
		}
		treeR.validate();
		treeR.updateUI();
	}

	public void recalculateMaterialTree()
	{
		topL.removeAllChildren();
		treeModelL.reload();
		calculateSurfaceNodes(topL);

		topR.removeAllChildren();
		treeModelR.reload();

		validObj=validMat=false;
	}

	public class SurfaceNode//Will hold all the material and surface information for surfaces in the tree
	{
		//String treeText = null;
		PMesh.SurfCell surfRef=null;
		PMesh meshRef=null;
		int surfIndex=0;
		int index=0;
		PMesh.SurfCell surf = null;
		PMesh mesh = null;

		SurfaceNode(int i,PMesh meshRef ,PMesh.SurfCell surfRef)
		{
			this.surfRef=surfRef;
			this.meshRef = meshRef;
			index=i;
			//treeText=("surface:"+i+" using material:"+surfRef.material+" "+meshRef.materials[surfRef.material]);
			surf = surfRef;
			mesh = meshRef;
		}
		public String toString()//Needed for DefaultMutableTreeNode
		{
			return "surface:"+index+" material: "+meshRef.materials[surfRef.material];
		}
	}

	public class MaterialNode//Will hold all the material information in the tree based on PMesh.materials[]
	{
		PMesh mesh = null;
		int index = 0;

		MaterialNode(PMesh tmpMesh, int i)
		{
			mesh=tmpMesh;
			index=i;
		}

		public String toString()//Needed for DefaultMutableTreeNode
		{
			return (index+" "+mesh.materials[index].materialName);
		}
	}

	public void updateMaterialFromMaterial(MaterialCell target, MaterialCell source)
	{
		target.ka=source.ka;
		target.ks=source.ks;
		target.kd=source.kd;
		target.emmColor=source.emmColor;
		target.reflectivity = source.reflectivity;
		target.refractivity = source.refractivity;
		target.refractiveIndex = source.refractiveIndex;
		target.transmissionFilter = source.transmissionFilter;
		target.lineColor = source.lineColor;
		//target.reflOneValue = source.reflOneValue;
		//target.refrOneValue = source.refrOneValue;

		target.shiny=source.shiny;
		target.doubleSided=source.doubleSided;
	}

} // end class MaterialChooser
