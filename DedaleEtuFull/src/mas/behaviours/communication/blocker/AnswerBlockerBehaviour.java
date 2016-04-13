package mas.behaviours.communication.blocker;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mas.agents.AgentExplorateur;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AnswerBlockerBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8390804184844220786L;

	private List<String> path;
	
	private boolean finished = false;
	
	private Object[] data;
	
	public AnswerBlockerBehaviour(final Agent myagent, List<String> path) {
		super(myagent);
		this.path = path;
	}
	
	@Override
	public void action() {
		final MessageTemplate msgTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF),
				MessageTemplate.MatchProtocol("BlocageProtocol"));

        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            ACLMessage accuseReception = new ACLMessage(ACLMessage.CONFIRM);
            accuseReception.setProtocol("BlocageProtocol");
            accuseReception.addReceiver(msg.getSender());
            accuseReception.setSender(this.myAgent.getAID());//new AID(this.myAgent.getLocalName(), AID.ISLOCALNAME));
            Object[] data;
            System.out.println("<----Message received from "+msg.getSender().getLocalName());
            try {
                data = (Object[]) msg.getContentObject();
                if (data != null) {
                	// Stock data
                	this.data = data;
                	
                	//AID senderIAD = (AID) data[0];
                	//String senderPosition = (String) data[1];
                	String senderDestination = ((List<String>) data[2]).get(0);
                	
                	// L'agent vérifie que c'est bien lui qui bloque
                	if (!senderDestination.equals(((mas.abstractAgent)this.myAgent).getCurrentPosition())) {
                		return;
                	}
                	
                	Object[] dataContent = { ((mas.abstractAgent)this.myAgent).getAID() , ((mas.abstractAgent)this.myAgent).getCurrentPosition(), this.path };
                	
                    accuseReception.setContentObject(dataContent);
                    ((AgentExplorateur) this.myAgent).sendMessage(accuseReception);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Message reception failed");
            }
            this.finished = true;
        }
	}

	@Override
	public boolean done() {
		if (this.finished)
			this.myAgent.addBehaviour(new NegociateBlockerBehaviour(this.myAgent, data));
		return this.finished;
	}

}
