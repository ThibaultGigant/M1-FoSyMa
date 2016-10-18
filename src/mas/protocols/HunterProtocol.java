package mas.protocols;

import mas.abstractAgent;
import mas.behaviours.communication.AckReceiveKnowledgeBehaviour;
import mas.behaviours.communication.ReceiveKnowledgeBehaviour;
import mas.behaviours.communication.SendKnowledgeBehaviour;
import mas.behaviours.move.MoveBehaviour;
import mas.strategies.HunterStrategy;
import mas.strategies.IStrategy;

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
        behaviours.add(new MoveBehaviour(myAgent, 600, strategy));

        behaviours.add(new SendKnowledgeBehaviour(myAgent, 600));
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
        if (strategy == null)
            return null;
        return this.strategy.getCasesToAvoid();
    }

}
