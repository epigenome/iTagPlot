package Controls;

import Clustering.WekaFeatureWiseHierarchicalClustererWrapper;
import Clustering.WekaFeatureWiseKMeansClustererWrapper;
import Clustering.WekaSampleWiseHierarchicalClustererWrapper;
import Clustering.WekaSampleWiseKMeansClustererWrapper;
import Clustering.WekaSeriesWiseHierarchicalClustererWrapper;
import Clustering.WekaSeriesWiseKMeansClustererWrapper;
import Heatmap.FeaturewiseHeatmap;
import Heatmap.Heatmap;
import Heatmap.SamplewiseHeatmap;
import Heatmap.SerieswiseHeatmap;
import Objects.ColorPickerMenuItem;
import Objects.ComboBoxMenuItem;
import Objects.TagList;
import Objects.TagListSmoothingIterator;
import Objects.TagMap;
import Transform.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import tagviz.TagViz;

public class GraphControl implements Serializable {
    // <editor-fold defaultstate="collapsed" desc="variables">
    transient XYChart chart;
    transient Heatmap heatmap;
    transient NumberAxis xAxis;
    transient NumberAxis yAxis;
    transient XYChart.Series selectedSeries;
    transient ContextMenu contextMenu;
    transient int xMaxValue;
    
    transient Transform transform;

    GRAPHTYPE type;
    SYMBOL symbol;
    STROKE xStroke, yStroke;
    Side legendSide;
    final static int MAX_ITEMS = 100;
    String[] seriesColor = new String[MAX_ITEMS];
    String[] areaFillColor = new String[MAX_ITEMS];
    String[] lineweight = new String[MAX_ITEMS*2];
    
    final static int NUM_HEATMAP_COLORS = 3;
    String[] heatmapColors = new String[NUM_HEATMAP_COLORS];
    String[] heatmapValues = new String[NUM_HEATMAP_COLORS];

    int xMaxTickCount, yMaxTickCount, smoothingBin;

    Boolean showLegend, showXticks, showYticks, showXlabels, showYlabels,
            openFile;

    float  constantForLogScale;
    
    String background, graphArea, borderColor, titleColor,
            axisColor, legendLabels, legendBG, xGridlines,
            yGridlines, tickLines, tickLabels, symbolFill;

    String titleFont, titleFontSize, axesFont, axesFontSize,
            legendFont, legendFontSize, tickFont, tickFontSize,
            tickWidth, tickLength, title, xlabel, ylabel;

    CLUSTERING_TYPE clustering;
    HEATMAP_GROUP clusteringGroup;
    String clusteringDistanceFunction, clusteringLinkType;
    
    int    kMeanNumClusters;
    int    heatmapSeriesHeight;
    
    TRANSFORM_TYPE transformFlag;
    
    // </editor-fold>
    public enum GRAPHTYPE {
        LINE, AREA
    }

    public static enum SYMBOL {
        NO, CIRCLE, SQUARE, DIAMOND
    }

    public static enum STROKE {
        SOLID, NONE
    }

    public static enum HEATMAP_GROUP {
        SERIESWISE, FEATUREWISE, SAMPLEWISE
    }
    
    public static enum TRANSFORM_TYPE {
        NO,
        LOG,
        STANDARDIZATION,
        QUANTILE
    }
    
    public static enum CLUSTERING_TYPE {
        NO,
        HIERARCHICAL,
        KMEAN
    }
    
    public GraphControl() 
    {
        // set defaults;
        type = GRAPHTYPE.LINE;
        legendSide = Side.TOP;
        symbol = SYMBOL.NO;

        showXticks = showYticks = showXlabels = showYlabels = showLegend = openFile = true;
        background = graphArea = legendBG = symbolFill = "#ffffff";
        borderColor = xGridlines = yGridlines = tickLines = "#cccccc";
        titleColor = axisColor = tickLabels = legendLabels = "#000000";
        xStroke = yStroke = STROKE.SOLID;
        titleFont = axesFont = legendFont = tickFont = "System";
        axesFontSize = legendFontSize = tickFontSize = "11";
        titleFontSize = "16";

        tickWidth = "1";
        tickLength = "8";
        xMaxTickCount = 0;
        yMaxTickCount = 0;
	smoothingBin = 0;
        seriesColor[0] = "#f9d900";
        seriesColor[1] = "#a9e200";
        seriesColor[2] = "#22bad9";
        seriesColor[3] = "#0181e2";
        seriesColor[4] = "#2f357f";
        seriesColor[5] = "#860061";
        seriesColor[6] = "#c62b00";
        seriesColor[7] = "#ff5700";
        areaFillColor[0] = "#f9d90044";
        areaFillColor[1] = "#a9e20044";
        areaFillColor[2] = "#22bad944";
        areaFillColor[3] = "#0181e244";
        areaFillColor[4] = "#2f357f44";
        areaFillColor[5] = "#86006144";
        areaFillColor[6] = "#c62b0044";
        areaFillColor[7] = "#ff570044";
        
        heatmapColors[0] = "#ff0000";
        heatmapColors[1] = "#000000";
        heatmapColors[2] = "#00ff00";
        
        heatmapValues[0] = "MAX";
        heatmapValues[1] = "CENTER";
        heatmapValues[2] = "MIN";
        
        constantForLogScale = 1.0f;
        
        clustering = CLUSTERING_TYPE.HIERARCHICAL;
        clusteringGroup = HEATMAP_GROUP.SERIESWISE;
        clusteringDistanceFunction = "Euclidean";
        clusteringLinkType = "Average";
        
        transformFlag = TRANSFORM_TYPE.NO;
        
        kMeanNumClusters    = 2;
        heatmapSeriesHeight = 32;
    }

