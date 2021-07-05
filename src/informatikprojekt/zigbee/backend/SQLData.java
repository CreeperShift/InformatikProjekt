package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;
import java.util.List;

public class SQLData {

    private final LocalDateTime timeStamp;
    private final List<SensorData> sensorData;

    public SQLData(List<SensorData> sensorData, LocalDateTime time) {
        this.sensorData = sensorData;
        this.timeStamp = time;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public List<SensorData> getSensorData() {
        return sensorData;
    }
}
