/*
 *  Handles serialization and deserialization
 */
package Objects;

import Controls.GraphControl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.security.AccessControlException;
import tagviz.TagViz;

public class Serializer {

    public void Serialize(GraphControl gCtrl) {
	try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(TagViz.settings_file))) {
	    out.writeObject(TagViz.visDirectory);
	    out.writeObject(TagViz.msigDirectory);
	    out.writeObject(TagViz.compDirectory);
	    out.writeObject(TagViz.annDirectory);
	    out.writeInt(TagViz.maxSeries);
	    out.writeObject(TagViz.ruleDelimeter);
	    out.writeObject(TagViz.ruleStartIndex);
	    out.writeObject(TagViz.ruleEndIndex);
	    out.writeObject(TagViz.gridCmd);
	    out.writeObject(TagViz.cores);
	    out.writeObject(TagViz.perl);
	    out.writeObject(TagViz.samtools);
            out.writeObject(TagViz.clusteredFeatureParameter);
	    out.writeObject(gCtrl);
        } catch (IOException | AccessControlException ex) {
            //System.out.println("I/O error:writing");
	}
    }
    
    public GraphControl Deserialize() {
        GraphControl g = null;
	int s = 0;
	int f = 0;
	
	try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(TagViz.settings_file))) {
	    while (true) {
		try {
		    Object o = in.readObject();
		    if (o instanceof File) {
			if      (++f == 1) TagViz.visDirectory = (File) o;
			else if (  f == 2) TagViz.msigDirectory = (File) o;
			else if (  f == 3) TagViz.compDirectory = (File) o;
			else if (  f == 4) TagViz.annDirectory = (File) o;
		    } else if (o instanceof String) {
			if (++s == 1) TagViz.ruleDelimeter = (String) o;
			else if (s == 2) TagViz.ruleStartIndex = TagViz.ruleEndIndex = (String) o;
			else if (s == 3) TagViz.ruleEndIndex = (String) o;
			else if (s == 4) TagViz.gridCmd = (String) o;
			else if (s == 5) TagViz.cores = (String) o;
			else if (s == 6) TagViz.perl = (String) o;
			else if (s == 7) TagViz.samtools = (String) o;
		    } else if (o instanceof GraphControl) {
			g = (GraphControl) o;
		    } else if (o instanceof TagViz.ClusteredFeatureParameter) {
                        TagViz.clusteredFeatureParameter = (TagViz.ClusteredFeatureParameter) o;
                    }
		} catch (OptionalDataException ex) {
		    int n = in.readInt();
		    if (n > 10) TagViz.maxSeries = n;
		} catch (ClassNotFoundException ex) {
	            return new GraphControl();
		} catch (ClassCastException ex) {
		    break;
		}
	    }
        } catch (IOException | AccessControlException ex) {
	}
	
        return g != null ? g : new GraphControl();
    }
}
