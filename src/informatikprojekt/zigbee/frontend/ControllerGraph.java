package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.backend.DataManager;
import informatikprojekt.zigbee.backend.DataSet;
import informatikprojekt.zigbee.backend.SensorData;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ControllerGraph implements Initializable {
    public VBox box;
    public Button btnTemp;
    public Button btnFeucht;
    public Button btnCO2;
    public Button btnVoC;
    private NumberAxis dataAxis;
    private boolean setupDone = false;
    public static ControllerGraph INSTANCE;
    private Button[] buttons;
    private XYChart.Series<NumberAxis, NumberAxis> tempSeries;
    private XYChart.Series<NumberAxis, NumberAxis> humidSeries;
    private XYChart.Series<NumberAxis, NumberAxis> co2Series;
    private XYChart.Series<NumberAxis, NumberAxis> vocSeries;
    private LineChart<NumberAxis, NumberAxis> lineChart;
    private LocalDateTime start;
    private Button activeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        INSTANCE = this;
        activeButton = btnTemp;
        buttons = new Button[]{btnCO2, btnTemp, btnFeucht, btnVoC};
        box.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        dataAxis = new NumberAxis(0, 70, 5);
        NumberAxis timeAxis = new NumberAxis(0, 60, 2);
        lineChart = new LineChart(timeAxis, dataAxis);

        tempSeries = new XYChart.Series();
        humidSeries = new XYChart.Series();
        co2Series = new XYChart.Series();
        vocSeries = new XYChart.Series();
        tempSeries.setName("Temperatur");
        humidSeries.setName("Feuchtigkeit");
        co2Series.setName("CO2");
        vocSeries.setName("VoC");
        lineChart.getData().add(tempSeries);
        box.getChildren().add(1, lineChart);
        lineChart.setAnimated(false);

    }


    public void setupData() {

        if (!setupDone) {
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

    private void onBtnDelay() {
        for (Button b : buttons) {
            b.setDisable(true);

            Timer timer = new Timer();
            CommonUtils.registerTimer(timer);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    Platform.runLater(() -> {
                        for (Button b : buttons) {
                            b.setDisable(false);
                        }
                        timer.cancel();
                    });
                }
            }, 100, 500);
        }
    }


    public void onBtnTemp(ActionEvent actionEvent) {
        if (activeButton != btnTemp) {
            lineChart.getData().clear();
            onBtnDelay();
            lineChart.getData().add(tempSeries);
            dataAxis.setLowerBound(0);
            dataAxis.setUpperBound(70);
            dataAxis.setTickUnit(5);
            activeButton = btnTemp;
        }
    }

    public void onBtnFeucht(ActionEvent actionEvent) {
        if (activeButton != btnFeucht) {
            lineChart.getData().clear();
            onBtnDelay();
            lineChart.getData().add(humidSeries);
            dataAxis.setLowerBound(0);
            dataAxis.setUpperBound(100);
            dataAxis.setTickUnit(10);
            activeButton = btnFeucht;
        }
    }

    public void onBtnCO2(ActionEvent actionEvent) {
        if (activeButton != btnCO2) {
            lineChart.getData().clear();
            onBtnDelay();
            lineChart.getData().add(co2Series);
            dataAxis.setLowerBound(350);
            dataAxis.setUpperBound(900);
            dataAxis.setTickUnit(50);
            activeButton = btnCO2;
        }
    }

    public void onBtnVoC(ActionEvent actionEvent) {
        if (activeButton != btnVoC) {
            lineChart.getData().clear();
            onBtnDelay();
            lineChart.getData().add(vocSeries);
            dataAxis.setLowerBound(0);
            dataAxis.setUpperBound(1000);
            dataAxis.setTickUnit(100);
            activeButton = btnVoC;
        }
    }
}
