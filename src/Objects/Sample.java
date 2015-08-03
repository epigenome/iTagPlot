package Objects;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;
import org.bitbucket.kienerj.io.OptimizedRandomAccessFile;

public class Sample extends HashMap<String, Long> {
    public final static String ALL = "all";
    
    private String name;
    private String path;
    private int numCols;
    private OptimizedRandomAccessFile raf;
    private TagMap tagMap = new TagMap();
    private TagMap naMap = new TagMap();


    public Sample(String name, String path) throws FileNotFoundException {
        super();
        this.name = name;
        this.raf = new OptimizedRandomAccessFile(path, "r");
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumCols() {
	return numCols;
    }

    public void setNumCols(int numCols) {
	this.numCols = numCols;
    }

    public OptimizedRandomAccessFile getRaf() {
        return raf;
    }

    public void setRaf(OptimizedRandomAccessFile raf) {
        this.raf = raf;
    }

    public TagMap getNaMap() {
	return naMap;
    }

    public void setNaMap(TagMap naMap) {
	this.naMap = naMap;
    }
    
    public TagMap getTagMap() {
	return tagMap;
    }

    public void setTagMap(TagMap tagMap) {
	this.tagMap = tagMap;
    }
    
    public void addTags(String name, TagList sum, TagList nas)
    {
	tagMap.put(name, sum);
	 naMap.put(name, nas);
    }
    
    public TagList getTags(String name) throws IOException
    {
        if (name == null) name = ALL;
	return tagMap.containsKey(name) ? tagMap.get(name) : getTagsFor(name);
    }
    
    public TagList getTagsAt(String grp)
    {
        if (grp == null) grp = ALL;
	return tagMap.containsKey(grp) ? tagMap.get(grp) : null;
    }
    
    public TagList getTagsFor(String name) throws IOException, NoSuchElementException
    {
        if (!containsKey(name)) throw new NoSuchElementException("Unknown feature name: " + name);
	raf.seek(get(name));
	String[] sa = raf.readLine().split("\t");
	TagList tags = new TagList(numCols);
	for (int i = sa.length-numCols; i < sa.length; i++) {
	    try { tags.add(Float.parseFloat(sa[i])); }
	    catch (NumberFormatException ex) { tags.add(TagList.NA); }
	}
	return tags;
    }
    
    public TagList computeTags(Group grp) throws IOException
    {
	TagList sum = null;
	TagList nas = null;
	Collection<String> features = grp.features(getName());
	if (features == null) return null;
	
	for (String feat : features)
	{
            try
            {
                if (sum == null)
                {
                    sum = getTagsFor(feat);
		    nas = new TagList(sum.size());
		    for (Float f : sum) nas.add(f == null ? 1F : 0F);
                }
                else
                {
                    TagList f = getTagsFor(feat);
                    for (int i = 0; i < sum.size(); i++) {
			if (f.get(i) != null) sum.set(i, sum.get(i) == null ? f.get(i) : sum.get(i)+f.get(i));
			else                  nas.set(i, nas.get(i)+1);
		    }
                }
		
		grp.enable(getName(), feat);
            }
            catch (NoSuchElementException e)
            {
            }
	}
	
//	for (int i = 0; i < sum.size(); i++) sum.set(i, sum.get(i) / grp.size()); different number of features in samples
	if (sum != null) addTags(grp.getName(), sum, nas);
	return sum;
    }

    public TagList averageAt(String grp, int count)
    {
        if (grp == null) grp = ALL;
	return tagMap.containsKey(grp) ? tagMap.get(grp).average(count, naMap.get(grp)) : null;
    }
}
