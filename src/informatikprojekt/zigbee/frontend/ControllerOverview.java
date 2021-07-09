package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.backend.DataManager;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ControllerOverview implements Initializable {
    public Circle ledRED;
    public Circle ledORANGE;
    public Circle ledGREEN;
    public Label tempValue;
    public Label humValue;
    public Label co2Value;
    public Label vocValue;

    public static ControllerOverview INSTANCE;

    Paint redColor;
    Paint orangeColor;
    Paint greenColor;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        INSTANCE = this;
        redColor = ledRED.getFill();
        orangeColor = ledORANGE.getFill();
        greenColor = ledGREEN.getFill();

        ledRED.setFill(Color.GRAY);
        ledORANGE.setFill(Color.GRAY);
    }

    public void startTimer() {

        Timer timer1 = new Timer();
        CommonUtils.registerTimer(timer1);
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    float temp = DataManager.get().get15MinAverage("Temperatur");
                    float hum = DataManager.get().get15MinAverage("Feuchtigkeit");
                    float co2 = DataManager.get().get15MinAverage("CO2");
                    float voc = DataManager.get().get15MinAverage("VOC");

                    tempValue.setText(temp + "");
                    humValue.setText(hum + "");
                    vocValue.setText(voc + "");
                    co2Value.setText(co2 + "");
                });
            }
        }, Duration.ofMinutes(2).toMillis(), Duration.ofMinutes(15).toMillis());

    }

}
