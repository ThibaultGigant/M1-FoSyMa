package mas.agents;

import env.Attribute;
import env.Environment;
import jade.core.behaviours.Behaviour;
import mas.abstractAgent;
import mas.protocols.IProtocol;
import mas.util.Knowledge;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent principal du projet
 * Created by Tigig on 13/02/2016.
 */
public class AgentExplorateur extends abstractAgent {
    /**
     * Connaissance de l'agent
     */
    Knowledge knowledge;

    /**
     * Protocole que suivra l'agent
     */
    IProtocol protocol;

    /**
     * Getters et Setters
     */
    public Knowledge getKnowledge() {
        return knowledge;
    }

    public IProtocol getProtocol() {
        return protocol;
    }

    public void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
    }

    /**
     * En plus de changer le protocole dans les paramètres, il nettoie les behaviours de l'agent
     * et applique le nouveau protocole
     * @param protocol nouveau protocole à appliquer
     */
    public void setProtocol(IProtocol protocol) {
        // Clean behaviours
        this.protocol.removeBehaviours(this);
        // Change le protocole et l'applique
        this.protocol = protocol;
        this.protocol.addBehaviours(this);
    }

    /**
     * This method is automatically called when "agent".start() is executed.
     * Consider that Agent is launched for the first time.
     * 			1) set the agent attributes
     *	 		2) add the behaviours
     *
     */
    protected void setup(){

        super.setup();

        //get the parameters given into the object[]. In the current case, the environment where the agent will evolve
        final Object[] args = getArguments();
        if(args[0]!=null){
            deployAgent((Environment) args[0]);
            protocol = (IProtocol) args[1];
        }else{
            System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
            System.exit(-1);
        }

        // Adding a knowledge
        this.knowledge = new Knowledge(this, new SingleGraph("knowledge"));

        //Add the behaviours
        this.protocol.addBehaviours(this);

        System.out.println("the agent "+this.getLocalName()+ " is started");

        String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
        String nodeStyle_agent= "node.agent {"+"fill-color: blue;"+"}";
        String nodeStyle_visited= "node.visited {"+"fill-color: black;"+"}";
        String nodeStyle_unvisited= "node.unvisited {"+"fill-color: red;"+"}";

        String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_visited+nodeStyle_unvisited;
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        this.getKnowledge().getGraph().setAttribute("ui.stylesheet",nodeStyle);
        Viewer viewer = this.getKnowledge().getGraph().display();
    }

    /**
     * Mise à jour de la connaissance d'un agent depuis une observation
     * @param lobs liste d'observations
     */
    public void updateKnowledge(List<Environment.Couple<String, List<Attribute>>> lobs) {
        this.knowledge.updateKnowledge(lobs);
    }


    /**
     * This method is automatically called after doDelete()
     */
    protected void takeDown(){

    }
}
