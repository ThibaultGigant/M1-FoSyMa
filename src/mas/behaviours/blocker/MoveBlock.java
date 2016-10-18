package mas.behaviours.blocker;


import java.io.IOException;
import java.util.List;
import java.util.Random;

import env.Attribute;
import env.Couple;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.lang.acl.UnreadableException;
import mas.abstractAgent;
import mas.agents.AgentExplorateur;
import mas.protocols.BlocageProtocol;

/**
 * Created by Tigig on 09/03/2016.
 */
public class MoveBlock {
    abstractAgent myAgent;
    List<String> path;
    List<String> otherPath;
    private List<Couple<String, List<Attribute>>> places;
    private AID otherAgent;

    private int tryToMoveCounter = 0;
    private int tryToMoveMaxTries = 30;
    private int waitToMoveCounter = 0;
    private int waitToMoveMaxTries = 30;
    private int waitOtherToMoveCounter = 0;
    private int waitOtherToMoveMaxTries = 30;
    private int whoWinCounter = 0;
    private int whoWinMaxTries = 30;

    private float float_ShiFuMi;

    public MoveBlock(abstractAgent myAgent, List<String> otherPath, AID otherAgent, List<String> path) {
    	this.myAgent = myAgent;
        this.path = path;
        this.otherPath = otherPath;
        this.otherAgent = otherAgent;
        this.places = this.myAgent.observe();
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public void setOther(AID otherAgent, List<String> otherPath) {
        this.otherAgent = otherAgent;
        this.otherPath = otherPath;
    }

    /**
     * Cette fonction essaie de faire bouger l'agent pour laisser le champs libre
     * à l'agent qu'il bloque
     * @return      : •  1 : Mouvement effectué
     *                •  0 : Mouvement non effectué
     *                • -1 : Nombre d'essaie limite atteinte
     */
    public int tryToMove() {

        boolean flag = false;
        for (Couple<String, List<Attribute>> couple: this.places) {
            String noeud = couple.getLeft();
            if (this.path.get(0).equals(noeud))
                continue;
            if (otherPath.size() >= 2 && otherPath.get(1).equals(noeud))
                continue;
            if (myAgent.moveTo(noeud)) {
                flag = true;
                break;
            }
        }

        if (flag || (!flag && otherPath.size() > 1 && myAgent.moveTo(otherPath.get(1))) ) {
        	sendMessage();
            this.tryToMoveCounter = 0;
        	return 1;
        }
        else
            this.tryToMoveCounter += 1;

        if (this.tryToMoveCounter >= this.tryToMoveMaxTries) {
            this.tryToMoveCounter = 0;
            return -1;
        }
        
        return 0;
    }
    
    private void sendMessage() {
		ACLMessage message = new ACLMessage(ACLMessage.CONFIRM);
        message.setProtocol("BlocageProtocol | TryToMove");
        message.addReceiver(otherAgent);
        message.setSender(this.myAgent.getAID());
		this.myAgent.sendMessage(message);
    }

    /**
     * Cette fonction essaie de faire bouger l'agent pour laisser le champs libre
     * à l'agent qu'il bloque
     * @return      : •  1 : Message reçu
     *                •  0 : Message non reçu
     *                • -1 : Nombre d'essaie limite atteinte
     */
    public int waitToMove() {
        this.waitToMoveCounter++;

        // Description du message à lire
        final MessageTemplate msgTemplate = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                MessageTemplate.MatchProtocol("BlocageProtocol | TryToMove"));

        // Récupération du message
        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            // Vérifier si c'est bien l'agent avec lequel on était en bloqué
            if (msg.getSender().equals(this.otherAgent)) {
                // Si l'agent réussi à avancer comme voulu
                if (this.myAgent.moveTo(this.path.get(0))) {
                    // Confirme à l'autre agent qu'il a bougé
                    ACLMessage message = new ACLMessage(ACLMessage.CONFIRM);
                    message.setProtocol("BlocageProtocol | waitToMove");
                    message.addReceiver(otherAgent);
                    message.setSender(this.myAgent.getAID());
                    this.myAgent.sendMessage(message);

                    this.waitToMoveCounter = 0;
                    return 1;
                }
            }
        }

        // Nombre d'essaie limite atteinte
        if (this.waitToMoveCounter >= this.waitToMoveMaxTries) {
            this.waitToMoveCounter = 0;
            return -1;
        }

        return 0;

    }

    public int waitOtherToMove() {
        this.waitOtherToMoveCounter++;

        // Description du message à lire
        final MessageTemplate msgTemplate = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                MessageTemplate.MatchProtocol("BlocageProtocol | waitToMove"));

        // Récupération du message
        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            this.waitOtherToMoveCounter = 0;
            return 1;
        }

        // Nombre d'essaie limite atteinte
        if (this.waitOtherToMoveCounter >= this.waitOtherToMoveMaxTries) {
            this.waitOtherToMoveCounter = 0;
            return -1;
        }

        return 0;
    }

    public void shiFuMi() {
        ACLMessage message = new ACLMessage(ACLMessage.PROPOSE);
        message.setProtocol("BlocageProtocol");
        message.addReceiver(otherAgent);
        message.setSender(this.myAgent.getAID());

        //System.out.println(myAgent.getLocalName() + " joue vs " + otherAgent.getLocalName());

        this.float_ShiFuMi = (new Random()).nextFloat();

        try {
            message.setContentObject(float_ShiFuMi);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.myAgent.sendMessage(message);
    }

    /**
     *
     * @return      : •  1 : Cet agent à la priorité
     *                •  0 : Egalité, rejouer
     *                • -1 : Cet agent doit ceder le passage
     *                • -2 : pas de message reçu
     *                • -3 : Nombre d'essaie limite atteinte
     */
    public int whoWin() {
        whoWinCounter++;
        // Description du message à lire
        final MessageTemplate msgTemplate = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                MessageTemplate.MatchProtocol("BlocageProtocol"));

        // Récupération du message
        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            this.whoWinCounter = 0;
            float float_opponentShiFuMi = 0;
            try {
                float_opponentShiFuMi = (float) msg.getContentObject();

                // Gagné
                if (float_ShiFuMi > float_opponentShiFuMi)
                    return 1;
                // Perdu
                else if (float_ShiFuMi < float_opponentShiFuMi)
                    return -1;
                // Egalité
                return 0;
            }
            catch (UnreadableException e) {
                e.printStackTrace();
            }
        }

        if (whoWinCounter >= whoWinMaxTries)
            return -3;

        // Message non reçu
        return -2;
    }
}
