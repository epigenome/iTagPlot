/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Clustering;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 * 
 *
 * @author SHKim12
 */
public class WekaHierarchicalClustererWrapper implements ITagListClusterer {
    
    protected String m_LastErrorMessage;
    
    public String getLastErrorMessage() {
        return m_LastErrorMessage;
    }
    
    protected String m_LinkType;
    protected String m_DistanceFunction;
    protected IArffExporter m_ArffExporter;
        
    private String getLinkTypeOptionString( String linkType ) {
        switch( linkType ) {
            case "Single":            return "SINGLE";
            case "Complete":          return "COMPLETE";
            case "Average":           return "AVERAGE";
            case "Mean":              return "MEAN";
            case "Centroid":          return "CENTROID";
            case "Ward":              return "WARD";
            case "Adjusted Complete": return "ADJCOMPLETE";
            case "Neighbor Joining":  return "NEIGHBOR_JOINING";
            default:                  return null;
        }
    }
    
    private String getDistanceFunctionOptionString( String linkType ) {
        switch( linkType ) {
            case "Manhattan": return "weka.core.ManhattanDistance";
            case "Euclidean": return "weka.core.EuclideanDistance";
            case "Chebyshev": return "weka.core.ChebyshevDistance";
            default:          return null;
        }
    }
    
    public WekaHierarchicalClustererWrapper( String linkType, String distanceFunction, IArffExporter arffExporter ) {
        m_LinkType         = getLinkTypeOptionString( linkType );
        m_DistanceFunction = getDistanceFunctionOptionString( distanceFunction );
        m_ArffExporter     = arffExporter;
    }
    
    public static WekaFeatureWiseHierarchicalClustererWrapper getFeaturewiseClusterer(  String linkType, String distanceFunction  ) {
        return new WekaFeatureWiseHierarchicalClustererWrapper(linkType, distanceFunction);
    }
    
    public static WekaSeriesWiseHierarchicalClustererWrapper getSerieswiseClusterer(  String linkType, String distanceFunction  ) {
        return new WekaSeriesWiseHierarchicalClustererWrapper(linkType, distanceFunction);
    }
    
    @Override
    public String cluster( HashMap<String, List > data ) {
        
        try {
            File arff = m_ArffExporter.getArff( data );
            if( arff == null ) return null;
            
            FileInputStream is = new FileInputStream( arff.getAbsolutePath() );
            Instances instances =  ConverterUtils.DataSource.read( is );
            is.close();
            
            HierarchicalClusterer cl = new HierarchicalClusterer();
            
            String[] options = new String[6];
            options[0] = "-N"; // number of clusters should be "1"
            options[1] = "1";
            options[2] = "-L"; // linking type
            options[3] = m_LinkType;
            options[4] = "-A";
            options[5] = m_DistanceFunction;
                    
            cl.setOptions( options );
            
            cl.buildClusterer( instances );
            
            String newickString = cl.graph();
            
            if( !arff.delete() ) arff.deleteOnExit();
            
            return newickString;
            
        } catch (Exception ex) {
            //System.out.println( "[EXCEPTION] " + ex.toString() );
            m_LastErrorMessage = ex.getMessage();
            return null;
        }
    }
}
