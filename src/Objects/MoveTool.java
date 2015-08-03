/*
 *  Move tool
 */
package Objects;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import tagviz.MainFormController;

public class MoveTool extends Tool {

    public MoveTool(MainFormController ctrller) {
        super(ctrller);
    }

    @Override
    public void start() {
        final Delta offset = new Delta();
        mouse_press = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                /*
                dragDelta.x = chart.getLayoutX() - mouseEvent.getX();
                dragDelta.y = chart.getLayoutY() - mouseEvent.getY();
                chart.setCursor(Cursor.MOVE);
                */
                
                offset.x = mouseEvent.getX();
                offset.y = mouseEvent.getY();
                
                controller.setOnplotCursor(Cursor.MOVE);
            }
        };
        mouse_release = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                controller.setOnplotCursor(Cursor.HAND);
            }
        };
        mouse_drag = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //chart.setLayoutX(mouseEvent.getX() + dragDelta.x);
                //chart.setLayoutY(mouseEvent.getY() + dragDelta.y);
                controller.scroll(offset.x - mouseEvent.getX(), offset.y - mouseEvent.getY() );
                
                offset.x = mouseEvent.getX();
                offset.y = mouseEvent.getY();
            }
        };
        mouse_enter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                controller.setOnplotCursor(Cursor.HAND);
            }
        };
        mouse_move = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.consume();
            }
        };
        Assign();
    }
/*
    private void ClampGraph( double cx, double cy ) {
        if (chart != null) {
            double x = (GraphPane.getWidth() - chart.getWidth());
            double y = (GraphPane.getHeight() - chart.getHeight());

            if (cx > 0) {
                cx = 0;
            }
            if (cx < x) {
                cx = x;
            }
            if (cy > 0) {
                cy = 0;
            }
            if (cy < y) {
                cy = y;
            }
            
            chart.setLayoutX( cx );
            chart.setLayoutX( cy );
        }
    }*/
}
