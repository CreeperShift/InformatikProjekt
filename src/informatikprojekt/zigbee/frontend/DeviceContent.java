package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.backend.Data;
import informatikprojekt.zigbee.backend.DataManager;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceContent extends AnchorPane {
    @FXML
    private Label labelDeviceID;
    @FXML
    private VBox contentBox;

    private int id;

    public DeviceContent(int id) {
        this.id = id;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/device.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        labelDeviceID.setText("Device " + id);

        Timer deviceTimer = new Timer();
        CommonUtils.registerTimer(deviceTimer);
        deviceTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (DataManager.get().isConnected()) {
                        if (!ControllerBase.getDataList().isEmpty()) {
                            contentBox.getChildren().clear();
                            contentBox.getChildren().add(labelDeviceID);
                            for (Data data : ControllerBase.getDataList()) {
                                if (data.Device() == id) {
                                    Label label = new Label();
                                    label.setFont(new Font("System", 14));
                                    label.setText(data.dataType() + " : " + data.value());
                                    contentBox.getChildren().add(label);
                                }
                            }

                        }
                    }
                });

            }
        }, 0, 5000);

    }

}
