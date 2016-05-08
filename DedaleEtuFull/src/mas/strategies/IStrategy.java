package mas.strategies;

import org.graphstream.graph.Graph;

import java.util.List;

public interface IStrategy {
    boolean moveTo(Graph knowledge);
    void setMyAgent(mas.abstractAgent myAgent);
    void setPath(List<String> path);
    List<String> getCasesToAvoid();
}
