
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class AgentSender extends Agent{

	protected List<String> data;
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
			data = (List<String>) args[0];

		}else{
			System.out.println("Erreur lors du tranfert des parametres");
		}

		//Add the behaviours
		addBehaviour(new SendMessage(this));

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

		public SendMessage(final Agent myagent) {
			super(myagent);

		}


		public void action() {
			//Create a message in order to send it to the choosen agent
			final ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setSender(this.myAgent.getAID());
			//msg.setLanguage(MyOntology.LANGUAGE);
			//msg.setOntology(MyOntology.ONTOLOGY_NAME);
			//msg.setProtocol(MyOntology.PAXOS_PREPARE);
			
			msg.addReceiver(new AID("Agent1", AID.ISLOCALNAME)); // hardcoded= bad, must give it with objtab
				
			msg.setContent(((AgentSender)this.myAgent).data.get(0));

			this.myAgent.send(msg);
			this.finished=true;
			System.out.println("----> Message sent to "+msg.getAllReceiver().next()+" ,content= "+msg.getContent());

		}

		public boolean done() {
			return finished;
		}

	}

}
