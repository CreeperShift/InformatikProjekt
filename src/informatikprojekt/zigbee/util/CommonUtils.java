package informatikprojekt.zigbee.util;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.List;

public class CommonUtils {

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
            List<Node> children = ((Parent)node).getChildrenUnmodifiable();
            for (int i = children.size() - 1; i >= 0; i--) {
                Node child = children.get(i);
                p = child.sceneToLocal(sceneX, sceneY, true /* rootScene */);
                if (child.isVisible()  && child.contains(p)) {
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
