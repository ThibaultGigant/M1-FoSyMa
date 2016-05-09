package mas.strategies;

import env.Attribute;
import env.Couple;
import jade.core.AID;
import mas.abstractAgent;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.w3c.dom.Attr;
import mas.agents.AgentExplorateur;
import mas.protocols.BlocageProtocol;
import mas.protocols.RandomObserveProtocol;
import mas.util.GraphTools;
import mas.util.noWumpus;
import mas.util.TreasureTargeted;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Stratégie de déplacement correspondant à l'exploration : on va au plus proche non-visité
 * Created by Tigig on 13/02/2016.
 */
public class HunterStrategy implements IStrategy {
    /**
     * myAgent              : Agent sur lequel s'applique la stratégie
     * path                 : Chemin vers le point non visité le plus proche
     * casesToAvoid         : Liste des cases à éviter
     * treasureToPurchase   : Trésor en ligne de mire
     *
     * countBlocage         : Nombre de blocage consécutif
     * maxBlocage           : Nombre de blocage consécutif maximal avant de se mettre en mode
     *                          blocage
     */
    abstractAgent myAgent;
    List<String> path = new ArrayList<String>();
    private List<String> casesToAvoid = new ArrayList<String>();
    private String treasureToPurchase = "";

    private int countBlocage = 0;
    private int maxBlocage = 10;

    private String lastPlace = "";

    private int countAvoid = 0;
    private int maxAvoid = 20;

