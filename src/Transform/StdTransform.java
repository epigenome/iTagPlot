/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Transform;

import Objects.TagList;

/**
 *
 * @author SHKim12
 */
public class StdTransform implements Transform {
    public StdTransform( TagList l ) {
        double sum = 0.0;
        double sqsum = 0.0;
        long   cnt = 0;

        int size = l.size();
        for( int i = 0; i < size; ++i ) {
            float v = l.get(i);
            sum += v;
            sqsum += v*v;
            ++cnt;
        }

        m_Mean = (float)( sum / cnt );
        m_Stddev = (float)Math.sqrt( sqsum / cnt - m_Mean * m_Mean );
        if( m_Stddev == 0 ) m_Stddev = 1;
    }

    @Override
    public float transform( float v ) {
        return (v-m_Mean)/m_Stddev;
    }
    float m_Mean;
    float m_Stddev;
}
