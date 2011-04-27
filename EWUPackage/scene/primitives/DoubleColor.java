package EWUPackage.scene.primitives;

import java.io.*;
import java.text.DecimalFormat;
import java.awt.*;

//import RayTracer.ShadeColor;

/**
 * This class provides a vector of three single-precision floating point values
 * along with common vector operations.
 */
public class DoubleColor implements Serializable
{
	private static final long serialVersionUID = 1L;
	public double r;
	public double g;
	public double b;
	public double a;

	/**
	 * This constructor creates a default Double3D with all three components set
	 * to zero.
     */
	public DoubleColor()
	{
		r = 0.0f;
		g = 0.0f;
		b = 0.0f;
		a = 1.0;
	} // end constructor

	/**
	 * This constructor creates a Double3D with the same component values as the
	 * parameter.
     */
	public DoubleColor(DoubleColor from)
	{
		r = from.r;
		g = from.g;
		b = from.b;
		a = from.a;

	} // end constructor

	/**
	 * This constructor creates a Double3D with the specified component values.
     */
	public DoubleColor(double nR, double nG, double nB, double nA)
	{
		r = nR;
		g = nG;
		b = nB;
		a = nA;
	} // end constructor
	
	public DoubleColor(Color javaColor){
		float [] colors = new float[4];
		colors = javaColor.getComponents(null);
		r = (double)colors[0];
		g = (double)colors[1];
		b = (double)colors[2];
		a = (double)colors[3];
		
	}
	public void plus(DoubleColor other){
		r = r + other.r;
		g = g + other.g;
		b = b + other.b;
	}
	
	public void scale(double scaleValue){
		r *=scaleValue;
		g *= scaleValue;
		b *= scaleValue;
	}	
	public float [] toFloatv(){
		float [] rtn = new float[4];
		rtn[0] = (float)r;
		rtn[1] = (float)g;
		rtn[2] = (float)b;
		rtn[3] = (float)a;
		return rtn;
	}
	
	public String toString(){
		DecimalFormat twodig= new DecimalFormat("#.##");
		String rtn = new String(twodig.format(r)+"  "+twodig.format(g)+"  "+twodig.format(b));
		return rtn;
	} // end toString
}