    @Override
    public boolean moveTo(Graph knowledge) {
        String destination;

        System.out.println(myAgent.getLocalName() + " " + treasureToPurchase + " " + path.toString());

        // Si l'on n'a plus de place dans notre sac
        if (myAgent.getBackPackFreeSpace() == 0) {
            System.out.println(this.myAgent.getLocalName() + " | Fin Hunter sac");
            ((AgentExplorateur) this.myAgent).setProtocol(new RandomObserveProtocol());
            return false;
        }

        // Si on ciblait un trésor mais qu'on apprend qu'un autre à la priorité
        if (treasureToPurchase != "" && !((TreasureTargeted)knowledge.getNode(treasureToPurchase).getAttribute("ciblé")).agent.equals(myAgent.getAID())) {
            path.clear();
            treasureToPurchase = "";
        }


        // Si on a atteind notre objectif
        if (path.isEmpty() && myAgent.getCurrentPosition().equals(treasureToPurchase)) {
            System.out.println("objectif");
            for (Attribute attr : ((List< Attribute>) knowledge.getNode(myAgent.getCurrentPosition()).getAttribute("contenu"))) {
                if (attr.equals(Attribute.TREASURE) && (Integer) attr.getValue() > 0 ) {
                    int tic = myAgent.getBackPackFreeSpace();
                    myAgent.pick();
                    int tac = myAgent.getBackPackFreeSpace();
                    System.out.println(myAgent.getLocalName() + " Pick " + (tic - tac) );
                    // TODO attendre un peu avant de pick, pour voir s'il n'y a pas un agent plus apte à le prendre
                    TreasureTargeted target = ((TreasureTargeted) knowledge.getNode(treasureToPurchase).getAttribute("ciblé"));
                    target.done();
                    break;
                }

            }
        }

        // Si l'on est sur une case dangereuse
        if (lastPlace != "" && noWumpus.isWumpus(knowledge, myAgent.getCurrentPosition())) {
            String tmp = myAgent.getCurrentPosition();

            // Si l'on ciblait un trésor
            if (treasureToPurchase != "") {
                TreasureTargeted a = (TreasureTargeted) knowledge.getNode(treasureToPurchase).getAttribute("ciblé");
                if (((AID) a.agent) == myAgent.getAID())
                    a.done();
            }


            path.clear();
            treasureToPurchase = "";
            path.add(lastPlace);
            lastPlace = "";
            if (this.myAgent.moveTo(path.get(0))) {
                lastPlace = "";
                countBlocage = 0;
                path.clear();
                return true;
            }
            else {
                countBlocage++;
            }

            if (countBlocage >= maxBlocage) {
                ((AgentExplorateur) this.myAgent)
                        .setProtocol(new BlocageProtocol(this.myAgent, this.path, ( (AgentExplorateur) this.myAgent).getProtocol()));
            }

            return true;
        }

        // Récupération du chemin
        if (path.isEmpty()) {
            path = GraphTools.bestPath(myAgent.getAID(), myAgent.getCurrentPosition(), knowledge, casesToAvoid, myAgent.getBackPackFreeSpace());
            // S'il y a un trésor au bout du chemin
            if (path.size() > 0 && ((List< Attribute>) knowledge.getNode(path.get(path.size() - 1)).getAttribute("contenu")).contains(Attribute.TREASURE)) {
                if (knowledge.getNode(path.get(path.size() - 1)).hasAttribute("ciblé")) {
                    TreasureTargeted target = (TreasureTargeted) knowledge.getNode(path.get(path.size() - 1)).getAttribute("ciblé");
                    System.out.println(myAgent.getLocalName() + " " + target.toString());
                    int myValue = valuation(knowledge.getNode(path.get(path.size() - 1)));
                    if (target.value < 0 || target.value >= myValue) {
                        target.value = myValue;
                        target.agent = myAgent.getAID();
                        target.date = new Date();
                        knowledge.getNode(path.get(path.size() - 1)).setAttribute("ciblé", target);
                        treasureToPurchase = path.get(path.size() - 1);
                    }
                }
                else {
                    knowledge.getNode(path.get(path.size() - 1)).setAttribute("ciblé", new TreasureTargeted(myAgent.getAID(),  valuation(knowledge.getNode(path.get(path.size() - 1))), new Date()));
                    treasureToPurchase = path.get(path.size() - 1);
                }
            }
            else
                treasureToPurchase = "";

            if (path.isEmpty() && !casesToAvoid.isEmpty()) {
                casesToAvoid.clear();
                if (countAvoid <= maxAvoid) {
                    path = GraphTools.bestPath(myAgent.getAID(), myAgent.getCurrentPosition(), knowledge, casesToAvoid, myAgent.getBackPackFreeSpace());
                    countAvoid++;
                }
            }
        }

        // Si le chemin est vide, c'est qu'on a tout pris
        if (path.isEmpty()) {
        	System.out.println(this.myAgent.getLocalName() + " | Fin Hunter");
            ((AgentExplorateur) this.myAgent).setProtocol(new RandomObserveProtocol());
            return false;
        }
        // Sinon on va au prochain point sur le chemin
        else {
            casesToAvoid.clear();
        	destination = path.get(0);
            String tmp = myAgent.getCurrentPosition();
        	if (this.myAgent.moveTo(destination)) {
                lastPlace = tmp;
        		countBlocage = 0;
        		path.remove(0);
        	}
        	else {
        		countBlocage++;
            }
        }
        
        if (countBlocage >= maxBlocage) {
        	((AgentExplorateur) this.myAgent)
        					.setProtocol(new BlocageProtocol(this.myAgent, this.path, ( (AgentExplorateur) this.myAgent).getProtocol()));
        }
        
        return true;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<String> getCasesToAvoid() {
        return casesToAvoid;
    }

    private int valuation(Node node) {
        List<Attribute> attr = (List<Attribute>)node.getAttribute("contenu");
        int val = -1;
        for (Attribute a : attr) {
            if (a.equals(Attribute.TREASURE)) {
                val = (Integer)a.getValue();
                break;
            }
        }

        if (val != -1) {
            int diff;
            diff = myAgent.getBackPackFreeSpace() - val;

            // Pénalisation si le trésor est trop gros
            if (diff < 0)
                diff *= -2;

            return diff;
        }

        return -1;
    }

    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
