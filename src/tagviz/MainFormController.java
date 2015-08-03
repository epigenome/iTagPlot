package tagviz;

import Clustering.WekaFeatureWiseKMeansClustererWrapper;
import Clustering.WekaKMeansClustererWrapper;
import Clustering.WekaSeriesWiseKMeansClustererWrapper;
import Controls.ComputationDialog;
import Controls.FeatureClusteringDialog;
import Controls.GraphControl;
import Controls.IDConventionDialog;
import Controls.QuantityDialog;
import Controls.SaveControl;
import Forms.ComputationFormController;
import Forms.IDConventionFormController;
import Heatmap.Heatmap;
import Objects.Feature;
import Objects.Group;
import Objects.IDConvention;
import Objects.QuantityGroup;
import Objects.Sample;
import Objects.Serializer;
import Objects.SimpleGroup;
import Objects.TableObject;
import Objects.TagList;
import Objects.TagMap;
import Objects.Tool;
import doubleslider.DoubleSlider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.converter.DefaultStringConverter;
import org.apache.commons.lang3.StringUtils;

public class MainFormController implements Initializable {

    // <editor-fold defaultstate="collapsed" desc="FXML References">
    @FXML
    AnchorPane Base;
    @FXML
    AnchorPane GraphPopPanel;
    @FXML
    AnchorPane ColorsPopPanel;
    @FXML
    AnchorPane TextPopPanel;
    @FXML
    AnchorPane XAxisPopPanel;
    @FXML
    AnchorPane YAxisPopPanel;
    @FXML
    AnchorPane ZoomPopPanel;
    @FXML
    AnchorPane ClusteringPopPanel;
    @FXML
    AnchorPane DrawPopPanel;
    @FXML
    AnchorPane WritePopPanel;

    @FXML
    SplitPane BodyPane;
    @FXML
    AnchorPane DataAnchorPane;
    @FXML
    AnchorPane GraphPane;
    @FXML
    StackPane GraphStack;
    @FXML
    HBox ControlPane;
    @FXML
    SplitPane ItemPane;
    @FXML
    CheckMenuItem DisplayFile;
    
    @FXML
    ScrollBar scrollBarX;
    @FXML
    ScrollBar scrollBarY;

    @FXML
    Slider YzoomSlider;
    @FXML
    DoubleSlider YRangeSlider;
    @FXML
    Slider XzoomSlider;

    @FXML
    TableColumn fName;
    @FXML
    TableColumn fValue;
    @FXML
    TableView ftable;
    @FXML
    TableColumn gName;
    @FXML
    TableColumn gValue;
    @FXML
    TableView gtable;
    @FXML
    TableColumn sName;
    @FXML
    TableColumn sValue;
    @FXML
    TableView stable;
    @FXML
    MenuItem dcsmenu;
    @FXML
    MenuItem dcgmenu;
    @FXML
    AnchorPane SearchPane;
    @FXML
    ProgressBar progBar;
    @FXML
    ProgressBar progBarGroupClustering;
    @FXML
    TitledPane SearchBar;
    @FXML
    TextField SearchText;

    @FXML
    ToggleGroup GraphTypeToggleGroup;
    @FXML
    RadioButton LineRadioButton;
    @FXML
    RadioButton AreaRadioButton;

    @FXML
    ToggleGroup LegendToggleGroup;
    @FXML
    RadioButton LTopRadioButton;
    @FXML
    RadioButton LRightRadioButton;
    @FXML
    RadioButton LBottomRadioButton;
    @FXML
    RadioButton LLeftRadioButton;
    @FXML
    RadioButton LNoneRadioButton;

    @FXML
    ToggleGroup SymbolToggleGroup;
    @FXML
    RadioButton CircleRadioButton;
    @FXML
    RadioButton SquareRadioButton;
    @FXML
    RadioButton DiamondRadioButton;
    @FXML
    RadioButton NoSymbolRadioButton;

    @FXML
    ColorPicker backgroundColor;
    @FXML
    ColorPicker chartAreaColor;
    @FXML
    ColorPicker xGridLinesColor;
    @FXML
    ColorPicker yGridLinesColor;
    @FXML
    ColorPicker tickLinesColor;
    @FXML
    ColorPicker titleColor;
    @FXML
    ColorPicker axisColor;
    @FXML
    ColorPicker legLabelsColor;
    @FXML
    ColorPicker legBgColor;
    @FXML
    ColorPicker tickLabelsColor;
    @FXML
    ColorPicker chartBorderColor;
    @FXML
    ColorPicker symbolFillColor;
    //BEGIN Entries for heatmap configuration
    @FXML
    ComboBox    heatmapPreset;
    @FXML
    ColorPicker heatmapColor1;
    @FXML
    ComboBox    heatmapValue1;
    @FXML
    ColorPicker heatmapColor2;
    @FXML
    ComboBox   heatmapValue2;
    @FXML
    ColorPicker heatmapColor3;
    @FXML
    ComboBox   heatmapValue3;
    //END   Entries for heatmap configuration

    @FXML
    TextField TitleField;
    @FXML
    TextField XAxisField;
    @FXML
    TextField YAxisField;
    @FXML
    ComboBox objectCombo;
    @FXML
    ComboBox fontCombo;
    @FXML
    ComboBox sizeCombo;

    @FXML
    TextField XScaleField;
    @FXML
    TextField YScaleField;

    @FXML
    CheckBox xAxisTickLabelCheckBox;
    @FXML
    CheckBox xAxisTickMarkCheckBox;
    @FXML
    TextField xAxisTickCountField;
    @FXML
    ComboBox xAxisTickWidthCombo;
    @FXML
    ComboBox xAxisTickLengthCombo;
    @FXML
    ComboBox xAxisGridlinesCombo;
    
    @FXML
    CheckBox yAxisTickLabelCheckBox;
    @FXML
    CheckBox yAxisTickMarkCheckBox;
    @FXML
    TextField yAxisTickCountField;
    @FXML
    ComboBox yAxisTickWidthCombo;
    @FXML
    ComboBox yAxisTickLengthCombo;
    @FXML
    ComboBox yAxisGridlinesCombo;
    
    @FXML
    ComboBox yAxisTransform;
    @FXML
    TextField yAxisConstantFactorField;
    
    @FXML
    ComboBox clusteringCombo;
    @FXML
    ComboBox groupingCombo;
    //@FXML
    //CheckBox groupingCheckBox;
    
    @FXML
    ComboBox linkTypeCombo;
    @FXML
    ComboBox distanceCombo;
    @FXML
    TextField numClusters;
    @FXML
    TextField rowHeight;
    
    @FXML
    AnchorPane MessagePanel;
    @FXML
    Label MessageHeader;
    @FXML
    Label MessageBody;

    @FXML
    ColorPicker DrawColor;
    @FXML
    ComboBox DrawWeight;

    @FXML
    ComboBox WriteFont;
    @FXML
    ComboBox WriteSize;

    // </editor-fold> 
    
    XYChart chart;
    Heatmap heatmap;
    
    GraphControl gCtrl;
    ArrayList<AnchorPane> PopUps;
    ArrayList<ToggleGroup> TGroups;
    boolean cpPop = false;
    Boolean mouseEnterPopup = false;
    Boolean showingError = false;
    Serializer serializer = new Serializer();
    Tool eTool;
    private TagViz app;
    @FXML
    private Button findButton;
    String searchStr = "";
    @FXML
    private MenuItem casmenu;
    @FXML
    private MenuItem uasmenu;
    @FXML
    private MenuItem icsmenu;
    @FXML
    private MenuItem cagmenu;
    @FXML
    private MenuItem uagmenu;
    @FXML
    private MenuItem icgmenu;
    @FXML
    private MenuItem epngmenu;
    @FXML
    private MenuItem epdfmenu;
    @FXML
    private MenuItem eepsmenu;
    
    @FXML
    private Insets x1;
    @FXML
    private Menu msigMenu;
    @FXML
    private TextField smoothingField;
    
    public Log log = new Log();
    @FXML
    private MenuItem qmMenu;
    @FXML
    private MenuItem qrMenu;
    @FXML
    private MenuItem qbMenu;
    @FXML
    private MenuItem qqMenu;
    @FXML
    private TextField maxSeriesField;
    @FXML
    private TextField lineWeightField;
    @FXML
    private MenuItem etabmenu;
    
    public ProgressBar GetBar() {
        return progBar;
    }

    private void Save() {
        serializer.Serialize(gCtrl);
    }

