package collada;

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
public class Input {
    public int offset;
    public String semantic;
    public String source;
    public int set;

    public Input() {

    }
    
    public Input(Input i)
    {
    	offset = i.offset;
    	semantic = i.semantic;
    	source = i.source;
    	set = i.set;
    }
}
