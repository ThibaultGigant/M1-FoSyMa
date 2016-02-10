package mas.strategies;

import env.Attribute;
import mas.util.CustomCouple;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface IStrategy {
    String moveTo(HashMap<String, CustomCouple<Date, List<Attribute>>> knowledge);
    void setMyAgent(mas.abstractAgent myAgent);
}
