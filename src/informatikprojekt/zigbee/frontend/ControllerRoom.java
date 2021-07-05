package informatikprojekt.zigbee.frontend;

import informatikprojekt.zigbee.Main;
import informatikprojekt.zigbee.util.LineGraph;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import jfxtras.styles.jmetro.JMetroStyleClass;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;

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
    public Button btnMove;
    private Line currentLine;
    private LineGraph lineGraph = new LineGraph();
    private Circle currentCircle = null;
    private boolean toggleButtonActive = false;
    public CheckBox cbGitterNetzLinien;
    public CheckBox cbLineal;
    private Circle activeCircle = null;
    private final Map<Line, Boolean> draggedLines = new HashMap<>();

    private final List<Line> gridList = new ArrayList<>();
    private final List<Line> lineList = new ArrayList<>();
    private final List<Text> textList = new ArrayList<>();

    private final Line lineX = new Line(0, 25, 0, 0);
    private final Line lineY = new Line(25, 0, 0, 0);


    enum TOOL_TYPE {
        NONE, WAND, DEVICE, DELETE, MOVE
    }


    private TOOL_TYPE activeTool = TOOL_TYPE.NONE;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        drawingArea.getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }

    private void GitterNetzLinien(int increase, Color color) {
        for (int i = 25; i <= drawingArea.getWidth() - 5; i += increase) {
            Line linie1 = new Line(i, 25, i, drawingArea.getHeight() - 25);
            linie1.setStroke(color);
            linie1.setStrokeWidth(1);
            gridList.add(linie1);
        }

        for (int i = 25; i <= drawingArea.getHeight() - 4; i += increase) {
            Line line = new Line();
            line.setStartX(25);
            line.setEndX(drawingArea.getWidth() - 5);
            line.setStartY(i);
            line.setEndY(i);
            line.setStroke(color);
            line.setStrokeWidth(1);
            gridList.add(line);
        }
    }

    private void Lineal(int increase, int startX, int startY, int sW) {
        for (int i = 25; i < drawingArea.getWidth(); i += increase) {
            Line linie1 = new Line(i, startY, i, 25);
            linie1.setStroke(Color.DARKGRAY);
            linie1.setStrokeWidth(sW);
            lineList.add(linie1);
        }

        for (int i = 25; i < drawingArea.getHeight(); i += increase) {
            Line line = new Line();
            line.setStartX(startX);
            line.setEndX(25);
            line.setStartY(i);
            line.setEndY(i);
            line.setStroke(Color.DARKGRAY);
            line.setStrokeWidth(sW);
            lineList.add(line);
        }
    }

    private void Koordinaten() {
        for (int i = 100; i < drawingArea.getWidth() - 25; i += 100) {
            Text text1 = new Text(i - 5, 20, "" + i);
            textList.add(text1);
        }

        for (int i = 100; i < drawingArea.getHeight(); i += 100) {
            Text text2 = new Text(0, i + 25, "" + i);
            textList.add(text2);
        }
    }


    public void guiWand(ActionEvent actionEvent) {

        if (activeTool != TOOL_TYPE.WAND) {
            activeTool = TOOL_TYPE.WAND;
            Image image = new Image("informatikprojekt/zigbee/frontend/cursor/pen.png");
            Main.s.setCursor(new ImageCursor(image, (image.getWidth() / 2) - 512, (image.getHeight() / 2) + 512));
        } else {
            resetTool();
        }
    }

    public void onBtnMove(ActionEvent actionEvent) {
        if (activeTool != TOOL_TYPE.MOVE) {
            activeTool = TOOL_TYPE.MOVE;
            Image image = new Image("informatikprojekt/zigbee/frontend/cursor/move.png");
            Main.s.setCursor(new ImageCursor(image, (image.getWidth() / 2), (image.getHeight() / 2)));
        } else {
            resetTool();
        }
    }

    private void resetTool() {
        activeTool = TOOL_TYPE.NONE;
        Main.s.setCursor(Cursor.DEFAULT);
    }

    public void onEditMode(ActionEvent actionEvent) {

        if(!Main.isConnected) {
            if (!toggleButtonActive) {
                setToolbarDisabled(false);
                lineGraph.getCircles().forEach(c -> c.setStroke(Color.BLACK));
                if (cbGitterNetzLinien.isSelected()) {
                    drawingArea.getChildren().addAll(0, gridList);
                }
                if (cbLineal.isSelected()) {
                    drawingArea.getChildren().addAll(0, lineList);
                    drawingArea.getChildren().addAll(0, textList);
                }
            } else {
                setToolbarDisabled(true);
                drawingArea.getChildren().removeAll(gridList);
                drawingArea.getChildren().removeAll(lineList);
                drawingArea.getChildren().removeAll(textList);
                lineGraph.getCircles().forEach(c -> c.setStroke(Color.TRANSPARENT));
            }

            toggleButtonActive = !toggleButtonActive;
        }
    }

    public void onNewRoom(ActionEvent actionEvent) {

        drawingArea.getChildren().clear();
        editMode.setDisable(false);
        GitterNetzLinien(25, Color.LIGHTGRAY);
        GitterNetzLinien(100, Color.LIGHTPINK);
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
        cbLineal.setDisable(bool);
        btnMove.setDisable(bool);
    }

    public void guiDelete(ActionEvent actionEvent) {

        if (activeTool != TOOL_TYPE.DELETE) {
            activeTool = TOOL_TYPE.DELETE;
            Image image = new Image("informatikprojekt/zigbee/frontend/cursor/eraser-tool.png");
            Main.s.setCursor(new ImageCursor(image, (image.getWidth() / 2) - 512, (image.getHeight() / 2) + 512));
        } else {
            resetTool();
        }
    }

    public void guiDevice(ActionEvent actionEvent) {
    }

    public void guiClearAll(ActionEvent actionEvent) {

        drawingArea.getChildren().clear();
        drawingArea.getChildren().addAll(gridList);
        drawingArea.getChildren().addAll(lineList);
        drawingArea.getChildren().addAll(textList);
        lineGraph = new LineGraph();
    }

    public void gitterOnMouseClicked(MouseEvent event) {
        if (cbGitterNetzLinien.isSelected() && !drawingArea.getChildren().containsAll(gridList)) {
            drawingArea.getChildren().addAll(0, gridList);
        } else {
            drawingArea.getChildren().removeAll(gridList);
        }
    }

    public void linealOnMouseClicked(MouseEvent event) {

        if (cbLineal.isSelected()) {
            if (lineList.isEmpty()) {
                Lineal(25, 20, 20, 1);
                Lineal(100, 0, 0, 2);
            }
            if (textList.isEmpty()) {
                Koordinaten();
            }

            drawingArea.getChildren().addAll(0, textList);
            drawingArea.getChildren().addAll(0, lineList);
        } else {
            drawingArea.getChildren().removeAll(lineList);
            drawingArea.getChildren().removeAll(textList);
        }
    }

    public void keinLinealOnMouseExited(MouseEvent event) {
        drawingArea.getChildren().remove(lineX);
        drawingArea.getChildren().remove(lineY);
    }

    public void linealOnMouseMoved(MouseEvent event) {
        if (cbLineal.isSelected()) {
                lineX.setStroke(Color.RED);
                lineY.setStroke(Color.RED);
                lineX.setStrokeWidth(2);
                lineY.setStrokeWidth(2);
                lineX.setStartX(event.getX());
                lineX.setEndX(event.getX());
                lineY.setStartY(event.getY());
                lineY.setEndY(event.getY());
                drawingArea.getChildren().addAll(lineX, lineY);
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
                activeCircle = currentCircle;
            }
        }
    }

    public void onMouseDragExited(MouseDragEvent mouseDragEvent) {
    }

    public void onMouseDragReleased(MouseDragEvent mouseDragEvent) {
        double x = mouseDragEvent.getX();
        if (mouseDragEvent.getX() > 1114) {
            x = drawingArea.getWidth() - 10;
        }

        if (activeTool == TOOL_TYPE.WAND && currentLine != null) {

            List<Circle> matches = new LinkedList<>();

            double finalX = x;
            drawingArea.getChildren().forEach(e ->
            {
                if (e instanceof Circle && e.contains(finalX, mouseDragEvent.getY()))
                    matches.add((Circle) e);
            });

            Circle circle;

            /*
            There is no circle where we release the mouse
             */
            if (matches.isEmpty()) {
                if (mouseDragEvent.isShiftDown()) {
                    clampDirection(x, mouseDragEvent.getY());
                } else {
                    currentLine.setEndX(x);
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
                lineGraph.addEdge(circle, currentCircle, currentLine);
            } else {
                /*
                A circle is at the location, we get the top most one and connect the line to it.
                 */
                circle = matches.get(matches.size() - 1);
                if (currentCircle == circle) {
                    drawingArea.getChildren().remove(currentLine);
                } else {
                    currentLine.setEndX(circle.getCenterX());
                    currentLine.setEndY(circle.getCenterY());
                    lineGraph.addEdge(circle, currentCircle, currentLine);
                }
            }


            currentCircle = null;
            currentLine = null;
            activeCircle = null;

        }
        mouseDragEvent.consume();
    }


    public void onMouseDragOver(MouseDragEvent mouseDragEvent) {

        if (mouseDragEvent.getX() < 1114) {

            if (activeTool == TOOL_TYPE.MOVE && currentLine == null) {

                moveCircle(mouseDragEvent.getX(), mouseDragEvent.getY());
            }


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
    }

    private void moveCircle(double x, double y) {
        if (draggedLines.isEmpty()) {

            for (Line line : lineGraph.getLine(activeCircle)) {
                if (line.getEndX() == activeCircle.getCenterX() && line.getEndY() == activeCircle.getCenterY()) {
                    draggedLines.put(line, true);
                } else {
                    draggedLines.put(line, false);
                }
            }
        }
        activeCircle.setCenterX(x);
        activeCircle.setCenterY(y);
        for (Map.Entry<Line, Boolean> l : draggedLines.entrySet()) {
            if (!l.getValue()) {
                l.getKey().setStartX(x);
                l.getKey().setStartY(y);
            } else {
                l.getKey().setEndX(x);
                l.getKey().setEndY(y);
            }

        }
    }

    private void setupCircleHandlers(Circle c) {
        c.addEventFilter(MouseEvent.DRAG_DETECTED, event -> {
            c.startFullDrag();
            if (event.getSource() instanceof Circle && activeTool == TOOL_TYPE.MOVE) {
                draggedLines.clear();
                activeCircle = (Circle) event.getSource();
            }
            event.consume();
        });

        c.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (activeTool == TOOL_TYPE.DELETE) {
                if (event.getSource() instanceof Circle) {
                    Circle circle = (Circle) event.getSource();
                    Set<Line> linesToRemove = new HashSet<>(lineGraph.getLine(circle));
                    drawingArea.getChildren().remove(circle);
                    drawingArea.getChildren().removeAll(linesToRemove);
                    lineGraph.removeCircle(circle);
                }
            }
        });
        c.addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, event ->
        {
            Circle source = (Circle) event.getSource();
            if (activeTool == TOOL_TYPE.WAND && currentLine == null) {
                currentLine = new Line();
                currentLine.setStrokeWidth(Double.parseDouble(guiBSize.getText()));
                currentLine.setStroke(guiColorPicker.getValue());

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

        double xTotal, yTotal;

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
