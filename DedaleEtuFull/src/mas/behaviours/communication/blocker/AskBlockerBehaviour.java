package mas.behaviours.communication.blocker;

import java.io.IOException;
import java.util.List;

import mas.agents.AgentExplorateur;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AskBlockerBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2300248729215239486L;
	
	private List<String> path;
	
	public AskBlockerBehaviour(final mas.abstractAgent myagent, List<String> path) {
		super(myagent);
		this.path = path;
	}
	
	@Override
	public void action() {
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		ACLMessage msg=new ACLMessage(ACLMessage.INFORM_IF);
		msg.setProtocol("BlocageProtocol");
		msg.setSender(this.myAgent.getAID());

		if (myPosition!=""){
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd  = new ServiceDescription();
			sd.setType( "explorer" ); // TODO to change ?
			dfd.addServices(sd);
			            
			DFAgentDescription[] result = {};
			try {
				result = DFService.search(this.myAgent, dfd);
			} catch (FIPAException e1) { e1.printStackTrace(); }			
			
			for (DFAgentDescription fd : result) {
				
				if (!myAgent.getAID().equals(fd.getName())){
					msg.addReceiver(fd.getName());
				}else{
					this.setContent(fd, msg);
				}
	
			}

			((mas.abstractAgent)this.myAgent).sendMessage(msg);
		}
	}

	private void setContent(DFAgentDescription fd, ACLMessage msg) {
		System.out.println("Piano\n");
		Object[] data = { ((mas.abstractAgent)this.myAgent).getAID() , ((mas.abstractAgent)this.myAgent).getCurrentPosition(), this.path };
		try {
			msg.setContentObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean done() {
		// TODO add NegociateBlockerBehaviour
		this.myAgent.addBehaviour(new ConfirmBlockerBehaviour(this.myAgent));
		return true;
	}

}
