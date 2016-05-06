package src.mas.strategies;

import env.Attribute;
import env.Couple;
import mas.abstractAgent;
import src.mas.agents.AgentExplorateur;
import src.mas.protocols.BlocageProtocol;

import org.graphstream.graph.Graph;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.List;

/**
 * Created by Tigig on 09/03/2016.
 */
public class MoveCauseBlockStrategy implements IStrategy {
    abstractAgent myAgent;
    List<String> otherPath;
    private boolean flag = false;
    private int count = 0;
    private int maxTries = 5;
    private AID otherAgent;
    private List<Couple<String, List<Attribute>>> places;

    public MoveCauseBlockStrategy(abstractAgent myAgent, List<String> otherPath, AID otherAgent) {
    	this.myAgent = myAgent;
        this.otherPath = otherPath;
        this.otherAgent = otherAgent;
        this.places = this.myAgent.observe();
    }

    @Override
    public boolean moveTo(Graph knowledge) {

        if (this.count > this.maxTries)
            return false;

        for (Couple<String, List<Attribute>> couple: this.places) {
            String noeud = couple.getLeft();
            /*if (!((AgentExplorateur) this.myAgent).getProtocol().getClass().getName().equals(BlocageProtocol.class.getName())) {
            	return false;
            }*/
            if (((BlocageProtocol) ((AgentExplorateur) this.myAgent).getProtocol()).getPath().get(0).equals(noeud))
                continue;
            if (otherPath.size() >= 2 && otherPath.get(1).equals(noeud))
                continue;
            if (myAgent.moveTo(noeud)) {
                this.flag = true;
                break;
            }
        }

        if (flag || (!flag && otherPath.size() > 1 && myAgent.moveTo(otherPath.get(1))) ) {
        	//((AgentExplorateur) myAgent).setProtocol(((BlocageProtocol)(((AgentExplorateur) myAgent).getProtocol())).getLastProtocol());
        	sendMessage();
            this.flag = true;
            // TODO attendre que l'autre agent ai bougé
        	return true;
        }
        else
            this.count += 1;
        
        
        return false;
    }
    
    private void sendMessage() {
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		msg.setProtocol("BlocageProtocol");
		msg.addReceiver(otherAgent);
		msg.setSender(this.myAgent.getAID());
		((AgentExplorateur) this.myAgent).sendMessage(msg);
    }

    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
