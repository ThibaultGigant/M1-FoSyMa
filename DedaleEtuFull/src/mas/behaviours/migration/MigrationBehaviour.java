package src.mas.behaviours.migration;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.behaviours.SimpleBehaviour;

public class MigrationBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 827450773440666091L;

	public MigrationBehaviour(Agent a) {
		super(a);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		ContainerID loc = new ContainerID();
		loc.setName("MyDistantContainer0");
		loc.setPort("8888");
		loc.setAddress("132.227.112.239");
		
		this.myAgent.doMove(loc);
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return true;
	}

}
