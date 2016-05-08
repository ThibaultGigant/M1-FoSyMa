package src.mas.behaviours.blocker;

import java.util.ArrayList;
import java.util.List;

import env.Attribute;
import env.Couple;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import src.mas.agents.AgentExplorateur;
import src.mas.protocols.IProtocol;
import src.mas.protocols.BlocageProtocol;
import src.mas.behaviours.blocker.MoveBlock;
import src.mas.behaviours.blocker.BlocageProcedure;
import src.mas.util.Debug;

public class BlockerBehaviour extends SimpleBehaviour {


	private static final long serialVersionUID = 2300248729215239486L;

	/**
	 * path					: Chemin que souhaite emprunter l'agent
	 * myPosition			: Position de l'agent
	 * finished				: Booléen indiquant si le behaviour doit ou non se terminer
	 * myValue				: Valeur de priorité
	 *
	 * othersAgentsInfo		: Liste des agents que l'on bloque
	 * blockerAgentInfo		: L'agent avec qui nous souhaitons négocier
	 * finalAgentInfo		: L'agent avec qui nous allons tenter de négocier
	 * blockers				: Liste des agents qui nous ont bloqués et à qui on essaie de laisser la place
	 * casesToAvoid			: Liste des cases à éviter
	 *
	 * blocageProcedure		: Class gérant les communications principales
	 * moveBlock			: Class gérant les mouvements principaux
	 */
	private List<String> path;
	private String myPosition;
	private boolean finished = false;
	private float myValue;

	private List<Object[]> othersAgentInfo = new ArrayList<Object[]>(); // Information concernant les agents qui sont bloqués par cet agent
	private Object[] blockerAgentInfo; // Information concernant l'agent qui bloque cet aggent
	private Object[] finalBlockerInfo; // Information concernant l'agent avec qui celui-ci négociera au final
	private List<Object[]> blockers = new ArrayList<Object[]>();
	private List<String> casesToAvoid = new ArrayList<String>();

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

	private boolean debugFlag = true;

