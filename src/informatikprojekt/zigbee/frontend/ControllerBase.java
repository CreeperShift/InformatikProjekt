package informatikprojekt.zigbee.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ControllerBase implements Initializable {


    public Button btnRoom;
    public Button btnStart;
    public Button btnGraph;
    public Button btnData;
    public VBox sidePanel;

    private List<Button> dataButtonList;

    private Button[] allButtons;

    public AnchorPane contentPanel;
    public AnchorPane graphPanel;
    public AnchorPane dataPanel;
    public VBox contentStart;
    VBox content;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        allButtons = new Button[]{btnRoom, btnStart, btnData, btnGraph};

        addDataButtons();

        setButtonActive(btnStart);

        try {

            content = (VBox) FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/room.fxml")));
            graphPanel = (AnchorPane) FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/graph.fxml")));
            dataPanel = (AnchorPane) FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("fxml/data.fxml")));

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


    public void onButtonRoom(ActionEvent actionEvent) {
        setButtonActive(btnRoom);
        clearDataButtons();
        contentPanel.getChildren().clear();
        content.setPrefHeight(contentPanel.getPrefHeight());
        content.setPrefWidth(contentPanel.getPrefWidth());
        content.setMinWidth(contentPanel.getWidth());
        contentPanel.getChildren().add(content);
    }

    public void onButtonStart(ActionEvent actionEvent) {
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
}
