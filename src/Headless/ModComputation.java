package Headless;

import Objects.ProcessExecutor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import tagviz.MainFormController;
import tagviz.TagViz;

/**
 *
 * @author SHKim12
 */
public class ModComputation implements Module {
    Process child;
    TagViz app;
    MainFormController cntl;
    
    int status = 0;      // 0 - normal, 1 - running, 2 - done
    final ObservableList<String> listItems = FXCollections.observableArrayList();
    String conf;         // Annotation Configuration File
    String base;         // Annotation Base Directory
    String output;       // Output Directory
    int mode;            // Running Mode, 0:serial, 1:Multicore, 2:Grid.
    String thread;       // Number of Threads
    String gridcmd;      // Grid Command
    int datatype;        // Data Type, 0:Enrichment, 1:BetaScore.
    String fragmentsize; // Fragment Size
    String columnnumber; // Column Number
    String format;       // File Type
    String perl     = null; // pearl path
    String fragFile = null; // fragment File
    String samtools = null; // samtools path
    String test = null; // Unit test
    
    public ModComputation( TagViz a, MainFormController c ) {
        app  = a;
        cntl = c;
    }
    
    private void showError( String msg ) {
        System.err.println( msg );
    }
    
    private int getMode( String mode ) {
        switch( mode.toLowerCase() ) {
            case "serial"   : return 0;
            case "multicore": return  1;
            case "grid"     : return  2;
            default         : return -1;
        }
    }
    
    private int getDataType( String type ) {
        switch( type.toLowerCase() ) {
            case "enrichment": return  0;
            case "betascore" : return  1;
            default          : return -1;
        }
    }
    
    @Override
    public void showOptions() {
        System.out.println( "Options for [Compute]");
        System.out.println( "\t"+"Sample       "+": "+"sample file");
        System.out.println( "\t"+"Conf         "+": "+"annotation configuration file");
        System.out.println( "\t"+"Base         "+": "+"annotation base directory");
        System.out.println( "\t"+"Output       "+": "+"output directory");
        System.out.println( "\t"+"Mode         "+": "+"running mode. { Serial, Multicore, Grid }" );
        System.out.println( "\t"+"Threads      "+": "+"the number of threads. used with Mode=Multicore.");
        System.out.println( "\t"+"GridCmd      "+": "+"grid command. used with Mode=Grid.");
        System.out.println( "\t"+"DataType     "+": "+"data type. {Enrichment, BetaScore}");
        System.out.println( "\t"+"FragmentSize "+": "+"Fragment Size. used with DataType=Enrichment.");
        System.out.println( "\t"+"ColumnNumber "+": "+"Column Number. used with DataType=BetaScore");
        System.out.println( "\t"+"FileType     "+": "+"File Format. {bed, bam}");
        System.out.println( "\t"+"FragFile     "+": "+"fragment file");
        System.out.println( "\t"+"Perl         "+": "+"specifies the path for Perl");
        System.out.println( "\t"+"Samtools     "+": "+"specifies the path for SamTools");
    }
    
