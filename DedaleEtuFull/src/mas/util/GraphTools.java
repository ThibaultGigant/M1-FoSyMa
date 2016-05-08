package src.mas.util;

import env.Attribute;
import env.Couple;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Classe permettant d'effectuer certaines actions courantes nécessaires au projet sur des graphes
 * Created by Tigig on 13/02/2016.
 */
public class GraphTools {

    /**
     * Effectue un parcours en largeur du graphe à partir de la position courante
     * pour trouver l'ID du prochain noeud sur le chemin vers le prochain noeud encore non-visité
     * @param currentPosition ID du noeud courant de l'agent
     * @param graph graphe dans lequel on cherche
     * @param stopCriterion critère d'arrêt de l'algorithme : valeur d'un attibut d'un noeud
     * @return Liste des noeuds formant le chemin vers le prochain noeud voulu
     *          Retourne une liste vite s'il n'y a plus de noeud correspondant au citère d'arrêt dans le graphe
     */
    public static List<String> pathToTarget(String currentPosition, Graph graph, String stopCriterion) {
        return pathToTarget(currentPosition, graph, stopCriterion, null);
    }

    /**
     * Effectue un parcours en largeur du graphe à partir de la position courante
     * pour trouver l'ID du prochain noeud sur le chemin vers le prochain noeud encore non-visité
     * @param currentPosition ID du noeud courant de l'agent
     * @param graph graphe dans lequel on cherche
     * @param stopCriterion critère d'arrêt de l'algorithme : valeur d'un attibut d'un noeud
     * @param casesToAvoid liste des noeuds à éviter
     * @return Liste des noeuds formant le chemin vers le prochain noeud voulu
     *          Retourne une liste vite s'il n'y a plus de noeud correspondant au citère d'arrêt dans le graphe
     */
    public static List<String> pathToTarget(String currentPosition, Graph graph, String stopCriterion, List<String> casesToAvoid) {
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
                if (!peres.containsKey(tempNode.getId()) && !nodesToAvoid.contains(tempNode)) {
                    peres.put(tempNode.getId(), pere.getId());
                    if (tempNode.hasAttribute(stopCriterion) && tempNode.getAttribute(stopCriterion).equals(false)) {
                        found = true;
                        break;
                    }
                    nodes.add(tempNode.getId());
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

    public static List<String> bestPath(String currentPosition, Graph graph, List<String> casesToAvoid, int bagContent) {
        // Initialisation des variables nécessaires
        ArrayList<String> nodes = new ArrayList<String>(); // Liste des noeuds parcourus par l'algorithme
        HashMap<String, String> peres = new HashMap<String, String>(); // Map des IDs des noeuds et leur père dans le chemin
        List<String> path = new ArrayList<String>(); // Liste des noeuds à parcourir pour aller au point voulu
        List<Node> nodesToAvoid = new ArrayList<Node>(); // Liste des noeuds à éviter
        HashMap<String,Integer> treasures = new HashMap<String, Integer>(); // Liste des trésors identifiés
        HashMap<String,List<String>> treasuresPath = new HashMap<String, List<String>>();
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
                if (!peres.containsKey(tempNode.getId()) && !nodesToAvoid.contains(tempNode)) {
                    peres.put(tempNode.getId(), pere.getId());
                    nodes.add(tempNode.getId());

                    // Si c'est un trésor, le garder en mémoire
                    List<Attribute> attr = tempNode.getAttribute("contenu");
                    for (Attribute attribute : attr) {
                        if (attribute.equals(Attribute.TREASURE)) {
                            treasures.put(tempNode.getId(), (Integer) attribute.getValue());
                            break;
                        }
                    }
                }
            }
        }

        if (treasures.isEmpty()) {
            return pathToTarget(currentPosition, graph, "visited", casesToAvoid);
        }

        // Calculer la distance à chaque trésor
        for (String treasurePlace : treasures.keySet())
            treasuresPath.put(treasurePlace, foundPath(peres, currentPosition, treasurePlace));

        return bestTreasure(treasuresPath, bagContent, treasures);
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

    private static List<String> bestTreasure(HashMap<String, List<String>> treasuresPath, int bagContent, HashMap<String,Integer> treasures) {
        List<String> path;
        List<String> bestPath = new ArrayList<String>();
        int lenght;
        int diff = -1;
        int min = -1;

        // Pour chaque trésor
        for (String treasure : treasuresPath.keySet()) {
            // TODO prendre en compte les éventuels Wumpus
            path = treasuresPath.get(treasure);
            lenght = path.size();

            diff = bagContent - treasures.get(treasure);

            // Pénalisation si le trésor est trop gros
            if (diff < 0)
                diff *= -2;
            // else if (diff > treasures.get(treasure)) diff = treasures.get(treasure); // Pour donner un priviliège aux tout petits trésors

            if (min > diff || min == -1) {
                min = diff;
                bestPath = path;
            }
        }

        return bestPath;
    }
}
