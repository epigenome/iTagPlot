/*
 *  TagViz
 *  2014
 */

package Objects;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;

/**
 *
 * @author jechoi
 */
public class SimpleGroup extends HashMap<String, Boolean> implements Group {
    private String name;
    private int count;

    public SimpleGroup(String name) {
	this.name = name;
	this.count = 0;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public void setName(String name) {
	this.name = name;
    }  
    
    @Override
    public Set<String> features() {
	return keySet();
    }
    
    @Override
    public Set<String> features(String sample) {
	return keySet();
    }

    @Override
    public void add(final String sample, final String feature) {
	count = 0;
	put(feature, false);
    }
    
    @Override
    public void addAll(final String sample, final ArrayList<String> features) {
	for (String f : features) add(sample, f);
    }
    
    @Override
    public void enable(final String sample, final String feature) {
	count = 0;
	put(feature, true);
    }
    
    @Override
    public void changeSampleName(final String oldname, final String newname) {
    }
    
    @Override
    public int count(final String sample) {
	if (count == 0) {
	    for (Boolean b : values()) if (b) count++;
	}
	return count;
    }
}
