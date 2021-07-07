package informatikprojekt.zigbee.backend;

import javafx.scene.shape.Circle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class DataManager implements IData {

    private final LinkedList<DataSet> dataSets = new LinkedList<>();
    private static UartReader uartReader;
    private String port = "COM1";
    Connection connection = ConnectionManager.getConnection();

    private static DataManager INSTANCE = null;

    private DataManager() throws SQLException {
    }

    public static DataManager get() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new DataManager();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return INSTANCE;
    }

    public static void stop() {
        if (uartReader != null) {
            uartReader.stop();
        }
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void startReader() {

        if (uartReader == null || isStopped() || isFailed()) {
            uartReader = new MockUartReader(port);
            uartReader.startReader();
        }
    }

    @Override
    public List<DataSet> getDataForTime(LocalDateTime from, LocalDateTime to) {

        LinkedList<DataSet> returnData = new LinkedList<>();

        for (DataSet data : dataSets) {
            if (data.getTime().isAfter(from) && data.getTime().isBefore(to)) {
                returnData.add(data);
            }
        }

        return returnData;
    }

    @Override
    public List<DataSet> getDataForTime(LocalDateTime from) {
        return getDataForTime(from, LocalDateTime.now());
    }

    @Override
    public List<SQLData> getDailyMeanForType(String type, LocalDateTime from, LocalDateTime to) {
        return null;
    }

    @Override
    public List<SQLData> getDailyMinForType(String type, LocalDateTime from, LocalDateTime to) {
        return null;
    }

    @Override
    public List<SQLData> getDailyMaxForType(String type, LocalDateTime from, LocalDateTime to) {
        return null;
    }

    @Override
    public List<SQLData> getStandardDeviationForType(String type) {
        return null;
    }

    @Override
    public List<String> getAllDataTypes() {
        return null;
    }

    @Override
    public List<Integer> getAllDevices() {
        return null;
    }

    @Override
    public List<String> getAllSensors() {
        return null;
    }

    @Override
    public void writeRoom(Room room) throws SQLException {

        String queryCreateRoom = "INSERT INTO room (id, roomName, created) VALUES (NULL, ?, ?)";
        String queryCreatePoints = "INSERT INTO roomPoints (id, x, y, roomID_FK) VALUES (NULL, ?, ?, (SELECT id from room where created = ? & room.roomName = ?))";

        PreparedStatement createRoom = connection.prepareStatement(queryCreateRoom);
        createRoom.setString(1, room.getName());
        createRoom.setString(2, room.getCreatedFormatted());
        createRoom.executeUpdate();

        for (Circle c : room.getRoomGraph().getCircles()) {
            PreparedStatement createPoint = connection.prepareStatement(queryCreatePoints);
            createPoint.setDouble(1, c.getCenterX());
            createPoint.setDouble(2, c.getCenterY());
            createPoint.setString(3, room.getCreatedFormatted());
            createPoint.setString(4, room.getName());
            createPoint.executeUpdate();
        }

    }

    public void stopReader() {
        if (uartReader != null) {
            uartReader.setReaderState(UartReader.State.ENDED);
        }
    }

    public final List<DataSet> getDataAll() {
        return dataSets;
    }

    public boolean isConnected() {
        return uartReader.getReaderState() == UartReader.State.CONNECTED;
    }


    public boolean isFailed() {
        return uartReader.getReaderState() == UartReader.State.FAILED;
    }

    public boolean isStopped() {
        return uartReader.getReaderState() == UartReader.State.ENDED;
    }
}
