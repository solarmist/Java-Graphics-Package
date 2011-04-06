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
	public boolean hit(Ray r, double tmin, double tmax, double time,
			HitRecord record) {
	    Double3D temp = new Double3D(r.origin().x - center.x,
	    							 r.origin().y - center.y,
	    							 r.origin().z - center.z);
	    
	    double a = r.direction().dot(r.direction());
	    double b = 2 * r.direction().dot(temp);
	    double c = temp.dot(temp) - radius * radius;
	    
	    double discriminant = b * b - 4 * a * c;
	    
	    //First check to see if the ray intersects the sphere
	    if(discriminant > 0){
	    	discriminant = Math.sqrt(discriminant);
	        double t = (- b - discriminant) / (2 * a);
	        
	        //Now check for a valid interval
	        if(t < tmin)
	        	t = (- b + discriminant) / (2 * a);
	        if(t < tmin || t > tmax)
	        	return false;
	        
	        //We have a valid hit
	        record.t = t;
	        record.normal = new Double3D(r.origin().x + t * r.direction().x - center.x,
	        							 r.origin().y + t * r.direction().y - center.y,
	        							 r.origin().z + t * r.direction().z - center.z);
	        record.normal.unitize();
	        //record.color = color;
	        
	        return true;
	    }
	    return false;
	}


	@Override
	public boolean shadowHit(Ray r, double tmin, double tmax, double time,
			HitRecord record) {
		Double3D temp = new Double3D(r.origin().x - center.x,
				r.origin().y - center.y,
				r.origin().z - center.z);

		double a = r.direction().dot(r.direction());
		double b = 2 * r.direction().dot(temp);
		double c = temp.dot(temp) - radius * radius;

		double discriminant = b * b - 4 * a * c;

		//First check to see if the ray intersects the sphere
		if(discriminant > 0){
			discriminant = Math.sqrt(discriminant);
			double t = (- b - discriminant) / (2 * a);

			//Now check for a valid interval
			if(t < tmin)
				t = (- b + discriminant) / (2 * a);
			if(t < tmin || t > tmax)
				return false;

			//We have a valid hit
			return true;
		}
		return false;	
	}

} // end class Sphere
