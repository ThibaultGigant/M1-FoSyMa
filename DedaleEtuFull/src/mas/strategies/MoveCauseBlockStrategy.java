package mas.strategies;

import env.Attribute;
import env.Couple;
import mas.abstractAgent;
import mas.agents.AgentExplorateur;
import mas.protocols.BlocageProtocol;
import org.graphstream.graph.Graph;

import java.util.List;

/**
 * Created by Tigig on 09/03/2016.
 */
public class MoveCauseBlockStrategy implements IStrategy {
    abstractAgent myAgent;
    List<String> otherPath;
    private int count = 0;

    public MoveCauseBlockStrategy(List<String> otherPath) {
        this.otherPath = otherPath;
    }

    @Override
    public boolean moveTo(Graph knowledge) {
        //TODO
        boolean flag = false;
        for (Couple<String, List<Attribute>> couple: this.myAgent.observe()) {
            String noeud = couple.getLeft();
            if (((BlocageProtocol) ((AgentExplorateur) this.myAgent).getProtocol()).getPath().get(0).equals(noeud))
                continue;
            if (otherPath.get(1).equals(noeud))
                continue;
            if (myAgent.moveTo(noeud)) {
                flag = true;
                break;
            }
        }

        if (flag || (!flag && myAgent.moveTo(otherPath.get(1))) ) {
            // TODO sendMessage();
        }
        else
            this.count += 1;

        return false;
    }

    @Override
    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
