package mas.protocols;

import mas.behaviours.move.ObservationBehaviour;
import mas.behaviours.move.RandomWalkBehaviour;
import mas.behaviours.move.MoveBehaviour;
import mas.strategies.RandomStrategy;

/**
 * Created by Tigig on 14/02/2016.
 */
public class RandomObserveProtocol extends AbstractProtocol {
    @Override
    public void addBehaviours(mas.abstractAgent myAgent) {

        RandomStrategy strategy = new RandomStrategy();
        strategy.setMyAgent(myAgent);
        MoveBehaviour moveBehaviour = new MoveBehaviour(myAgent, 600, strategy);

        behaviours.add(new ObservationBehaviour(myAgent, 600));
        behaviours.add(moveBehaviour);
        behaviours.forEach(myAgent::addBehaviour);
    }
}
