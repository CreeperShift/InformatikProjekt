package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.Main;
import informatikprojekt.zigbee.backend.Data;
import informatikprojekt.zigbee.backend.DataManager;
import informatikprojekt.zigbee.backend.Room;
import informatikprojekt.zigbee.util.CommonUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
    public TextArea consoleOut;
    public CheckBox autoScroll;
    public Button btnNewRoom;
    public TextField txtRoomName;
    public Button btnOverview;
    public Button btnRoomEdit;
    public Label txtDatensatz;
    private boolean isConnected = false;
    public static ControllerBase INSTANCE;
    private Room currentRoom;

    private static Window activeWindow = Window.START;

    private Button[] allButtons;

    public AnchorPane contentPanel;
    public AnchorPane graphPanel;
    public BorderPane dataPanel;
    public VBox createRoom;
    public VBox contentStart;
    public VBox content;
    public VBox overview;
    public static TextArea textConsole;
    public static CheckBox checkStayConsole;

    /*
    Contains the latest data as read by the UART reader.
     */
    private static List<Data> latestData = new ArrayList<>();

    public static void setDataList(List<Data> data) {
        latestData = data;
    }

    /*
    Returns a read only view of the latest data.
     */
    public static List<Data> getDataList() {
        return Collections.unmodifiableList(latestData);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void onAutoScroll(ActionEvent actionEvent) {
    }

    public void onBtnNewRoom(ActionEvent actionEvent) {
        if (!txtRoomName.getText().isBlank()) {
            if (DataManager.get().existRoom(txtRoomName.getText())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Raum");
                alert.setHeaderText("Raum existiert bereits!");
                alert.setContentText("Bitte neuen Namen wählen oder den Raum laden.");
                alert.showAndWait();
            } else {
                setActiveWindow(Window.CREATEROOM);
                currentRoom = new Room(txtRoomName.getText());
                txtDatensatz.setText("Aktueller Raum: " + currentRoom.getName());
                btnConnect.setDisable(false);
                btnRoom.setDisable(false);
                btnRoomEdit.setDisable(false);
                contentPanel.getChildren().clear();
                createRoom.setPrefHeight(contentPanel.getPrefHeight());
                createRoom.setPrefWidth(contentPanel.getPrefWidth());
                createRoom.setMinWidth(contentPanel.getWidth());
                contentPanel.getChildren().add(createRoom);
                ControllerRoom.get().setRoom(currentRoom);
            }
        }
    }

    public void onBtnLoadRoom(ActionEvent actionEvent) {
        if (!txtRoomName.getText().isBlank()) {
            if (DataManager.get().existRoom(txtRoomName.getText())) {
                try {
                    currentRoom = DataManager.get().readRoom(txtRoomName.getText());
                    btnRoomEdit.setDisable(false);
                    txtDatensatz.setText("Aktueller Raum: " + currentRoom.getName());
                    btnConnect.setDisable(false);
                    btnRoom.setDisable(false);
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Raum nicht gefunden");
                alert.setHeaderText("Es existiert kein Raum mit diesem Name.");
                alert.showAndWait();
            }
        }

    }

    public void onBtnRoomEdit(ActionEvent actionEvent) {
        if (!txtRoomName.getText().isBlank()) {
            setActiveWindow(Window.CREATEROOM);
            contentPanel.getChildren().clear();
            createRoom.setPrefHeight(contentPanel.getPrefHeight());
            createRoom.setPrefWidth(contentPanel.getPrefWidth());
            createRoom.setMinWidth(contentPanel.getWidth());
            contentPanel.getChildren().add(createRoom);
            ControllerRoom.get().setRoom(currentRoom);
        }
    }


    public enum Window {
        START, ROOM, GRAPH, DATA, CREATEROOM, OVERVIEW;
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
        allButtons = new Button[]{btnRoom, btnStart, btnData, btnGraph, btnOverview};
        contentPanel.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        if (Main.dev) {
            for (Button b : allButtons) {
                b.setDisable(false);
            }
            btnConnect.setDisable(false);
        }

        setButtonActive(btnStart);

        try {

            content = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/view.fxml")));
            createRoom = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/room.fxml")));
            graphPanel = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/graph.fxml")));
            dataPanel = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/data.fxml")));
            overview = FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/overview.fxml")));

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
        if (currentRoom != null) {
            ControllerRoomView.INSTANCE.addRoom(currentRoom);
        }
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

    public void onBtnOverview(ActionEvent actionEvent) {
        setActiveWindow(Window.OVERVIEW);
        setButtonActive(btnOverview);
        overview.setPrefHeight(contentPanel.getPrefHeight());
        overview.setPrefWidth(contentPanel.getPrefWidth());
        overview.setMinWidth(contentPanel.getWidth());
        overview.setMinHeight(contentPanel.getHeight());
        contentPanel.getChildren().clear();
        contentPanel.getChildren().add(overview);
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
                        });
                        t1.cancel();
                    } else if (DataManager.get().isFailed()) {
                        ledStatusNavbar.setFill(Color.RED);
                        CommonUtils.consoleString("Error while connecting. See terminal for more info.");
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
                            ledStatusNavbar.setFill(Color.RED);
                            isConnected = false;
                            CommonUtils.consoleString("Disconnected.");
                        });
                        t1.cancel();
                    }
                }
            }, 100, 100);
        }
    }

}
