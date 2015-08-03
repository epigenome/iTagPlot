package Objects;

import java.util.ArrayList;
import java.util.Collection;

public interface Group {

    public String getName();
    public void setName(String name);
    
    public Collection<String> features();
    public Collection<String> features(String sample);

    public void add(final String sample, final String feature);
    public void addAll(final String sample, final ArrayList<String> features);
    
    public void enable(final String sample, final String feature);
    
    public void changeSampleName(final String oldname, final String newname);
    
    public int count(final String sample);
}
