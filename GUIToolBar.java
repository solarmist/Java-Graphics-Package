import javax.swing.*;
import java.awt.*;

/**
 *
 * @author  Ryan Mauer
 * @version 23-Jan-2005
 */

public class GUIToolBar extends JToolBar
{
    public CS570 app;

    public GUIToolBar(CS570 app)
    {
        this.app = app;
        this.setFloatable(false);
        this.setBorder(BorderFactory.createEtchedBorder());
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
		this.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        init();

    } // end constructor

    public void init()
    {
        // Init buttons here
        JButton test = new JButton(" ");

        // Add buttons here
        add(test);

    } // end method init

    // Internal Class
    class ItemSeparator extends JSeparator {
        public ItemSeparator() {
            super(JSeparator.VERTICAL);
            this.setPreferredSize(new Dimension(2, 25));
            this.setMinimumSize(new Dimension(2, 25));
            this.setBackground(Color.white);
            this.setForeground(Color.gray);
        } // end constructor
    } // end internal class ItemSeparator

} // end class GUIToolBar
