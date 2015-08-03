/*
 *  TagViz
 *  2014
 */

package Controls;

import Objects.TableObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author jechoi
 */
public class TableHeaderCheckBox extends CheckBox {
    private TableColumn col;
    private TableView table;
    

    public TableHeaderCheckBox(TableView table, TableColumn col) {
        super("A");
        this.table = table;
        this.col = col;

        selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                for (Object item : getTable().getItems())
                    ((TableObject) item).setValue(t1);
            }
        });
    }

    public TableColumn getCol() {
        return col;
    }

    public void setCol(TableColumn col) {
        this.col = col;
    }

    public TableView getTable() {
        return table;
    }

    public void setTable(TableView table) {
        this.table = table;
    }
}
