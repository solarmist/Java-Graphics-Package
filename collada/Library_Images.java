package collada;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Library_Images {

	public ArrayList<Image> images;
    public Document document;
    
    public Library_Images()
    {
    	images = new ArrayList<Image>();
    }
    
	public void getImages(String fileName)
	{
		NamedNodeMap attributes;
	    Node attribute;
	    NodeList childern;
	    Image tempImage;
	
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
	    NodeList nl = docEle.getElementsByTagName("library_images");
	
	    childern = nl.item(0).getChildNodes();
	
	    for (int x = 1; x < childern.getLength(); x += 2) {
	    		    	  	
	    	if (childern.item(x).getNodeName().matches("image")) {
	    		tempImage = proccessImage(childern.item(x).getChildNodes());
	    		
	    		if (tempImage != null) {
                    
	    			attributes = childern.item(x).getAttributes();
                    
	    			attribute = attributes.getNamedItem("id");
                    if (attribute != null)
                    	tempImage.id = attribute.getNodeValue();

                    attribute = attributes.getNamedItem("name");
                    if (attribute != null)
                    	tempImage.name = attribute.getNodeValue();
                    
                    attribute = attributes.getNamedItem("format");
                    if (attribute != null)
                    	tempImage.format = attribute.getNodeValue();
                    
                    attribute = attributes.getNamedItem("height");
                    if (attribute != null)
                    	tempImage.height = Integer.parseInt(attribute.getNodeValue());
                    
                    attribute = attributes.getNamedItem("width");
                    if (attribute != null)
                    	tempImage.width = Integer.parseInt(attribute.getNodeValue());
                    
                    attribute = attributes.getNamedItem("depth");
                    if (attribute != null)
                    	tempImage.depth = Integer.parseInt(attribute.getNodeValue());
                    
                    if(tempImage != null)
                    	images.add(tempImage);
	    		}//if
	    	}//if
	    }//for	
	}//getImage()
	
	public Image proccessImage(NodeList nodes)
	{
		Image tempImage = new Image();
				
		
		if(nodes.item(1).getNodeName().matches("init_from"))
		{
				 tempImage.init_from = nodes.item(1).getTextContent();	
		}
		
		else
			System.out.println("Alert" + nodes.item(1).getNodeName() + " not done in libary_images 1");
		
		return tempImage;
	}
}//class
