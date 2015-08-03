package Headless;

import Controls.QuantityDialog;
import Objects.IDConvention;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import tagviz.MainFormController;
import tagviz.TagViz;

/**
 *
 * @author SHKim12
 */
public class DataLoader {
    TagViz app;
    MainFormController cntl;
    
    public DataLoader( TagViz a, MainFormController c ) {
        app  = a;
        cntl = c;
    }
    
    public void loadSamples( List<File> files ) {
	String[] uniqueName = cntl.uniqueNames( files, true );
	
	for( int i = 0; i < uniqueName.length; ++i ) {
            String path;
            try {
                path = files.get(i).getCanonicalPath();
            } catch (IOException ex) {
                continue;
            }
            System.out.println( "loading sample: " + path );
            cntl.loadSample( path, uniqueName[i], app );
	}
	//System.out.println( "sample loaded!" );
    }
    
    public IDConvention prepareIDConvention(  String delimiter, int indexStart, int indexEnd ) {
        //String delimiter  = TagViz.ruleDelimeter;
        //int    indexStart = Integer.parseInt( TagViz.ruleStartIndex );
        //int    indexEnd   = Integer.parseInt( TagViz.ruleEndIndex   );
        IDConvention rule = IDConvention.create( delimiter, indexStart, indexEnd );
        rule.convert(app.featMap.keySet());
        return rule;
    }
    
   public void loadGroups( List<File> files, IDConvention rule ) {
	// name
	String[] uniqueName = cntl.uniqueNames( files, true );
	
	// ID conversion 
        if( rule != null ) {            
            for (String grp : uniqueName) app.idMap.put(grp, rule);
        }
        
	//
	for( int i = 0; i < uniqueName.length; ++i ) {
            String path;
            try {
                path = files.get(i).getCanonicalPath();
            } catch (IOException ex) {
                continue;
            }
            System.out.println( "loading group: " + path );
            cntl.loadGroup ( path, uniqueName[i], app);
	}
	//System.out.println( "group loaded!" );
    }
   
    public void loadQuantityGroup(final IDConvention rule, final String path, final String[] criteriaNames, final Float[] creteriaValues, final Boolean[] columnCategory, final boolean isGCT, final QuantityDialog.Type type, final HashMap map) {
        System.out.println( "loading quantity group: " + path );
        final String[] names = cntl.uniqueNames( criteriaNames, false);
        
        if( rule != null ) {            
            for (String grp : names) app.idMap.put(grp, rule);
        }
        
        switch( type ) {
            case MICROARRAY:
            case RNASEQ:
            case BETA:
                cntl.loadQuantityByValue(path, names, creteriaValues, columnCategory, isGCT, map);
                break;
            case QUANTILE:
                cntl.loadQuantityByRelativeRank(path, names, creteriaValues, columnCategory, isGCT, map);
                break;
            default:
                System.err.println( "Unknown quantity type" );
                return;
        }
    }
    
    public void loadClusteredGroup( final String path ) {
        System.out.println( "loading cluster group: " + path );
        try {
            cntl.loadClusteringResult( path );
        } catch (Exception ex) {
            System.err.println( ex.getMessage() );
        }
    }
}
