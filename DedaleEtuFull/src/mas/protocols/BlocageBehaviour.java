package mas.protocols;

import mas.abstractAgent;

/**
 * Created by Tigig on 09/03/2016.
 */
public class BlocageBehaviour extends AbstractProtocol {
    /**
     * Dernier protocole suivi par l'agent, afin qu'il puisse le retrouver après résolution du blocage
     */
    IProtocol lastProtocol;

    /**
     * Constructor
     * @param lastProtocol
     */
    public BlocageBehaviour(IProtocol lastProtocol) {
        this.lastProtocol = lastProtocol;
    }

    @Override
    public void addBehaviours(abstractAgent myAgent) {
        //behaviours.add(new AskBlockerBehaviour(lastProtocol));

        behaviours.forEach(myAgent::addBehaviour);
    }
}
