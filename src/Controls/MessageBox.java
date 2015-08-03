/*
 *  TagViz
 *  2014
 */

package Controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author jechoi
 */
public class MessageBox {

    public static void show(final Stage stg, final String title, final String text) {
	MessageBox.show(stg, title, text, false);
    }

    public static void confirm(final Stage stg, final String title, final String text) {
	MessageBox.show(stg, title, text, true);
    }
    
    public static void show(final Stage stg, final String title, final String text, boolean confirm) {
	final Stage dialog = new Stage();
	dialog.setTitle(title);
	dialog.setResizable(false);
	dialog.initOwner(stg);
	dialog.initModality(Modality.APPLICATION_MODAL);

	FlowPane buttons = new FlowPane(10,10);
	buttons.setAlignment(Pos.CENTER);
	Button yes = new Button("Yes");
	yes.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent event) {
		dialog.close();
	    }
	});
		
	if (confirm) {
	    Button no = new Button("No");
	    no.setOnAction(new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
		    dialog.close();
		}
	    });
	    buttons.getChildren().addAll(yes, no);
	} else {
	    buttons.getChildren().addAll(yes);
	}
	
	VBox box = new VBox();
	box.setPadding(new Insets(10));
	box.setAlignment(Pos.CENTER);
	box.setSpacing(10);
	box.getChildren().addAll(new Label(text), buttons);
	Scene s = new Scene(box);
	dialog.setScene(s);
	dialog.show();
    }    
}
