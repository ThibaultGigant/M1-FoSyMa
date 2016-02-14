package mas.protocols;

import jade.core.behaviours.Behaviour;
import mas.abstractAgent;
import mas.behaviours.ObservationBehaviour;
import mas.behaviours.RandomWalkBehaviour;

import java.util.ArrayList;

/**
 * Created by Tigig on 14/02/2016.
 */
public abstract class AbstractProtocol implements IProtocol{
    /**
     * Liste des behaviours associés au protocole
     */
    ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();

    @Override
    public abstract void addBehaviours(mas.abstractAgent myAgent);

    @Override
    public void removeBehaviours(abstractAgent myAgent) {
        behaviours.forEach(myAgent::removeBehaviour);
    }
}
