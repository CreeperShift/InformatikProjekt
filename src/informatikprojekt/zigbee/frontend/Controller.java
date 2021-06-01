package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.Main;
import informatikprojekt.zigbee.backend.UartReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.util.*;

public class Controller {


    public Button guiClearAllID;

    @FXML
    private SplitPane splitPane;

    public void onDragDetected(MouseEvent mouseEvent) {
        if (activeTool == TOOL_TYPE.WAND) {
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

    private Line currentLine;
    private LinkedList<Line> undoActions = new LinkedList<>();

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

    enum TOOL_TYPE {
        NONE, WAND, DEVICE, DELETE
    }


    private TOOL_TYPE activeTool = TOOL_TYPE.NONE;

    @FXML
    public AnchorPane guiDrawingArea;
    @FXML
    public ColorPicker guiColorPicker;
    @FXML
    public TextField guiBSize;
    @FXML
    private Button guiWandID;

    @FXML
    private Button guiDeviceID;

    @FXML
    private Button guiDeleteID;

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
            Main.s.setCursor(new ImageCursor(image, (image.getWidth() / 2) - 500, (image.getHeight() / 2) + 500));
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
            Line l = undoActions.remove(undoActions.size() - 1);
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

                            for (Line line : undoActions) {
                                if (line == l) {
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
