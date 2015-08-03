/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Headless;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tagviz.MainFormController;
import tagviz.TagViz;

/**
 *
 * @author SHKim12
 */
public class Main extends Application {

    static WritableImage snapshot;

    static String[] cmdArguments;
    
    static MainFormController cntl = new MainFormController();
    static TagViz app = new TagViz();
    
    static AnchorPane root = new AnchorPane();

    public static void main( String[] args ) {
        cmdArguments = args;
        Application.launch(args);
    }
    
    public static class FxPlatformExecutor {
        public static void runOnFxApplication(Runnable task) {
            if (Platform.isFxApplicationThread()) {
                task.run();
            } else {
                Platform.runLater(task);
            }
        }
    }
    
    private void showOptions() {
        System.out.println( "Available commands:");
        
        System.out.println( "\t"+"Compute  "+" : "+"compute tag densities.");
        System.out.println( "\t"+"Cluster  "+" : "+"cluster features into group.");
        System.out.println( "\t"+"Visualize"+" : "+"draw a graph with tag density data.");
        
    }
    
    private void run( ) {        
        Options opt = Options.parseCmdLine( cmdArguments );
        
        Module mod = null;
        
        if( cmdArguments.length == 0 ) {
            showOptions();
            return;
        }
        
        switch( cmdArguments[0].toLowerCase() ) {
            case "compute"  : mod = new ModComputation    ( app, cntl );       break;
            case "cluster"  : mod = new ModGroupClustering( app, cntl );       break;
            case "visualize": mod = new ModVisualization  ( app, cntl, root ); break;
            case "help"     : showOptions(); return;
            default         : System.err.println( "Unknown command: " + cmdArguments[0] );
        }
        if( mod == null ) return;
        
        if( cmdArguments.length == 1 ) {
            mod.showOptions();
            return;
        }
        
        if( opt.getHashMap().containsKey( "cf" ) ) {
            ArrayList<String> cfList = opt.getHashMap().get( "cf" );
            opt.getHashMap().remove( "cf" );
            for( String cf : cfList ) {
                Options opt_file = Options.parseConfFile( cf );
                if( opt_file == null || !mod.applyOptions( opt_file ) ) {
                    System.err.println( "fail: option reading from " + cf );
                    return;
                }
            }
        }
        
        if( !mod.applyOptions( opt ) ) {
            System.err.println( "fail: option reading." );
            return;
        }
        
        if( !mod.checkParameters() ) {
            System.err.println( "fail: check paremeters." );
            return;
        }
        
        mod.run();
    }
    
    @Override
    public void start(Stage stage) {
        cntl.setApp( app );
        stage.setScene( new Scene( root ) );
        root.getStylesheets().add( "/tagviz/FormStyle.css" );
        
        run( );
        
        Platform.exit();
    }
}
