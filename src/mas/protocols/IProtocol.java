package mas.protocols;


import java.util.List;

public interface IProtocol {
    void addBehaviours(mas.abstractAgent myAgent);
    void removeBehaviours(mas.abstractAgent myAgent);
    void setPath(List<String> path);
    List<String> getCasesToAvoid();
}
