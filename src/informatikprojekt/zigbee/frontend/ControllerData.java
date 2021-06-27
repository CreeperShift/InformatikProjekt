package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.backend.DataManager;
import informatikprojekt.zigbee.backend.DataSet;
import informatikprojekt.zigbee.backend.SensorData;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class ControllerData implements Initializable {

    public TableView<SensorData> table;
    public static ControllerData INSTANCE = null;
    public MenuButton btnDevice;
    public MenuButton btnSensor;
    public MenuButton btnType;

    private final List<String> sensorFilterList = new ArrayList<>();
    private final List<Integer> deviceIDFilterList = new ArrayList<>();
    private final List<String> dataTypeFilterList = new ArrayList<>();
    private boolean setupDone = false;
    public HBox menubar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        setupColumns();
        INSTANCE = this;
    }

    private void setupColumns() {
        TableColumn<SensorData, String> columnTime = new TableColumn<>("Time");
        columnTime.setCellValueFactory(new PropertyValueFactory<>("formattedTime"));

        TableColumn<SensorData, Integer> columnDeviceID = new TableColumn<>("Device");
        columnDeviceID.setCellValueFactory(new PropertyValueFactory<>("deviceID"));

        TableColumn<SensorData, String> columnSensorID = new TableColumn<>("Sensor");
        columnSensorID.setCellValueFactory(new PropertyValueFactory<>("sensorName"));

        TableColumn<SensorData, String> columnDataType = new TableColumn<>("Datentyp");
        columnDataType.setCellValueFactory(new PropertyValueFactory<>("dataType"));

        TableColumn<SensorData, Float> columnData = new TableColumn<>("Wert");
        columnData.setCellValueFactory(new PropertyValueFactory<>("data"));


        table.getColumns().add(columnTime);
        table.getColumns().add(columnDeviceID);
        table.getColumns().add(columnSensorID);
        table.getColumns().add(columnDataType);
        table.getColumns().add(columnData);
    }

    public void setupData() {
        if (!setupDone) {

            Timer updateData = new Timer();
            CommonUtils.registerTimer(updateData);
            updateData.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() ->
                            {
                                if (ControllerBase.INSTANCE.isConnected()) {
                                    for (DataSet s : DataManager.get().getDataForTime(LocalDateTime.now().minusSeconds(4))) {
                                        for (SensorData d : s.getSensorData()) {
                                            if (!isFilteredOut(d)) {
                                                table.getItems().add(d);
                                            }
                                        }

                                    }
                                }
                            }
                    );

                }
            }, 100, 3000);
            setupDone = true;
        }
    }

    private boolean isFilteredOut(SensorData data) {
        for (String filter : dataTypeFilterList) {
            if (data.getDataType().equalsIgnoreCase(filter)) {
                return true;
            }
        }
        for (String sensor : sensorFilterList) {
            if (data.getSensorName().equalsIgnoreCase(sensor)) {
                return true;
            }
        }

        for (int id : deviceIDFilterList) {
            if (data.getDeviceID() == id) {
                return true;
            }
        }
        return false;
    }

    private void filterAllExisting() {
        List<SensorData> toRemove = new LinkedList<>();
        for (SensorData data : table.getItems()) {
            if (isFilteredOut(data)) {
                toRemove.add(data);
            }
        }
        table.getItems().removeAll(toRemove);
    }


    public void onBtnFilter(ActionEvent actionEvent) {

    }


    public void onDevice1(ActionEvent actionEvent) {
        addOrRemoveDevice(1);
        filterAllExisting();
    }

    public void onTypeParticle(ActionEvent actionEvent) {
        addOrRemoveDataType("Partikel");
        filterAllExisting();
    }

    public void onDevice2(ActionEvent actionEvent) {
        addOrRemoveDevice(2);
        filterAllExisting();
    }

    public void onDevice3(ActionEvent actionEvent) {
        addOrRemoveDevice(3);
        filterAllExisting();
    }

    public void onDevice4(ActionEvent actionEvent) {
        addOrRemoveDevice(4);
        filterAllExisting();
    }

    public void onSensorSHT21(ActionEvent actionEvent) {
        addOrRemoveSensor("SHT21");
        filterAllExisting();
    }

    public void onSensorCCS(ActionEvent actionEvent) {
        addOrRemoveSensor("CCS");
        filterAllExisting();
    }

    public void onSensorSCD30(ActionEvent actionEvent) {
        addOrRemoveSensor("SCD30");
        filterAllExisting();
    }

    public void onSensorSCD41(ActionEvent actionEvent) {
        addOrRemoveSensor("SCD41");
        filterAllExisting();
    }

    public void onTypeTemp(ActionEvent actionEvent) {
        addOrRemoveDataType("Temperatur");
        filterAllExisting();

    }

    public void onTypeHum(ActionEvent actionEvent) {
        addOrRemoveDataType("Feuchtigkeit");
        filterAllExisting();
    }

    public void onTypeCO2(ActionEvent actionEvent) {
        addOrRemoveDataType("CO2");
        filterAllExisting();
    }

    public void onTypeVOC(ActionEvent actionEvent) {
        addOrRemoveDataType("VoC");
        filterAllExisting();
    }


    private void addOrRemoveDevice(int dev) {
        if (deviceIDFilterList.contains(dev)) {
            deviceIDFilterList.remove(dev);
        } else {
            deviceIDFilterList.add(dev);
        }
    }

    private void addOrRemoveSensor(String sensor) {
        if (sensorFilterList.contains(sensor)) {
            sensorFilterList.remove(sensor);
        } else {
            sensorFilterList.add(sensor);
        }
    }

    private void addOrRemoveDataType(String type) {
        if (dataTypeFilterList.contains(type)) {
            dataTypeFilterList.remove(type);
        } else {
            dataTypeFilterList.add(type);
        }
    }


}
