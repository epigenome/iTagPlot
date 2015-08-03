/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Clustering;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author SHKim12
 */
public interface ITagListClusterer {
    // input : a set of list
    // output: newick string
     public String cluster( HashMap<String, List > data );
     
     public String getLastErrorMessage();
}