    // <editor-fold defaultstate="collapsed" desc="POPUP - GRAPH">
    public void setType(GRAPHTYPE type) {
        this.type = type;
    }

    public GRAPHTYPE getType() {
        return type;
    }

    public void setLegend(Side s) {
        if (s == null) {
            if (chart != null) {
                chart.setLegendVisible(false);
            }
            showLegend = false;
        } else {
            legendSide = s;
            showLegend = true;
            if (chart != null) {
                chart.setLegendVisible(true);
                chart.setLegendSide(legendSide);
            }
        }
    }

    public Side getLegend() {
        return legendSide;
    }

    public void setSymbol(SYMBOL s) {
        symbol = s;
    }

    public SYMBOL getSymbol() {
        return symbol;
    }
    
    public void setConstantForLogScale( float c ) {
        constantForLogScale = c;
    }
    
    public float getConstantForLogScale() {
        return constantForLogScale;
    }

    
    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="POPUP - COLORS">  
    public void setBackgroundColor(Color c) {
        background = ColorToHex(c);
        ChangeChartCSS(".chart", "-fx-background-color", ColorToHex(c));
    }

    public Color getBackgroundColor() {
        return Color.web(background);
    }

    public void setGraphAreaColor(Color c) {
        graphArea = ColorToHex(c);
        setGraphBorderColor(Color.web(borderColor));
    }

    public Color getGraphAreaColor() {
        return Color.web(graphArea);
    }

    public void setGraphBorderColor(Color c) {
        borderColor = ColorToHex(c);
        String s = String.format("%s, %s", borderColor, graphArea);
        ChangeChartCSS(".chart-plot-background", "-fx-background-color", s);
    }

    public Color getGraphBorderColor() {
        return Color.web(borderColor);
    }

    public void setTitleColor(Color c) {
        titleColor = ColorToHex(c);
        ChangeChartCSS(".chart-title", "-fx-font", String.format("%s \"%s\"; -fx-text-fill: %s", titleFontSize, titleFont, titleColor));

    }

    public Color getTitleColor() {
        return Color.web(titleColor);
    }

    public void setAxisColor(Color c) {
        axisColor = ColorToHex(c);
        ChangeChartCSS(".axis-label", "-fx-font", String.format("%s \"%s\"; -fx-text-fill: %s", axesFontSize, axesFont, axisColor));

    }

    public Color getAxisColor() {
        return Color.web(axisColor);
    }

    public void setLegendLabelsColor(Color c) {
        legendLabels = ColorToHex(c);
        ChangeChartCSS(".chart-legend-item", "-fx-text-fill", legendLabels);
    }

    public Color getLegendLabelsColor() {
        return Color.web(legendLabels);
    }

    public void setLegendBGColor(Color c) {
        legendBG = ColorToHex(c);
        ChangeChartCSS(".chart-legend", "-fx-font", String.format("%s \"%s\"; -fx-background-color: %s", legendFontSize, legendFont, legendBG));
    }

    public Color getLegendBGColor() {
        return Color.web(legendBG);
    }

    public void setXGridlinesColor(Color c) {
        xGridlines = ColorToHex(c);
        if (!xStroke.equals(STROKE.NONE)) {
            ChangeChartCSS(".chart-vertical-grid-lines", "-fx-stroke", xGridlines);
        } else {
            ChangeChartCSS(".chart-vertical-grid-lines", "-fx-stroke", "transparent");
        }
    }

    public Color getXGridlinesColor() {
        return Color.web(xGridlines);
    }

    public void setYGridlinesColor(Color c) {
        yGridlines = ColorToHex(c);
        if (!yStroke.equals(STROKE.NONE)) {
            ChangeChartCSS(".chart-horizontal-grid-lines", "-fx-stroke", yGridlines);
        } else {
            ChangeChartCSS(".chart-horizontal-grid-lines", "-fx-stroke", "transparent");
        }
    }

    public Color getYGridlinesColor() {
        return Color.web(yGridlines);
    }

    public void setTickLabelsColor(Color c) {
        tickLabels = ColorToHex(c);
        /*xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        xAxis.setTickLabelFill(c);
        yAxis.setTickLabelFill(c);*/
    }

    public Color getTickLabelsColor() {
        return Color.web(tickLabels);
    }

    public void setTicklinesColor(Color c) {
        tickLines = ColorToHex(c);
        ChangeChartCSS(".axis-tick-mark", "-fx-stroke", String.format("%s; -fx-stroke-width: %s", tickLines, tickWidth));
        ChangeChartCSS(".axis-minor-tick-mark", "-fx-stroke", tickLines);
    }

    public Color getTicklinesColor() {
        return Color.web(tickLines);
    }

