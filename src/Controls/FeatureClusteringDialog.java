/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Controls;

import Forms.FeatureClusteringFormController;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author SHKim12
 */
public class FeatureClusteringDialog {
    private static final String fxml = "/Forms/FeatureClusteringForm.fxml";
    private FeatureClusteringFormController controller;
    
    public FeatureClusteringDialog(Stage owner) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxml));
            AnchorPane page = (AnchorPane) fxmlLoader.load();

	    final Stage dialog = new Stage();
	    controller = fxmlLoader.getController();
	    dialog.initModality(Modality.WINDOW_MODAL);
	    dialog.initOwner(owner);
            dialog.setScene(new Scene(page));
            dialog.setTitle("Clustered Feature Dialog");
            dialog.setResizable(false);
	    dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
		public void handle(WindowEvent e) {
		    dialog.close();
		}
	    });
            dialog.showAndWait();
        } catch (IOException ex) {
            MessageBox.show(null, "Error", "loding error of " + fxml);
        }
    }
    
    public int getNumClusters() {
        return controller.getNumClusters();
    }
    
    public String getDistanceMetric() {
        return controller.getDistanceMetric();
    }
    
    public boolean getSampleInARow() {
        return controller.getSampleInARow();
    }
    
    public String getPrefix() {
        return controller.getPrefix();
    }
    
    public boolean isOK() {
        return controller.isOK();
    }
    
    public String getSaveFile() {
        return controller.getSaveFile();
    }
}
