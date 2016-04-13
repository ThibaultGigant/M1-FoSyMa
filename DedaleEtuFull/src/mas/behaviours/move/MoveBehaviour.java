package mas.behaviours.move;

import org.junit.Assert;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import mas.agents.AgentExplorateur;
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
        this.strategy.moveTo(((AgentExplorateur) this.myAgent).getKnowledge().getGraph());
    }
}
