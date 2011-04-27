package collada;

import java.util.ArrayList;

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
public class Mesh {

    public ArrayList<Source> sources;
    public Vertices vertices;
    //public ArrayList lines;
    //public ArrayList linestrips;
    //public ArrayList polygons;
    //public ArrayList polylist;
    public ArrayList<Triangles> triangles;
    //public ArrayList tristrips;

    public Mesh() {
        sources = new ArrayList<Source>();
        vertices = new Vertices();
        //lines = new ArrayList();
        //linestrips = new ArrayList();
        //polygons = new ArrayList();
        //polylist = new ArrayList();
        triangles = new ArrayList<Triangles>();
        //tristrips = new ArrayList();
    }
}
