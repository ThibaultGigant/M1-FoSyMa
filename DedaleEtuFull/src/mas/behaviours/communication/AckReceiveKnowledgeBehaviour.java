package src.mas.behaviours.communication;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import src.mas.agents.AgentExplorateur;

import java.util.Date;

/**
 * Created by Tigig on 17/02/2016.
 */
public class AckReceiveKnowledgeBehaviour extends SimpleBehaviour {
    public AckReceiveKnowledgeBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        final MessageTemplate msgTemplate = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                MessageTemplate.MatchProtocol("Knowledge"));

        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            //System.out.println("<----Accusé Réception from "+msg.getSender().getLocalName() + " reçu");
            try {
                Date date = (Date) msg.getContentObject();
                String agentID = msg.getSender().getLocalName();
                ((AgentExplorateur) this.myAgent).updateLastCommunication(agentID, date);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Accusé réception mal écrit");
            }
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
