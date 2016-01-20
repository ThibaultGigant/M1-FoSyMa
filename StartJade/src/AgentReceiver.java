
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class AgentReceiver extends Agent{
	
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args.length!=0){
			System.out.println("Erreur lors de la creation du receveur");

		}

		//Add the behaviours
		addBehaviour(new ReceiveMessage(this));

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

		public ReceiveMessage(final Agent myagent) {
			super(myagent);

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
				System.out.println("<----Message received from "+msg.getSender()+" ,content= "+msg.getContent());
				this.finished=true;
			}else{
				System.out.println("Receiver - No message received");
			}
		}

		public boolean done() {
			return finished;
		}

	}

}
