package EWUPackage.scene;

import EWUPackage.scene.primitives.*;

/**
 * This class represents a Sphere.
 */
public class Sphere implements Shape
{

	public Double3D center;
	public double radius;
	public double radiusSq;
	public PMesh theObj;

	public Sphere()
	{
		center = null;
		radius = 0;
		radiusSq = radius * radius;
		theObj = null;
	} // end constructor
	
	public Sphere(Double3D cent, double rad, PMesh ownerObj)
	{
		center = cent;
		radius = rad;
		radiusSq = radius * radius;
		theObj = ownerObj;
	} // end constructor
	
	public Sphere(Sphere from)
	{
		center = from.center;
		radius = from.radius;
		radiusSq = from.radiusSq;
		theObj = from.theObj;
	} // end constructor

	public String toString(){
		String rtn = new String("Center: " + center.toString() + " Radius " + radius);
		return rtn;
	} // end method toString

	@Override
	public boolean hit(Ray r, double tMin, double tMax, double time,
			HitRecord record) 
	{
	    Double3D temp = new Double3D(r.origin.x - center.x,
	    							 r.origin.y - center.y,
	    							 r.origin.z - center.z);
	    
	    double a = r.dir.dot(r.dir);
	    double b = 2 * r.dir.dot(temp);
	    double c = temp.dot(temp) - radius * radius;
	    
	    double discriminant = b * b - 4 * a * c;
	    
	    //First check to see if the ray intersects the sphere
	    if(discriminant > 0)
	    {
	    	discriminant = Math.sqrt(discriminant);
	        double t = (- b - discriminant) / (2 * a);
	        
	        //Now check for a valid interval
	        if(t < tMin)
	        	t = (- b + discriminant) / (2 * a);
	        if(t > tMax) //t < tMin || don't need this
	        	return false;
	        
	        //We have a valid hit
	        record.t = t;
	        record.hitP = r.pointAtParameter(t);
	        record.normal = record.hitP.minus(center).getUnit();

	        //record.color = color;
	        
	        return true;
	    }
	    return false;
	}


	@Override
	public boolean shadowHit(Ray r, double tMin, double tMax, double time) 
	{
		Double3D temp = new Double3D(r.origin.x - center.x,
				r.origin.y - center.y,
				r.origin.z - center.z);

		double a = r.dir.dot(r.dir);
		double b = 2 * r.dir.dot(temp);
		double c = temp.dot(temp) - radius * radius;

		double discriminant = b * b - 4 * a * c;

		//First check to see if the ray intersects the sphere
		if(discriminant > 0){
			discriminant = Math.sqrt(discriminant);
			double t = (- b - discriminant) / (2 * a);

			//Now check for a valid interval
			if(t < tMin)
				t = (- b + discriminant) / (2 * a);
			if(t < tMin || t > tMax)
				return false;

			//We have a valid hit
			return true;
		}
		return false;	
	}

} // end class Sphere
