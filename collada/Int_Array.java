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
public class Int_Array {

    public String id;
    public int count;
    public int[] array;

    public void setCount(String c) {
        count = Integer.parseInt(c);
        array = new int[count];
    }

    public int getCount() {
        return count;
    }

    public void setArray(String s) {
        String[] tempString = s.split(" ");

        for (int x = 0; x < tempString.length; x++) {
            array[x] = Integer.parseInt(tempString[x]);
        }
    }


    public int[] getArray() {
        return array;
    }

    public Int_Array() {
    }
}
