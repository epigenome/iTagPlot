/*
 *  TagViz
 *  2014
 */

package Objects;

import java.util.HashMap;

/**
 *
 * @author jechoi
 */
public class TagMap extends HashMap<String, TagList> {

    @Override
    public TagList put(String key, TagList value) {
	return super.put(key, value); //To change body of generated methods, choose Tools | Templates.
    }
    
    public float[] getMaxAndMin() {
        float[] values = {Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY};
        for (TagList al : values()) {
            for (Float v : al) {
		if (v == TagList.NA) continue;
                if (v < values[1]) values[1] = v;
                if (v > values[0]) values[0] = v;
            }
        }
        return values;
    }
}
