package Headless;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author SHKim12
 */
public class Options extends StringHashMultimap {
    
    HashMap<String,String> macro = new HashMap<>();
    
    public static Options parseCmdLine( String[] args ) {
        Options opt = new Options();
        for( int i = 1; i < args.length; ++i ) {
            if( args[i].length() <= 1 ) {
                System.err.println( "Empty argument.");
                return null;
            }
            int j = 0;
            while( args[i].charAt(j) == '-' ) ++j;
            opt.parse( args[i].substring( j ));
        }
        return opt;
    }
    
    public static Options parseConfFile( String file )  {
        File           f;
        BufferedReader in;
        Options        opt = null;
        try {
            f  = new File( file );
            in = new BufferedReader( new FileReader( file ) );
            opt = new Options();
            
            StringBuilder buffer = new StringBuilder();
            String line;
            while( ( line = in.readLine() ) != null ) {
                line = line.trim();
                if( line.length() == 0 || line.charAt(0) == '%' || line.charAt(0) == '#' ) {
                    continue;
                }
                if( line.charAt( line.length() -1 ) != ',' ) {
                    opt.parse( buffer.append(line).toString() );
                    buffer.setLength(0);
                } else {
                    buffer.append(line);
                }
            }
            
            in.close();
        } catch (FileNotFoundException ex) {
            System.err.println( "File not found: " + file );
            return null;
        } catch (IOException e) {
            System.err.println( "IOException: " + e.getMessage() );
            return null;
        }
        
        return opt;
    }
    
    public void parse( String option ) {
        int delim = option.indexOf( '=' );
        if( delim == -1 ) {
            // binary option
            this.put( option, null );
            return;
        }
        String key   = option.substring( 0, delim ).trim();
        String value = option.substring( delim+1  ).trim();
        
        for( Entry<String,String> e : macro.entrySet() ) {
            if( value.contains( e.getKey() ) ) {
                value = value.replace( e.getKey(), e.getValue() );
            }
        }
        
        if( key.length() > 1 && key.charAt(0) == '$' ) {
            key = key.replace( "{", "" ).replace( "}", "");
            key = "${" + key.substring( 1 ) + "}";
            macro.put( key, value );
            return;
        } 
        
        this.put( key, value );
    }
}
