package src.mas.behaviours.communication.blocker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import mas.abstractAgent;
import src.mas.agents.AgentExplorateur;

/**
 * Created by Fayçal on 06/05/2016.
 */
public class BlocageProcedure {

    private abstractAgent myAgent;
    private List<String> path;
    private List<Object[]> othersAgentInfo = new ArrayList<Object[]>();
    private int counter = 0;
    private int maxTry = 10;

    public BlocageProcedure(abstractAgent myAgent, List<String> path, List<Object[]> othersAgentInfo) {
        this.myAgent = myAgent;
        this.path = path;
        this.othersAgentInfo = othersAgentInfo;
    }

    public void Ask() {
        ACLMessage msg=new ACLMessage(ACLMessage.INFORM_IF);
        msg.setProtocol("BlocageProtocol");
        msg.setSender(this.myAgent.getAID());

        if (this.myAgent.getCurrentPosition() != "") {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd  = new ServiceDescription();
            sd.setType( "explorer" ); // TODO to change ?
            dfd.addServices(sd);

            DFAgentDescription[] result = {};
            try {
                result = DFService.search(this.myAgent, dfd);
            } catch (FIPAException e1) { e1.printStackTrace(); }


            for (DFAgentDescription fd : result)
                if (!this.myAgent.getAID().equals(fd.getName()))
                    msg.addReceiver(fd.getName());


            this.setContent(msg);

            this.myAgent.sendMessage(msg);
        }
    }

    /**
     * Utilisé dans la fonction Ask
     * • Remplie le contenu du message à envoyer
     * @param msg	: Message à remplir
     */
    public void setContent(ACLMessage msg) {
        Object[] data = { this.myAgent.getAID() , this.myAgent.getCurrentPosition(), this.path, valuation() };
        try {
            msg.setContentObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Possibilité de bloquer plusieurs autres agents
     * Nombre d'itération limitée
     */
    public boolean Answer() {
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
                    String senderDestination = ((List<String>) data[2]).get(0);

                    // L'agent vérifie que c'est bien lui qui bloque
                    if (!senderDestination.equals(this.myAgent.getCurrentPosition()) || agentAlreadyCheck(data)) {
                        return (this.counter < this.maxTry);
                    }

                    // Stock les données concernant l'autre agent qui est bloqué cet agent
                    this.othersAgentInfo.add(data);

                    // Création du message de confirmation
                    ACLMessage accuseReception = new ACLMessage(ACLMessage.CONFIRM);
                    accuseReception.setProtocol("BlocageProtocol");
                    accuseReception.addReceiver(msg.getSender());
                    accuseReception.setSender(this.myAgent.getAID());
                    //System.out.println("<----Message received from "+msg.getSender().getLocalName());

                    // Création du contenu du message
                    Object[] dataContent = { this.myAgent.getAID() , this.myAgent.getCurrentPosition(), this.path, valuation() };
                    accuseReception.setContentObject(dataContent);

                    // Envoie du message
                    this.myAgent.sendMessage(accuseReception);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                //System.out.println("Message reception failed");
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
    public boolean agentAlreadyCheck(Object[] data) {
        AID newAID = (AID) data[0];

        for (Object[] agentInfo : this.othersAgentInfo) {
            if (newAID.equals((AID) agentInfo[0]))
                return true;
        }

        return false;
    }

    public Object[] Confirm() {
        return Confirm(false);
    }

    public Object[] Confirm(boolean flag) {
        String after = "";
        if (flag)
            after = " | After";

        // Description du message à lire
        final MessageTemplate msgTemplate = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
                MessageTemplate.MatchProtocol("BlocageProtocol" + after));

        // Récupération du message à lire
        final ACLMessage msg = this.myAgent.receive(msgTemplate);

        if (msg != null) {
            Object[] data;
            //System.out.println("<----Message received from "+msg.getSender().getLocalName() + after);
            try {
                // Récupération des données du message
                data = (Object[]) msg.getContentObject();
                if (data != null) {
                    AID senderIAD = (AID) data[0];
                    String senderPosition = (String) data[1];
                    String senderDestination = ((List<String>) data[2]).get(0);

                    // TODO tester sans cette clause
                    // L'agent vérifie que c'est bien lui qui bloque
                    if (!senderDestination.equals(this.myAgent.getCurrentPosition())) {
                        return null;
                    }

                    // Confirmation reçu
                    // Stock les données concernant l'autre agent qui bloque cet agent
                    return data;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                //System.out.println("Message reception failed");
            }
        }
        // Confirmation non reçu
        return null;
    }

    public void AnswerAfter(Object[] other) {
        // Création du message de confirmation
        ACLMessage accuseReception = new ACLMessage(ACLMessage.CONFIRM);
        accuseReception.setProtocol("BlocageProtocol | After");
        accuseReception.addReceiver((AID) other[0]);
        accuseReception.setSender(this.myAgent.getAID());

        // Création du contenu du message
        Object[] dataContent = { this.myAgent.getAID() , ((mas.abstractAgent)this.myAgent).getCurrentPosition(), this.path, valuation() };
        try {
            accuseReception.setContentObject(dataContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((abstractAgent) this.myAgent).sendMessage(accuseReception);
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
                int capacity = this.myAgent.getBackPackFreeSpace();
                result *= capacity;
                break;

            case "explorer":
            default:
                puce = -1;
                break;
        }

        //System.out.println(this.myAgent.getLocalName() + "|  Valeur : " + puce * result);

        return puce*result;
    }
}
