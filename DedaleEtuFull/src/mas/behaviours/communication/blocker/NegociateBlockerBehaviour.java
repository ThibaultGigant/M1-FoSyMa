package mas.behaviours.communication.blocker;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.abstractAgent;
import mas.agents.AgentExplorateur;
import mas.behaviours.move.MoveBehaviour;
import mas.protocols.BlocageProtocol;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import mas.strategies.MoveCauseBlockStrategy;

import java.io.IOException;

public class NegociateBlockerBehaviour extends SimpleBehaviour {

	private Object[] data;
	private int state = 0;
	private int count = 0;
	private int Maxcount = 10;
	private boolean finished = false;

	public NegociateBlockerBehaviour(final Agent myagent, Object[] data) {
		//( (BlocageProtocol) ((AgentExplorateur) myagent).getProtocol()).getPath();
		super(myagent);
		this.data = data;
	}

	@Override
	public void action() {
		
		if (!((AgentExplorateur)this.myAgent).getProtocol().getClass().getName().equals(BlocageProtocol.class.getName())) {
			finished = true;
			return;
		}
		
		switch (state) {
			case 0:
				sendValuation();
				break;
			case 1:
				receiveValuation();
				break;
			case 2:
				waitForOtherToMove();
				break;
			case 3:
				tryToMove();
				break;
			case 4:
				waitForOtherToMove2();
			default:
				sendValuation();
		}




		// Réception acceptance ou pas

	}

	private void sendValuation() {
		Float thisVal = valuation();
		AID receiver = (AID) data[0];
		ACLMessage accuseReception = new ACLMessage(ACLMessage.PROPOSE);
		accuseReception.setProtocol("BlocageProtocol");
		accuseReception.addReceiver(receiver);
		accuseReception.setSender(this.myAgent.getAID());
		try {
			accuseReception.setContentObject(thisVal);
		} catch (IOException e) {
			return;
		}
		((AgentExplorateur) this.myAgent).sendMessage(accuseReception);
		state = 1;
	}

	private void receiveValuation() {
		Float thisVal = valuation();
		Float otherVal;
		AID receiver = (AID) data[0];
		// Réception valuation de l'autre
		final MessageTemplate msgTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
				MessageTemplate.MatchProtocol("BlocageProtocol"));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);

		if (msg != null) {
			try {
				otherVal = (Float) msg.getContentObject();
			} catch (UnreadableException e) {
				return;
			}
	
			if (otherVal < thisVal) {
				// TODO on essage d'avancer
				state = 2;
			}
			else {
				// TODO on essaye de dégager
				state = 3;
			}
		}
	}

	private void waitForOtherToMove() {
		final MessageTemplate msgTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
				MessageTemplate.MatchProtocol("BlocageProtocol"));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null || count >= Maxcount) {
			if (!((AgentExplorateur) this.myAgent).moveTo(((BlocageProtocol)((AgentExplorateur)this.myAgent).getProtocol()).getPath().get(0))) {
				count++;
				if (count < Maxcount)
					return;
			}
			else {
				ACLMessage accuse = new ACLMessage(ACLMessage.CONFIRM);
				accuse.setProtocol("BlocageProtocol");
				accuse.addReceiver((AID)data[0]);
				accuse.setSender(this.myAgent.getAID());
				((AgentExplorateur) this.myAgent).sendMessage(accuse);
			}
				
			if ( ((AgentExplorateur) this.myAgent). getProtocol().getClass().getName().equals(BlocageProtocol.class.getName()) ) {
				finished = true;
			}
			else {
				// TODO revenir à ancien protocol
				((AgentExplorateur) this.myAgent).setProtocol(((BlocageProtocol) ((AgentExplorateur) this.myAgent).getProtocol()).getLastProtocol());
				finished = true;
			}
		}
		count++;
	}

	private void waitForOtherToMove2() {
		final MessageTemplate msgTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
				MessageTemplate.MatchProtocol("BlocageProtocol"));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null || count >= Maxcount) {
			if ( ((AgentExplorateur) this.myAgent). getProtocol().getClass().getName().equals(BlocageProtocol.class.getName()) ) {
				finished = true;
			}
			else {
				// TODO revenir à ancien protocol
				((AgentExplorateur) this.myAgent).setProtocol(((BlocageProtocol) ((AgentExplorateur) this.myAgent).getProtocol()).getLastProtocol());
				finished = true;
			}
		}
		count++;
	}

	private void tryToMove() {
		this.myAgent.addBehaviour(new MoveBehaviour(this.myAgent, 10,
				new MoveCauseBlockStrategy((abstractAgent) this.myAgent, ((BlocageProtocol) ((AgentExplorateur) this.myAgent).getProtocol()).getPath(), (AID) data[0])));

		//((AgentExplorateur) this.myAgent).setProtocol(new MoveCauseBlockStrategy((abstractAgent) this.myAgent, ((BlocageProtocol) ((AgentExplorateur) this.myAgent).getProtocol()).getPath()));
		//finished = true;
		state = 4;
	}



	private float valuation() {
		String job = ((AgentExplorateur) this.myAgent).getJob();

		float result;

		int way = ( (BlocageProtocol) ((AgentExplorateur) this.myAgent).getProtocol()).getPath().size();

		result = (float) (1.0 / (float) way);

		int puce;

		switch(job) {
			case "hunter":
				puce = 1;
				int capacity = ((AgentExplorateur)this.myAgent).getBackPackFreeSpace();
				result *= capacity;
				break;

			case "explorer":
			default:
				puce = -1;
				break;
		}

		return puce*result;
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return finished;
	}

}
