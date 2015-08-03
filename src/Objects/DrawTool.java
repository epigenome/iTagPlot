/*
 *  Draw tool
 */
package Objects;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import tagviz.MainFormController;

public class DrawTool extends Tool {

    final Color color;
    final int width;

    public DrawTool(MainFormController ctrler, Color color, int width) {
        super(ctrler);
        this.color = color;
        this.width = width;
    }

    @Override
    public void start() {
        mouse_press = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                controller.setOnplotCursor(Cursor.DEFAULT);
                final Path path = new Path();
                path.setStrokeWidth(width);
                path.setStroke(color);

                path.getElements().add(new MoveTo(
                            mouseEvent.getX(),
                            mouseEvent.getY()
                ));
                
                controller.addChildEntity(path);
                /*
                GraphPane.getChildren().add(path);
                
                chart.layoutXProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue ov, Object t, Object t1) {
                        //Debug.println( "MOVE", "" + t1 + ">>" + t);
                        path.setLayoutX(path.getLayoutX() + ((double) t1 - (double) t));
                    }
                });
                chart.layoutYProperty().addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue ov, Object t, Object t1) {
                        path.setLayoutY(path.getLayoutY() + ((double) t1 - (double) t));
                    }
                });*/
                
                path.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            controller.removeChildEntity(path);
                        }
                    }
                });
            }
        };
        mouse_drag = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                /*
                ((Path) GraphPane.getChildren().get(GraphPane.getChildren().size() - 1))
                        .getElements().add(new LineTo(
                            mouseEvent.getX(),
                            mouseEvent.getY()
                        ));
                */
                controller.appendToLastPath(new LineTo(
                            mouseEvent.getX(),
                            mouseEvent.getY()
                        ) );
            }
        };

        mouse_release = mouse_enter = mouse_move = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.consume();
            }
        };
        Assign();
    }
}
