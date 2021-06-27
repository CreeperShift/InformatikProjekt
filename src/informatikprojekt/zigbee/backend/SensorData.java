package informatikprojekt.zigbee.backend;

public class SensorData {

    private final String formattedTime;
    private final int deviceID;
    private final int sensorID;
    private final int dataType;
    private final float data;

    public SensorData(String formattedTime, int deviceID, int SensorID, int dataType, float data) {

        this.formattedTime = formattedTime;
        this.deviceID = deviceID;
        sensorID = SensorID;
        this.dataType = dataType;
        this.data = data;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public int getSensorID() {
        return sensorID;
    }

    public int getDataType() {
        return dataType;
    }

    public float getData() {
        return data;
    }
}
