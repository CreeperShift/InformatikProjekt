package informatikprojekt.zigbee.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DeviceContent extends AnchorPane {
    @FXML
    private Label labelDeviceID;

    public DeviceContent(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/device.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        labelDeviceID.setText("Device " + id);
    }

}
