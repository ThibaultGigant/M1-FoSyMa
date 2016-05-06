package src.mas.behaviours.communication;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import src.mas.agents.AgentExplorateur;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * Permet la réception de messages contenant des informations sur le graphe, et exécute les modifications nécessaires
 * Created by Tigig on 15/02/2016.
 */
public class ReceiveKnowledgeBehaviour extends SimpleBehaviour {
    public ReceiveKnowledgeBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        final MessageTemplate msgTemplate = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                MessageTemplate.MatchProtocol("Knowledge"));

        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            ACLMessage accuseReception = new ACLMessage(ACLMessage.CONFIRM);
            accuseReception.setProtocol("Knowledge");
            accuseReception.addReceiver(msg.getSender());
            accuseReception.setSender(this.myAgent.getAID());//new AID(this.myAgent.getLocalName(), AID.ISLOCALNAME));
            HashMap<String, HashMap<String, HashMap<String, Object>>> message;
            System.out.println("<----Message received from "+msg.getSender().getLocalName());
            try {
                message = (HashMap<String, HashMap<String, HashMap<String, Object>>>) msg.getContentObject();
                if (message != null) {
                    /*try {
                        System.out.println("Press a key to allow the agent "+this.myAgent.getLocalName() +" to execute its next move");
                        System.in.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    accuseReception.setContentObject((Date) message.get("date").get("date").get("date"));
                    ((AgentExplorateur) this.myAgent).sendMessage(accuseReception);
                    ((AgentExplorateur) this.myAgent).updateKnowledge(message);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Message reception failed");
            }
        }
        /*else{
            System.out.println("Receiver - No message received");
        }*/
    }

    @Override
    public boolean done() {
        return false;
    }
}
