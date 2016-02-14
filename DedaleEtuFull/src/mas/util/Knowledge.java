package mas.util;

import env.Attribute;
import env.Environment;
import jade.util.leap.Serializable;
import mas.abstractAgent;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Classe qui correspond à la connaissance d'un Agent.
 * Tous les outils liés à ces connaissances seront dans cette classe.
 * Created by Tigig on 10/02/2016.
 */
public class Knowledge implements Serializable {

    /**
     * Agent à qui appartient ces connaissances
     */
    private abstractAgent myAgent;

    /**
     * Graphe qui représente l'espace dont l'agent a connaissance
     * Chaque noeud du graphe comporte comme information (en plus des informations basiques) :
     * - La date la plus à jour de quand ce noeud a été visité ou observé par un agent
     * - le statut de ce noeud : visité ou non (boolean)
     * - le contenu (trésor, etc...) : "List&lt;Attribute&gt;"
     */
    private Graph graph;

    /**
     * HashMap représentant la dernière communication avec l'agent dont l'ID est la clé de la HashMap
     */
    HashMap<String, Date> lastCommunication = new HashMap<String, Date>();

    /**
     * Dernière position connue de l'agent
     */
    Node currentPosition;

    /**
     * Constructeur, à initialiser avec un graphe déjà instancié.
     * Celui-ci devrait être constitué uniquement du noeud où est placé l'agent
     * @param myAgent agent dont cette classe représente les connaissances
     * @param graph graphe des connaissances initiales de l'agent
     */
    public Knowledge(abstractAgent myAgent, Graph graph) {
        this.myAgent = myAgent;
        this.graph = graph;
        this.currentPosition = this.graph.addNode(this.myAgent.getCurrentPosition());
        this.currentPosition.setAttribute("visited", true);
        this.currentPosition.setAttribute("ui.class", "agent");
    }

    /**
     * Getters et Setters
     */
    public abstractAgent getMyAgent() {
        return myAgent;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Permet la mise à jour de la connaissance d'un agent avec celle d'un autre
     * @param agent Agent qui fournit le graphe. Utile pour mettre à jour la hashmap des communications
     * @param newGraph graphe
     */
    public void updateKnowledge(abstractAgent agent, Graph newGraph) {
        /*
        Mise à jour de la date de dernière communication entre les agents
         */
        Date date = new Date();
        this.lastCommunication.put(agent.getLocalName(), date);

        /*
         * Pour chaque noeud du nouveau graphe, s'il n'est pas dans les connaissances, l'ajouter
         * Sinon regarder si la date dans le nouveau graphe est plus récente, si c'est le cas, mettre à jour
         */
        for (Node node: newGraph.getNodeSet()) {
            Node n = this.getGraph().getNode(node.getId());

            if (n != null) {
                if (((Date) n.getAttribute("date")).compareTo(node.getAttribute("date")) < 0) {
                    for (String key: node.getAttributeKeySet()) {
                        n.addAttribute(key, n.getAttribute(key));
                    }
                }
            } else {
                this.getGraph().addNode(node.getId());
            }
            // Ajout des arêtes liées à ce noeud
            for (Edge edge: node.getEdgeSet()) {
                try {
                    n.getEdgeSet().add(edge);
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    /**
     * Permet la mise à jour de la connaissance d'un agent depuis une observation
     * @param lobs liste d'observations
     */
    public void updateKnowledge(List<Environment.Couple<String, List<Attribute>>> lobs) {
        Date date = new Date();
        String currentNode = this.myAgent.getCurrentPosition();
        Node n;

        /*
         * Pour chaque noeud de la liste donée, s'il n'est pas dans les connaissances, l'ajouter
         * Sinon mettre à jour ses données avec ce qu'on a observé
         */
        for (Environment.Couple<String, List<Attribute>> couple: lobs) {
            n = this.getGraph().getNode(couple.getLeft());

            if (n != null) {
                n.addAttribute("contenu", couple.getRight());
                // Ajout de l'arête si besoin
                if (!n.getId().equals(currentNode)) {
                    try {
                        this.getGraph().addEdge(currentNode + n.getId(), currentNode, n.getId());
                    } catch (Exception e) {
                        continue;
                    }
                }
            } else {
                n = this.getGraph().addNode(couple.getLeft());
                n.addAttribute("date", date);
                n.addAttribute("contenu", couple.getRight());
                // Ajout des arêtes et changement des statuts
                if (!n.getId().equals(currentNode)) {
                    n.addAttribute("visited", false);
                    n.setAttribute("ui.class", "unvisited");
                    try {
                        this.getGraph().addEdge(currentNode + n.getId(), currentNode, n.getId());
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }

        this.updateCurrentPosition();
    }

    /**
     * Change les données sur la position actuelle de l'agent, principalement pour l'affichage du graphe
     */
    public void updateCurrentPosition() {
        this.currentPosition.setAttribute("ui.class", "visited");
        this.currentPosition = this.graph.getNode(this.myAgent.getCurrentPosition());
        this.currentPosition.setAttribute("visited", true);
        this.currentPosition.setAttribute("ui.class", "agent");
    }
}
