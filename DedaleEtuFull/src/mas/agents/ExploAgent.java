package mas.agents;

import java.util.Date;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import env.Environment;
import mas.abstractAgent;
import mas.protocols.IProtocol;

public class ExploAgent extends abstractAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7545160765928961044L;
	
	/**
	 * Liste des connaissances pertinentes que l'agent a sur le monde
	 */
	private Graph knowledge = new SingleGraph("knowledge");
	private IProtocol protocol;
	

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
		this.protocol.addBehaviours(this);

		System.out.println("the agent "+this.getLocalName()+ " is started");

		String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
		String nodeStyle_wumpus= "node.wumpus {"+"fill-color: red;"+"}";
		String nodeStyle_agent= "node.agent {"+"fill-color: blue;"+"}";
		String nodeStyle_treasure="node.treasure {"+"fill-color: yellow;"+"}";
		String nodeStyle_EntryExit="node.exit {"+"fill-color: green;"+"}";
		
		String nodeStyle=defaultNodeStyle+nodeStyle_wumpus+nodeStyle_agent+nodeStyle_treasure+nodeStyle_EntryExit;
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		this.getKnowledge().setAttribute("ui.stylesheet",nodeStyle);
		this.getKnowledge().display();
	}

	/**
	 * @return 
	 * @return the knowledge
	 */
	public Graph getKnowledge() {
		return knowledge;
	}

	/**
	 * @param newKnowledge the knowledge of the other agent used to update the one of this agent
	 */
	public boolean updateKnowledge(Graph newKnowledge) {
		String id;
		Node currentNode;
		
		boolean flag = false;
		
		for (Node node : newKnowledge.getEachNode()) {
			id = node.getId();
			currentNode = knowledge.getNode(id);
			if (currentNode == null) {
				knowledge.addNode(id);
				for (String attr : node.getAttributeKeySet()) {
					knowledge.getNode(id).setAttribute(attr, (Object) node.getAttribute(attr));
				}
				flag = true;
			}
			else if (((Date) node.getAttribute("date")).compareTo(currentNode.getAttribute("date")) <= 0) {
				continue;
			}
			else if ((int) node.getAttribute("visited") > (int) currentNode.getAttribute("visited") ) {
				for (String attr : node.getAttributeKeySet()) {
					currentNode.setAttribute(attr, node.getAttribute(attr));
				}
				flag = true;
			}
		}
		
		for (Edge edge : newKnowledge.getEachEdge()) {
			try {
				knowledge.addEdge(edge.getId(), edge.getNode0().getId(), edge.getNode1().getId());
			}
			catch (Exception e) {
				continue;
			}
		}
		
		return flag;
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
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){
		
	}
}
