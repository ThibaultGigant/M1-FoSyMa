package mas.util;

import jade.core.AID;

import java.util.Date;

/**
 * Created by Fayçal on 08/05/2016.
 */
public class TreasureTargeted {
    public AID agent;
    public int value;
    public Date date;

    /**
     * Structure permettant de définir si un trésor est la cible d'un agent
     * @param agent     : Agent visant le trésor
     * @param value     : Valeur de priorité de l'agent sur le trésor
     * @param date      : Date à laquelle ces valeurs ont été mise à jour
     */
    public TreasureTargeted(AID agent, int value, Date date) {
        this.agent = agent;
        this.value = value;
        this.date  = date;
    }

    public TreasureTargeted copy() {
        return new TreasureTargeted(agent, value, date);
    }

    public void done() {
        this.value = -1;
        this.agent = null;
        this.date = new Date();
    }

    public String toString() {
        String name = "";
        String d = "";
        if (agent != null)
            name = agent.getLocalName();
        if (date != null)
            d = date.toString();
        return "(" + name + ", " + value + ", " + d + ")";
    }
}
