package src.mas.behaviours.communication.blocker;

import java.util.List;

import src.mas.agents.AgentExplorateur;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ConfirmBlockerBehaviour extends SimpleBehaviour {
	
	private boolean finished = false;
	
	
	
	public ConfirmBlockerBehaviour(final Agent myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		final MessageTemplate msgTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
				MessageTemplate.MatchProtocol("BlocageProtocol"));

        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            Object[] data;
            System.out.println("<----Message received from "+msg.getSender().getLocalName());
            try {
                data = (Object[]) msg.getContentObject();
                if (data != null) {
                	AID senderIAD = (AID) data[0];
                	String senderPosition = (String) data[1];
                	String senderDestination = ((List<String>) data[2]).get(0);
                	
                	// L'agent vérifie que c'est bien lui qui bloque
                	if (!senderDestination.equals(((mas.abstractAgent)this.myAgent).getCurrentPosition())) {
                		return;
                	}

                	
            		this.myAgent.addBehaviour(new NegociateBlockerBehaviour(this.myAgent, data));
            		this.finished = true;
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
		return this.finished;
	}

}
