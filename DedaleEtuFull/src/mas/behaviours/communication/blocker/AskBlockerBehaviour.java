package mas.behaviours.communication.blocker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mas.agents.AgentExplorateur;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AskBlockerBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2300248729215239486L;
	
	private List<String> path;
	private String myPosition;
	private boolean finished = false;
	
	
	private int counter = 0;
	private int maxTry = 10;
	private boolean confirmDone = false;
	
	// Information concernant les agents qui sont bloqués par cet agent
	private List<Object[]> othersAgentInfo = new ArrayList<Object[]>();
	
	// Information concernant l'agent qui bloque cet aggent
	private Object[] blockerAgentInfo;
	
	/**
	 * 0 : Informe son entourage qu'il est bloqué
	 * 1 : Vérifie si quelqu'un de son entourage indique qu'il est bloqué
	 * 2 : Vérifie si quelqu'un à répondu à son appel
	 */
	private int state = 0;
	
	public AskBlockerBehaviour(final mas.abstractAgent myagent, List<String> path) {
		super(myagent);
		this.path = path;
		this.myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
	}
	
	@Override
	public void action() {
		
		if (findBlocker()) {
			this.state = 3;
		}
		
		switch (this.state) {
		case 0:
			if (!confirmDone)
				Ask();
			this.state = 1;
			break;
		case 1:
			/**
			 * Possibilité de bloquer plusieurs autres agents
			 * Nombre d'itération limitée
			 */
			if (Answer())
				this.state = 2;
			else
				this.state = 3;
			break;
		case 2:
			if (!this.confirmDone)
				Confirm();
			this.state = 0;
			break;
		case 3:
			// TODO Si on a pas eu de reponse, on cherche un autre chemin qui ne passe pas par la salle inaccessible
			//		Si findBlocker renvoie faux, et qu'il y a eu au moins un agent qui est bloqué par celui-ci, choisir ...
			break;
		}

		
	}
	
	private void Ask() {
		ACLMessage msg=new ACLMessage(ACLMessage.INFORM_IF);
		msg.setProtocol("BlocageProtocol");
		msg.setSender(this.myAgent.getAID());

		if (this.myPosition!=""){
			DFAgentDescription dfd = new DFAgentDescription();
			ServiceDescription sd  = new ServiceDescription();
			sd.setType( "explorer" ); // TODO to change ?
			dfd.addServices(sd);
			            
			DFAgentDescription[] result = {};
			try {
				result = DFService.search(this.myAgent, dfd);
			} catch (FIPAException e1) { e1.printStackTrace(); }			
			
			
			for (DFAgentDescription fd : result)
				if (!myAgent.getAID().equals(fd.getName()))
					msg.addReceiver(fd.getName());
			
			
			this.setContent(msg);

			((mas.abstractAgent)this.myAgent).sendMessage(msg);
		}
	}

	/**
	 * Utilisé dans la fonction Ask
	 * • Remplie le contenu du message à envoyer
	 * @param msg	: Message à remplir
	 */
	private void setContent(ACLMessage msg) {
		System.out.println("Piano\n");
		Object[] data = { ((mas.abstractAgent)this.myAgent).getAID() , ((mas.abstractAgent)this.myAgent).getCurrentPosition(), this.path };
		try {
			msg.setContentObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean Answer() {
		this.counter ++;
		
		// Description du message à lire
		final MessageTemplate msgTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.INFORM_IF),
				MessageTemplate.MatchProtocol("BlocageProtocol"));

		// Récupération du message
        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        // Si le message voulu à été reçu
        if (msg != null) {
        	Object[] data;
            try {
            	// On récupère le contenu du message
                data = (Object[]) msg.getContentObject();
                if (data != null) {
                	
                	//AID senderIAD = (AID) data[0];
                	//String senderPosition = (String) data[1];
                	String senderDestination = ((List<String>) data[2]).get(0);
                	
                	// L'agent vérifie que c'est bien lui qui bloque
                	if (!senderDestination.equals(((mas.abstractAgent)this.myAgent).getCurrentPosition()) || agentAlreadyCheck(data)) {
                		return (this.counter < this.maxTry);
                	}
                	
                	// Stock les données concernant l'autre agent qui est bloqué cet agent
                	this.othersAgentInfo.add(data);
                	
                	// Création du message de confirmation
                	ACLMessage accuseReception = new ACLMessage(ACLMessage.CONFIRM);
                    accuseReception.setProtocol("BlocageProtocol");
                    accuseReception.addReceiver(msg.getSender());
                    accuseReception.setSender(this.myAgent.getAID());//new AID(this.myAgent.getLocalName(), AID.ISLOCALNAME));
                    System.out.println("<----Message received from "+msg.getSender().getLocalName());
                	
                    // Création du contenu du message
                	Object[] dataContent = { ((mas.abstractAgent)this.myAgent).getAID() , ((mas.abstractAgent)this.myAgent).getCurrentPosition(), this.path };
                    accuseReception.setContentObject(dataContent);

                    // Envoie du message
                    ((AgentExplorateur) this.myAgent).sendMessage(accuseReception);
                    
                    // Confirmation envoyé
                    // TODO
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Message reception failed");
            }
        }
        return (this.counter < this.maxTry);
	}
	
	/**
	 * Fonction utilisée dans Answer
	 * @param data	: Informations de l'agent qui a envoyé le nouveau message
	 * @return		: • vraie si l'agent à déjà envoyé un message
	 * 				  • faux sinon
	 */
	private boolean agentAlreadyCheck(Object[] data) {
		AID newAID = (AID) data[0];
		
		for (Object[] agentInfo : this.othersAgentInfo) {
			if (newAID.equals((AID) agentInfo[0]))
				return true;
		}
		
		return false;
	}
	
	private void Confirm() {
		// Description du message à lire
		final MessageTemplate msgTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
				MessageTemplate.MatchProtocol("BlocageProtocol"));

		// Récupération du message à lire
        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            Object[] data;
            System.out.println("<----Message received from "+msg.getSender().getLocalName());
            try {
            	// Récupération des données du message
                data = (Object[]) msg.getContentObject();
                if (data != null) {
                	AID senderIAD = (AID) data[0];
                	String senderPosition = (String) data[1];
                	String senderDestination = ((List<String>) data[2]).get(0);
                	
                	// L'agent vérifie que c'est bien lui qui bloque
                	if (!senderDestination.equals(((mas.abstractAgent)this.myAgent).getCurrentPosition())) {
                		return;
                	}

                	// Stock les données concernant l'autre agent qui bloque cet agent
                	this.blockerAgentInfo = data;
                	
                	// Confirmation reçu
            		this.confirmDone = true;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Message reception failed");
            }
        }
	}
	
	/**
	 * Fonction utilisée en début de "action"
	 * @return		: • vraie si l'agent qui bloque est bloqué par cet agent
	 * 				  • faux sinon
	 */
	private boolean findBlocker() {
		for (Object[] agentInfo : this.othersAgentInfo) {
			if (((AID) agentInfo[0]).equals((AID)blockerAgentInfo[0]))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean done() {
		// TODO add NegociateBlockerBehaviour
		this.myAgent.addBehaviour(new ConfirmBlockerBehaviour(this.myAgent));
		return this.finished;
	}

}
