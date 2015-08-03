package Headless;

import Controls.GraphControl;
import Controls.QuantityDialog;
import Controls.SaveControl;
import Objects.IDConvention;
import Objects.TagMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import tagviz.MainFormController;
import tagviz.TagViz;

/**
 *
 * @author SHKim12
 */
public class ModVisualization implements Module  {
    WritableImage snapshot;
    
    TagViz app;
    MainFormController cntl;
    AnchorPane pane;
    GraphControl gCtrl = new GraphControl();
    
    ArrayList<String> samples        = new ArrayList<>();
    ArrayList<String> simplegroups   = new ArrayList<>();
    ArrayList<String> quantitygroups = new ArrayList<>();
    ArrayList<String> clustergroups  = new ArrayList<>();
    
    ArrayList<String> drawlist       = new ArrayList<>();
    
    String type;
    String width;
    String height;
    String output;
    
    String heatColor;
    String heatValue;
    
    public ModVisualization( TagViz a, MainFormController c, AnchorPane p ) {
        app  = a;
        cntl = c;
        pane = p;
    }
   
    private void showError( String msg ) {
        System.err.println( msg );
    }
    
    @Override
    public void showOptions() {
        System.out.println( "Options for [Visualize]");
        System.out.println( "\t"+"Sample            "+": "+"load sample tag density filename");
        
        System.out.println( "\t"+"Group             "+": "+"load group. Group=filename[,additional options]");
        System.out.println( "\t    "+"Delim         "+": "+"(ID convention) delimiter");
        System.out.println( "\t    "+"Start         "+": "+"(ID convention)start offset");
        System.out.println( "\t    "+"End           "+": "+"(ID convention)end offset");
        
        System.out.println( "\t"+"Quantity          "+": "+"load quantity group. Quantity=filename[,additional options]");
        System.out.println( "\t    "+"Delim         "+": "+"(ID convention) delimiter");
        System.out.println( "\t    "+"Start         "+": "+"(ID convention)start offset");
        System.out.println( "\t    "+"End           "+": "+"(ID convention)end offset");
        System.out.println( "\t    "+"Type          "+": "+"quantity type. {Microarray, RNASeq, Beta, Quantile}");
        System.out.println( "\t    "+"GCT           "+": "+"indicates GCT if it is set");
        System.out.println( "\t    "+"CriteriaNames "+": "+"criteria names delimited by colon. e.g.) CriteriaNames=low:med:high");
        System.out.println( "\t    "+"CriteriaValues"+": "+"criteria values delimited by colon. e.g.) CriteriaValues=0.1:0.5:MAX");
        System.out.println( "\t    "+"FeatColumn    "+": "+"specifies the feature (0-based) column index");
        System.out.println( "\t    "+"SampleColumns "+": "+"specifies the sample (0-based) column indexes delimited by colon. e.g.) SampleColumns=1:2:3");
        System.out.println( "\t    "+"Map           "+": "+"sample name mapping. e.g.) Map=SampleName:NameInFile");
        
        System.out.println( "\t"+"ClusterGroup      "+": "+"load clustered group." );
        
        System.out.println( "\t"+"Select            "+": "+"select series" );
        
        System.out.println( "\t"+"Type              "+": "+"graph type. {Chart,Heatmap}" );
        System.out.println( "\t"+"Width             "+": "+"graph width" );
        System.out.println( "\t"+"Height            "+": "+"graph height" );
        System.out.println( "\t"+"Output            "+": "+"output file" );
        
        System.out.println( "" );
        System.out.println( "Graph preference (for Chart)" );
        System.out.println( "\t"+"ChartType         "+": "+"chart type {Line(default), Area}" );
        System.out.println( "\t"+"Legend            "+": "+"legend location {Top(default), Right, Bottom, Left, None}" );
        System.out.println( "\t"+"Symbol            "+": "+"symbol type {Circle, Square, Diamond, None(default)}" );
        System.out.println( "\t"+"LineWeight        "+": "+"line weight in chart" );
        
        System.out.println( "Cluster preference (for Heatmap)" );
        System.out.println( "\t"+"Clustering        "+": "+"clustering type. {No, Hierarchical(default), KMeans}" );
        System.out.println( "\t"+"NumCluster        "+": "+"the number of clusters (for KMeans)" );
        System.out.println( "\t"+"Linkage           "+": "+"linkage type. {Single, Complete, Average(default), Mean, Centroid, Ward, AdjustedComplete, NeighborJoining}" );
        System.out.println( "\t"+"Distance          "+": "+"distance metric. {Euclidean(default), Manhattan, Chebyshev}" );
        System.out.println( "\t"+"Grouping          "+": "+"grouping method. {No(default), Row, Column}" );
        
        System.out.println( "Color preference" );
        System.out.println( "\t"+"DrawingAreaColor  "+": "+"color for drawing area (#RRGGBB)" );
        System.out.println( "\t"+"GraphAreaColor    "+": "+"color for graph area (#RRGGBB)" );
        System.out.println( "\t"+"GraphBorderColor  "+": "+"color for graph border (#RRGGBB)" );
        System.out.println( "\t"+"GraphTitleColor   "+": "+"color for graph title (#RRGGBB)" );
        System.out.println( "\t"+"AxisLabelColor    "+": "+"color for graph axis labels (#RRGGBB)" );
        System.out.println( "\t"+"LegendLabelColor  "+": "+"color for legend labels (#RRGGBB)" );
        System.out.println( "\t"+"LegendBgColor     "+": "+"color for legend background (#RRGGBB)" );
        System.out.println( "\t"+"GridXColor        "+": "+"color for grid lines perpendicular to X-axis (#RRGGBB)" );
        System.out.println( "\t"+"GridYColor        "+": "+"color for grid lines perpendicular to Y-axis (#RRGGBB)" );
        System.out.println( "\t"+"TickLabelColor    "+": "+"color for tick lables" );
        System.out.println( "\t"+"TickLineColor     "+": "+"color for tick lines" );
        System.out.println( "\t"+"SymbolFillColor   "+": "+"color for filling the symbols on curves" );
        
        System.out.println( "\t"+"HeatColor         "+": "+"heatmap color scheme" );
        System.out.println( "\t"+"HeatValue         "+": "+"criteria for heatmap color assignment" );
        
        System.out.println( "Label preference" );
        System.out.println( "\t"+"Title             "+": "+"graph title" );
        System.out.println( "\t"+"TitleFont         "+": "+"font style for title. e.g.) TitleFont=FontName:FontSize" );
        System.out.println( "\t"+"XLabel            "+": "+"X-axis label" );
        System.out.println( "\t"+"YLabel            "+": "+"Y-axis label" );
        System.out.println( "\t"+"AxesFont          "+": "+"font style for axis labels." );
        System.out.println( "\t"+"TickFont          "+": "+"font style for tick labels." );
        System.out.println( "\t"+"LegendFont        "+": "+"font style for legend." );
        
        System.out.println( "Grid preference" );
        System.out.println( "\t"+"XMaxTickCount     "+": "+"maximum tick count for X-axis" );
        System.out.println( "\t"+"YMaxTickCount     "+": "+"maximum tick count for Y-axis" );
        System.out.println( "\t"+"TickWidth         "+": "+"tick width" );
        System.out.println( "\t"+"TickLength        "+": "+"tick length" );
        System.out.println( "\t"+"HideXGridline     "+": "+"hide gridlines perpendicular to X-axis if set" );
        System.out.println( "\t"+"HideXTickLabel    "+": "+"hide tick labels on X-axis if set" );
        System.out.println( "\t"+"HideXTickMark     "+": "+"hide tick marks on X-axis if set" );
        System.out.println( "\t"+"HideYGridline     "+": "+"hide gridlines perpendicular to Y-axis if se" );
        System.out.println( "\t"+"HideYTickLabel    "+": "+"hide tick labels on Y-axis if set" );
        System.out.println( "\t"+"HideYTickMark     "+": "+"hide tick marks on Y-axis if set" );
        
        System.out.println( "Transform preference" );
        System.out.println( "\t"+"Transform         "+": "+"transform type. {No(default), Logarithm, Standardization, Quantile}" );
        System.out.println( "\t"+"Constant          "+": "+"adding constant before log transformation" );
        System.out.println( "\t"+"Smoothing         "+": "+"smoothing factor" );
        
    }
    
