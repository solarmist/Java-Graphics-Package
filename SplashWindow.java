
/*
 * @(#)SplashWindow.java 1.3 2003-06-01
 *
 * Copyright (c) 1999-2003 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland
 * All rights reserved.
 *
 * This material is provided "as is", with absolutely no warranty expressed
 * or implied. Any use is at your own risk.
 *
 * Permission to use or copy this software is hereby granted without fee,
 * provided this copyright notice is retained on all copies.
 */
import java.awt.*;
import java.awt.event.*;

/**
 * Splash Window to show an image during startup of an application.<p>
 *
 * Usage:
 * <pre>
 * // open the splash window
 * Frame splashOwner = SplashWindow.splash(anImage);
 *
 * // start the application
 * // ...
 *
 * // dispose the splash window by disposing the frame that owns the window.
 * splashOwner.dispose();
 * </pre>
 *
 * <p>To use the splash window as an about dialog write this:
 * <pre>
 *  new SplashWindow(
 *      this,
 *      getToolkit().createImage(getClass().getResource("splash.png"))
 * ).show();
 * </pre>
 *
 * The splash window disposes itself when the user clicks on it.
 *
 * @author Werner Randelshofer, Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * @version 1.3 2003-06-01 Revised.
 */
public class SplashWindow extends Window {
    public Image splashImage;
	public String text; // added for text support
	public int textX, textY; // text coordinates
    public Color textColor;

    /**
     * This attribute indicates whether the method
     * paint(Graphics) has been called at least once since the
     * construction of this window.<br>
     * This attribute is used to notify method splash(Image)
     * that the window has been drawn at least once
     * by the AWT event dispatcher thread.<br>
     * This attribute acts like a latch. Once set to true,
     * it will never be changed back to false again.
     *
     * @see #paint
     * @see #splash
     */
    public boolean paintCalled = false;

    /**
     * Constructs a splash window and centers it on the
     * screen. The user can click on the window to dispose it.
     *
     * @param   owner       The frame owning the splash window.
     * @param   splashImage The splashImage to be displayed.
     */
    public SplashWindow(Frame owner, Image splashImage,
                        String text, int textX, int textY, Color textColor) {
        super(owner);
        this.splashImage = splashImage;
		this.text = text;
		this.textX = textX;
		this.textY = textY;
		this.textColor = textColor;

        // Load the image
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(splashImage,0);
        try {
            mt.waitForID(0);
        } catch(InterruptedException ie){}

        // Center the window on the screen.
        int imgWidth = splashImage.getWidth(this);
        int imgHeight = splashImage.getHeight(this);
        setSize(imgWidth, imgHeight);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
            (screenDim.width - imgWidth) / 2,
            (screenDim.height - imgHeight) / 2
        );

        // Users shall be able to close the splash window by
        // clicking on its display area. This mouse listener
        // listens for mouse clicks and disposes the splash window.
        MouseAdapter disposeOnClick = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Note: To avoid that method splash hangs, we
                // must set paintCalled to true and call notifyAll.
                // This is necessary because the mouse click may
                // occur before the contents of the window
                // has been painted.
                synchronized(SplashWindow.this) {
                    SplashWindow.this.paintCalled = true;
                    SplashWindow.this.notifyAll();
                }
                dispose();
            }
        };
        addMouseListener(disposeOnClick);
    }

    /**
     * Updates the display area of the window.
     */
    public void update(Graphics g) {
        // Note: Since the paint method is going to draw an
        // image that covers the complete area of the component we
        // do not fill the component with its background color
        // here. This avoids flickering.
        g.setColor(getForeground());
        paint(g);
    }
    /**
     * Paints the image on the window.
     */
    public void paint(Graphics g) {
        g.drawImage(splashImage, 0, 0, this);
		g.setColor(textColor); // Display colored text
        g.drawString(text, textX, textY); // draw text

        // Notify method splash that the window
        // has been painted.
        // Note: To improve performance we do not enter
        // the synchronized block unless we have to.
        if (! paintCalled) {
            paintCalled = true;
            synchronized (this) { notifyAll(); }
        }
    }

	public void updateText(String str) {
		text = str;
		this.paint(this.getGraphics());
    } // end method updateText

    /**
     * Constructs and displays a SplashWindow.<p>
     * This method is useful for startup splashs.
     * Dispose the return frame to get rid of the splash window.<p>
     *
     * @param   splashImage The image to be displayed.
     * @return  Returns the SplashWindow.
     */
    public static SplashWindow splash(Image splashImage, String text, int textX, int textY, Color textColor) {
        Frame f = new Frame();
        SplashWindow w = new SplashWindow(f, splashImage, text, textX, textY, textColor);

        // Show the window.
        w.toFront();
        w.show();

        // Note: To make sure the user gets a chance to see the
        // splash window we wait until its paint method has been
        // called at least by the AWT event dispatcher thread.
        if (! EventQueue.isDispatchThread()) {
            synchronized (w) {
                while (! w.paintCalled) {
                    try { w.wait(); } catch (InterruptedException e) {}
                }
            }
        }
        return w;
    }
}
