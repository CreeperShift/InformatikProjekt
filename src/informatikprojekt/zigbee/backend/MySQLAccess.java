package informatikprojekt.zigbee.backend;

import java.sql.*;

public class MySQLAccess {

    private Connection connect;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private static MySQLAccess INSTANCE;

    public static MySQLAccess getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new MySQLAccess();
        }
        return INSTANCE;
    }

    private MySQLAccess() {
        readDataBase();
    }



    public void readDataBase(){
        try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost/luftqualitaet?" + "user=zigbee&password=zigbee");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void writeData(SensorData sensorData) throws SQLException {
        preparedStatement = connect.prepareStatement("insert into luftqualitaet.datasets values (?, ? , ?)");
        preparedStatement.setTimestamp(1, Timestamp.valueOf(sensorData.getDate()));
        preparedStatement.setFloat(2, sensorData.getData());
        preparedStatement.setInt(3, 2);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }


}
