package collada;

import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Instance_Geometry {
    
	public String sid;
    public String name;
    public String url;
    public ArrayList instance_material;

    public Instance_Geometry() {
        instance_material = new ArrayList();
    }
}
