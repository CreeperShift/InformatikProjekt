package informatikprojekt.zigbee.backend;

import informatikprojekt.zigbee.util.CommonUtils;

public class SensorData {

    private String formattedTime;
    private final int deviceID;
    private final int sensorID;
    private final String dataType;
    private final float data;
    private final String sensorName;

    public SensorData(String formattedTime, int deviceID, int SensorID, String dataType, float data) {

        this.formattedTime = formattedTime;
        this.deviceID = deviceID;
        sensorID = SensorID;
        this.dataType = dataType;
        this.data = data;
        this.sensorName = CommonUtils.getSensorName(sensorID);
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setFormattedTime(String time) {
        formattedTime = time;
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

    public String getDataType() {
        return dataType;
    }

    public float getData() {
        return data;
    }
}
