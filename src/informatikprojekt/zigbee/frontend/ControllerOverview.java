package informatikprojekt.zigbee.frontend;

import javafx.fxml.Initializable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerOverview implements Initializable {
    public Circle ledRED;
    public Circle ledORANGE;
    public Circle ledGREEN;

    Paint redColor;
    Paint orangeColor;
    Paint greenColor;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        redColor = ledRED.getFill();
        orangeColor = ledORANGE.getFill();
        greenColor = ledGREEN.getFill();

        ledRED.setFill(Color.GRAY);
        ledORANGE.setFill(Color.GRAY);

    }
}
