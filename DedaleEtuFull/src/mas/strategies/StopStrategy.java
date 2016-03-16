package mas.strategies;

import mas.abstractAgent;
import org.graphstream.graph.Graph;

/**
 * Created by Tigig on 09/03/2016.
 */
public class StopStrategy implements IStrategy {
    /**
     * Agent sur lequel s'applique la stratégie
     */
    mas.abstractAgent myAgent;

    @Override
    public boolean moveTo(Graph knowledge) {
        return true;
    }

    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