    @Override
    public boolean applyOptions( Options options ) {
        for( Map.Entry<String,String> opt : options ) {
            final String key   = opt.getKey  ().toLowerCase();
            final String value = opt.getValue();
            
            if( value == null ) {
                switch( key ) {
                    // hide switches in grid preference
                    case "hidexgridline"   : gCtrl.setXGridLineStyle(GraphControl.STROKE.NONE); break;
                    case "hideygridline"   : gCtrl.setYGridLineStyle(GraphControl.STROKE.NONE); break;
                    case "hidexticklabel"  : gCtrl.setShowXlabels   (Boolean.FALSE); break;
                    case "hidextickmark"   : gCtrl.setShowXTicks    (Boolean.FALSE); break;
                    case "hideyticklabel"  : gCtrl.setShowYlabels   (Boolean.FALSE); break;
                    case "hideytickmark"   : gCtrl.setShowYTicks    (Boolean.FALSE); break;
                    
                    case "help":
                        showOptions();
                        return false;
                    default:
                        showError( "Unknown option, or missing value : " + key );
                        return false;
                }
                continue;
            }
            
            switch( key ) {
                // series load/select
                case "sample"          : samples       .add( value ); break;
                case "group"           : simplegroups  .add( value ); break;
                case "quantity"        : quantitygroups.add( value ); break;
                case "clustergroup"    : clustergroups .add( value ); break;
                case "select"          : drawlist      .add( value ); break;
                    
                // output
                case "type"            : type   = value; break;
                case "width"           : width  = value; break;
                case "height"          : height = value; break;
                case "output"          : output = value; break;
                    
                // graph preference
                case "charttype"       : gCtrl.setType  ( getParam_charttype( value ) ); break;
                case "legend"          : gCtrl.setLegend( getParam_legend   ( value ) ); break;
                case "symbol"          : gCtrl.setSymbol( getParam_symbol   ( value ) ); break;
                case "lineweight"      : gCtrl.setAllLineWeight(GraphControl.GRAPHTYPE.LINE, Integer.parseInt( value ) ); break;

                // cluster preference
                case "clustering"      : gCtrl.setClustering                ( getParam_clustering( value ) ); break;
                case "numcluster"      : gCtrl.setKMeanNumClusters          ( Integer.parseInt   ( value ) ); break;
                case "linkage"         : gCtrl.setClusteringLinkType        ( getParam_linkage   ( value ) ); break;
                case "distance"        : gCtrl.setClusteringDistanceFunction( getParam_distance  ( value ) ); break;
                case "grouping"        : gCtrl.setClusteringGroup           ( getParam_grouping  ( value ) );  break;

                // color preference **
                case "drawingareacolor": gCtrl.setBackgroundColor  (Color.web(value)); break;
                case "graphareacolor"  : gCtrl.setGraphAreaColor   (Color.web(value)); break;
                case "graphbordercolor": gCtrl.setGraphBorderColor (Color.web(value)); break;
                case "graphtitlecolor" : gCtrl.setTitleColor       (Color.web(value)); break;
                case "axislabelcolor"  : gCtrl.setAxisColor        (Color.web(value)); break;
                case "legendlabelcolor": gCtrl.setLegendLabelsColor(Color.web(value)); break;
                case "legendbgcolor"   : gCtrl.setLegendBGColor    (Color.web(value)); break;
                case "gridxcolor"      : gCtrl.setXGridlinesColor  (Color.web(value)); break;
                case "gridycolor"      : gCtrl.setYGridlinesColor  (Color.web(value)); break;
                case "ticklabelcolor"  : gCtrl.setTickLabelsColor  (Color.web(value)); break;
                case "ticklinecolor"   : gCtrl.setTicklinesColor   (Color.web(value)); break;
                case "symbolfillcolor" : gCtrl.setSymbolFillColor  (Color.web(value)); break;

                case "heatcolor"       : heatColor = value; break;
                case "heatvalue"       : heatValue = value; break;

                // label preference
                case "title"           : gCtrl.setTitle ( value ); break;
                case "xlabel"          : gCtrl.setXLabel( value ); break;
                case "ylabel"          : gCtrl.setYLabel( value ); break;
                case "titlefont"       : gCtrl.setTitleFont    ( value.split(":")[0], value.split(":")[1] ); break;
                case "axesfont"        : gCtrl.setAxesFont     ( value.split(":")[0], value.split(":")[1] ); break;
                case "tickfont"        : gCtrl.setTickLabelFont( value.split(":")[0], value.split(":")[1] ); break;
                case "legendfont"      : gCtrl.setLegendFont   ( value.split(":")[0], value.split(":")[1] ); break;

                // grid preference
                case "xmaxtickcount"   : gCtrl.setXMaxTickCount( Integer.parseInt( value ) ); break;
                case "ymaxtickcount"   : gCtrl.setYMaxTickCount( Integer.parseInt( value ) ); break;
                case "tickwidth"       : gCtrl.setTickWidth ( value ); break;
                case "ticklength"      : gCtrl.setTickLength( value ); break;

                // transform preference
                case "transform"       : gCtrl.setTransform( getParam_transform( value ) );         break;
                case "constant"        : gCtrl.setConstantForLogScale( Float.parseFloat( value ) ); break;
                case "smoothing"       : gCtrl.setSmoothingBin( Integer.parseInt( value ) );        break;
                    
                default: 
                    showError( "Unknown option: " + key );
                    return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean checkParameters() {
        return true;
    }
    
    @Override
    public void run() {
        DataLoader ld = new DataLoader( app, cntl );
        
        ArrayList<File> sampleFiles = new ArrayList<>();
        for( String f : samples ) {
            File file = new File( f );
            sampleFiles.add( file );
            if( !file.exists() || !file.canRead() || !file.isFile() ) {
                showError( "cannot access to " + f );
                return;
            }
        }
        ld.loadSamples(sampleFiles);
        
        // group
        for( String arg : simplegroups ) {
            String[] splitArgs = arg.split(",");
            Options options = Options.parseCmdLine( splitArgs );
            
            String delim = null;
            int    start = 0;
            int    end   = 0;
            for( Entry<String,String> opt: options ) {
                String key   = opt.getKey().toLowerCase();
                String value = opt.getValue();
                try {
                    switch( key ) {
                        case "delim": delim = value; break;
                        case "start": start = Integer.parseInt( value ); break;
                        case "end"  : end   = Integer.parseInt( value ); break;
                        default:
                            //TODO error
                            return;
                    }
                } catch( NumberFormatException e ) {
                    //TODO error
                    showError( "start and end offsets for a simple group should be integers");
                    return;
                }
            }
            
            ArrayList<File> groupFiles = new ArrayList<>();
            File file = new File( splitArgs[0] );
            groupFiles.add( file );
            
            if( !file.exists() || !file.canRead() || !file.isFile() ) {
                showError( "cannot access to " + splitArgs[0] );
                return;
            }
            
            IDConvention rule = null;
            if( delim != null ) rule = ld.prepareIDConvention(delim, start, end);
            
            ld.loadGroups(groupFiles, rule);
        }
        
        // quantity group
        for( String arg : quantitygroups ) {
            String[] splitArgs = arg.split(",");
            Options options = Options.parseCmdLine( splitArgs );
            String path = splitArgs[0];
            
            File file = new File( path );
            if( !file.exists() || !file.canRead() || !file.isFile() ) {
                showError( "cannot access to " + splitArgs[0] );
                return;
            }
            
            String  delim = null;
            int     start = 0;
            int     end   = 0;
            boolean isGCT = false;
            String    quantityType   = null;
            String [] criteriaNames  = null;
            String [] criteriaValuesString = null;
            Float  [] criteriaValues = null;
            String    featColumnString = null;
            int       featColumn;
            String [] sampleColumnsString  = null;
            int    [] sampleColumnsInteger = null;
            Boolean[] columnCategory = null;
            StringHashMultimap map = new StringHashMultimap();
            
            for( Entry<String,String> opt: options ) {
                String key   = opt.getKey().toLowerCase();
                String value = opt.getValue();
                
                if( value == null ) {
                    switch( key ) {
                        case "gct": isGCT = true; break;
                        default:
                            //TODO error
                            showError( "unknown binary option for quantity group: " + key );
                            return;
                    }
                    continue;
                }
                
                switch( key ) {
                    case "delim"         : delim = value;                        break;
                    case "start"         : start = Integer.parseInt( value );    break;
                    case "end"           : end   = Integer.parseInt( value );    break;
                    case "type"          : quantityType = value;                 break;
                    case "criterianames" : criteriaNames        = value.split( ":" ); break;
                    case "criteriavalues": criteriaValuesString = value.split( ":" ); break;
                    case "featcolumn"    : featColumnString     = value;              break;
                    case "samplecolumns" : sampleColumnsString  = value.split( ":" ); break;
                    case "map"           : map.put( value.split(":")[1], value.split(":")[0] ); break;    
                        
                    default:
                        //TODO error
                        showError( "unknown quantity group option" );
                        return;
                }
            }
            
            // option check
            /**/    if( quantityType == null || criteriaNames == null || criteriaValuesString == null ) {
            /**/        //TODO error
            /**/        showError( "quantity type and criteria names/values should be specified" );
            /**/        return;
            /**/    }
            // ENDOF option check
                
            IDConvention rule = null;
            if( delim != null ) {
                rule = ld.prepareIDConvention( delim, start, end );
            }
            
            try{ 
                criteriaValues = new Float[ criteriaValuesString.length ];
                for( int i = 0; i < criteriaValuesString.length; ++i ) {
                    if( criteriaValuesString[i].toLowerCase().equals("max") ) {
                        criteriaValues[i] = Float.MAX_VALUE;
                    } else {
                        criteriaValues[i] = Float.parseFloat( criteriaValuesString[i] );
                    }
                }
            } catch( NumberFormatException e ) {
                //TODO error
                showError( "invalid criteria value" );
                return;
            }
            
            QuantityDialog.Type qtype;
            switch( quantityType.toLowerCase() ) {
                case "microarray": qtype = QuantityDialog.Type.MICROARRAY; break;
                case "rnaseq"    : qtype = QuantityDialog.Type.RNASEQ    ; break;
                case "beta"      : qtype = QuantityDialog.Type.BETA      ; break;
                case "quantile"  : qtype = QuantityDialog.Type.QUANTILE  ; break;
                default:
                    //TODO error
                    showError( "invalid quantity type" );
                    return;
            }
            
            if( featColumnString == null ) {
                showError( "feature column should be specified" );
                return;
            } 
            
            try{
                featColumn = Integer.parseInt( featColumnString );
            } catch( NumberFormatException ex ) {
                showError( "invalid feature column index" );
                return;
            }
            
            if( sampleColumnsString == null ) {
                showError( "sample columns should be specified" );
                return;
            }
            
            int maxColumnindex   = featColumn;
            sampleColumnsInteger = new int[ sampleColumnsString.length ];
            try { 
                for( int i = 0; i < sampleColumnsString.length; ++i ) {
                    sampleColumnsInteger[i] = Integer.parseInt( sampleColumnsString[i] );
                    maxColumnindex = Math.max( maxColumnindex, sampleColumnsInteger[i] );
                }
            } catch( NumberFormatException ex ) {
                //TODO error
                showError( "invalid sample column index" );
                return;
            }
            
            columnCategory = new Boolean[ maxColumnindex+1 ];
            columnCategory[ featColumn ] = Boolean.FALSE;
            for( int i = 0; i < sampleColumnsInteger.length; ++i ) {
                columnCategory[sampleColumnsInteger[i]] = Boolean.TRUE;
            }
            
            ld.loadQuantityGroup(rule, path, criteriaNames, criteriaValues, columnCategory, isGCT, qtype, map.getHashMap() );
        }
        
        // cluster group
        for( String f : clustergroups ) {
            File file = new File( f );
            if( !file.exists() || !file.canRead() || !file.isFile() ) {
                showError( "cannot access to " + f );
                return;
            }
            ld.loadClusteredGroup( f );
        }
        
        for( String series : drawlist ) {
            if( series.endsWith( "*" ) ) { 
                // prefix match
                String prefix = series.substring( 0, series.length() - 1 );
                for( String id : app.samMap.keySet() ) {
                    if( id.startsWith( prefix ) && !app.selectedItems.SelectionContains( id ) ) {
                        app.selectedItems.add( id );
                    }
                }
                for( String id : app.groupMap.keySet() ) {
                    if( id.startsWith( prefix ) && !app.selectedItems.SelectionContains( id ) ) {
                        app.selectedItems.add( id );
                    }
                }
                for( String id : app.featMap.keySet() ) {
                    if( id.startsWith( prefix ) && !app.selectedItems.SelectionContains( id ) ) {
                        app.selectedItems.add( id );
                    }
                }
            } else { 
                //exact match
                if( app.samMap.containsKey( series )
                 || app.groupMap.containsKey( series )
                 || app.featMap.containsKey( series ) ) {

                    if( !app.selectedItems.SelectionContains( series ) ) app.selectedItems.add( series );
                }
            }
        }
        
        try {
            int w = Integer.parseInt( width  );
            int h = Integer.parseInt( height );
            
            boolean ischart = false;
            switch( type.toLowerCase() ) {
                case "chart"  : ischart = true ; break;
                case "heatmap": ischart = false; break;
                default:
                    //TODO error
                    showError( "unknown graph type" );
                    return;
            }
            
            System.out.println( "generating graph" );
            generate( w, h, ischart, output );
            System.out.println( "graph saved" );
        } catch( NumberFormatException e ) {
            //TODO error
            showError( "width and height should be integers" );
            return;
        }
    }

    private GraphControl.CLUSTERING_TYPE getParam_clustering(String value) {
        switch( value.toLowerCase() ) {
            case "no"          : return GraphControl.CLUSTERING_TYPE.NO;
            case "hierarchical": return GraphControl.CLUSTERING_TYPE.HIERARCHICAL;
            case "kmeans"      : return GraphControl.CLUSTERING_TYPE.KMEAN;
                
            default            : return null;
        }
    }

    private GraphControl.HEATMAP_GROUP getParam_grouping(String value) {
        switch( value.toLowerCase() ) {
            case "no"    : return GraphControl.HEATMAP_GROUP.SERIESWISE;
            case "row"   : return GraphControl.HEATMAP_GROUP.FEATUREWISE;
            case "column": return GraphControl.HEATMAP_GROUP.SAMPLEWISE;
                
            default      : return null;
        }
    }

    private GraphControl.GRAPHTYPE getParam_charttype(String value) {
        switch( value.toLowerCase() ) {
            case "line": return GraphControl.GRAPHTYPE.LINE;
            case "area": return GraphControl.GRAPHTYPE.AREA;
                
            default    : return null;
        }
    }

    private Side getParam_legend(String value) {
        switch( value.toLowerCase() ) {
            case "top"   : return Side.TOP;
            case "bottom": return Side.BOTTOM;
            case "left"  : return Side.LEFT;
            case "right" : return Side.RIGHT;
                
            default      : return null;
        }
    }

    private GraphControl.SYMBOL getParam_symbol(String value) {
        switch( value.toLowerCase() ) {
            case "no"     : return GraphControl.SYMBOL.NO;
            case "circle" : return GraphControl.SYMBOL.CIRCLE;
            case "square" : return GraphControl.SYMBOL.SQUARE;
            case "diamond": return GraphControl.SYMBOL.DIAMOND;
                
            default       : return null;
        }
    }

    private GraphControl.TRANSFORM_TYPE getParam_transform(String value) {
        switch( value.toLowerCase() ) {
            case "no"             : return GraphControl.TRANSFORM_TYPE.NO;
            case "log"            : return GraphControl.TRANSFORM_TYPE.LOG;
            case "quantile"       : return GraphControl.TRANSFORM_TYPE.QUANTILE;
            case "std"            :
            case "standardization": return GraphControl.TRANSFORM_TYPE.STANDARDIZATION;

            default: return null;
        }
    }

    private String getParam_linkage(String value) {
        switch( value.toLowerCase() ) {
            case "single"           : return "Single";            
            case "complete"         : return "Complete";          
            case "average"          : return "Average";           
            case "mean"             : return "Mean";              
            case "centroid"         : return "Centroid";          
            case "ward"             : return "Ward";              
            case "adjustedcomplete" : return "Adjusted Complete"; 
            case "neighborjoining"  : return "Neighbor Joining";  
                
            default                 : return null;
        }
    }

    private String getParam_distance(String value) {
        switch( value.toLowerCase() ) {
            case "euclidean": return "Euclidean";
            case "manhattan": return "Manhattan";
            case "chebyshev": return "Chebyshev";
                
            default         : return null;
        }
    }
    
    class SnapshotTask implements Runnable {

        Semaphore waitForInit;
        Node node;
        
        SnapshotTask( Semaphore w, Node n ) {
            waitForInit = w;
            node = n;
        }
        
        @Override
        public void run() {
            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            snapshot = node.snapshot(snapshotParameters, null);
            waitForInit.release();
        }
    }
    
    static class FxPlatformExecutor {
        public static void runOnFxApplication(Runnable task) {
            if (Platform.isFxApplicationThread()) {
                task.run();
            } else {
                Platform.runLater(task);
            }
        }
    }
    
    synchronized void generate( int width, int height, boolean isChart, String outfile ) {
        Semaphore waitForInit = new Semaphore(0);
        
        TagMap cData = cntl.getDrawingData();
        Region chart;
        
        if( isChart ) { // normal chart
            chart = gCtrl.GenerateChart( app, cData );
        } else {        // heatmap
            if( heatColor != null ) {
                String[] colors = heatColor.split( ":" );
                for( int i = 0; i < colors.length; ++i ) {
                    gCtrl.setHeatmapColor( i, Color.web( colors[i] ) );
                }
            }
            
            if( heatValue != null ) {
                String[] vals = heatValue.split( ":" );
                for( int i = 0; i < vals.length; ++i ) {
                    gCtrl.setHeatmapValue( i, vals[i] );
                }
            }
            
            chart = gCtrl.GenerateHeatmap(app, cData );
        }
        
	pane.setPrefSize( width, height );
	pane.getChildren().add( chart );
        pane.layout();
        
	chart.setPrefSize( pane.getPrefWidth(), pane.getPrefHeight() );
        
	pane.layout();
        chart.layout();
        
        FxPlatformExecutor.runOnFxApplication( new SnapshotTask( waitForInit, pane ) );
        waitForInit.acquireUninterruptibly();
        
        SaveControl saveControl = new SaveControl( snapshot );
        int ext_offset = outfile.lastIndexOf('.');
        if( ext_offset == -1 ) {
            saveControl.SaveImage(new File( outfile ));
        } else {
            switch( outfile.substring( ext_offset + 1 ).toLowerCase() ) {
                case "pdf": saveControl.SavePDF(new File( outfile )); break;
                case "eps": saveControl.SaveEPS(new File( outfile )); break;
                case "png":
                default:
                    saveControl.SaveImage(new File( outfile ));
            }
        }
        
    }
}
