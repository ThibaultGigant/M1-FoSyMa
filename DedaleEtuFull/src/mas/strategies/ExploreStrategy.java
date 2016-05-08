package src.mas.strategies;

import env.Attribute;
import env.Couple;
import mas.abstractAgent;
import src.mas.agents.AgentExplorateur;
import src.mas.protocols.BlocageProtocol;
import src.mas.protocols.RandomObserveProtocol;
import src.mas.protocols.HunterProtocol;
import src.mas.util.GraphTools;

import org.graphstream.graph.Graph;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Stratégie de déplacement correspondant à l'exploration : on va au plus proche non-visité
 * Created by Tigig on 13/02/2016.
 */
public class ExploreStrategy implements IStrategy {
    /**
     * myAgent      : Agent sur lequel s'applique la stratégie
     * path         : Chemin vers le point non visité le plus proche
     * casesToAvoid : Liste des cases à éviter
     * countBlocage : Nombre de blocage consécutif
     * maxBlocage   : Nombre de blocage consécutif maximal avant de se mettre en mode
     *                  blocage
     */
    mas.abstractAgent myAgent;
    List<String> path = new ArrayList<String>();
    private List<String> casesToAvoid = new ArrayList<String>();
    private int countBlocage = 0;
    private int maxBlocage = 10;

    @Override
    public boolean moveTo(Graph knowledge) {
        String destination;

        // Récupération du chemin
        if (path.isEmpty()) {
            path = GraphTools.pathToTarget(myAgent.getCurrentPosition(), knowledge, "visited", casesToAvoid);
            casesToAvoid.clear();
        }

        // Si le chemin est vide, c'est qu'on a tout visité
        if (path.isEmpty()) {
        	System.out.println(this.myAgent.getLocalName() + " | Fin");
            //((AgentExplorateur) this.myAgent).setProtocol(new RandomObserveProtocol());
            ((AgentExplorateur) this.myAgent).setProtocol(new HunterProtocol());
            return false;
        }
        // Sinon on va au prochain point sur le chemin
        else {
            // On y va seulement s'il est toujours considéré comme "visited" (updateKnowledge entre temps ?)
            if (!knowledge.getNode(path.get(path.size() - 1)).hasAttribute("visited")) {
                path.clear();
                return true;
            }

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
