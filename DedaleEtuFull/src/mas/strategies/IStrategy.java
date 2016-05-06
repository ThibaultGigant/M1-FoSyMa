package src.mas.strategies;

import org.graphstream.graph.Graph;

public interface IStrategy {
    boolean moveTo(Graph knowledge);
    void setMyAgent(mas.abstractAgent myAgent);
}
