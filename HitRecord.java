/*
 * Structure for passing data to and from the ray intersection methods
 * 
 * @author Joshua Olson
 * @version	1-Apr-2011
 */

public class HitRecord {
	    double t;
	    Double3D normal;
	    DoubleColor color;
	    Double3D uv;         //We will use this for 2d textures
	    Double3D hit_p;      //The point of intersection
	    //Texture* hit_tex;   //The nearest intersected object's texture
}
