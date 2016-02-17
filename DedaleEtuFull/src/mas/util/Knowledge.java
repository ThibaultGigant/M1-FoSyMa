package mas.util;

import env.Attribute;
import env.Couple;
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
     * @param myAgent agent dont cette classe représente les connaissances
     * @param graph graphe des connaissances initiales de l'agent
     */
    public Knowledge(abstractAgent myAgent, Graph graph) {
        this.myAgent = myAgent;
        this.graph = graph;
        this.currentPosition = this.graph.addNode(this.myAgent.getCurrentPosition());
        this.currentPosition.setAttribute("visited", true);
        this.currentPosition.setAttribute("ui.class", "agent");
        this.currentPosition.setAttribute("date", new Date());
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
     * @param newKnowledge Connaissances partagée par un autre agent
     */
    public void updateKnowledge(HashMap<String, HashMap<String, HashMap<String, Object>>> newKnowledge) {

        /*
         * Pour chaque noeud du nouveau graphe, s'il n'est pas dans les connaissances, l'ajouter
         * Sinon regarder si la date dans le nouveau graphe est plus récente, si c'est le cas, mettre à jour
         */
        for (String nodeID: newKnowledge.get("nodes").keySet()) {
            Node n = this.getGraph().getNode(nodeID);
            if (n != null) {
                Date oldDate = (Date) n.getAttribute("date");
                Date newDate = (Date) newKnowledge.get("nodes").get(nodeID).get("date");
                if (oldDate.compareTo(newDate) < 0 && !(!n.getAttribute("ui.class").equals("unvisited") && newKnowledge.get("nodes").get(nodeID).get("ui.class").equals("unvisited"))) {
                    for (String key: newKnowledge.get("nodes").get(nodeID).keySet()) {
                        n.addAttribute(key, newKnowledge.get("nodes").get(nodeID).get(key));
                    }
                    if (n.getAttribute("ui.class").equals("agent")) {
                        n.setAttribute("ui.class", "visited");
                    }
                }
            } else {
                n = this.getGraph().addNode(nodeID);
                for (String key: newKnowledge.get("nodes").get(nodeID).keySet()) {
                    n.addAttribute(key, newKnowledge.get("nodes").get(nodeID).get(key));
                }
                if (n.getAttribute("ui.class").equals("agent")) {
                    n.setAttribute("ui.class", "visited");
                }
            }
        }

        // Ajout des arêtes si elles n'y sont pas déjà
        for (String edgeID: newKnowledge.get("edges").keySet()) {
            try {
                this.getGraph().addEdge(edgeID, (String) newKnowledge.get("edges").get(edgeID).get("node0"), (String) newKnowledge.get("edges").get(edgeID).get("node1"));
            } catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * Permet la mise à jour de la connaissance d'un agent depuis une observation
     * @param lobs liste d'observations
     */
    public void updateKnowledge(List<Couple<String, List<Attribute>>> lobs) {
        Date date = new Date();
        String currentNode = this.myAgent.getCurrentPosition();
        Node n;
        List<Attribute> attr, attr2;

        /*
         * Pour chaque noeud de la liste donée, s'il n'est pas dans les connaissances, l'ajouter
         * Sinon mettre à jour ses données avec ce qu'on a observé
         */
        for (Couple<String, List<Attribute>> couple: lobs) {
            n = this.getGraph().getNode(couple.getLeft());
        	attr = couple.getRight();
        	if (!attr.isEmpty())
        		System.out.println(attr.toString());

            if (n != null) {
            	
            	// Ne pas mettre a jour si l'observation voit le trésor alors que l'on a su à un moment
            	// au moins le montant exacte
            	try {
            		if (couple.getRight().contains(Attribute.TREASURE)) {
            			for ( Attribute a : couple.getRight() ) {
            				if (a.equals(Attribute.TREASURE)) {
                    			for ( Attribute a2 : (List<Attribute>)n.getAttribute("contenu") ) {
                    				if (a2.equals(Attribute.TREASURE) && (int)a.getValue() != 0/* && (int) a.getValue() > (int)a2.getValue()*/)
                    					continue;
                    			}
            				}
            			}
            				
            		}
            	}
            	catch (Exception e) {
            		System.out.println("");
            	}
            	
                n.addAttribute("contenu", couple.getRight());
                if (attr.contains(Attribute.TREASURE)) {
                	n.setAttribute("ui.class", "treasure");
                	n.setAttribute("real.class", "treasure");
                }
                else if (attr.contains(Attribute.HOWL)) {
                	n.setAttribute("ui.class", "wumpus");
                	n.setAttribute("real.class", "wumpus");
                }
                
                // Ajout de l'arête si besoin
                if (!n.getId().equals(currentNode)) {
                    try {
                        this.getGraph().addEdge(currentNode + n.getId(), currentNode, n.getId()).setAttribute("date", date);
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
                    
                    if (attr.contains(Attribute.TREASURE)) {
                    	n.setAttribute("ui.class", "treasure");
                    	n.setAttribute("real.class", "treasure");
                    }
                    else if (attr.contains(Attribute.HOWL)) {
                    	n.setAttribute("ui.class", "wumpus");
                		n.setAttribute("real.class", "wumpus");
                    }
                    else {
                    	n.setAttribute("ui.class", "unvisited");
                    	n.setAttribute("real.class", "unvisited");
                    }
                    
                    try {
                        this.getGraph().addEdge(currentNode + n.getId(), currentNode, n.getId()).setAttribute("date", date);
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
    	
    	this.currentPosition.setAttribute("ui.class", this.currentPosition.getAttribute("real.class"));
        this.currentPosition = this.graph.getNode(this.myAgent.getCurrentPosition());
        this.currentPosition.setAttribute("visited", true);
        this.currentPosition.setAttribute("ui.class", "agent");
        
        try {
	        if (this.currentPosition.getAttribute("real.class").equals("unvisited"))
	        	this.currentPosition.setAttribute("real.class", "visited");
        }
        catch (Exception e) {
        	this.currentPosition.setAttribute("real.class", "visited");
        }
    }

    /**
     * Crée une instance de Knowledge à partager avec l'agent passé en paramètre
     */
    public HashMap<String, HashMap<String, HashMap<String, Object>>> shareKnowledge(String agentID) {
        Date date = new Date();
        HashMap<String, HashMap<String, HashMap<String, Object>>> toShare = new HashMap<String, HashMap<String, HashMap<String, Object>>>();
        HashMap<String, HashMap<String, Object>> noeuds = new HashMap<String, HashMap<String, Object>>();
        HashMap<String, HashMap<String, Object>> aretes = new HashMap<String, HashMap<String, Object>>();
        HashMap<String, Object> attributes;

        // Si l'agent n'a pas encore été rencontré, on peut tout lui envoyer après mise à jour de la date de dernière communication
        if (!this.lastCommunication.containsKey(agentID)) {
            this.lastCommunication.put(agentID, date);

            // Ajout des noeuds avec leurs attributs
            for (Node n: this.getGraph().getNodeSet()) {
                attributes = new HashMap<String, Object>();
                for (String attribute: n.getAttributeKeySet()) {
                    attributes.put(attribute, n.getAttribute(attribute));
                }
                noeuds.put(n.getId(), attributes);
            }

            // Ajout des arêtes avec leurs attributs
            for (Edge edge: this.getGraph().getEdgeSet()) {
                attributes = new HashMap<String, Object>();
                for (String attribute: edge.getAttributeKeySet()) {
                    attributes.put(attribute, edge.getAttribute(attribute));
                }
                attributes.put("node0", edge.getNode0().getId());
                attributes.put("node1", edge.getNode1().getId());
                aretes.put(edge.getId(), attributes);
            }
        }
        else {
            //this.lastCommunication.put(agentID, date);

            // Ajout des noeuds avec leurs attributs
            for (Node n: this.getGraph().getNodeSet()) {
                if (((Date) n.getAttribute("date")).compareTo(this.lastCommunication.get(agentID)) > 0) {
                    attributes = new HashMap<String, Object>();
                    for (String attribute : n.getAttributeKeySet()) {
                        attributes.put(attribute, n.getAttribute(attribute));
                    }
                    noeuds.put(n.getId(), attributes);
                }
            }

            // Ajout des arêtes avec leurs attributs
            for (Edge edge: this.getGraph().getEdgeSet()) {
                if (((Date) edge.getAttribute("date")).compareTo(this.lastCommunication.get(agentID)) > 0) {
                    attributes = new HashMap<String, Object>();
                    for (String attribute : edge.getAttributeKeySet()) {
                        attributes.put(attribute, edge.getAttribute(attribute));
                    }
                    attributes.put("node0", edge.getNode0().getId());
                    attributes.put("node1", edge.getNode1().getId());
                    aretes.put(edge.getId(), attributes);
                }
            }
        }
        if (noeuds.isEmpty())
            return null;
        toShare.put("nodes", noeuds);
        toShare.put("edges", aretes);
        HashMap<String, HashMap<String, Object>> dateHashHash = new HashMap<>();
        HashMap<String, Object> dateHash = new HashMap<>();
        dateHash.put("date", date);
        dateHashHash.put("date", dateHash);
        toShare.put("date", dateHashHash);
        return toShare;
    }
}
