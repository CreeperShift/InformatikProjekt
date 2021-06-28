package informatikprojekt.zigbee.util;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.*;

public class CommonUtils {

    public static final String TEMPERATURE = "Temperatur";
    public static final String HUMIDITY = "Feuchtigkeit";
    public static final String CO2 = "CO2";
    public static final String PARTICLES = "Partikel";
    public static final String VOC = "VoC";

    public static final Map<Integer, String> sensors = new HashMap<>() {{
        put(1, "SHT21");
        put(2, "CCS");
        put(3, "SCD30");
        put(4, "SCD41");
    }};

    private static final List<Timer> timers = new ArrayList<>();

    public static String getSensorName(int id) {
        return sensors.getOrDefault(id, "Unknown Sensor");

    }

    public static int getSensorID(String id) {
        for (Map.Entry<Integer, String> s : sensors.entrySet()) {
            if (s.getValue().equalsIgnoreCase(id)) {
                return s.getKey();
            }
        }
        return 0;
    }

    public static void stopAllTimers() {
        for (Timer t : timers) {
            t.cancel();
        }
    }

    public static void registerTimer(Timer t) {
        timers.add(t);
    }

    public static Node pick(Node node, double sceneX, double sceneY) {
        Point2D p = new Point2D(sceneX, sceneY);

        // check if the given node has the point inside it, or else we drop out
        if (!node.contains(p)) return null;

        // at this point we know that _at least_ the given node is a valid
        // answer to the given point, so we will return that if we don't find
        // a better child option
        if (node instanceof Parent) {
            // we iterate through all children in reverse order, and stop when we find a match.
            // We do this as we know the elements at the end of the list have a higher
            // z-order, and are therefore the better match, compared to children that
            // might also intersect (but that would be underneath the element).
            Node bestMatchingChild = null;
            List<Node> children = ((Parent) node).getChildrenUnmodifiable();
            for (int i = children.size() - 1; i >= 0; i--) {
                Node child = children.get(i);
                p = child.sceneToLocal(sceneX, sceneY, true /* rootScene */);
                if (child.isVisible() && child.contains(p)) {
                    bestMatchingChild = child;
                    break;
                }
            }

            if (bestMatchingChild != null) {
                return pick(bestMatchingChild, sceneX, sceneY);
            }
        }

        return node;
    }

}
