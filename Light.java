import java.io.*;

/**
 * This class provides a light to be used for lighting during rendering.
 *
 * @version 5-Feb-2005
 */
public class Light implements Serializable
{

///////////////////////////////////////////////////////////////////////////////
// STATIC VARIABLES

	/**
	 * lightSwitch constant indicating the light is OFF
	 */
	public final static int OFF = 0;

	/**
	 * lightSwitch constant indicating the light is ON
	 */
	public final static int ON = 1;

	/**
	 * location constant indicating the light is DIRECTIONAL
	 */
	public final static int DIRECTIONAL = 0;

	/**
	 * location constant indicating the light is LOCAL
	 */
	public final static int LOCAL = 1;

///////////////////////////////////////////////////////////////////////////////
// DATA MEMBERS

	/**
	 * Whether the light is ON or OFF
	 */
	public int lightSwitch;

	public float ambient[];			//R, G, B, A
	public float diffuse[];			//R, G, B, A
	public float specular[];		//R, G, B, A
	public float position[];		//X, Y, Z, W
	public float viewPos[];			//X, Y, Z, W
	public float direction[];		//X, Y, Z, W

	/**
	 * Angle for a spotlight (measured from center to edge of cone)
	 */
	public float spotCutoff;

	/**
	 * How fast the light fades from the center of the cone to the edge
	 */
	public float spotExponent;

	public float spotDirection[]; // X, Y, Z

	/**
	 * constant attenuation as a function of the distance from the source
	 */
	public float constAttenuation;

	/**
	 * linear attenuation as a function of the distance from the source
	 */
	public float linearAttenuation;

	/**
	 * quadratic attenuation as a function of the distance from the source
	 */
	public float quadraticAttenuation;
	/**
	 * allows choice between using set spotlight cutoff or using 180.0 as
	 *  is needed for point lights - doesn't matter for directional lights
	 */
	public boolean spotLight;

	/**
	 * LOCAL light or DIRECTIONAL light
	 */
	public int location;

	public int lightBase;
	public int lightName;

///////////////////////////////////////////////////////////////////////////////
// CONSTRUCTOR

	/**
	 * This constructor creates a light with default settings.
	 */
	public Light()
	{
		lightSwitch = OFF;
		spotLight = false;
		ambient = new float [] {0.3f, 0.3f, 0.3f, 1.0f};		//R, G, B, A
		diffuse = new float [] {0.5f, 0.5f, 0.5f, 1.0f};		//R, G, B, A
		specular = new float [] {1.0f, 1.0f, 1.0f, 1.0f};		//R, G, B, A
		position = new float [] {0.0f, 0.0f, 200.0f, 1.0f};		//X, Y, Z, W
		viewPos = new float [] {0.0f, 0.0f, 0.0f, 0.0f};		//X, Y, Z, W
		direction = new float [] {0.0f, 0.0f, -1.0f, 0.0f};		//X, Y, Z, W
		spotCutoff = 180.0f; // degrees
		spotExponent = 0.0f;
		spotDirection = new float [] {0.0f, 0.0f, -1.0f};
		constAttenuation = 1.0f;
		linearAttenuation = 0.0f;
		quadraticAttenuation = 0.0f;
		location = LOCAL;
		lightName = 0x4000;
	} // end constructor

} // end class Light