    @Override
    public boolean applyOptions( Options options ) {
        for( Entry<String,String> opt : options ) {
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
                case "sample"      : listItems.add( value );              break;
                case "conf"        : conf         = value;                break;
                case "base"        : base         = value;                break;
                case "output"      : output       = value;                break;
                case "mode"        : mode         = getMode( value );     break;
                case "threads"     : thread       = value;                break;
                case "gridcmd"     : gridcmd      = value;                break;
                case "datatype"    : datatype     = getDataType( value ); break;
                case "fragmentsize": fragmentsize = value;                break;
                case "columnnumber": columnnumber = value;                break;
                case "filetype"    : format       = value;                break;
                case "fragfile"    : fragFile     = value;                break;
                case "perl"        : perl         = value;                break;
                case "samtools"    : samtools     = value;                break;
                case "test"        : test         = value;                break;
                    
                default: 
                    showError( "Unknown option: " + key );
                    return false;
            }
        }
        return true;
    }
    
    private boolean checkFileValidity( String file ) {
        File f = new File( file );
        if( !f.exists() || !f.isFile() ) {
            return false;
        }    
        return true;
    }
    
    private boolean checkDirectory( String dir ) {
        File f = new File( dir );
        if( !f.exists() || !f.isDirectory() ) {
            return false;
        }    
        return true;
    }
    
    @Override
    public boolean checkParameters() {
        if( listItems.isEmpty() ) {
            showError( "No sample specified" );
            return false;
        }
        
        if( conf == null ) {
            showError( "No annotation configuration file specified" );
            return false;
        }
        
//        if( base == null ) {
//            showError( "No annotation base directory specified" );
//            return false;
//        }
        
        if( output == null ) {
            showError( "No output directory specified" );
            return false;
        }
        
        //Check file & directory validity -- currently removed.
        // Sample
        for( String path : listItems ) {
            if( !checkFileValidity( path ) ) {
                showError( "Cannot find file: " + path );
                return false;
            }
        }
        
        // Configuration File
        if( !checkFileValidity( conf ) ) { 
            showError( "Cannot find file: " + conf );
            return false; 
        }
        
        // Configuration Base Dir
        if( base != null && !checkDirectory( base   ) ) { 
            showError( "Invalid annotation base directory: " + base );
            return false; 
        }
        
        // output
//        if( !checkDirectory( output ) ) { 
//            showError( "Invalid output directory: " + base );
//            return false; 
//        }
        
        if( mode == -1 ) {
            showError( "Invalid mode; should be one of {Serial, Multicore, Grid}." );
            return false;
        }
        
        if( thread != null && mode != 1 ) {
            showError( "[warning] Option Threads is used only with the Multicore mode; will be ignored.");
        }
        
        if( mode == 1 ) {
            if( thread == null  ) {
                showError( "[warning] Number of threads should be specified for the Multicore mode." );
                return false;
            }
            try {
                int v = Integer.parseInt( thread );
            } catch( NumberFormatException ex ) {
                showError( "Number of threads should be an integer" );
                return false;
            }
        }
        
        if( gridcmd != null && mode != 2 ) {
            showError( "[warning] Option GridCmd is used only with the Grid mode; will be ignored.");
        }
        
        if( gridcmd == null && mode == 2 ) {
            showError( "[warning] Grid Command should be specified for the Grid mode." );
            return false;
        }
        
        if( datatype == -1 ) {
            showError( "Invalid Data Type; should be one of {Enrichment, BetaScore}.");
            return false;
        }
        
        if( fragmentsize != null && mode != 0 ) {
            showError( "[warning] Option FragmentSize is used only with the Enrichment data types; will be ignored.");
        }
        
        if( columnnumber != null && mode != 1 ) {
            showError( "[warning] Option ColumnNumber is used only with BetaScore data types; will be ignored.");
        }
        
        if( format == null ) {
            showError( "File format not specified." );
            return false;
        }
        
        switch( format.toLowerCase() ) {
            case "bed": case "bam": break;
            default:
                showError( "Unknown file format; should be one of {bed, bam}." );
                return false;
        }
        
        return true;
    }
    
    // almost identical to ComputationFormController.buildCommand()
    String buildCommand( String batchScript, String outfile, String conffile, String dir, int runMode, String thread, String grid, int dataType, String fragment, String column, String format ) {
        // runMode 0: serial, 1: core, 2: grid
        
	StringBuilder sb = new StringBuilder();
        if (perl != null) sb.append(perl).append(' ');
        sb.append( batchScript );
        sb.append(" -o ").append( outfile );
        sb.append(" -conf ").append( conffile );
        if (dir != null && !dir.isEmpty()) sb.append(" -base ").append(dir);
        if (runMode == 1 || runMode == 2) sb.append(" -thread ").append(thread);
        if (runMode == 2) sb.append(" -grid ").append( grid );
        if (dataType == 0) {
            if (!fragment.isEmpty()) sb.append(" -fragment ").append(fragment);
            if (fragFile != null) sb.append(" -size ").append(fragFile);
        } else if (dataType == 1) {
            sb.append(" -m -score ").append(column);
        }
        sb.append(" -out ").append(outfile);
        sb.append(" -type ").append(format);
        if (samtools != null) sb.append(" -samtools ").append(samtools);
        for (String s : listItems) sb.append(' ').append(s);
	return sb.toString();
    }
    
    @Override
    public void run() {
	String dir;
        try {
            dir = ProcessExecutor.deploy(TagViz.featureScript, TagViz.batchScript);
        } catch (IOException ex) {
	    System.err.println("fail: " + ex.getMessage());
            return;
        }
        
	final String cmd = buildCommand( dir+File.separator+TagViz.batchScript, output, conf, base, mode, thread, gridcmd, datatype, fragmentsize, columnnumber, format);
	status = 1;
        //System.out.println( cmd );

	Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
		execute(cmd);
                return null;
            }
        };

        Thread t = new Thread(task);
	t.start();
	
	if (test != null) {
	    // only for JUnit test
	    try { t.join(); } 
	    catch (InterruptedException ex) { System.err.println(ex.getMessage()); }
	}
    }
    
     private boolean execute(String cmd) {
	System.out.println(cmd + "\n");
	    
	try {
	    child = Runtime.getRuntime().exec(cmd);
	    
	    BufferedReader bo_stream = new BufferedReader(new InputStreamReader(child.getInputStream()));
	    Thread thread_stdout = new Thread(new CommandRunThread(bo_stream, false, System.out ));
	    thread_stdout.start();
	    
	    BufferedReader be_stream = new BufferedReader(new InputStreamReader(child.getErrorStream()));
	    Thread thread_error = new Thread(new CommandRunThread(be_stream, true, System.err ));
	    thread_error.start();
	    
	    child.waitFor();
	    
	    thread_stdout.join(100);
	    thread_error.join(100);
	    if (thread_stdout.isAlive())
		thread_stdout.interrupt();
	    if (thread_error.isAlive())
		thread_error.interrupt();
	    
	    if(child.exitValue() != 0) {
		System.err.print( "Exit code: " + child.exitValue() + "\n");
		status = 0;
		return false;
	    }
	} catch (IOException | InterruptedException ex) {
	    System.err.print( "\n[ERROR] " + (ex.getMessage().isEmpty() ? "" : ex.getMessage()) + "\n");
	}
	
	status = 2;
	return true;
    }
     
     class CommandRunThread implements Runnable{
	BufferedReader bufferReader;
	boolean flag;
        PrintStream out;
	
	public CommandRunThread(BufferedReader br, boolean b, PrintStream o) {
	    bufferReader = br;
	    flag = b;
            out = o;
	}
	
	public void run()
	{	
	    try
	    {
		while (true) {
		    final String str = bufferReader.readLine();
		    if (str == null) break;
		    if (flag) out.print ("[ERROR] ");
		    out.print (str + "\n");
		} 
	    }
	    catch(final IOException ex) { 
		out.print ("\n[ERROR] " + ex.getMessage() + "\n");
	    }
	}		
    }
}
