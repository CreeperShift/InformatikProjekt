package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.Main;
import informatikprojekt.zigbee.backend.UartReader;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    public AnchorPane chartAnchor;

    public LineChart<Number, Number> lineChart;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void onDragDetected(MouseEvent mouseEvent) {
        if (activeTool == TOOL_TYPE.WAND || activeTool == TOOL_TYPE.RECHTECK) {
            guiDrawingArea.startFullDrag();
            mouseEvent.consume();
        }
        if (activeTool == TOOL_TYPE.DELETE) {
            for (Node n : guiDrawingArea.getChildren()) {
                n.startFullDrag();
            }
            mouseEvent.consume();
        }
    }

    private boolean hasDrawn = false;

    public void drawGrid() {

        if (!hasDrawn) {
            hasDrawn = true;

            for (int x = 0; x < 1275; x += 25) {
                Line line = new Line();
                line.setStartX(x);
                line.setStartY(0);
                line.setEndY(800);
                line.setEndX(x);
                line.setStrokeWidth(2);
                line.setStroke(Color.rgb(153, 153, 153, 0.15));

                guiDrawingArea.getChildren().add(line);

            }
            for (int x = 0; x < 795; x += 25) {
                Line line = new Line();
                line.setStartY(x);
                line.setStartX(0);
                line.setEndX(1280);
                line.setEndY(x);
                line.setStrokeWidth(2);
                line.setStroke(Color.rgb(153, 153, 153, 0.15));

                guiDrawingArea.getChildren().add(line);
            }

            for (int x = 0; x < 1275; x += 100) {
                Line line = new Line();
                line.setStartX(x);
                line.setStartY(0);
                line.setEndY(800);
                line.setEndX(x);
                line.setStrokeWidth(2);
                line.setStroke(Color.rgb(0, 0, 51, 0.3));

                guiDrawingArea.getChildren().add(line);

            }
            for (int x = 0; x < 795; x += 100) {
                Line line = new Line();
                line.setStartY(x);
                line.setStartX(0);
                line.setEndX(1280);
                line.setEndY(x);
                line.setStrokeWidth(2);
                line.setStroke(Color.rgb(0, 0, 51, 0.3));

                guiDrawingArea.getChildren().add(line);
            }


        }


    }

    private Line currentLine;
    private Rectangle currentRect;
    private LinkedList<Node> undoActions = new LinkedList<>();

    public void onMouseClick(MouseEvent mouseEvent) {
        if (activeTool == TOOL_TYPE.DEVICE) {

            if (deviceMap.size() < 4) {

                boolean[] num = {false, false, false, false};

                for (Device d : deviceMap) {
                    num[d.getID()] = true;
                }
                int index = 0;
                for (; index < 4; index++) {
                    if (!num[index]) {
                        break;
                    }
                }


                Device device = new Device(index, mouseEvent.getX(), mouseEvent.getY());
                deviceMap.add(device);

                guiDrawingArea.getChildren().add(device.getImageView());


                guiDrawingArea.getChildren().add(device.getText());

                device.getImageView().addEventFilter(MouseEvent.ANY, event -> {
                            if (activeTool == TOOL_TYPE.DELETE && event.isPrimaryButtonDown()) {
                                ImageView l = (ImageView) event.getSource();


                                for (Device d : deviceMap) {
                                    if (d.getImageView() == l) {
                                        deviceMap.remove(d);
                                        break;
                                    }
                                }

                                guiDrawingArea.getChildren().remove(l);
                                guiDrawingArea.getChildren().remove(device.getText());
                            }
                        }
                );

            }
        }
    }

    Set<Device> deviceMap = new HashSet<>();

    static AnchorPane guiPane;

    public void guiClearAll(ActionEvent actionEvent) {

        if (guiDrawingArea.getChildren().size() > 0) {

            guiPane = guiDrawingArea;
            Main.dialog.show();
        }
    }

    public void popupConfirm(ActionEvent actionEvent) {
        undoActions.clear();
        deviceMap.clear();
        guiPane.getChildren().clear();
        Main.dialog.hide();
    }

    public void popupCancel(ActionEvent actionEvent) {
        Main.dialog.hide();
    }

    public void guiRechtEck(ActionEvent actionEvent) {

        if (activeTool != TOOL_TYPE.RECHTECK) {
            activeTool = TOOL_TYPE.RECHTECK;
            Image image = new Image("informatikprojekt/zigbee/frontend/pen.png");
            Main.s.setCursor(new ImageCursor(image, (image.getWidth() / 2) - 500, (image.getHeight() / 2) + 500));
        } else {
            activeTool = TOOL_TYPE.NONE;
            Main.s.setCursor(Cursor.DEFAULT);
        }

    }

    public void onTabChange(Event event) {
        drawGrid();
    }

    public void onDataTabChange(Event event) {

        NumberAxis xAxis = new NumberAxis(0, 10, 10);
        xAxis.setLabel("Minutes");

        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Data");

        lineChart = new LineChart<>(xAxis, yAxis);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Temperature");
        series.getData().add(new XYChart.Data<>(0, 74));
        series.getData().add(new XYChart.Data<>(1, 76));
        series.getData().add(new XYChart.Data<>(2, 80));
        series.getData().add(new XYChart.Data<>(3, 44));
        series.getData().add(new XYChart.Data<>(4, 24));
        series.getData().add(new XYChart.Data<>(5, 24));
        series.getData().add(new XYChart.Data<>(6, 24));
        series.getData().add(new XYChart.Data<>(7, 28));
        series.getData().add(new XYChart.Data<>(8, 32));
        series.getData().add(new XYChart.Data<>(9, 38));
        series.getData().add(new XYChart.Data<>(10, 70));

        lineChart.getData().add(series);
        lineChart.setPrefWidth(chartAnchor.getWidth());
        lineChart.setPrefHeight(chartAnchor.getHeight());

        chartAnchor.getChildren().add(lineChart);
    }


    enum TOOL_TYPE {
        NONE, WAND, DEVICE, DELETE, RECHTECK
    }


    private TOOL_TYPE activeTool = TOOL_TYPE.NONE;

    @FXML
    public AnchorPane guiDrawingArea;
    @FXML
    public ColorPicker guiColorPicker;
    @FXML
    public TextField guiBSize;

    @FXML
    void guiDelete(ActionEvent event) {
        if (activeTool != TOOL_TYPE.DELETE) {
            activeTool = TOOL_TYPE.DELETE;
            Image image = new Image("informatikprojekt/zigbee/frontend/eraser-tool.png");
            Main.s.setCursor(new ImageCursor(image, (image.getWidth() / 2) - 500, (image.getHeight() / 2) + 500));
        } else {
            activeTool = TOOL_TYPE.NONE;
            Main.s.setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    void guiDevice(ActionEvent event) {
        if (activeTool != TOOL_TYPE.DEVICE) {
            activeTool = TOOL_TYPE.DEVICE;
            Image image = new Image("informatikprojekt/zigbee/frontend/sensor.png");
            Main.s.setCursor(new ImageCursor(image, (image.getWidth() / 2) - 135, (image.getHeight() / 2) - 240));
        } else {
            activeTool = TOOL_TYPE.NONE;
            Main.s.setCursor(Cursor.DEFAULT);
        }
    }

    @FXML
    void guiWand(ActionEvent event) {
        if (activeTool != TOOL_TYPE.WAND) {
            activeTool = TOOL_TYPE.WAND;
            Image image = new Image("informatikprojekt/zigbee/frontend/pen.png");
            Main.s.setCursor(new ImageCursor(image, (image.getWidth() / 2) - 512, (image.getHeight() / 2) + 512));
        } else {
            activeTool = TOOL_TYPE.NONE;
            Main.s.setCursor(Cursor.DEFAULT);
        }
    }

    public void guiConnect(ActionEvent actionEvent) {

        try {
            UartReader.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void guiUndo(ActionEvent actionEvent) {
        Main.s.setCursor(Cursor.DEFAULT);
        if (undoActions.size() > 0) {
            Node l = undoActions.remove(undoActions.size() - 1);
            guiDrawingArea.getChildren().add(l);
        }

    }

    public void onMouseEntered(MouseDragEvent mouseDragEvent) {
        if (activeTool == TOOL_TYPE.WAND) {
            currentLine = new Line();
            currentLine.setStartX(mouseDragEvent.getX());
            currentLine.setStartY(mouseDragEvent.getY());
            currentLine.setEndX(mouseDragEvent.getX());
            currentLine.setEndY(mouseDragEvent.getY());
            currentLine.setStrokeWidth(Double.parseDouble(guiBSize.getText()));
            currentLine.setStroke(guiColorPicker.getValue());
            guiDrawingArea.getChildren().add(currentLine);

            currentLine.addEventFilter(MouseEvent.ANY, event -> {
                        if (activeTool == TOOL_TYPE.DELETE && event.isPrimaryButtonDown()) {
                            Line l = (Line) event.getSource();

                            boolean found = false;

                            for (Node n : undoActions) {
                                if (n == l) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                undoActions.add(l);
                            }
                            guiDrawingArea.getChildren().remove(l);
                        }
                    }
            );

        }
        if (activeTool == TOOL_TYPE.RECHTECK) {
            currentRect = new Rectangle();
            currentRect.setX(mouseDragEvent.getX());
            currentRect.setY(mouseDragEvent.getY());
            currentRect.setHeight(1);
            currentRect.setWidth(1);
            currentRect.setFill(Color.TRANSPARENT);
            currentRect.setStroke(guiColorPicker.getValue());
            currentRect.setStrokeWidth(Double.parseDouble(guiBSize.getText()));
            guiDrawingArea.getChildren().add(currentRect);

            currentRect.addEventFilter(MouseEvent.ANY, event -> {
                        if (activeTool == TOOL_TYPE.DELETE && event.isPrimaryButtonDown()) {
                            Node l = (Node) event.getSource();

                            boolean found = false;

                            for (Node n : undoActions) {
                                if (n == l) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                undoActions.add(l);
                            }
                            guiDrawingArea.getChildren().remove(l);
                        }
                    }
            );
        }
    }

    public void onMouseExit(MouseDragEvent mouseDragEvent) {

    }

    public void OnMouseOver(MouseDragEvent mouseDragEvent) {
        if (activeTool == TOOL_TYPE.WAND) {

            if (!mouseDragEvent.isShiftDown()) {
                currentLine.setEndX(mouseDragEvent.getX());
                currentLine.setEndY(mouseDragEvent.getY());
            } else {

                clampDirection(mouseDragEvent.getX(), mouseDragEvent.getY());

            }
        }

        if (activeTool == TOOL_TYPE.RECHTECK) {

            setRectDirection(mouseDragEvent.getX(), mouseDragEvent.getY());
        }

    }

    private void setRectDirection(double x, double y) {

        double startX = currentRect.getX();
        double startY = currentRect.getY();

        double xTotal = 0;
        double yTotal = 0;

        if (startX > x) {
            xTotal = startX - x;
        } else {
            xTotal = x - startX;
        }
        if (startY > y) {
            yTotal = startY - y;
        } else {
            yTotal = y - startY;
        }

        currentRect.setHeight(yTotal);
        currentRect.setWidth(xTotal);


    }

    private void clampDirection(double x, double y) {

        double startX = currentLine.getStartX();
        double startY = currentLine.getStartY();

        double xTotal = 0;
        double yTotal = 0;

        if (startX > x) {
            xTotal = startX - x;
        } else {
            xTotal = x - startX;
        }
        if (startY > y) {
            yTotal = startY - y;
        } else {
            yTotal = y - startY;
        }

        if (xTotal >= yTotal) {
            currentLine.setEndX(x);
            currentLine.setEndY(startY);
        } else {
            currentLine.setEndX(startX);
            currentLine.setEndY(y);
        }
    }

    public void OnMouseRelease(MouseDragEvent mouseDragEvent) {
        if (activeTool == TOOL_TYPE.WAND) {
            if (!mouseDragEvent.isShiftDown()) {
                currentLine.setEndX(mouseDragEvent.getX());
                currentLine.setEndY(mouseDragEvent.getY());
            } else {
                clampDirection(mouseDragEvent.getX(), mouseDragEvent.getY());
            }
        }
    }


}