    private GraphControl Restore() {
        GraphControl gc = serializer.Deserialize();
	if (TagViz.msigDirectory != null) makeMsigMenus(TagViz.msigDirectory);
	return gc;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        gCtrl = Restore();
        PopUps = new ArrayList<>(Arrays.asList(GraphPopPanel, ColorsPopPanel, TextPopPanel, XAxisPopPanel, YAxisPopPanel, ZoomPopPanel, ClusteringPopPanel, DrawPopPanel, WritePopPanel));
        TGroups = new ArrayList<>(Arrays.asList(SymbolToggleGroup, LegendToggleGroup, GraphTypeToggleGroup));

        fontCombo.getItems().addAll(FXCollections.observableArrayList(Font.getFamilies()));
        WriteFont.getItems().addAll(FXCollections.observableArrayList(Font.getFamilies()));
        TitleField.setText(gCtrl.getTitle());
        XAxisField.setText(gCtrl.getXLabel());
        YAxisField.setText(gCtrl.getYLabel());
        
        // SH:[TODO] TickWidth, TickLength for each X- and Y- axes
        xAxisTickWidthCombo.setValue(gCtrl.getTickWidth());
        xAxisTickLengthCombo.setValue(gCtrl.getTickLength());
        yAxisTickWidthCombo.setValue(gCtrl.getTickWidth());
        yAxisTickLengthCombo.setValue(gCtrl.getTickLength());
        
        xAxisTickLabelCheckBox.setSelected(gCtrl.getShowXlabels());
        xAxisTickMarkCheckBox.setSelected(gCtrl.getShowXTicks());
        xAxisTickCountField.setText(String.format("%s", gCtrl.getXMaxTickCount()));
        xAxisGridlinesCombo.setValue(gCtrl.getStrokeStyle(gCtrl.getXGridLineStyle()));
        
        yAxisTickLabelCheckBox.setSelected(gCtrl.getShowYlabels());
        yAxisTickMarkCheckBox.setSelected(gCtrl.getShowYTicks());
        yAxisTickCountField.setText(String.format("%s", gCtrl.getYMaxTickCount()));
        yAxisGridlinesCombo.setValue(gCtrl.getStrokeStyle(gCtrl.getYGridLineStyle()));
        
        switch( gCtrl.getTransform() ){
            case NO:              yAxisTransform.setValue( "No" );              break;
            case LOG:             yAxisTransform.setValue( "Logarithm" );       break;
            case STANDARDIZATION: yAxisTransform.setValue( "Standardization" ); break;
            case QUANTILE:        yAxisTransform.setValue( "Quantile" );        break;
        }
        yAxisConstantFactorField.setText    (Float.toString(gCtrl.getConstantForLogScale()));
        yAxisConstantFactorField.setDisable (gCtrl.getTransform()!=GraphControl.TRANSFORM_TYPE.LOG);
        
	smoothingField.setText(String.format("%s", gCtrl.getSmoothingBin()));
	maxSeriesField.setText(String.format("%s", TagViz.maxSeries));

        InitializeMenuColors();
        BindScales();

        objectCombo.setValue(objectCombo.getValue());
        ChangeFontObject();

        for (ToggleGroup tg : TGroups) {
            for (Toggle t : tg.getToggles()) {
                t.setSelected(false);
            }
        }
        if (gCtrl.getType().equals(GraphControl.GRAPHTYPE.LINE)) {
            LineRadioButton.setSelected(true);
        } else {
            AreaRadioButton.setSelected(true);
        }
        if (gCtrl.getSymbol().equals(GraphControl.SYMBOL.CIRCLE)) {
            CircleRadioButton.setSelected(true);
        } else if (gCtrl.getSymbol().equals(GraphControl.SYMBOL.SQUARE)) {
            SquareRadioButton.setSelected(true);
        } else if (gCtrl.getSymbol().equals(GraphControl.SYMBOL.DIAMOND)) {
            DiamondRadioButton.setSelected(true);
        } else {
            NoSymbolRadioButton.setSelected(true);
        }
        if (gCtrl.getLegend().equals(Side.TOP)) {
            LTopRadioButton.setSelected(true);
        } else if (gCtrl.getLegend().equals(Side.RIGHT)) {
            LRightRadioButton.setSelected(true);
        } else if (gCtrl.getLegend().equals(Side.BOTTOM)) {
            LBottomRadioButton.setSelected(true);
        } else if (gCtrl.getLegend().equals(Side.LEFT)) {
            LLeftRadioButton.setSelected(true);
        } else {
            LNoneRadioButton.setSelected(true);
        }
        
        switch( gCtrl.getClusteringGroup()  ) {
            //case SERIESWISE:  groupingCheckBox.setSelected(false); break;
            //case FEATUREWISE: groupingCheckBox.setSelected(true); break;
            case SERIESWISE : groupingCombo.getSelectionModel().select(0); break;
            case SAMPLEWISE : groupingCombo.getSelectionModel().select(1); break;
            case FEATUREWISE: groupingCombo.getSelectionModel().select(2); break;
            default: ;
        }
        
        switch( gCtrl.getClustering() ) {
            case NO:           clusteringCombo.getSelectionModel().select(0); break;
            case HIERARCHICAL: clusteringCombo.getSelectionModel().select(1); break;
            case KMEAN:        clusteringCombo.getSelectionModel().select(2); break;
            default: ;
        }
        setClusteringOptionsEnabled( gCtrl.getClustering() );
        
        distanceCombo.setValue( (String)gCtrl.getClusteringDistanceFunction() );
        linkTypeCombo.setValue( (String)gCtrl.getClusteringLinkType());
        numClusters.setText    ( Integer.toString( gCtrl.getKMeanNumClusters() ));
        rowHeight.setText     ( Integer.toString( gCtrl.getHeatmapSeriesHeight() ));
        
        HidePopPanels();

        /* listener for feature search field */
        SearchText.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    Search(ke);
                }
            }
        });
        TitleField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ChangeGraphTitle();
                }
            }
        });
        XAxisField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ChangeXAxisLabel();
                }
            }
        });
        YAxisField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ChangeYAxisLabel();
                }
            }
        });
	lineWeightField.textProperty().addListener(new ChangeListener<String>() {
	    @Override
	    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		if (newValue.length() >= 2) {
		    newValue = newValue.substring(newValue.length()-1);
		    lineWeightField.setText(newValue);
		}
		int n = ValidateNumberInput(newValue);
		if (0 < n && n < 10) {
		    if (LineRadioButton.isSelected()) {
			gCtrl.setAllLineWeight(GraphControl.GRAPHTYPE.LINE, n);
		    } else if (AreaRadioButton.isSelected()) {
			gCtrl.setAllLineWeight(GraphControl.GRAPHTYPE.AREA, n);
		    }
		    Update(true);
		} else {
		    lineWeightField.setText("");
		}
	    }
	});
        
        numClusters.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ChangeNumClusters();
                }
            }
        });
        rowHeight.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    ChangeRowHeight();
                }
            }
        });

        initializeTable(stable, sValue, sName);
        initializeTable(gtable, gValue, gName);
        initializeTable(ftable, fValue, fName);
        
        YRangeSlider.setValue1(YRangeSlider.getMin());
        YRangeSlider.setValue2(YRangeSlider.getMax());
    
        associateScrollBarsWithGraph();
    }
    
    public void scroll( double dx, double dy ) {
        scroll( scrollBarX, dx );
        scroll( scrollBarY, dy );
    }
    
    private void scroll( ScrollBar scrollBar, double d ) {
        if( d == 0.0 ) return;
        scrollTo( scrollBar, scrollBar.getValue() + d );
    }
    
    private void scrollTo( ScrollBar scrollBar, double v ) {
        double newValue = v;
        if( newValue < scrollBar.getMin() ) newValue = scrollBar.getMin();
        if( scrollBar.getMax() < newValue ) newValue = scrollBar.getMax();
        scrollBar.setValue( newValue );
    }
    
    private void associateScrollBarsWithGraph() {
        scrollBarX.valueProperty().addListener( new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if( chart != null ) {
                    chart.setLayoutX( (int)-ov.getValue().doubleValue() );
                }
                if( heatmap != null ) {
                    heatmap.setLayoutX( (int)-ov.getValue().doubleValue() );
                }
            }
        });
        
        scrollBarY.valueProperty().addListener( new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if( chart != null ) {
                    chart.setLayoutY( (int)-ov.getValue().doubleValue() );
                }
                if( heatmap != null ) {
                    heatmap.setLayoutY( (int)-ov.getValue().doubleValue() );
                }
            }
        });
    }

    private void initializeTable(final TableView table, TableColumn vCol, TableColumn sCol) {
	sCol.setCellValueFactory(new PropertyValueFactory<TableObject, String>("Name"));
        if( table != ftable ) {
            sCol.setCellFactory(
                    new Callback<TableColumn<Object,String>,TableCell<Object,String> >() {
                        @Override
                        public TableCell<Object,String> call(TableColumn<Object, String> p) {
                            TableCell<Object,String> cell = new TextFieldTableCell<Object, String>( new DefaultStringConverter() ) {

                                private HashMap<String, Object> getHashMap() {
                                    if     ( table == stable ) return app.samMap;
                                    else if( table == gtable ) return app.groupMap;
                                    else if( table == ftable ) return null;          //reserved
                                    return null;
                                }
                                
                                private void removeObject( Object obj ) {
                                    if     ( table == stable ) app.removeSample( ((Sample)obj).getName(), false );
                                    else if( table == gtable ) app.removeGroup ( ((Group )obj).getName(), false );
                                    else if( table == ftable ) return;               //reserved

				    clearFeaturesIf();
                                }
                                
                                private void changeName( Object obj, String newname ) {
                                    if     ( table == stable ) ((Sample )obj).setName( newname );
                                    else if( table == gtable ) ((Group  )obj).setName( newname );
                                    else if( table == ftable ) return;               //reserved
                                }

                                private void addObject( Object obj ) {
                                    try {
                                        if     ( table == stable ) app.addSample ( (Sample) obj, false );
                                        else if( table == gtable ) app.addGroup  ( (Group)  obj, false );
                                        else if( table == ftable ) return;           //reserved
                                    } catch (IOException ex) {
                                        Error("Reading error", "in AddObject() in commitEdit()");
                                    }    
                                }
                                
                                private void updateWithNewSample( String oldkey, String key ) {
                                    // update Groups
                                    for( Entry<String,Object> groupKeyValue : app.groupMap.entrySet() ) {
                                        Group group = (Group)groupKeyValue.getValue();
                                        group.changeSampleName(oldkey, key);
                                    }
                                }
                                
                                private void updateWithNewGroup( String oldkey, String key ) {
                                    // update Samples
                                    for( Entry<String,Object> sampleKeyValue : app.samMap.entrySet() ) {
                                        Sample sample = (Sample)sampleKeyValue.getValue();
                                        TagMap tagMap = sample.getTagMap();
                                        if( tagMap.containsKey( oldkey ) ) {
                                            TagList tagList = tagMap.get( oldkey );
                                            tagMap.remove( oldkey );
                                            tagMap.put( key, tagList );
                                        }
                                        
                                        TagMap naMap = sample.getNaMap();
                                        if( naMap.containsKey( oldkey ) ) {
                                            TagList tagList = naMap.get( oldkey );
                                            naMap.remove( oldkey );
                                            naMap.put( key, tagList );
                                        }
                                    }
                                }
                                
                                private void updateAssociation( String oldkey, String key ) {
                                    if( table == stable ) updateWithNewSample( oldkey, key );
                                    if( table == gtable ) updateWithNewGroup ( oldkey, key );
                                    if( table == ftable ) return;                     // reserved
                                }
                                
                                private void updateSelectedItems( String oldkey, String key ) {
                                    boolean selected = app.selectedItems.SelectionContains(oldkey);
                                    if( !selected ) return;
                                    
                                    if      ( table == stable ) app.selectedItems.changeSampleName(oldkey, key);
                                    else if ( table == gtable ) app.selectedItems.changeGroupName (oldkey, key);
                                    else if ( table == stable ) return;               // reserved
                                }

                                @Override
                                public void commitEdit( String key ) {

                                    if( table == stable && key.contains( ":" ) ) {
                                        Error( "Edit Error", "Sample key must not contain any colon(':').");
                                        return;
                                    }

                                    String oldkey = this.getItem();
                                    
                                    if( oldkey.equals( key ) || key.length() == 0 ) {
                                        this.cancelEdit();
                                        return;
                                    }
                                    
                                    if( app.samMap.containsKey ( key ) ||
                                        app.featMap.containsKey( key ) ||
                                        app.groupMap.containsKey( key ) ) {
                                        Error( "Edit Error", "Duplicate keys are not allowed");
                                        return;
                                    }
                                    
                                    HashMap<String, Object> hashmap = getHashMap();

                                    Object obj = hashmap.get( oldkey );

                                    removeObject     ( obj   );
                                    changeName       ( obj   , key );
                                    updateAssociation( oldkey, key );
                                    addObject        ( obj   );
                                    
                                    updateSelectedItems( oldkey, key );

                                    //hashmap.remove( oldkey );                                
                                    //hashmap.put( key, value );
                                    
                                    super.commitEdit(key);
                                }
                            };
                            return cell;
                        }
                    } );
            sCol.setEditable(true);
        } else {
            sCol.setEditable(false);
        }
	vCol.setCellValueFactory(new PropertyValueFactory<TableObject, Boolean>("Value"));
	vCol.setCellFactory(CheckBoxTableCell.forTableColumn(vCol));
	vCol.setEditable(true);
        vCol.setGraphic(ignoreCheckBox(table, vCol));
	table.setEditable(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private CheckBox ignoreCheckBox(final TableView table, final TableColumn col)
    {
        CheckBox fcheck = new CheckBox("I");
        
        fcheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
               if      (table == stable) app.selectedItems.setIgnoreSample(t1);
	       else if (table == gtable) app.selectedItems.setIgnoreGroup(t1);
	       else if (table == ftable) app.selectedItems.setIgnoreFeature(t1);
            }
        });

        return fcheck;
    }
    
    private ContextMenu tableMenu(final TableView table) {
        final ContextMenu menu = new ContextMenu();
	final boolean isSample = table == stable;
        if (table == stable || table == gtable) {
            final String text = isSample ? "Sample" : "Group";
            final MenuItem openItem = new MenuItem("Open " + text + " ...");
            openItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    UploadObject(isSample);
                }
            });
            final MenuItem dcItem = new MenuItem("Delete Checked " + text);
            dcItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DeleteCheckedObjects(isSample);
                }
            });
            final MenuItem dsItem = new MenuItem("Delete Selected " + text);
            dsItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DeleteSelectedObjects(isSample);
                }
            });
            final MenuItem checkAllItem = new MenuItem("Check All");
            checkAllItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    CheckAllObjects(table, true);
                }
            });
            
            menu.getItems().addAll(openItem, dcItem, dsItem, new SeparatorMenuItem(), checkAllItem);
	    menuBindNotChecked(dcItem, table);
	    menuBindNotSelected(dsItem, table);
	    menuBindAllChecked(checkAllItem, table);
        }
        
	final MenuItem uncheckAllItem = new MenuItem("Uncheck All");
	uncheckAllItem.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
		CheckAllObjects(table, false);
	    }
	});
	menu.getItems().addAll(uncheckAllItem);
	menuBindNotChecked(uncheckAllItem, table);
	
	if (table == stable || table == gtable) {
            final MenuItem invertCheckedItem = new MenuItem("Invert Check");
            invertCheckedItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    InvertCheckedObjects(isSample);
                }
            });
            menu.getItems().addAll(invertCheckedItem);
	    menuBindItemExists(invertCheckedItem, table);
	}
	
        final MenuItem checkSelectedItem = new MenuItem("Check Selection");
        checkSelectedItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckSelectedObjects(table, true);
            }
        });
        final MenuItem uncheckSelectedItem = new MenuItem("Uncheck Selection");
        uncheckSelectedItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckSelectedObjects(table, false);
            }
        });
        menu.getItems().addAll(new SeparatorMenuItem(), checkSelectedItem, uncheckSelectedItem);
	menuBindNotSelected(checkSelectedItem, table);
	menuBindNotSelected(uncheckSelectedItem, table);

	if (table == stable || table == gtable) {
	    final MenuItem selectAllItem = new MenuItem("Select All");
	    selectAllItem.setOnAction(new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
		    SelectAllObjects(table, true);
		}
	    });
	    final MenuItem deselectAllItem = new MenuItem("Deselect All");
	    deselectAllItem.setOnAction(new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
		    SelectAllObjects(table, false);
		}
	    });
            final MenuItem invertSelectedItem = new MenuItem("Invert Selection");
            invertSelectedItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    InvertSelectedObjects(isSample);
                }
            });
            menu.getItems().addAll(new SeparatorMenuItem(), selectAllItem, deselectAllItem, invertSelectedItem);
	    menuBindAllSelected(selectAllItem, table);
	    menuBindNotSelected(deselectAllItem, table);
	    menuBindItemExists(invertSelectedItem, table);
	}
	
        return menu;
    }

