/*
 *  TagViz
 *  2014
 */
package Objects;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class ComboBoxMenuItem extends CustomMenuItem {
    
    final ComboBox comboBox;
    final Label label;
    
    public ComboBoxMenuItem(String tlabel, String[] values) {
        this.label = new Label(tlabel);
        this.comboBox = new ComboBox(FXCollections.observableArrayList(values));
        label.setPrefWidth(100);
        label.setMinWidth(100);
        label.setMaxWidth(Double.MAX_VALUE);
        comboBox.setPrefWidth(Math.max(100, comboBox.getPrefWidth()));
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setFocusTraversable(false);
        
        final BorderPane pane = new BorderPane();
        pane.setLeft(label);
        pane.setCenter(comboBox);
        pane.setPadding(new Insets(0, 2, 0, 2));
        pane.getStyleClass().add("container");
        pane.setMaxWidth(Double.MAX_VALUE);
        
        setContent(pane);
        setHideOnClick(false);
        getStyleClass().add("combobox-menu-item");
    }
    
    public String getValue() {
        return comboBox.getValue().toString();
    }
    
    public void setValue(String s) {
        comboBox.setValue(s);
    }
    
    public ComboBox getComboBox() {
        return comboBox;
    }
}
