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
public class FeaturewiseHeatmap extends Heatmap {

    private ArrayList<String> m_samples     = null;
    private ArrayList<String> m_features    = null;
    private boolean         m_containsAllFeatures = false;

    public FeaturewiseHeatmap(ITagListClusterer clusterer) {
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
            if( m_containsAllFeatures ) {
                String keyAll = getKeyALL();
                m_mapKeyToIdx.put( keyAll, j );
                m_mapIdxToKey[j] = keyAll;
                j++;
            }
            
            for( String feature : m_features ) {
                m_mapKeyToIdx.put( feature, j );
                m_mapIdxToKey[j] = feature;
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
        drawFeaturewiseHeatmap( gc, canvasRect );
    }
    
    @Override
    public int getNumRows() {
        if( m_features == null ) preprocessing();
        return m_features.size() + (m_containsAllFeatures?1:0);
    }
    
    private String getKeyALL() {
        String keyAll = "ALL";
        while( m_features.contains( keyAll ) ) {
            keyAll = "_" + keyAll;
        }
        return keyAll;
    }
    
    private void drawFeaturewiseHeatmap( final GraphicsContext gc, final Rect canvasRect ) {
        
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
        
        int    numSamples  = m_samples.size();
        double widthSample = canvasRect.getWidth() / (double) numSamples;
        
        //final double seriesHeight = canvasRect.getHeight() / ( numSamples + 1 );
        final double seriesHeight = canvasRect.getHeight() * computeSeriesOccupationRatio() ;
        
        //Rect rectSampleLabel = canvasRect.cutTop( seriesHeight );

        if( m_containsAllFeatures ) {
            String keyAll = getKeyALL();
            
            int i = map( keyAll );
            Rect rectRow = new Rect( canvasRect.getLeft(), canvasRect.getTop() + i * seriesHeight, canvasRect.getRight(), canvasRect.getTop() + (i+1)*seriesHeight );
            drawHeatmapSingleSeriesLabel(gc, new Rect( rectLabel.getLeft(), rectLabel.getTop() + i*seriesHeight, rectLabel.getRight(), rectLabel.getTop() + (i+1)*seriesHeight ), keyAll );
        
            for( String keySample : m_samples ) {
                Series series = (Series)m_data.get(keySample);

                Rect rectPlot   = rectRow.cutLeft(widthSample);
                drawHeatmapSingleSeries( gc, rectPlot, series );
                
                // draw vertical gridline
                if( m_ShowVerticalGridlines ) {
                    drawVerticalHeatmapGrid ( gc, rectPlot );
                }

                // draw plot border
                gc.setStroke( m_PlotBorderColor );
                gc.strokeRect( rectPlot.getLeft(), rectPlot.getTop(), rectPlot.getWidth(), rectPlot.getHeight() );
            }
        }
          
        // draw tick lines
        if( m_ShowXTicks ) {
            gc.setStroke( m_TickLineColor );
            gc.setLineWidth( m_TickLineWidth );
            for( int i = 0; i < m_samples.size(); ++i ) {   
                gc.setStroke( m_TickLineColor );
                gc.setLineWidth( m_TickLineWidth );
                Rect rectCurrentTick = rectTicks.cutLeft( widthSample );
                
                if( i > 0 ) {
                    // do not draw first tick line
                    gc.strokeLine( rectCurrentTick.getLeft(),rectCurrentTick.getTop(), rectCurrentTick.getLeft(), rectCurrentTick.getBottom());
                }
                
                if( m_ShowXTickLabels ) {
                    drawHeatmapTickLabels(gc, rectCurrentTick, i == 0, i == m_samples.size() - 1);
                }
            }
            // do not draw last tick line
            //gc.strokeLine( rectTicks.getLeft(),rectTicks.getTop(), rectTicks.getLeft(), rectTicks.getBottom());
        }
        
        // draw tick labels
        if( m_ShowXTickLabels ) {
            gc.setFill( m_TickLabelColor );
            gc.setFont( m_TickLabelFont );
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            for( String keySample : m_samples ) {   
                Rect rectCurrentLabel = rectTickLabels.cutLeft( widthSample );
                gc.fillText( keySample, rectCurrentLabel.getCenterX(), rectCurrentLabel.getCenterY() );
            }
        }
            
        // draw series labels and heatmap plot 
        for( String keyFeature : m_features ) {
            int i = map( keyFeature );
            Rect rectRow = new Rect( canvasRect.getLeft(), canvasRect.getTop() + i * seriesHeight, canvasRect.getRight(), canvasRect.getTop() + (i+1)*seriesHeight );
            drawHeatmapSingleSeriesLabel(gc, new Rect( rectLabel.getLeft(), rectLabel.getTop() + i*seriesHeight, rectLabel.getRight(), rectLabel.getTop() + (i+1)*seriesHeight ), keyFeature );

            for( String keySample : m_samples ) {
                Rect rectPlot   = rectRow.cutLeft(widthSample);
                String key = keySample + ":" + keyFeature;
                Series series = (Series)m_data.get(key);
                
                
                drawHeatmapSingleSeries( gc, rectPlot, series );
                //drawHeatmapSingleSeriesLabel(gc, new Rect( rectLabel.getLeft(), rectLabel.getTop() + i*seriesHeight, rectLabel.getRight(), rectLabel.getTop() + (i+1)*seriesHeight ), key );
            
                // draw vertical gridline
                
                if( m_ShowVerticalGridlines ) {
                    drawVerticalHeatmapGrid ( gc, rectPlot );
                }
                
                // draw plot border
                gc.setStroke( m_PlotBorderColor );
                gc.strokeRect( rectPlot.getLeft(), rectPlot.getTop(), rectPlot.getWidth(), rectPlot.getHeight() );
                
            }
        }
        
    }
}
