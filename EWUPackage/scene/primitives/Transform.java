package EWUPackage.scene.primitives;


/**
 * This class provides a way to store information about an object transform.
 * The supported transforms are: TRANSLATE, ROTATE, and SCALE.
 *
 * @version 5-Feb-2005
 */
public class Transform
{
	public int type;
 	public double x;
 	public double y;
 	public double z;
	public double angle;
	public static final int TRANSLATE = 0;
	public static final int ROTATE = 1;
	public static final int SCALE = 2;

	public void translate(double xtran, double ytran, double ztran){
		type = TRANSLATE;
		x = xtran;
		y = ytran;
		z = ztran;
	} // end method translate

	public void rotate(double rotang, double xaxis, double yaxis, double zaxis ){
		type = ROTATE;
		x = xaxis;
		y = yaxis;
		z = zaxis;
		angle = rotang;
	} // end method rotate

	public void scale(double xfact, double yfact, double zfact){
		type = SCALE;
		x = xfact;
		y = yfact;
		z = zfact;
	} // end method scale

} // end class Transform
