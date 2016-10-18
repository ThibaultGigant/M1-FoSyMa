package mas.util;

import env.Attribute;
import env.Couple;
import jade.core.AID;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mas.util.Debug;
import mas.util.TreasureTargeted;
import mas.util.noWumpus;
import org.w3c.dom.Attr;

/**
 * Classe permettant d'effectuer certaines actions courantes nécessaires au projet sur des graphes
 * Created by Tigig on 13/02/2016.
 */
public class GraphTools {

    private static boolean debugFlag = true;

    /**
     * Effectue un parcours en largeur du graphe à partir de la position courante
     * pour trouver l'ID du prochain noeud sur le chemin vers le prochain noeud encore non-visité
     * @param currentPosition ID du noeud courant de l'agent
     * @param graph graphe dans lequel on cherche
     * @param stopCriterion critère d'arrêt de l'algorithme : valeur d'un attibut d'un noeud
     * @return Liste des noeuds formant le chemin vers le prochain noeud voulu
     *          Retourne une liste vite s'il n'y a plus de noeud correspondant au citère d'arrêt dans le graphe
     */
    public static List<String> pathToTarget(String currentPosition, Graph graph, String stopCriterion, String valueOfStopCriterion) {
        return pathToTarget(currentPosition, graph, stopCriterion, valueOfStopCriterion, null);
    }

    /**
     * Effectue un parcours en largeur du graphe à partir de la position courante
     * pour trouver l'ID du prochain noeud sur le chemin vers le prochain noeud encore non-visité
     * @param currentPosition ID du noeud courant de l'agent
     * @param graph graphe dans lequel on cherche
     * @param stopCriterion critère d'arrêt de l'algorithme : valeur d'un attibut d'un noeud
     * @param valueOfStopCriterion valeur que doit avoir le critère
     * @param casesToAvoid liste des noeuds à éviter
     * @return Liste des noeuds formant le chemin vers le prochain noeud voulu
     *          Retourne une liste vite s'il n'y a plus de noeud correspondant au citère d'arrêt dans le graphe
     */
    public static List<String> pathToTarget(String currentPosition, Graph graph, String stopCriterion, String valueOfStopCriterion, List<String> casesToAvoid) {
        // Initialisation des variables nécessaires
        ArrayList<String> nodes = new ArrayList<String>(); // Liste des noeuds parcourus par l'algorithme
        HashMap<String, String> peres = new HashMap<String, String>(); // Map des IDs des noeuds et leur père dans le chemin
        List<String> path = new ArrayList<String>(); // Liste des noeuds à parcourir pour aller au point voulu
        List<Node> nodesToAvoid = new ArrayList<Node>(); // Liste des noeuds à éviter
        // Itérateur sur les successeurs d'un noeud
        Iterator<Node> nodeIterator;
        Node pere;
        Node tempNode = graph.getNode(currentPosition);
        boolean found = false;
        String temp;

        // Liste des noeud à éviter
        if (casesToAvoid != null)
            for (String caseToAvoid : casesToAvoid)
                nodesToAvoid.add(graph.getNode(caseToAvoid));

        nodes.add(currentPosition);

        // Recherche du premier noeud correspondant au critère d'arrêt
        while (!nodes.isEmpty() && !found) {
            pere = graph.getNode(nodes.get(0));
            nodeIterator = pere.getNeighborNodeIterator();
            nodes.remove(0);
            while (nodeIterator.hasNext()) {
                tempNode = nodeIterator.next();
                if (!peres.containsKey(tempNode.getId()) && !nodesToAvoid.contains(tempNode)) {// && !noWumpus.isWumpus(graph, tempNode.getId())) {
                    // On accepte d'aller sur un case dangereuse si elle n'a pas encore été découverte
                    // Mais nous ne laissons pas un agent traverser une telle case pour en atteindre une autre
                    if ((tempNode.hasAttribute(stopCriterion) && tempNode.getAttribute(stopCriterion).equals(valueOfStopCriterion)) || !noWumpus.isWumpus(graph, tempNode.getId())) {
                        peres.put(tempNode.getId(), pere.getId());
                        if (tempNode.hasAttribute(stopCriterion) && tempNode.getAttribute(stopCriterion).equals(valueOfStopCriterion)) {
                            found = true;
                            break;
                        }
                        nodes.add(tempNode.getId());
                    }
                }
            }
        }

        // Si found est toujours false, alors on a parcouru tout le graphe sans trouver de noeud correspondant au critère d'arrêt
        if (!found) {
            return path;
        }
        // Sinon, recherche du premier noeud sur le chemin vers le noeud non-visité
        path.add(0, tempNode.getId());
        temp = tempNode.getId();
        while (!peres.get(temp).equals(currentPosition)) {
            temp = peres.get(temp);
            path.add(0, temp);
        }
        return path;
    }

