package informatikprojekt.zigbee.frontend;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerGraph implements Initializable {
    public VBox box;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        box.getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }
}
