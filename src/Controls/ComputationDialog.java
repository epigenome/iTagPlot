/*
 *  TagViz
 *  2014
 */

package Controls;

import Forms.ComputationFormController;
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
 * @author jechoi
 */
public class ComputationDialog {
    
    private static String fxml = "/Forms/ComputationForm.fxml";
    private ComputationFormController controller;
    
    public ComputationDialog(Stage owner) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxml));
            AnchorPane page = (AnchorPane) fxmlLoader.load();

	    final Stage dialog = new Stage();
	    controller = fxmlLoader.getController();
	    controller.setStage(dialog);
	    dialog.initModality(Modality.WINDOW_MODAL);
	    dialog.initOwner(owner);
            dialog.setScene(new Scene(page));
            dialog.setTitle("Computation Dialog");
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

    public ComputationFormController getController() {
	return controller;
    }

    public void setController(ComputationFormController controller) {
	this.controller = controller;
    }
}
