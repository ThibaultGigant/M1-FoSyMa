package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import mas.agents.AgentExplorateur;
import mas.protocols.RandomObserveProtocol;
import mas.strategies.IStrategy;

/**
 * Behaviour correspondant au déplacement de l'agent
 * Created by Tigig on 14/02/2016.
 */
public class MoveBehaviour extends TickerBehaviour {
    /**
     * Stratégie de déplacement qui sera appliqué par l'agent
     */
    IStrategy strategy;

    public MoveBehaviour(Agent a, long period, IStrategy strategy) {
        super(a, period);
        this.strategy = strategy;
    }

    @Override
    protected void onTick() {
        String destination = this.strategy.moveTo(((AgentExplorateur) this.myAgent).getKnowledge().getGraph());
        if (destination.isEmpty()) {
            ((AgentExplorateur) this.myAgent).setProtocol(new RandomObserveProtocol());
        } else {
            ((AgentExplorateur) this.myAgent).moveTo(destination);
        }
    }
}
