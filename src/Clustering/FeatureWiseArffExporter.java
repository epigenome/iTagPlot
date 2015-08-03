/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Clustering;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author SHKim12
 */
public class FeatureWiseArffExporter implements IArffExporter {
    
    @Override
    public File getArff(HashMap<String, List> data) {
        try {
            File arff = File.createTempFile("tagplot", ".arff");
            PrintWriter writer;
            writer = new PrintWriter( arff );
            
            HashSet<String> samples  = new HashSet<>();
            HashSet<String> features = new HashSet<>();
            boolean containsAllFeatures = false;
            
            // extract sample and feature keys from selected series.
            for( String key : data.keySet() ) {
                int i = key.indexOf(':');
                if( i > 0 ) {
                    samples.add( key.substring(0, i) );
                    features.add( key.substring( i+1 ) );
                } else {
                    samples.add( key );
                    containsAllFeatures = true;
                }
            }
            
            // print header
            writer.println("%");
            writer.println("@RELATION taglists");
            writer.println();
            writer.println("@ATTRIBUTE ID STRING" );
            int len = -1;
            // compute the series length per feature
            for (String featureKey : features) {
                int local_len = 0;
                
                for (String sampleKey : samples) {
                    String seriesKey = sampleKey + ":" + featureKey;
                    if( data.containsKey( seriesKey ) ) {
                        local_len += data.get( seriesKey ).size();
                    }
                }
                
                if( len == -1 ) {
                    len = local_len;
                } else if( len != local_len ) {
                    // [TODO] should be error. but ignore now, because we assume every series has the same size.
                }
            }
            for( int i = 0; i < len; ++i ) {
                writer.printf("@ATTRIBUTE v%d NUMERIC\n", i );
            }
            
            writer.println();
            
            int count = 0;
            
            // print data
            writer.println("@DATA");
            
            if( containsAllFeatures ) {
                String featureKey = "ALL";
                while( features.contains( featureKey ) ) {
                    featureKey = "_" + featureKey;
                }
                writer.printf( "%s", featureKey );
                for( String key : samples ) {
                    List series = data.get(key);
                    for( Object f : series ) {
                        if( f != null && !Float.isNaN( (Float)f ) ) {
                            writer.printf( ",%f", (float)f);
                        } else {
                            writer.printf( ",?" );              // Missing value
                        }
                    }
                }
                writer.println();
                ++count;
            }
            for( String featureKey : features ) {
                writer.printf( "%s", featureKey );
                for( String sampleKey : samples ) {
                    List series = data.get(sampleKey + ":" + featureKey);
                    for( Object f : series ) {
                        if( f != null && !Float.isNaN( (Float)f ) ) {
                            writer.printf( ",%f", (float)f);
                        } else {
                            writer.printf( ",?" );              // Missing value
                        }
                    }
                }
                writer.println();
                ++count;
            }
            
            writer.close();
            if( count <= 1 ) {
                arff.delete();
                return null;
            }
            
            return arff;
            
        } catch (IOException ex) {
            System.out.println( "[EXCEPTION] " + ex.getMessage() );
            return null;
        }
    }
}
