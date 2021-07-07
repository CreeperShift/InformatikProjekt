package informatikprojekt.zigbee.backend;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public List<String> getAllDataTypes() throws SQLException {
        List<String> allDataTypes = new LinkedList<>();
        String getDataTypes = "Select dataType from data";
        PreparedStatement preparedStatement = connection.prepareStatement(getDataTypes);
        ResultSet result = preparedStatement.executeQuery();

        while(result.next()){
            allDataTypes.add(result.getString("dataType"));
        }
        preparedStatement.close();
        return allDataTypes;
    }

    @Override
    public List<Integer> getAllDevices() {

    }

    @Override
    public List<String> getAllSensors() throws SQLException {
        List<String> allSensors = new LinkedList<>();
        String getSensors = "Select sensors from data";
        PreparedStatement preparedStatement = connection.prepareStatement(getSensors);
        ResultSet result = preparedStatement.executeQuery();

        while(result.next()){
            allSensors.add(result.getString("sensor"));
        }
        preparedStatement.close();
        return allSensors;
    }

    @Override
    public void writeRoom(Room room) throws SQLException {

        String queryCreateRoom = "INSERT INTO room (id, roomName, created) VALUES (NULL, ?, ?)";
        String queryRoomID = "SELECT id from room where created = ?";
        String queryCreatePoints = "INSERT INTO roomPoints (id, x, y, roomID_FK) VALUES (NULL, ?, ?, ?)";
        String createConnections = "INSERT INTO graph (roomPoint_FK, connected_FK) VALUES ((select id from roomPoints where roomID_FK == ? AND x == ? and y == ?), (select id from roomPoints where roomID_FK == ? AND x == ? and y == ?))";

        PreparedStatement createRoom = connection.prepareStatement(queryCreateRoom);
        createRoom.setString(1, room.getName());
        createRoom.setString(2, room.getCreatedFormatted());
        createRoom.executeUpdate();
        PreparedStatement getID = connection.prepareStatement(queryRoomID);
        getID.setString(1, room.getCreatedFormatted());
        ResultSet res = getID.executeQuery();
        int id = 0;
        if (res.next()) {
            id = res.getInt("id");
        }

        for (Circle c : room.getRoomGraph().getCircles()) {
            PreparedStatement createPoint = connection.prepareStatement(queryCreatePoints);
            createPoint.setDouble(1, c.getCenterX());
            createPoint.setDouble(2, c.getCenterY());
            createPoint.setInt(3, id);
            createPoint.executeUpdate();
        }

        for (Circle c : room.getRoomGraph().getCircles()) {
            for (Circle connected : room.getRoomGraph().getAdj(c).keySet()) {

                PreparedStatement connections = connection.prepareStatement(createConnections);
                connections.setInt(1, id);
                connections.setDouble(2, c.getCenterX());
                connections.setDouble(3, c.getCenterY());
                connections.setInt(4, id);
                connections.setDouble(5, connected.getCenterX());
                connections.setDouble(6, connected.getCenterY());
                connections.executeUpdate();
            }
        }
    }

    @Override
    public Room readRoom(String name) throws SQLException {
        Room room = new Room(name);
        String getRoomQuery = "SELECT * from room where roomName == ?;";
        PreparedStatement getRoom = connection.prepareStatement(getRoomQuery);
        getRoom.setString(1, name);
        ResultSet roomData = getRoom.executeQuery();
        if (roomData.next()) {
            String pointQuery = "select * from roomPoints where roomID_FK == ?;";
            PreparedStatement pointStatement = connection.prepareStatement(pointQuery);
            pointStatement.setInt(1, roomData.getInt("id"));
            ResultSet points = pointStatement.executeQuery();
            Map<Integer, Circle> allCircles = new HashMap<>();
            while (points.next()) {
                Circle circle = createCircle(points.getDouble("x"), points.getDouble("y"));
                room.getRoomGraph().addCircle(circle);
                allCircles.put(points.getInt("id"), circle);
            }
            pointStatement.close();
            String pointConnectedQuery = "select * from graph where roomPoint_FK == ?;";

            for (Map.Entry<Integer, Circle> entry : allCircles.entrySet()) {
                Circle startCircle = entry.getValue();
                PreparedStatement pointConnected = connection.prepareStatement(pointConnectedQuery);
                pointConnected.setInt(1, entry.getKey());
                ResultSet connectedPointsData = pointConnected.executeQuery();
                while (connectedPointsData.next()) {
                    Circle endCircle = allCircles.get(connectedPointsData.getInt("connected_FK"));
                    Line connectedLine = new Line();
                    connectedLine.setStrokeWidth(4);
                    connectedLine.setStartX(startCircle.getCenterX());
                    connectedLine.setStartY(startCircle.getCenterY());
                    connectedLine.setEndX(endCircle.getCenterX());
                    connectedLine.setEndY(endCircle.getCenterY());
                    room.getRoomGraph().addEdge(startCircle, endCircle, connectedLine);
                }
                connectedPointsData.close();
            }
        }
        getRoom.close();
        return room;
    }

    private Circle createCircle(double x, double y) {
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(15);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(4);
        return circle;
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
