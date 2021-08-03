package informatikprojekt.zigbee.backend;

import informatikprojekt.zigbee.frontend.Device;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DataManager implements IData {

    private final LinkedList<DataSet> dataSets = new LinkedList<>();
    private static UartReader uartReader;
    private String port = "COM1";
    private int currentRecording = -1;

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

    public void startReader(String roomName) {

        if (uartReader == null || isStopped() || isFailed()) {
            uartReader = new UartReader(port, roomName);
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
    public Map<String, Float> getDailyCalcForType(String type, String calc) {
        SortedMap<String, Float> valueList = new TreeMap<>();
        String query;


        switch (calc) {
            default -> query = "select dataType, round(AVG(dataValue), 2) as mean,\n" +
                    "       strftime('%d', timeRecorded) as Day,\n" +
                    "       strftime('%m', timeRecorded) as Month,\n" +
                    "       strftime('%Y', timeRecorded) as Year\n" +
                    "from data\n" +
                    "    inner join dataset d on data.dataSetID = d.id\n" +
                    "    inner join recording r on d.recording_FK = r.id\n" +
                    "where r.id = ? and dataType=?\n" +
                    "group by strftime('%Y', timeRecorded),\n" +
                    "         strftime('%m', timeRecorded),\n" +
                    "         strftime('%d', timeRecorded)" +
                    " order by Year, Month, Day;";
            case "Min" -> query = "select dataType, round(min(dataValue), 2) as mean,\n" +
                    "       strftime('%d', timeRecorded) as Day,\n" +
                    "       strftime('%m', timeRecorded) as Month,\n" +
                    "       strftime('%Y', timeRecorded) as Year\n" +
                    "from data\n" +
                    "    inner join dataset d on data.dataSetID = d.id\n" +
                    "    inner join recording r on d.recording_FK = r.id\n" +
                    "where r.id = ? and dataType=?\n" +
                    "group by strftime('%Y', timeRecorded),\n" +
                    "         strftime('%m', timeRecorded),\n" +
                    "         strftime('%d', timeRecorded) " +
                    " order by Year, Month, Day;";
            case "Max" -> query = "select dataType, round(max(dataValue), 2) as mean,\n" +
                    "       strftime('%d', timeRecorded) as Day,\n" +
                    "       strftime('%m', timeRecorded) as Month,\n" +
                    "       strftime('%Y', timeRecorded) as Year\n" +
                    "from data\n" +
                    "    inner join dataset d on data.dataSetID = d.id\n" +
                    "    inner join recording r on d.recording_FK = r.id\n" +
                    "where r.id = ? and dataType=?\n" +
                    "group by strftime('%Y', timeRecorded),\n" +
                    "         strftime('%m', timeRecorded),\n" +
                    "         strftime('%d', timeRecorded)" +
                    " order by Year, Month, Day;";
        }


        try (Connection connection = ConnectionManager.getConnection()) {


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, currentRecording);
            preparedStatement.setString(2, type);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String date = "";
                String day = resultSet.getString("Day");
                String month = resultSet.getString("Month");
                date = day + "-" + month;
                valueList.put(date, resultSet.getFloat("mean"));
            }
            return valueList;

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return valueList;
    }

    @Override
    public List<SQLData> getStandardDeviationForType(String type) {
        return null;
    }

    @Override
    public List<String> getAllDataTypes() throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        List<String> allDataTypes = new LinkedList<>();
        String getDataTypes = "select distinct dataType from data inner join dataset d on data.dataSetID = d.id inner join recording r on d.recording_FK = r.id where r.id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(getDataTypes);
        preparedStatement.setInt(1, currentRecording);
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
    public List<ExportData> getExportListForRoom(String name) {

        List<ExportData> exportDataList = new LinkedList<>();

        try (Connection connection = ConnectionManager.getConnection()) {
            String query;

            query = "select date(timeStarted) as RecordDate, time(timeStarted) as RecordTime, date(timeRecorded) as Date, time(timeRecorded) as Time,\n" +
                    "       sensor, dataType, networkID, dataValue\n" +
                    "from dataset\n" +
                    "    inner join recording r on r.id = dataset.recording_FK inner join data d on dataset.id = d.dataSetID inner join device d2 on d2.id = d.device_FK\n" +
                    "inner join room r2 on r2.id = r.room_FK where roomName = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String recordTime = resultSet.getString("RecordTime");
                String recordDate = resultSet.getString("RecordDate");
                String date = resultSet.getString("Date");
                String time = resultSet.getString("Time");
                String sensor = resultSet.getString("sensor");
                String type = resultSet.getString("dataType");
                String id = resultSet.getString("networkID");
                String value = resultSet.getString("dataValue");

                ExportData exportData = new ExportData(recordDate, recordTime, date, time, sensor, type, id, value);
                exportDataList.add(exportData);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return exportDataList;
    }

    @Override
    public List<ExportData> getExportList() {

        List<ExportData> exportDataList = new LinkedList<>();

        try (Connection connection = ConnectionManager.getConnection()) {
            String query;

            query = "select date(timeRecorded) as Date, time(timeRecorded) as Time,\n" +
                    "       sensor, dataType, networkID, dataValue\n" +
                    "from dataset\n" +
                    "    inner join recording r on r.id = dataset.recording_FK inner join data d on dataset.id = d.dataSetID inner join device d2 on d2.id = d.device_FK where recording_FK = ?;\n";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, currentRecording);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String date = resultSet.getString("Date");
                String time = resultSet.getString("Time");
                String sensor = resultSet.getString("sensor");
                String type = resultSet.getString("dataType");
                String id = resultSet.getString("networkID");
                String value = resultSet.getString("dataValue");

                ExportData exportData = new ExportData("", "", date, time, sensor, type, id, value);
                exportDataList.add(exportData);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return exportDataList;
    }

    @Override
    public float get15MinAverage(String type) {

        int id = getCurrentRecordID();

        try (Connection connection = ConnectionManager.getConnection()) {

            String minQuery = "select avg(dataValue)\n" +
                    "from data inner join dataset d on d.id = data.dataSetID\n" +
                    "    inner join recording on d.recording_FK = recording.id\n" +
                    "where recording.id = ?\n" +
                    "  AND dataType = ? AND DATETIME(d.timeRecorded) >= (datetime('now', 'localtime', '-15 minutes'))\n" +
                    "group by dataType;";
            PreparedStatement getMinAverage = connection.prepareStatement(minQuery);
            getMinAverage.setInt(1, id);
            getMinAverage.setString(2, type);
            ResultSet resultSet = getMinAverage.executeQuery();
            if (resultSet.next()) {
                return resultSet.getFloat(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
        createRoom.setString(2, CommonUtils.formatTime(room.getCreated()));
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
    public boolean hasRoomData(String name) {
        PreparedStatement dataStatement;
        try (Connection conn = ConnectionManager.getConnection()) {
            String dataQuery = "SELECT Count(room.id) from room inner join recording r on room.id = r.room_FK where roomName = ?;";
            dataStatement = conn.prepareStatement(dataQuery);
            dataStatement.setString(1, name);
            ResultSet resultSet = dataStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
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
            String dataQuery = "delete from room where roomName = ?";
            deleteStatement = conn.prepareStatement(dataQuery);
            deleteStatement.setString(1, name);
            deleteStatement.executeUpdate();
            deleteStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentRecordID() {
        try (Connection connection = ConnectionManager.getConnection()) {
            String query = "select max(id) from recording;";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                resultSet.close();
                statement.close();
                return id;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void setCurrentRecording() {
        Timer t1 = new Timer();
        CommonUtils.registerTimer(t1);
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                if (uartReader.getReaderState() == UartReader.State.FAILED) {
                    t1.cancel();
                } else if (uartReader.isCreatedRecording()) {
                    currentRecording = getCurrentRecordID();
                    t1.cancel();
                }
            }
        }, 500, 500);

    }

    @Override
    public List<String> getRecordingsForRoom(String room) {

        List<String> recordings = new LinkedList<>();

        try (Connection connection = ConnectionManager.getConnection()) {
            String query = "select timeStarted from recording inner join room r on recording.room_FK = r.id where roomName == ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, room);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String timedate = resultSet.getString(1);

                recordings.add(timedate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recordings;
    }

    @Override
    public void loadRecording(String time) {
        try (Connection connection = ConnectionManager.getConnection()) {
            String query = "select id from recording where timeStarted = ?;";
            PreparedStatement prep = connection.prepareStatement(query);

            prep.setString(1, time);

            ResultSet resultSet = prep.executeQuery();

            if (resultSet.next()) {
                currentRecording = resultSet.getInt(1);
            }

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
