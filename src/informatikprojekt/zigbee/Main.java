package informatikprojekt.zigbee;

import informatikprojekt.zigbee.backend.UartReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("frontend/sample.fxml")));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        UartReader uartReader = UartReader.getInstance();



    }


    public static void main(String[] args) {
        launch(args);
    }
}
