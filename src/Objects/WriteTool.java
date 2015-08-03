/*
 *  Write tool
 */
package Objects;

import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import tagviz.MainFormController;

public class WriteTool extends Tool {

    final String family;
    final Double size;

    public WriteTool(MainFormController ctrler, String family, Double size) {
        super(ctrler);
        this.family = family;
        this.size = size;
    }

    @Override
    public void start() {

        mouse_press = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                final AnchorPane a = new AnchorPane();
                a.getStyleClass().add("custom-base");
                a.setLayoutX(mouseEvent.getX() - 20);
                a.setLayoutY(mouseEvent.getY() - 15);
                a.setPrefSize(AnchorPane.USE_COMPUTED_SIZE, AnchorPane.USE_COMPUTED_SIZE);
                a.setMinWidth(220d);
                MovableObject(a);

                final TextField t = new TextField();
                // javafx 8 t.setFont(Font.font(family, size));
                t.setStyle("-fx-font-family: " + family + "; -fx-font-size: " + size + ";");
                t.getStyleClass().add("custom-text-field");
                t.setPromptText("Enter text");
                t.setPrefSize(200d, TextField.USE_COMPUTED_SIZE);
                t.setMinWidth(200d);
                t.setLayoutX(10);
                t.setLayoutY(10);
                t.requestFocus();
                
                t.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        t.setPrefWidth(new TextUtils().computeTextWidth(Font.font(family, size), newValue, 0.0D) + 80);
                    }
                });

                a.getChildren().add(t);
                AnchorPane.setLeftAnchor(t, 5d);
                AnchorPane.setRightAnchor(t, 5d);
                AnchorPane.setTopAnchor(t, 5d);
                AnchorPane.setBottomAnchor(t, 5d);
                
                //GraphPane.getChildren().add(GraphPane.getChildren().size(), a);
                controller.addChildEntity( a );
                

                t.setSkin(new TextFieldSkin(t, new TextFieldBehavior(t) {
                     @Override
                    public void mouseReleased(MouseEvent e) {
                     if (e.getButton() == MouseButton.SECONDARY) {
                      return; // don't allow context menu to show 
                     }
                     super.mouseReleased(e);
                    }
                }));
                
                t.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            controller.removeChildEntity(a);
                        }
                        mouseEvent.consume();
                    }
                });
                a.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        controller.requestFocusToPlot();
                        mouseEvent.consume();
                    }
                });
                
                
            }
        };
        mouse_move = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                controller.setOnplotCursor(Cursor.TEXT);
                mouseEvent.consume();
            }
        };
        mouse_release = mouse_drag = mouse_enter = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.consume();
            }
        };
        Assign();
    }

    private void MovableObject(final Node n) {
        if (n != null) {
            final Delta dragDelta = new Delta();
            final Delta origin = new Delta();
            EventHandler press = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    origin.x = n.getLayoutX();
                    origin.y = n.getLayoutY();
                    dragDelta.x = mouseEvent.getSceneX();
                    dragDelta.y = mouseEvent.getSceneY();
                    n.setCursor(Cursor.MOVE);
                    
                    mouseEvent.consume();
                }
            };
            EventHandler release = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    n.setCursor(Cursor.MOVE);
                }
            };
            EventHandler drag = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    n.setLayoutX(origin.x + mouseEvent.getSceneX() - dragDelta.x);
                    n.setLayoutY(origin.y + mouseEvent.getSceneY() - dragDelta.y);
                            
                }
            };
            EventHandler enter = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    n.setCursor(Cursor.MOVE);
                }
            };
            n.setOnMousePressed(press);
            n.setOnMouseReleased(release);
            n.setOnMouseDragged(drag);
            n.setOnMouseEntered(enter);
        }
    }

    public class TextUtils {

        final Text helper = new Text();
        final double DEFAULT_WRAPPING_WIDTH = helper.getWrappingWidth();
        //javafx 8 final double DEFAULT_LINE_SPACING = helper.getLineSpacing();
        final String DEFAULT_TEXT = helper.getText();
        final TextBoundsType DEFAULT_BOUNDS_TYPE = helper.getBoundsType();

        public double computeTextWidth(Font font, String text, double help0) {
            helper.setText(text);
            helper.setFont(font);

            helper.setWrappingWidth(0.0D);
            //javafx 8 helper.setLineSpacing(0.0D);
            double d = Math.min(helper.prefWidth(-1.0D), help0);
            helper.setWrappingWidth((int) Math.ceil(d));
            d = Math.ceil(helper.getLayoutBounds().getWidth());

            helper.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
            //javafx 8 helper.setLineSpacing(DEFAULT_LINE_SPACING);
            helper.setText(DEFAULT_TEXT);
            return d;
        }
    }
}
