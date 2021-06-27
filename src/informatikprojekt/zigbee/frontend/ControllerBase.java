package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.backend.DataManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
    public Circle ledStatus;
    public TextField fieldPort;
    public Button btnConnect;
    private boolean isConnected = false;
    public static ControllerBase INSTANCE;

    private static Window activeWindow = Window.START;

    private List<Button> dataButtonList;

    private Button[] allButtons;

    public AnchorPane contentPanel;
    public AnchorPane graphPanel;
    public TableView dataPanel;
    public VBox contentStart;
    VBox content;

    public boolean isConnected() {
        return isConnected;
    }

    public enum Window {
        START, ROOM, GRAPH, DATA;
    }

    public static Window getActiveWindow() {
        return activeWindow;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        INSTANCE = this;
        allButtons = new Button[]{btnRoom, btnStart, btnData, btnGraph};
        contentPanel.getStyleClass().add(JMetroStyleClass.BACKGROUND);


        addDataButtons();

        setButtonActive(btnStart);

        try {

            content = (VBox) FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/room.fxml")));
            graphPanel = (AnchorPane) FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/graph.fxml")));
            dataPanel = (TableView) FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/data.fxml")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addDataButtons() {
        dataButtonList = new LinkedList<>();
        dataButtonList.add(new Button("Temperatur"));
        dataButtonList.add(new Button("Feuchtigkeit"));
        dataButtonList.add(new Button("CO2"));
        dataButtonList.add(new Button("VoC"));

        dataButtonList.forEach(b -> {
            b.getStylesheets().add("informatikprojekt/zigbee/frontend/fxml/frontend.css");
            b.getStyleClass().add("navbarButton");
            b.setPrefWidth(167);
            b.setPrefHeight(42);
        });

    }

    private void setActiveWindow(Window window) {
        activeWindow = window;
    }


    public void onButtonRoom(ActionEvent actionEvent) {
        setActiveWindow(Window.ROOM);
        setButtonActive(btnRoom);
        clearDataButtons();
        contentPanel.getChildren().clear();
        content.setPrefHeight(contentPanel.getPrefHeight());
        content.setPrefWidth(contentPanel.getPrefWidth());
        content.setMinWidth(contentPanel.getWidth());
        contentPanel.getChildren().add(content);
    }

    public void onButtonStart(ActionEvent actionEvent) {
        setActiveWindow(Window.START);
        setButtonActive(btnStart);
        clearDataButtons();
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
        clearDataButtons();
        graphPanel.setPrefHeight(contentPanel.getPrefHeight());
        graphPanel.setPrefWidth(contentPanel.getPrefWidth());
        graphPanel.setMinWidth(contentPanel.getWidth());
        graphPanel.setMinHeight(contentPanel.getHeight());
        contentPanel.getChildren().clear();
        contentPanel.getChildren().add(graphPanel);
    }

    public void onButtonData(ActionEvent actionEvent) {
        setActiveWindow(Window.DATA);
        ControllerData.INSTANCE.setupData();
        setButtonActive(btnData);
        clearDataButtons();

        if (!sidePanel.getChildren().contains(dataButtonList.get(0))) {
            sidePanel.getChildren().addAll(dataButtonList);
        }
        dataPanel.setPrefHeight(contentPanel.getPrefHeight());
        dataPanel.setPrefWidth(contentPanel.getPrefWidth());
        dataPanel.setMinWidth(contentPanel.getWidth());
        dataPanel.setMinHeight(contentPanel.getHeight());
        contentPanel.getChildren().clear();
        contentPanel.getChildren().add(dataPanel);


    }

    private void clearDataButtons() {
        sidePanel.getChildren().removeAll(dataButtonList);
    }

    public void onDataHover(MouseEvent mouseEvent) {
    }

    public void onBtnConnect(ActionEvent actionEvent) {

        if (btnConnect.getText().equalsIgnoreCase("Verbinden")) {

            DataManager.get().setPort("COM5");
            DataManager.get().startReader();
            Timer t1 = new Timer();
            t1.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (DataManager.get().isConnected()) {
                        Platform.runLater(() -> {
                            btnConnect.setText("Stop");
                            isConnected = true;
                        });
                        t1.cancel();
                    } else if (DataManager.get().isFailed()) {
                        System.out.println("Cancelled");
                        t1.cancel();
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
                            isConnected = false;
                        });
                        t1.cancel();
                    }
                }
            }, 100, 100);
        }
    }

}
