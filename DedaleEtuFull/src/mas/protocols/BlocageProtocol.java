package mas.protocols;

import java.util.List;

import mas.abstractAgent;
import mas.behaviours.communication.blocker.AnswerBlockerBehaviour;
import mas.behaviours.communication.blocker.AskBlockerBehaviour;
import mas.behaviours.move.MoveBehaviour;
import mas.strategies.StopStrategy;

/**
 * Created by Tigig on 09/03/2016.
 */
public class BlocageProtocol extends AbstractProtocol {
    /**
     * Dernier protocole suivi par l'agent, afin qu'il puisse le retrouver après résolution du blocage
     */
    IProtocol lastProtocol;
    
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
        behaviours.add(new AskBlockerBehaviour(myAgent, path));
        behaviours.add(new AnswerBlockerBehaviour(myAgent, path));
        //behaviours.add(new MoveBehaviour(this.myAgent, 1000	, new StopStrategy()));
        
        behaviours.forEach(myAgent::addBehaviour);
    }
}
