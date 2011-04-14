import javax.swing.*;
import java.awt.*;

/**
 * The ONLY reason for this class is to give credit where credit is due.
 * Aanyone is free to add their name to it IF they contribute.
 * Please do not remove names from here.
 *
 * @author  Mark Haney
 * @version 5-June-2003
 */
public class AboutDialog extends JDialog
{
	public JScrollPane scroller;
	public JTextArea text;

	public AboutDialog(Frame parent)
	{
                super(parent, "About...");

		String controls = "Camera Controls:\n" +
                 "   W - move forward\n" +
                 "   A - move left\n" +
                 "   S - move backward\n" +
                 "   D - move move right\n" +
                 "   Up - look up\n" +
                 "   Down - look down\n" +
                 "   Left - look left\n" +
                 "   Right - look right\n\n";
		String credits = "Credits:\n\n" +
                "Bill Clark:\n" +
                "   Original (C++) interface design.\n" +
                "   Wrote original (C++) obj loader\n" +
                "   Rewrote Java interface to get rid\n" +
                "   of the NASTY Forte code.\n" +
                "   Wrote smooth shading code.\n" +
                "   Provided interface for students.\n\n" +
                "Ryan Mauer:\n" +
                "   Refactored the entire project,\n" +
                "   cleaning up code and improving the\n" +
                "   overall design.\n" +
                "   Added menubar, toolbar, statusarea.\n" +
                "   Wrote Camera and CameraUpdateThread.\n" +
                "   Ported to JOGL.\n\n" +
                "Mark Haney:\n" +
                "   Ported all C++ code to Java.\n" +
                "   Java interface design.\n" +
                "   Integration of mouse interface,\n" +
                "   misc tab, ThreeDSLoader, and\n" +
                "   pretty much everything everyone\n" +
                "   else wrote.\n" +
                "   Cleaned up mouse interface to\n" +
                "   work in a more intuitive fashion\n" +
                "   (i.e. translations happen in camera\n" +
                "    coordinates rather than world).\n" +
                "   Wrote camera panning code after\n" +
                "   a suggestion by Jeff Powell.\n\n" +
                "Don Bushnell:\n" +
                "   Wrote Material Editor and Material\n" +
                "   Chooser.\n\n" +
                "Bart Hunking and Bill Kvasnikoff:\n" +
                "   Wrote scene saving/loading process\n\n" +
                "Jeff Powell:\n" +
                "   Wrote 3DS Loader and helped with\n" +
                "   integration of everyone's code.\n\n" +
                "Maren Johnson and Josh Slider:\n" +
                "   Wrote object \"picking\" code.\n" +
                "   Their class also served as the place\n" +
                "   to put all object/camera transform\n" +
                "   code written by the other students.\n\n" +
                "Brent Heinz:\n" +
                "   Wrote object moving via mouse code\n\n" +
                "Jeremy Gross and Christine Talbot:\n" +
                "   Wrote camera rotation via mouse code.\n\n" +
                "Daniel Dawson:\n" +
                "   Wrote Trackball Code allowing easy\n" +
                "   object rotations using the mouse.\n\n" +
                "Russell Valentine:\n" +
                "   Wrote Fog Panel Code.\n\n";
		text = new JTextArea(controls + credits);
		text.setEditable(false);
		scroller = new JScrollPane(text);
		scroller.setPreferredSize(new Dimension(500, 500));

                setSize(500,500);
                getContentPane().setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridheight = GridBagConstraints.REMAINDER;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                getContentPane().add(scroller, gbc);
                setLocationRelativeTo(null); // center on screen

	} // end constructor

} // end class MiscPanel
