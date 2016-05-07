package src.mas.strategies;

import env.Attribute;
import env.Couple;
import mas.abstractAgent;
import org.graphstream.graph.Graph;
import org.w3c.dom.Attr;
import src.mas.agents.AgentExplorateur;
import src.mas.protocols.BlocageProtocol;
import src.mas.protocols.RandomObserveProtocol;
import src.mas.util.GraphTools;

import java.util.ArrayList;
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
    private String treasureToPurchase;

    private int countBlocage = 0;
    private int maxBlocage = 10;

    @Override
    public boolean moveTo(Graph knowledge) {
        String destination;

        // Si l'on n'a plus de place dans notre sac
        if (myAgent.getBackPackFreeSpace() == 0) {
            //System.out.println(this.myAgent.getLocalName() + " | Fin");
            ((AgentExplorateur) this.myAgent).setProtocol(new RandomObserveProtocol());
            return false;
        }

        // Récupération du chemin
        if (path.isEmpty()) {
            // Si l'on a atteint
            if (myAgent.getCurrentPosition().equals(treasureToPurchase)) {
                for (Attribute attr : ((List< Attribute>) knowledge.getNode(myAgent.getCurrentPosition()).getAttribute("contenu"))) {
                    if (attr.equals(Attribute.TREASURE) && (Integer) attr.getValue() > 0 ) {
                        System.out.println(attr.toString());
                        System.out.println("Pick");
                        myAgent.pick();
                    }

                }
            }
            //path = GraphTools.pathToTarget(myAgent.getCurrentPosition(), knowledge, "visited", casesToAvoid);
            path = GraphTools.bestPath(myAgent.getCurrentPosition(), knowledge, casesToAvoid, myAgent.getBackPackFreeSpace());
            // S'il y a un trésor au bout du chemin
            if (path.size() > 0 && ((List< Attribute>) knowledge.getNode(path.get(path.size() - 1)).getAttribute("contenu")).contains(Attribute.TREASURE))
                treasureToPurchase = path.get(path.size() - 1);
            else
                treasureToPurchase = "";
            casesToAvoid.clear();
        }

        // Si le chemin est vide, c'est qu'on a tout pris
        if (path.isEmpty()) {
        	//System.out.println(this.myAgent.getLocalName() + " | Fin");
            ((AgentExplorateur) this.myAgent).setProtocol(new RandomObserveProtocol());
            return false;
        }
        // Sinon on va au prochain point sur le chemin
        else {
        	destination = path.get(0);
        	if (this.myAgent.moveTo(destination)) {
        		countBlocage = 0;
        		path.remove(0);
        	}
        	else {
        		countBlocage++;
            }
        }
        
        if (countBlocage >= maxBlocage) {
        	// TODO
        	((AgentExplorateur) this.myAgent)
        					.setProtocol(new BlocageProtocol(this.myAgent, this.path, ( (AgentExplorateur) this.myAgent).getProtocol()));
            /*
        	List<Couple<String,List<Attribute>>> lobs=(this.myAgent).observe();//myPosition
            //Random move from the current position
            Random r= new Random();
            int moveId=r.nextInt(lobs.size());

            //The move action (if any) should be the last action of your behaviour
            (this.myAgent).moveTo(lobs.get(moveId).getLeft());
            */
        }
        
        return true;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<String> getCasesToAvoid() {
        return casesToAvoid;
    }

    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
