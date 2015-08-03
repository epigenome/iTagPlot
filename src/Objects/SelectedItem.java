/*
 *  Collection of selected items
 */
package Objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import tagviz.TagViz;

public class SelectedItem
{
    public ArrayList<String> samples = new ArrayList<>();
    private ArrayList<String> groups = new ArrayList<>();
    private ArrayList<String> features = new ArrayList<>();
    private TagMap tags = new TagMap();
    tagviz.TagViz app;
    private boolean ignoreSample = false;
    private boolean ignoreGroup = false;
    private boolean ignoreFeature = false;
    private final ReadOnlyIntegerWrapper groupProperty = new ReadOnlyIntegerWrapper();
    private final ReadOnlyIntegerWrapper sampleProperty = new ReadOnlyIntegerWrapper();
    private final ReadOnlyIntegerWrapper featureProperty = new ReadOnlyIntegerWrapper();
    private final ReadOnlyIntegerWrapper tagProperty = new ReadOnlyIntegerWrapper();

    public SelectedItem(TagViz app) {
        this.app = app;
    }

    public TagViz getApp() {
        return app;
    }

    public void setApp(TagViz app) {
        this.app = app;
    }
 
    public boolean isIgnoreSample() {
        return ignoreSample;
    }

    public void setIgnoreSample(boolean ignoreSample) {
        this.ignoreSample = ignoreSample;
    }
    
    public boolean isIgnoreGroup() {
        return ignoreGroup;
    }

    public void setIgnoreGroup(boolean ignoreGroup) {
        this.ignoreGroup = ignoreGroup;
    }
    
    public boolean isIgnoreFeature() {
        return ignoreFeature;
    }

    public void setIgnoreFeature(boolean ignoreFeature) {
        this.ignoreFeature = ignoreFeature;
    }
    
    public int getSampleProperty() {
        return sampleProperty.get();
    }

    public ReadOnlyIntegerProperty sampleProperty() {
        return sampleProperty.getReadOnlyProperty();
    }

    public int getGroupProperty() {
        return groupProperty.get();
    }

    public ReadOnlyIntegerProperty groupProperty() {
        return groupProperty.getReadOnlyProperty();
    }

    public int getFeatureProperty() {
        return featureProperty.get();
    }

    public ReadOnlyIntegerProperty featureProperty() {
        return featureProperty.getReadOnlyProperty();
    }

    public int getTagProperty() {
        return tagProperty.get();
    }

    public ReadOnlyIntegerProperty tagProperty() {
        return tagProperty.getReadOnlyProperty();
    }

    public TagMap getSelection() {
        if (!isIgnoreSample() && !isIgnoreGroup() && !isIgnoreFeature()) return tags;

	TagMap _tags = new TagMap();
	for (String sam : samples) {
	    if (!isIgnoreSample()) _tags.put(sam, tags.get(sam));
	    if (!isIgnoreGroup()) {
		for (String gro : groups) {
		    String name = sam + ":" + gro;
		    if (tags.get(name) != null) _tags.put(name, tags.get(name));
		}
	    }
	    if (!isIgnoreFeature()) {
		for (String feat : features) {
		    String name = sam + ":" + feat;
		    _tags.put(name, tags.get(name));
		}
	    }
        }
        return _tags;
    }
    
    public Boolean SelectionContains(String name) {
        return samples.contains(name) || groups.contains(name) || features.contains(name);
    }

    public boolean remove(String name) {
        if (samples.contains(name)) {
            ArrayList<String> ids = new ArrayList<>();
            for (String key : tags.keySet()) if (key.equals(name) || key.startsWith(name + ":")) { ids.add(key); }
            for (String key : ids) tags.remove(key);
            samples.remove(name);
        } else {
            for (String sam : samples) tags.remove(sam + ":" + name);
            groups.remove(name);
            features.remove(name);
        }

        sampleProperty.set(samples.size());
        groupProperty.set(groups.size());
        featureProperty.set(features.size());
        tagProperty.set(tags.size());
        return true;
    }

