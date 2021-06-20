package informatikprojekt.zigbee.util;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.*;

public class LineGraph {

    private final Map<Circle, Map<Circle, Line>> adjCircles = new HashMap<>();

    public void addCircle(Circle c) {
        adjCircles.putIfAbsent(c, new HashMap<>());
    }

    public void removeCircle(Circle c) {
        adjCircles.values().forEach(e -> e.remove(c));
        adjCircles.remove(c);
    }

    public void addEdge(Circle a, Circle b, Line l) {
        adjCircles.get(a).put(b, l);
        adjCircles.get(b).put(a, l);
    }

    public void removeEdge(Circle a, Circle b) {
        Map<Circle, Line> eL1 = adjCircles.get(a);
        Map<Circle, Line> eL2 = adjCircles.get(b);

        if (eL1 != null) {
            eL1.remove(b);
        }

        if (eL2 != null) {
            eL2.remove(a);
        }
    }

    public boolean isEmpty() {
        return adjCircles.isEmpty();
    }

    public Set<Circle> getCircles() {
        return adjCircles.keySet();
    }

    public List<Line> getLine(Circle c) {
        return adjCircles.get(c).values().stream().toList();
    }

}
