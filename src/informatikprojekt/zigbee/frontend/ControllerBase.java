package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.backend.AmpelLed;
import informatikprojekt.zigbee.backend.DataManager;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ControllerBase implements Initializable {


    public Button btnRoom;
    public Button btnStart;
    public Button btnGraph;
    public Button btnData;
    public VBox sidePanel;
    public Circle ledStatusNavbar;
    public Circle ledStatusModulEins;
    public Circle ledStatusModulZwei;
    public Circle ledStatusModulDrei;
    public Circle ledStatusModulVier;
    public Circle ledStatus;
    public TextField fieldPort;
    public Button btnConnect;
    public TextArea consoleOut;
    public CheckBox autoScroll;
    private boolean isConnected = false;
    public static ControllerBase INSTANCE;
    private boolean setupDone = false;
    private static Window activeWindow = Window.START;

    private Button[] allButtons;

    public AnchorPane contentPanel;
    public AnchorPane graphPanel;
    public BorderPane dataPanel;
    public VBox contentStart;
    public VBox content;
    public static TextArea textConsole;
    public static CheckBox checkStayConsole;
    private boolean ModEinsStop =false;
    private boolean ModZweiStop =false;
    private boolean ModDreiStop =false;
    public boolean isConnected() {
        return isConnected;
    }

    public void onAutoScroll(ActionEvent actionEvent) {
    }

    public enum Window {
        START, ROOM, GRAPH, DATA;
    }


    public static Window getActiveWindow() {
        return activeWindow;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textConsole = consoleOut;
        checkStayConsole = autoScroll;
        ledStatus.fillProperty().bindBidirectional(ledStatusNavbar.fillProperty());

        INSTANCE = this;
        allButtons = new Button[]{btnRoom, btnStart, btnData, btnGraph};
        contentPanel.getStyleClass().add(JMetroStyleClass.BACKGROUND);


        setButtonActive(btnStart);

        try {

            content = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/room.fxml")));
            graphPanel = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/graph.fxml")));
            dataPanel = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/data.fxml")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setActiveWindow(Window window) {
        activeWindow = window;
    }





    public void onButtonRoom(ActionEvent actionEvent) {
        setActiveWindow(Window.ROOM);
        setButtonActive(btnRoom);
        contentPanel.getChildren().clear();
        content.setPrefHeight(contentPanel.getPrefHeight());
        content.setPrefWidth(contentPanel.getPrefWidth());
        content.setMinWidth(contentPanel.getWidth());
        contentPanel.getChildren().add(content);
    }

    public void onButtonStart(ActionEvent actionEvent) {
        setActiveWindow(Window.START);
        setButtonActive(btnStart);
        contentPanel.getChildren().clear();
        contentPanel.getChildren().add(contentStart);


    }


    private void setButtonActive(Button button) {

        Arrays.stream(allButtons).forEach(b -> b.setStyle(null));

        button.setStyle("-fx-background-color: #728fa3");
    }

    public void onButtonGraph(ActionEvent actionEvent) {
        setActiveWindow(Window.GRAPH);

        setButtonActive(btnGraph);
        graphPanel.setPrefHeight(contentPanel.getPrefHeight());
        graphPanel.setPrefWidth(contentPanel.getPrefWidth());
        graphPanel.setMinWidth(contentPanel.getWidth());
        graphPanel.setMinHeight(contentPanel.getHeight());
        contentPanel.getChildren().clear();
        contentPanel.getChildren().add(graphPanel);
    }

    public void onButtonData(ActionEvent actionEvent) {
        setActiveWindow(Window.DATA);

        setButtonActive(btnData);

        dataPanel.setPrefHeight(contentPanel.getPrefHeight());
        dataPanel.setPrefWidth(contentPanel.getPrefWidth());
        dataPanel.setMinWidth(contentPanel.getWidth());
        dataPanel.setMinHeight(contentPanel.getHeight());
        contentPanel.getChildren().clear();
        contentPanel.getChildren().add(dataPanel);


    }


    public void onBtnConnect(ActionEvent actionEvent) {

        if (btnConnect.getText().equalsIgnoreCase("Verbinden")) {


            DataManager.get().setPort(fieldPort.getText());
            DataManager.get().startReader();
            Timer t1 = new Timer();
            AmpelLed amp = new AmpelLed();

            t1.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (DataManager.get().isConnected()) {
                        Platform.runLater(() -> {
                            btnConnect.setText("Stop");
                            isConnected = true;
                            ledStatusNavbar.setFill(Color.GREEN);
                            ControllerGraph.INSTANCE.setupData();
                            ControllerData.INSTANCE.setupData();
                            CommonUtils.consoleString("Connected.");

                           modulEins();
                           modulZwei();
                           modulDrei();


                        });
                        t1.cancel();
                    } else if (DataManager.get().isFailed()) {
                        ledStatusNavbar.setFill(Color.RED);
                        ledStatusModulEins.setFill(Color.RED);
                        ledStatusModulDrei.setFill(Color.RED);
                        ledStatusModulZwei.setFill(Color.RED);
                        CommonUtils.consoleString("Error while connecting. See terminal for more info.");
                        t1.cancel();
                        ModEinsStop=true;
                        ModZweiStop=true;
                        ModDreiStop=true;

                    }





                }
            }, 100, 100);

        } else {
            DataManager.get().stopReader();
            Timer t1 = new Timer();
            t1.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (DataManager.get().isStopped()) {
                        Platform.runLater(() -> {
                            btnConnect.setText("Verbinden");
                            ledStatusNavbar.setFill(Color.RED);
                            ledStatusModulEins.setFill(Color.RED);
                            ledStatusModulDrei.setFill(Color.RED);
                            ledStatusModulZwei.setFill(Color.RED);
                            isConnected = false;
                            CommonUtils.consoleString("Disconnected.");
                        });
                        t1.cancel();

                        ModEinsStop=true;
                        ModZweiStop=true;
                        ModDreiStop=true;
                    }
                }
            }, 100, 100);
        }









    }


public void modulEins() {
    Timer t2 = new Timer();


    t2.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {

            if (ModEinsStop==false){
                //System.out.println(DataManager.get().getModulEins() + " TEST");

                ledStatusModulEins.setFill(Color.GREEN);

            }else {
                t2.cancel();

                ledStatusModulEins.setFill(Color.RED);


            }
            ModEinsStop=false;

        }
    },500,2000);

}

    public void modulZwei() {
        Timer t2 = new Timer();


        t2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (ModZweiStop==false){
                    //System.out.println(DataManager.get().getModulEins() + " TEST");

                    ledStatusModulZwei.setFill(Color.GREEN);

                }else {
                    t2.cancel();

                    ledStatusModulZwei.setFill(Color.RED);


                }
                ModZweiStop=false;

            }
        },500,2000);

    }

    public void modulDrei() {
        Timer t4 = new Timer();


        t4.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (ModDreiStop==false){
                    //System.out.println(DataManager.get().getModulEins() + " TEST");

                    ledStatusModulDrei.setFill(Color.GREEN);

                }else {
                    t4.cancel();

                    ledStatusModulDrei.setFill(Color.RED);


                }
                ModDreiStop=false;

            }
        },500,2000);

    }


}
