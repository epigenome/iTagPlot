/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clustering;

/**
 *
 * @author SHKim12
 */
public class WekaSeriesWiseHierarchicalClustererWrapper extends WekaHierarchicalClustererWrapper {

    public WekaSeriesWiseHierarchicalClustererWrapper(String linkType, String distanceFunction) {
        super(linkType, distanceFunction, new SeriesWiseArffExporter());
    }
}