	public BlockerBehaviour(final mas.abstractAgent myagent, List<String> path) {
		super(myagent);
		this.path = path;
		this.myPosition=((mas.abstractAgent)this.myAgent).getCurrentPosition();
		blocageProcedure = new BlocageProcedure((mas.abstractAgent)this.myAgent, this.path, this.othersAgentInfo);
		this.moveBlock = new MoveBlock((mas.abstractAgent)this.myAgent, null, null, this.path);
		this.myValue = valuation();
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
			if (this.blockerAgentInfo == null)
				this.blocageProcedure.Ask();

			this.state = 1;
			break;

		// Vérifie si quelqu'un de son entourage indique qu'il est bloqué
		case 1:
			if (this.blocageProcedure.Answer())
				this.state = 2;
			else
				this.state = 3;
			break;

		// Vérifie si quelqu'un à répondu à son appel
		case 2:
			if (this.blockerAgentInfo == null) {
				this.blockerAgentInfo = this.blocageProcedure.Confirm();
				if (this.blockerAgentInfo == null) {
					this.state = 3;
					break;
				}
			}
			this.state = 0;
			break;

		// Determine qui doit laisser passer l'autre
		case 3:
			// Si on bloque l'agent qui nous bloque
			if (findBlocker()) {
				/**
				 * GoTo	state = 4 si on doit laisser le passage
				 * 		state = 5 si on a la priorité
				 * 		state = 6 si on doit choisir aléatoirement
				 */
				negociation();
			}
			// Sinon, si on bloque au moins un autre agent
			else if (this.blockerAgentInfo == null && this.othersAgentInfo.size() != 0) {
				this.state = 7;
			}

			/**
			 * Si on ne bloque personne et personne ne semble nous bloquer
			 * Essayer de se déplacer là où l'on souhaiter aller
			 * Sinon, chercher un nouveau chemin qui ne passe pas par la case bloquante
 			 */
			else {
				if (((mas.abstractAgent)this.myAgent).moveTo(this.path.get(0)))
					this.path.remove(0);
				else {
					// Eviter la case que l'on souhaitait atteindre
					this.casesToAvoid.add(this.path.get(0));
					this.path.clear();
				}
				finish();
			}

			break;

		// L'agent doit laisser la place à l'autre, puis doit attendre qu'il se soit bien déplacé
		case 4:
			Debug.print(myAgent.getLocalName() + " laisse " + ((AID)finalBlockerInfo[0]).getLocalName(), debugFlag);
			// Si l'on était déjà bloqué auparavant
			if (!blockers.isEmpty()) {
				myValue = (float) finalBlockerInfo[3];
				finalBlockerInfo = blockers.get(blockers.size() - 1);
				blockers.clear();
				//blockerAgentInfo = finalBlockerInfo = null;
				path.clear();
				path.add((String)finalBlockerInfo[1]);
				state = 0;
			}

			// Si on n'a pas encore laissé la place
			if (((mas.abstractAgent)this.myAgent).getCurrentPosition().equals(this.myPosition)) {
				flag = this.moveBlock.tryToMove();
				Debug.print(myAgent.getLocalName() + " essaie de bouger", debugFlag);
				if (flag == 1) {
				}
				// Nombre d'essaie limite atteint
				if (flag == -1) {
					boucler();
				}
			}
			// Si l'on doit attendre que l'autre se déplace
			else {
				flag = moveBlock.waitOtherToMove();
				// La voie est libre
				if (flag == 1) {
					this.path.clear();
					finish();
				}
				// Nombre d'essaie limite atteint
				else if (flag == -1) {
					casesToAvoid.add(path.get(0));
					path.clear();
					finish();
				}
			}
			break;

		// L'agent attend que l'autre lui laisse la place
		case 5:
			Debug.print(myAgent.getLocalName() + " passe devant " + ((AID)finalBlockerInfo[0]).getLocalName(), debugFlag);
			flag = this.moveBlock.waitToMove();
			// Débloqué
			if (flag == 1) {
				this.path.remove(0);
				// Si c'est notre premier blocage
				if (blockers.isEmpty())
					finish();
				// Si l'on bloquait déjà quelqu'un
				else {
					blockerAgentInfo = finalBlockerInfo = blockers.get(blockers.size()-1);
					blockers.clear();
					state = 4;
				}
			}
			// Nombre d'essaie limite atteint
			else if (flag == -1) {
				this.casesToAvoid.add(this.path.get(0));
				this.path.clear();
				finish();
			}
			break;

		// En cas d'égalité, procédure de choix aléatoire
		case 6:
			flag = moveBlock.whoWin();
			switch (flag) {
			// Gagné
			case 1:
				Debug.print(myAgent.getLocalName() + " VS " + ((AID)finalBlockerInfo[0]).getLocalName(), debugFlag);
				this.state = 5;
				break;
			// Egalité
			case 0:
				Debug.print(myAgent.getLocalName() + " VS " + ((AID)finalBlockerInfo[0]).getLocalName(), debugFlag);
				this.moveBlock.shiFuMi();
				break;
			// Perdu
			case -1:
				Debug.print(myAgent.getLocalName() + " VS " + ((AID)finalBlockerInfo[0]).getLocalName(), debugFlag);
				this.state = 4;
				break;
			// Pas de message reçu
			case -2:
				break;
			// Nombre d'essaie limite atteinte
			case -3:
				this.casesToAvoid.add(this.path.get(0));
				this.path.clear();
				finish();
				break;
			}
			break;

		// Cas ou l'on ne bloque pas l'agent qui nous bloque
		// On se rabat sur l'un des agents que l'on bloque
		case 7:
			Object[] max = this.othersAgentInfo.get(0);

			// Choix de l'agent à qui répondre
			for (Object[] other : this.othersAgentInfo) {
				if ((float) max[3] < (float) other[3])
					max = other;
			}

			this.finalBlockerInfo = max;
			Debug.print(myAgent.getLocalName() + " se rabat sur " + ((AID)finalBlockerInfo[0]).getLocalName(), debugFlag);
			this.moveBlock.setOther((AID)this.finalBlockerInfo[0], (List<String>)this.finalBlockerInfo[2]);

			// Si un agent nous a répondu après coup, on négocie avec
			this.blockerAgentInfo = this.blocageProcedure.Confirm(true);
			if (this.blockerAgentInfo != null) {
				Debug.print(myAgent.getLocalName() + " --------------------------- Timon ---------------------------", debugFlag);
				this.finalBlockerInfo = this.blockerAgentInfo;
				this.moveBlock.setOther((AID)this.finalBlockerInfo[0], (List<String>)this.finalBlockerInfo[2]);
			}
			// Sinon on voit si l'on doit en laisser passer un autre
			else {
				// On le laisse passer seulement s'il à la priorité
				if ((float) finalBlockerInfo[3] >= myValue) {
					Debug.print(myAgent.getLocalName() + " evite cycle avec " + ((AID)finalBlockerInfo[0]).getLocalName(), debugFlag);
					this.blockerAgentInfo = this.finalBlockerInfo;
					this.blocageProcedure.AnswerAfter(this.finalBlockerInfo);
					state = 4;
					break;
				}
			}

			// Cet agent à la priorité
			if ((float) this.finalBlockerInfo[3] < this.myValue)
				this.state = 5;
				// Cet agent doit céder le passage
			else if ((float) this.finalBlockerInfo[3] > this.myValue)
				this.state = 4;
				// Egalité, choisir aléatoirement
			else
				this.state = 6;

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
			return 0;
		}
		else if (diff > 0f) {
			return 1;
		}
		return -1;
	}

	private void negociation() {
		this.finalBlockerInfo = this.blockerAgentInfo;
		this.moveBlock.setOther((AID)this.finalBlockerInfo[0], (List<String>)this.finalBlockerInfo[2]);

		int flag = hasPriorite();

		// attend que l'autre agent lui confirme d'avoir bougé
		if (flag == 1)
			this.state = 5;

			// laisser la place libre à l'autre agent et lui envoyer un message quand c'est fait, ou s'il est bloqué
		else if (flag == -1)
			this.state = 4;

			// Choisir aléatoirement
		else {
			this.moveBlock.shiFuMi();
			this.state = 6;
		}
	}

	private float valuation() {
		String job = ((AgentExplorateur) this.myAgent).getJob();

		float result;

		int way = this.path.size();

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

	private void boucler() {
		this.blockers.add(finalBlockerInfo);
		List<Couple<String, List<Attribute>>> lobs = ((mas.abstractAgent)myAgent).observe();

		boolean flag = false;
		boolean flag2 = false;

		// On prend comme valeur de priorité, la plus grande valeur des agents avec lesquels nous sommes
		// entré en conflit
		Object[] max = blockers.get(0);
		for (Object[] blocker : blockers) {
			if ((float) blocker[3] > (float)max[3] && !((AID)max[0]).equals((AID)finalBlockerInfo[0]))
				max = blocker;
		}

		this.myValue = (float) max[3];

		// Prendre un chemin où l'on pense qu'il n'y a pas d'agent bloquant
		for (Couple<String, List<Attribute>> couple: lobs) {
			flag2 = false;
			for (Object[] blocker : blockers) {
				if (((String) blocker[1]).equals(couple.getLeft())) {
					flag2 = true;
					break;
				}
			}
			if (flag2)
				continue;

			for (Object[] blocker : othersAgentInfo) {
				if (((String) blocker[1]).equals(couple.getLeft())) {
					flag2 = true;
					break;
				}
			}

			if (!flag2) {
				this.path.clear();
				this.path.add(couple.getLeft());
				flag = true;
				break;
			}
		}

		if (!flag) {
			Object[] min = blockers.get(0);
			for (Object[] blocker : blockers) {
				if (((float) blocker[3]) < (float)min[3]) {
					min = blocker;
				}
			}

			for (Object[] blocker : othersAgentInfo) {
				if (((float) blocker[3]) < (float)min[3]) {
					min = blocker;
				}
			}
			this.path.clear();
			this.path.add((String)min[1]);
			moveBlock.setPath(path);
			Debug.print(myAgent.getLocalName() + " set Path", debugFlag);
		}

		blockerAgentInfo = null;
		finalBlockerInfo = null;

		Debug.print(myAgent.getLocalName() + " boucle", debugFlag);

		state = 0;

	}

	private boolean clearBlockerMessages() {
		// Description du message à lire
		MessageTemplate msgTemplate = MessageTemplate.MatchProtocol("BlocageProtocol");

		// Récupération du message
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
		lastProtocol.setPath(this.path);
		// Ajout des cases à éviter
		for (String caseToAvoid : this.casesToAvoid) {
			if (lastProtocol.getCasesToAvoid() != null && !lastProtocol.getCasesToAvoid().isEmpty() && !lastProtocol.getCasesToAvoid().contains(caseToAvoid))
				lastProtocol.getCasesToAvoid().add(caseToAvoid);
		}
		((AgentExplorateur)this.myAgent).setProtocol(lastProtocol);
		this.finished = true;
	}

	@Override
	public boolean done() {
		return this.finished;
	}

}
