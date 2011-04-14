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
public class Triangles {
    public String material;
    public int count;
    public ArrayList inputs;
    public int[] verts;

    public Triangles() {
        inputs = new ArrayList();
    }

    public void setVerts(String i) {
    	    	
    	if(i != "")
    	{
    		String[] temp = i.split(" ");
    		verts = new int[temp.length];

    		for (int x = 0; x < temp.length; x++) {
    			verts[x] = Integer.parseInt(temp[x]);
    		}
    	}
    }
    
    public int[] getLocations()
    {
    	return this.verts;
    }
}
