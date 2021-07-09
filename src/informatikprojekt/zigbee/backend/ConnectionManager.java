package informatikprojekt.zigbee.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {
    private static List<Connection> connectionList = new ArrayList<>();
    private static Connection conn;

    private ConnectionManager() {

    }

    public static void stopConnections() {
        for (Connection con : connectionList) {
            try {
                if (!con.isClosed()) {
                    con.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static Connection getConnection() throws SQLException {


        if (conn == null) {
            conn = DriverManager.getConnection("jdbc:sqlite:zigbee.sqlite");
            connectionList.add(conn);
        }

        if (conn.isClosed()) {
            conn = DriverManager.getConnection("jdbc:sqlite:zigbee.sqlite");
        }

        return conn;
    }

    public static void firstRun() {
        try {
            String createQuery = "create table if not exists room\n" +
                    "(\n" +
                    "\tid integer\n" +
                    "\t\tconstraint room_pk\n" +
                    "\t\t\tprimary key,\n" +
                    "\troomName varchar(50) not null,\n" +
                    "\tcreated datetime\n" +
                    ");\n" +
                    "\n" +
                    "create table if not exists device\n" +
                    "(\n" +
                    "\tid integer\n" +
                    "\t\tconstraint device_pk\n" +
                    "\t\t\tprimary key,\n" +
                    "\tnetworkID int,\n" +
                    "\tx double(30),\n" +
                    "\ty double(30),\n" +
                    "\troom_FK int\n" +
                    "\t\treferences room\n" +
                    "\t\t\ton update cascade on delete cascade\n" +
                    ");\n" +
                    "\n" +
                    "create table if not exists recording\n" +
                    "(\n" +
                    "\tid integer\n" +
                    "\t\tconstraint recording_pk\n" +
                    "\t\t\tprimary key,\n" +
                    "\troom_FK int\n" +
                    "\t\treferences room\n" +
                    "\t\t\ton update cascade on delete cascade,\n" +
                    "\tdateStarted date,\n" +
                    "\ttimeStarted time\n" +
                    ");\n" +
                    "\n" +
                    "create table if not exists dataset\n" +
                    "(\n" +
                    "\tid integer\n" +
                    "\t\tprimary key,\n" +
                    "\tdateRecorded date,\n" +
                    "\ttimeRecorded time,\n" +
                    "\trecording_FK int\n" +
                    "\t\treferences recording\n" +
                    "\t\t\ton update cascade on delete cascade\n" +
                    ");\n" +
                    "\n" +
                    "create table if not exists data\n" +
                    "(\n" +
                    "\tdataID integer\n" +
                    "\t\tprimary key,\n" +
                    "\tdataSetID integer\n" +
                    "\t\treferences dataset,\n" +
                    "\tsensor varchar(50) default 'Unknown',\n" +
                    "\tdataType varchar(50) default 'Unknown',\n" +
                    "\tdataValue float,\n" +
                    "\tdevice_FK int\n" +
                    "\t\treferences device\n" +
                    "\t\t\ton update cascade on delete cascade\n" +
                    ");\n" +
                    "\n" +
                    "create unique index  if not exists room_roomName_uindex\n" +
                    "\ton room (roomName);\n" +
                    "\n" +
                    "create table if not exists roomPoints\n" +
                    "(\n" +
                    "\tid integer\n" +
                    "\t\tconstraint roomPoints_pk\n" +
                    "\t\t\tprimary key,\n" +
                    "\tx double(30),\n" +
                    "\ty double(30),\n" +
                    "\troomID_FK int\n" +
                    "\t\treferences room\n" +
                    "\t\t\ton update cascade on delete cascade\n" +
                    ");\n" +
                    "\n" +
                    "create table if not exists graph\n" +
                    "(\n" +
                    "\troomPoint_FK int\n" +
                    "\t\treferences roomPoints\n" +
                    "\t\t\ton update cascade on delete cascade,\n" +
                    "\tconnected_FK int\n" +
                    "\t\treferences roomPoints\n" +
                    "\t\t\ton update cascade on delete cascade,\n" +
                    "\tconstraint graph_pk\n" +
                    "\t\tprimary key (roomPoint_FK, connected_FK)\n" +
                    ");\n" +
                    "\n";
            Connection connection = ConnectionManager.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate(createQuery);


            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}
