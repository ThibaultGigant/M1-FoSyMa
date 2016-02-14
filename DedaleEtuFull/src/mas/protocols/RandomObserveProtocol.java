package mas.protocols;

import mas.behaviours.ObservationBehaviour;
import mas.behaviours.RandomWalkBehaviour;

/**
 * Created by Tigig on 14/02/2016.
 */
public class RandomObserveProtocol extends AbstractProtocol {
    @Override
    public void addBehaviours(mas.abstractAgent myAgent) {
        behaviours.add(new ObservationBehaviour(myAgent, 900));
        behaviours.add(new RandomWalkBehaviour(myAgent));
        behaviours.forEach(myAgent::addBehaviour);
    }
}
