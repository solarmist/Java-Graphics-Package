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
	public double n = 1.0;
	public double nt = 1.0;
	public boolean inside = false;
	
    public Ray(){
    	this.origin = new Double3D();
    	this.dir = new Double3D();
	}
    
	public Ray(Double3D a, Double3D b){
		this.dir = new Double3D();
		this.origin = new Double3D();
		//this.data = new Double3D[2];
    	//this.data[0] = a; 
    	//this.data[1] = b;
    	this.origin = a;
    	this.dir = b;
	}
	
	public Ray(Double3D a, Double3D b, double _n){
		this.dir = new Double3D();
		this.origin = new Double3D();
		//this.data = new Double3D[2];
    	//this.data[0] = a; 
    	//this.data[1] = b;
    	this.origin = a;
    	this.dir = b;
    	n = _n;
    }
	
	public Ray(Double3D a, Double3D b, double _n, double _nt, boolean _inside){
		this.dir = new Double3D();
		this.origin = new Double3D();
		//this.data = new Double3D[2];
    	//this.data[0] = a; 
    	//this.data[1] = b;
    	this.origin = a;
    	this.dir = b;
    	n = _n;
    	nt = _nt;
    	inside = _inside;
	}
	
	Double3D pointAtParameter(double t){
		return new Double3D(origin.x + t * dir.x,
				origin.y + t * dir.y,
				origin.z + t * dir.z);
	}
    
	public String toString(){
		return '[' + origin.toString() +  " + t" + dir.toString() + ']';
	} 
}
