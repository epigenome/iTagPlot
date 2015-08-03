/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Heatmap;

import Clustering.ITagListClusterer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author SHKim12
 */
public class SamplewiseHeatmap extends Heatmap {

    private ArrayList<String> m_samples     = null;
    private ArrayList<String> m_features    = null;
    private boolean         m_containsAllFeatures = false;

    public SamplewiseHeatmap(ITagListClusterer clusterer) {
        super(clusterer);
    }

    private void preprocessing() {
        HashSet<String> samples = new HashSet<String>();
        HashSet<String> features = new HashSet<String>();
        
        m_samples     = new ArrayList<>();
        m_features    = new ArrayList<>();
        m_containsAllFeatures = false;

        for( String key : m_data.keySet() ) {
            int i = key.indexOf(':');
            if( i > 0 ) {
                samples.add( key.substring(0, i) );
                features.add( key.substring( i+1 ) );
            } else {
                samples.add( key );
                m_containsAllFeatures = true;
            }
        }
        
        for( String sample : samples ) {
            m_samples.add(sample);
        }
        Collections.sort( m_samples );
        
        for( String feature: features ) {
            m_features.add(feature);
        }
        Collections.sort( m_features );
        
        if( !m_Clustering  &&  m_mapKeyToIdx == null ) {
            m_mapKeyToIdx = new HashMap<>();
            m_mapIdxToKey = new String[ getNumRows() ];
            
            int j = 0;
            
            for( String sample : m_samples ) {
                m_mapKeyToIdx.put( sample, j );
                m_mapIdxToKey[j] = sample;
                j++;
            }
        }
        
        if( m_tickPositionsX == null ) {
            computeXTickPositions(0);
        }
    }
    
    @Override
    protected void predraw() {
        super.predraw();
        preprocessing();
        
    }
    
    @Override
    protected void drawHeatmap(GraphicsContext gc, Rect canvasRect) {
        drawSamplewiseHeatmap( gc, canvasRect );
    }
    
    @Override
    public int getNumRows() {
        if( m_samples == null ) preprocessing();
        return m_samples.size();
    }
    
    private String getKeyALL() {
        String keyAll = "ALL";
        while( m_features.contains( keyAll ) ) {
            keyAll = "_" + keyAll;
        }
        return keyAll;
    }
    
    private void drawSingleHeatmapCell(  final GraphicsContext gc, final Rect rectPlot, final Series series ) {
        drawHeatmapSingleSeries( gc, rectPlot, series );
                
        if( m_ShowVerticalGridlines ) {
            drawVerticalHeatmapGrid ( gc, rectPlot );
        }

        // draw plot border
        gc.setStroke( m_PlotBorderColor );
        gc.strokeRect( rectPlot.getLeft(), rectPlot.getTop(), rectPlot.getWidth(), rectPlot.getHeight() );
    }
    
    private void drawSamplewiseHeatmap( final GraphicsContext gc, final Rect canvasRect ) {
        
        // draw series labels
        Rect rectLabel  = canvasRect.cutRight( m_LabelWidth + 2* m_LabelMargin );
        
        gc.setStroke ( m_LabelBackgroundColor );
        gc.setFill   ( m_LabelBackgroundColor );
        gc.fillRect  ( rectLabel.getLeft(), rectLabel.getTop(), rectLabel.getWidth(), rectLabel.getHeight() );
        gc.strokeRect( rectLabel.getLeft(), rectLabel.getTop(), rectLabel.getWidth(), rectLabel.getHeight() );
        
        Rect rectTickLabels = null;
        Rect rectTicks = null; 
        if( m_ShowXTickLabels ) {
            rectTickLabels = canvasRect.cutBottom( m_TickHeight );
        }
        if( m_ShowXTicks ) {
            rectTicks = canvasRect.cutBottom( m_TickLineLength );
        }
        
        gc.setFont( m_LabelFont );
        gc.setFill( m_PlotBackgroundColor );
        gc.fillRect( canvasRect.getLeft(), canvasRect.getTop(), canvasRect.getWidth(), canvasRect.getHeight() );
        
        int    numFeatures  = m_features.size();
        numFeatures += ( m_containsAllFeatures )? 1 : 0;
        double widthFeature = canvasRect.getWidth() / (double) numFeatures;
        
        final double seriesHeight = canvasRect.getHeight() * computeSeriesOccupationRatio() ;
                
        // draw tick lines
        if( m_ShowXTicks ) {
            gc.setStroke( m_TickLineColor );
            gc.setLineWidth( m_TickLineWidth );
            
            for( int i = 0; i < numFeatures; ++i ) {   
                gc.setStroke( m_TickLineColor );
                gc.setLineWidth( m_TickLineWidth );
                Rect rectCurrentTick = rectTicks.cutLeft( widthFeature );
                
                if( i > 0 ) {
                    // do not draw first tick line
                    gc.strokeLine( rectCurrentTick.getLeft(),rectCurrentTick.getTop(), rectCurrentTick.getLeft(), rectCurrentTick.getBottom());
                }
                
                if( m_ShowXTickLabels ) {
                    drawHeatmapTickLabels(gc, rectCurrentTick, i == 0, i == numFeatures - 1);
                }
            }
            // do not draw last tick line
            //gc.strokeLine( rectTicks.getLeft(),rectTicks.getTop(), rectTicks.getLeft(), rectTicks.getBottom());
        }
        
        // draw tick labels
        if( m_ShowXTickLabels && rectTickLabels != null ) {
            gc.setFill( m_TickLabelColor );
            gc.setFont( m_TickLabelFont );
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            
            if( m_containsAllFeatures ) {
                Rect rectCurrentLabel = rectTickLabels.cutLeft( widthFeature );
                gc.fillText( "ALL", rectCurrentLabel.getCenterX(), rectCurrentLabel.getCenterY() );
            }
            
            for( String keyFeature : m_features ) {   
                Rect rectCurrentLabel = rectTickLabels.cutLeft( widthFeature );
                gc.fillText( keyFeature, rectCurrentLabel.getCenterX(), rectCurrentLabel.getCenterY() );
            }
        }
        
        // draw series labels and heatmap plot 
        for( String keySample : m_samples ) {
            int i = map( keySample );
            Rect rectRow = new Rect( canvasRect.getLeft(), canvasRect.getTop() + i * seriesHeight, canvasRect.getRight(), canvasRect.getTop() + (i+1)*seriesHeight );
            drawHeatmapSingleSeriesLabel(gc, new Rect( rectLabel.getLeft(), rectLabel.getTop() + i*seriesHeight, rectLabel.getRight(), rectLabel.getTop() + (i+1)*seriesHeight ), keySample );

            if( m_containsAllFeatures ) {
                Rect rectPlot   = rectRow.cutLeft(widthFeature);
                String key = keySample;
                Series series = (Series)m_data.get(key);
                
                drawSingleHeatmapCell( gc, rectPlot, series );
            }
            
            for( String keyFeature : m_features ) {
                Rect rectPlot   = rectRow.cutLeft(widthFeature);
                String key = keySample + ":" + keyFeature;
                Series series = (Series)m_data.get(key);
                
                drawSingleHeatmapCell( gc, rectPlot, series );
            }
        }
    }
}
