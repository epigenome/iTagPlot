/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Clustering;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author SHKim12
 */
public class WekaKMeansClustererWrapper implements ITagListClusterer  {

    protected String m_LastErrorMessage;
    
    public String getLastErrorMessage() {
        return m_LastErrorMessage;
    }
    
    protected int m_NumberOfClusters;
    protected String m_DistanceFunction;
    protected IArffExporter m_ArffExporter;
        
    public WekaKMeansClustererWrapper( int numberOfClusters, String distanceFunction, IArffExporter arffExporter ) {
        m_NumberOfClusters = numberOfClusters;
        m_DistanceFunction = getDistanceFunctionOptionString( distanceFunction );
        m_ArffExporter     = arffExporter;
    }
    
    private String getDistanceFunctionOptionString( String linkType ) {
        switch( linkType ) {
            case "Manhattan": return "weka.core.ManhattanDistance";
            case "Euclidean": return "weka.core.EuclideanDistance";
            case "Chebyshev": return "weka.core.ChebyshevDistance";
            default:          return null;
        }
    }
    public ArrayList<String>[] classify(HashMap<String, List> data) {
        return classify( data, false );
    }
    
    public ArrayList<String>[] classify(HashMap<String, List> data, boolean clearData ) {
        ArrayList<String>[] clusterResult;
        try{
            File arff = m_ArffExporter.getArff( data );
            int nSize = data.size();
            if( arff == null ) return null;
            if( clearData ) data.clear();
            
            FileInputStream is = new FileInputStream( arff.getAbsolutePath() );
            Instances instances =  ConverterUtils.DataSource.read( is );
            is.close();
            
            String[] keys = new String[ instances.numInstances() ];
            for( int i = 0; i < instances.numInstances(); ++i ) {
                Instance instance = instances.instance(i);
                keys[i] = instance.stringValue(0); // assume that the 0th attribute is the key string
            }
            
            instances.deleteStringAttributes();
            
            SimpleKMeans cl = new SimpleKMeans();
            
            int numClusters = m_NumberOfClusters<nSize?m_NumberOfClusters:nSize;
            
            String[] options = new String[5];
            options[0] = "-O";
            options[1] = "-N";
            options[2] = Integer.toString( numClusters );
            options[3] = "-A";
            options[4] = m_DistanceFunction;
            
            cl.setOptions( options );
            
            //System.out.println( "Clustering" );
            cl.buildClusterer( instances );
            
            //System.out.println( "Create ArrayList" );
            clusterResult = new ArrayList[ m_NumberOfClusters ];
            for( int i = 0; i < m_NumberOfClusters; ++i ) {
                clusterResult[i] = new ArrayList<>();
            }
            
            //System.out.println( "Assigning" );
            int[] assignment = cl.getAssignments();
            for( int i = 0; i < assignment.length; ++i ) {
                clusterResult[ assignment[i] ].add( keys[i] );
            }
            
            //System.out.println( "Done" );
            if( !arff.delete() ) arff.deleteOnExit();
        } catch( Exception ex ) {
            //System.out.println( "[EXCEPTION] " + ex.getMessage() );
            m_LastErrorMessage = ex.getMessage();
            return null;
        }
        
        return clusterResult;
    }
    
    @Override
    public String cluster(HashMap<String, List> data) {
        
        try {
            ArrayList<String>[] clusterResult = classify(data);
            if( clusterResult == null ) return null;
            
            //System.out.println( "Building newick String" );
            StringBuilder newickString = new StringBuilder();
            
            newickString.append( "Newick:(" );
            for( int i = 0; i < m_NumberOfClusters; ++i ) {
                
                if( clusterResult[i].size() > 1 ) {
                    newickString.append( '(' );
                }
                
                for( int j = 0; j < clusterResult[i].size(); ++j ) {
                    newickString.append( clusterResult[i].get(j) );
                    newickString.append( ":1," );
                }
                if( clusterResult[i].size() > 1 ) {
                    newickString.deleteCharAt( newickString.length() - 1 );
                    newickString.append( "):1," );
                }
            }
            newickString.deleteCharAt( newickString.length() - 1 );
            newickString.append( ')' );
            
            return newickString.toString();
            
        } catch (Exception ex) {
            //System.out.println( "[EXCEPTION] " + ex.toString() );
            m_LastErrorMessage = ex.getMessage();
            return null;
        }
    }
    
}
