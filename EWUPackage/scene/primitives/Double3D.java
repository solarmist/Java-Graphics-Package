package EWUPackage.scene.primitives;

import java.lang.Math;
import java.io.*;
import java.text.DecimalFormat;

/**
 * This class provides a vector of three single-precision floating point values
 * along with common vector operations.
 */
public class Double3D implements Serializable
{
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	public double z;

	/**
	 * This constructor creates a default Double3D with all three components set
	 * to zero.
     */
	public Double3D()
	{
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	} // end constructor

	/**
	 * This constructor creates a Double3D with the same component values as the
	 * parameter.
     */
	public Double3D(Double3D from)
	{
		x = from.x;
		y = from.y;
		z = from.z;
	} // end constructor

	/**
	 * This constructor creates a Double3D with the specified component values.
     */
	public Double3D(double nX, double nY, double nZ)
	{
		x = nX;
		y = nY;
		z = nZ;
	} // end constructor

	/**
	 * This constructor creates a Double3D with the same values as the specified
	 * Double3D except using single instead of double precision.
	 
	public Double3D(Double3D from)
	{
		x = from.x;
		y = from.y;
		z = from.z;
	} // end constructor
*/
	/**
	 * This method returns the sum of two Double3Ds.
     */
	public boolean equals(Double3D other){
		if ( x == other.x && y == other.y && z == other.z)
			return true;
		else 
			return false;
	}
	public Double3D plus(Double3D t1)
	{
		Double3D ans = new Double3D();

		ans.x = x + t1.x;
		ans.y = y + t1.y;
		ans.z = z + t1.z;

		return ans;
	} // end plus

	/**
	 * This method returns the difference of two Double3Ds.
     */
	public Double3D minus(Double3D t1)
	{
		Double3D ans = new Double3D();

		ans.x = x - t1.x;
		ans.y = y - t1.y;
		ans.z = z - t1.z;

		return ans;
	} // end minus

	/**
	 * This method returns the cross product of this Double3D with the paramter.
     */
	public Double3D cross(Double3D t1)
	{
		Double3D ans = new Double3D();

		ans.x = (y)*(t1.z) - (t1.y)*(z);
		ans.y = (z)*(t1.x) - (t1.z)*(x);
		ans.z = (x)*(t1.y) - (t1.x)*(y);

		return ans;
	} // end cross

	/**
	 * This method returns the dot product of this Double3D with the paramter.
     */
	public double dot(Double3D t1)
	{
		return (x)*(t1.x) + (y)*(t1.y) + (z)*(t1.z);
	} // end dot

	/**
	 * This method returns a new Double3D that is a scalar multiple of this Double3D.
     */
	public Double3D sMult(double s)
	{
		Double3D ans = new Double3D();

		ans.x = s*x;
		ans.y = s*y;
		ans.z = s*z;

		return ans;
	} // end sMult

	/**
	 * This method returns a new Double3D resulting from a scalar divide on this
	 * Double3D.
     */
	public Double3D sDiv(double s)
	{
		Double3D ans = new Double3D();
		ans.x = x/s;
		ans.y = y/s;
		ans.z = z/s;
		return ans;
	} // end sDiv

	/**
	 * This method normalizes this Double3D.
     */
	public void unitize()
	{
		float s =(float) Math.sqrt((double)(x*x + y*y + z*z));

		if( s > 0 )
		{
			x = x / s;
			y = y / s;
			z = z / s;
		} // end if
	} // end unitize

	/**
	 * This method returns the normalized form of this Double3D.
     */
	public Double3D getUnit()
	{
		Double3D unit = new Double3D();
		double s = Math.sqrt((double)(x*x + y*y + z*z));

		if( s > 0 )
		{
			unit.x = x / s;
			unit.y = y / s;
			unit.z = z / s;
		} // end if
		return unit;
	} // end getUnit

	/**
	 * This method pre-multiplies this Double3D by the specified matrix.
     */
	public Double3D preMultiplyMatrix(double m[ ])
	{
	// OpenGL matrices are stored in column major order NOT row major
		Double3D t = new Double3D();
		// COLUMN MAJOR code
		t.x = (float)(m[0] * x + m[4] * y + m[8] * z + m[12]);
		t.y =  (float)(m[1] * x + m[5] *y + m[9] * z + m[13]);
		t.z = (float)( m[2] * x + m[6] * y + m[10] * z + m[14]);

		// ROW MAJOR code
		/*t.x = m[0] * x + m[1] * y + m[2] * z + m[3];
		t.y = m[4] * x + m[5] *y + m[6] * z + m[7];
		t.z = m[8] * x + m[9] * y + m[10] * z + m[11];*/
		return t;
	} // end preMultiplyMatrix

	/**
	 * This method calculates and returns the distance between two Double3Ds that
	 * represent points.
	*/
	public float distanceTo(Double3D point)
	{
		Double3D newVect = this.minus(point);
		float s =(float) Math.sqrt((double)(newVect.x * newVect.x + newVect.y * newVect.y + newVect.z * newVect.z));
		return s;
	} // end distanceTo

	public String toString(){
		DecimalFormat twodig= new DecimalFormat("#.##");
		String rtn = new String("("+twodig.format(x)+", "+twodig.format(y)+", "+twodig.format(z)+")");
		return rtn;
	} // end toString

} // end class Double3D
