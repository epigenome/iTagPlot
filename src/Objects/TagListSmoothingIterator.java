/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

/**
 *
 * @author SHKim12
 */
public class TagListSmoothingIterator {

    TagList m_tagList;
    int     m_smoothingBin;
    int     m_upperBound;
    float   m_sum;
    int     m_cnt;
    int     m_i;
    
    public TagListSmoothingIterator(TagList d, int smoothingBin, int upperBound ) {
        m_tagList      = d;
        m_smoothingBin = smoothingBin;
        m_upperBound   = upperBound;
        m_sum          = 0.0f;
        m_cnt          = 0;
        m_i            = 0;
        
        for (int i = 0; i <= smoothingBin; i++) {
            if (d.get(i) == TagList.NA) continue;
            m_sum += d.get(i);
            m_cnt++;
        }
    }

    public boolean end() {
        return m_i < m_upperBound;
    }

    public void next() {
        m_i++;
        if (m_i > m_smoothingBin && m_tagList.get(m_i-1) != TagList.NA) {
            m_sum -= m_tagList.get(m_i-1);
            m_cnt--;
        }
        if (m_i+m_smoothingBin< m_upperBound && m_tagList.get(m_i+m_smoothingBin) != TagList.NA) {
            m_sum += m_tagList.get(m_i+m_smoothingBin);
            m_cnt++;
        }
    }
    
    public int getCurrentIndex() {
        return m_i;
    }
    
    public Float getCurrentValue() {
        return (m_cnt>0)?m_sum/m_cnt:Float.NaN;
    }
    
}
