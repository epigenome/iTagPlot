/*
 *  TagViz
 *  2014
 */

package Controls;

import Forms.QuantityFormController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
public class QuantityDialog {
    
    public static enum Type { MICROARRAY, RNASEQ, BETA, QUANTILE };
    private static String fxml = "/Forms/QuantityForm.fxml";
    private QuantityFormController controller;
    Data data;
    
    public QuantityDialog(Stage owner, Type type) {
	this(owner, type, null, null);
    }
    
    public QuantityDialog(Stage owner, Type type, File path, Collection loadedSamples) {
	data = new Data(type);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxml));
            AnchorPane page = (AnchorPane) fxmlLoader.load();

	    final Stage dialog = new Stage();

	    controller = fxmlLoader.getController();
	    controller.initialize(dialog, data, path, loadedSamples);

	    dialog.initModality(Modality.WINDOW_MODAL);
	    dialog.initOwner(owner);
            dialog.setScene(new Scene(page));
            dialog.setTitle(String.format("Quantity Group Dialog based on %s", data.name));
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

    public QuantityFormController getController() {
	return controller;
    }

    public void setController(QuantityFormController controller) {
	this.controller = controller;
    }
    
    public class Data {
	public String name;
	public String[] prefix;
	public String[] cutoffs;

	
	public Data(Type type) {
	    switch (type) {
		case MICROARRAY:
		    name = "Microarray";
		    prefix = new String[] { "LowExp", "MidExp", "HighExp" };
		    cutoffs = new String[] { "MIN", "7", "9", "MAX" };
		    break;
		case RNASEQ:
		    name = "RNA-seq";
		    prefix = new String[] { "LowRNA", "MidRNA", "HighRNA", "SupRNA" };
		    cutoffs = new String[] { "0", "1", "5", "20", "MAX" };
		    break;
		case BETA:
		    name = "Beta values";
		    prefix = new String[] { "FullHypo", "PartHypo", "PartHyper", "FullHyper" };
		    cutoffs = new String[] { "0", "0.3", "0.5", "0.7", "1" };
		    break;
		case QUANTILE:
		    name = "Quantile";
		    prefix = new String[] { "Quartile1", "Quartile2", "Quartile3", "Quartile4" };
		    cutoffs = new String[] { "0", "0.25", "0.5", "0.75", "1" };
		    break;
	    }
	}
    }
    
    public HashMap<String, ArrayList<String>> getMap() {
        return controller.getMap();
    }
}