    public boolean add(String name) {
        try {
            if (getApp().samMap.containsKey(name)) {
                samples.add(name);
                tags.put(name, ((Sample) getApp().samMap.get(name)).averageAt(null, getApp().featMap.size()));
                //for (String g : groups) tags.put(name + ":" + g, ((Sample) getApp().samMap.get(name)).averageAt(g, getApp().featMap.size()));
                
                for (String g : groups) {
                    try{
                        tags.put(name + ":" + g, ((Sample) getApp().samMap.get(name)).averageAt(g, ((Group) getApp().groupMap.get(g)).count(name)));
                    } catch (NoSuchElementException | NullPointerException ex) {
                        //add an all-zero series
                        tags.put(name + ":" + g, TagList.zero(getApp().header.length));
                    }
                }
                for (String f : features) {
		    String name2 = name + ":" + f;
		    try {
			tags.put(name2, ((Sample) getApp().samMap.get(name)).getTagsFor(f));
		    } catch (NoSuchElementException ex) {
                        //add an all-zero series
			tags.put(name2, TagList.zero(getApp().header.length));
		    }
		}
            } else {
                if (getApp().groupMap.containsKey(name)) {
		    groups.add(name);
		} else if (getApp().featMap.containsKey(name)) {
		    features.add(name);
		}

                for (String sam : samples) {
                    String name2 = sam + ":" + name;
                    if (tags.containsKey(name2)) continue;

		    try {
			if (getApp().groupMap.containsKey(name)) {
			    TagList avg = ((Sample) getApp().samMap.get(sam)).averageAt(name, ((Group) getApp().groupMap.get(name)).count(sam));
	                    if (avg != null) tags.put(name2, avg);
			}
			else if (getApp().featMap.containsKey(name)) {
                            tags.put(name2, ((Sample) getApp().samMap.get(sam)).getTagsFor(name));
			}
		    } catch (NoSuchElementException | NullPointerException ex) {
                        //add an all-zero series
			tags.put(name2, TagList.zero(getApp().header.length));
                    }
                }
            }
        } catch (IOException | NoSuchElementException ex) {
            Logger.getLogger(SelectedItem.class.getName()).log(Level.SEVERE, null, ex);
        }

        sampleProperty.set(samples.size());
        groupProperty.set(groups.size());
        featureProperty.set(features.size());
        tagProperty.set(tags.size());
        return true;
    }
    
    //[DONE] 0718 add changeSampleName and changeGroupName
    public void changeSampleName( String oldSampleKey, String newSampleKey ) {
        if( !samples.contains( oldSampleKey ) ) return;
        
        samples.set( samples.indexOf( oldSampleKey ), newSampleKey);
        //samples.remove( oldSampleKey );
        //samples.add   ( newSampleKey );
        
        TagList sample = tags.get( oldSampleKey );
        tags.remove( oldSampleKey );
        tags.put( newSampleKey, sample );

        ArrayList<String> oldTagSuffix = new ArrayList<>();
        for( String key : tags.keySet() ) {
            if( key.startsWith( oldSampleKey + ":" ) ) {
                oldTagSuffix.add( key.substring( key.indexOf( ':' ) + 1 ) );
            }
        }
        
        for( String suffix : oldTagSuffix ) {
            String tagKey = oldSampleKey + ":" + suffix;
            TagList value = tags.get( tagKey );
            tags.remove( tagKey );
            tags.put( newSampleKey + ":" + suffix, value );
        }
        
        sampleProperty.set(samples.size());
        tagProperty.set(tags.size());
    }
    
    public void changeGroupName( String oldGroupKey, String newGroupKey ) {
        if( !groups.contains( oldGroupKey ) ) return;
        
        for( String sampleKey : samples ) {
            String oldTagKey = sampleKey + ":" + oldGroupKey;
            String newTagKey = sampleKey + ":" + newGroupKey;
            
            TagList value = tags.get( oldTagKey );
            if( value == null ) continue;
            tags.remove( oldTagKey );
            tags.put   ( newTagKey, value );
        }
        
        groups.set( groups.indexOf( oldGroupKey ), newGroupKey );
        //groups.remove( oldGroupKey );
        //groups.add   ( newGroupKey );
        
        groupProperty.set(groups.size());
        tagProperty.set(tags.size());
    }
    
    private boolean hasSamples() {
        return !samples.isEmpty();
    }

    private boolean hasFeatures() {
        return !features.isEmpty();
    }

    private boolean hasGroups() {
        return !groups.isEmpty();
    }

    public Boolean isEmpty() {
        return tags.isEmpty();
    }

    public void clear() {
        samples.clear();
        groups.clear();
        features.clear();
        tags.clear();
        sampleProperty.set(samples.size());
        groupProperty.set(groups.size());
        tagProperty.set(tags.size());
    }
}
    