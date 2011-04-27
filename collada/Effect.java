package collada;

import java.util.*;

/**
 * <p>Title: Effect</p>
 *
 * <p>Description: Holds the effect properties</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Effect {

//used for the library_effects
    public String id;
    public String name;
    public float emission[];
    public float ambient[];
    public float diffuse[];
    public float specular[];
    public float shininess;
    public float reflective[];
    public float reflectivity;
    public float transparent[];
    public float transparency;
    public float indexOfRefraction;
    public ArrayList<Newparam> newparam;
    

    public Texture emissionTexture;
    public Texture ambientTexture;
    public Texture diffuseTexture;
    public Texture specularTexture;
    public Texture reflectiveTexture;
    public Texture transparentTexture;
    


    public Effect() {
    	
        emission = new float[4];
        ambient = new float[4];
        diffuse = new float[4];
        specular = new float[4];
        reflective = new float[4];
        transparent = new float[4];
        newparam = new ArrayList<Newparam>();
        
        emissionTexture = new Texture();
        ambientTexture = new Texture();
        diffuseTexture = new Texture();
        specularTexture = new Texture();
        reflectiveTexture = new Texture();
        transparentTexture = new Texture();
        
        

    }
}
