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
import java.util.List;
import java.util.Map;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author SHKim12
 */
public class SerieswiseHeatmap extends Heatmap {

    public SerieswiseHeatmap(ITagListClusterer clusterer) {
        super(clusterer);
    }
    
    private void preprocessing() {
        if( !m_Clustering &&  m_mapKeyToIdx == null ) {
            m_mapKeyToIdx = new HashMap<>();
            m_mapIdxToKey = new String[ getNumRows() ];
            
            ArrayList<String> keyList = new ArrayList<>();
            for( String key : m_data.keySet() ) {
                keyList.add( key );
            }
            Collections.sort( keyList );
            
            int j = 0;
            for( String key : keyList ) {
                m_mapKeyToIdx.put( key, j );
                m_mapIdxToKey[j] = key;
                j++;
            }
        }
        
        if( m_tickPositionsX == null ) {
            computeXTickPositions(5);
        }
    }
    
    @Override
    protected void predraw() {
        super.predraw();
        preprocessing();
        
    }
    
    @Override
    protected void drawHeatmap(GraphicsContext gc, Rect canvasRect) {
        drawSerieswiseHeatmap( gc, canvasRect );
    }
    
    private void drawSerieswiseHeatmap( final GraphicsContext gc, final Heatmap.Rect canvasRect ) {
        
        // draw series labels
        Heatmap.Rect rectLabel  = canvasRect.cutRight( m_LabelWidth + 2* m_LabelMargin );
        
        gc.setStroke ( m_LabelBackgroundColor );
        gc.setFill   ( m_LabelBackgroundColor );
        gc.fillRect  ( rectLabel.getLeft(), rectLabel.getTop(), rectLabel.getWidth(), rectLabel.getHeight() );
        gc.strokeRect( rectLabel.getLeft(), rectLabel.getTop(), rectLabel.getWidth(), rectLabel.getHeight() );
        
        
        Heatmap.Rect rectPlot   = canvasRect;
        
        if( m_ShowXTickLabels ) {
            drawHeatmapTickLabels( gc, rectPlot.cutBottom( m_TickHeight ), false, false );
        }
        if( m_ShowXTicks ) {
            drawHeatmapTicks( gc, rectPlot.cutBottom( m_TickLineLength ) );
        }
        
        final double seriesHeight = rectPlot.getHeight() * computeSeriesOccupationRatio() ;
        
        gc.setFont( m_LabelFont );
        gc.setFill( m_PlotBackgroundColor );
        gc.fillRect( rectPlot.getLeft(), rectPlot.getTop(), rectPlot.getWidth(), rectPlot.getHeight() );
        
        // draw series labels and heatmap plot 
        for( Map.Entry<String,List> entry : m_data.entrySet() ) {
            String key    = entry.getKey();
            Series series = (Series)entry.getValue();
            int i = map( key );
            
            drawHeatmapSingleSeries( gc, new Heatmap.Rect( rectPlot.getLeft(), rectPlot.getTop() + i*seriesHeight, rectPlot.getRight(), rectPlot.getTop() + (i+1)*seriesHeight ), series );
            drawHeatmapSingleSeriesLabel(gc, new Heatmap.Rect( rectLabel.getLeft(), rectLabel.getTop() + i*seriesHeight, rectLabel.getRight(), rectLabel.getTop() + (i+1)*seriesHeight ), key );
        }
        
        // draw gridlines
        if( m_ShowVerticalGridlines   ) drawVerticalHeatmapGrid ( gc, rectPlot );
        if( m_ShowHorizontalGridlines ) drawHorizontalHeatmapGrid ( gc, rectPlot );
        
        // draw plot border
        gc.setStroke( m_PlotBorderColor );
        gc.strokeRect( rectPlot.getLeft(), rectPlot.getTop(), rectPlot.getWidth(), rectPlot.getHeight() );
        
    }
    
}