    public void setSymbolFillColor(Color c) {
        symbolFill = ColorToHex(c);
        for (int i = 0; i < 8; i++) {
            ChangeSeriesCSS(i, ".chart-line-symbol", "-fx-background-insets: 0, 2; -fx-background-color", String.format("%s, %s", getSeriesColor(i), symbolFill));
            ChangeSeriesCSS(i, ".chart-area-symbol", "-fx-background-insets: 0, 2; -fx-background-color", String.format("%s, %s", getSeriesColor(i), symbolFill));
        }
    }

    public Color getSymbolFillColor() {
        return Color.web(symbolFill);
    }

    public void setHeatmapColor( int i, Color color ) {
        heatmapColors[i] = ColorToHex(color);
        if( heatmap != null ) heatmap.setHeatColor( i, color );
    }
    
    public Color getHeatmapColor( int i ) {
        return Color.web(heatmapColors[i] );
    }
    
    public void setHeatmapValue( int i, String v ) {
        heatmapValues[i] = v;
    }
    
     public String getHeatmapValue( int i ) {
        return heatmapValues[i];
    }
    
    public int getSmoothingBin() {
	return smoothingBin;
    }

    public void setSmoothingBin(int smoothingBin) {
	this.smoothingBin = smoothingBin;
    }
    
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="POPUP - TEXTS">  
    public void setTitle(String s) {
        title = ValidateString(s);
        if (chart != null) {
            chart.setTitle(title);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setXLabel(String s) {
        xlabel = ValidateString(s);
        if( chart != null ) xAxis.setLabel(xlabel);
    }

    public String getXLabel() {
        return xlabel;
    }

    public void setYLabel(String s) {
        ylabel = ValidateString(s);
        if( chart != null ) yAxis.setLabel(ylabel);
    }

    public String getYLabel() {
        return ylabel;
    }

    public void setTitleFont(String family, String size) {
        titleFontSize = size;
        titleFont = family;
    }

    public String getTitleFontSize() {
        return titleFontSize;
    }

    public String getTitleFontFamily() {
        return titleFont;
    }

    public void setAxesFont(String family, String size) {
        axesFontSize = size;
        axesFont = family;
    }

    public String getAxesFontFamily() {
        return axesFont;
    }

    public String getAxesFontSize() {
        return axesFontSize;
    }

    public void setTickLabelFont(String family, String size) {
        tickFontSize = size;
        tickFont = family;
    }

    public String getTickLabelFontFamily() {
        return tickFont;
    }

    public String getTickLabelFontSize() {
        return tickFontSize;
    }

    public void setLegendFont(String family, String size) {
        legendFontSize = size;
        legendFont = family;
    }

    public String getLegendFontFamily() {
        return legendFont;
    }

    public String getLegendFontSize() {
        return legendFontSize;
    }

    // </editor-fold>     
    // <editor-fold defaultstate="collapsed" desc="POPUP - GRID">
    public void setShowXTicks(Boolean b) {
        showXticks = b;
        if( xAxis != null ) xAxis.setTickMarkVisible(b);
    }

    public Boolean getShowXTicks() {
        return showXticks;
    }

    public void setShowYTicks(Boolean b) {
        showYticks = b;
        if( yAxis != null ) yAxis.setTickMarkVisible(b);
    }

    public Boolean getShowYTicks() {
        return showYticks;

    }

    public void setShowXlabels(Boolean b) {
        showXlabels = b;
        if( xAxis != null ) xAxis.setTickLabelsVisible(b);
    }

    public Boolean getShowXlabels() {
        return showXlabels;
    }

    public void setShowYlabels(Boolean b) {
        showYlabels = b;
        if( yAxis != null ) yAxis.setTickLabelsVisible(b);
    }

    public Boolean getShowYlabels() {
        return showYlabels;
    }

    public void setXMaxTickCount(int i) {
        xMaxTickCount = i;
    }

    public int getXMaxTickCount() {
        return (int) xMaxTickCount;
    }

    public void setYMaxTickCount(int i) {
        yMaxTickCount = i;
    }

    public int getYMaxTickCount() {
        return (int) yMaxTickCount;
    }

    public void setXGridLineStyle(STROKE s) {
        xStroke = s;
    }

    public STROKE getXGridLineStyle() {
        return xStroke;
    }

    public void setYGridLineStyle(STROKE s) {
        yStroke = s;
    }

    public STROKE getYGridLineStyle() {
        return yStroke;
    }

    public String getStrokeStyle(STROKE s) {
        if (s.equals(STROKE.SOLID)) {
            return "Solid";
        } else {
            return "None";
        }
    }

    public void setTickWidth(String s) {
        tickWidth = s;
        ChangeChartCSS(".axis-tick-mark", "-fx-stroke", String.format("%s; -fx-stroke-width: %s", tickLines, tickWidth));
    }

    public String getTickWidth() {
        return tickWidth;
    }

    public void setTickLength(String s) {
        tickLength = s;
        ChangeChartCSS(".axis", "-fx-tick-length", s);
    }

    public String getTickLength() {
        return tickLength;
    }
    
    public void setTransform( TRANSFORM_TYPE f ) {
        transformFlag = f;
    }
    
    public TRANSFORM_TYPE getTransform() {
        return transformFlag;
    }
    
    public void setHeatmapSeriesHeight( int height ) {
        heatmapSeriesHeight = height;
    }
    
    public int getHeatmapSeriesHeight() {
        return heatmapSeriesHeight;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="POPUP - CLUSTER">
    
    public CLUSTERING_TYPE getClustering() {
        return clustering;
    }
    
    public void setClustering( CLUSTERING_TYPE value ) {
        clustering = value;
    }
    
    public void setClusteringGroup( HEATMAP_GROUP value ) {
        clusteringGroup = value;
    }
    
    public void setClusteringDistanceFunction( String value ) {
        clusteringDistanceFunction = value;
    }
    
    public void setKMeanNumClusters( int num ) {
        kMeanNumClusters = num;
    }
    
    public void setClusteringLinkType( String value ) {
        clusteringLinkType = value;
    }
    
    public HEATMAP_GROUP getClusteringGroup() {
        return clusteringGroup;
    }
    
    public String getClusteringDistanceFunction() {
        return clusteringDistanceFunction;
    }
    
    public int getKMeanNumClusters() {
        return kMeanNumClusters;
    }
    
    public String getClusteringLinkType() {
        return clusteringLinkType;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="STYLE - Support methods">
    private String ColorToHex(Color c) {
        return String.format("#%02X%02X%02X", (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }

    private String ValidateString(String s) {
        s = s.trim();
        return s.substring(0, Math.min(150, s.length()));
    }

    private String getSeriesColor(int i) {
        return getColor(i, seriesColor);
    }

    private String getAreaColor(int i) {
        return getColor(i, areaFillColor);
    }

    private String getColor(int i, String[] s) {
        String color = "#999999";
        if (s != null && i >= 0 && i < s.length && s[i] != null) {
            color = s[i];
        } else if ( s != null && i >= 0 && i < s.length && s[ i % 8 ] != null ) {
            color = s[i%8];
        }
        return color;
    }

    public String getLineWeight(int i) {
        i = i + (type.equals(GRAPHTYPE.AREA) ? MAX_ITEMS : 0);
        if (i < lineweight.length && lineweight[i] != null) {
            return lineweight[i];
        }
        return (i > MAX_ITEMS ? "1" : "3");
    }
    
    private void setLineWeight(int i, String weight) {
	lineweight[i + (type.equals(GRAPHTYPE.AREA) ? MAX_ITEMS : 0)] = weight;
    }

    public void setAllLineWeight(GraphControl.GRAPHTYPE type, int n) {
	int b, e;
	if (type.equals(GRAPHTYPE.LINE)) { b = 0; e = MAX_ITEMS; }
	else                             { b = MAX_ITEMS; e = 2*MAX_ITEMS; }
	for (int i = b; i < e; i++) lineweight[i] = Integer.toString(n);
    }
    
    private int getSelectionIndex(String name) {
        int i = -1;
        if (chart != null) {
            for (int n = 0; n < chart.getData().size(); n++) {
                String s = ((XYChart.Series) chart.getData().get(n)).getName();
                if (s.equals(name)) {
                    i = n;
                    break;
                }
            }
        }
        return i;
    }

    private void ChangeLine(String name, String color, String weight) {
        int i = getSelectionIndex(name);
        ChangeLine(i, color, weight);
        
    }
    
    private void resetLabelColors() {
        Set<Node> items = chart.lookupAll("Label.chart-legend-item");
        for( Node n : items ) {
            Label label = (Label)n;
            label.setStyle( "-fx-text-fill:" + getSeriesColor( getSelectionIndex( label.getText() ) ) );
        }
    }

    private void ChangeLine(int i, String color, String weight) {
        if (i >= 0) {
            if (color == null) {
                color = getSeriesColor(i);
            }
            if (weight == null) {
                weight = getLineWeight(i);
            }
            seriesColor[i] = color;
            setLineWeight(i, weight);
            String s = String.format("-fx-stroke-width: %s; -fx-stroke", weight);
            ChangeSeriesCSS(i, ".chart-series-area-line", s, color);
            ChangeSeriesCSS(i, ".chart-series-line", s, color);
            setSymbolFillColor(getSymbolFillColor());

            /* update legend colors */
            Set<Node> items = chart.lookupAll("Label.chart-legend-item");
            int n = 0;
            for (Node item : items) {
                Label label = (Label) item;
                final Rectangle rectangle = new Rectangle(10, 10, Color.web(getSeriesColor(n)));
                label.setGraphic(rectangle);
                n++;
            }
        }
    }

    private void ChangeAreaColor(String name, String c) {
        int i = getSelectionIndex(name);
        ChangeAreaColor(i, c);
    }

    private void ChangeAreaColor(int i, String c) {
        if (i >= 0) {
            String color = String.format("%s44", c);
            if (c == null) {
                color = getAreaColor(i);
            }
            areaFillColor[i] = color;
            ChangeSeriesCSS(i, ".chart-series-area-fill", "-fx-fill", color);
        }
    }

    private void ChangeSeriesCSS(int index, String lookup, String property, String value) {
        ChangeChartCSS(String.format(".default-color%s%s", index, lookup), property, value);
    }

    private void ChangeChartCSS(String lookup, String property, String value) {
        if (chart != null) {
            Set<Node> nodes = chart.lookupAll(lookup);
            for (final Node n : nodes) {
                n.setStyle(String.format("%s: %s;", property, value));
                //int i = 1;
            }
        }
    }

    // </editor-fold>    
    
    private void InitTransform( final TagList l ) {
        switch( transformFlag ) {
            case NO:
                transform = new IdentityTransform();
                break;
            case LOG:
                transform = new LogTransform(constantForLogScale);
                break;
            case STANDARDIZATION:
                transform = new StdTransform( l );
                break;
            case QUANTILE:
                transform = new QuantileTransform( l );
                break;
        }
    }
    
    public XYChart GenerateChart(final TagViz app, final TagMap cData ) {

        if (!cData.isEmpty()) {
            
            xAxis = new NumberAxis();
            yAxis = new NumberAxis();

	    if (xMaxTickCount == 0) {
		xAxis.setAutoRanging(true);
	    } else {		
		double xTickunit = Math.max(1, Math.round(app.header.length / xMaxTickCount));
		xAxis.setAutoRanging(false);
		xAxis.setUpperBound(app.header.length);
		xAxis.setLowerBound(0);
		xAxis.setTickUnit(xTickunit);
	    }

	    if (yMaxTickCount == 0) {
		yAxis.setAutoRanging(true);
	    } else {		
		float[] m = cData.getMaxAndMin();
                computeYValues( m );
                //[NOTE]: Modified by SH Kim.
		//double yTickunit = Math.max(0.001d, m[0] / yMaxTickCount);
                double yTickunit = Math.max(0.001d, (m[0] - m[1]) / yMaxTickCount );
		yAxis.setAutoRanging(false);
		yAxis.setUpperBound(m[0] + yTickunit);
		yAxis.setLowerBound(m[1]);
		yAxis.setTickUnit(yTickunit);
//		yAxis.setMinorTickVisible(false);
	    }
            
	    xAxis.setLabel(xlabel);
            yAxis.setLabel(ylabel);
            xAxis.setTickLabelsVisible(showXlabels);
            yAxis.setTickLabelsVisible(showYlabels);
            xAxis.setTickMarkVisible(showXticks);
            yAxis.setTickMarkVisible(showYticks);
	    xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
		@Override
		public String toString(Number object) {
		    int i = object.intValue();
		    if (i == app.header.length) i--; // to show the last label
//		    return i < app.header.length ? String.format("%s", app.header[i]) : "";
		    return (i >= app.header.length) ? "" : 
			    !app.header[i].endsWith("%") ? app.header[i] :
				String.format("%.0f%%", new Float(app.header[i].substring(0, app.header[i].length()-1)));
		}
	    });
            yAxis.setTickLabelFormatter(getYAxisFormatter());

            /* create chart */
            if (type.equals(GRAPHTYPE.AREA)) {
                chart = new AreaChart<>(xAxis, yAxis);
            } else {
                chart = new LineChart<>(xAxis, yAxis);
            }

            if (chart != null) {
                chart.setTitle(title);
                chart.setCache(true);
                chart.setAnimated(false);
		xMaxValue = 0;
                
                /* plot data */
                String[] headers = app.header;
                ArrayList<String> keys = new ArrayList<>();
                for (String name : cData.keySet()) {
                    keys.add(name);
                }
                
                Collections.sort(keys);
                
                for(String name: keys) {
                    TagList d = cData.get(name);
                    InitTransform( d );
                    
                    XYChart.Series series = new XYChart.Series();
                    series.setName(name);

                    int min = Math.min(headers.length, d.size());
		    if (xMaxValue < min) xMaxValue = min;
                    
                    for( TagListSmoothingIterator i = new TagListSmoothingIterator(d, smoothingBin, min); i.end(); i.next() ) {
                        addDataToChartSeries(series, i.getCurrentIndex(), i.getCurrentValue());
                    }
                    
		    chart.getData().add(series);
                    applyMouseEvents(series);
                }
                ApplyStyles(cData);
            }
        }
        return chart;
    }
    
    private Heatmap createHeatmap( String linkType, String distanceFunction ) {
        switch( clustering ) {
            case NO:
                switch( clusteringGroup ) {
                    case SERIESWISE : return new SerieswiseHeatmap ( null );
                    case FEATUREWISE: return new FeaturewiseHeatmap( null );
                    case SAMPLEWISE : return new SamplewiseHeatmap ( null );
                }
                break;
            case HIERARCHICAL:
                switch( clusteringGroup ) {
                    case SERIESWISE : return new SerieswiseHeatmap ( new WekaSeriesWiseHierarchicalClustererWrapper ( linkType, distanceFunction ));
                    case FEATUREWISE: return new FeaturewiseHeatmap( new WekaFeatureWiseHierarchicalClustererWrapper( linkType, distanceFunction ) );
                    case SAMPLEWISE : return new SamplewiseHeatmap ( new WekaSampleWiseHierarchicalClustererWrapper( linkType, distanceFunction ) );
                }
                break;
            case KMEAN:
                switch( clusteringGroup ) {
                    case SERIESWISE : return new SerieswiseHeatmap ( new WekaSeriesWiseKMeansClustererWrapper ( kMeanNumClusters, distanceFunction ));
                    case FEATUREWISE: return new FeaturewiseHeatmap( new WekaFeatureWiseKMeansClustererWrapper( kMeanNumClusters, distanceFunction ));
                    case SAMPLEWISE : return new SamplewiseHeatmap ( new WekaSampleWiseKMeansClustererWrapper ( kMeanNumClusters, distanceFunction ));
                }
        }
        return null;
    }
    
    private void assignHeatmapProperty() {
        /* TODO: Remaining properties
        
            GRAPHTYPE type;
            SYMBOL symbol;
            STROKE xStroke, yStroke;
            Side legendSide;
            final static int MAX_ITEMS = 100;
            String[] seriesColor = new String[MAX_ITEMS];
            String[] areaFillColor = new String[MAX_ITEMS];
            String[] lineweight = new String[MAX_ITEMS*2];

            int xMaxTickCount, yMaxTickCount, smoothingBin;

            Boolean showLegend, showYlabels, showYticks;
                    openFile,
                    logScaledY;

            float  constantForLogScale;
        
            String symbolFill;
        */

        // Temporarily assigned 
        heatmap.setDendrogramLineColor    (Color.web(axisColor));
        heatmap.setLabelColor             (Color.web(legendLabels));
        heatmap.setLabelBackgroundColor   (Color.web(legendBG));
        heatmap.setLabelFont              (Font.font(legendFont, Double.parseDouble(legendFontSize)));
        ////////////////////////
        
        heatmap.setClustering(clustering != CLUSTERING_TYPE.NO);
        
        heatmap.setDrawingAreaColor       (Color.web(background));
        heatmap.setPlotBackgroundColor    (Color.web(graphArea));
        heatmap.setPlotBorderColor        (Color.web(borderColor));
        heatmap.setTitleColor             (Color.web(titleColor));
        heatmap.setGridHorizontalLineColor(Color.web(yGridlines));
        heatmap.setGridVerticalLineColor  (Color.web(xGridlines));
        heatmap.setTickLineColor          (Color.web(tickLines));
        heatmap.setTickLabelColor         (Color.web(tickLabels));

        heatmap.setTitleFont     ( Font.font(titleFont, Double.parseDouble( titleFontSize ) ) );
        heatmap.setXAxisLabelFont( Font.font(axesFont , Double.parseDouble( axesFontSize  ) ) );
        heatmap.setYAxisLabelFont( Font.font(axesFont , Double.parseDouble( axesFontSize  ) ) );
        
        heatmap.setTickLineWidth  ( Double.parseDouble( tickWidth ) );
        heatmap.setTickLineLength ( Double.parseDouble( tickLength ) );
        heatmap.setTickLabelFont  ( Font.font(tickFont, Double.parseDouble(tickFontSize)) );
        
        heatmap.setTitle     (title);
        heatmap.setXAxisLabel(xlabel);
        heatmap.setYAxisLabel(ylabel);
        heatmap.setXAxisLabelColor(Color.web(axisColor));
        heatmap.setYAxisLabelColor(Color.web(axisColor));
        
        heatmap.setShowXTicks     ( showXticks );
        heatmap.setShowXTickLabels( showXlabels );
        
        // HorizontalGridStroke
        heatmap.setShowHorizontalGridlines(!yStroke.equals(STROKE.NONE));
        // VerticalGridStroke
        heatmap.setShowVerticalGridlines  (!xStroke.equals(STROKE.NONE));
        
        heatmap.setSeriesHeight(heatmapSeriesHeight);
    }
    
    public Heatmap GenerateHeatmap(TagViz app, TagMap cData) {
        if (cData.isEmpty()) return heatmap;
                
        heatmap = createHeatmap( clusteringLinkType, clusteringDistanceFunction );
        
        assignHeatmapProperty();
        
        xMaxValue = 0;
                
        /* plot data */
        Float minValue = Float.MAX_VALUE, maxValue = Float.MIN_VALUE, meanValue = 0.0f;
        long cnt = 0;
        
        String[] headers = app.header;
        heatmap.setHeader( app.header );
        ArrayList<String> keys = new ArrayList<>();
        for (String name : cData.keySet()) {
            keys.add(name);
        }

        Collections.sort(keys);
                
        for(String name: keys) {
            TagList d = cData.get(name);
            InitTransform( d );
            
            int min = Math.min(headers.length, d.size());
            if (xMaxValue < min) xMaxValue = min;

            Heatmap.Series series = heatmap.newSeries( name );
            
            for( TagListSmoothingIterator i = new TagListSmoothingIterator(d, smoothingBin, min); i.end(); i.next() ) {
                
                Float v = i.getCurrentValue();
                addDataToHeatmapSeries(series, i.getCurrentIndex(), v);
                
                if( !( v.isInfinite() || v.isNaN() ) ) {
                    float v_transformed = computeYValue( v );
                    if( v_transformed < minValue ) minValue = v_transformed;
                    if( maxValue < v_transformed ) maxValue = v_transformed;
                    meanValue += v_transformed;
                    ++cnt;
                }
            }
        }
        meanValue /= cnt;

        class ColorPosPair implements Comparable<ColorPosPair>{
            public Color  color;
            public Double pos;
            
            ColorPosPair( Color c, double p ) {
                color = c;
                pos   = p;
            }
            
            @Override
            public int compareTo( ColorPosPair x ) {
                return pos.compareTo( x.pos );
            }
        }
        
        ColorPosPair[] pairColorPos = new ColorPosPair[ heatmapColors.length ];
        for( int i = 0; i < heatmapColors.length; ++i ) {

            double value;
            switch( heatmapValues[i] ) {
                case "MIN":      value = minValue;    break;
                case "MAX":      value = maxValue;    break;
                case "MEAN":     value = meanValue;   break;
                case "CENTER":   value = (minValue+maxValue)/2;   break;
                //case "MEDIAN":  value = medianValue; break;
                case "DISABLED": value = Double.NaN;  break;
                default:         value = Double.parseDouble( heatmapValues[i] );
            }
            pairColorPos[i] = new ColorPosPair( Color.web( heatmapColors[i] ), value );
        }
        Arrays.sort( pairColorPos );
        
        int numColors = 0;
        for( int i = 0; i < heatmapColors.length; ++i ) {
            if( !Double.isNaN(pairColorPos[i].pos) ) numColors++;
        }
        
        heatmap.setNumColors( numColors + 2 );
        
        heatmap.setHeatColor(0, pairColorPos[0].color);
        heatmap.setHeatValue(0, minValue);
        int j = 1;
        for( int i = 0; i < heatmapColors.length; ++i ) {
            if( Double.isNaN(pairColorPos[i].pos) ) continue;
            heatmap.setHeatColor(j, pairColorPos[i].color);
            heatmap.setHeatValue(j, pairColorPos[i].pos);
            
            heatmap.setHeatColor(j+1, pairColorPos[i].color);
            heatmap.setHeatValue(j+1, maxValue);
            ++j;
        }
        
        return heatmap;
    }
    
    private StringConverter<Number> getYAxisFormatter() {
        return new NumberAxis.DefaultFormatter(yAxis) {
                @Override
                public String toString(Number object) {
                    return String.format("%6.3f", object);
                }
            };
        /*
        if( getLogScaledY() ) {
            return new NumberAxis.DefaultFormatter(yAxis) {
                final float constantFactor = getConstantForLogScale();
                
                @Override
                public String toString(Number object) {
                    return String.format("%6.3f", (float)Math.pow(10.0d, object.floatValue()) - constantFactor );
                }
            };
        } else {
            return new NumberAxis.DefaultFormatter(yAxis) {
                @Override
                public String toString(Number object) {
                    return String.format("%6.3f", object);
                }
            };
        }*/
    }
    
    private float computeYValue( float y ) {
        return transform.transform(y);
        //return getLogScaledY()?(float)Math.log10( y + this.getConstantForLogScale() ):y;
    }
    
    private void computeYValues( float[] ys ){
        for( int i = 0; i < ys.length; ++i ) {
            //ys[i] = computeYValue( ys[i] );
            ys[i] = transform.transform(ys[i]);
        }
    }
    
    private void addDataToChartSeries(XYChart.Series series, int i, Float value) {
	if (value == null || value.isNaN() ) return;
        float y = computeYValue( value );
        
	final Data<Integer, Float> data = new Data<>(i, y);
//	data.setNode(new HoveredThresholdNode(sum/count));
        
	series.getData().add(data);
    }
    
    private void addDataToHeatmapSeries(Heatmap.Series series, int i, Float value) {
	if (value == null || value.isNaN() ) return;
        float y = computeYValue( value );

	series.add( y );
    }
    
    private void ApplyStyles(TagMap cData) {
        System.out.println( "Applying- " );
        if (chart != null) {

        System.out.println( "Applying1 " );
            xAxis.setTickLabelFill(getTickLabelsColor());
            yAxis.setTickLabelFill(getTickLabelsColor());
            xAxis.setTickLabelFont(Font.font(tickFont, Double.parseDouble(tickFontSize)));
            yAxis.setTickLabelFont(Font.font(tickFont, Double.parseDouble(tickFontSize)));
            chart.setLegendSide(legendSide);
            chart.setLegendVisible(showLegend);
            setTickLength(getTickLength());

            setBackgroundColor(getBackgroundColor());
            setGraphBorderColor(getGraphBorderColor());
            setTitleColor(getTitleColor());
            setAxisColor(getAxisColor());
            setTicklinesColor(getTicklinesColor());
            setLegendLabelsColor(getLegendLabelsColor());
            setLegendBGColor(getLegendBGColor());
            setXGridlinesColor(getXGridlinesColor());
            setYGridlinesColor(getYGridlinesColor());

            if (symbol.equals(SYMBOL.NO)) {
        System.out.println( "Applying2 " );
                chart.getStyleClass().add("no-symbol");
            } else if (symbol.equals(SYMBOL.SQUARE)) {
                chart.getStyleClass().add("square-symbol");
            } else if (symbol.equals(SYMBOL.CIRCLE)) {
                chart.getStyleClass().add("circle-symbol");
            } else {
                chart.getStyleClass().add("diamond-symbol");
            }

            /* series colors */
            for (String name : cData.keySet()) {
                int i = getSelectionIndex(name);
                for (Node node : chart.lookupAll(".series" + i)) {
                    node.getStyleClass().remove("default-color" + (i % 8));
                    node.getStyleClass().add("default-color" + i);
                }
                
                ChangeLine(name, null, null);
                ChangeAreaColor(name, null);
            }
        }
    }

    public boolean saveChartToTable(File file) {
	NumberAxis xaxis = (NumberAxis) chart.getXAxis();

	try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
	    bw.write(xaxis.getLabel() != null ? xaxis.getLabel() : "Location");
	    for (Object o : chart.getData()) {
		XYChart.Series<Integer, Float> series = (XYChart.Series<Integer, Float>) o;
		bw.write("\t" + series.getName());
	    }
	    bw.write('\n');

	    for (int i = 0; i < xMaxValue; i++) {
		bw.write(xaxis.getTickLabelFormatter().toString(i));
		for (Object o : chart.getData()) {
		    XYChart.Series<Integer, Float> series = (XYChart.Series<Integer, Float>) o;
		    XYChart.Data<Integer, Float> data = series.getData().get(i);
		    bw.write("\t" + data.getYValue());
		}
		bw.write('\n');
	    }
	} catch (IOException ex) {
	}
	
	return true;
    }

    // <editor-fold defaultstate="collapsed" desc="Series mouse events">
    private void applyMouseEvents(final XYChart.Series series) {
        final Node node = series.getNode();
        if (node != null) {
            node.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    node.setCursor(Cursor.HAND);
                }
            });

            node.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent arg0) {
                    node.setCursor(Cursor.DEFAULT);
                }
            });

            node.setOnMouseReleased(new EventHandler<MouseEvent>() {
                
                @Override
                public void handle(MouseEvent mouseEvent) {
                    
                    if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        /* do not create duplicate menus if one already exists */
                        
                        if (contextMenu == null) {
                            contextMenu = new ContextMenu() {
                                @Override
                                public void show() {
                                    int i = getSelectionIndex( selectedSeries.getName() );
                                    String s = String.format("-fx-stroke-width: 3; -fx-stroke");
                                    ChangeSeriesCSS(i, ".chart-series-area-line", s, "#000000");
                                    ChangeSeriesCSS(i, ".chart-series-line", s, "#000000");
                                    ChangeSeriesCSS(i, ".chart-series-area-fill", "-fx-fill", "#00000077");
                                    super.show();
                                }
                                
                                @Override
                                public void hide() {
                                    ChangeLine     (selectedSeries.getName(), (String)null, (String)null );
                                    ChangeAreaColor(selectedSeries.getName(), (String)null);
                                    super.hide();
                                }
                            };
                            contextMenu.getStyleClass().add("PopupPanel");
                        }
                        contextMenu.getItems().clear();

                        String weight = getLineWeight(getSelectionIndex(series.getName()));
                        String lcolor = getSeriesColor(getSelectionIndex(series.getName()));
                        String[] w = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
                        final CustomMenuItem menuItem = new CustomMenuItem (new Label( series.getName()), false);
                        final ColorPickerMenuItem lineColorMenuItem = new ColorPickerMenuItem("Line color", Color.web(lcolor));
                        final ComboBoxMenuItem lineWeightMenuItem = new ComboBoxMenuItem("Line weight", w);
                        lineWeightMenuItem.setValue(weight);
                        
                        EventHandler change_line = new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent arg0) {
                                if (selectedSeries != null) {
                                    ChangeLine(selectedSeries.getName(), ColorToHex(lineColorMenuItem.getValue()),
                                            lineWeightMenuItem.getValue());
                                }
                            }
                        };
                        lineColorMenuItem.getPrompt().fillProperty().addListener(new ChangeListener() {
                            @Override
                            public void changed(ObservableValue ov, Object t, Object t1) {
                                if (selectedSeries != null) {
                                    ChangeLine(selectedSeries.getName(), ColorToHex(lineColorMenuItem.getValue()),
                                            lineWeightMenuItem.getValue());
                                }
                            }
                        });

                        lineWeightMenuItem.getComboBox().setOnAction(change_line);
                        contextMenu.getItems().add(menuItem);
                        contextMenu.getItems().add(lineColorMenuItem);
                        contextMenu.getItems().add(lineWeightMenuItem);

                        if (type.equals(GRAPHTYPE.AREA)) {
                            String acolor = getAreaColor(getSelectionIndex(series.getName()));
                            final ColorPickerMenuItem fillMenuItem = new ColorPickerMenuItem("Area color", Color.web(acolor));

                            fillMenuItem.getPrompt().fillProperty().addListener(new ChangeListener() {
                                @Override
                                public void changed(ObservableValue ov, Object t, Object t1) {
                                    if (selectedSeries != null) {
                                        ChangeAreaColor(selectedSeries.getName(), ColorToHex(fillMenuItem.getValue()));
                                    }
                                }
                            });
                            contextMenu.getItems().add(1, fillMenuItem);
                        }

                        selectedSeries = series;
                        contextMenu.show(node, mouseEvent.getScreenX() + 1, mouseEvent.getScreenY() + 1);
                    }
                }
            });
        }
    }

    class HoveredThresholdNode extends StackPane {

        HoveredThresholdNode(Float value) {

            final Label label = createDataThresholdLabel(value);
            final VBox vbox = new VBox();
            vbox.setMinHeight(70);
            vbox.getChildren().add(label);
            vbox.setAlignment(Pos.TOP_CENTER);

            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getChildren().setAll(vbox);
                    vbox.toFront();
                }
            });
            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getChildren().clear();
                }
            });
        }

    }

    private Label createDataThresholdLabel(Float value) {
        final Label label = new Label(String.format("%s", value));
        label.getStyleClass().addAll("default-color0", "chart-series-line", "chart-info-label");
        label.setTextFill(Color.BLACK);
        label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        return label;
    }

    // </editor-fold>   
}
