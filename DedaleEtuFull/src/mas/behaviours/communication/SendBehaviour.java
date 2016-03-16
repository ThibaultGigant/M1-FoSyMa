package mas.behaviours.communication;

import java.io.IOException;

import mas.agents.AgentExplorateur;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public abstract class SendBehaviour extends TickerBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1950231564492255724L;

	public SendBehaviour(final Agent myagent, long period) {
		super(myagent, period);
	}

	@Override
	public void onTick() {
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());

		if (myPosition!=""){
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd  = new ServiceDescription();
			sd.setType( "explorer" ); /* le même nom de service que celui qu'on a déclaré*/
			dfd.addServices(sd);
			            
			DFAgentDescription[] result = {};
			try {
				result = DFService.search(this.myAgent, dfd);
			} catch (FIPAException e1) { e1.printStackTrace(); }
			            
			//System.out.println(result.length + " results" );
			
			//if (result.length>0)
				//System.out.println(" " + result[0].getName() );
			
			
			for (DFAgentDescription fd : result) {
				
				if (!myAgent.getAID().equals(fd.getName())){
					msg.addReceiver(fd.getName());
				}else{
					this.setContent(fd, msg);
				}
	
				((mas.abstractAgent)this.myAgent).sendMessage(msg);
			}
		}

	}

	public abstract void setContent(DFAgentDescription fd, ACLMessage msg);

}