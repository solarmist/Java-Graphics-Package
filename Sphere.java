
/**
 * This class represents a Sphere.
 */
public class Sphere
{

	public Double3D center;
	public double radius;
	public double radiusSq;
	public PMesh theObj;

	public Sphere(Double3D cent, double rad, PMesh ownerObj)
	{
		center = cent;
		radius = rad;
		radiusSq = radius * radius;
		theObj = ownerObj;
	} // end constructor
	

	public String toString(){
		String rtn = new String("Center: "+center+" Radius "+radius);
		return rtn;
	} // end method toString

} // end class Sphere
