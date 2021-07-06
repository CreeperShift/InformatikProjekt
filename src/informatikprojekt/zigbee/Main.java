package informatikprojekt.zigbee;

import informatikprojekt.zigbee.backend.DataManager;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.sql.*;
import java.util.Objects;

public class Main extends Application {

    public static Scene s;
    public static BorderPane root;
    public static boolean isConnected = false;
    public static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        setupDatabaseLite();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("frontend/fxml/baseLayout.fxml")));
        primaryStage.setTitle("Luftqualität in Innenräumen");
        s = new Scene(root, 1290, 800);
        s.getStylesheets().add("informatikprojekt/zigbee/frontend/fxml/frontend.css");
        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(s);
        mainStage = primaryStage;
        primaryStage.setScene(s);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void setupDatabaseLite() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        String createQuery = """
                create table if not exists dataset
                (
                \tid integer
                \t\tprimary key,
                \trecordedAt datetime(4) not null
                );

                create table if not exists room
                (
                \tid int
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
                \tx float,
                \ty float,
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
                \tid int
                \t\tconstraint roomPoints_pk
                \t\t\tprimary key,
                \tx float,
                \ty float,
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
        Connection connection = DriverManager.getConnection("jdbc:sqlite:zigbee.sqlite");

        Statement statement = connection.createStatement();
        statement.executeUpdate(createQuery);

        statement.close();
        connection.close();

    }

    @Override
    public void stop() {
        DataManager.get().stopReader();
        CommonUtils.stopAllTimers();
        DataManager.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }


}
