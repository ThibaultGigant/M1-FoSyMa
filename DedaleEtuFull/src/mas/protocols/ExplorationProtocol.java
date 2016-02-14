package mas.protocols;

import mas.abstractAgent;
import mas.behaviours.MoveBehaviour;
import mas.behaviours.ObservationBehaviour;
import mas.behaviours.WalkThroughTheAir;
import mas.strategies.ExploreStrategy;
import mas.strategies.IStrategy;
import mas.strategies.NewHorizon;

/**
 * Protocole d'exploration
 * Ajoute les comportements liés à l'observation à l'agent passé en paramètre
 * Created by Tigig on 13/02/2016.
 */
public class ExplorationProtocol extends AbstractProtocol {
    @Override
    public void addBehaviours(abstractAgent myAgent) {
        behaviours.add(new ObservationBehaviour(myAgent, 50));
        IStrategy strategy = new ExploreStrategy();
        strategy.setMyAgent(myAgent);
        behaviours.add(new MoveBehaviour(myAgent, 100, strategy));
        behaviours.forEach(myAgent::addBehaviour);
    }
}
