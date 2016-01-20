
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

public class AgentReceiverB extends Agent{
	
	private HashMap<String, Integer> results = new HashMap<String, Integer>();
	
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args.length==0){
			System.out.println("Erreur lors de la creation du receveur");

		}
		
		//Add the behaviours
		addBehaviour(new ReceiveMessage(this, (Integer) args[0]));

		System.out.println("the receiver agent "+this.getLocalName()+ " is started");

	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

	}

	
	/**************************************
	 * 
	 * 
	 * 				BEHAVIOURS
	 * 
	 * 
	 **************************************/

	public class ReceiveMessage extends SimpleBehaviour{
		/**
		 * When an agent choose to communicate with others agents in order to reach a precise decision, 
		 * it tries to form a coalition. This behaviour is the first step of the paxos
		 *  
		 */
		private static final long serialVersionUID = 9088209402507795289L;

		private boolean finished=false;
		private int nbOccurs;
		

		public ReceiveMessage(final Agent myagent, Integer nbOccurs) {
			super(myagent);
			this.nbOccurs = nbOccurs;
		}


		public void action() {
			//1) receive the message
			final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				//MessageTemplate.and(
					//MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM),
					//MessageTemplate.and(
					//		MessageTemplate.MatchProtocol(MyOntology.PAXOS_QUIT_COALITION),
					//		MessageTemplate.and(
					//				MessageTemplate.MatchLanguage(MyOntology.LANGUAGE),
					//				MessageTemplate.MatchOntology(MyOntology.ONTOLOGY_NAME))
					//)
			

			final ACLMessage msg = this.myAgent.receive(msgTemplate);
			if (msg != null) {
				String sender = msg.getSender().getLocalName();
				if (msg.getContent().equals("Done")) {
					nbOccurs--;
					ACLMessage m = new ACLMessage(ACLMessage.INFORM);
					m.setSender(this.myAgent.getAID());
					m.addReceiver(new AID(sender, AID.ISLOCALNAME));
					m.setContent(results.get(sender).toString());
					this.myAgent.send(m);
					
					if (nbOccurs == 0)
						this.finished=true;
				}
				else {
					if (results.containsKey(sender)) {
						results.put(sender, results.get(sender) + Integer.parseInt(msg.getContent()));
					}
					else
						results.put(sender, Integer.parseInt(msg.getContent()));
				}
				
			}else{
				//System.out.println("Receiver - No message received");
			}
		}

		public boolean done() {
			return finished;
		}

	}

}
