package mas.behaviours;

import env.Attribute;
import env.Environment.Couple;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import mas.agents.AgentExplorateur;
import java.util.List;

/**
 * ObserveBehabiour : Behaviour qui force l'agent à observer autour de lui périodiquement en cas de changement 
 *
 */
public class ObservationBehaviour extends TickerBehaviour {

	/**
	 * Constructeur
	 * @param a Agent associé à ce behaviour
	 * @param period période d'actualisation
	 */
	public ObservationBehaviour(Agent a, long period) {
		super(a, period);
	}

	/**
	 * Mise à jour des connaissances périodique à partir des observations
	 */
	@Override
	protected void onTick() {
		// Récupération des observations
		List<Couple<String, List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();
		// Mise à jour de ces observations dans les connaissances de l'agent
		((AgentExplorateur) this.myAgent).updateKnowledge(lobs);


		// System.out.println("Agent " + this.myAgent.getLocalName() + " observes around position: " + ((AgentExplorateur) this.myAgent).getCurrentPosition());
	}
}
