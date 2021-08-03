package informatikprojekt.zigbee.backend;

import gnu.io.NRSerialPort;
import informatikprojekt.zigbee.frontend.ControllerBase;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

public class UartReader extends Thread {

    private final ArrayBlockingQueue<Data> dataSet = new ArrayBlockingQueue<>(100);
    private boolean isReading = false;
    private boolean createdRecording = false;
    private int recID = 0;

    private List<Data> battery = new ArrayList<>();

    State activeState = State.NONE;

    /*
    Verbundene States
     */
    public enum State {
        NONE, CONNECTED, FAILED, ENDED
    }

    final int baudRate = 38400;
    private NRSerialPort serial = null;
    private final String port;
    private final String roomName;

    public UartReader(String port, String roomName) {
        this.roomName = roomName;
        this.port = port;
    }

    public synchronized State getReaderState() {
        return activeState;
    }

    public synchronized void setReaderState(State state) {
        activeState = state;
    }

    private synchronized ArrayBlockingQueue<Data> getDataSet() {
        return dataSet;
    }

    public synchronized boolean isCreatedRecording() {
        return createdRecording;
    }

    public void startReader() {
        this.start();
    }

    @Override
    public void run() {
        /*
        Oeffnet eine Serial Verbindung
         */
        DataInputStream ins = null;
        try {
            serial = new NRSerialPort(port, baudRate);
            serial.connect();
            //Erstellt einen DataStream für die Nachricht
            ins = new DataInputStream(serial.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            activeState = State.FAILED;
        }

        while (serial != null && ins != null && serial.isConnected() && activeState != State.ENDED) {
            activeState = State.CONNECTED;

            /*
            Erstellt ein neues Recording in der Datenbank
             */
            if (!createdRecording) {

                try {
                    Connection conn = ConnectionManager.getConnection();
                    String recordingQuery = "INSERT INTO recording (id, room_FK, timeStarted) values (NULL, (select id from room where roomName == ?), ?)";
                    PreparedStatement recording = conn.prepareStatement(recordingQuery);
                    String date = CommonUtils.getCurrentTimeFormatted();
                    recording.setString(1, roomName);
                    recording.setString(2, date);
                    recording.executeUpdate();
                    recording.close();

                    String getID = "select id from recording where timeStarted == ?";
                    PreparedStatement recordingID = conn.prepareStatement(getID);
                    recordingID.setString(1, date);
                    ResultSet resultSet = recordingID.executeQuery();
                    if (resultSet.next()) {
                        recID = resultSet.getInt("id");
                    }
                    resultSet.close();
                    recordingID.close();
                    conn.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                createdRecording = true;
            }

            try {
                //Liest nachricht aus bis zum Stopchar #
                String data = "";
                while (ins.available() > 0) {// read all bytes
                    char b = (char) ins.read();
                    if (b == '#') {
                        break;
                    }
                    data = data.concat(String.valueOf(b));
                }
                if (!data.isBlank()) {
                    /*                    data = data.substring(0, data.length() - 1);*/
                    String finalData = data;
                    Platform.runLater(() -> {
                        CommonUtils.consoleString(finalData);
                    });
                    //Aufspalten nach API mit ;
                    String[] dataSplit = data.split(";");
                    try {
                        if (Integer.parseInt(dataSplit[0]) == 0 || Integer.parseInt(dataSplit[0]) == 1) {
                            //Wenn es SensorDaten also 0 ist dann process
                            processData(dataSplit);
                        }
                    } catch (Exception ignored) {
                    }
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
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (serial == null || !serial.isConnected()) {
            activeState = State.FAILED;
        }

        if (serial != null && serial.isConnected()) {
            serial.disconnect();
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                handleBattery(dataSplit);
                break;
            case 2:
                //TODO: Error code of some sort?
                break;
        }
    }

    private void handleBattery(String[] dataSplit) {

        int device = Integer.parseInt(dataSplit[1]);
        float val = Float.parseFloat(dataSplit[2]);

        battery.add(new Data(device, "Battery", "Batterie", val));

    }

    void handleData(String[] dataSplit) throws SQLException {

        if (!isReading) {
            isReading = true;
            //Nach dem ersten Lesen für X sekunden alle Daten aggregieren
            addDataToList(dataSplit);
            //Nach dem Timer alle gesammelten daten als ein Datensatz in die Datenbank schreiben
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
                List<Data> currentData = new ArrayList<>();
                Connection connection = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:zigbee.sqlite");

                    isReading = false;
                    String createDataSet = "INSERT INTO dataset (id, timeRecorded, recording_FK) VALUES ( NULL, ?, ?)";
                    String createDataPoint = "INSERT INTO data (dataID, dataSetID, sensor, dataType, dataValue, device_FK) values (NULL, (select id from dataset where timeRecorded == ?), ?, ?, ?, ?);";
                    try {
                        PreparedStatement statement = connection.prepareStatement(createDataSet);

                        String date = CommonUtils.getCurrentTimeFormatted();

                        statement.setString(1, date);
                        statement.setInt(2, recID);
                        statement.executeUpdate();
                        statement.close();
                        PreparedStatement createData = connection.prepareStatement(createDataPoint);


                        while (!getDataSet().isEmpty()) {
                            Data d = getDataSet().take();
                            currentData.add(d);
                            createData.setString(1, date);
                            createData.setString(2, d.SensorName());
                            createData.setString(3, d.dataType());
                            createData.setFloat(4, d.value());
                            createData.setInt(5, d.Device());
                            Platform.runLater(() -> {
                                CommonUtils.consoleString("REC> " + "Device " + d.Device() + " " + d.SensorName() + " " + d.dataType() + " " + d.value());
                            });
                            createData.executeUpdate();
                        }
                        createData.close();


                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        System.err.println("Could not write into Database!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException | ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                }
                if (battery != null) {
                    currentData.addAll(battery);
                    battery.clear();
                }
                notifyOthers(currentData);
                writeTimer.cancel();
                try {
                    assert connection != null;
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }, 3000, 1000);
    }

    private void addDataToList(String[] dataSplit) {

        try {
            if (dataSplit[0].equals("0")) {

                for (int i = 4; i < (Integer.parseInt(dataSplit[3]) * 2) + 4; i = i + 2) {

                    Data data = new Data(Integer.parseInt(dataSplit[1]), dataSplit[2], dataSplit[i], Float.parseFloat(dataSplit[i + 1]));
                    if (existsType(data.dataType())) {
                        dataSet.add(data);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            dataSet.clear();
        }
    }

    private boolean existsType(String type) {
        return switch (type) {
            case CommonUtils.CO2, CommonUtils.HUMIDITY, CommonUtils.TEMPERATURE, CommonUtils.VOC -> true;
            default -> false;
        };

    }

    private synchronized void notifyOthers(List<Data> data) {
        Platform.runLater(() -> {
            ControllerBase.setDataList(data);
        });
    }
}
