package mas.strategies;

import env.Attribute;
import env.Couple;
import mas.abstractAgent;
import mas.agents.AgentExplorateur;
import mas.protocols.BlocageProtocol;
import mas.protocols.RandomObserveProtocol;
import mas.protocols.HunterProtocol;
import mas.util.GraphTools;
import mas.util.noWumpus;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Iterator;
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
    private String lastPlace = "";

    @Override
    public boolean moveTo(Graph knowledge) {
        String destination;

        // Si l'on est sur une case dangereuse
        if (lastPlace != "" && noWumpus.isWumpus(knowledge, myAgent.getCurrentPosition())) {
            String tmp = myAgent.getCurrentPosition();
            path.clear();
            path.add(lastPlace);
            System.out.println("la");
            lastPlace = "";
            if (this.myAgent.moveTo(lastPlace)) {
                lastPlace = "";
                countBlocage = 0;
                path.clear();
                return true;
            }
            else {
                countBlocage++;
            }

            if (countBlocage >= maxBlocage) {
                System.out.println("isWumpus");
                ((AgentExplorateur) this.myAgent)
                        .setProtocol(new BlocageProtocol(this.myAgent, this.path, ( (AgentExplorateur) this.myAgent).getProtocol()));
            }

            return true;
        }

        // Récupération du chemin
        if (path.isEmpty()) {
            path = GraphTools.pathToTarget(myAgent.getCurrentPosition(), knowledge, "visited", "false", casesToAvoid);
            if (!path.isEmpty())
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

    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