/*    private ContextMenu tableMenu(final TableView table) {
        final ContextMenu menu = new ContextMenu();
        final MenuItem checkItem = new MenuItem("Check Selection");
        checkItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckSelectedObjects(table, true);
            }
        });
        final MenuItem uncheckItem = new MenuItem("Uncheck Selection");
        uncheckItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckSelectedObjects(table, false);
            }
        });
        menu.getItems().addAll(checkItem, uncheckItem);
        return menu;
    }*/

    private void menuBindItemExists(MenuItem menu, TableView table) {
	if      (table == stable) menu.disableProperty().bind(app.sampleProperty().isEqualTo(0));
	else if (table == gtable) menu.disableProperty().bind(app.groupProperty().isEqualTo(0));
    }
    
    private void menuBindNotChecked(MenuItem menu, TableView table) {
	if      (table == stable) menu.disableProperty().bind(app.selectedItems.sampleProperty().isEqualTo(0));
	else if (table == gtable) menu.disableProperty().bind(app.selectedItems.groupProperty().isEqualTo(0));
	else                      menu.disableProperty().bind(app.selectedItems.featureProperty().isEqualTo(0));
    }
    
    private void menuBindAllChecked(MenuItem menu, TableView table) {
	if      (table == stable) menu.disableProperty().bind(app.selectedItems.sampleProperty().isEqualTo(app.sampleProperty()));
	else if (table == gtable) menu.disableProperty().bind(app.selectedItems.groupProperty().isEqualTo(app.groupProperty()));
    }
    
    private void menuBindNotSelected(MenuItem menu, TableView table) {
	if      (table == stable) menu.disableProperty().bind(stable.getSelectionModel().selectedItemProperty().isNull());
	else if (table == gtable) menu.disableProperty().bind(gtable.getSelectionModel().selectedItemProperty().isNull());
	else                      menu.disableProperty().bind(ftable.getSelectionModel().selectedItemProperty().isNull());
    }
    
    private void menuBindSampleOpened(MenuItem menu) {
	menuBindItemExists(menu, stable);
    }

    private void menuBindGroupOpened(MenuItem menu) {
	menuBindItemExists(menu, gtable);
    }

    private void menuBindDrawn(MenuItem menu) {
	menu.disableProperty().bind(app.selectedItems.tagProperty().isEqualTo(0));
    }

    private void menuBindAllSelected(final MenuItem menu, final TableView table) {
	table.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	    @Override
	    public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
		menu.setDisable(table.getSelectionModel().getSelectedIndices().size() == table.getItems().size());
	    }
    	});
    }
    
    // <editor-fold defaultstate="collapsed" desc="Menubar methods">
    @FXML
    private void UploadSample(ActionEvent event) {
        UploadObject(true);
    }

    @FXML
    private void UploadGroup(ActionEvent event) {
        UploadObject(false);
    }
    
    public void UploadObject(boolean isSample) {
        final List<File> paths = TagViz.ChooseFiles(TagViz.visDirectory);
	if (paths == null || paths.isEmpty())
            return;

	TagViz.visDirectory = paths.get(0).getParentFile();
	String[] names = uniqueNames(paths, isSample);
	if (!isSample) prepareIDConversion(names, fetchFeatureFromGroup(paths.get(0)));

	ThreadGroup tg = new ThreadGroup("upload");
        int i = 0;
        for (File file : paths)
        {
            Upload(isSample, file, names[i], tg);
            i++;
        }
        
        postUpload(isSample, tg, progBar);
    }

    public String[] uniqueNames(List<File> paths, boolean isSample) {
	String[] strs = new String[paths.size()];
	for (int i = 0; i < strs.length; i++) strs[i] = paths.get(i).getName();
	return uniqueNames(strs, isSample);
    }

    public String[] uniqueNames(String[] paths, boolean isSample)
    {
        HashMap<String, Object> map = isSample ? app.samMap : app.groupMap;
        String[] names = new String[paths.length];
        String[][] temps = new String[paths.length][];
        int i, j;
        int len = 0;
        
        for (i = 0; i < paths.length; i++) {
            temps[i] = paths[i].split("\\.");
            if (temps[i].length > len) len = temps[i].length;
        }
        
        for (i = 1; i < len; i++) {
            HashSet<String> set = new HashSet<>();
            for (j = 0; j < names.length; j++) {
                set.add(StringUtils.join(Arrays.asList(Arrays.copyOfRange(temps[j], 0, i)), "."));
            }
            
            if (set.size() == names.length) {
                int count = 0;
                for (String s : set) {
                    if (map.containsKey(s)) break;
                    count++;
                }
                if (count == names.length) break;
            }
        }
        
        for (j = 0; j < names.length; j++) names[j] = StringUtils.join(Arrays.asList(Arrays.copyOfRange(temps[j], 0, i)), ".");

        return names;
    }

    private void postUpload(final boolean isSample, final ThreadGroup tg, final ProgressBar bar) {
        /* handles the actual reading */
        final Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (tg.activeCount() > 0) {
                    try {
//                        System.out.println(tg.activeCount() + " threads are running"); // MASTER: remove for release version
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFormController.class.getName()).log(Level.FINEST, null, ex);
                        break;
                    }
                }
