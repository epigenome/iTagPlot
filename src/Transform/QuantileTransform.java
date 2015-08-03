/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Transform;

import Objects.TagList;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author SHKim12
 */
public class QuantileTransform implements Transform {
    public QuantileTransform( TagList l ) {
        if( l.size() == 0 ) m_QuantileVector = null;

        ArrayList<Float> totalList = new ArrayList<>();
        totalList.addAll(l);

        Collections.sort(totalList);

        int qvSize = QUANTILE_RESOLUTION<totalList.size()?QUANTILE_RESOLUTION:totalList.size();

        m_QuantileVector = new ArrayList<>();

        for( int i = 0; i < qvSize; ++i ) {
            m_QuantileVector.add( totalList.get( i * totalList.size() / qvSize ) );
        }
    }

    @Override
    public float transform( float v ) {
        int s = 0;
        int e = m_QuantileVector.size();
        int m = s;
        while( s < e - 1) {
            m = (s+e)/2;
            Float mv = m_QuantileVector.get(m);
            if( mv < v ) s = m;
            else if( v < mv ) e = m;
            else break;
        }

        return m / (float)(m_QuantileVector.size() - 1);
    }

    public final static int QUANTILE_RESOLUTION = 1000;
    ArrayList<Float> m_QuantileVector;
}
