package src.mas.behaviours.move;

import env.Attribute;
import env.Couple;
import org.junit.Assert;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import src.mas.agents.AgentExplorateur;
import src.mas.strategies.IStrategy;

import java.util.List;

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
        observe();
        this.strategy.moveTo(((AgentExplorateur) this.myAgent).getKnowledge().getGraph());
    }

    private void observe() {
        // Récupération des observations
        List<Couple<String, List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();
        // Mise à jour de ces observations dans les connaissances de l'agent
        ((AgentExplorateur) this.myAgent).updateKnowledge(lobs);


        // System.out.println("Agent " + this.myAgent.getLocalName() + " observes around position: " + ((AgentExplorateur) this.myAgent).getCurrentPosition());
    }
}
