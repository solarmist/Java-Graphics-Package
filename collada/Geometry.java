package collada;

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
public class Geometry {

    public String id;
    public Mesh mesh;
    public Convex_Mesh convex_mesh;
    public Spline spline;

    public Geometry() {
        mesh = null;
        convex_mesh = null;
        spline = null;
    }
}
