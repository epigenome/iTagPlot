/*
 *  TagViz
 *  2014
 */

package Objects;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jechoi
 */
public class TagList extends ArrayList<Float> {
    public static final Float NA = null;
    private static HashMap<Integer, TagList> zeros = new HashMap<>();

    public TagList(int initialCapacity) {
	super(initialCapacity);
    }

    public TagList(int size, float f) {
	super(size);
	for (int i = 0; i < size; i++) add(f);
    }

    public TagList average(int count, final TagList nas) {
	TagList avg = new TagList(size());
	for (int i = 0; i < size(); i++) {
	    float n = nas == null ? count : count-nas.get(i);
	    avg.add(n != 0 ? get(i) / n : NA);
	}
	return avg;
    }
    
    public static TagList zero(int size) {
	if (!zeros.containsKey(size)) {
	    TagList a = new TagList(size);
	    for (int i = 0; i < size; i++) a.add(0.0f);
	    zeros.put(size, a);
	}
	
	return zeros.get(size);
    }
}
