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
public class Library_Materials {

    public ArrayList<Material> materials;
    public Document document;


    public Library_Materials() {
        materials = new ArrayList<Material>();
    }
    
    public int getNumber(String name)
    {
    	
    	for(int x=0;x<materials.size();x++)
    	{
    		if( materials.get(x).id.matches(name) )
    		{
    			return x;
    		}
    	}
    	
    	return -1;
    }
    
    public String getEffect(String name)
    {
    	return materials.get( getNumber(name) ).instance_effect;
    	
    }

    /*
     *Reads the material elements and places them in the list
     *Calls the function that reads the material values.
     */
    public void getMaterials(String fileName) {
        Material tempMaterial;
        Node tempNode;
        NodeList nl2;
        NamedNodeMap attributes;
        Node attribute;

        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM tree of the XML file
            document = db.parse(fileName);
        }

        //Possible errors when parsing the tree the DOM uses SAX to parse the tree
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

        //get the root element
        Element docEle = document.getDocumentElement();

        //get a NodeList of <material> elements
        NodeList nl = docEle.getElementsByTagName("library_materials");
        nl = nl.item(0).getChildNodes();

        for (int x = 1; x < nl.getLength(); x += 2) {
            if (nl.item(x).getNodeName().matches("material")) {
                tempMaterial = new Material();
                tempNode = nl.item(x);

                //get the name and the id from the current node
                attributes = tempNode.getAttributes();

                attribute = attributes.getNamedItem("id");
                tempMaterial.id = attribute.getNodeValue();

                //get the instance_effect url
                nl2 = tempNode.getChildNodes();

                for (int y = 1; y < nl2.getLength(); y += 2) {
                    tempNode = nl2.item(y);
                    if (tempNode.getNodeName().matches("instance_effect")) {
                        attributes = tempNode.getAttributes();
                        attribute = attributes.getNamedItem("url");
                        tempMaterial.instance_effect = attribute.getNodeValue();
                    } else
                        System.out.println("Alert: " + nl2.item(y).getNodeName() +
                                           " not done in library_materials 1");
                }

                materials.add(tempMaterial);
            } else
                System.out.println("Alert: " + nl.item(x).getNodeName() +
                                   " not done in library_materials 2");
        }
    }
}
