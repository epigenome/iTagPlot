/*
 *  Tool base class
 */
package Objects;

import javafx.event.EventHandler;
import tagviz.MainFormController;

public abstract class Tool {

    //Chart chart;
    //AnchorPane GraphPane;
    MainFormController controller;
    EventHandler mouse_press;
    EventHandler mouse_release;
    EventHandler mouse_drag;
    EventHandler mouse_enter;
    EventHandler mouse_move;

    protected class Delta {
        double x, y;
    }

    public Tool(MainFormController ctrler) {
        controller = ctrler;
    }

    public abstract void start();

    protected void Assign() {
        controller.setMouseEventHandlerForTools( mouse_press, mouse_release, mouse_drag, mouse_enter, mouse_move );
    }

    public void ClearDraws() {
        controller.ClearDraws();
    }

    public void ClearWrites() {
        controller.ClearWrites();
    }
}
