
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

/**
 * This class provides a way to animate the canvas whenever input indicates
 * changes should occur to the camera.  The camera movement becomes smooth
 * because the canvas is constantly refreshed while camera changes are made.
 * When no keyboard input is present, this thread is put into a suspended
 * state to avoid rendering when it's not necessary.
 *
 * @author	Ryan Mauer
 * @version	28-Nov-2004
 */
public class CameraUpdateThread extends Thread implements KeyListener, FocusListener
{
	public Scene theScene;
	public volatile boolean threadSuspended;

	public CameraUpdateThread(Scene scene)
	{
		super();
		theScene = scene;
		threadSuspended = true; // initially suspended
		theScene.canvas.addKeyListener(this);
		theScene.canvas.addFocusListener(this);

	} // end constructor

	/**
	 * This method continuously calls the canvas's display() method unless this
	 * thread is suspended. The thread is automatically put into a suspended state
	 * whenever a valid key camera update key is not being pressed on the keyboard.
	 */
	public void run()
	{
		while(true)
		{
			try
			{
				if(threadSuspended)
				{
					// System.out.println("Thread suspended");
					synchronized(this)
					{
						while(threadSuspended)
							wait();
					} // end synchronized
				} // end if
			} // end try
			catch(InterruptedException ex)
			{
				ex.printStackTrace();
			} // end catch

			// System.out.println("Setting Rendering Thread: CameraUpdateThread");
            //net.java.games.jogl.GLCanvas canvas = theScene.getCanvas();
			javax.media.opengl.awt.GLCanvas canvas = theScene.canvas;
//canvas.setRenderingThread(this); // make this the rendering thread
			canvas.display(); // display the scene
//canvas.setRenderingThread(null); // allow other threads to render
			// System.out.println("Released Rendering Thread");
		} // end while

	} // end method run

	public void keyTyped(KeyEvent e) { }

	/**
	 * This method is called when a key has been pressed.
	 *
	 * @param e	info about the key press
	 */
	public synchronized void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		if (key == 'R'){
			System.out.printf("Ray trace from key\n");
			theScene.rayTrace = true;
			theScene.refreshCanvas();
		}
		// System.out.println("Pressed: " + e.getKeyCode());
		int action = theScene.getAction(e.getKeyCode());
		boolean valid = theScene.camera.setAction(action, true);
		if( valid ) {
			if(threadSuspended) {
				// System.out.println("Unsuspending thread");
				threadSuspended = !threadSuspended;
				if (!threadSuspended) {
					notify();
				} // end if
			} // end if
		} // end if
	} // end keyPressed

	/**
	 * This method is called when a key has been released.
	 *
	 * @param e	info about the key release
	 */
	public synchronized void keyReleased(KeyEvent e)
	{
		// System.out.println("Released: " + e.getKeyCode()); // DEBUG
		int action = theScene.getAction(e.getKeyCode());
		boolean valid = theScene.camera.setAction(action, false); // was the input valid?
		if( valid ) {
			if( ! theScene.camera.hasActionsToPerform() ) {
				// System.out.println("Suspending thread");
				threadSuspended = !threadSuspended;
			} // end if
		} // end if
	} // end keyReleased

	/**
	 * This method is called when the canvas gains the keyboard focus.
	 *
	 * @param e info about the focus gain
	 */
	public void focusGained(FocusEvent e)
	{
		System.out.println("Canvas GAINED focus");

	} // end method focusGained

	/**
	 * This method is called when the canvas loses the keyboard focus.
	 *
	 * @param e info about the focus lost
	 */
	public synchronized void focusLost(FocusEvent e)
	{
		System.out.println("Canvas LOST focus");
		theScene.camera.discardAllActions(); // invalidate all current input
		if( !threadSuspended )
			threadSuspended = !threadSuspended; // suspend rendering

	} // end method focusLost

} // end class CameraUpdateThread