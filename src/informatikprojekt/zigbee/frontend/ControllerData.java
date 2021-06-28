package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.backend.SensorData;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerData implements Initializable {

    public TableView table;
    public static ControllerData INSTANCE = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        TableColumn<SensorData, String> columnTime = new TableColumn<>("Time");
        columnTime.setCellValueFactory(new PropertyValueFactory<>("formattedTime"));
        table.getColumns().add(columnTime);
        INSTANCE = this;
    }

    public void setupData() {

        if (ControllerBase.INSTANCE.isConnected()) {
            table.getItems().add(new SensorData("test", 0, 0, 0, 0));
            table.getItems().add(new SensorData("test2", 0, 0, 0, 0));
            table.getItems().add(new SensorData("test3", 0, 0, 0, 0));
            System.out.println("test");

        }


    }
}
