package informatikprojekt.zigbee.util;

import javafx.scene.shape.Circle;

import java.util.*;
import java.util.function.Consumer;

public class LineGraph {

    private final Map<Circle, List<Circle>> adjCircles = new HashMap<>();

    public void addCircle(Circle c) {
        adjCircles.putIfAbsent(c, new ArrayList<>());
    }

    public void removeCircle(Circle c) {
        adjCircles.values().forEach(e -> e.remove(c));
        adjCircles.remove(c);
    }

    public void addEdge(Circle a, Circle b) {
        adjCircles.get(a).add(b);
        adjCircles.get(b).add(a);
    }

    public void removeEdge(Circle a, Circle b) {
        List<Circle> eL1 = adjCircles.get(a);
        List<Circle> eL2 = adjCircles.get(b);

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

}
