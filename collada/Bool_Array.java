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
public class Bool_Array {
    public String id;
    public String name;
    public boolean[] array;
    public int count;
    
    public void setCount(String c) {
        count = Integer.parseInt(c);
        array = new boolean[count];
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
    				array[x] = Boolean.parseBoolean(tempString[x]);
    			}
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
    }


    public boolean[] getArray() {
        return array;
    }
    
    public boolean getValue(int location)
    {
    	return array[location];
    }

    public Bool_Array() {
        
    }
}
