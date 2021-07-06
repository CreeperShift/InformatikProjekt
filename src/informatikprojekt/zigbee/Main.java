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
        String createQuery = "create table IF NOT EXISTS dataset\n" +
                "(\n" +
                "    id         integer \n" +
                "        primary key,\n" +
                "    recordedAt datetime(4) not null\n" +
                ");\n" +
                "\n" +
                "create table IF NOT EXISTS data\n" +
                "(\n" +
                "    dataID    integer \n" +
                "        primary key,\n" +
                "    dataSetID integer                        null,\n" +
                "    sensor    varchar(50) default 'Unknown' null,\n" +
                "    dataType  varchar(50) default 'Unknown' null,\n" +
                "    dataValue float                         null,\n" +
                "    device    int                           null,\n" +
                "    constraint data_dataset_id_fk\n" +
                "        foreign key (dataSetID) references dataset (id)\n" +
                ");\n";
        Connection connection = DriverManager.getConnection("jdbc:sqlite:G:\\Code\\Uni\\zigbeeeeeeeee\\zigbee.sqlite");
        connection.setSchema("Zigbee");

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
