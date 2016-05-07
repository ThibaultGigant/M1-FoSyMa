package src.mas.protocols;

import jade.core.behaviours.Behaviour;
import mas.abstractAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tigig on 14/02/2016.
 */
public abstract class AbstractProtocol implements IProtocol, Serializable{
    /**
     * Liste des behaviours associés au protocole
     */
    ArrayList<Behaviour> behaviours = new ArrayList<Behaviour>();

    List<String> path;

    @Override
    public abstract void addBehaviours(mas.abstractAgent myAgent);

    @Override
    public void removeBehaviours(abstractAgent myAgent) {
        behaviours.forEach(myAgent::removeBehaviour);
        behaviours.clear();
    }

    @Override
    public void setPath(List<String> path) {}
    @Override
    public List<String> getCasesToAvoid() {return null;}
}
