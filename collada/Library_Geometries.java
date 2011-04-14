package collada;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

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
public class Library_Geometries {

    public ArrayList geometry;
    public Document document;

    public Library_Geometries() {

        geometry = new ArrayList();
    }

    public Mesh processMesh(NodeList meshChildern) {
        Mesh tempMesh = new Mesh();

        NamedNodeMap attributes;
        Node attribute;
        NodeList childern;

        for (int z = 1; z < meshChildern.getLength(); z += 2) {
            Node currentNode = meshChildern.item(z);

            if (currentNode.getNodeName().matches("source")) {
                Source tempSource = null;
                childern = currentNode.getChildNodes();

                
                
                tempSource = new Source();
                tempSource = processSource(childern);
                    
                attributes = currentNode.getAttributes();
                attribute = attributes.getNamedItem("id");
                tempSource.id = attribute.getNodeValue();
                tempMesh.sources.add(tempSource);

             

                
            }

            else if (currentNode.getNodeName().matches("vertices")) {
                Vertices tempVert = new Vertices();
                Node curNode;
                Input tempInput;
                childern = currentNode.getChildNodes();

                for (int x = 1; x < childern.getLength(); x += 2) {
                    curNode = childern.item(x);

                    if (curNode.getNodeName().matches("input")) {
                        tempInput = proccessInput(curNode);
                        tempVert.input.add(tempInput);
                    }

                    else
                        System.out.println("Alert: " +
                                           childern.item(x).getNodeName() +
                                           " not done in Library_Geometries 1");

                }

                attributes = currentNode.getAttributes();
                attribute = attributes.getNamedItem("id");
                tempVert.id = attribute.getNodeValue();

                tempMesh.vertices = tempVert;

            }

            else if (currentNode.getNodeName().matches("triangles")) {
                Triangles tempTriangle = new Triangles();
                childern = currentNode.getChildNodes();
                Node curNode;
                Input tempInput;

                for (int x = 1; x < childern.getLength(); x += 2) {
                    curNode = childern.item(x);

                    if (curNode.getNodeName().matches("input")) {
                        tempInput = this.proccessInput(curNode);
                        tempTriangle.inputs.add(tempInput);
                    } else if (curNode.getNodeName().matches("p")) {
                        tempTriangle.setVerts(curNode.getTextContent());
                    }

                    else
                        System.out.println("Alert: " +
                                           childern.item(x).getNodeName() +
                                           " not done in Library_Geometries 2");

                }

                attributes = currentNode.getAttributes();
                attribute = attributes.getNamedItem("material");

                if (attribute != null)
                    tempTriangle.material = attribute.getNodeValue();

                attributes = currentNode.getAttributes();
                attribute = attributes.getNamedItem("count");

                if (attribute != null)
                    tempTriangle.count = Integer.parseInt(attribute.
                            getNodeValue());

                tempMesh.triangles.add(tempTriangle);
            }

            else
                System.out.println("Alert: " + currentNode.getNodeName() +
                                   " not done in Library_Geometries 3");

        }

        return tempMesh;
    }

    public Input proccessInput(Node curNode) {
        Input tempInput = new Input();

        NamedNodeMap attributes;
        Node attribute;

        attributes = curNode.getAttributes();

        attribute = attributes.getNamedItem("semantic");
        if (attribute != null)
            tempInput.semantic = attribute.getNodeValue();

        attribute = attributes.getNamedItem("source");
        if (attribute != null)
            tempInput.source = attribute.getNodeValue();

        attribute = attributes.getNamedItem("offset");
        if (attribute != null)
            tempInput.offset = Integer.parseInt(attribute.getNodeValue());

        attribute = attributes.getNamedItem("set");
        if (attribute != null) {
            try {
                tempInput.set = Integer.parseInt(attribute.getNodeValue());
            } catch (NumberFormatException e) {
                tempInput.set = -1;
            }

        }
        return tempInput;
    }

