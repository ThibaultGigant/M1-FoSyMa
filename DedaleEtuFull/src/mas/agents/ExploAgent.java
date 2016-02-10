package mas.agents;

import java.util.HashMap;
import java.util.List;
import java.util.Date;

import env.Attribute;
import env.Environment;
import mas.abstractAgent;
import mas.behaviours.ObserveBehaviour;
import mas.protocols.IProtocol;
import mas.strategies.IStrategy;
import mas.util.CustomCouple;

public class ExploAgent extends abstractAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7545160765928961044L;
	
	/**
	 * Liste des connaissances pertinentes que l'agent a sur le monde
	 */
	private HashMap<String, CustomCouple<Date, List<Attribute>>> knowledge = new HashMap<String, CustomCouple<Date,List<Attribute>>>();
	private IProtocol protocol;
	// private IStrategy strategy;
	

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]. In the current case, the environment where the agent will evolve
		final Object[] args = getArguments();
		if(args[0]!=null){

			deployAgent((Environment) args[0]);
			protocol = (IProtocol) args[1];
			//protocol.setMyAgent(this);

		}else{
			System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
			System.exit(-1);
		}

		//Add the behaviours
		//TODO
		this.protocol.addBehaviours(this);
		//addBehaviour(new ObserveBehaviour(this, 1000));

		System.out.println("the agent "+this.getLocalName()+ " is started");

	}

	/**
	 * @return 
	 * @return the knowledge
	 */
	public HashMap<String, CustomCouple<Date, List<Attribute>>> getKnowledge() {
		return knowledge;
	}

	/**
	 * @param newKnowledge the knowledge of the other agent used to update the one of this agent
	 */
	public void updateKnowledge(HashMap<String, CustomCouple<Date, List<Attribute>>> newKnowledge) {
		for (String pos: newKnowledge.keySet()) {
			if ((knowledge.containsKey(pos) && knowledge.get(pos).getLeft().compareTo(newKnowledge.get(pos).getLeft()) < 0) || !knowledge.containsKey(pos)) {
				knowledge.put(pos, newKnowledge.get(pos));
			}
		}
		
	}

	/**
	 * @return the protocol
	 */
	public IProtocol getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(IProtocol protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the strategy
	 */
	/*public IStrategy getStrategy() {
		return strategy;
	}*/

	/**
	 * @param strategy the strategy to set
	 */
	/*public void setStrategy(IStrategy strategy) {
		this.strategy = strategy;
	}*/

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){
		
	}
}
