package mas.strategies;

import env.Attribute;
import env.Couple;
import mas.abstractAgent;
import mas.agents.AgentExplorateur;
import mas.protocols.RandomObserveProtocol;
import mas.util.GraphTools;
import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Stratégie de déplacement correspondant à l'exploration : on va au plus proche non-visité
 * Created by Tigig on 13/02/2016.
 */
public class ExploreStrategy implements IStrategy {
    /**
     * Agent sur lequel s'applique la stratégie
     */
    mas.abstractAgent myAgent;

    /**
     * Chemin vers le point non visité le plus proche
     */
    List<String> path = new ArrayList<String>();
    
    /**
     * Nombre de blocage consécutif
     */
    private int countBlocage = 0;
    
    /**
     * Nombre de blocage consécutif maximal avant de se mettre en mode
     * blocage
     */
    private int maxBlocage = 52;

    @Override
    public boolean moveTo(Graph knowledge) {
        String destination;

        // Récupération du chemin
        if (path.isEmpty()) {
            path = GraphTools.pathToTarget(myAgent.getCurrentPosition(), knowledge, "visited");
        }

        // Si le chemin est vide, c'est qu'on a tout visité
        if (path.isEmpty()) {
            ((AgentExplorateur) this.myAgent).setProtocol(new RandomObserveProtocol());
            return false;
        }
        // Sinon on va au prochain point sur le chemin
        else {
        	destination = path.get(0);
        	if (this.myAgent.moveTo(destination)) {
        		countBlocage = 0;
        		destination = path.remove(0);
        	}
        	else {
        		countBlocage++;
            }
        }
        
        if (countBlocage >= maxBlocage) {
        	// TODO
        	
        }
        
        return true;
    }


    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
