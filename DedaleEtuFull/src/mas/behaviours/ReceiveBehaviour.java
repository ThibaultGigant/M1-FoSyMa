package mas.behaviours;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import mas.agents.AgentExplorateur;

import java.util.HashMap;

/**
 * Permet la réception de messages contenant des informations sur le graphe, et exécute les modifications nécessaires
 * Created by Tigig on 15/02/2016.
 */
public class ReceiveBehaviour extends SimpleBehaviour {
    public ReceiveBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

        final ACLMessage msg = this.myAgent.receive(msgTemplate);
        HashMap<String, HashMap<String, HashMap<String, Object>>> message;

        if (msg != null) {
            System.out.println("<----Message received from "+msg.getSender());
            try {
                Object uncastMessage = msg.getContentObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                message = (HashMap<String, HashMap<String, HashMap<String, Object>>>) msg.getContentObject();
                if (message != null)
                    ((AgentExplorateur) this.myAgent).updateKnowledge(message);
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
