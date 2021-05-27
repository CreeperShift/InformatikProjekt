package informatikprojekt.zigbee;

import informatikprojekt.zigbee.backend.MySQLAccess;
import informatikprojekt.zigbee.backend.SensorData;
import informatikprojekt.zigbee.backend.UartReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("frontend/sample.fxml")));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        UartReader uartReader = UartReader.getInstance();

        while (true) {
            if (!uartReader.getQueue().isEmpty()) {
                SensorData current = uartReader.getQueue().take();
                DateTimeFormatter form = DateTimeFormatter.ofPattern("HH:mm:ss");
                System.out.println("SensorID: " + current.getId() + " | Data: " + current.getData() + " | Timestamp: " + current.getDate().format(form));
                MySQLAccess.getINSTANCE().writeData(current);
            }
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}
