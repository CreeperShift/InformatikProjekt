package informatikprojekt.zigbee;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static Scene s;
    public static Stage dialog;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("frontend/sample.fxml")));
        primaryStage.setTitle("Luftqualität in Innenräumen");
        s = new Scene(root, 1280, 800);
        primaryStage.setScene(s);
        primaryStage.setResizable(false);
        primaryStage.show();

        dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        Parent pop = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("frontend/popup.fxml")));
        Scene dialogScene = new Scene(pop, 200, 100);
        dialog.setScene(dialogScene);
        dialog.setResizable(false);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
