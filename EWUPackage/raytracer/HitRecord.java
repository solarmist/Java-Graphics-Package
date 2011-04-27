package EWUPackage.raytracer;

import EWUPackage.scene.primitives.*;
/*
 * Structure for passing data to and from the ray intersection methods
 * 
 * @author Joshua Olson
 * @version	1-Apr-2011
 */

public class HitRecord {
	    double t;
	    int index = -1;			//Which object did it hit
	    int matIndex = -1;
	
	    Double3D normal;
	    DoubleColor color;
	    Double3D uv;       	//We will use this for 2d textures
	    Double3D hitP;      //The point of intersection
	    public boolean inMat = false; //Are we inside a material and need to swap n and nt?
	    //Texture* hitTex;	//The nearest intersected object's texture
}
