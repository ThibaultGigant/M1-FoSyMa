package src.mas.behaviours.communication;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import src.mas.agents.AgentExplorateur;

import java.io.IOException;

public class SendKnowledgeBehaviour extends SendBehaviour {

	/**
	 * An agent tries to contact its friend and to give him its current position
	 * @param myagent the agent who posses the behaviour
	 *
	 */
	public SendKnowledgeBehaviour(final Agent myagent, long period) {
		super(myagent, period);
	}

	@Override
	public void setContent(DFAgentDescription fd, ACLMessage msg) {
		try {
			msg.setProtocol("Knowledge");
			msg.setContentObject(((AgentExplorateur) this.myAgent).getKnowledge().shareKnowledge(fd.getName().getLocalName()));
		} catch (IOException e) {
			System.out.println("L'envoi a raté gros noeud");
			e.printStackTrace();
		}
	}

}