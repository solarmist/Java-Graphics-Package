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
public class Library_Effects {

    public ArrayList effects;
    public Document document;

    public Library_Effects() {
        effects = new ArrayList();
    }

    public void getEffects(String fileName) {
        Effect tempEffect;
        NamedNodeMap attributes;
        Node attribute;
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
        NodeList nl = docEle.getElementsByTagName("library_effects");

        childern = nl.item(0).getChildNodes();

        
        
        for (int x = 1; x < childern.getLength(); x += 2) {

            if (childern.item(x).getNodeName().matches("effect")) {
            	

            	tempEffect = proccessEffect(childern.item(x).getChildNodes());

                if (tempEffect != null) {
                    attributes = childern.item(x).getAttributes();
                    attribute = attributes.getNamedItem("id");

                    if (attribute != null)
                        tempEffect.id = attribute.getNodeValue();

                    attribute = attributes.getNamedItem("name");

                    if (attribute != null)
                        tempEffect.name = attribute.getNodeValue();

                    if(tempEffect != null)
                    	effects.add(tempEffect);
                }
            }

            else
                System.out.println("Alert: " + childern.item(x).getNodeName() +
                                   " not done in Library_Effects 1");

        }

    }

    public Effect proccessEffect(NodeList nodes) {
        Effect tempEffect = new Effect();

        for (int x = 1; x < nodes.getLength(); x += 2) {
            if (nodes.item(x).getNodeName().matches("profile_COMMON")) {
                proccessCommon(nodes.item(x).getChildNodes(),tempEffect);
            } else
                System.out.println("Alert: " + nodes.item(x).getNodeName() +
                                   " not done in Library_Effects 2");
        }

        return tempEffect;

    }

    public void proccessCommon(NodeList nodes, Effect tempEffect) {
    	
       
        for (int x = 1; x < nodes.getLength(); x += 2) {
            if (nodes.item(x).getNodeName().matches("technique")) {
                proccessTechnique(nodes.item(x).getChildNodes(),tempEffect);
            }
            
            else if (nodes.item(x).getNodeName().matches("newparam")) {
            	proccessNewParam(nodes.item(x),tempEffect);
            }
            
            else
                System.out.println("Alert: " + nodes.item(x).getNodeName() +
                                   " not done in Library_Effects 3");
        }

       
    }
    
    public void proccessTechnique(NodeList nodes, Effect tempEffect)
    {
    	for(int x=1;x<nodes.getLength();x+=2)
    	{
    		if (nodes.item(x).getNodeName().matches("phong"))
    			proccessPhong(nodes.item(x).getChildNodes(),tempEffect);
    		else
    			System.out.println("Alert: " + nodes.item(x).getNodeName() +
                                   " not done in Library_Effects 11");
    	}
    }
    
    public Newparam processSurface(NodeList list)
    {
    	Newparam param = new Newparam();
    	
    	
    	for(int x = 1; x< list.getLength();x+=2)
    	{
    		if(list.item(x).getNodeName().matches("init_from"))
    			param.surface.init_from = list.item(x).getTextContent();
    		
    		if(list.item(x).getNodeName().matches("format"))
    			param.surface.format = list.item(x).getTextContent();
    	}
    	
    	
        
    	return param;
    }
    
    public Newparam processSampler2D(NodeList list)
    {
    	Newparam param = new Newparam();
    	
    	for(int x = 1; x< list.getLength();x+=2)
    	{
    		if(list.item(x).getNodeName().matches("source"))
    			param.sampler2D.source = list.item(x).getTextContent();
    		
    		else if(list.item(x).getNodeName().matches("minfilter"))
    			param.sampler2D.minfilter = list.item(x).getTextContent();
    		
    		else if(list.item(x).getNodeName().matches("magfilter"))
    			param.sampler2D.magfilter = list.item(x).getTextContent();
    		
    		else if(list.item(x).getNodeName().matches("wrap_s"))
    			param.sampler2D.wrap_s = list.item(x).getTextContent();
    		
    		else if(list.item(x).getNodeName().matches("wrap_t"))
    			param.sampler2D.wrap_t = list.item(x).getTextContent();
    		
    		else if(list.item(x).getNodeName().matches("mipfilter"))
    			param.sampler2D.mipfilter = list.item(x).getTextContent();
    		
    		else if(list.item(x).getNodeName().matches("border_color"))
    			param.sampler2D.border_color = list.item(x).getTextContent();
    		
    		else if(list.item(x).getNodeName().matches("mipmap_maxlevel"))
    			param.sampler2D.mipmap_maxlevel = list.item(x).getTextContent();
    		
    		else if(list.item(x).getNodeName().matches("mipmap_bias"))
    			param.sampler2D.mipmap_bias = list.item(x).getTextContent();
    		
    		else
    			System.out.println("Alert: " + list.item(x).getNodeName() +
                " not done in Library_Effects 13");
    	}
    	
    	return param;
    	    	
    }
    
