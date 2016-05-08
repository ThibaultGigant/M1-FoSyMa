package mas.protocols;

import jade.core.behaviours.Behaviour;
import mas.abstractAgent;
import mas.behaviours.communication.AckReceiveKnowledgeBehaviour;
import mas.behaviours.communication.ReceiveKnowledgeBehaviour;
import mas.behaviours.communication.SendKnowledgeBehaviour;
import mas.behaviours.move.MoveBehaviour;
import mas.behaviours.move.ObservationBehaviour;
import mas.strategies.ExploreStrategy;
import mas.strategies.IStrategy;

import java.util.List;

/**
 * Protocole d'exploration
 * Ajoute les comportements liés à l'observation à l'agent passé en paramètre
 * Created by Tigig on 13/02/2016.
 */
public class ExplorationProtocol extends AbstractProtocol {
    IStrategy strategy;
    @Override
    public void addBehaviours(abstractAgent myAgent) {
        //behaviours.add(new ObservationBehaviour(myAgent, 500));

        strategy = new ExploreStrategy();
        strategy.setMyAgent(myAgent);
        behaviours.add(new MoveBehaviour(myAgent, 500, strategy));

        behaviours.add(new SendKnowledgeBehaviour(myAgent, 500));
        behaviours.add(new ReceiveKnowledgeBehaviour(myAgent));
        behaviours.add(new AckReceiveKnowledgeBehaviour(myAgent));

        behaviours.forEach(myAgent::addBehaviour);
    }

    @Override
    public void setPath(List<String> path) {
        this.strategy.setPath(path);
    }

    @Override
    public List<String> getCasesToAvoid() {
        return this.strategy.getCasesToAvoid();
    }

}
