package informatikprojekt.zigbee.frontend;

import com.opencsv.CSVWriter;
import informatikprojekt.zigbee.Main;
import informatikprojekt.zigbee.backend.DataManager;
import informatikprojekt.zigbee.backend.DataSet;
import informatikprojekt.zigbee.backend.ExportData;
import informatikprojekt.zigbee.backend.SensorData;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ControllerGraph implements Initializable {
    public VBox box;
    public Button btnTemp;
    public Button btnFeucht;
    public Button btnCO2;
    public Button btnVoC;
    public HBox buttonPanel;
    public ComboBox<String> btnCombo;
    public Button onBtnUpdate;
    private NumberAxis dataAxis;
    private CategoryAxis timeAxis;
    private boolean setupDone = false;
    public static ControllerGraph INSTANCE;
    private Button[] buttons;
    private XYChart.Series<NumberAxis, NumberAxis> tempSeries;
    private XYChart.Series<NumberAxis, NumberAxis> humidSeries;
    private XYChart.Series<NumberAxis, NumberAxis> co2Series;
    private XYChart.Series<NumberAxis, NumberAxis> vocSeries;
    private LineChart<CategoryAxis, NumberAxis> lineChart;
    private LocalDateTime start;
    private Button activeButton;

    private String activeType = "";
    private String activeCalc = "Mean";
    private boolean initialSetupDone = false;

    private List<Button> dataButtons = new LinkedList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        INSTANCE = this;
        activeButton = btnTemp;
        buttons = new Button[]{btnCO2, btnTemp, btnFeucht, btnVoC};
        box.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        dataAxis = new NumberAxis(0, 70, 5);
        timeAxis = new CategoryAxis();
        timeAxis.setLabel("Datum");
        lineChart = new LineChart(timeAxis, dataAxis);
        box.getChildren().add(1, lineChart);
        lineChart.setAnimated(false);
        lineChart.setPrefHeight(600);
        List<String> calc = Arrays.asList("Mean", "Min", "Max");
        ObservableList<String> strings = FXCollections.observableArrayList(calc);
        btnCombo.setItems(strings);
        btnCombo.setValue("Mean");


    }

    public void setupButtons() {

        Timer t1 = new Timer();
        CommonUtils.registerTimer(t1);
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    List<String> dataTypes = null;
                    try {
                        dataTypes = DataManager.get().getAllDataTypes();

                        for (String type : dataTypes) {
                            if (!checkIfContainsDataType(type)) {
                                Button newButton = new Button(type);
                                newButton.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent actionEvent) {
                                        onTypeChange(newButton.getText());
                                    }
                                });
                                dataButtons.add(newButton);
                                buttonPanel.getChildren().add(newButton);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 1500, 10000);

    }


    private void onTypeChange(String type) {
        if (!initialSetupDone) {

        }

        if (!activeType.equalsIgnoreCase(type)) {
            activeType = type;

            updateData();

        }
    }

    private void updateData() {
        Map<String, Float> data = DataManager.get().getDailyCalcForType(activeType, activeCalc);

        setDataAxis(activeType);

        XYChart.Series<CategoryAxis, NumberAxis> currentSeries = new XYChart.Series<>();
        currentSeries.setName(activeType);

        for (Map.Entry<String, Float> e : data.entrySet()) {
            currentSeries.getData().add(new XYChart.Data(e.getKey(), e.getValue()));
        }


        lineChart.getData().clear();
        lineChart.getData().add(currentSeries);

    }

    private void setDataAxis(String type) {

        switch (type) {
            case CommonUtils.TEMPERATURE -> {
                dataAxis.setLowerBound(0);
                dataAxis.setUpperBound(70);
                dataAxis.setTickUnit(5);
                dataAxis.setLabel("°C");
            }
            case CommonUtils.HUMIDITY -> {
                dataAxis.setLowerBound(0);
                dataAxis.setUpperBound(100);
                dataAxis.setTickUnit(10);
                dataAxis.setLabel("%");
            }
            case CommonUtils.CO2 -> {
                dataAxis.setLowerBound(300);
                dataAxis.setUpperBound(3500);
                dataAxis.setTickUnit(100);
                dataAxis.setLabel("ppm");
            }
            case CommonUtils.VOC -> {
                dataAxis.setLowerBound(0);
                dataAxis.setUpperBound(10);
                dataAxis.setTickUnit(0.1);
                dataAxis.setLabel("ppm");
            }
        }

    }

    private boolean checkIfContainsDataType(String type) {

        for (Button b : dataButtons) {
            if (b.getText().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }


    public void setupData() {

        if (!setupDone) {
            setupButtons();
            Timer timer = new Timer();
            CommonUtils.registerTimer(timer);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    Platform.runLater(() -> {
                                List<DataSet> data = DataManager.get().getDataForTime(LocalDateTime.now().minusSeconds(4));
                                for (DataSet d : data) {
                                    float temp = 0;
                                    float co2 = 0;
                                    float voc = 0;
                                    float hum = 0;
                                    int readingsT = 0;
                                    int readingsC = 0;
                                    int readingsV = 0;
                                    int readingsH = 0;
                                    for (SensorData sensorData : d.getSensorData()) {
                                        if (sensorData.getDataType().equalsIgnoreCase(CommonUtils.TEMPERATURE)) {
                                            temp = temp + sensorData.getData();
                                            readingsT++;
                                        }
                                        if (sensorData.getDataType().equalsIgnoreCase(CommonUtils.HUMIDITY)) {
                                            hum = hum + sensorData.getData();
                                            readingsH++;
                                        }
                                        if (sensorData.getDataType().equalsIgnoreCase(CommonUtils.CO2)) {
                                            co2 = co2 + sensorData.getData();
                                            readingsC++;
                                        }
                                        if (sensorData.getDataType().equalsIgnoreCase(CommonUtils.VOC)) {
                                            voc = voc + sensorData.getData();
                                            readingsV++;
                                        }

                                    }
                                    if (start == null) {
                                        start = d.getTime();
                                    }
                                    Duration dur = Duration.between(start, d.getTime());
                                    long sec = dur.toSeconds();
                                    if (sec < 0) {
                                        sec = 0;
                                    }

                                    if (readingsT > 0) {
                                        tempSeries.getData().add(new XYChart.Data(sec, temp / readingsT));
                                    }
                                    if (readingsH > 0) {
                                        humidSeries.getData().add(new XYChart.Data(sec, hum / readingsH));
                                    }
                                    if (readingsC > 0) {
                                        co2Series.getData().add(new XYChart.Data(sec, co2 / readingsC));
                                    }
                                    if (readingsV > 0) {
                                        vocSeries.getData().add(new XYChart.Data(sec, voc / readingsV));
                                    }
                                }

                            }

                    );

                }
            }, 500, 3000);

            setupDone = true;
        }
    }

    public void onBtnUpdate(ActionEvent actionEvent) {
        if (!btnCombo.getValue().isBlank() && !activeType.isBlank()) {
            if (!btnCombo.getValue().equalsIgnoreCase(activeCalc)) {
                activeCalc = btnCombo.getValue();
                updateData();
            }
        }

    }

    public void onBtnExport(boolean all) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportieren");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(Main.mainStage);

        if (file != null) {

            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                List<ExportData> exportDataList;
                List<String[]> dataList = new LinkedList<>();
                if (all) {
                    String[] header = new String[8];
                    header[0] = "Recording StartDate";
                    header[1] = "Recording StartTime";
                    header[2] = "Dataset Date";
                    header[3] = "DataSet Time";
                    header[4] = "Sensor";
                    header[5] = "Type";
                    header[6] = "Device";
                    header[7] = "Value";
                    dataList.add(header);
                    exportDataList = DataManager.get().getExportListForRoom(ControllerBase.INSTANCE.currentRoom.getName());
                } else {
                    String[] header = new String[6];
                    header[0] = "Dataset Date";
                    header[1] = "DataSet Time";
                    header[2] = "Sensor";
                    header[3] = "Type";
                    header[4] = "Device";
                    header[5] = "Value";
                    dataList.add(header);
                    exportDataList = DataManager.get().getExportList();
                }


                for (ExportData data : exportDataList) {
                    if (all) {
                        String[] header = new String[8];
                        header[0] = data.recordDate();
                        header[1] = data.recordTime();
                        header[2] = data.setDate();
                        header[3] = data.setTime();
                        header[4] = data.sensor();
                        header[5] = data.type();
                        header[6] = data.device();
                        header[7] = data.value();
                        dataList.add(header);
                    } else {
                        String[] header = new String[8];
                        header[0] = data.setDate();
                        header[1] = data.setTime();
                        header[2] = data.sensor();
                        header[3] = data.type();
                        header[4] = data.device();
                        header[5] = data.value();
                        dataList.add(header);
                    }
                }
                writer.writeAll(dataList);
                CommonUtils.consoleString("Daten wurden exportiert.");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void onBtnExportSet(ActionEvent actionEvent) {
        onBtnExport(false);
    }

    public void onBtnExportAll(ActionEvent actionEvent) {
        onBtnExport(true);
    }
}
