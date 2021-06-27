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
import javafx.scene.control.RadioButton;
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
    public RadioButton radioTmp;
    public RadioButton radioHum;
    public RadioButton radioVOC;
    public RadioButton radioCO2;
    public RadioButton radioPartikel;
    private NumberAxis dataAxis;
    private boolean setupDone = false;
    public static ControllerGraph INSTANCE;
    private RadioButton[] buttons;
    private XYChart.Series<NumberAxis, NumberAxis> tempSeries;
    private XYChart.Series<NumberAxis, NumberAxis> humidSeries;
    private XYChart.Series<NumberAxis, NumberAxis> co2Series;
    private XYChart.Series<NumberAxis, NumberAxis> vocSeries;
    private LineChart<NumberAxis, NumberAxis> lineChart;
    private LocalDateTime start;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        INSTANCE = this;
        buttons = new RadioButton[]{radioTmp, radioCO2, radioHum, radioVOC, radioPartikel};
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

    }

    public void onRadio(ActionEvent actionEvent) {

        RadioButton source = ((RadioButton) actionEvent.getSource());
        if (!source.isSelected()) {
            source.setSelected(true);
        } else {

            for (RadioButton b : buttons) {
                if (actionEvent.getSource() != b) {
                    b.setSelected(false);
                }
            }
        }

        lineChart.getData().clear();
        for (RadioButton b : buttons) {
            if (b.isSelected()) {
                if (b.getText().equalsIgnoreCase("Temperatur")) {
                    lineChart.getData().add(tempSeries);
                } else if (b.getText().equalsIgnoreCase("Feuchtigkeit")) {
                    lineChart.getData().add(humidSeries);

                } else if (b.getText().equalsIgnoreCase("VoC")) {
                    lineChart.getData().add(vocSeries);
                } else if (b.getText().equalsIgnoreCase("CO2")) {
                    lineChart.getData().add(co2Series);
                }
            }
        }

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
                                    int readings = 0;
                                    for (SensorData sensorData : d.getSensorData()) {
                                        if (sensorData.getDataType().equalsIgnoreCase(CommonUtils.TEMPERATURE)) {
                                            temp = temp + sensorData.getData();
                                            readings++;
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

                                    tempSeries.getData().add(new XYChart.Data(sec, temp / readings));
                                }

                            }

                    );

                }
            }, 500, 3000);

            setupDone = true;
        }
    }
}
