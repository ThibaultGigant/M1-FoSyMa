package mas.behaviours.communication.blocker;

import mas.agents.AgentExplorateur;
import mas.protocols.BlocageProtocol;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

public class NegociateBlockerBehaviour extends SimpleBehaviour {
	
	private Object[] data;
	
	public NegociateBlockerBehaviour(final Agent myagent, Object[] data) {
		//( (BlocageProtocol) ((AgentExplorateur) myagent).getProtocol()).getPath();
		super(myagent);
		this.data = data;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		System.out.println("Timon & Pumba");
	}

	private float valuation() {
		String job = ((AgentExplorateur) this.myAgent).getJob();
		
		float result = 0;
		
		int way = ( (BlocageProtocol) ((AgentExplorateur) this.myAgent).getProtocol()).getPath().size();

		result = (float) (1.0 / (float) way);
		
		int puce;
			
		switch(job) {
		case "hunter":
			puce = 1;
			int capacity = ((AgentExplorateur)this.myAgent).getBackPackFreeSpace();
			result *= capacity;
			break;
			
		case "explorer":
		default:
			puce = -1;
			break;
		}
		
		return puce*result;
	}
	
	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
