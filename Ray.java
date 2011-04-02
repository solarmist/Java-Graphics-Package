import java.text.DecimalFormat;

/**
 * 
 */

/*
 * Ray class
 * 
 * @author Joshua Olson
 *
 */
//
//  Ray.h
//  RayTracer
//
//  Created by Joshua Olson on 3/16/11.
//  Copyright 2011 solarmist. All rights reserved.
//

public class Ray{

	public Double3D data[];
	
    public Ray(){
    	this.data = new Double3D[2];
	}
    
	public Ray(Double3D a, Double3D b){
		this.data = new Double3D[2];
    	this.data[0] = a; 
    	this.data[1] = b;
	}
	
	Double3D origin()  {
		return data[0];
	}
	Double3D direction(){
		return data[1];
	}
	Double3D pointAtParameter(float t){
		return new Double3D(data[0].x + t * data[1].x,
					data[0].y + t * data[1].y,
					data[0].z + t * data[1].z);
	}
    
	public String toString(){
		return '[' + data[0].toString() +  " + t" + data[1].toString() + ']';
	} // end toString
}