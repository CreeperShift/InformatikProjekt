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

import java.util.Objects;

public class Main extends Application {

    public static Scene s;
    public static BorderPane root;
    public static boolean isConnected = false;
    public static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
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
