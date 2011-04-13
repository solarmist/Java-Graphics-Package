/*
 * Structure for passing data to and from the ray intersection methods
 * 
 * @author Joshua Olson
 * @version	1-Apr-2011
 */

public class HitRecord {
	    double t;
	    int index = -1;			//Which object did it hit
	    int depth = 0;
	    Double3D normal;
	    DoubleColor color;
	    Double3D uv;       	//We will use this for 2d textures
	    Double3D hitP;      //The point of intersection
	    //Texture* hitTex;	//The nearest intersected object's texture
}
