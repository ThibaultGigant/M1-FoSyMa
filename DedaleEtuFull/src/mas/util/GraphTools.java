package mas.util;

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
        // Initialisation des variables nécessaires
        ArrayList<String> nodes = new ArrayList<String>(); // Liste des noeuds parcourus par l'algorithme
        HashMap<String, String> peres = new HashMap<String, String>(); // Map des IDs des noeuds et leur père dans le chemin
        List<String> path = new ArrayList<String>(); // Liste des noeuds à parcourir pour aller au point voulu
        // Itérateur sur les successeurs d'un noeud
        Iterator<Node> nodeIterator;
        Node pere;
        Node tempNode = graph.getNode(currentPosition);
        boolean found = false;
        String temp;

        nodes.add(currentPosition);

        // Recherche du premier noeud correspondant au critère d'arrêt
        while (!nodes.isEmpty() && !found) {
            pere = graph.getNode(nodes.get(0));
            nodeIterator = pere.getNeighborNodeIterator();
            nodes.remove(0);
            while (nodeIterator.hasNext()) {
                tempNode = nodeIterator.next();
                if (!peres.containsKey(tempNode.getId())) {
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
}
