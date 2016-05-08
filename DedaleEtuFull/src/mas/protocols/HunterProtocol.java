package src.mas.protocols;

import mas.abstractAgent;
import src.mas.behaviours.communication.AckReceiveKnowledgeBehaviour;
import src.mas.behaviours.communication.ReceiveKnowledgeBehaviour;
import src.mas.behaviours.communication.SendKnowledgeBehaviour;
import src.mas.behaviours.move.MoveBehaviour;
import src.mas.strategies.HunterStrategy;
import src.mas.strategies.IStrategy;

import java.util.List;

/**
 * Protocole d'exploration
 * Ajoute les comportements liés à l'observation à l'agent passé en paramètre
 * Created by Tigig on 13/02/2016.
 */
public class HunterProtocol extends AbstractProtocol {
    IStrategy strategy;
    @Override
    public void addBehaviours(abstractAgent myAgent) {
        //behaviours.add(new ObservationBehaviour(myAgent, 500));

        strategy = new HunterStrategy();
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
