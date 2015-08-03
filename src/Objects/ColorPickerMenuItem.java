/*
 *  TagViz
 *  2014
 */
package Objects;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public final class ColorPickerMenuItem extends CustomMenuItem {

    BorderPane pane;
    Rectangle rect;
    Boolean isOpen;
    final AnchorPane ColorsBase;
    final Label label;
    Label colorLabel;
    Color color;

    public ColorPickerMenuItem(String tlabel, Color defcolor) {
        pane = new BorderPane();
        colorLabel = new Label();
        ColorsBase = new AnchorPane();
        rect = new Rectangle();
        color = defcolor;
        isOpen = false;

        pane.setPadding(new Insets(0, 2, 0, 2));
        pane.getStyleClass().add("container");
        pane.setMaxWidth(Double.MAX_VALUE);
        rect.setWidth(20);
        rect.setHeight(18);
        rect.setFill(defcolor);
        colorLabel.setPrefWidth(70);
        setLabel(defcolor);
        ColorsBase.setVisible(false);

        pane.setBottom(ColorsBase);
        pane.setCenter(rect);
        pane.setRight(colorLabel);

        EventHandler toggle = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (!isOpen) {
                    LoadColors();
                    ColorsBase.setPadding(new Insets(5, 0, 5, 0));
                    ColorsBase.setVisible(true);
                    FadeTransition ft = new FadeTransition(Duration.millis(300), ColorsBase);
                    ft.setFromValue(0.0);
                    ft.setToValue(1.0);
                    ft.setCycleCount(1);
                    ft.setAutoReverse(false);
                    ft.play();
                } else {
                    ColorsBase.setPadding(new Insets(0));
                    ColorsBase.setVisible(false);
                    ColorsBase.getChildren().clear();
                }
                isOpen = !isOpen;
            }
        };
        rect.setOnMouseClicked(toggle);
        colorLabel.setOnMouseClicked(toggle);

        label = new Label(tlabel);
        label.setPrefWidth(100);
        label.setMinWidth(100);
        label.setMaxWidth(Double.MAX_VALUE);
        pane.setLeft(label);

        setContent(pane);
        setHideOnClick(false);
        getStyleClass().add("colorpicker-menu-item");
        colorLabel.setStyle("-fx-cursor: hand;");
        rect.setStyle("-fx-cursor: hand;-fx-stroke: rgba(0,0,0,0.5); -fx-stroke-width: 1px; -fx-stroke-type: inside;");
    }

    public Color getValue() {
        return color;
    }

    public Rectangle getPrompt() {
        return rect;
    }

    private void setLabel(Color c) {
        colorLabel.setText(String.format("  %02X%02X%02X", (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255), (int) (c.getBlue() * 255)));
    }

    private void LoadColors() {
        String[] colors = {
            "#000000", "#330000", "#330c00", "#331800", "#332500",
            "#333100", "#293300", "#1d3300", "#103300", "#043300",
            "#003308", "#003314", "#003321", "#00332d", "#002d33",
            "#002133", "#001433", "#000833", "#040033", "#100033",
            "#1d0033", "#290033", "#330031", "#330025", "#330018",
            "#202020", "#660000", "#661800", "#663100", "#664900",
            "#666200", "#526600", "#396600", "#216600", "#086600",
            "#006610", "#006629", "#006641", "#00665a", "#005a66",
            "#004166", "#002966", "#001066", "#080066", "#210066",
            "#390066", "#520066", "#660062", "#660049", "#660031",
            "#404040", "#990000", "#992500", "#994900", "#996e00",
            "#999300", "#7a9900", "#569900", "#319900", "#0c9900",
            "#009918", "#00993d", "#009962", "#009987", "#008799",
            "#006299", "#003d99", "#001899", "#0c0099", "#310099",
            "#560099", "#7a0099", "#990093", "#99006e", "#990049",
            "#606060", "#cc0000", "#cc3100", "#cc6200", "#cc9300",
            "#ccc400", "#a3cc00", "#72cc00", "#41cc00", "#10cc00",
            "#00cc21", "#00cc52", "#00cc83", "#00ccb4", "#00b4cc",
            "#0083cc", "#0052cc", "#0021cc", "#1000cc", "#4100cc",
            "#7200cc", "#a300cc", "#cc00c4", "#cc0093", "#cc0062",
            "#808080", "#FF0000", "#ff3d00", "#ff7a00", "#ffb800",
            "#fff500", "#ccff00", "#8fff00", "#52ff00", "#14ff00",
            "#00ff29", "#00ff66", "#00ffa3", "#00ffe0", "#00e0ff",
            "#00a3ff", "#0066ff", "#0029ff", "#1400ff", "#5200ff",
            "#8f00ff", "#cc00ff", "#ff00f5", "#ff00b8", "#ff007a",
            "#9f9f9f", "#ff3333", "#ff6433", "#ff9533", "#ffc633",
            "#fff733", "#d6ff33", "#a5ff33", "#74ff33", "#43ff33",
            "#33ff54", "#33ff85", "#33ffb6", "#33ffe7", "#33e7ff",
            "#33b6ff", "#3385ff", "#3354ff", "#4333ff", "#7433ff",
            "#a533ff", "#d633ff", "#ff33f7", "#ff33c6", "#ff3395",
            "#bfbfbf", "#ff6666", "#ff8b66", "#ffaf66", "#ffd466",
            "#fff966", "#e0ff66", "#bcff66", "#97ff66", "#72ff66",
            "#66ff7e", "#66ffa3", "#66ffc8", "#66ffed", "#66edff",
            "#66c8ff", "#66a3ff", "#667eff", "#7266ff", "#9766ff",
            "#bc66ff", "#e066ff", "#ff66f9", "#ff66d4", "#ff66af",
            "#dfdfdf", "#ff9999", "#ffb199", "#ffca99", "#ffe299",
            "#fffb99", "#ebff99", "#d2ff99", "#baff99", "#a1ff99",
            "#99ffa9", "#99ffc2", "#99ffda", "#99fff3", "#99f3ff",
            "#99daff", "#99c2ff", "#99a9ff", "#a199ff", "#ba99ff",
            "#d299ff", "#eb99ff", "#ff99fb", "#ff99e2", "#ff99ca",
            "#ffffff", "#ffcccc", "#ffd8cc", "#ffe4cc", "#fff1cc",
            "#fffdcc", "#f5ffcc", "#e9ffcc", "#dcffcc", "#d0ffcc",
            "#ccffd4", "#ccffe0", "#ccffed", "#ccfff9", "#ccf9ff",
            "#ccedff", "#cce0ff", "#ccd4ff", "#d0ccff", "#dcccff",
            "#e9ccff", "#f5ccff", "#ffccfd", "#ffccf1", "#ffcce4"
        };

        for (int i = 0; i < colors.length; i++) {
            final Rectangle r = new Rectangle();
            r.setFill(Color.web(colors[i]));
            r.setWidth(8);
            r.setHeight(8);
            r.setLayoutX(i % 25 * r.getWidth());
            r.setLayoutY(8 + Math.floor(i / 25) * r.getHeight());
            r.setStyle("-fx-cursor:hand;");

            r.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    color = (Color) r.getFill();
                    rect.setFill(r.getFill());
                    setLabel(color);
                }
            });

            ColorsBase.getChildren().add(r);
        }
        ColorsBase.setPrefSize(140, AnchorPane.USE_COMPUTED_SIZE);
        ColorsBase.setMinSize(140, AnchorPane.USE_COMPUTED_SIZE);
        ColorsBase.setMaxSize(140, AnchorPane.USE_COMPUTED_SIZE);
    }
}
