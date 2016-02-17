package mas.protocols;

import mas.behaviours.move.ObservationBehaviour;
import mas.behaviours.move.WalkThroughTheAir;
import mas.strategies.IStrategy;
import mas.strategies.NewHorizon;

/**
 * Created by Tigig on 09/02/2016.
 */
public class ExploProtocol extends AbstractProtocol {
    @Override
    public void addBehaviours(mas.abstractAgent myAgent) {
        behaviours.add(new ObservationBehaviour(myAgent, 100));
        IStrategy strategy = new NewHorizon();
        strategy.setMyAgent(myAgent);
        behaviours.add(new WalkThroughTheAir(myAgent, 200, strategy));
        behaviours.forEach(myAgent::addBehaviour);
    }
}
