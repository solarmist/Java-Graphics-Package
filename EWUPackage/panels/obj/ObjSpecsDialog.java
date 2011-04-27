package EWUPackage.panels.obj;

import javax.swing.*;
import java.awt.*;

import EWUPackage.scene.*;
import EWUPackage.scene.primitives.*;

/**
 * This class provides a dialog for displaying details about a PMesh object.
 *
 * @version 1-Feb-2005
 */
public class ObjSpecsDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	JPanel objSpecsPanel;

	public ObjSpecsDialog(PMesh curObj)
	{
		Container cp = getContentPane();
		setSize(new Dimension(300,200));
		objSpecsPanel = new JPanel();
		objSpecsPanel.setLayout(new GridLayout(6, 2));

		addField("Name:", curObj.filePath+curObj.objName);
		addField("File Type:", curObj.fileType);
		addField("Number of Polygons:", String.valueOf(curObj.numPolys));
		addField("Number of Surfaces:", String.valueOf(curObj.numSurf));
		Double3D center = curObj.center;
		addField("Center:", center.toString());
		/* more detailed center
		addField("Center.x:", String.valueOf(center.x));
		addField("Center.y:", String.valueOf(center.y));
		addField("Center.z:", String.valueOf(center.z));
	    */
	    addField("Radius:", String.valueOf(curObj.boundingSphere.radius));

		cp.add(objSpecsPanel);
		setTitle("Object Specifications");
	} // end constructor

	public void addField(String fieldName, String fieldValue)
	{
		JLabel label = new JLabel(fieldName);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		objSpecsPanel.add(label);

		JTextField edit = new JTextField();
		edit.setHorizontalAlignment(JTextField.RIGHT);
		edit.setText(fieldValue);
		edit.setEnabled(false);
		objSpecsPanel.add(edit);
	} // end method addField

} // end class ObjSpecsDialog
