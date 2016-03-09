package mas.behaviours.communication.blocker;

import mas.agents.AgentExplorateur;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ConfirmBlockerBehaviour extends SimpleBehaviour {

	@Override
	public void action() {
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);

        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
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
