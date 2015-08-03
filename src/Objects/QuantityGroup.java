/*
 *  TagViz
 *  2014
 */

package Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author jechoi
 */
public class QuantityGroup extends HashMap<String, SimpleGroup> implements Group {
    private String name;

    
    public QuantityGroup(String name) {
	this.name = name;
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
	return null;
    }
    
    @Override
    public Set<String> features(String sample) {
        if( get(sample) == null ) return null;
	return get(sample).features(sample);
    }
    
    @Override
    public void add(final String sample, final String feature) {
	if (!containsKey(sample)) put(sample, new SimpleGroup(null));
	get(sample).add(null, feature);
    }
    
    @Override
    public void addAll(final String sample, final ArrayList<String> features) {
	for (String f : features) add(sample, f);
    }
    
    @Override
    public void enable(final String sample, final String feature) {
	get(sample).enable(null, feature);
    }
    
    @Override
    public void changeSampleName(final String oldname, final String newname) {
        if(this.containsKey(oldname) ) {
            SimpleGroup value = this.get(oldname);
            this.put(newname, value);
            this.remove(oldname);
	}
    }
    
    @Override
    public int count(final String sample) {
	return get(sample).count(null);
    }
}
