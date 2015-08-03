/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Forms;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tagviz.TagViz;

/**
 * FXML Controller class
 *
 * @author SHKim12
 */
public class FeatureClusteringFormController implements Initializable {

    private final static String fileExtention = ".clf";
    
    private boolean ok;
    
    @FXML
    private TextField txtNumClusters;
    @FXML
    private ComboBox  cmbDistance;
    @FXML
    private CheckBox  chkSamplewise;
    @FXML
    private TextField txtPrefix;
    @FXML
    private TextField txtSaveFile;
        
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ok = false;
        txtNumClusters .setText    ( Integer.toString( TagViz.clusteredFeatureParameter.cfNumClusters ) );
        cmbDistance    .setValue   ( TagViz.clusteredFeatureParameter.cfDistance );
        chkSamplewise.setSelected  (!TagViz.clusteredFeatureParameter.cfSampleInARow );
        txtPrefix      .setText    ( TagViz.clusteredFeatureParameter.cfPrefix );
    }    
    
    private boolean validate() {
        try {
            int n = Integer.parseInt( txtNumClusters.getText() );
            if( n <= 0 ) return false;
        } catch( NumberFormatException ex ) {
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void okAction(ActionEvent event) {
	if (!validate()) return;
	ok = true;
        
        TagViz.clusteredFeatureParameter.cfNumClusters  = getNumClusters();
        TagViz.clusteredFeatureParameter.cfDistance     = getDistanceMetric();
        TagViz.clusteredFeatureParameter.cfSampleInARow = getSampleInARow();
        TagViz.clusteredFeatureParameter.cfPrefix       = getPrefix();
        
	((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    private void cancelAction(ActionEvent event) {
	ok = false;
  	((Node)(event.getSource())).getScene().getWindow().hide();
    }
    
    @FXML
    private void openFileDialog(ActionEvent event) {
	FileChooser fileChooser = new FileChooser();
        File currentFile = new File( txtSaveFile.getText() );
        File dir = currentFile.getName().isEmpty()?TagViz.visDirectory:currentFile.getParentFile();
	fileChooser.setInitialDirectory(dir);
        fileChooser.setInitialFileName( currentFile.getName() );
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("iTagPlot Clustered Features", "*" + fileExtention));
        
        fileChooser.setTitle("Save Clustering Result");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            if (!file.getName().endsWith(fileExtention)) {
                file = new File(file.getAbsolutePath() + fileExtention);
            }
            txtSaveFile.setText( file.getAbsolutePath() );
        } else {
            txtSaveFile.setText( "" );
        }
    }
    
    public int getNumClusters() {
        return Integer.parseInt( txtNumClusters.getText() );
    }
    
    public String getDistanceMetric() {
        return (String)cmbDistance.getValue();
    }
    
    public boolean getSampleInARow() {
        return !chkSamplewise.isSelected();
    }
    
    public String getPrefix() {
        return txtPrefix.getText();
    }
    
    public boolean isOK() {
        return ok;
    }
    
    public String getSaveFile() {
        return txtSaveFile.getText().isEmpty()?null:txtSaveFile.getText().trim();
    }
}
