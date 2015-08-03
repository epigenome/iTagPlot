/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Heatmap;

/**
 *
 * @author SHKim12
 */
import Clustering.ITagListClusterer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public abstract class Heatmap extends AnchorPane {

    static public class Series extends ArrayList<Float> {
    }
    
    private class TreeNode {
        TreeNode() {
                m_level    = 0;
                m_children = null;
                m_weight   = 0;
        }

        public void addChild( TreeNode child ) {
                if( m_children == null ) m_children = new ArrayList<>();
                m_children.add( child );
                if( m_level < child.getDepth() + 1 ) m_level = child.getDepth() + 1;
        }
        
        public List<TreeNode> getChild() {
            return m_children;
        }

        public int getDepth() {
                return m_level;
        }
        
        public void setWeight( double w ) {
            m_weight = w;
        }

        public String string() {
                StringBuilder x = new StringBuilder();
                x.append( '(' );
                for( TreeNode v : m_children ) {
                        x.append( v.string() );
                        x.append( ',' );
                }
                x.deleteCharAt( x.length() - 1 );
                x.append( ')' );
                return x.toString();
        }
        
        private double           m_weight;
        private int              m_level;
        private List< TreeNode > m_children;
    }

    private class TreeLeaf extends TreeNode {
            TreeLeaf( int id ) {
                    m_id = id;
            }

            @Override
            public String string() {
                    return m_id.toString();
            }
            
            public int getID() {
                return m_id;
            }
            
            private Integer m_id;
    }

    protected class Rect {
        Rect( double left, double top, double right, double bottom ) {
            this.left   = left;
            this.top    = top;
            this.right  = right;
            this.bottom = bottom;
        }
        
        public Rect cutTop( double v ) { 
            double newBottom = top+v;
            if( newBottom > bottom ) {
                newBottom = bottom;
            }
            Rect rect = new Rect( left, top, right, newBottom );
            top = newBottom;
            return rect;
        }
        
        public Rect cutLeft( double v ) { 
            double newRight = left+v;
            if( newRight > right ) {
                newRight = right;
            }
            Rect rect = new Rect( left, top, newRight, bottom );
            left = newRight;
            return rect;
        }
        
        public Rect cutRight( double v ) { 
            double newLeft = right-v;
            if( newLeft < left ) {
                newLeft = left;
            }
            Rect rect = new Rect( newLeft, top, right, bottom );
            right = newLeft;
            return rect;
        }
        
        public Rect cutBottom( double v ) { 
            double newTop = bottom-v;
            if( newTop < top ) {
                newTop = top;
            }
            Rect rect = new Rect( left, newTop, right, bottom );
            bottom = newTop;
            return rect;
        }
        
        public Rect throwTop( double v ) { cutTop( v ); return this; }
        public Rect throwLeft( double v ) { cutLeft( v ); return this; }
        public Rect throwRight( double v ) { cutRight( v ); return this; }
        public Rect throwBottom( double v ) { cutBottom( v ); return this; }
        
        public double getTop()    { return top; }
        public double getLeft()   { return left; }
        public double getBottom() { return bottom; }
        public double getRight()  { return right; }
        
        public double getWidth()   { return right - left; }
        public double getHeight()   { return bottom - top; }
        
        public double getCenterX()   { return (right + left)/2; }
        public double getCenterY()   { return (bottom + top)/2; }
        
        
        
        private double top;
        private double left;
        private double right;
        private double bottom;
    }
    
    @FXML protected Canvas canvas;
    
    protected ITagListClusterer m_Clusterer;
    
    protected String[] m_Header;
    
    protected Color    m_DrawingAreaColor;
    protected Color    m_PlotBackgroundColor;
    protected Color    m_PlotBorderColor;
    
    protected String   m_Title;
    
    protected double   m_TitleHeight;
    protected Font     m_TitleFont;
    protected Color    m_TitleColor;
    
    protected double   m_LabelWidth;
    protected double   m_LabelMargin;
    protected Font     m_LabelFont;
    protected Color    m_LabelColor;
    protected Color    m_LabelBackgroundColor;
    
    protected double   m_SeriesHeight;
    
    protected double   m_DendrogramLevelWidth;
    protected Color    m_DendrogramLineColor;
    protected double   m_DendrogramLineWidth;
    protected double   m_DendrogramWidth;
    
    protected double   m_RightMargin;
    
    protected double   m_TickHeight;
    protected double   m_MaxSeries;
    
    protected double   m_YAxisLabelHeight;
    protected double   m_XAxisLabelHeight;
    
    protected String   m_XAxisLabel;
    protected String   m_YAxisLabel;
    
    protected Font     m_XAxisLabelFont;
    protected Color    m_XAxisLabelColor;
    protected Font     m_YAxisLabelFont;
    protected Color    m_YAxisLabelColor;
    
    protected double   m_TickLineWidth;
    protected double   m_TickLineLength;
    protected Color    m_TickLineColor;
    protected Color    m_TickLabelColor;
    protected Font     m_TickLabelFont;
    
    protected double   m_GridHorizontalLineWidth;
    protected Color    m_GridHorizontalLineColor;
    protected double   m_GridVerticalLineWidth;
    protected Color    m_GridVerticalLineColor;
    protected double   m_GridVerticalBoldLineWidth;
    protected Color    m_GridVerticalBoldLineColor;

    protected boolean  m_ShowXTicks;
    protected boolean  m_ShowXTickLabels;
    protected boolean  m_ShowVerticalGridlines;
    protected boolean  m_ShowHorizontalGridlines;
    
    protected Color [] m_HeatColor;
    protected double[] m_HeatValue;
    
    protected int[]    m_tickPositionsX;
    
    protected TreeNode m_dendrogramTreeRoot;
    
    protected boolean  m_isSeriesWise;
    
    protected boolean  m_Clustering;
    
    // without having setter
    private   double   m_ColorSchemeHeight;
    private   double   m_ColorSchemeWidth;
    
/*          m_YAxisLabelHeight                       m_RightMargin
                 ^                  Layout                 ^
               +---+----------------------------------------+
               |   |                  Title                 |   } m_TitleHeight
               |   +-------+----------+-------------------+-+
               |   |       |  Series1 |                   | |
               |   |       |  Series2 |                   | |
               |   |       |  Series3 |                   | |
               |   |       |  Series4 |                   | |
               |   |       |  Series5 |                   | |
               |   +-------+----------+---------------------+
               |   |                                        |   } m_TickHeight
               |   +----------------------------------------+
               |   |                                        |   } m_XAxisLabelHeight
               +---+----------------------------------------+
                  /       //        //
       m_DendrogramWidth //        //
                        //        //
           m_LabelMargin/        /m_LabelMargin
                      m_LabelWidth
           
    */
    
    HashMap<String, List >           m_data;
    HashMap<String,Integer>          m_mapKeyToIdx;
    String[]                         m_mapIdxToKey;
    
    public DoubleProperty minRelativeOffsetProperty;
    public DoubleProperty maxRelativeOffsetProperty;
    
    public Heatmap( ITagListClusterer clusterer ) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Heatmap.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
                
        m_PlotBackgroundColor = Color.BLACK;
        m_PlotBorderColor     = Color.GRAY;
        
        m_Clusterer   = clusterer;
        
        m_Title       = "Title";
        m_TitleHeight = 40;
        m_TitleFont   = new Font("Times New Roman", 15);
        m_TitleColor  = Color.BLACK;
        m_LabelWidth = 150;
        m_LabelMargin= 10;
        m_LabelFont  = new Font("System", 10);
        m_LabelColor = Color.BLACK;
        m_LabelBackgroundColor = Color.WHITE;
        
        m_SeriesHeight         = 32;
        
        m_DendrogramLevelWidth = 10;
        m_DendrogramLineColor  = Color.BLACK;
        m_DendrogramLineWidth  = 1;
        //m_DendrogramWidth      = 100;
       
        m_RightMargin = 32;
        
        m_TickHeight = 32;
        m_MaxSeries  = 50;
        
        m_YAxisLabelHeight = 32;
        m_XAxisLabelHeight = 32;
    
        m_XAxisLabel = "X AXIS";
        m_YAxisLabel = "Y AXIS";
    
        m_XAxisLabelFont  = new Font("System", 10);
        m_XAxisLabelColor = Color.BLACK;
        m_YAxisLabelFont  = new Font("System", 10);
        m_YAxisLabelColor = Color.BLACK;
        
        m_TickLineWidth  = 1;
        m_TickLineLength = 8;
        
        m_data = new HashMap<>();
        
        m_HeatColor = new Color [3];
        m_HeatValue = new double[3];
        
        m_HeatColor[0] = Color.rgb(255,0,0);
        m_HeatColor[1] = Color.rgb(255,255,255);
        m_HeatColor[2] = Color.rgb(0,0,255);
        
        m_HeatValue[0] =  0.0f;
        m_HeatValue[1] =  0.5f;
        m_HeatValue[2] =  1.0f;
        
        m_TickLineLength = 1;
        m_TickLineColor  = Color.GRAY;
        m_TickLabelColor = Color.BLACK;
        m_TickLabelFont  = new Font("System", 10);

        m_GridHorizontalLineWidth    = 1;
        m_GridHorizontalLineColor    = Color.GRAY;
        m_GridVerticalLineWidth      = 1;
        m_GridVerticalLineColor      = Color.GRAY;
        m_GridVerticalBoldLineWidth  = 1;
        m_GridVerticalBoldLineColor  = Color.WHITE;    
        
        m_ShowXTicks  = true;
        m_ShowXTickLabels = true;
        
        m_ShowVerticalGridlines = true;
        m_ShowHorizontalGridlines = true; 
        
        m_isSeriesWise = true;
        
        m_Clustering = true;
        
        m_ColorSchemeHeight = 15;
        m_ColorSchemeWidth  = 100;
        
        minRelativeOffsetProperty = new SimpleDoubleProperty( 0.0 );
        maxRelativeOffsetProperty = new SimpleDoubleProperty( 1.0 );
        
        minRelativeOffsetProperty.addListener( new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue ov, Number t, Number t1) {
                if( t != t1 ) requestLayout();
            }
        });
        maxRelativeOffsetProperty.addListener( new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue ov, Number t, Number t1) {
                if( t != t1 ) requestLayout();
            }
        });
        
        this.prefHeightProperty().addListener( new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if( t != t1 ) requestLayout();
            }
        });
        this.prefWidthProperty().addListener( new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if( t != t1 ) requestLayout();
            }
        });
    }
    
    @Override 
    protected void layoutChildren() {
        super.layoutChildren();
        draw();
    }
   
    public void setHeader(String[] header) {
        m_Header = header;
    }
    
    //Color m_DrawingAreaColor;
    public void setDrawingAreaColor( Color drawingAreaColor ) {
            m_DrawingAreaColor = drawingAreaColor;
    }
    
    //Color m_PlotBackgroundColor;
    public void setPlotBackgroundColor( Color plotBackgroundColor ) {
            m_PlotBackgroundColor = plotBackgroundColor;
    }
    //Color m_PlotBorderColor;
    public void setPlotBorderColor( Color plotBorderColor ) {
            m_PlotBorderColor = plotBorderColor;
    }
    //String m_Title;
    public void setTitle( String title ) {
            m_Title = title;
    }
    //double m_TitleHeight;
    public void setTitleHeight( double titleHeight ) {
            m_TitleHeight = titleHeight;
    }
    //Font m_TitleFont;
    public void setTitleFont( Font titleFont ) {
            m_TitleFont = titleFont;
    }
    //Color m_TitleColor;
    public void setTitleColor( Color titleColor ) {
            m_TitleColor = titleColor;
    }
    //double m_LabelWidth;
    public void setLabelWidth( double labelWidth ) {
            m_LabelWidth = labelWidth;
    }
    //double m_LabelMargin;
    public void setLabelMargin( double labelMargin ) {
            m_LabelMargin = labelMargin;
    }
    //Font m_LabelFont;
    public void setLabelFont( Font labelFont ) {
            m_LabelFont = labelFont;
    }
    //Color m_LabelColor;
    public void setLabelColor( Color labelColor ) {
            m_LabelColor = labelColor;
    }
    //Color m_LabelBackgroundColor;
    public void setLabelBackgroundColor( Color labelbgColor ) {
            m_LabelBackgroundColor = labelbgColor;
    }
    //double m_SeriesHeight;
    public void setSeriesHeight( double seriesHeight ) {
            m_SeriesHeight = seriesHeight;
    }
    //double m_DendrogramLevelWidth;
    public void setDendrogramLevelWidth( double dendrogramLevelWidth ) {
            m_DendrogramLevelWidth = dendrogramLevelWidth;
    }
    //Color m_DendrogramLineColor;
    public void setDendrogramLineColor( Color dendrogramLineColor ) {
            m_DendrogramLineColor = dendrogramLineColor;
    }
    //double m_DendrogramLineWidth;
    public void setDendrogramLineWidth( double dendrogramLineWidth ) {
            m_DendrogramLineWidth = dendrogramLineWidth;
    }
    //double m_DendrogramWidth;
    public void setDendrogramWidth( double dendrogramWidth ) {
            m_DendrogramWidth = dendrogramWidth;
    }
    //double m_RightMargin;
    public void setRightMargin( double rightMargin ) {
            m_RightMargin = rightMargin;
    }
    //double m_TickHeight;
    public void setTickHeight( double tickHeight ) {
            m_TickHeight = tickHeight;
    }
    //double m_MaxSeries;
    public void setMaxSeries( double maxSeries ) {
            m_MaxSeries = maxSeries;
    }
    //double m_YAxisLabelHeight;
    public void setYAxisLabelHeight( double yAxisLabelHeight ) {
            m_YAxisLabelHeight = yAxisLabelHeight;
    }
    //double m_XAxisLabelHeight;
    public void setXAxisLabelHeight( double xAxisLabelHeight ) {
            m_XAxisLabelHeight = xAxisLabelHeight;
    }
    //String m_XAxisLabel;
    public void setXAxisLabel( String xAxisLabel ) {
            m_XAxisLabel = xAxisLabel;
    }
    //String m_YAxisLabel;
    public void setYAxisLabel( String yAxisLabel ) {
            m_YAxisLabel = yAxisLabel;
    }
    //Font m_XAxisLabelFont;
    public void setXAxisLabelFont( Font xAxisLabelFont ) {
            m_XAxisLabelFont = xAxisLabelFont;
    }
    //Color m_XAxisLabelColor;
    public void setXAxisLabelColor( Color xAxisLabelColor ) {
            m_XAxisLabelColor = xAxisLabelColor;
    }
    //Font m_YAxisLabelFont;
    public void setYAxisLabelFont( Font yAxisLabelFont ) {
            m_YAxisLabelFont = yAxisLabelFont;
    }
    //Color m_YAxisLabelColor;
    public void setYAxisLabelColor( Color yAxisLabelColor ) {
            m_YAxisLabelColor = yAxisLabelColor;
    }
    //double m_TickLineWidth;
    public void setTickLineWidth( double tickLineWidth ) {
            m_TickLineWidth = tickLineWidth;
    }
    //double m_TickLineLength;
    public void setTickLineLength( double tickLineLength ) {
            m_TickLineLength = tickLineLength;
    }
    //Color m_TickLineColor;
    public void setTickLineColor( Color tickLineColor ) {
            m_TickLineColor = tickLineColor;
    }
    //Color m_TickLabelColor;
    public void setTickLabelColor( Color tickLabelColor ) {
            m_TickLabelColor = tickLabelColor;
    }
    //Font m_TickLabelFont;
    public void setTickLabelFont( Font font ) {
            m_TickLabelFont = font;
    }
    //double m_GridHorizontalLineWidth;
    public void setGridHorizontalLineWidth( double gridHorizontalLineWidth ) {
            m_GridHorizontalLineWidth = gridHorizontalLineWidth;
    }
    //Color m_GridHorizontalLineColor;
    public void setGridHorizontalLineColor( Color gridHorizontalLineColor ) {
            m_GridHorizontalLineColor = gridHorizontalLineColor;
    }
    //double m_GridVerticalLineWidth;
    public void setGridVerticalLineWidth( double gridVerticalLineWidth ) {
            m_GridVerticalLineWidth = gridVerticalLineWidth;
    }
    //Color m_GridVerticalLineColor;
    public void setGridVerticalLineColor( Color gridVerticalLineColor ) {
            m_GridVerticalLineColor = gridVerticalLineColor;
    }
    //double m_GridVerticalBoldLineWidth;
    public void setGridVerticalBoldLineWidth( double gridVerticalBoldLineWidth ) {
            m_GridVerticalBoldLineWidth = gridVerticalBoldLineWidth;
    }
    //Color m_GridVerticalBoldLineColor;
    public void setGridVerticalBoldLineColor( Color gridVerticalBoldLineColor ) {
            m_GridVerticalBoldLineColor = gridVerticalBoldLineColor;
    }
    //boolean m_ShowXTicks;
    public void setShowXTicks( boolean show ) {
            m_ShowXTicks = show;
    }
    //boolean m_ShowXTickLabels;
    public void setShowXTickLabels( boolean show ) {
            m_ShowXTickLabels = show;
    }
    //boolean  m_ShowHorizontalGridlines;
    public void setShowHorizontalGridlines( boolean show ) {
        m_ShowHorizontalGridlines = show;
    }
    //boolean  m_ShowVerticalGridlines;
    public void setShowVerticalGridlines( boolean show ) {
        m_ShowVerticalGridlines = show;
    }
    //boolean  m_Clustering
    public void setClustering( boolean clustering ) {
        m_Clustering = clustering;
    }

    public void setNumColors( int n ) {
        if( n == 0 ) {
            m_HeatColor = null;
            m_HeatValue = null;
        } else {
            m_HeatColor = new Color [n];
            m_HeatValue = new double[n];   
        }
    }
    
    public boolean setHeatColor( int i, Color c ) {
        if( 0 <= i && i < m_HeatColor.length ) {
            m_HeatColor[i] = c;
            return true;
        } else {
            return false;
        }
    }
    
    public void setHeatValue( int i, double value ) {
        m_HeatValue[i] = value;
    }
    
    public HashMap<String, List > getData() {
        return m_data;
    }
    
    public Series newSeries(String name) {
        if( m_data.containsKey(name) ) return null;
        Series series = new Series();
        m_data.put( name, series );
        return series;
    }

    public int estimateHeight() {
        int h = 0;
        if( m_Title != null && !m_Title.isEmpty() ) {
            h += m_TitleHeight;
        }
        
        if( m_XAxisLabel != null && !m_XAxisLabel.isEmpty() ) {
            h += m_XAxisLabelHeight;
        }
        
        h += m_ColorSchemeHeight;
        
        h += m_SeriesHeight * getNumRows();
        
        if( m_ShowXTickLabels ) {
            h += m_TickHeight;
        }
        if( m_ShowXTicks ) {
            h += m_TickLineLength;
        }
        
        return h;
    }
    
    protected void predraw() {
        int numSeries = (m_data==null)?0:m_data.size() ;
        
        double canvasWidth = this.getPrefWidth() - 50;
        double canvasHeight = this.getPrefHeight() - 50;
        
        canvas.setWidth( canvasWidth );
        canvas.setHeight( canvasHeight );
       
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill( m_DrawingAreaColor );
        gc.fillRect( 0, 0, canvasWidth, canvasHeight );
        
        if( getNumRows() == 1 ) {
            m_Clustering = false;
        }
        
        if( m_Clustering && m_dendrogramTreeRoot == null ) {
            m_mapKeyToIdx = new HashMap<>();
            m_mapIdxToKey = new String[ numSeries ];
        
            String newickString = m_Clusterer.cluster( m_data );
            
            if( newickString != null ) {
                m_dendrogramTreeRoot = parseNewick(newickString);
                m_DendrogramWidth = (m_dendrogramTreeRoot.getDepth()+1) * (m_DendrogramLevelWidth+1);
            } else {
                m_Clustering = false;
            }
        }
    }
    
    protected int map( String key ) {
        if( m_mapKeyToIdx == null ) return 0;
        
        Integer idx = m_mapKeyToIdx.get(key);
        
        if( idx != null ) return idx;
        
        int newIdx = m_mapKeyToIdx.size();
        m_mapKeyToIdx.put(key, newIdx);
        m_mapIdxToKey[newIdx] = key;
        return newIdx;
    }
    
    public void draw() {
        if( m_data == null ) return;
        
        predraw();
        
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        
        Rect canvasRect = new Rect( 0, 0, canvasWidth, canvasHeight );
        
        if( m_Title != null && !m_Title.isEmpty() ) {
            drawTitle( gc, canvasRect.cutTop( m_TitleHeight ) );
        }
        
        if( m_YAxisLabel != null && !m_YAxisLabel.isEmpty() ) {
            drawYAxisLabel( gc, canvasRect.cutLeft(m_YAxisLabelHeight ));
        }
       
        drawHeatmapColorScheme( gc, canvasRect.cutTop( m_ColorSchemeHeight ).throwRight( m_LabelWidth + 2 * m_LabelMargin ).cutRight( m_ColorSchemeWidth ) );
        
        if( m_XAxisLabel != null && !m_XAxisLabel.isEmpty() ) {
            drawXAxisLabel( gc, canvasRect.cutBottom(m_XAxisLabelHeight ));
        }
        
        if( m_Clustering ) {
            int bottomMargin = 0;
            if( m_ShowXTickLabels ) bottomMargin += m_TickHeight;
            if( m_ShowXTicks      ) bottomMargin += m_TickLineLength;
            drawDendrogram( gc, canvasRect.cutLeft( m_DendrogramWidth ).throwBottom( bottomMargin ) );
        } else {
            canvasRect.cutLeft( 2 * m_DendrogramLevelWidth );
        }
        
        drawHeatmap( gc, canvasRect );
    }
    
    public int getNumRows() {
        return m_data.size();
    }
    
    protected double computeSeriesOccupationRatio() {
        //return 1 / (double)m_MaxSeries;
        return 1 / (double)getNumRows();
    }
    
    private void drawTitle( final GraphicsContext gc, final Rect canvasRect ) {
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont( m_TitleFont );
        gc.setFill( m_TitleColor );
        gc.fillText( m_Title, canvasRect.getCenterX(), canvasRect.getCenterY() );
    }
    
    private void drawYAxisLabel( final GraphicsContext gc, final Rect canvasRect ) {
                
        gc.setFont(m_YAxisLabelFont);
        gc.setFill(m_YAxisLabelColor);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.save();
        //gc.translate( m_YAxisLabelHeight / 2, heatmapBottomY / 2);
        gc.translate( canvasRect.getCenterX(), canvasRect.getCenterY() );
        gc.rotate( -90 );
        gc.fillText( m_YAxisLabel, 0, 0 );
        gc.restore();
    }
    
    private void drawXAxisLabel( final GraphicsContext gc, final Rect canvasRect ) {
                
        gc.setFont(m_XAxisLabelFont);
        gc.setFill(m_XAxisLabelColor);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.TOP);
        gc.save();
        gc.translate( canvasRect.getCenterX(), canvasRect.getTop() );
        gc.fillText( m_XAxisLabel, 0, 0 );
        gc.restore();
    }
    
    private void drawDendrogram( final GraphicsContext gc, final Rect canvasRect ) {
        
        gc.setStroke( m_DendrogramLineColor );
        gc.setFill  ( m_DendrogramLineColor );
        gc.setLineWidth( m_DendrogramLineWidth );
        
        drawDendrogramRecursive( gc, m_dendrogramTreeRoot, canvasRect );
        
    }
        
    private double drawDendrogramRecursive( GraphicsContext gc, TreeNode node, final Rect canvasRect ) {
        final double seriesHeight = canvasRect.getHeight() * computeSeriesOccupationRatio();
        
        if( node instanceof TreeLeaf ) {
            return canvasRect.getTop() + seriesHeight *(((TreeLeaf)node).getID()+0.5);
        }
        
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        
        int sum = 0;
        double xNode = canvasRect.getLeft() + m_DendrogramWidth - m_DendrogramLevelWidth * node.getDepth();
        for( TreeNode child : node.getChild() ) {
            double yChild = drawDendrogramRecursive(gc, child, canvasRect);
            double xChild = canvasRect.getRight() - m_DendrogramLevelWidth * child.getDepth();
            
            gc.strokeLine( xChild, yChild, xNode, yChild );
            
            if( yChild < min ) min = yChild;
            if( max < yChild ) max = yChild;
            sum += yChild;
        }
        gc.strokeLine( xNode, min, xNode, max );
        
        return sum / (double)node.getChild().size();
    }
    
    protected void drawHeatmapSingleSeries( final GraphicsContext gc, final Rect canvasRect, Series series ) {
        
        final double left   = canvasRect.getLeft();
        final double top    = canvasRect.getTop();
        final double height = canvasRect.getHeight();
        final double seriesUnitWidth = canvasRect.getWidth() / (double) m_Header.length;
        
        for( int j = 0; j < m_Header.length; ++j ) {
                if( series.size() <= j ) break;
                
                gc.setFill  ( getHeatmapColor( series.get(j) ) );
                gc.setStroke( getHeatmapColor( series.get(j) ) );
                gc.strokeRect( seriesUnitWidth*j + left, top+1, seriesUnitWidth, height-2 );
                gc.fillRect  ( seriesUnitWidth*j + left, top+1, seriesUnitWidth, height-2 );
            }
    }
    
    protected void drawHeatmapSingleSeriesLabel( final GraphicsContext gc, final Rect canvasRect, String label ) {
        gc.setFill( m_LabelColor );
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText( label, canvasRect.getLeft() + m_LabelMargin, canvasRect.getCenterY() );
    }
    
    protected void computeXTickPositions( int n ) {
        int i;
        int iPercentStart = 0;
        for( i = 0 ; i < m_Header.length-1; ++i ) {
            if( m_Header[i].charAt( m_Header[i].length() - 1 ) == '%' ) {
                iPercentStart = i;
                break;
            }
        }
        
        int iPercentEnd   = m_Header.length-1;
        for( ; i < m_Header.length-1; ++i ) {
            if( m_Header[i].charAt( m_Header[i].length() - 1 ) != '%' ) {
                iPercentEnd = i;
                break;
            }
        }
        
        if( n == 0 ) {
            m_tickPositionsX = new int[2];
            m_tickPositionsX[0] = iPercentStart;
            m_tickPositionsX[1] = iPercentEnd-1;
            return;
        }
        
        final int stepRear    = iPercentStart / n;
        final int stepPercent = (iPercentEnd - iPercentStart )/n;
        final int stepFront   = (m_Header.length - iPercentEnd) / n;
        
        int iTick = 0;
        m_tickPositionsX = new int[ 3 * n  + 1];
        
        for( i = stepRear; i <= iPercentStart-stepRear+1; i += stepRear ) {
            m_tickPositionsX[iTick++] = i;
        }

        for( i = iPercentStart; i <= iPercentEnd-stepPercent+1; i += stepPercent ) {
            m_tickPositionsX[iTick++] = i;
        }

        for( i = iPercentEnd-1; i <= m_Header.length-stepFront+1; i += stepFront ) {
            m_tickPositionsX[iTick++] = i;
        }
        
        m_tickPositionsX[iTick] = m_Header.length - 1;
        
    }
    
    protected abstract void drawHeatmap( final GraphicsContext gc, final Rect canvasRect );
    
    private void drawHeatmapColorScheme( final GraphicsContext gc, final Rect canvasRect ) {
        
        double min = m_HeatValue[0];
        double max = m_HeatValue[ m_HeatValue.length - 1 ];
        
        Rect rectSpectrum = canvasRect.throwBottom(5.0);
        double w = rectSpectrum.getWidth();
        for( double x = 0 ; x <= w-1; x+=1.0 ) {
            double v = min + x*(max-min)/w;
            gc.setFill( getHeatmapColor( v ) );
            gc.fillRect( x + rectSpectrum.getLeft(), rectSpectrum.getTop(), 1, rectSpectrum.getHeight() );
        }
        
        gc.setStroke( Color.BLACK );
        gc.strokeRect( rectSpectrum.getLeft(), rectSpectrum.getTop(), rectSpectrum.getWidth(), rectSpectrum.getHeight());

        
        gc.setFill( Color.BLACK );
        gc.setFont( new Font( m_LabelFont.getName(), canvasRect.getHeight() - 1));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText( String.format("%.2f ", m_HeatValue[0] ), canvasRect.getLeft(), canvasRect.getCenterY());
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText( String.format(" %.2f", m_HeatValue[ m_HeatValue.length - 1 ] ), canvasRect.getRight(), canvasRect.getCenterY() );
    }
    
    protected void drawHeatmapTicks( final GraphicsContext gc, final Rect canvasRect ) {
        final double seriesUnitWidth = canvasRect.getWidth() / (double) m_Header.length;
        
        gc.setLineWidth( m_TickLineWidth );
        gc.setStroke( m_TickLineColor );

        for( int i = 0; i < m_tickPositionsX.length; ++i ) {
            double x = canvasRect.getLeft() + seriesUnitWidth * m_tickPositionsX[i];
            gc.strokeLine( x, canvasRect.getTop(), x, canvasRect.getBottom());
        }
    }
    
    protected void drawHeatmapTickLabels( final GraphicsContext gc, final Rect canvasRect, boolean showLeftmostHeader, boolean showRightmostHeader ) {
        final double seriesUnitWidth = canvasRect.getWidth() / (double) m_Header.length;
        
        gc.setFont( m_TickLabelFont );
        gc.setFill( m_TickLabelColor );
        gc.setTextBaseline(VPos.TOP);
        gc.setTextAlign(TextAlignment.CENTER);
        
        if( showLeftmostHeader ) {
            gc.fillText( getTickLabelString( 0 ), canvasRect.getLeft(), canvasRect.getTop());
        }
        
        if( showRightmostHeader ) { 
            gc.fillText( getTickLabelString( m_Header.length - 1 ), canvasRect.getRight(), canvasRect.getTop());
        }
        

        for( int i = 0; i < m_tickPositionsX.length; ++i ) {
            double x = canvasRect.getLeft() + seriesUnitWidth * m_tickPositionsX[i];
            gc.fillText( getTickLabelString( m_tickPositionsX[i] ), x, canvasRect.getTop());
        }
    }
    
    private String getTickLabelString( int i ) {
        if (i == m_Header.length) i--; // to show the last label
        return (i >= m_Header.length) ? "" : 
            !m_Header[i].endsWith("%") ? m_Header[i] :
            String.format("%.0f%%", new Float(m_Header[i].substring(0, m_Header[i].length()-1)));
    }
    
    protected void drawVerticalHeatmapGrid( final GraphicsContext gc, final Rect canvasRect ) {
        final double seriesUnitWidth = canvasRect.getWidth() / (double) m_Header.length;
        gc.setLineWidth ( m_GridVerticalLineWidth );
        gc.setStroke    ( m_GridVerticalLineColor );
        
        for( int i = 0; i < m_tickPositionsX.length; ++i ) {
            double x = canvasRect.getLeft() + seriesUnitWidth * m_tickPositionsX[i];
            gc.strokeLine( x, canvasRect.getTop(), x, canvasRect.getBottom());
        }
    }
    
    protected void drawHorizontalHeatmapGrid( final GraphicsContext gc, final Rect canvasRect ) {
        gc.setLineWidth ( m_GridHorizontalLineWidth );
        gc.setStroke    ( m_GridHorizontalLineColor );

        final double seriesHeight = canvasRect.getHeight() * computeSeriesOccupationRatio();

        for( int i = 1; i < m_data.size(); ++i ) {
            gc.setLineWidth(m_GridHorizontalLineWidth);
            gc.setStroke   (m_GridHorizontalLineColor);
            gc.strokeLine  (canvasRect.getLeft(),
                            canvasRect.getTop() + i*seriesHeight,
                            canvasRect.getRight(),
                            canvasRect.getTop() + i*seriesHeight);
        }
    }
    
    protected Color getHeatmapColor(double rawval) {
        if( m_HeatColor.length == 0 ) {
            return m_DrawingAreaColor;
        }
        
        final double minVal = m_HeatValue[ 0 ];
        final double maxVal = m_HeatValue[ m_HeatValue.length - 1 ];
        
        if( minVal == maxVal ) return m_HeatColor[0];
        
        final double minRelativeOffset = minRelativeOffsetProperty.doubleValue();
        final double maxRelativeOffset = maxRelativeOffsetProperty.doubleValue();
        
        //double val = ( rawval - minVal ) * ( maxRelativeOffset - minRelativeOffset ) + minRelativeOffset * ( maxVal - minVal ) + minVal;
        double val = ( rawval - minRelativeOffset * ( maxVal - minVal ) - minVal ) / ( maxRelativeOffset - minRelativeOffset ) + minVal;
        
        if( val < minVal ) {
            return m_HeatColor[0];
        } else if( maxVal <= val  ) {
            return m_HeatColor[m_HeatValue.length - 1];
        } else {
            int lower = 0;
            int upper = m_HeatValue.length;
            int iColor;
            while( true ) {
                iColor = (lower + upper) / 2;
                if( val < m_HeatValue[iColor] ) upper = iColor;
                else if( m_HeatValue[iColor+1] <= val ) lower = iColor + 1;
                else break;
            }
            double d = (val-m_HeatValue[iColor])/(m_HeatValue[iColor+1]-m_HeatValue[iColor]);
            return m_HeatColor[iColor].interpolate( m_HeatColor[iColor+1], d );
        }
    }
    
    private TreeNode parseNewick( String nw ) {
        int depth = 0;
        int i = 0, j, k;
        int leafid;

        Stack< TreeNode > stack = new Stack<>();
        String x;
        TreeNode u;

        while( i < nw.length() ) {
            switch( nw.charAt(i) ) {
                case '(':
                    stack.push( new TreeNode() );
                    depth++;
                    if( nw.charAt(i+1) != '(' ) {
                            j = nw.indexOf( ',', i );
                            x = nw.substring( i+1,j );
                            x = x.substring( 0, x.lastIndexOf( ':' ) );
                            
                            leafid = map( x );
                            stack.push( new TreeLeaf( leafid ) );
                            i = j-1;
                    }
                    break;
                case ')':
                    depth--;

                    u = stack.pop();
                    stack.peek().addChild( u );

                    if( depth < 0 ) {
                            return null;
                    }
                    break;
                case ',':
                    u = stack.pop();
                    stack.peek().addChild( u );
                    if( nw.charAt(i+1) != '(' ) {
                            j = nw.indexOf( ')', i+1 );
                            k = nw.indexOf( ',', i+1 );
                                                        
                            j = (k<j && k>0)?k:j;
                            
                            x = nw.substring( i+1,j );
                            x = x.substring( 0, x.lastIndexOf( ':' ) );

                            leafid = map( x );
                            stack.push( new TreeLeaf( leafid ) );						
                            i = j-1;
                    }
                    break;
                default:
                    ;
            }
            ++i;
        }
        if( depth > 0 ) {
            return null;
        }

        u = stack.pop();
        
        return u;
    }
}