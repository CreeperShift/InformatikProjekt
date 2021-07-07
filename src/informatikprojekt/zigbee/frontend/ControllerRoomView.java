package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.backend.Room;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ControllerRoomView implements Initializable {
    public AnchorPane drawingArea;
    public Label noRoomLabel;
    private Room room;
    public static ControllerRoomView INSTANCE;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        drawingArea.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        INSTANCE = this;
    }

    public void addRoom(Room room){
        this.room = room;
        drawingArea.getChildren().clear();
        loadGraph();
    }

    private void loadGraph(){
        for(Circle c : room.getRoomGraph().getCircles()){

            for(Map.Entry<Circle, Line> entry : room.getRoomGraph().getAdj(c).entrySet()){
                Line l = entry.getValue();
                if(!drawingArea.getChildren().contains(l)){
                    drawingArea.getChildren().add(l);
                }
            }

        }
    }

}
