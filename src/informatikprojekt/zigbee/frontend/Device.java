package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.util.CommonUtils;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

public class Device {
    private static int deviceIDs = 1;
    private final int id;
    private final Text text;
    private final Circle circle;
    private final PopOver popOver;
    private boolean isRender = false;


    public Device(double x, double y) {
        this(deviceIDs, x, y);
        deviceIDs++;
    }

    public Device(int id, double x, double y) {
        this.id = id;
        popOver = new PopOver();
        CommonUtils.addPopOver(popOver);
        DeviceContent deviceContent = new DeviceContent(id);
        popOver.setContentNode(deviceContent);
        popOver.setDetachable(false);
        popOver.setAnimated(true);
        popOver.setMaxHeight(100);
        popOver.setMaxWidth(70);
        popOver.setX(x);
        popOver.setY(y);
        circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(18);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        text = new Text(id + "");
        text.setFont(new Font("Courier", 18));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setX(x - 5);
        text.setY(y + 6);
        text.setMouseTransparent(true);

        circle.setOnMouseClicked(event -> {
            if (isRender) {
                if (popOver.isShowing()) {
                    popOver.hide(Duration.ZERO);

                } else {
                    popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
                    popOver.show(ControllerRoomView.INSTANCE.drawingArea, event.getX() + 440, event.getY() + 88);
                }
            }

        });


    }

    public void setRender() {
        isRender = true;
    }

    public void addTo(AnchorPane anchorPane) {

        if (!anchorPane.getChildren().contains(circle) && !anchorPane.getChildren().contains(text)) {
            anchorPane.getChildren().add(circle);
            anchorPane.getChildren().add(text);
        }

    }

    public Circle getCircle() {
        return circle;
    }

    public Text getText() {
        return text;
    }

    public int getID() {
        return id;
    }

}
