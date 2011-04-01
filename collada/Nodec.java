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
public class Nodec {
    public String id;
    public String name;
    public String sid;
    public String type;
    public String layer;
    public ArrayList lookat;
    public ArrayList matrix;
    public ArrayList rotate;
    public ArrayList skew;
    public ArrayList translate;
    public ArrayList instance_camera;
    public ArrayList instance_controller;
    public ArrayList instance_geometry;
    public ArrayList instance_light;
    public ArrayList instance_node;
    public ArrayList node;


    public Nodec() {
        lookat = new ArrayList();
        matrix = new ArrayList();
        rotate = new ArrayList();
        skew = new ArrayList();
        translate = new ArrayList();
        instance_camera = new ArrayList();
        instance_controller = new ArrayList();
        instance_geometry = new ArrayList();
        instance_light = new ArrayList();
        instance_node = new ArrayList();
        node = new ArrayList();
    }


}