//                        System.out.println(tg.activeCount() + " threads are running"); // MASTER: remove for release version
                return null;
            }

            /* action to take after read is complete */
            @Override
            protected void done() {
                super.done();
                updateProgress(0, 1);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (isSample) {
                            ReloadSamples();
                            CheckAllSamples(null);
                        } else {
                            ReloadGroups();
                            postIDConversion();
                        }

                        if (!log.isEmpty()) {
                            Error("Warning", log.toString());
                            log.clear();
                        }
                    }
                });
            }
        };

        /* bar must be declared final */
        bar.progressProperty().bind(task.progressProperty());
        new Thread(task).start();
    }

    public void Upload(boolean isSample, File path, String name, ThreadGroup tg) {
        if (!path.exists()) {
            Error("Read Error", "File does not exist");
        } else if (!path.isDirectory()) {
            ReadFromStream(path.getAbsolutePath(), name, isSample, tg);
        } else {
            Error("Read Error", "File cannot be a directory");
        }
    }

    private void ReadFromStream(final String path, final String name, final boolean isSample, ThreadGroup tg) {
        /* handles the actual reading */
        final Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (isSample) loadSample(path, name);
                else          loadGroup(path, name);
                return null;
            }

            /* action to take after read is complete */
            @Override
            protected void done() {
                super.done();
                updateProgress(0, 1);
            }
        };

        new Thread(tg, task).start();
    }
    
    public void loadSample(final String path, final String name)
    {
	loadSample(path, name, app);
    }
    public void loadSample(final String path, final String name, TagViz app)
    {
        String line;
        boolean flag = true;
        Long fpos = null;
        Sample sam;

        try {
            sam = new Sample(name, path);
	    TagList sum = null;
	    TagList na = null;
	    
            while ((line = sam.getRaf().readLine()) != null) {
                String[] sa = line.split("\t");
                if (flag) { 
                    String[] sa2 = Arrays.copyOfRange(sa, TagViz.MAX_INFO_COLUMN, sa.length);
                    if (app.header == null) { app.header = sa2; }
                    else if (!Arrays.equals(app.header, sa2)) { throw new IOException("Header is different"); }
                    
		    sam.setNumCols(sa.length-TagViz.MAX_INFO_COLUMN);
		    sum = new TagList(sam.getNumCols(), 0);
		    na = new TagList(sam.getNumCols(), 0);
//		    for (int i = app.MAX_INFO_COLUMN; i < sa.length; i++) sum.add(new Float(0));
                    flag = false;
                }
                else {
                    sam.put(sa[0], fpos);
                    app.addFeature(new Feature(sa[0], sa[1], Integer.parseInt(sa[2]), Integer.parseInt(sa[3]), sa[4]));
		    for (int i = 0; i < sum.size(); i++) {
			if (!sa[i+TagViz.MAX_INFO_COLUMN].equalsIgnoreCase(TagViz.NA))
			    sum.set(i, sum.get(i) + Float.parseFloat(sa[i+TagViz.MAX_INFO_COLUMN]));
			else
			    na.set(i, na.get(i)+1);
		    }
               }

                fpos = sam.getRaf().getFilePointer();
            }
	    
//	    for (int i = 0; i < sum.size(); i++) sum.set(i, sum.get(i) / sam.size()); different number of features in samples
	    sam.addTags(Sample.ALL, sum, na);
	    app.addSample(sam);
        } catch (FileNotFoundException ex) {
            Error("File not found", path);
        } catch (IOException ex) {
            Error("Reading error", path);
        }        
    }

    public void loadGroup(final String path, final String name)
    {
	loadGroup(path, name, app);
    }
    public void loadGroup(final String path, final String name, TagViz app)
    {
        String line;
        SimpleGroup grp;

	try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            grp = new SimpleGroup(name);
	    while ((line = br.readLine()) != null) {
		String[] sa = line.split("\t");
		for (String f : sa) app.addFeatureToGroup(grp, null, f);
	    }

	    app.addGroup(grp);
	} catch (FileNotFoundException ex) {
            Error("File not found", path);
        } catch (IOException ex) {
            Error("Reading error", path);
        }        
    }
    
    public String fetchFeatureFromGroup(final File path)
    {
        String line;

	try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	    while ((line = br.readLine()) != null) {
		String[] sa = line.split("\t");
		return sa[0];
	    }
        } catch (Exception ex) {
        }
	
	return null;
    }

    public void loadGmt(final String path)
    {
        String line;
        SimpleGroup grp;
	IDConvention rule = prepareIDConversion(new String[] {""}, fetchFeatureFromGmt(path));

	try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	    while ((line = br.readLine()) != null) {
		String[] sa = line.split("\t");
		grp = new SimpleGroup(sa[0]);
		if (rule != null) app.idMap.put(sa[0], rule); // when calling prepareIDConvention, the group name is known
		for (int i = 2; i < sa.length; i++) app.addFeatureToGroup(grp, null, sa[i]);
		app.addGroup(grp);
	    }
	} catch (FileNotFoundException ex) {
            Error("File not found", path);
        } catch (IOException ex) {
            Error("Reading error", path);
        }
	
	ReloadGroups();
	if (!log.isEmpty()) {
	    Error("Warning", log.toString());
	    log.clear();
	}
    }

    public String fetchFeatureFromGmt(final String path)
    {
        String line;

	try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	    while ((line = br.readLine()) != null) {
		String[] sa = line.split("\t");
		return sa[2];
	    }
        } catch (Exception ex) {
        }
	
	return null;
    }

    private void DeleteSelectedSamples(ActionEvent event) {
        DeleteSelectedObjects(true);
    }

    private void DeleteSelectedGroups(ActionEvent event) {
        DeleteSelectedObjects(false);
    }

    public void DeleteSelectedObjects(boolean isSample) {
        TableView table = isSample ? stable : gtable;
        for (Object obj : table.getSelectionModel().getSelectedItems())
        {
            TableObject to = (TableObject) obj;
            to.setValue(false);
            app.removeObject(isSample, to.getName());
        }
        table.getItems().removeAll(new ArrayList(table.getSelectionModel().getSelectedItems()));
        table.getSelectionModel().clearSelection();

	clearFeaturesIf();
    }
    
     @FXML
    private void DeleteCheckedSamples(ActionEvent event) {
        DeleteCheckedObjects(true);
    }

    @FXML
    private void DeleteCheckedGroups(ActionEvent event) {
        DeleteCheckedObjects(false);
    }

    public void DeleteCheckedObjects(boolean isSample) {
        TableView table = isSample ? stable : gtable;
	ArrayList dels = new ArrayList();
        for (Object item : table.getItems()) {
            TableObject to = (TableObject) item;
            if (!to.getValue()) continue;
            to.setValue(false);
            app.removeObject(isSample, to.getName());
	    dels.add(item);
        }
        table.getItems().removeAll(dels);
	clearFeaturesIf();
    }

    void clearFeaturesIf() {
        if( stable.getItems().size() == 0 && gtable.getItems().size() == 0 ) {
            ftable.getItems().clear();
            app.featMap.clear();
	    app.header = null;
	    removeGraph();
        }
	app.updateTitle();
    }
    private void SelectAllSamples(ActionEvent event) {
        SelectAllObjects(stable, true);
    }

    private void SelectAllGroups(ActionEvent event) {
        SelectAllObjects(gtable, true);
    }

    private void DeselectAllSamples(ActionEvent event) {
        SelectAllObjects(stable, false);
    }
    
    private void DeselectAllGroups(ActionEvent event) {
        SelectAllObjects(gtable, false);
    }

    public void SelectAllObjects(TableView table, boolean selected) {
	if (selected) table.getSelectionModel().selectAll();
	else          table.getSelectionModel().clearSelection();
    }

    private void InvertSelectedSamples(ActionEvent event) {
        InvertSelectedObjects(true);
    }

    private void InvertSelectedGroups(ActionEvent event) {
        InvertSelectedObjects(false);
    }

    public void InvertSelectedObjects(boolean isSample) {
        TableView table = isSample ? stable : gtable;
        for (int i = 0; i < table.getItems().size(); i++) {
	    if (table.getSelectionModel().isSelected(i)) table.getSelectionModel().clearSelection(i);
	    else                                         table.getSelectionModel().select(i);
        }
    }

    @FXML
    private void CheckAllSamples(ActionEvent event) {
        CheckAllObjects(stable, true);
    }

    @FXML
    private void CheckAllGroups(ActionEvent event) {
        CheckAllObjects(gtable, true);
    }

    @FXML
    private void UncheckAllSamples(ActionEvent event) {
        CheckAllObjects(stable, false);
    }
    
    @FXML
    private void UncheckAllGroups(ActionEvent event) {
        CheckAllObjects(gtable, false);
    }

    public void CheckAllObjects(TableView table, boolean selected) {
        for (Object item : table.getItems())
            ((TableObject) item).setValue(selected);
    }

    @FXML
    private void InvertCheckedSamples(ActionEvent event) {
        InvertCheckedObjects(true);
    }

    @FXML
    private void InvertCheckedGroups(ActionEvent event) {
        InvertCheckedObjects(false);
    }

    public void InvertCheckedObjects(boolean isSample) {
        TableView table = isSample ? stable : gtable;
        for (Object item : table.getItems()) {
            TableObject to = (TableObject) item;
            to.setValue(!to.getValue());
        }
    }

    public void CheckSelectedObjects(boolean isSample, boolean checked) {
        CheckSelectedObjects(isSample ? stable : gtable, checked);
    }
    public void CheckSelectedObjects(TableView table, boolean checked) {
        for (Object item : table.getSelectionModel().getSelectedItems())
            ((TableObject) item).setValue(checked);
    }

    @FXML
    public void ExportPNG() {
	Export(SaveControl.FORMAT.PNG);
    }

    @FXML
    public void ExportPDF() {
	Export(SaveControl.FORMAT.PDF);
    }
    
    @FXML
    public void ExportEPS() {
	Export(SaveControl.FORMAT.EPS);
    }
    
    public void Export (SaveControl.FORMAT format) {
        WritableImage image = GraphPane.snapshot(new SnapshotParameters(), null);
        if( heatmap != null ) {
            WritableImage oldimage = image;
            image = new WritableImage(oldimage.getPixelReader(), 0, 0, (int)oldimage.getWidth(), heatmap.estimateHeight() + 50 );
        }
        SaveControl save = new SaveControl(image);
        File file = save.Save(format);
	if (file != null && DisplayFile.isSelected())
	    app.getHostServices().showDocument(file.getAbsoluteFile().toURI().toString());
    }

    @FXML
    private void ExportTable(ActionEvent event) {
        SaveControl save = new SaveControl(null);
        File file = save.Save(SaveControl.FORMAT.TABLE);
	if (file != null) gCtrl.saveChartToTable(file);
    }
    
    @FXML
    public void CollapsePanels() {
        ControlPane.setPrefHeight(0);
        ControlPane.setMinHeight(0);
        ControlPane.setMaxHeight(0);
        BodyPane.setDividerPosition(0, 0);
        DataAnchorPane.setPrefWidth(0);
        DataAnchorPane.setMaxWidth(0);
    }

    @FXML
    public void ShowPanels() {
        DataAnchorPane.setPrefWidth(206);
        DataAnchorPane.setMaxWidth(206);
        BodyPane.setDividerPosition(0, 0.209);
        ControlPane.setPrefHeight(77);
        ControlPane.setMinHeight(77);
        ControlPane.setMaxHeight(77);
    }

    @FXML
    public void ClearSelection() {
        app.selectedItems.clear();
        ReloadAll();
    }

    @FXML
    public void ClearGraph() {
        GraphPane.getChildren().clear();
    }

    @FXML
    public void ExitApplication() {
        Save();
        TagViz.ExitApplication();
    }

    //</editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Activate tools">
    @FXML
    public void ActivateMove() {
        LoadTool(0);
    }

    @FXML
    public void ActivateDraw() {
        LoadTool(1);
    }

    @FXML
    public void ClearDraws() {
        ObservableList<Node> list = GraphPane.getChildren();
        for (int i = list.size(); i > 0; i -= 1) {
            if (list.get(i - 1) instanceof Path) {
                list.remove(list.get(i - 1));
            }
        }
    }

    @FXML
    public void ActivateWrite() {
        LoadTool(2);
    }

    @FXML
    public void ClearWrites() {
        ObservableList<Node> list = GraphPane.getChildren();
        for (int i = list.size(); i > 0; i -= 1) {
            if (list.get(i - 1) instanceof AnchorPane) {
                list.remove(list.get(i - 1));
            }
        }
    }

    public void setMouseEventHandlerForTools( EventHandler mouse_press, EventHandler mouse_release, EventHandler mouse_drag, EventHandler mouse_enter, EventHandler mouse_move ) {
        GraphPane.setOnMousePressed(mouse_press);
        GraphPane.setOnMouseReleased(mouse_release);
        GraphPane.setOnMouseDragged(mouse_drag);
        GraphPane.setOnMouseEntered(mouse_enter);
        GraphPane.setOnMouseMoved(mouse_move);
    }
    
    public void addChildEntity( final Node a ) {
        GraphPane.getChildren().add(GraphPane.getChildren().size(), a);
        
        if( chart != null ) {
            a.translateXProperty().bind( chart.layoutXProperty().subtract( chart.getLayoutX() ));
            a.translateYProperty().bind( chart.layoutYProperty().subtract( chart.getLayoutY() ));
        } else if( heatmap != null ) {
            a.translateXProperty().bind( heatmap.layoutXProperty().subtract( heatmap.getLayoutX() ));
            a.translateYProperty().bind( heatmap.layoutYProperty().subtract( heatmap.getLayoutY() ));
            
        }
    }
    
    public void appendToLastPath( final PathElement a ) {
        Node n = GraphPane.getChildren().get(GraphPane.getChildren().size() - 1);
        if( n instanceof Path ) ((Path)n).getElements().add(a);
    }
    
    public void removeChildEntity( Node a ) {
        GraphPane.getChildren().remove(a);
    }
    
    public void requestFocusToPlot() {
        if( chart   != null ) chart  .requestFocus();
        if( heatmap != null ) heatmap.requestFocus();
    }
    
    public void setOnplotCursor( Cursor cursor ) {
        if( chart   != null ) chart  .setCursor(cursor);
        if( heatmap != null ) heatmap.setCursor(cursor);
    }
    
    private void LoadTool(int i) {
        HidePopPanels();
        if (chart != null || heatmap != null) {
            if (i == 0) {
                eTool = new Objects.MoveTool(this);
                //eTool = new Objects.MoveTool(chart, GraphPane);
            } else if (i == 1) {
                Color c = DrawColor.getValue();
                int w = Integer.parseInt(DrawWeight.getValue().toString());
                eTool = new Objects.DrawTool(this, c, w);
                //eTool = new Objects.DrawTool(chart, GraphPane, Base, c, w);
            } else {
                String font = WriteFont.getValue().toString();
                Double size = Double.parseDouble(WriteSize.getValue().toString());
                eTool = new Objects.WriteTool(this, font, size);
                //eTool = new Objects.WriteTool(chart, GraphPane, Base, font, size);
            }
            eTool.start();
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Data Tables methods">
    public void Search(KeyEvent e) {
        SearchFeature(SearchText.getText());
    }

    @FXML
    private void OnSearchFeature(ActionEvent event) {
        SearchFeature(SearchText.getText());
    }

    public void SearchFeature(String input) {
        int i;
        if (!input.equalsIgnoreCase(searchStr)) {
            i = 0;
            if (ftable.getSelectionModel().getSelectedIndex() != -1) ftable.getSelectionModel().clearSelection();
        } else {
            i = ftable.getSelectionModel().getSelectedIndex()+1;
        }
        
        for ( ; i < ftable.getItems().size(); i++) {
            if (((TableObject) ftable.getItems().get(i)).getName().contains(input)) {
                ftable.getSelectionModel().select(i);
                ftable.scrollTo(i);
                break;
            }
        }
        
        if (i == ftable.getItems().size()) {
            if (input.equalsIgnoreCase(searchStr)) {
                Error("Reached the end", "No more feature for " + input);
                input = "";
            } else {
                Error("No feature for " + input, "Check feature name");
            }
        }
        
        searchStr = input;
    }
    
    public String lookupFeature(String input) {
        for (int i = 0; i < ftable.getItems().size(); i++) {
            if (((TableObject) ftable.getItems().get(i)).getName().contains(input)) {
		return ((TableObject) ftable.getItems().get(i)).getName();
	    }
	}
	
	return null;
    }
    
    @FXML
    public void ToggleSearch() {
        double h = 30;
        if (!SearchBar.isExpanded()) {
//            ReloadFeats();
            SearchText.setText(null);
            h = 0;
        }
        SearchPane.setMaxHeight(h);
        SearchPane.setMinHeight(h);
        SearchPane.setPrefHeight(h);
    }

    @FXML
    public void ReloadAll() {
        ReloadGroups();
        ReloadSamples();
    }

    public void ReloadGroups() {
        ReloadObject(app.groupMap, gtable, gValue, gName);
        app.updateTitle();
    }

    public void ReloadSamples() {
        ReloadObject(app.samMap, stable, sValue, sName);
        ReloadFeats();
        app.updateTitle();
    }

    private void ReloadFeats() {
        ReloadObject(app.featMap, ftable, fValue, fName);
    }

    private void ReloadObject(HashMap<String, Object> map, TableView table, TableColumn vCol, TableColumn sCol) {

        if (map != null && map.size() > 0) {
            table.getItems().clear();

            ObservableList data = FXCollections.observableArrayList();
	    for (String name : map.keySet())
		data.add(new TableObject(app.selectedItems.SelectionContains(name), name, app));
            table.setItems(data);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="POPUP Logic">
    private void ShowPopup(AnchorPane p) {
        mouseEnterPopup = false;
	if (cpPop || p.isVisible()) return;
        HidePopPanels();

        FadeTransition ft = new FadeTransition(Duration.millis(150), p);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();

        p.setVisible(true);
    }

    @FXML
    public void HidePopPanels() {
        mouseEnterPopup = false;
	if (cpPop) return;
        for (AnchorPane a : PopUps) {
            a.setVisible(false);
        }
    }

    @FXML
    public void EnterPopUp() {
        mouseEnterPopup = true;
    }

    @FXML
    public void ExitPopUp() {
        mouseEnterPopup = false;
    }

    @FXML
    private void OnHidingChildPopup(Event event) {
	cpPop = false;
	if (!mouseEnterPopup) HidePopPanels();
    }

    @FXML
    private void OnShowingChildPopup(Event event) {
	cpPop = true;
    }

    @FXML
    public void ShowGraphOpts() {
        ShowPopup(GraphPopPanel);
    }

    @FXML
    public void ShowColorOpts() {
        ShowPopup(ColorsPopPanel);
    }

    @FXML
    public void ShowEditLabels() {
        ShowPopup(TextPopPanel);
    }

    @FXML
    public void ShowXAxisPanel() {
        ShowPopup(XAxisPopPanel);
    }

    @FXML
    public void ShowYAxisPanel() {
        ShowPopup(YAxisPopPanel);
    }

    @FXML
    public void ShowZoomPane() {
        ShowPopup(ZoomPopPanel);
    }
    
    @FXML
    public void ShowClusteringPane() {
        ShowPopup(ClusteringPopPanel);
    }

    @FXML
    public void ShowDrawPane() {
        ShowPopup(DrawPopPanel);
    }

    @FXML
    public void ShowWritePane() {
        ShowPopup(WritePopPanel);
    }

    public void Error(String title, String body) {
        MessageHeader.setText(title);
        MessageBody.setText(body);
        MessagePanel.toFront();
        MessagePanel.setVisible(true);
        if (!showingError) {
            showingError = true;

            FadeTransition ft = new FadeTransition(Duration.millis(300), MessagePanel);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);
            ft.play();
            ft.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // show for a while
                    FadeTransition ft2 = new FadeTransition(Duration.millis(3000), MessagePanel);
                    ft2.setFromValue(1.0);
                    ft2.setToValue(1.0);
                    ft2.setCycleCount(1);
                    ft2.setAutoReverse(false);
                    ft2.play();
                    ft2.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            FadeTransition ft3 = new FadeTransition(Duration.millis(2000), MessagePanel);
                            ft3.setFromValue(1.0);
                            ft3.setToValue(0.0);
                            ft3.setCycleCount(1);
                            ft3.setAutoReverse(true);
                            ft3.play();
                            ft3.setOnFinished(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    MessagePanel.setVisible(false);
                                    showingError = false;
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @FXML
    public final void HideError() {
        MessagePanel.setVisible(false);
        showingError = false;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="POPUP - Graph">
    @FXML
    public void ChangeType() {
        if (LineRadioButton.isSelected()) {
            gCtrl.setType(GraphControl.GRAPHTYPE.LINE);
        } else if (AreaRadioButton.isSelected()) {
            gCtrl.setType(GraphControl.GRAPHTYPE.AREA);
        }
        Update(true);
    }

    @FXML
    public void ChangeLegend() {
        if (LTopRadioButton.isSelected()) {
            gCtrl.setLegend(Side.TOP);
        } else if (LRightRadioButton.isSelected()) {
            gCtrl.setLegend(Side.RIGHT);
        } else if (LBottomRadioButton.isSelected()) {
            gCtrl.setLegend(Side.BOTTOM);
        } else if (LLeftRadioButton.isSelected()) {
            gCtrl.setLegend(Side.LEFT);
        } else {
            gCtrl.setLegend(null);
        }
        Update(true);
    }

    @FXML
    public void ChangeSymbol() {
        if (CircleRadioButton.isSelected()) {
            gCtrl.setSymbol(GraphControl.SYMBOL.CIRCLE);
        } else if (SquareRadioButton.isSelected()) {
            gCtrl.setSymbol(GraphControl.SYMBOL.SQUARE);
        } else if (DiamondRadioButton.isSelected()) {
            gCtrl.setSymbol(GraphControl.SYMBOL.DIAMOND);
        } else {
            gCtrl.setSymbol(GraphControl.SYMBOL.NO);
        }
        Update(true);
    }

    @FXML
    private void ChangeSmoothingBin(KeyEvent event) {
        int n = ValidateNumberInput(smoothingField.getText());
	gCtrl.setSmoothingBin(n);
        Update(heatmap!=null, false);
    }

    @FXML
    private void ChangeMaxSeries(KeyEvent event) {
        int n = ValidateNumberInput(maxSeriesField.getText());
	if (10 <= n && n <= 1000) TagViz.maxSeries = n;
        Update(heatmap!=null, false);
    }
    
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="POPUP - Colors">
    private void InitializeMenuColors() {
        setColor(backgroundColor, gCtrl.getBackgroundColor());
        setColor(chartAreaColor, gCtrl.getGraphAreaColor());
        setColor(xGridLinesColor, gCtrl.getXGridlinesColor());
        setColor(yGridLinesColor, gCtrl.getYGridlinesColor());
        setColor(tickLinesColor, gCtrl.getTicklinesColor());
        setColor(titleColor, gCtrl.getTitleColor());
        setColor(axisColor, gCtrl.getAxisColor());
        setColor(legLabelsColor, gCtrl.getLegendLabelsColor());
        setColor(legBgColor, gCtrl.getLegendBGColor());
        setColor(tickLabelsColor, gCtrl.getTickLabelsColor());
        setColor(chartBorderColor, gCtrl.getGraphBorderColor());
        setColor(symbolFillColor, gCtrl.getSymbolFillColor());
        setColor(DrawColor, Color.ORANGE);
        
        // BEGIN Entries for heatmap configuration
        //Debug.println( "HIGHCOLOR", gCtrl.getSymbolFillColor().toString() );
        setColor(heatmapColor1, gCtrl.getHeatmapColor(0));
        setColor(heatmapColor2, gCtrl.getHeatmapColor(1));
        setColor(heatmapColor3, gCtrl.getHeatmapColor(2));
        
        heatmapValue1.setValue ( gCtrl.getHeatmapValue(0) );
        heatmapValue2.setValue ( gCtrl.getHeatmapValue(1) );
        heatmapValue3.setValue ( gCtrl.getHeatmapValue(2) );
        // END Entries for heatmap configuration
    }

    @FXML
    public void ChangeBackgroundColor() {
        gCtrl.setBackgroundColor(backgroundColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeChartAreaColor() {
        gCtrl.setGraphAreaColor(chartAreaColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeXGridLinesColor() {
        gCtrl.setXGridlinesColor(xGridLinesColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeYGridLinesColor() {
        gCtrl.setYGridlinesColor(yGridLinesColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeTickLinesColor() {
        gCtrl.setTicklinesColor(tickLinesColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeTitleColor() {
        gCtrl.setTitleColor(titleColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeAxisColor() {
        gCtrl.setAxisColor(axisColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeLegendLabelsColor() {
        gCtrl.setLegendLabelsColor(legLabelsColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeLegendBgColor() {
        gCtrl.setLegendBGColor(legBgColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeTickLabelsColor() {
        gCtrl.setTickLabelsColor(tickLabelsColor.getValue());
        Update(chart != null);
    }

    @FXML
    public void ChangeBorderColor() {
        gCtrl.setGraphBorderColor(chartBorderColor.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeSymbolFillColor() {
        gCtrl.setSymbolFillColor(symbolFillColor.getValue());
        Update(heatmap!=null);
    }
    
    // BEGIN Entries for heatmap configuration
    
    @FXML
    public void ChangeHeatmapPreset() {
        String selectedPreset = (String)heatmapPreset.getValue();
        String[] colors = selectedPreset.split("-");
        
        if( colors.length <= 1 ) return;
        
        Heatmap tmp_heatmap = heatmap;
        heatmap = null;
        
        heatmapColor1.setValue(Color.web(colors[0]));
        heatmapValue1.setValue("MAX");
        heatmapColor1.fireEvent(new ActionEvent(null, heatmapColor1));
        
        if( colors.length == 3 ) {
            heatmapColor2.setValue(Color.web(colors[1]));
            heatmapValue2.setValue("MEAN");
            
            heatmapColor3.setValue(Color.web(colors[2]));
        } else {
            heatmapColor2.setValue(Color.BLACK);
            heatmapValue2.setValue("DISABLED");
            
            
            heatmapColor3.setValue(Color.web(colors[1]));
        }
        
        heatmapValue3.setValue("MIN");
        
        heatmapColor2.fireEvent(new ActionEvent(null, heatmapColor2));
        heatmapColor3.fireEvent(new ActionEvent(null, heatmapColor3));
        
        heatmap = tmp_heatmap;
        Update( heatmap != null );
        
        heatmapPreset.setValue( "=== Select ===" );
    }
    
    @FXML
    public void ChangeHeatmapColor1() {
        gCtrl.setHeatmapColor(0,heatmapColor1.getValue());
        Update(heatmap!=null);
    }
    
    @FXML
    public void ChangeHeatmapValue1() {
        gCtrl.setHeatmapValue(0,(String)heatmapValue1.getValue());
    }
    
    @FXML
    public void ChangeHeatmapColor2() {
        gCtrl.setHeatmapColor(1,heatmapColor2.getValue());
        Update(heatmap!=null);
    }
    
    @FXML
    public void ChangeHeatmapValue2() {
        gCtrl.setHeatmapValue(1,(String)heatmapValue2.getValue());
    }
    
    @FXML
    public void ChangeHeatmapColor3() {
        gCtrl.setHeatmapColor(2,heatmapColor3.getValue());
        Update(heatmap!=null);
    }
    
    @FXML
    public void ChangeHeatmapValue3() {
        gCtrl.setHeatmapValue(2,(String)heatmapValue3.getValue());
    }
       

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="POPUP - Fonts">
    @FXML
    public void ChangeFontObject() {
        int selected = objectCombo.getItems().indexOf(objectCombo.getValue());

        switch (selected) {
            case 0: {
                fontCombo.setValue(gCtrl.getTitleFontFamily());
                sizeCombo.setValue(gCtrl.getTitleFontSize());
                break;
            }
            case 1: {
                fontCombo.setValue(gCtrl.getAxesFontFamily());
                sizeCombo.setValue(gCtrl.getAxesFontSize());
                break;
            }
            case 2: {
                fontCombo.setValue(gCtrl.getTickLabelFontFamily());
                sizeCombo.setValue(gCtrl.getTickLabelFontSize());
                break;
            }
            default:
                fontCombo.setValue(gCtrl.getLegendFontFamily());
                sizeCombo.setValue(gCtrl.getLegendFontSize());
                break;
        }

    }

    @FXML
    public void ChangeFont() {
        int selected = objectCombo.getItems().indexOf(objectCombo.getValue());
        String family = fontCombo.getValue().toString();
        String size = sizeCombo.getValue().toString();
        switch (selected) {
            case 0: {
                gCtrl.setTitleFont(family, size);
                break;
            }
            case 1: {
                gCtrl.setAxesFont(family, size);
                break;
            }
            case 2: {
                gCtrl.setTickLabelFont(family, size);
                break;
            }
            default:
                gCtrl.setLegendFont(family, size);
                break;
        }
        Update(true);
    }

    public void ChangeGraphTitle() {
        gCtrl.setTitle(TitleField.getText());
        Update(heatmap != null);
    }

    public void ChangeXAxisLabel() {
        gCtrl.setXLabel(XAxisField.getText());
        Update(heatmap != null);
    }

    public void ChangeYAxisLabel() {
        gCtrl.setYLabel(YAxisField.getText());
        Update(heatmap != null);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="POPUP - Grid">
    
    @FXML
    public void ChangeYTransform() {
        
        GraphControl.TRANSFORM_TYPE transform = GraphControl.TRANSFORM_TYPE.NO;
        
        switch( (String)yAxisTransform.getValue() ) {
            case "No":             transform = GraphControl.TRANSFORM_TYPE.NO;              break;
            case "Logarithm":      transform = GraphControl.TRANSFORM_TYPE.LOG;             break;
            case "Standardization":transform = GraphControl.TRANSFORM_TYPE.STANDARDIZATION; break;
            case "Quantile":       transform = GraphControl.TRANSFORM_TYPE.QUANTILE;        break;
        }
        gCtrl.setTransform(transform);
        yAxisConstantFactorField.setDisable( transform != GraphControl.TRANSFORM_TYPE.LOG );
            
        Update(heatmap!=null, false);
    }
    
    @FXML
    public void ChangeConstantFactor() {
        float constantValue = 0.0f;
        try {
            constantValue = Float.parseFloat(yAxisConstantFactorField.getText());
        } catch ( NumberFormatException e ) {
            return;
        }
        if(constantValue <= 0.0) {
            //Error("Parameter Error","Only a positive constant must be inputed.");
            return;
        }
            
        gCtrl.setConstantForLogScale(constantValue);
        Update(heatmap!=null, false);
    }
   
    @FXML
    public void ChangeXTickLabelVisibility() {
        gCtrl.setShowXlabels(xAxisTickLabelCheckBox.isSelected());
        Update(heatmap!=null);
    }
    
    @FXML
    public void ChangeYTickLabelVisibility() {
        gCtrl.setShowYlabels(yAxisTickLabelCheckBox.isSelected());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeXTickMarksVisibility() {
        gCtrl.setShowXTicks(xAxisTickMarkCheckBox.isSelected());
        Update(heatmap!=null);
    }
    
    @FXML
    public void ChangeYTickMarksVisibility() {
        gCtrl.setShowYTicks(yAxisTickMarkCheckBox.isSelected());
        Update(heatmap!=null);
    }

    private int ValidateNumberInput(String s) {
        int result = -1;
        s = s.trim().replaceAll("\\D", s);
        if (s != null) {
            try {
                result = Integer.parseInt(s);
                if (result > 999) {
                    result = 999;
                }
            } catch (NumberFormatException e) {
                // not an integer; 
            }
        }
        return result;
    }

    @FXML
    public void ChangeXMaxTickCount() {
        int i = ValidateNumberInput(xAxisTickCountField.getText());
        if (i >= 0) gCtrl.setXMaxTickCount(i);
        Update(heatmap!=null, false);
    }

    @FXML
    public void ChangeYMaxTickCount() {
        int i = ValidateNumberInput(yAxisTickCountField.getText());
        if (i >= 0) gCtrl.setYMaxTickCount(i);
        Update(heatmap!=null, false);
    }

    @FXML
    public void ChangeXGridLines() {
        String s = xAxisGridlinesCombo.getValue().toString().toLowerCase();
        GraphControl.STROKE stroke = GraphControl.STROKE.NONE;
        if (s.contains("solid")) {
            stroke = GraphControl.STROKE.SOLID;
        }
        gCtrl.setXGridLineStyle(stroke);
        Update(true);
    }
    
    @FXML
    public void ChangeYGridLines() {
        String s = yAxisGridlinesCombo.getValue().toString().toLowerCase();
        GraphControl.STROKE stroke = GraphControl.STROKE.NONE;
        if (s.contains("solid")) {
            stroke = GraphControl.STROKE.SOLID;
        }
        gCtrl.setYGridLineStyle(stroke);
        Update(true);
    }

    // SH:[TODO] TickWidth and TickLength for each of X- and Y- Axes
    @FXML
    public void ChangeXTickWidth() {
        gCtrl.setTickWidth(xAxisTickWidthCombo.getValue().toString());
        yAxisTickWidthCombo.setValue(xAxisTickWidthCombo.getValue());
        Update(heatmap!=null);
    }
    
    @FXML
    public void ChangeYTickWidth() {
        gCtrl.setTickWidth(yAxisTickWidthCombo.getValue().toString());
        xAxisTickWidthCombo.setValue(yAxisTickWidthCombo.getValue());
        Update(heatmap!=null);
    }

    @FXML
    public void ChangeXTickLength() {
        gCtrl.setTickLength(xAxisTickLengthCombo.getValue().toString());
        yAxisTickLengthCombo.setValue(xAxisTickLengthCombo.getValue());
        Update(heatmap!=null);
    }
    
    @FXML
    public void ChangeYTickLength() {
        gCtrl.setTickLength(yAxisTickLengthCombo.getValue().toString());
        xAxisTickLengthCombo.setValue(yAxisTickLengthCombo.getValue());
        Update(heatmap!=null);
    }

    // </editor-fold>    
    // <editor-fold defaultstate="collapsed" desc="POPUP - Scale">
    @FXML
    public void ResetScale() {
        YzoomSlider.setValue(10);
        YRangeSlider.setValue1(YRangeSlider.getMin());
        YRangeSlider.setValue2(YRangeSlider.getMax());
        XzoomSlider.setValue(10);
        
        // TODO: CHECK
        scrollTo( scrollBarX, 0 );
        scrollTo( scrollBarY, 0 );
        
        /*
        if (chart != null) {
            chart.setLayoutX(0);
            chart.setLayoutY(0);
        }*/
    }

    private void BindScales() {
        XScaleField.textProperty().bind(XzoomSlider.valueProperty().divide(10d).asString());
        YScaleField.textProperty().bind(YzoomSlider.valueProperty().divide(10d).asString());
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="POPUP - Cluster">
    @FXML
    public void ChangeClustering() {
        GraphControl.CLUSTERING_TYPE type = GraphControl.CLUSTERING_TYPE.NO;
        switch( clusteringCombo.getSelectionModel().getSelectedIndex() ) {
            case 0: 
                type = GraphControl.CLUSTERING_TYPE.NO;
                break;
            case 1: 
                type = GraphControl.CLUSTERING_TYPE.HIERARCHICAL;
                break;
            case 2: 
                type = GraphControl.CLUSTERING_TYPE.KMEAN;
                break;
            default: ;
        }
        gCtrl.setClustering(type);
        setClusteringOptionsEnabled( type );
        Update(heatmap != null);
    }
    
    public void setClusteringOptionsEnabled( GraphControl.CLUSTERING_TYPE type ) {
        switch( type ) {
            case NO:
                linkTypeCombo.setDisable(true);
                distanceCombo.setDisable(true);
                numClusters.setDisable(true);
                
                break;
            case HIERARCHICAL:
                linkTypeCombo.setDisable(false);
                distanceCombo.setDisable(false);
                numClusters.setDisable(true);
                
                break;
            case KMEAN:
                linkTypeCombo.setDisable(true);
                distanceCombo.setDisable(false);
                numClusters.setDisable(false);
                
                break;
            default:
        }
    }
    
    @FXML
    public void ChangeGroupingType() {
        switch( groupingCombo.getSelectionModel().getSelectedIndex() ) {
            case 0: gCtrl.setClusteringGroup(GraphControl.HEATMAP_GROUP.SERIESWISE ); break;
            case 1: gCtrl.setClusteringGroup(GraphControl.HEATMAP_GROUP.SAMPLEWISE ); break;
            case 2: gCtrl.setClusteringGroup(GraphControl.HEATMAP_GROUP.FEATUREWISE); break;
                
            default:
                ;
        }
        /*
        if( groupingCheckBox.isSelected() ) {
            gCtrl.setClusteringGroup(GraphControl.HEATMAP_GROUP.FEATUREWISE);
        } else {
            gCtrl.setClusteringGroup(GraphControl.HEATMAP_GROUP.SERIESWISE);
        }*/
        Update(heatmap != null);
    }
    
    @FXML
    public void ChangeLinkType() {
        gCtrl.setClusteringLinkType( (String)linkTypeCombo.getValue() );
        Update(heatmap != null);
    }
    
    @FXML
    public void ChangeDistanceFunction() {
        gCtrl.setClusteringDistanceFunction( (String)distanceCombo.getValue() );
        Update(heatmap != null);
    }
    
    public void ChangeNumClusters() {
        gCtrl.setKMeanNumClusters( ValidateNumberInput(numClusters.getText()));
        Update(heatmap != null);
    }
    
    public void ChangeRowHeight() {
        gCtrl.setHeatmapSeriesHeight( ValidateNumberInput(rowHeight.getText()));
        Update(heatmap != null);
    }
    
    // </editor-fold>
    
    /* ugly necessary fix for resetting color in javafx 2.2 */
    private void setColor(ColorPicker cpicker, Color color) {
        cpicker.setValue(color);
        cpicker.fireEvent(new ActionEvent(null, cpicker));
    }

    /* specify what to do after changing setting*/
    private void Update(Boolean redraw ) {
        Update( redraw, true );
    }
    private void Update(Boolean redraw, Boolean updated) {
        Save();
        if (redraw && app != null && app.selectedItems != null && !app.selectedItems.isEmpty()) {
            if( chart != null ) DrawChart();
            else if( heatmap != null ) {DrawHeatmap();}
        }
        
        if( !updated && chart != null ) {
            // TODO: Please confirm the message.
            Error( "Redraw Request", "Please click the draw button again to complete this update." ) ;
        }
    }
    
    public TagMap getDrawingData() {
        if (app.selectedItems.isEmpty()) {
            String msg = app.samMap.isEmpty() ? "Load" : "Select";
            Error("Data required", msg + " data before\nvisualizing a graph");
        } else if (app.selectedItems.isIgnoreSample() && app.selectedItems.isIgnoreGroup() && app.selectedItems.isIgnoreFeature()) {
	    Error("Interface update", " Uncheck at least one Ignore checkbox");
	} else {
	    TagMap cData = app.selectedItems.getSelection();

	    if (cData.isEmpty()) {
		Error("No data", " Selected items don't have data");
	    } else if (cData.size() > TagViz.maxSeries) {
		Error("Too many data", " Check less than " + TagViz.maxSeries + " series");
	    } else {
		return cData;
	    }
	}
	
        return null;
    }
    
    @FXML
    public void DrawChart() {
        TagMap cData = getDrawingData();
        
        if( cData != null ) {
            removeGraph();
            SubDrawChart(cData);
        }
    }
    
    public void removeGraph() {
        if (!GraphPane.getChildren().isEmpty()) {
	    //GraphPane.getChildren().remove(0);
            GraphPane.getChildren().clear();
	}
                
        if( chart != null ) {
            NumberAxis yAxis;
            yAxis = (NumberAxis) chart.getYAxis();
            yAxis.upperBoundProperty().unbind();
            yAxis.lowerBoundProperty().unbind();
            yAxis.tickUnitProperty()  .unbind();
            chart = null;
        }
        
        if( heatmap != null ) {
            heatmap = null;
        }
    }
    
    public void SubDrawChart(TagMap cData) {
        chart = gCtrl.GenerateChart(app, cData);
        
	chart.prefHeightProperty().bind(YzoomSlider.valueProperty().multiply(GraphPane.heightProperty().divide(10d)));
	chart.prefWidthProperty() .bind(XzoomSlider.valueProperty().multiply(GraphPane.widthProperty().divide(10d)));
        
	chart.getXAxis().setOnMouseClicked(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent mouseEvent) {
		if (!TextPopPanel.isVisible()) {
		    ShowEditLabels();
		}
		XAxisField.requestFocus();
	    }
	});
	chart.getYAxis().setOnMouseClicked(new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent mouseEvent) {
		if (!TextPopPanel.isVisible()) {
		    ShowEditLabels();
		}
		YAxisField.requestFocus();
	    }
	});
	if (eTool != null) {
	    eTool.start();
	} else {
	    ActivateMove();
	}

	GraphPane.getChildren().add(0, chart);
        
        associateGraphWithScrollBars( chart );
        
        GraphPane.layout();
        
        NumberAxis axis = (NumberAxis) chart.getYAxis();
        
        final double axisRange = axis.getUpperBound() - axis.getLowerBound();
        final double axisLowerbound = axis.getLowerBound();
        final double axisTickCount  = axisRange / axis.getTickUnit();
        
        axis.setAutoRanging(false);
        axis.upperBoundProperty().bind( YRangeSlider.value1Property()        .divide(100d).negate().add(1d).multiply( axisRange ).add( axisLowerbound ));
        axis.lowerBoundProperty().bind( YRangeSlider.value2Property().add(1d).divide(100d).negate().add(1d).multiply( axisRange ).add( axisLowerbound ));
        // a trick, which adds +0.001, is used to keep having "axisTickCount" ticks.
        axis.tickUnitProperty()  .bind( axis.upperBoundProperty().subtract( axis.lowerBoundProperty() ).divide( axisTickCount + 0.001 ) );
        
        setScrollBarRange( scrollBarX, GraphPane.getWidth() , chart.getPrefWidth () );
        setScrollBarRange( scrollBarY, GraphPane.getHeight(), chart.getPrefHeight() );
    }
    
    private void setScrollBarRange( ScrollBar scrollBar, double screenSize, double objectSize ) {
        double range = Math.max( 0, objectSize - screenSize );
        scrollBar.setValue( Math.min( scrollBar.getValue(), range ) );
        scrollBar.setMax( range );
        scrollBar.setBlockIncrement( range/100 );
        scrollBar.setVisibleAmount( range * (screenSize/objectSize) );
    }
    
    private void associateGraphWithScrollBars( final Region graph ) {
        graph.prefWidthProperty().addListener( new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                setScrollBarRange( scrollBarX, GraphPane.getWidth(), graph.getPrefWidth() );
            }
        });
        
        graph.prefHeightProperty().addListener( new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                setScrollBarRange( scrollBarY, GraphPane.getHeight(), graph.getPrefHeight() );
            }
        });
        
        GraphPane.setOnScroll( new EventHandler<ScrollEvent>() {            
            @Override
            public void handle(ScrollEvent t) {
                scroll( scrollBarX, -t.getDeltaX() );
                scroll( scrollBarY, -t.getDeltaY() );
            }
        });
    }
    
    @FXML
    public void DrawHeatmap() {  
        TagMap cData = getDrawingData();
        
        if( cData != null ) {
            removeGraph();
            
            heatmap = gCtrl.GenerateHeatmap(app, cData);

            int h = heatmap.estimateHeight();
            heatmap.prefHeightProperty().bind(YzoomSlider.valueProperty().divide(10d).multiply(h).add(50));
            heatmap.prefWidthProperty() .bind(XzoomSlider.valueProperty().divide(10d).multiply(GraphPane.widthProperty()));
            
            GraphPane.getChildren().add(heatmap);
            associateGraphWithScrollBars( heatmap );
        
            heatmap.maxRelativeOffsetProperty.bind( YRangeSlider.value1Property()        .divide(100d).negate().add(1d) );
            heatmap.minRelativeOffsetProperty.bind( YRangeSlider.value2Property().add(1d).divide(100d).negate().add(1d) );
            
            GraphPane.layout();
            
            setScrollBarRange( scrollBarX, GraphPane.getWidth() , heatmap.getPrefWidth () );
            setScrollBarRange( scrollBarY, GraphPane.getHeight(), heatmap.getPrefHeight() );
        }
    }

    public TagViz getApp() {
	return app;
    }

    public void setApp(TagViz app) {
	this.app = app;
    }
    
    public void postInitialization(TagViz app) {
        setApp( app );
        
        stable.setContextMenu(tableMenu(stable));
        gtable.setContextMenu(tableMenu(gtable));
        ftable.setContextMenu(tableMenu(ftable));

	menuBindNotChecked(dcsmenu, stable);
        menuBindAllChecked(casmenu, stable);
        menuBindNotChecked(uasmenu, stable);
        menuBindItemExists(icsmenu, stable);
        
	menuBindNotChecked(dcgmenu, gtable);
        menuBindAllChecked(cagmenu, gtable);
        menuBindNotChecked(uagmenu, gtable);
        menuBindItemExists(icgmenu, gtable);

	menuBindDrawn(epngmenu);
	menuBindDrawn(epdfmenu);
        menuBindDrawn(eepsmenu);
    }

    @FXML
    private void browseMsig(ActionEvent event) {

	try {
	    app.getHostServices().showDocument(TagViz.msigUrl);
	} catch (Exception ex) {
	    Error("Connection error", TagViz.msigUrl);
	}
    }

    @FXML
    private void SetMsigDir(ActionEvent event) {
	File dir = TagViz.ChooseDirectory(TagViz.msigDirectory);
	if (dir == null) return;
	TagViz.msigDirectory = dir;
	makeMsigMenus(dir);
    }
    
    public void makeMsigMenus(File dir) {
	if (msigMenu.getItems().size() > 3) msigMenu.getItems().remove(3, msigMenu.getItems().size());
	for (File file : dir.listFiles(new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		return name.endsWith("gmt");
	    }
	})) {
            final MenuItem item = new MenuItem(file.getName());
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
		    MenuItem item = (MenuItem) event.getSource();
		    File path = new File(TagViz.msigDirectory, item.getText());
		    try {
			loadGmt(path.getCanonicalPath());
		    } catch (IOException ex) {
			Error("Reading error", path.toString());
		    }
                }
            });
	    msigMenu.getItems().add(item);
	}
    }
    
    private PrintWriter openClusteredFeaturesSaveFile( final String savefile, final String prefix, int numSamples, int num_clusters ) {
    // if numSamples == 0 : samplesInARow is set.
    // otherwise, samplesInARow is not set, it indicates there are clustering results for each sample.
        if( savefile == null ) return null;
        try{ 
            PrintWriter writer = new PrintWriter( savefile );
            writer.println( prefix );
            writer.println( numSamples );
            writer.println( num_clusters );
            return writer;
        }catch( IOException ex ) {
            return null;
        }
    }
    
    private void saveClusteringResult( final PrintWriter writer, String sampleName, ArrayList<String>[] result, int num_clusters ) {
        if( writer == null ) return;
        writer.println( sampleName );
        for( int i = 0; i < num_clusters; ++i ) {
            int num_features = result[i].size();
            writer.println( num_features );
            for( String feature : result[i] ) {
                writer.println( feature );
            }
        }
    }
    
    private void loadGroupsFromClusteringResult( int num_clusters, HashMap<Integer, Group> grps, BufferedReader in, String sampleName ) throws IOException {
        for( int i = 0; i < num_clusters; ++i ) {
            int num_features = Integer.parseInt( in.readLine() );
            for( int j = 0; j < num_features; ++j ) {
                String feat = in.readLine();
                app.addFeatureToGroup( grps.get( (i+1) ), sampleName, feat );
            }
        }
    }
    
    public boolean loadClusteringResult( final String filename ) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader( filename ));

        String  prefix   = in.readLine();
        int numSamples   = Integer.parseInt( in.readLine() );
        int num_clusters = Integer.parseInt( in.readLine() );

        prefix = getValidClusterPrefix(prefix, num_clusters);

        if( numSamples == 0 ) {
            // samples In a row
            // simple groups will be uploaded
            HashMap<Integer,Group> grps = new HashMap<>();
            for( int i = 0; i < num_clusters; ++i ) {
                grps.put( i+1, new SimpleGroup( prefix + (i+1) ));
            }
            in.readLine(); // read dummy samplename
            loadGroupsFromClusteringResult( num_clusters, grps, in, null );

            for( Group qg : grps.values() ) {
                    app.addGroup(qg);
            }
        } else {
            // quantity groups will be uploaded
            // to support sample-wise clustered features
            HashMap<Integer,Group> grps = new HashMap<>();
            for( int i = 0; i < num_clusters; ++i ) {
                grps.put( i+1, new QuantityGroup( prefix + (i+1) ));
            }

            for( int i = 0; i < numSamples; ++i ) {
                String sampleName = in.readLine();
                loadGroupsFromClusteringResult( num_clusters, grps, in, sampleName );
            }

            for( Group qg : grps.values() ) {
                    app.addGroup(qg);
            }
        }

        in.close();

        return true;
    }
    
    private void closeClusteredFeaturesSaveFile( final PrintWriter writer ) {
        if( writer == null ) return;
        writer.close();
    }
    
    private boolean checkClusteredFeatureName( String prefix, int n ) {
        for( int i = 1; i <= n; ++i ) {
            if( app.groupMap.containsKey( prefix + i ) ) return false;
        }
        return true;
    }
    
    private String getValidClusterPrefix( String prefix, int n ) {
        String pf = prefix;
        int j = 1;
        while( !checkClusteredFeatureName( pf, n ) ) {
            pf = prefix + (++j) + "_" ;
        }
        return pf;
    }
    
    @SuppressWarnings("empty-statement")
    public void UploadClusteredFeature( int numKMeansClusters, String distanceMetric, String prefix, boolean sampleInARow, String savefile ) {
        
        prefix = getValidClusterPrefix(prefix, numKMeansClusters);
        if( sampleInARow ) {
            WekaKMeansClustererWrapper cl = new WekaFeatureWiseKMeansClustererWrapper(numKMeansClusters, distanceMetric);
        
            HashMap<String,List> tls = new HashMap<>();
            try{
                for( Object v : app.samMap.values() ) {
                    Sample sam = (Sample) v;
                    String samKey = sam.getName();
                    
                    for( String feat : app.featMap.keySet() ) {
                        TagList featureTagList;
                        if( sam.containsKey(feat)) {
                            featureTagList = sam.getTagsFor( feat );
                        } else {
                            featureTagList = TagList.zero( app.header.length );
                        }
                        tls.put( samKey + ":" + feat, featureTagList );
                    }
                }
            }catch( IOException ex ) {
                return;
            }
            
            HashMap<String, SimpleGroup> grps = new HashMap<>( numKMeansClusters );
            for( int i = 1; i <= numKMeansClusters; ++i ) {
                String s = prefix + i;
                grps.put( s, new SimpleGroup(s) );
            }
            
            ArrayList<String>[] result = cl.classify( tls, true );
            
            if( result == null ) return;
            PrintWriter writer = openClusteredFeaturesSaveFile(savefile, prefix, 0, numKMeansClusters );
            saveClusteringResult( writer, "ALL", result, numKMeansClusters );
            closeClusteredFeaturesSaveFile( writer );
            
            for( int i = 0; i < result.length; ++i ) {
                for( String feat : result[i] ) {
                    app.addFeatureToGroup( grps.get( prefix + (i+1)),null, feat );
                }
            }
            
            try {
                for( SimpleGroup qg : grps.values() ) {
                        app.addGroup(qg);
                }
            } catch (IOException ex) {
               return;
            }
        } else {
            WekaKMeansClustererWrapper cl = new WekaSeriesWiseKMeansClustererWrapper(numKMeansClusters, distanceMetric);
        
            // Cluster feature for each sample
            HashMap<String, QuantityGroup> grps = new HashMap<>( numKMeansClusters );
            for( int i = 1; i <= numKMeansClusters; ++i ) {
                String s = prefix + i;
                grps.put( s, new QuantityGroup(s) );
            }
            
            
            PrintWriter writer = openClusteredFeaturesSaveFile( savefile, prefix, app.samMap.values().size(), numKMeansClusters );
            for( Object v: app.samMap.values() ){
                HashMap<String,List> tls = new HashMap<>();
                Sample sam = (Sample) v;
                
                for( String feat : app.featMap.keySet() ) {
                    try{
                        TagList tl = sam.getTagsFor(feat);
                        if( tl != null ) {
                            tls.put( feat, tl );
                        }
                    } catch ( IOException | NoSuchElementException ex ) {
                        ;
                    }
                }
                
                ArrayList<String>[] result = cl.classify(tls, true);
                
                if( result == null ) {
                    break;
                }
                saveClusteringResult( writer, sam.getName(), result, numKMeansClusters );
                
                for( int i = 0; i < result.length; ++i ) {
                    for( String feat : result[i] ) {
                        app.addFeatureToGroup(grps.get( prefix + (i+1) ), sam.getName(), feat);
                    }
                }
            }
            closeClusteredFeaturesSaveFile( writer );
            
            try {
                for( QuantityGroup qg : grps.values() ) {
                        app.addGroup(qg);
                }
            } catch (IOException ex) {
               
            }
        }
    }
    
    void RunThreadUploadClusteredFeature( final int numKMeansClusters, final String distanceMetric, final String prefix, final boolean sampleInARow, final String savefile ) {
        ThreadGroup tg = new ThreadGroup("upload");
        final Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                UploadClusteredFeature( numKMeansClusters, distanceMetric, prefix, sampleInARow, savefile );
                
                return null;
            }

            /* action to take after read is complete */
            @Override
            protected void done() {
                super.done();
                
                updateProgress(0, 1);
            }
            
        };

        task.setOnFailed( new EventHandler() {
            @Override
            public void handle(Event t) {
                WorkerStateEvent evt = (WorkerStateEvent)t;
                if( evt == null || evt.getSource() == null || evt.getSource().getException() == null || evt.getSource().getException().getMessage() == null ) {
                    Error( "Error", "Feature Clustering has been Failed: Unexpected exception" );
                } else {
                    Error( "Error", "Feature Clustering has been Failed: " + evt.getSource().getException().getMessage() );
                }
            }
            
        });
        
        Thread thread = new Thread(tg, task);
        thread.start();
        
        postUpload(false, tg, progBarGroupClustering);
    }
    
    @FXML void OpenClusteredFeature(ActionEvent event) {
        
        FeatureClusteringDialog dlg = new FeatureClusteringDialog(app.stage);
        
        if( !dlg.isOK() ) return;
        
        int     numKMeansClusters = dlg.getNumClusters();
        String  distanceMetric    = dlg.getDistanceMetric();
        String  prefix            = dlg.getPrefix();
        boolean sampleInARow      = dlg.getSampleInARow();
        String  savefile          = dlg.getSaveFile();
        
        RunThreadUploadClusteredFeature( numKMeansClusters, distanceMetric, prefix, sampleInARow, savefile );
    }
    
    @FXML void LoadClusteredFeature(ActionEvent event) {
        final File path = TagViz.ChooseFile(TagViz.visDirectory);
        if( path != null ) {
            ThreadGroup tg = new ThreadGroup("upload");
            final Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    loadClusteringResult( path.getAbsolutePath() ) ;
                    return null;
                }
                
                @Override
                protected void done() {
                        super.done();
                        updateProgress(0, 1);
                }
            };

            task.setOnFailed( new EventHandler() {

                @Override
                public void handle(Event t) {
                    Error( "Error", "Loading Clusters has been Failed: Invalid File" );
                }
            
            });
            
            new Thread(tg, task).start();
            postUpload(false, tg, progBar);
        }
    }
    
    @FXML
    private void OpenQuantity(ActionEvent event) {
	if      (event.getSource() == qmMenu) quantityDialog(QuantityDialog.Type.MICROARRAY);
	else if (event.getSource() == qrMenu) quantityDialog(QuantityDialog.Type.RNASEQ);
	else if (event.getSource() == qbMenu) quantityDialog(QuantityDialog.Type.BETA);
	else if (event.getSource() == qqMenu) quantityDialog(QuantityDialog.Type.QUANTILE);
    }
    
    void quantityDialog(QuantityDialog.Type type) {
        final File path = TagViz.ChooseFile(TagViz.visDirectory);
	if (path != null) {
	    TagViz.visDirectory = path.getParentFile();
	    QuantityDialog dlg = new QuantityDialog(app.stage, type, path, app.samMap.keySet());
	    if (dlg.getController().isOk()) {
                UploadQuantity(path, dlg, type, dlg.getMap());
            }
	}
    }
    
    public void UploadQuantity(final File path, final QuantityDialog dlg, final QuantityDialog.Type type, final HashMap map) {
        final String[] names = uniqueNames(dlg.getController().getCriteriaNames(), false);
	prepareIDConversion(names, dlg.getController().getFeatureName());

        ThreadGroup tg = new ThreadGroup("upload");
        final Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                switch( type ) {
                    case MICROARRAY:
                    case RNASEQ:
                    case BETA:
                        loadQuantityByValue(path.getCanonicalPath(), names, dlg.getController().getCriteriaValues(), dlg.getController().getColumnCategory(), dlg.getController().isGCT(), map);
                        return null;
                    case QUANTILE:
                        loadQuantityByRelativeRank(path.getCanonicalPath(), names, dlg.getController().getCriteriaValues(), dlg.getController().getColumnCategory(), dlg.getController().isGCT(), map);
                        return null;
                    default:
                        Error( "Type Error", "Unknown quantity type" );
                        return null;
                }
            }

            /* action to take after read is complete */
            @Override
            protected void done() {
                super.done();
                updateProgress(0, 1);
            }
        };

        new Thread(tg, task).start();
	
        postUpload(false, tg, progBar);
    }
    
    public void loadQuantityByValue(final String path, final String[] names, final Float[] values, final Boolean[] categories, boolean gct, final HashMap map)
    {
	int k;
        String line;
	String[] headers = null;
        HashMap<String, QuantityGroup> grps = new HashMap<>(names.length);
	for (String s : names) grps.put(s, new QuantityGroup(s));
	
	int index = -1;
	for (int i = 0; i < categories.length; i++) {
	    if ( categories[i] != null && !categories[i]) {
		index = i;
		break;
	    }
	}

	try (BufferedReader br = new BufferedReader(new FileReader(path))) {
	    if (gct) {
		br.readLine(); // version
		br.readLine(); // dimension
	    }
	    
	    while ((line = br.readLine()) != null) {
		String[] sa = line.split("\t");
		if (headers == null) {
		    headers = sa;
		} else {
		    for (int i = 0; i < sa.length; i++) {
                        
			if ( i >= categories.length || categories[i] == null || !categories[i]) continue; // if not a sample column

			try {
			    Float f = Float.parseFloat(sa[i]);
			    for (k = 0; k < values.length; k++) {
				if (f < values[k]) break;
			    }
			    // names[k] = group, headers[i] = sample, sa[index] = feature, f = quantity
                            //app.addFeatureToGroup(grps.get(names[k]), headers[i], sa[index]);
                            ArrayList<String> list = (ArrayList<String>)map.get( headers[i] );
                            if( list != null ) {
                                for( String sampleName : list ) {
                                    app.addFeatureToGroup(grps.get(names[k]), sampleName, sa[index]);
                                }
                            }
			} catch (NumberFormatException ex) {
			}
		    }
		}
	    }

	    for (QuantityGroup qg : grps.values()) app.addGroup(qg);
        } catch (FileNotFoundException ex) {
            Error("File not found", path);
        } catch (IOException ex) {
            Error("Reading error", path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadQuantityByRelativeRank(final String path, final String[] names, final Float[] values, final Boolean[] categories, boolean gct, final HashMap map)
    {
	int k;
        String line;
	String[] headers;
        HashMap<String, QuantityGroup> grps = new HashMap<>(names.length);
	for (String s : names) grps.put(s, new QuantityGroup(s));
	
	int index = -1;
	for (int i = 0; i < categories.length; i++) {
	    if (!categories[i]) {
		index = i;
		break;
	    }
	}
        
        BufferedReader br;
	try {
            //check header
            br = new BufferedReader(new FileReader(path));
	    if (gct) {
		br.readLine(); // version
		br.readLine(); // dimension
	    }
            
            if( (line = br.readLine()) == null ) throw new IOException();
            
            headers = line.split("\t");
            int nHeader = headers.length;
	    
            br.close();
            
            // read data
            for( int j = 0; j < nHeader; ++j ) {
                if( categories[j] == null || !categories[j] ) continue;
                
                br = new BufferedReader( new FileReader(path) );
                
                // skip headers
                if( gct ) {
                    br.readLine();
                    br.readLine();
                }
                br.readLine();
                
                // read quantities for the current sample
                List<Map.Entry<String, Float>> featureQuantities = new ArrayList<>();

                while( ( line = br.readLine() ) != null ) {
                    String[] sa = line.split("\t");
                    featureQuantities.add( new AbstractMap.SimpleEntry<String,Float>(sa[index], Float.parseFloat(sa[j])) {} );
                }
                
                // sort by quantity
                Collections.sort(featureQuantities, new Comparator<Map.Entry<String, Float>>() {
                    @Override
                    public int compare(Map.Entry<String, Float> left, Map.Entry<String, Float> right) {
                        return left.getValue().compareTo(right.getValue());
                    }
                });
                
                // ranking and assign group
                k = 0;
                int rank = 0;
                Iterator<Map.Entry<String,Float>> i = featureQuantities.iterator();
                while( i.hasNext() ) {
                    rank++;
                    float relativeRank = rank / (float) featureQuantities.size();
                    while( values[k] < relativeRank ) ++k;
                    Entry<String, Float> entry = i.next();
                    
                    // names[k] = group, headers[i] = sample, sa[index] = feature, f = quantity
                    //app.addFeatureToGroup(grps.get(names[k]), headers[j], entry.getKey());
                    ArrayList<String> list = (ArrayList<String>)map.get( headers[j] );
                    if( list != null ) {
                        for( String sampleName : list ) {
                            app.addFeatureToGroup(grps.get(names[k]), sampleName, entry.getKey());
                        }
                    }
                }
                
                br.close();
            }
            
	    for (QuantityGroup qg : grps.values()) app.addGroup(qg);
            
        } catch (FileNotFoundException ex) {
            Error("File not found", path);
        } catch (IOException ex) {
            Error("Reading error", path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private IDConvention prepareIDConversion(String[] names, String grpFeat) {
	String samFeat;
	if (grpFeat == null) samFeat =  null;
	else if ((samFeat = lookupFeature(grpFeat)) == null) samFeat = ((TableObject) ftable.getItems().get(0)).getName();
	IDConventionDialog dlg = new IDConventionDialog(app.stage, samFeat, grpFeat);
	IDConventionFormController cont = dlg.getController();
	if (cont.isOk()) {
	    IDConvention rule = IDConvention.create(cont.getDelimeter(), cont.getIndex(true), cont.getIndex(false));
	    rule.convert(app.featMap.keySet());
	    for (String grp : names) app.idMap.put(grp, rule);
	    return rule;
	}
	
	return null;
    }
    
    private void postIDConversion() {
	app.idMap.clear();
    }

    @FXML
    private void RunComputation(ActionEvent event) {
	ComputationDialog dlg = new ComputationDialog(app.stage);
	ComputationFormController cont = dlg.getController();
//	if (cont.isOk()) {
//	}
    }

    public class Log {
	
	private StringBuffer sb = new StringBuffer();

	public boolean isEmpty() {
	    return sb.length() == 0;
	}
	
	@Override
	public String toString() {
	    return sb.toString();
	}

	public void append(String msg) {
	    sb.append(msg);
	}
	
	public void clear() {
	    sb.delete(0, sb.length());
	}
    }
}
