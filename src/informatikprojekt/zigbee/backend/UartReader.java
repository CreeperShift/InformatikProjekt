package informatikprojekt.zigbee.backend;

import gnu.io.NRSerialPort;
import informatikprojekt.zigbee.util.CommonUtils;

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
            System.out.println("Connected");
            activeState = State.CONNECTED;

            try (DataInputStream ins = new DataInputStream(serial.getInputStream());){
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
                SensorData s = new SensorData("",Integer.parseInt(dataSplit[1]), 1, CommonUtils.TEMPERATURE, Float.parseFloat(dataSplit[2]));
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


}
