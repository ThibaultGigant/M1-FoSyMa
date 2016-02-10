package mas.strategies;

import org.graphstream.graph.Graph;

public interface IStrategy {
    String moveTo(Graph knowledge);
    void setMyAgent(mas.abstractAgent myAgent);
}
