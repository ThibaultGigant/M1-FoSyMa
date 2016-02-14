package mas.strategies;

import mas.abstractAgent;
import mas.util.GraphTools;
import org.graphstream.graph.Graph;

/**
 * Stratégie de déplacement correspondant à l'exploration : on va au plus proche non-visité
 * Created by Tigig on 13/02/2016.
 */
public class ExploreStrategy implements IStrategy {
    /**
     * Agent sur lequel s'applique la stratégie
     */
    mas.abstractAgent myAgent;

    @Override
    public String moveTo(Graph knowledge) {
        return GraphTools.nearestUnvisited(myAgent.getCurrentPosition(), knowledge);
    }

    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
