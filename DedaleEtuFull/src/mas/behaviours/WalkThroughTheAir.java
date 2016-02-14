package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import mas.strategies.IStrategy;

import mas.agents.AgentExplorateur;

/**
 * Created by Fayçal on 08/02/2016.
 */
public class WalkThroughTheAir extends TickerBehaviour {

    private IStrategy strategy;

    public WalkThroughTheAir(Agent a, long period, IStrategy strategy) {
        super(a, period);
        this.strategy = strategy;
    }

    @Override
    protected void onTick() {
        // comportement préliminaire
        System.out.println("Agent " + this.myAgent.getLocalName() + " about to move");
        ((mas.abstractAgent)this.myAgent).moveTo( strategy.moveTo(((AgentExplorateur) this.myAgent).getKnowledge().getGraph()));
    }
}