    public void proccessNewParam(Node node,Effect tempEffect)
    {
    	Newparam param;
    	NamedNodeMap attributes;
        Node attribute;
        NodeList list = node.getChildNodes();
    	
    	for(int x=1;x<list.getLength();x+=2)
    	{
    		
    		if(list.item(x).getNodeName().matches("surface"))
    		{
    			param = processSurface(list.item(x).getChildNodes());
    			attributes = list.item(x).getAttributes();
    	        attribute = attributes.getNamedItem("type");
    	        
    	        if(attribute != null)
    	        	param.sid = attribute.getNodeValue();
    	        
    	        attributes = node.getAttributes();
    	        attribute = attributes.getNamedItem("sid");
    	        
    	        if(attributes != null)
    	        	param.sid = attribute.getNodeValue();
    	        
    			tempEffect.newparam.add(param);
    		}
    		
    		else if(list.item(x).getNodeName().matches("sampler2D"))
    		{
    			param = processSampler2D(list.item(x).getChildNodes());
    			
    			attributes = node.getAttributes();
    	        attribute = attributes.getNamedItem("sid");
    	        
    	        if(attributes != null)
    	        	param.sid = attribute.getNodeValue();    	        
    			tempEffect.newparam.add(param);
    		}
    		
    		else
    			System.out.println("Alert: " + list.item(x).getNodeName() +
                                   " not done in Library_Effects 12");
    			
    	}
    }

    public void proccessPhong(NodeList list, Effect effect) {
        String what;
        String value;

        float floatValue = -1;
        float[] floatArray = null;
        Node cur;
        NodeList childern;
        boolean texture;
        boolean colors;
        String text=null;
        String coord = null;
       
        for (int x = 1; x < list.getLength(); x += 2) {
        	
        	texture = false;
        	colors = false;
        	cur = list.item(x);
            what = cur.getNodeName();

            childern = cur.getChildNodes();
            value = childern.item(1).getTextContent();

            if (childern.item(1).getNodeName().matches("color")) {
            	colors = true;
                floatArray = breakApart(value);
            }
            
            else if((childern.item(1).getNodeName().matches("texture")))
            {
            	NamedNodeMap attributes;
                Node attribute;
                
                attributes = childern.item(1).getAttributes();
                attribute = attributes.getNamedItem("texture");
                
            	texture = true;
            	
            	text = attribute.getNodeValue();
            	
            	attribute = attributes.getNamedItem("texcoord");
            	coord = attribute.getNodeValue();
            	
            }

            else if((childern.item(1).getNodeName().matches("float")))
                floatValue = Float.parseFloat(value);
            
            else 
            	System.out.println("Alert: " + childern.item(1).getNodeName() +
            " not done in Library_Effects 10");

            if (what.matches("emission"))
            {
            	if(colors)
            		effect.emission = floatArray;
            	if(texture)
            	{
            		effect.emissionTexture.name = text;
            		effect.emissionTexture.coord = coord;
            	}
            }
            else if (what.matches("ambient"))
            {
            	if(colors)
            		effect.ambient = floatArray;
            	if(texture)
            	{
            		effect.ambientTexture.name = text;
            		effect.ambientTexture.coord = coord;
            	}
            }
            else if (what.matches("diffuse"))
            {
            	if(colors)
            		effect.diffuse = floatArray;
            	if(texture)
            	{
            		effect.diffuseTexture.name = text;
            		effect.diffuseTexture.coord = coord;
            	}
            }
            else if (what.matches("specular"))
            {
            	if(colors)
            		effect.specular = floatArray;
            	if(texture)
            	{
            		effect.specularTexture.name = text;
            		effect.diffuseTexture.coord = coord;
            	}
            }
            else if (what.matches("shininess"))
            {
            	effect.shininess = floatValue;
            }
            else if (what.matches("reflective"))
            {
            	if(colors)
            		effect.reflective = floatArray;
            	if(texture)
            	{
            		effect.reflectiveTexture.name = text;
            		effect.reflectiveTexture.coord = coord;
            	}
            }
            else if (what.matches("reflectivity"))
            		effect.reflectivity = floatValue;

            else if (what.matches("transparent"))
            {
            	if(colors)
            		effect.transparent = floatArray;
            	if(texture)
            	{
            		effect.transparentTexture.name = text;
            		effect.transparentTexture.coord = coord;
            	}
            }
            else if (what.matches("transparency"))
            	     effect.transparency = floatValue;

            else if (what.matches("index_of_refraction"))
            		effect.indexOfRefraction = floatValue;

            else
                System.out.println("Alert: " + list.item(x).getNodeName() +
                                   " not done in Library_Effects 4");

        }

    }

    public float[] breakApart(String value) {
        float[] v = new float[4];
        String[] s;

        s = value.split(" ");

        v[0] = Float.parseFloat(s[0]);
        v[1] = Float.parseFloat(s[1]);
        v[2] = Float.parseFloat(s[2]);
        v[3] = Float.parseFloat(s[3]);

        return v;
    }
}
