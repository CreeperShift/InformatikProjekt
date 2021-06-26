package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataSet {

    private static int ID_COUNTER = 0;

    private final int id;
    private final List<SensorData> sensorData = new ArrayList<>();
    private final LocalDateTime time;


    public DataSet() {
        this(LocalDateTime.now());
    }

    public DataSet(LocalDateTime time) {
        this.time = time;
        this.id = ID_COUNTER++;
    }

    public final List<SensorData> getSensorData() {
        return Collections.unmodifiableList(sensorData);
    }

    public void addSensorData(SensorData data) {
        sensorData.add(data);
    }

    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
        return "Dataset at: " + time.format(format) + " with data: " + Arrays.toString(sensorData.toArray());
    }
}
