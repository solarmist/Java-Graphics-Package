package EWUPackage.raytracer;

import EWUPackage.scene.primitives.*;
/*
 * Ray class
 * 
 * @author Joshua Olson
 * @version	1-Apr-2011
 */

public class Ray{

	//public Double3D data[];
	public Double3D origin;
	public Double3D dir;

	//Stack of rays to get back to original n
	Refract r = null;
	
	class Refract
	{
		public double n = 1.0;
		public int objectNum = -1;
		public Refract prevR = null;
	}
	
    public Ray(){
    	this.origin = new Double3D();
    	this.dir = new Double3D();
    	this.r = new Refract();
	}
    
	public Ray(Double3D a, Double3D b){
		this.dir = new Double3D();
		this.origin = new Double3D();
    	this.origin = a;
    	this.dir = b.getUnit();
    	this.r = new Refract();
	}
	
	/*public Ray(Double3D a, Double3D b){
		this.dir = new Double3D();
		this.origin = new Double3D();
    	this.origin = a;
    	this.dir = b.getUnit();
    }
	
	/*public Ray(Double3D a, Double3D b){
		this.dir = new Double3D();
		this.origin = new Double3D();
    	this.origin = a;
    	this.dir = b;
    	n = _n;
    	nt = _nt;
    	inside = _inside;
	}*/
	
	Double3D pointAtParameter(double t){
		return new Double3D(origin.x + t * dir.x,
				origin.y + t * dir.y,
				origin.z + t * dir.z);
	}
    
	public String toString(){
		return '[' + origin.toString() +  " + t" + dir.toString() + ']';
	} 
}
