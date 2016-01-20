
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentSenderMult extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//protected List<String> data;
	private int nbOccurs;
	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1 set the agent attributes 
	 *	 		2 add the behaviours
	 *          
	 */
	protected void setup(){

		super.setup();

		//get the parameters given into the object[]
		final Object[] args = getArguments();
		if(args[0]!=null){
			nbOccurs = (Integer) args[0];

		}else{
			System.out.println("Erreur lors du tranfert des parametres");
		}

		//Add the behaviours
		addBehaviour(new SendMessage(this, nbOccurs));

		System.out.println("the sender agent "+this.getLocalName()+ " is started");
		
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

	public class SendMessage extends SimpleBehaviour{
		/**
		 * When an agent choose to communicate with others agents in order to reach a precise decision, 
		 * it tries to form a coalition. This behaviour is the first step of the paxos
		 *  
		 */
		private static final long serialVersionUID = 9088209402507795289L;

		private boolean finished=false;
		private int sentMessages;

		public SendMessage(final Agent myagent, int sentMsg) {
			super(myagent);
			sentMessages = sentMsg;

		}


		public void action() {
			//Create a message in order to send it to the choosen agent
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			//msg.setLanguage(MyOntology.LANGUAGE);
			//msg.setOntology(MyOntology.ONTOLOGY_NAME);
			//msg.setProtocol(MyOntology.PAXOS_PREPARE);
			
			msg.addReceiver(new AID("Agent2", AID.ISLOCALNAME)); // hardcoded= bad, must give it with objtab
			
			Random r = new Random();
			msg.setContent(Integer.toString(r.nextInt()%15));

			this.myAgent.send(msg);
			sentMessages--;
			if (sentMessages == 0) {
				this.finished=true;
				msg.setContent("Done");
				this.myAgent.send(msg);
				this.myAgent.addBehaviour(new ReceiveMessage(this.myAgent));
			}
			System.out.println("----> Message sent to "+msg.getAllReceiver().next()+" ,content= "+msg.getContent());

		}

		public boolean done() {
			return finished;
		}

	}
	
	public class ReceiveMessage extends SimpleBehaviour{
		/**
		 * When an agent choose to communicate with others agents in order to reach a precise decision, 
		 * it tries to form a coalition. This behaviour is the first step of the paxos
		 *  
		 */
		private static final long serialVersionUID = 9088209402507795288L;

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
				//System.out.println("Receiver - No message received");
			}
		}

		public boolean done() {
			return finished;
		}

	}

}
