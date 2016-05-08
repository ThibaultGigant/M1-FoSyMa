package mas.protocols;

import java.util.List;

import mas.abstractAgent;
//import mas.behaviours.blocker.AnswerBlockerBehaviour;
import mas.behaviours.blocker.BlockerBehaviour;
//import mas.behaviours.move.MoveBehaviour;
//import mas.strategies.StopStrategy;

/**
 * Created by Tigig on 09/03/2016.
 */
public class BlocageProtocol extends AbstractProtocol {
    /**
     * Dernier protocole suivi par l'agent, afin qu'il puisse le retrouver après résolution du blocage
     */
    private IProtocol lastProtocol;
    
    /**
     * Chemin que l'agent voudrait emprunter
     */
    private List<String> path;

    /**
     * Constructor
     * @param lastProtocol
     */
    public BlocageProtocol(mas.abstractAgent myAgent, List<String> path, IProtocol lastProtocol) {
        this.lastProtocol = lastProtocol;
        this.path = path;
    }

    public List<String> getPath() {
    	return this.path;
    }

    public IProtocol getLastProtocol() {
        return lastProtocol;
    }

    @Override
    public void addBehaviours(abstractAgent myAgent) {
        behaviours.add(new BlockerBehaviour(myAgent, path));
        
        behaviours.forEach(myAgent::addBehaviour);
    }

    @Override
    public void setPath(List<String> path) {}
}
