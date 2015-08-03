/*
 *  TagViz
 *  2014
 */

package Controls;

import Forms.IDConventionFormController;
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
public class IDConventionDialog {
    
    private static String fxml = "/Forms/IDConventionForm.fxml";
    private IDConventionFormController controller;
    
    public IDConventionDialog(Stage owner, String samFeat, String grpFeat) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxml));
            AnchorPane page = (AnchorPane) fxmlLoader.load();

	    final Stage dialog = new Stage();
	    controller = fxmlLoader.getController();
	    controller.initLabels(samFeat, grpFeat);
	    dialog.initModality(Modality.WINDOW_MODAL);
	    dialog.initOwner(owner);
            dialog.setScene(new Scene(page));
            dialog.setTitle("ID Convention Dialog");
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

    public IDConventionFormController getController() {
	return controller;
    }

    public void setController(IDConventionFormController controller) {
	this.controller = controller;
    }
}
