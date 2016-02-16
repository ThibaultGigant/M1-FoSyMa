package mas.behaviours;

import java.util.Date;
import java.util.List;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import mas.agents.ExploAgent;
import env.Attribute;
import env.Couple;
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
		
		Date date = new Date();
		
		try {
			((ExploAgent)this.myAgent).getKnowledge().addNode(myPosition);
		}
		catch( Exception e) {
		}
		Node currentNode = ((ExploAgent)this.myAgent).getKnowledge().getNode(myPosition);
		currentNode.addAttribute("visited", 1);
		currentNode.addAttribute("date", date);

		if (!myPosition.equals("")){
			//List of observable from the agent's current position
			List<Couple<String, List<Attribute>>> lobs=((mas.abstractAgent)this.myAgent).observe();
			Graph knowledge = new SingleGraph("observe");
			Node newNode;
			for (Couple<String, List<Attribute>> couple: lobs) {
				try {
					knowledge.addNode(couple.getLeft());
					newNode = knowledge.getNode(couple.getLeft()); 
					newNode.addAttribute("visited", 0);
					newNode.addAttribute("date", date);
					knowledge.addEdge(newNode.getId() + currentNode.getId(), newNode, currentNode);
				}
				catch (Exception e) {
					continue;
				}
				
				// TODO add others attributes
			}
			((ExploAgent) this.myAgent).updateKnowledge(knowledge);
			System.out.println("Agent " + this.myAgent.getLocalName() + " observes around position: " + myPosition);
		}
	}
}
