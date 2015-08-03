package Headless;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import tagviz.MainFormController;
import tagviz.TagViz;

/**
 *
 * @author SHKim12
 */
public class ModGroupClustering implements Module  {
    TagViz app;
    MainFormController cntl;
    
    ArrayList<String> samples = new ArrayList<>();
    String numKMeansClusters;
    int numClusters;
    String distanceMetric;
    String prefix;
    boolean sampleInARow = true;
    String savefile;
    
    public ModGroupClustering( TagViz a, MainFormController c ) {
        app  = a;
        cntl = c;
    }
    
    private void showError( String msg ) {
        System.err.println( msg );
    }
    
    
    @Override
    public void showOptions() {
        System.out.println( "Options for [Cluster]");
        System.out.println( "\t"+"Clusters     "+": "+"the number of clusters");
        System.out.println( "\t"+"Distance     "+": "+"distance metric. {Euclidean, Manhattan}");
        System.out.println( "\t"+"Prefix       "+": "+"group name prefix");
        System.out.println( "\t"+"Output       "+": "+"output file to save the result" );
    }
    
    @Override
    public boolean applyOptions( Options options ) {
        for( Map.Entry<String,String> opt : options ) {
            final String key   = opt.getKey  ().toLowerCase();
            final String value = opt.getValue();
            
            if( value == null ) {
                switch( key ) {
                    case "help":
                        showOptions();
                        return false;
                    default:
                        showError( "Unknown option, or missing value : " + key );
                        return false;
                }
                //continue;
            }
            
            switch( key ) {
                case "sample"      : samples.add( value );      break;
                case "clusters"    : numKMeansClusters = value; break;
                case "distance"    : distanceMetric    = value; break;
                case "prefix"      : prefix            = value; break;
                case "output"      : savefile          = value; break;
                    
                default: 
                    showError( "Unknown option: " + key );
                    return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean checkParameters() {
        if( samples.isEmpty() ) {
            showError( "At least one sample should be specified" );
            return false;
        }
        
        if( numKMeansClusters == null ) {
            showError( "Number of clusters should be specified" );
            return false;
        }
        
        try{
            numClusters = Integer.parseInt( numKMeansClusters );
        } catch( NumberFormatException e ) {
            showError( "Number of clusters should be an integer" );
            return false;
        }
        
        if( distanceMetric == null ) {
            showError( "Distance metric should be specified" );
            return false;
        }
        
        switch( distanceMetric.toLowerCase() ) {
            case "euclidean": case "manhattan": break;
            default:
                showError( "Unsupported distance metric: " + distanceMetric );
                return false;
        }
        
        if( prefix == null ) {
            showError( "Group prefix metric should be specified" );
            return false;
        }
        
        if( savefile == null ) {
            showError( "Output filename should be specified" );
            return false;
        }
        
        return true;
    }
    
    @Override
    public void run() {
        DataLoader dl = new DataLoader( app, cntl );
        
        ArrayList<File> sampleFiles = new ArrayList<>();
        for( String filename : samples ) {
            File file = new File( filename );
            if( !file.exists() || !file.isFile() || !file.canRead() ) {
                showError( "Cannot read :" + filename );
                return;
            }
            sampleFiles.add( file );
        }
        dl.loadSamples( sampleFiles );
        System.out.println( "clustering features" );
        cntl.UploadClusteredFeature(numClusters, distanceMetric, prefix, sampleInARow, savefile);
    }
}
