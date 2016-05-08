package mas.strategies;

import mas.abstractAgent;
import org.graphstream.graph.Graph;
import mas.agents.AgentExplorateur;
import mas.protocols.BlocageProtocol;

import java.util.ArrayList;
import java.util.List;

import env.Attribute;
import env.Couple;

import java.util.Random;

/**
 * Created by Fayçal on 07/05/2016.
 */
public class RandomStrategy implements IStrategy {
    /**
     * myAgent      : Agent sur lequel s'applique la stratégie
     * path         : Chemin vers le point non visité le plus proche
     * casesToAvoid : Liste des cases à éviter
     * countBlocage : Nombre de blocage consécutif
     * maxBlocage   : Nombre de blocage consécutif maximal avant de se mettre en mode
     *                  blocage
     */
    mas.abstractAgent myAgent;
    private int countBlocage = 0;
    private int maxBlocage = 10;

    @Override
    public boolean moveTo(Graph knowledge) {
        String destination;

        List<Couple<String,List<Attribute>>> lobs=(this.myAgent).observe();//myPosition
        //Random move from the current position
        Random r= new Random();
        int moveId=r.nextInt(lobs.size());

        destination = lobs.get(moveId).getLeft();

        if (this.myAgent.moveTo(destination)) {
            countBlocage = 0;
        }
        else {
            countBlocage++;
        }

        if (countBlocage >= maxBlocage) {
            List<String> path = (new ArrayList<String>());
            path.add(destination);
            ((AgentExplorateur) this.myAgent)
                    .setProtocol(new BlocageProtocol(this.myAgent, path , ( (AgentExplorateur) this.myAgent).getProtocol()));
        }

        return true;
    }

    public void setPath(List<String> path) {}

    public List<String> getCasesToAvoid() { return null;}

    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
