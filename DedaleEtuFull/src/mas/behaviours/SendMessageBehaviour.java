package mas.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import mas.agents.AgentExplorateur;

import java.io.IOException;

public class SendMessageBehaviour extends TickerBehaviour{

	/**
	 * An agent tries to contact its friend and to give him its current position
	 * @param myagent the agent who posses the behaviour
	 *
	 */
	public SendMessageBehaviour(final Agent myagent, long period) {
		super(myagent, period);
	}

	@Override
	public void onTick() {
		String myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();

		ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());

		if (myPosition!=""){
			System.out.println("Agent "+this.myAgent.getLocalName()+ " is trying to reach its friends");
			//msg.setContent("Hello World, I'm at "+myPosition);
			if (!myAgent.getLocalName().equals("Explo1")){
				msg.addReceiver(new AID("Explo1",AID.ISLOCALNAME));
			}else{
				try {
					msg.setContentObject(((AgentExplorateur) this.myAgent).getKnowledge().shareKnowledge("Explo1"));
				} catch (IOException e) {
					System.out.println("L'envoi a raté gros noeud");
					e.printStackTrace();
				}
				msg.addReceiver(new AID("Explo2",AID.ISLOCALNAME));
			}

			((mas.abstractAgent)this.myAgent).sendMessage(msg);

		}

	}

}