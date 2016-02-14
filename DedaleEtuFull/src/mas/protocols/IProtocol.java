package mas.protocols;


import mas.agents.ExploAgent;

public interface IProtocol {
    void addBehaviours(mas.abstractAgent myAgent);
    void removeBehaviours(mas.abstractAgent myAgent);
}
