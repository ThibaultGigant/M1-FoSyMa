package mas.protocols;

import mas.abstractAgent;
import mas.behaviours.*;
import mas.strategies.ExploreStrategy;
import mas.strategies.IStrategy;

/**
 * Protocole d'exploration
 * Ajoute les comportements liés à l'observation à l'agent passé en paramètre
 * Created by Tigig on 13/02/2016.
 */
public class ExplorationProtocol extends AbstractProtocol {
    @Override
    public void addBehaviours(abstractAgent myAgent) {
        behaviours.add(new ObservationBehaviour(myAgent, 90));

        IStrategy strategy = new ExploreStrategy();
        strategy.setMyAgent(myAgent);
        behaviours.add(new MoveBehaviour(myAgent, 500, strategy));

//        behaviours.add(new SendMessageBehaviour(myAgent, 1000));
//        behaviours.add(new ReceiveBehaviour(myAgent));

        behaviours.forEach(myAgent::addBehaviour);
    }
}
