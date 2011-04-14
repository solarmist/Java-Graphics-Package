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
public class Collada {

    public Library_Materials material;
    public Library_Effects effects;
    public Library_Geometries gemetries;
    public Library_Visual_Scenes vscenes;
    public Library_Images images;
    public Scene scene;
    String fileName;
    public Document document;


    public Collada(String file) {
        material = new Library_Materials();
        effects = new Library_Effects();
        gemetries = new Library_Geometries();
        vscenes = new Library_Visual_Scenes();
        scene = new Scene();
        images = new Library_Images();

        fileName = file;
        readFile(fileName);
    }

    public void readFile(String fileName) {

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

        NodeList nl = docEle.getChildNodes();

        for (int x = 1; x < nl.getLength(); x += 2) {

            if (nl.item(x).getNodeName().matches("library_materials")) {
                material.getMaterials(fileName);
            }

            else if (nl.item(x).getNodeName().matches("library_effects")) {
                effects.getEffects(fileName);
            }

            else if (nl.item(x).getNodeName().matches("library_geometries")) {
                gemetries.getGeometries(fileName);
            }

            else if (nl.item(x).getNodeName().matches("library_visual_scenes")) {
                this.vscenes.getScenes(fileName);
            } 
            else if (nl.item(x).getNodeName().matches("library_images")) {
                this.images.getImages(fileName);
            } 
            else if (nl.item(x).getNodeName().matches("scene")) {
                proccessScene(nl.item(x).getChildNodes());
            }

            else
                System.out.println("Alert: " + nl.item(x).getNodeName() +
                                   " not done in Collada 1");
        }
    }

    public void proccessScene(NodeList list) {
        Instance_Visual_Scene ivs;
        NamedNodeMap attributes;
        Node attribute;

        for (int x = 1; x < list.getLength(); x += 2) {

            if (list.item(x).getNodeName().matches("instance_visual_scene")) {
            	ivs = new Instance_Visual_Scene();
            	
                attributes = list.item(x).getAttributes();

                attribute = attributes.getNamedItem("url");
                if (attribute != null)
                    ivs.url = attribute.getNodeValue();

                attribute = attributes.getNamedItem("sid");
               if (attribute != null)
                   ivs.sid = attribute.getNodeValue();

               attribute = attributes.getNamedItem("name");
               if (attribute != null)
                   ivs.name = attribute.getNodeValue();
               
               this.scene.instance_visual_scene = ivs;
            }

            else
                System.out.println("Alert: " + list.item(x).getNodeName() +
                                   " not done in Collada 2");

        }
    }

}
