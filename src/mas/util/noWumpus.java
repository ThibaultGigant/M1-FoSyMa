package mas.util;

import env.Attribute;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Fayçal on 08/05/2016.
 */
public class noWumpus {
    public static boolean isWumpus(Graph knowledge, String node) {
        Node n = knowledge.getNode(node);

        if (!((List<Attribute>)n.getAttribute("contenu")).contains(Attribute.WIND)) {// && !((List<Attribute>)n.getAttribute("contenu")).contains(Attribute.STENCH)) {
            return false;
        }
        Iterator<Node> nodeIterator = n.getNeighborNodeIterator();
        Node tmp;
        int count = 0;
        int count_2 = 0;
        while(nodeIterator.hasNext()) {
            tmp = nodeIterator.next();
            if (((List<Attribute>)tmp.getAttribute("contenu")).contains(Attribute.WIND)) {// || ((List<Attribute>)tmp.getAttribute("contenu")).contains(Attribute.STENCH))
                //if (tmp.hasAttribute("wind") || tmp.hasAttribute("stench"))
                count++;
            }
            else if (!((List<Attribute>)tmp.getAttribute("contenu")).contains(Attribute.WIND)) { // && !((List<Attribute>)tmp.getAttribute("contenu")).contains(Attribute.STENCH)) {
                if (!tmp.hasAttribute("unvisited"))
                    return false;
                count_2++;
            }

        }


        return count > 1 && count_2 < 2;
    }
}
