package informatikprojekt.zigbee.backend;

import gnu.io.NRSerialPort;

import java.io.DataInputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class UartReader extends Thread {

    private ArrayBlockingQueue<SensorData> sensorDataQueue = new ArrayBlockingQueue<>(100);
    private boolean isActive = false;
    private boolean isConnected = false;
    private boolean isReaderActive = false;

    final int baudRate = 38400;
    private NRSerialPort serial = null;

    public UartReader() {
    }

    @Override
    public void run() {
        while (isActive) {

            serial = new NRSerialPort("COM5", baudRate);
            DataInputStream ins;

            while (serial != null && !serial.isConnected() && isActive) {
                try {
                    serial.connect();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    isConnected = false;
                    if (serial != null) {
                        serial.disconnect();
                        isConnected = false;
                    }
                }
            }

            while (serial != null && serial.isConnected() && isActive && isReaderActive) {
                isConnected = true;
                ins = new DataInputStream(serial.getInputStream());
                try {
                    String data = "";
                    while (ins.available() > 0) {// read all bytes
                        char b = (char) ins.read();
                        data = data.concat(String.valueOf(b));
                    }
                    if (!data.isBlank()) {
                        data = data.substring(0, data.length() - 1);
                        String finalData = data;
                        String[] dataSplit = data.split(";");

                        processData(dataSplit);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("UART ERROR");
                    if (serial != null) {
                        serial.disconnect();
                        isConnected = false;
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (serial != null && serial.isConnected()) {
                serial.disconnect();
                isConnected = false;
            }
        }
    }

    public BlockingQueue<SensorData> getQueue() {
        return sensorDataQueue;
    }

    private void processData(String[] dataSplit) {


        switch (Integer.parseInt(dataSplit[0])) {
            case 0:
                //0;1;086
                SensorData s = new SensorData(Integer.parseInt(dataSplit[1]), 1, 0, Float.parseFloat(dataSplit[2]));
                sensorDataQueue.add(s);
                break;
            case 1:
                //TODO: Battery level?
                break;
            case 2:
                //TODO: Error code of some sort?
                break;
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isReaderActive() {
        return isReaderActive;
    }

    public void setReaderActive(boolean active) {
        isReaderActive = active;
    }
}
