package informatikprojekt.zigbee.backend;

import gnu.io.NRSerialPort;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class UartReader extends Thread {

    private final ArrayBlockingQueue<SensorData> sensorDataQueue = new ArrayBlockingQueue<>(100);

    State activeState = State.NONE;

    public enum State {
        NONE, CONNECTED, FAILED, ENDED
    }

    final int baudRate = 38400;
    private NRSerialPort serial = null;
    private final String port;

    public UartReader(String port) {
        this.port = port;
    }

    public synchronized State getReaderState() {
        return activeState;
    }

    public synchronized void setReaderState(State state) {
        activeState = state;
    }

    public void startReader() {
        this.start();
    }

    @Override
    public void run() {

        serial = new NRSerialPort(port, baudRate);
        serial.connect();

        while (serial != null && serial.isConnected() && activeState != State.ENDED) {
            activeState = State.CONNECTED;

            try (DataInputStream ins = new DataInputStream(serial.getInputStream());) {
                String data = "";
                while (ins.available() > 0) {// read all bytes
                    char b = (char) ins.read();
                    data = data.concat(String.valueOf(b));
                }
                if (!data.isBlank()) {
                    data = data.substring(0, data.length() - 1);
                    String finalData = data;
                    Platform.runLater(() -> {
                        CommonUtils.consoleString(finalData);
                    });
                    String[] dataSplit = data.split(";");

                    processData(dataSplit);

                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("UART ERROR");
                if (serial != null) {
                    serial.disconnect();
                    activeState = State.FAILED;
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (serial == null || !serial.isConnected()) {
            activeState = State.FAILED;
        }

        if (serial != null && serial.isConnected()) {
            serial.disconnect();
            activeState = State.ENDED;
        }
        this.stop();
    }


    public BlockingQueue<SensorData> getQueue() {
        return sensorDataQueue;
    }

    private void processData(String[] dataSplit) {


        switch (Integer.parseInt(dataSplit[0])) {
            case 0:
                //0;1;086
                handleData(dataSplit);
                break;
            case 1:
                //TODO: Battery level?
                break;
            case 2:
                //TODO: Error code of some sort?
                break;
        }
    }

    private void handleData(String[] dataSplit) {

        switch (Integer.parseInt(dataSplit[2])) {
            case 1 -> {
                SensorData s1 = new SensorData("", Integer.parseInt(dataSplit[1]), 1, CommonUtils.TEMPERATURE, Float.parseFloat(dataSplit[3]));
                SensorData s2 = new SensorData("", Integer.parseInt(dataSplit[1]), 1, CommonUtils.HUMIDITY, Float.parseFloat(dataSplit[4]));
                sensorDataQueue.add(s1);
                sensorDataQueue.add(s2);
            }
            case 2 -> {
                SensorData s3 = new SensorData("", Integer.parseInt(dataSplit[1]), 2, CommonUtils.CO2, Float.parseFloat(dataSplit[3]));
                SensorData s4 = new SensorData("", Integer.parseInt(dataSplit[1]), 2, CommonUtils.VOC, Float.parseFloat(dataSplit[4]));
                sensorDataQueue.add(s3);
                sensorDataQueue.add(s4);
            }
            case 3 -> {
                SensorData s5 = new SensorData("", Integer.parseInt(dataSplit[1]), 3, CommonUtils.CO2, Float.parseFloat(dataSplit[3]));
                SensorData s6 = new SensorData("", Integer.parseInt(dataSplit[1]), 3, CommonUtils.TEMPERATURE, Float.parseFloat(dataSplit[4]));
                SensorData s7 = new SensorData("", Integer.parseInt(dataSplit[1]), 3, CommonUtils.HUMIDITY, Float.parseFloat(dataSplit[5]));
                sensorDataQueue.add(s5);
                sensorDataQueue.add(s6);
                sensorDataQueue.add(s7);
            }
            case 4 -> {
                SensorData s8 = new SensorData("", Integer.parseInt(dataSplit[1]), 4, CommonUtils.CO2, Float.parseFloat(dataSplit[3]));
                SensorData s9 = new SensorData("", Integer.parseInt(dataSplit[1]), 4, CommonUtils.TEMPERATURE, Float.parseFloat(dataSplit[4]));
                SensorData s10 = new SensorData("", Integer.parseInt(dataSplit[1]), 4, CommonUtils.HUMIDITY, Float.parseFloat(dataSplit[5]));
                sensorDataQueue.add(s8);
                sensorDataQueue.add(s9);
                sensorDataQueue.add(s10);
            }
        }
    }


}
