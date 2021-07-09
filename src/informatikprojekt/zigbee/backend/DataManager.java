package informatikprojekt.zigbee.backend;

import informatikprojekt.zigbee.frontend.Device;
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
            uartReader = new UartReader(port);
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
        Connection connection = ConnectionManager.getConnection();
        List<String> allDataTypes = new LinkedList<>();
        String getDataTypes = "Select distinct dataType from data";
        PreparedStatement preparedStatement = connection.prepareStatement(getDataTypes);
        ResultSet result = preparedStatement.executeQuery();

        while (result.next()) {
            allDataTypes.add(result.getString("dataType"));
        }
        preparedStatement.close();
        return allDataTypes;
    }

    @Override
    public List<Integer> getAllDevices() {
        return null;
    }

    @Override
    public List<String> getAllSensors() throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        List<String> allSensors = new LinkedList<>();
        String getSensors = "Select distinct sensor from data";
        PreparedStatement preparedStatement = connection.prepareStatement(getSensors);
        ResultSet result = preparedStatement.executeQuery();

        while (result.next()) {
            allSensors.add(result.getString("sensor"));
        }
        preparedStatement.close();
        return allSensors;
    }

    @Override
    public boolean existRoom(String name) {

        try (Connection connection = ConnectionManager.getConnection()) {
            String queryExistRoom = "SELECT COUNT(roomName) from room where roomName == ?;";
            PreparedStatement existRoom = connection.prepareStatement(queryExistRoom);
            existRoom.setString(1, name);
            ResultSet resultRoom = existRoom.executeQuery();

            if (resultRoom.next()) {
                return resultRoom.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void writeRoom(Room room) throws SQLException {
        Connection connection = ConnectionManager.getConnection();

        String queryCreateRoom = "INSERT INTO room (id, roomName, created) VALUES (NULL, ?, ?)";
        String queryRoomID = "SELECT id from room where roomName == ?";
        String queryCreatePoints = "INSERT INTO roomPoints (id, x, y, roomID_FK) VALUES (NULL, ?, ?, ?)";
        String createConnections = "INSERT INTO graph (roomPoint_FK, connected_FK) VALUES ((select id from roomPoints where roomID_FK == ? AND x == ? and y == ?), (select id from roomPoints where roomID_FK == ? AND x == ? and y == ?))";

        PreparedStatement createRoom = connection.prepareStatement(queryCreateRoom);
        createRoom.setString(1, room.getName());
        createRoom.setString(2, room.getCreatedFormatted());
        createRoom.executeUpdate();
        PreparedStatement getID = connection.prepareStatement(queryRoomID);
        getID.setString(1, room.getName());
        ResultSet res = getID.executeQuery();
        int id = 0;
        if (res.next()) {
            id = res.getInt("id");
        }
        PreparedStatement createPoint = connection.prepareStatement(queryCreatePoints);
        for (Circle c : room.getRoomGraph().getCircles()) {

            createPoint.setDouble(1, c.getCenterX());
            createPoint.setDouble(2, c.getCenterY());
            createPoint.setInt(3, id);
            createPoint.executeUpdate();
        }
        createPoint.close();
        PreparedStatement connections = connection.prepareStatement(createConnections);
        for (Circle c : room.getRoomGraph().getCircles()) {
            for (Circle connected : room.getRoomGraph().getAdj(c).keySet()) {


                connections.setInt(1, id);
                connections.setDouble(2, c.getCenterX());
                connections.setDouble(3, c.getCenterY());
                connections.setInt(4, id);
                connections.setDouble(5, connected.getCenterX());
                connections.setDouble(6, connected.getCenterY());
                connections.executeUpdate();
            }
        }
        connections.close();

        String deviceQuery = "INSERT INTO device (id, networkID, x, y, room_FK) values (NULL, ?, ?, ?, ?)";
        PreparedStatement devices = connection.prepareStatement(deviceQuery);
        for (Device device : room.getDeviceList()) {

            devices.setInt(1, device.getID());
            devices.setDouble(2, device.getCircle().getCenterX());
            devices.setDouble(3, device.getCircle().getCenterY());
            devices.setInt(4, id);
            devices.executeUpdate();
        }
        devices.close();
        connection.close();
    }

    @Override
    public Room readRoom(String name) throws SQLException {
        Connection connection = ConnectionManager.getConnection();
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

            String deviceQuery = "SELECT networkID, x, y FROM device WHERE room_FK == ?;";
            PreparedStatement devices = connection.prepareStatement(deviceQuery);
            devices.setInt(1, roomData.getInt("id"));
            ResultSet deviceSet = devices.executeQuery();

            while (deviceSet.next()) {
                Device device = new Device(deviceSet.getInt("networkID"), deviceSet.getDouble("x"), deviceSet.getDouble("y"));
                room.addDevice(device);
            }
            devices.close();
        }
        getRoom.close();
        connection.close();
        return room;
    }

    @Override
    public void editRoom(Room room) {
        //TODO:Edit room
    }

    @Override
    public boolean hasRoomData(String name) {
        PreparedStatement dataStatement;
        try (Connection conn = ConnectionManager.getConnection()) {
            String dataQuery = "SELECT Count(room.id) from room inner join recording r on room.id = r.room_FK where roomName = ?;";
            dataStatement = conn.prepareStatement(dataQuery);
            dataStatement.setString(1, name);
            ResultSet resultSet = dataStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(0) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void deleteRoom(String name) {
        PreparedStatement deleteStatement;
        try (Connection conn = ConnectionManager.getConnection()) {
/*            String dataQuery = "SELECT Count(room.id) from room inner join recording r on room.id = r.room_FK where roomName = ?;";*/
            String dataQuery = "delete from room where roomName = ?";
            deleteStatement = conn.prepareStatement(dataQuery);
            deleteStatement.setString(1, name);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public boolean isConnected() {
        if (uartReader == null) {
            return false;
        }
        return uartReader.getReaderState() == UartReader.State.CONNECTED;
    }


    public boolean isFailed() {
        return uartReader.getReaderState() == UartReader.State.FAILED;
    }

    public boolean isStopped() {
        return uartReader.getReaderState() == UartReader.State.ENDED;
    }
}
