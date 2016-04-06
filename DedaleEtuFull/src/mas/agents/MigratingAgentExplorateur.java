package mas.agents;

import env.Attribute;
import env.Couple;
import env.Environment;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import mas.abstractAgent;
import mas.agents.interactions.protocols.deployMe.R1_deployMe;
import mas.agents.interactions.protocols.deployMe.R1_managerAnswer;
import mas.behaviours.migration.MigrationBehaviour;
import mas.protocols.ExplorationProtocol;
import mas.protocols.IProtocol;
import mas.util.Knowledge;

import org.graphstream.graph.implementations.SingleGraph;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Agent principal du projet
 * Created by Tigig on 13/02/2016.
 */
public class MigratingAgentExplorateur extends abstractAgent {
    /**
     * Connaissance de l'agent
     */
    Knowledge knowledge;
    
    private String job = "explorer";

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
    
    public String getJob() {
    	return this.job;
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
        
        // Setup arguments
        final Object[] args = getArguments();
        this.job = (String) args[1];
        protocol = new ExplorationProtocol();

        // Adding a knowledge
        //this.knowledge = new Knowledge(this, new SingleGraph("knowledge"));

        //Add the behaviours
        this.addBehaviour(new R1_deployMe((String)args[0], this));
        this.addBehaviour(new R1_managerAnswer((String)args[0], this));
        this.addBehaviour(new MigrationBehaviour(this));
        //this.protocol.addBehaviours(this);

        System.out.println("the agent "+this.getLocalName()+ " is started");
        //this.displayKnowledge();
    }

    /**
     * Récupère les arguments et initialise l'agent avec ces données
     */
    private void setupArguments() {
        //get the parameters given into the object[]. In the current case, the environment where the agent will evolve
        final Object[] args = getArguments();
        if(args[1]!=null){
            this.job = (String) args[1];
            protocol = new ExplorationProtocol();
            
            registerOnDF(this.job);
        }
        else{
            System.err.println("Malfunction during parameter's loading of agent"+ this.getClass().getName());
            System.exit(-1);
        }
    }

    public void registerOnDF(String role) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID()); /* getAID est l'AID de l'agent qui veut s'enregistrer*/
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(role);
        sd.setName(getLocalName() );
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd );
        }
        catch (FIPAException fe) { fe.printStackTrace(); }
    }

    /**
     * Mise à jour de la connaissance d'un agent depuis une observation
     * @param lobs liste d'observations
     */
    public void updateKnowledge(List<Couple<String, List<Attribute>>> lobs) {
        this.knowledge.updateKnowledge(lobs);
    }

    /**
     * Mise à jour de la connaissance d'un agent depuis la connaissance d'un autre agent
     * @param newKnowledge Serializable contenant toutes les données du graphe des connaissances de l'agent qui les partage
     */
    public void updateKnowledge(HashMap<String, HashMap<String, HashMap<String, Object>>> newKnowledge) {
        this.knowledge.updateKnowledge(newKnowledge);
    }

    /**
     * Provoque l'affichage du graphe des connaissances de l'agent
     */
    public void displayKnowledge() {
        // Création du style des noeuds
        String defaultNodeStyle = "node {fill-color: black; size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
        String nodeStyle_wumpus = "node.wumpus {fill-color: red;}";
        String nodeStyle_agent = "node.agent {fill-color: blue;}";
        String nodeStyle_treasure = "node.treasure {fill-color: yellow;}";
        String nodeStyle_EntryExit = "node.exit {fill-color: green;}";
        String nodeStyle_Well = "node.well {fill-color: cyan;}";
        String nodeStyle_Wind = "node.wind {fill-color: pink;}";
        String nodeStyle_Stench = "node.stench {fill-color: orange;}";
        String nodeStyle_visited= "node.visited {"+"fill-color: #347C2C;"+"}";
        String nodeStyle_unvisited= "node.unvisited {"+"fill-color: black;"+"}";

        String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_visited+nodeStyle_unvisited
                +nodeStyle_treasure+nodeStyle_wumpus+nodeStyle_EntryExit+nodeStyle_Stench+nodeStyle_Well+nodeStyle_Wind;
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        this.getKnowledge().getGraph().setAttribute("ui.stylesheet",nodeStyle);

        // Ouverture de la fenêtre
        this.getKnowledge().getGraph().display();
    }

    public void updateLastCommunication(String agentID, Date date) {
        this.getKnowledge().updateLastCommunication(agentID, date);
    }


    /**
     * This method is automatically called after doDelete()
     */
    protected void takeDown(){

    }
    
    protected void afterMove() {
    	this.addBehaviour(new R1_deployMe("GK", this));
    	
    	// Adding a knowledge
        this.knowledge = new Knowledge(this, new SingleGraph("knowledge"));
        
    	this.protocol.addBehaviours(this);
    }
}
