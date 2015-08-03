/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Clustering;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author SHKim12
 */
public interface IArffExporter {
    public File getArff(HashMap<String, List> data);
}
