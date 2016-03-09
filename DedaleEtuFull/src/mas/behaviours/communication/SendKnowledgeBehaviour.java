package mas.behaviours.communication;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import mas.agents.AgentExplorateur;

import java.io.IOException;

public class SendKnowledgeBehaviour extends TickerBehaviour{

	/**
	 * An agent tries to contact its friend and to give him its current position
	 * @param myagent the agent who posses the behaviour
	 *
	 */
	public SendKnowledgeBehaviour(final Agent myagent, long period) {
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
			} catch (FIPAException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			            
			//System.out.println(result.length + " results" );
			
			//if (result.length>0)
				//System.out.println(" " + result[0].getName() );
			
			
			for (DFAgentDescription fd : result) {
				
				if (!myAgent.getAID().equals(fd.getName())){
					msg.addReceiver(fd.getName());
				}else{
					try {
						msg.setContentObject(((AgentExplorateur) this.myAgent).getKnowledge().shareKnowledge(fd.getName().getLocalName()));
					} catch (IOException e) {
						System.out.println("L'envoi a raté gros noeud");
						e.printStackTrace();
					}
				}
	
				((mas.abstractAgent)this.myAgent).sendMessage(msg);
			}
		}

	}

}