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
public class Float_Array {
    public String id;
    public int count;
    public float[] array;

    public void setCount(String c) {
        count = Integer.parseInt(c);
        array = new float[count];
    }

    public int getCount() {
        return count;
    }

    public void setArray(String s) {
    	
    	String[] tempString=null;
    	
    	if(s!= "")
    		tempString = s.split(" ");

    	try
    	{
    		if(tempString != null)
    			for (int x = 0; x < tempString.length; x++) {
    				array[x] = Float.parseFloat(tempString[x]);
    			}
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
    }


    public float[] getArray() {
        return array;
    }
    
    public float getValue(int location)
    {
    	return array[location];
    }

    public Float_Array() {
    }
}
