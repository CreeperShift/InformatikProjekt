package informatikprojekt.zigbee.backend;

import java.time.LocalDateTime;

public class SensorData {

    private final int id;
    private final float data;
    private final LocalDateTime date;

    public SensorData(int id, float data, LocalDateTime date) {

        this.id = id;
        this.data = data;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public float getData() {
        return data;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
