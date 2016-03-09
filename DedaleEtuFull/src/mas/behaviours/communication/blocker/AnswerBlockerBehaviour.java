package mas.behaviours.communication.blocker;

import java.util.Date;
import java.util.HashMap;

import mas.agents.AgentExplorateur;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AnswerBlockerBehaviour extends SimpleBehaviour {

	private String destination;
	
	public AnswerBlockerBehaviour(final mas.abstractAgent myagent, String destination) {
		super(myagent);
		this.destination = destination;
	}
	
	@Override
	public void action() {
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF);

        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            ACLMessage accuseReception = new ACLMessage(ACLMessage.CONFIRM);
            accuseReception.addReceiver(msg.getSender());
            accuseReception.setSender(this.myAgent.getAID());//new AID(this.myAgent.getLocalName(), AID.ISLOCALNAME));
            Object[] data;
            System.out.println("<----Message received from "+msg.getSender().getLocalName());
            try {
                data = (Object[]) msg.getContentObject();
                if (data != null) {
                	AID senderIAD = (AID) data[0];
                	String senderPosition = (String) data[1];
                	String senderDestination = (String) data[2];
                	
                	// L'agent vérifie que c'est bien lui qui bloque
                	if (!senderDestination.equals(((mas.abstractAgent)this.myAgent).getCurrentPosition())) {
                		return;
                	}
                	
                	Object[] dataContent = { ((mas.abstractAgent)this.myAgent).getAID() , ((mas.abstractAgent)this.myAgent).getCurrentPosition(), this.destination };
                	
                    accuseReception.setContentObject(dataContent);
                    ((AgentExplorateur) this.myAgent).sendMessage(accuseReception);
                    
                    // TODO add NegociateBlockerBehaviour(data)
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Message reception failed");
            }
        }
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