    public static List<String> bestPath(AID agent, String currentPosition, Graph graph, List<String> casesToAvoid, int bagContent) {
        /**
         * Initialisation des variables nécessaires
         * ----------------------------------------
         * nodes                : Liste des noeuds parcourus par l'algorithme
         * peres                : Map des IDs des noeuds et leur père dans le chemin
         * path                 : Liste des noeuds à parcourir pour aller au point voulu
         * nodesToAvoid         : Liste des noeuds à éviter
         *
         * treasures            : Map des trésors identifiés
         * treasuresPath        : Map des chemins menant aux trésors identifiés
         * treasuresToWumpus    : Map des distances de la plus proche case considérée puante aux trésors
         * treasuresTargeted    : Map des trésors déjà ciblés par un agent
         */
        ArrayList<String> nodes = new ArrayList<String>();
        HashMap<String, String> peres = new HashMap<String, String>();
        List<String> path = new ArrayList<String>();
        List<Node> nodesToAvoid = new ArrayList<Node>();

        HashMap<String,Integer> treasures = new HashMap<String, Integer>();
        HashMap<String,List<String>> treasuresPath = new HashMap<String, List<String>>();
        HashMap<String,Integer> treasuresToWumpus = new HashMap<String, Integer>();
        HashMap<String, TreasureTargeted> treasuresTargeted = new HashMap<String, TreasureTargeted>();


        // Itérateur sur les successeurs d'un noeud
        Iterator<Node> nodeIterator;
        Node pere;
        Node tempNode = graph.getNode(currentPosition);
        boolean found = false;
        String temp;

        // Liste des noeud à éviter
        if (casesToAvoid != null)
            for (String caseToAvoid : casesToAvoid)
                nodesToAvoid.add(graph.getNode(caseToAvoid));

        nodes.add(currentPosition);

        // Parcours du graph entier
        while (!nodes.isEmpty()) {
            pere = graph.getNode(nodes.get(0));
            nodeIterator = pere.getNeighborNodeIterator();
            nodes.remove(0);
            while (nodeIterator.hasNext()) {
                tempNode = nodeIterator.next();
                if (!peres.containsKey(tempNode.getId()) && !nodesToAvoid.contains(tempNode)) {// && noWumpus.isWumpus(graph, tempNode.getId())) {
                    peres.put(tempNode.getId(), pere.getId());
                    nodes.add(tempNode.getId());

                    // Si c'est un trésor, le garder en mémoire, si aucun agent ne le cible avec une meilleur priorité
                    List<Attribute> attr = tempNode.getAttribute("contenu");
                    for (Attribute attribute : attr) {
                        if (attribute.equals(Attribute.TREASURE)) {
                            if (tempNode.hasAttribute("ciblé")) {

                                // Valeur de priorité
                                int valeur = bagContent - (Integer) attribute.getValue();
                                if (valeur < 0)
                                    valeur *= -2;

                                // On ne traite que les trésors non ciblé par des agents plus apte
                                if (((TreasureTargeted) tempNode.getAttribute("ciblé")).value < 0 || ((TreasureTargeted) tempNode.getAttribute("ciblé")).value >=  valeur){
                                    treasures.put(tempNode.getId(), (Integer) attribute.getValue());
                                    treasuresTargeted.put(tempNode.getId(), (TreasureTargeted) tempNode.getAttribute("ciblé"));
                                }
                            }
                            else {
                                treasures.put(tempNode.getId(), (Integer) attribute.getValue());
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (treasures.isEmpty()) {
            return pathToTarget(currentPosition, graph, "visited", "false", casesToAvoid);
        }

        // Calculer les distances de chaque trésors à la plus proche case puante
        for (String treasurePlace : treasures.keySet()) {
            treasuresToWumpus.put(treasurePlace, pathToTarget(treasurePlace, graph, "ui.class", "stench", casesToAvoid).size());
            if (treasuresToWumpus.get(treasurePlace) == 0 &&
                    !((String)graph.getNode(treasurePlace).getAttribute("ui.class")).equals("stench")) {
                treasuresToWumpus.put(treasurePlace, -1);
            }
        }

        // Calculer la distance à chaque trésor
        for (String treasurePlace : treasures.keySet())
            treasuresPath.put(treasurePlace, foundPath(peres, currentPosition, treasurePlace));

        return bestTreasure(agent, treasuresPath, treasuresToWumpus, bagContent, treasures, treasuresTargeted);
    }

    /**
     * Renvoie le chemin trouvé entre currentPosition et lastPosition
     * @param peres             : HashMap contenant les pères de chaque noeud
     * @param currentPosition   : la position de départ
     * @param lastPosition      : la position d'arrivée
     * @return                  : Le chemin trouvé s'il y en a un, sinon une liste vide
     */
    private static List<String> foundPath(HashMap<String, String> peres, String currentPosition, String lastPosition) {
        List<String> path = new ArrayList<String>();
        // Si aucun chemin n'a été trouvé
        if (!peres.containsKey(lastPosition)) {
            return path;
        }

        String temp;
        // Sinon, recherche du premier noeud sur le chemin vers le noeud non-visité
        path.add(0, lastPosition);
        temp = lastPosition;
        while (!peres.get(temp).equals(currentPosition)) {
            temp = peres.get(temp);
            path.add(0, temp);
        }
        return path;
    }

    /**
     * Choisit le meilleur trésor à aller chercher parmis ceux que l'on peut atteindre
     * @param treasuresPath         : HashMap des chemins menant aux trésors, indexé par leurs positions
     * @param treasuresToWumpus     : HashMap des distances des trésors aux plus proches Wumpus présumés, indexé par leurs positions
     * @param bagContent            : Capacité actuelle du sac
     * @param treasures             : HashMap des montant des trésors, indexé par leurs positions
     * @return                      : Le chemin menant au meilleur trésor, s'il y en a un, sinon une liste vide
     */
    private static List<String> bestTreasure(AID agent, HashMap<String, List<String>> treasuresPath, HashMap<String, Integer> treasuresToWumpus, int bagContent, HashMap<String,Integer> treasures, HashMap<String, TreasureTargeted> treasuresTargeted) {
        List<String> path;
        List<String> shortestPath = new ArrayList<String>();
        List<String> inDangerPath = new ArrayList<String>();
        int lenght;
        int diff = -1;
        int minDiff = -1;
        int minLenghtToDanger = -1;


        // Pour chaque trésor
        for (String treasure : treasuresPath.keySet()) {
            path = treasuresPath.get(treasure);
            lenght = path.size();

            diff = bagContent - treasures.get(treasure);

            // Recherche du trésor le plus proche d'une case considérée comme puante
            if ( treasuresToWumpus.get(treasure) != -1 && (minLenghtToDanger > treasuresToWumpus.get(treasure) || minLenghtToDanger == -1)) {
                minLenghtToDanger = treasuresToWumpus.get(treasure);
                inDangerPath = path;
            }

            // Pénalisation si le trésor est trop gros
            if (diff < 0)
                diff *= -2;
            // else if (diff > treasures.get(treasure)) diff = treasures.get(treasure); // Pour donner un priviliège aux tout petits trésors

            if (minDiff > diff || minDiff == -1) {
                minDiff = diff;
                shortestPath = path;
            }
        }

        // S'il y a un trésor en danger
        if (!inDangerPath.isEmpty()) {
            System.out.println("inDanger");
            return inDangerPath;
        }

        return shortestPath;
    }
}
