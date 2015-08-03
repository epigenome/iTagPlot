/*
 *  UI List object (sample, feature or group)
 */
package Objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import tagviz.TagViz;

public class TableObject {

    private final SimpleBooleanProperty value;
    private final SimpleStringProperty name;
    private final TagViz app;

    public TableObject(Boolean value, String name, TagViz app) {
        this.value = new SimpleBooleanProperty(value);
        this.name = new SimpleStringProperty(name);
        this.app = app;

        this.value.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if (getValue().equals(false)) {
                    getApp().selectedItems.remove(getName());
                } else {
                    getApp().selectedItems.add(getName());
                }
            }
        });
    }

    public TagViz getApp() {
        return app;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String Name) {
        name.set(Name);
    }

    public Boolean getValue() {
        return value.get();
    }

    public void setValue(Boolean val) {
        value.set(val);
    }

    public StringProperty NameProperty() {
        return name;
    }

    public BooleanProperty ValueProperty() {
        return value;
    }
}
