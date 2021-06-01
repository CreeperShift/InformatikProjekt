package informatikprojekt.zigbee.backend;

import gnu.io.NRSerialPort;

import java.io.DataInputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class UartReader extends Thread {

    private boolean active = false;
    private boolean hasStarted = false;
    private ArrayBlockingQueue<SensorData> sensorDataQueue = new ArrayBlockingQueue<>(100);
    private static UartReader INSTANCE;

    private UartReader() {
    }

    public static UartReader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UartReader();
        }
        return INSTANCE;
    }

    public void startReader(){
        if(!hasStarted){
            this.start();
            hasStarted = true;
            active = !active;
        }
    }

    @Override
    public void run() {
        int baudRate = 38400;
        NRSerialPort serial = new NRSerialPort("COM5", baudRate);
        serial.connect();

        DataInputStream ins = new DataInputStream(serial.getInputStream());
        while (active) {
            try {
                String data = "";
                while (ins.available() > 0) {// read all bytes
                    char b = (char) ins.read();
                    data = data.concat(String.valueOf(b));
                }
                if (!data.isBlank()) {
                    data = data.substring(0, 7);
                    String[] dataSplit = data.split(";");

                    processData(dataSplit);

                }
                Thread.sleep(200);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        serial.disconnect();
    }

    public BlockingQueue<SensorData> getQueue() {
        return sensorDataQueue;
    }

    private void processData(String[] dataSplit) {

        switch (Integer.parseInt(dataSplit[0])) {
            case 0:
                SensorData current = new SensorData(Integer.parseInt(dataSplit[1]), Float.parseFloat(dataSplit[2]), LocalDateTime.now());
                sensorDataQueue.add(current);
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
