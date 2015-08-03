/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Transform;

/**
 *
 * @author SHKim12
 */
public class LogTransform implements Transform {
    public LogTransform( float c ) {
        m_Constant = c;
    }

    @Override
    public float transform( float v ) {
        return (float)Math.log10(v+m_Constant);
    }
    float m_Constant;
}