    public Source processSource(NodeList nodes){
        Node attribute;
        NamedNodeMap attributes;
        Source tempSource = new Source();
        Node currentNode;
        
        for(int count= 1 ;count< nodes.getLength();count+=2){
        	
        	currentNode =nodes.item(count);
        	
        	//System.out.println(currentNode.getNodeName());

	        if (currentNode.getNodeName().matches("Name_array")) {
	            Name_Array tempNameA = new Name_Array();
	
	            attributes = currentNode.getAttributes();
	
	            attribute = attributes.getNamedItem("id");
	            if (attribute != null)
	                tempNameA.id = attribute.getNodeValue();
	
	            attribute = attributes.getNamedItem("count");
	            if (attribute != null)
	                tempNameA.count = Integer.parseInt(attribute.getNodeValue());
	
	            attribute = attributes.getNamedItem("name");
	            if (attribute != null)
	                tempNameA.name = attribute.getNodeValue();
	
	            tempNameA.names = currentNode.getTextContent().split(" ");
	
	            tempSource.nameArray = tempNameA;
	
	        }
	
	        else if (currentNode.getNodeName().matches("bool_array")) {
	            Bool_Array temp = new Bool_Array();
	
	            attributes = currentNode.getAttributes();
	
	            attribute = attributes.getNamedItem("id");
	            if (attribute != null)
	                temp.id = attribute.getNodeValue();
	
	            attribute = attributes.getNamedItem("count");
	            if (attribute != null)
	                temp.count = Integer.parseInt(attribute.getNodeValue());
	
	            attribute = attributes.getNamedItem("name");
	            if (attribute != null)
	                temp.name = attribute.getNodeValue();
	
	            String[] t = currentNode.getTextContent().split(" ");
	
	            for (int x = 0; x < t.length; x++) {
	                temp.bools.add(t[x]);
	            }
	
	            tempSource.boolArray = temp;
	
	        }
	
	        else if (currentNode.getNodeName().matches("float_array")) {
	
	            Float_Array tempFloatArray = new Float_Array();
	
	            attributes = currentNode.getAttributes();
	            attribute = attributes.getNamedItem("id");
	            tempFloatArray.id = attribute.getNodeValue();
	
	            attribute = attributes.getNamedItem("count");
	            tempFloatArray.setCount(attribute.getNodeValue());
	
	            //load the array
	            tempFloatArray.setArray(currentNode.getTextContent());
	            tempSource.floatArray = tempFloatArray;
	
	        }
	
	        else if (currentNode.getNodeName().matches("int_array")) {
	            Int_Array temp = new Int_Array();
	
	            attributes = currentNode.getAttributes();
	            attribute = attributes.getNamedItem("id");
	            if (attribute != null)
	                temp.id = attribute.getNodeValue();
	
	            attribute = attributes.getNamedItem("count");
	            if (attribute != null)
	                temp.setCount(attribute.getNodeValue());
	
	            //load the array
	            temp.setArray(currentNode.getTextContent());
	            tempSource.intArray = temp;
	
	        }
	
	        else if (currentNode.getNodeName().matches("technique_common")) {
	            NodeList child = currentNode.getChildNodes();
	
	            for (int x = 1; x < child.getLength(); x += 2) {
	                //must contain exactly one accessor
	                if (child.item(x).getNodeName().matches("accessor")) {
	                    Accessor tempAccessor = new Accessor();
	
	                    attributes = child.item(x).getAttributes();
	
	                    attribute = attributes.getNamedItem("count");
	                    if (attribute != null)
	                        tempAccessor.count = Integer.parseInt(attribute.
	                                getNodeValue());
	
	                    attribute = attributes.getNamedItem("stride");
	                    if (attribute != null)
	                        tempAccessor.stride = Integer.parseInt(attribute.
	                                getNodeValue());
	
	                    attribute = attributes.getNamedItem("source");
	                    if (attribute != null)
	                        tempAccessor.source = attribute.getNodeValue();
	
	                    tempSource.accessor = tempAccessor;
	
	                }
	            }
	        }
	
	        else
	            System.out.println("Alert: " + currentNode.getNodeName() +
	                               " not done in Library_Geometries 4");
        }
        
        return tempSource;
    }

    public void getGeometries(String fileName) {

        NodeList childern;
        Geometry tempGeometry = null;
        NamedNodeMap attributes;
        Node attribute;
        NodeList element;
        NodeList meshChildern;

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
        NodeList nl = docEle.getElementsByTagName("library_geometries");

        childern = nl.item(0).getChildNodes();

        for (int x = 1; x < childern.getLength(); x += 2) {
            if (childern.item(x).getNodeName().matches("geometry")) {
                tempGeometry = new Geometry();
                attributes = childern.item(x).getAttributes();
                attribute = attributes.getNamedItem("id");
                tempGeometry.id = attribute.getNodeValue();

                //there has to be one and only one of the fallowing convex_mesh mesh or spline
                element = childern.item(x).getChildNodes();

                for (int y = 1; y < element.getLength(); y += 2) {
                    if (element.item(y).getNodeName().matches("mesh")) {
                        meshChildern = element.item(y).getChildNodes();
                        tempGeometry.mesh = processMesh(meshChildern);
                    }

                    else
                        System.out.println("Alert: " +
                                           childern.item(x).getNodeName() +
                                           " not done in Library_Geometries 5");

                }

            }
        }

        geometry.add(tempGeometry);

    }
}
