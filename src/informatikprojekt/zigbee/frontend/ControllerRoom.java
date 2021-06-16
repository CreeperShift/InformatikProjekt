package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.Main;
import informatikprojekt.zigbee.util.LineGraph;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerRoom implements Initializable {
    public ColorPicker guiColorPicker;
    public TextField guiBSize;
    public AnchorPane drawingArea;
    public ToggleButton editMode;
    public Button btnClear;
    public Button btnDevice;
    public Button btnDelete;
    public Button btnWand;
    public Button btnNewRoom;
    private Line currentLine;
    private LineGraph lineGraph = new LineGraph();
    private Circle currentCircle = null;
    private boolean toggleButtonActive = false;
    public CheckBox cbGitterNetzLinien;


    enum TOOL_TYPE {
        NONE, WAND, DEVICE, DELETE
    }


    private TOOL_TYPE activeTool = TOOL_TYPE.NONE;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    void GitterNetzLinien() {
        for(int i = 0; i < 1300; i += 25) {
            Line linie1 = new Line(i, 0, i, 1300);
            linie1.setStroke(Color.LIGHTGREY);
            linie1.setStrokeWidth(1);
            Line linie2 = new Line(0, i, 1300, i);
            linie2.setStroke(Color.LIGHTGREY);
            linie2.setStrokeWidth(1);
            drawingArea.getChildren().addAll(linie1, linie2);

        }
    }

    void OrientierungsLinien() {
        for(int i = 0; i < 1300; i += 100) {
            Line linie1 = new Line(i, 0, i, 1300);
            linie1.setStroke(Color.LIGHTPINK);
            linie1.setStrokeWidth(1);
            Line linie2 = new Line(0, i, 1300, i);
            linie2.setStroke(Color.LIGHTPINK);
            linie2.setStrokeWidth(1);
            drawingArea.getChildren().addAll(linie1,linie2);
        }
    }


    public void guiWand(ActionEvent actionEvent) {

        if (activeTool != TOOL_TYPE.WAND) {
            activeTool = TOOL_TYPE.WAND;
            Image image = new Image("informatikprojekt/zigbee/frontend/cursor/pen.png");
            Main.s.setCursor(new ImageCursor(image, (image.getWidth() / 2) - 512, (image.getHeight() / 2) + 512));
        } else {
            activeTool = TOOL_TYPE.NONE;
            Main.s.setCursor(Cursor.DEFAULT);
        }
    }

    public void onEditMode(ActionEvent actionEvent) {

        if (!toggleButtonActive) {

            setToolbarDisabled(false);
            lineGraph.getCircles().forEach(c -> c.setStroke(Color.BLACK));

        } else {
            setToolbarDisabled(true);
            lineGraph.getCircles().forEach(c -> c.setStroke(Color.TRANSPARENT));
        }

        toggleButtonActive = !toggleButtonActive;

    }

    public void onNewRoom(ActionEvent actionEvent) {

        drawingArea.getChildren().clear();
        editMode.fire();

    }


    private void setToolbarDisabled(boolean bool) {

        if (bool) {
            activeTool = TOOL_TYPE.NONE;
            Main.s.setCursor(Cursor.DEFAULT);
        }

        btnWand.setDisable(bool);
        btnClear.setDisable(bool);
        btnDelete.setDisable(bool);
        btnDevice.setDisable(bool);
        guiColorPicker.setDisable(bool);
        guiBSize.setDisable(bool);
        cbGitterNetzLinien.setDisable(bool);

    }

    public void guiDelete(ActionEvent actionEvent) {
    }

    public void guiDevice(ActionEvent actionEvent) {
    }

    public void guiClearAll(ActionEvent actionEvent) {

        drawingArea.getChildren().clear();
        lineGraph = new LineGraph();

        if(cbGitterNetzLinien.isSelected()){
            GitterNetzLinien();
            OrientierungsLinien();
        }

    }


    public void gitterOnMouseClicked(MouseEvent event) {
        if (activeTool == TOOL_TYPE.NONE) {
            if (cbGitterNetzLinien.isSelected()) {
                GitterNetzLinien();
                OrientierungsLinien();
            } else {
                drawingArea.getChildren().clear();
            }
        }
    }

    public void onDragDetected(MouseEvent mouseEvent) {
        if (activeTool == TOOL_TYPE.WAND) {
            drawingArea.startFullDrag();
            drawingArea.getChildren().forEach(Node::startFullDrag);
            mouseEvent.consume();
        }
    }

    public void onMouseDragEntered(MouseDragEvent mouseDragEvent) {
        if (activeTool == TOOL_TYPE.WAND) {

            if (lineGraph.isEmpty()) {
                currentCircle = new Circle();
                currentLine = new Line();
                currentCircle.setRadius(15);
                currentCircle.setFill(Color.TRANSPARENT);
                currentCircle.setStroke(Color.BLACK);
                currentCircle.setStrokeWidth(4);
                currentCircle.setCenterX(mouseDragEvent.getX());
                currentCircle.setCenterY(mouseDragEvent.getY());
                currentLine.setStrokeWidth(Double.parseDouble(guiBSize.getText()));
                currentLine.setStroke(guiColorPicker.getValue());
                currentLine.setStartX(mouseDragEvent.getX());
                currentLine.setStartY(mouseDragEvent.getY());

                drawingArea.getChildren().add(currentCircle);
                drawingArea.getChildren().add(currentLine);
                lineGraph.addCircle(currentCircle);
                setupCircleHandlers(currentCircle);

            }
        }
    }

    public void onMouseDragExited(MouseDragEvent mouseDragEvent) {
    }

    public void onMouseDragReleased(MouseDragEvent mouseDragEvent) {

        if (activeTool == TOOL_TYPE.WAND && currentLine != null) {

            List<Circle> matches = new LinkedList<>();

            drawingArea.getChildren().forEach(e ->
            {
                if (e instanceof Circle && e.contains(mouseDragEvent.getX(), mouseDragEvent.getY()))
                    matches.add((Circle) e);
            });

            Circle circle;

            /*
            There is no circle where we release the mouse
             */
            if (matches.isEmpty()) {
                if (mouseDragEvent.isShiftDown()) {
                    clampDirection(mouseDragEvent.getX(), mouseDragEvent.getY());
                } else {
                    currentLine.setEndX(mouseDragEvent.getX());
                    currentLine.setEndY(mouseDragEvent.getY());
                }
                circle = new Circle();
                circle.setRadius(15);
                circle.setFill(Color.TRANSPARENT);
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(4);
                circle.setCenterX(currentLine.getEndX());
                circle.setCenterY(currentLine.getEndY());
                drawingArea.getChildren().add(circle);
                setupCircleHandlers(circle);
                lineGraph.addCircle(circle);
            } else {
                /*
                A circle is at the location, we get the top most one and connect the line to it.
                 */
                circle = matches.get(matches.size() - 1);
                currentLine.setEndX(circle.getCenterX());
                currentLine.setEndY(circle.getCenterY());
            }

            lineGraph.addEdge(circle, currentCircle);
            currentCircle = null;
            currentLine = null;

        }
        mouseDragEvent.consume();
    }

    public void onMouseDragOver(MouseDragEvent mouseDragEvent) {

        if (activeTool == TOOL_TYPE.WAND && currentLine != null) {


            List<Circle> matches = new LinkedList<>();

            drawingArea.getChildren().forEach(e ->
            {
                if (e instanceof Circle && e.contains(mouseDragEvent.getX(), mouseDragEvent.getY()))
                    matches.add((Circle) e);
            });

            if (matches.isEmpty()) {


                if (mouseDragEvent.getX() < 20) {

                    currentLine.setEndX(20);
                    currentLine.setEndY(mouseDragEvent.getY());

                } else if (mouseDragEvent.getY() < 20) {
                    currentLine.setEndX(mouseDragEvent.getX());
                    currentLine.setEndY(20);

                } else {
                    if (!mouseDragEvent.isShiftDown()) {
                        currentLine.setEndX(mouseDragEvent.getX());
                        currentLine.setEndY(mouseDragEvent.getY());
                    } else {
                        clampDirection(mouseDragEvent.getX(), mouseDragEvent.getY());
                    }
                }
            } else {
                Circle c = matches.get(matches.size() - 1);
                currentLine.setEndX(c.getCenterX());
                currentLine.setEndY(c.getCenterY());
            }

        }
    }

    private void setupCircleHandlers(Circle c) {
        c.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
            c.startFullDrag();
            event.consume();
        });
        c.addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, event ->
        {
            if (activeTool == TOOL_TYPE.WAND && currentLine == null) {
                currentLine = new Line();
                currentLine.setStrokeWidth(Double.parseDouble(guiBSize.getText()));
                currentLine.setStroke(guiColorPicker.getValue());
                Circle source = (Circle) event.getSource();
                currentLine.setStartX(source.getCenterX());
                currentLine.setStartY(source.getCenterY());
                drawingArea.getChildren().add(currentLine);

                currentCircle = source;
            }

        });
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
}
