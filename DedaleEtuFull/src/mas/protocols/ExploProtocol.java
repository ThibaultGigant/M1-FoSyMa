package mas.protocols;

import mas.agents.ExploAgent;
import mas.behaviours.ObserveBehaviour;
import mas.behaviours.WalkThroughTheAir;
import mas.strategies.IStrategy;
import mas.strategies.NewHorizon;

/**
 * Created by Tigig on 09/02/2016.
 */
public class ExploProtocol implements IProtocol{

    @Override
    public void addBehaviours(mas.abstractAgent myAgent) {
        myAgent.addBehaviour(new ObserveBehaviour(myAgent, 400));
        IStrategy strategy = new NewHorizon();
        strategy.setMyAgent(myAgent);
        myAgent.addBehaviour(new WalkThroughTheAir(myAgent, 1000, strategy));
    }
}
