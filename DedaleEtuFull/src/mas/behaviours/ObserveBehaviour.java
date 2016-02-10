package mas.behaviours;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import mas.agents.ExploAgent;
import mas.util.CustomCouple;
import env.Attribute;
import env.Environment.Couple;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

// TODO entirely
/**
 * ObserveBehabiour : Behaviour qui force l'agent à observer autour de lui périodiquement en cas de changement 
 *
 */
public class ObserveBehaviour extends TickerBehaviour {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8344082711994133531L;

	/**
	 * Constructeur
	 * @param a Agent associé à ce behaviour
	 * @param period période d'actualisation
	 */
	public ObserveBehaviour(Agent a, long period) {
		super(a, period);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Mise à jour des connaissances périodique à partir des observations
	 */
	@Override
	protected void onTick() {
		// TODO Auto-generated method stub
		//Example to retrieve the current position
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		if (!myPosition.equals("")){
			//List of observable from the agent's current position
			List<Couple<String, List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();
			Date date = new Date();
			HashMap<String, CustomCouple<Date, List<Attribute>>> knowledge = new HashMap<String, CustomCouple<Date,List<Attribute>>>();
			for (Couple<String, List<Attribute>> couple: lobs) {
				knowledge.put(couple.getLeft(), new CustomCouple<Date, List<Attribute>>(date, couple.getRight()));
			}
			((ExploAgent) this.myAgent).updateKnowledge(knowledge);
			System.out.println("Agent " + this.myAgent.getLocalName() + " observes around position: " + myPosition);
		}
	}
}
