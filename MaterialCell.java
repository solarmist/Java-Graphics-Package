import java.io.*;

public class MaterialCell implements Serializable
{
	public String materialName;	/* name of material */
	public DoubleColor ka;				/* (Ka    ) ambient reflection coefficient */
	public DoubleColor kd;				/* (Kd    ) diffuse reflection coefficient */
	public DoubleColor ks;				/* (Ks    ) specular reflection coefficient */
	String mapKa;			/* (map_Ka) filename for ambient texture map */
	String mapKd;			/* (map_Kd) filename for diffuse texture map */
	String mapKs;			/* (map_Ks) filename for specular texture map */
	String mapD;			/* (map_d ) filename for transparency texture map */
	public DoubleColor emmColor;		/* (em   ) emissive color */
	public double shiny;			/* (Ns    ) object shininess */
	public DoubleColor reflectivity;	/* (Ir) reflected ray coefficient */
	//boolean reflOneValue;
	public DoubleColor refractivity;	/* (It) refracted ray coefficient */
	//boolean refrOneValue;
	public double refractiveIndex;			/* (Ni    ) refractive index 1.0 = no refraction, values go up from there*/
	public DoubleColor transmissionFilter;  /* allows filtering out of certain colors in refraction */
	public DoubleColor lineColor;		/* (Lc    ) line color index (for vector renderings) */
	public boolean doubleSided;	/* (ds    ) flag indicating whether or not material is double-sided 0=false, 1=true*/

	public MaterialCell()
	{
		materialName = "default";
		ka = new DoubleColor(0.2, 0.2, 0.2, 1.0);
		kd = new DoubleColor(0.8, 0.0, 0.0, 1.0);
		ks = new DoubleColor(1.0, 1.0, 1.0, 1.0);
		mapKa = null;
		mapKd = null;
		mapKs = null;
		mapD = null;
		emmColor = new DoubleColor(0.0, 0.0, 0.0, 1.0);
		shiny = 90.0;
		reflectivity = new DoubleColor(0.3, 0.0, 0.0, 1.0);
		//reflOneValue = true;
		refractivity = new DoubleColor(0.3, 0.0, 0.0, 1.0);
		//refrOneValue = true;
		refractiveIndex = 1.5;
		transmissionFilter = new DoubleColor(1.0, 1.0, 1.0, 1.0);
		lineColor = new DoubleColor(1.0, 1.0, 1.0, 1.0);
		doubleSided = false;
	}

	public String toString()
	{
		return materialName;
	}
}
