package informatikprojekt.zigbee;

import informatikprojekt.zigbee.backend.DataManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static Scene s;
    public static BorderPane root;

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("frontend/fxml/baseLayout.fxml")));
        primaryStage.setTitle("Luftqualität in Innenräumen");
        s = new Scene(root, 1280, 800);
        s.getStylesheets().add("informatikprojekt/zigbee/frontend/fxml/frontend.css");
        primaryStage.setScene(s);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        DataManager.get().stopReader();
    }


    public static void main(String[] args) {
        launch(args);
    }


}
