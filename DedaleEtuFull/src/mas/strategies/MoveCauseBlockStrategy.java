package mas.strategies;

import mas.abstractAgent;
import org.graphstream.graph.Graph;

/**
 * Created by Tigig on 09/03/2016.
 */
public class MoveCauseBlockStrategy implements IStrategy {
    abstractAgent myAgent;

    @Override
    public boolean moveTo(Graph knowledge) {
        //TODO
        return false;
    }

    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
