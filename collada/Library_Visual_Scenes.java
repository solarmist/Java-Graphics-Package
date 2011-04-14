package collada;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
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
public class Library_Visual_Scenes {

    public String ID;
    public String name;
    public Document document;
    public ArrayList scenes;

    public Library_Visual_Scenes() {
        scenes = new ArrayList();
    }

    public void getScenes(String fileName) {
        
        NodeList childern;
        

        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM tree of the XML file
            document = db.parse(fileName);
        }

        //possbile errors when parsing the tree the DOM uses SAX to parse the tree
        catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
            return;
        } catch (SAXException e) {
            System.out.println(e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        //get the root elememt
        Element docEle = document.getDocumentElement();

        //get a nodelist of <material> elements
        NodeList nl = docEle.getElementsByTagName("library_visual_scenes");
        childern = nl.item(0).getChildNodes();
        setScene(childern);
    }

    public void setScene(NodeList nl) {
        Visual_Scene tempVScene;
        Node tempNode;
        Node curNode;
        NamedNodeMap attributes;
        Node attribute;
        NodeList childern;

        for (int x = 1; x < nl.getLength(); x += 2) {
            tempNode = nl.item(x);

            if (tempNode.getNodeName().matches("visual_scene")) {
                tempVScene = new Visual_Scene();

                attributes = tempNode.getAttributes();
                attribute = attributes.getNamedItem("id");

                if (attributes != null)
                    tempVScene.id = attribute.getNodeValue();

                attribute = attributes.getNamedItem("name");

                if (attributes != null)
                    tempVScene.name = attribute.getNodeValue();

                childern = tempNode.getChildNodes();

                for (int y = 1; y < childern.getLength(); y += 2) {
                    curNode = childern.item(y);

                    if (curNode.getNodeName().matches("node")) {
                        Nodec nc = doNode(curNode);
                        tempVScene.nodes.add(nc);
                    }

                }

                this.scenes.add(tempVScene);

            } else
                System.out.println("Alert: " + nl.item(x).getNodeName() +
                                   " not done in library_visual_scenes 1");
        }
    }

    public Nodec doNode(Node node) {
        NamedNodeMap attributes;
        Node attribute;
        Nodec tempNodec = new Nodec();
        NodeList childern;
        Node tempNode;
        String tempString;

        attributes = node.getAttributes();

        attribute = attributes.getNamedItem("id");
        if (attribute != null)
            tempNodec.id = attribute.getNodeName();

        attribute = attributes.getNamedItem("name");
        if (attribute != null)
            tempNodec.name = attribute.getNodeValue();

        attribute = attributes.getNamedItem("layer");
        if (attribute != null)
            tempNodec.layer = attribute.getNodeValue();

        attribute = attributes.getNamedItem("sid");
        if (attribute != null)
            tempNodec.sid = attribute.getNodeValue();

        attribute = attributes.getNamedItem("type");
        if (attribute != null)
            tempNodec.type = attribute.getNodeValue();

        childern = node.getChildNodes();

        for (int x = 1; x < childern.getLength(); x += 2) {
            tempNode = childern.item(x);
            tempString = tempNode.getNodeName();

            if (tempString.matches("instance_geometry")) {
                NodeList childern2 = tempNode.getChildNodes();
                NodeList childern3;
                NodeList bindings;
                Instance_Geometry tempIG = new Instance_Geometry();
                Instance_Material tempIM;

                for (int y = 1; y < childern2.getLength(); y += 2) {
                    if (childern2.item(y).getNodeName().matches("bind_material")) {
                        childern3 = childern2.item(y).getChildNodes();

                        for (int z = 1; z < childern3.getLength(); z += 2) {

                            if (childern3.item(z).getNodeName().matches(
                                    "technique_common")) {
                                bindings = childern3.item(z).getChildNodes();

                                for (int q = 1; q < bindings.getLength();
                                             q += 2) {
                                    if (bindings.item(q).getNodeName().matches(
                                            "instance_material")) {
                                        tempIM = processesInstanceMaterial(
                                                bindings.item(q));
                                        tempIG.instance_material.add(tempIM);
                                    } else
                                        System.out.println("Alert: " +
                                                bindings.item(q).getNodeName() +
                                                " not done in library_visual_scenes 2");

                                }
                            } else
                                System.out.println("Alert: " +
                                        childern3.item(z).getNodeName() +
                                        " not done in library_visual_scenes 3");

                        }
                    }

                    else
                        System.out.println("Alert: " +
                                           childern2.item(y).getNodeName() +
                                " not done in library_visual_scenes 4");
                }

                attributes = tempNode.getAttributes();
                attribute = attributes.getNamedItem("url");

                if (attribute != null)
                    tempIG.url = attribute.getNodeValue();

                tempNodec.instance_geometry.add(tempIG);
            }

            else if (tempString.matches("matrix")) {
                String temp = tempNode.getTextContent();
                Matrix m = new Matrix();
                m.setValue(temp);
                tempNodec.matrix.add(m);
            }

            else
                System.out.println("Alert: " + tempString +
                                   " not done in library_visual_scenes 5");

        }

        return tempNodec;
    }

    public Instance_Material processesInstanceMaterial(Node node) {
        NamedNodeMap attributes;
        Node attribute;
        Instance_Material tempIM = new Instance_Material();

        attributes = node.getAttributes();

        attribute = attributes.getNamedItem("symbol");
        if (attribute != null)
            tempIM.symbol = attribute.getNodeValue();

        attribute = attributes.getNamedItem("target");
        if (attribute != null)
            tempIM.target = attribute.getNodeValue();

        attribute = attributes.getNamedItem("sid");
        if (attribute != null)
            tempIM.sid = attribute.getNodeValue();

        return tempIM;
    }

}
