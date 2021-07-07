package informatikprojekt.zigbee.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {
    private static List<Connection> connectionList = new ArrayList<>();

    private ConnectionManager() {

    }

    public static void stopConnections(){
        for(Connection con : connectionList){
            try {
                if(!con.isClosed()) {
                    con.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:sqlite:zigbee.sqlite");
        connectionList.add(con);
        return con;
    }

    public static void firstRun() {
        try {
            String createQuery = """
                    create table if not exists dataset
                    (
                    \tid integer
                    \t\tprimary key,
                    \trecordedAt datetime(4) not null
                    );

                    create table if not exists room
                    (
                    \tid integer
                    \t\tconstraint room_pk
                    \t\t\tprimary key,
                    \troomName varchar(50) not null,
                    \tcreated datetime
                    );

                    create table if not exists device
                    (
                    \tid int
                    \t\tconstraint device_pk
                    \t\t\tprimary key,
                    \tnetworkID int,
                    \tx double(30),
                    \ty double(30),
                    \troom_FK int
                    \t\treferences room
                    \t\t\ton update cascade on delete cascade
                    );

                    create table if not exists data
                    (
                    \tdataID integer
                    \t\tprimary key,
                    \tdataSetID integer
                    \t\treferences dataset,
                    \tsensor varchar(50) default 'Unknown',
                    \tdataType varchar(50) default 'Unknown',
                    \tdataValue float,
                    \tdevice_FK int
                    \t\treferences device
                    \t\t\ton update cascade on delete cascade
                    );

                    create table if not exists roomPoints
                    (
                    \tid integer
                    \t\tconstraint roomPoints_pk
                    \t\t\tprimary key,
                    \tx double(30),
                    \ty double(30),
                    \troomID_FK int
                    \t\treferences room
                    \t\t\ton update cascade on delete cascade
                    );

                    create table if not exists graph
                    (
                    \troomPoint_FK int
                    \t\treferences roomPoints
                    \t\t\ton update cascade on delete cascade,
                    \tconnected_FK int
                    \t\treferences roomPoints
                    \t\t\ton update cascade on delete cascade,
                    \tconstraint graph_pk
                    \t\tprimary key (roomPoint_FK, connected_FK)
                    );

                    """;
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
