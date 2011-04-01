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
public class Matrix {
    float[] values;
    public Matrix() {

    }

    public void setValue(String s) {
        String[] temp = s.split(" ");
        values = new float[temp.length];

        for (int x = 0; x < temp.length; x++) {
            values[x] = Float.parseFloat(temp[x]);
        }
    }

    public float[] getValues() {
        return values;
    }
}
