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
import java.util.List;

/**
 *
 * @author SHKim12
 */
public class SeriesWiseArffExporter implements IArffExporter {
    @Override
    public File getArff(HashMap<String, List> data) {
        try {
            File arff = File.createTempFile("tagplot", ".arff");
            PrintWriter writer;
            writer = new PrintWriter( arff );
            
            writer.println("%");
            writer.println("@RELATION taglists");
            writer.println();
            writer.println("@ATTRIBUTE ID STRING" );
            int len = data.get( data.keySet().iterator().next() ).size(); // pick any list to obtain the length
            for( int i = 0; i < len; ++i ) {
                writer.printf("@ATTRIBUTE v%d NUMERIC\n", i );
            }
            
            writer.println();
            writer.println("@DATA");
            
            int count = 0;
            
            for( String key : data.keySet() ) {
                writer.printf( "%s", key );
                List series = data.get(key);
                for( Object f : series ) {
                    if( f != null && !f.equals( Float.NaN ) ) {
                        writer.printf( ",%f", (float)f);
                    } else {
                        writer.printf( ",?" );              // Missing value
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
