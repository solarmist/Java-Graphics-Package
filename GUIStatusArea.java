import javax.swing.*;
import java.awt.*;

/**
 *
 * @author  Ryan Mauer
 * @version 23-Jan-2005
 */

public class GUIStatusArea extends JPanel
{
	public JLabel status; // text
	public JProgressBar pBar;

	public GUIStatusArea(int appWidth)
	{
		pBar = new JProgressBar(0,100);
		pBar.setValue(50);
		pBar.setPreferredSize(new Dimension(400, 15));
		pBar.setStringPainted(true);
		status = new JLabel("Status Label");
		this.setPreferredSize(new Dimension(800, 25));
		this.setMinimumSize(new Dimension(800, 25));
		this.setMaximumSize(new Dimension(800, 25));
		this.setBorder(BorderFactory.createEtchedBorder());
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 3));
		this.add(pBar);
		this.add(status);
		status.setHorizontalAlignment(JLabel.LEFT);
	} // end constructor

	public void showStatus(String str) {
		status.setText(str);
	} // end method showStatus

} // end class GUIStatusArea
