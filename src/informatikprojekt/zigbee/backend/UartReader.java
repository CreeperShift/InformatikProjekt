package informatikprojekt.zigbee.backend;

import gnu.io.NRSerialPort;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UartReader extends Thread {

    private final List<Data> dataSet = new LinkedList<>();
    private boolean isReading = false;
    private Connection connection;

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

    private synchronized List<Data> getDataSet(){
        return dataSet;
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

            try (DataInputStream ins = new DataInputStream(serial.getInputStream())) {
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


    private void processData(String[] dataSplit) {


        switch (Integer.parseInt(dataSplit[0])) {
            case 0:
                //0;1;086
                try {
                    handleData(dataSplit);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case 1:
                //TODO: Battery level?
                break;
            case 2:
                //TODO: Error code of some sort?
                break;
        }
    }

    void handleData(String[] dataSplit) throws SQLException {

        if (!isReading) {
            isReading = true;
            addDataToList(dataSplit);
            createDatabaseTimer();
        } else {
            addDataToList(dataSplit);
        }

    }

    private void createDatabaseTimer() {
        Timer writeTimer = new Timer();
        CommonUtils.registerTimer(writeTimer);
        writeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/luftqualitaet", "root", "progex");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                isReading = false;
                String createDataSet = "INSERT INTO dataset (recordedAt) value (?);";
                String createDataPoint = "INSERT INTO data (dataSetID, sensor, dataType, dataValue, device) values ((select id from dataset where recordedAt = ?), ?, ?, ?, ?);";
                try {
                    PreparedStatement statement = connection.prepareStatement(createDataSet);
                    LocalDateTime loc = LocalDateTime.now();
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
                    Timestamp timestamp = Timestamp.valueOf(loc.format(format));

                    statement.setString(1,timestamp.toString());
                    statement.executeUpdate();
                    statement.close();
                    PreparedStatement createData = connection.prepareStatement(createDataPoint);


                    for (Data d : getDataSet()) {
                        createData.setString(1, timestamp.toString());
                        createData.setString(2, d.SensorName());
                        createData.setString(3, d.dataType());
                        createData.setFloat(4, d.value());
                        createData.setInt(5, d.Device());
                        createData.executeUpdate();
                    }
                    createData.close();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    System.err.println("Could not write into Database!");
                }
                writeTimer.cancel();
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }, 5000, 1000);
    }

    private void addDataToList(String[] dataSplit) {

        if (dataSplit[0].equals("0")) {

            for (int i = 4; i <= Integer.parseInt(dataSplit[3]) + 4; i = i + 2) {

                Data data = new Data(Integer.parseInt(dataSplit[1]), dataSplit[2], dataSplit[i], Float.parseFloat(dataSplit[i + 1]));
                dataSet.add(data);
            }
        }
    }


}
