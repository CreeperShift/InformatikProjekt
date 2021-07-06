package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.util.LineGraph;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerRoomView implements Initializable {
    public AnchorPane drawingArea;
    private LineGraph lineGraph = new LineGraph();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        drawingArea.getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }

}
