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
public class IdentityTransform implements Transform {
    @Override 
    public float transform( float v ) {
        return v;
    }
}
