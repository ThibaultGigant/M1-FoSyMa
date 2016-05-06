package src.mas.behaviours.communication.blocker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import mas.abstractAgent;
import src.mas.agents.AgentExplorateur;
import src.mas.protocols.IProtocol;
import src.mas.protocols.BlocageProtocol;
import src.mas.behaviours.communication.blocker.MoveBlock;
import src.mas.behaviours.communication.blocker.BlocageProcedure;

public class BlockerBehaviour extends SimpleBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2300248729215239486L;
	
	private List<String> path;
	private String myPosition;
	private boolean finished = false;

	private boolean confirmDone = false;
	
	// Information concernant les agents qui sont bloqués par cet agent
	private List<Object[]> othersAgentInfo = new ArrayList<Object[]>();
	
	// Information concernant l'agent qui bloque cet aggent
	private Object[] blockerAgentInfo;

	// Information concernant l'agent avec qui celui-ci négociera au final
	private Object[] finalBlockerInfo;

	// Classes contenant les fonctions de communications et de mouvement
	private BlocageProcedure blocageProcedure = null;
	private MoveBlock moveBlock = null;
	
	/**
	 * 0 : Informe son entourage qu'il est bloqué
	 * 1 : Vérifie si quelqu'un de son entourage indique qu'il est bloqué
	 * 2 : Vérifie si quelqu'un à répondu à son appel
	 * 3 : Determine qui doit laisser passer l'autre
	 * 4 : L'agent doit laisser la place à l'autre
	 * 5 : L'agent attend que l'autre lui laisse la place
	 * 6 : En cas d'égalité, procédure de choix aléatoire
	 */
	private int state = 0;
	
	public BlockerBehaviour(final mas.abstractAgent myagent, List<String> path) {
		super(myagent);
		this.path = path;
		this.myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		blocageProcedure = new BlocageProcedure((mas.abstractAgent)this.myAgent, this.path, this.othersAgentInfo);
	}
	
	@Override
	public void action() {

		int flag;

		if (this.state < 3 && findBlocker()) {
			this.state = 3;
		}
		
		switch (this.state) {
		// Informe son entourage qu'il est bloqué
		case 0:
			if (this.blockerAgentInfo == null) {
				this.blocageProcedure.Ask();
			}
			this.state = 1;
			break;
		// Vérifie si quelqu'un de son entourage indique qu'il est bloqué
		case 1:
			/**
			 * Possibilité de bloquer plusieurs autres agents
			 * Nombre d'itération limitée
			 */
			if (this.blocageProcedure.Answer())
				this.state = 2;
			else
				this.state = 3;
			break;
		// Vérifie si quelqu'un à répondu à son appel
		case 2:
			if (this.blockerAgentInfo == null) {
				this.blockerAgentInfo = this.blocageProcedure.Confirm();
			}
			this.state = 0;
			break;
		// Determine qui doit laisser passer l'autre
		case 3:
			// TODO Si on a pas eu de reponse, on cherche un autre chemin qui ne passe pas par la salle inaccessible
			// TODO Si findBlocker renvoie faux, et qu'il y a eu au moins un agent qui est bloqué par celui-ci, choisir ...

			if (findBlocker()) {
				System.out.println("Find Blocker");
				this.finalBlockerInfo = this.blockerAgentInfo;
				if (moveBlock == null)
					this.moveBlock = new MoveBlock((mas.abstractAgent)this.myAgent, (List<String>) this.finalBlockerInfo[2], (AID) this.finalBlockerInfo[0], this.path);

				flag = hasPriorite();

				// attend que l'autre agent lui confirme d'avoir bougé
				if (flag == 1) {
					this.state = 5;
				}

				// laisser la place libre à l'autre agent et lui envoyer un message quand c'est fait, ou s'il est bloqué
				else if (flag == -1)
					this.state = 4;

				// Choisir aléatoirement
				else {
					System.out.println(this.myAgent.getLocalName() + " | ----------------------------- Timon -----------------------------");
					this.moveBlock.shiFuMi();
					this.state = 6;
				}
			}
			else {
				finish();
			}

			break;
		// L'agent doit laisser la place à l'autre, puis doit attendre qu'il se soit bien déplacé
		case 4:
			//System.out.println(this.myAgent.getLocalName() + " | 4");
			// Si on n'a pas encore laissé la place
			if (((mas.abstractAgent)this.myAgent).getCurrentPosition().equals(this.myPosition)) {
				//System.out.println(this.myAgent.getLocalName() + " | TryToMove");
				flag = this.moveBlock.tryToMove();
				// Nombre d'essaie limite atteinte
				if (flag == -1) {
					finish();
				}
			}
			// Si l'on doit attendre que l'autre se déplace
			else {
				//System.out.println(this.myAgent.getLocalName() + " | WaitOtherToMove");
				flag = moveBlock.waitOtherToMove();
				if (flag != 0) {
					finish();
				}
			}
			break;
		// L'agent attend que l'autre lui laisse la place
		case 5:
			//System.out.println(this.myAgent.getLocalName() + " | 5");
			//System.out.println(this.myAgent.getLocalName() + " | WaitToMove");
			flag = this.moveBlock.waitToMove();
			if (flag != 0) {
				finish();
			}
			break;
		// En cas d'égalité, procédure de choix aléatoire
		case 6:
			flag = moveBlock.whoWin();
			switch (flag) {
			// Gagné
			case 1:
				this.state = 5;
				break;
			// Egalité
			case 0:
				break;
			// Perdu
			case -1:
				this.state = 4;
				break;
			// Pas de message reçu
			case -2:
				break;
			// Nombre d'essaie limite atteinte
			case -3:
				finish();
				break;
			}
			break;
		}

		
	}
	
	/**
	 * Fonction utilisée en début de "action"
	 * @return		: • vraie si l'agent qui bloque est bloqué par cet agent
	 * 				  • faux sinon
	 */
	private boolean findBlocker() {
		if (blockerAgentInfo == null)
			return false;

		for (Object[] agentInfo : this.othersAgentInfo) {
			if (((AID) agentInfo[0]).equals((AID)blockerAgentInfo[0]))
				return true;
		}
		return false;
	}

	private int hasPriorite() {
		float diff = valuation() - (float) this.finalBlockerInfo[3];
		if (diff == 0f) {
			// TODO choisir aléatoirement
			return 0;
		}
		else if (diff > 0f) {
			//System.out.println(diff);
			return 1;
		}
		return -1;
	}

	private float valuation() {
		String job = ((AgentExplorateur) this.myAgent).getJob();

		float result;

		int way = this.path.size();//( (BlocageProtocol) ((AgentExplorateur) this.myAgent).getProtocol()).getPath().size();

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

	private boolean clearBlockerMessages() {
		// Description du message à lire
		MessageTemplate msgTemplate = MessageTemplate.MatchProtocol("BlocageProtocol");

		// Récupération du message
		//System.out.println(this.myAgent);
		ACLMessage msg = this.myAgent.receive(msgTemplate);
		if (msg != null)
			return true;

		msgTemplate = MessageTemplate.MatchProtocol("BlocageProtocol | TryToMove");

		// Récupération du message
		msg = this.myAgent.receive(msgTemplate);
		if (msg != null)
			return true;

		msgTemplate = MessageTemplate.MatchProtocol("BlocageProtocol | WaitToMove");

		// Récupération du message
		msg = this.myAgent.receive(msgTemplate);
		if (msg != null)
			return true;

		return false;
	}

	private void finish() {
		// Supprimer tous les message reçus concernant les blocages
		while (clearBlockerMessages()) {}

		IProtocol lastProtocol = ((BlocageProtocol)((AgentExplorateur)this.myAgent).getProtocol()).getLastProtocol();
		((AgentExplorateur)this.myAgent).setProtocol(lastProtocol);
		this.finished = true;
	}

	@Override
	public boolean done() {
		return this.finished;
	}

}
