package src.mas.protocols;

import jade.core.behaviours.Behaviour;
import mas.abstractAgent;
import src.mas.behaviours.communication.AckReceiveKnowledgeBehaviour;
import src.mas.behaviours.communication.ReceiveKnowledgeBehaviour;
import src.mas.behaviours.communication.SendKnowledgeBehaviour;
import src.mas.behaviours.move.MoveBehaviour;
import src.mas.behaviours.move.ObservationBehaviour;
import src.mas.strategies.ExploreStrategy;
import src.mas.strategies.IStrategy;

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
        //System.out.println("----------------------------" + behaviours);

        behaviours.forEach(myAgent::addBehaviour);
    }

    @Override
    public void setPath(List<String> path) {
        //System.out.println("Path -----------------");
        this.strategy.setPath(path);
    }

    @Override
    public List<String> getCasesToAvoid() {
        return this.strategy.getCasesToAvoid();
    }

}
