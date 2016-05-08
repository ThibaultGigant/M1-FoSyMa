package src.mas.protocols;

import src.mas.behaviours.move.ObservationBehaviour;
import src.mas.behaviours.move.RandomWalkBehaviour;
import src.mas.behaviours.move.MoveBehaviour;
import src.mas.strategies.RandomStrategy;

/**
 * Created by Tigig on 14/02/2016.
 */
public class RandomObserveProtocol extends AbstractProtocol {
    @Override
    public void addBehaviours(mas.abstractAgent myAgent) {

        RandomStrategy strategy = new RandomStrategy();
        strategy.setMyAgent(myAgent);
        MoveBehaviour moveBehaviour = new MoveBehaviour(myAgent, 500, strategy);

        behaviours.add(new ObservationBehaviour(myAgent, 900));
        behaviours.add(moveBehaviour);
        behaviours.forEach(myAgent::addBehaviour);
    }
}
