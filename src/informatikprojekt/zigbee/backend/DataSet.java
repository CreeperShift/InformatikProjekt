package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class DataSet {

    private ArrayList<SensorData> sensorData;
    private final LocalDateTime time;


    public DataSet() {
        this(LocalDateTime.now());
    }

    public DataSet(LocalDateTime time) {
        this.time = time;
    }

    public ArrayList<SensorData> getSensorData() {
        return sensorData;
    }

    public void addSensorData(SensorData data) {
        sensorData.add(data);
    }

    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Dataset at: " + time.format(DateTimeFormatter.ISO_DATE_TIME) + " with data: " + Arrays.toString(sensorData.toArray());
    }
}
