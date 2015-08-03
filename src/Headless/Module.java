/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Headless;

/**
 *
 * @author SHKim12
 */
public interface Module {
    void    showOptions();
    boolean applyOptions( Options options );
    boolean checkParameters();
    void    run();
}
