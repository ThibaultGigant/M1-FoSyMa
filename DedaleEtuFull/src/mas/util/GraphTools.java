package mas.util;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
     * @return ID du prochain noeud sur le chemin vers le prochain noeud encore non-visité
     *          Retourne une chaine vide s'il n'y a plus de noeud non-visité dans le graphe (l'agent a déjà tout visité)
     */
    public static String nearestUnvisited(String currentPosition, Graph graph) {
        // Initialisation des variables nécessaires
        ArrayList<String> nodes = new ArrayList<String>(); // Liste des noeuds parcourus par l'algorithme
        HashMap<String, String> peres = new HashMap<String, String>(); // Map des IDs des noeuds et leur père dans le chemin
        // Itérateur sur les successeurs d'un noeud
        Iterator<Node> nodeIterator;
        Node pere;
        Node tempNode = graph.getNode(currentPosition);
        boolean found = false;
        String retour;

        nodes.add(currentPosition);

        // Recherche du premier noeud non-visité
        while (!nodes.isEmpty() && !found) {
            pere = graph.getNode(nodes.get(0));
            nodeIterator = pere.getNeighborNodeIterator();
            nodes.remove(0);
            while (nodeIterator.hasNext()) {
                tempNode = nodeIterator.next();
                if (!peres.containsKey(tempNode.getId())) {
                    peres.put(tempNode.getId(), pere.getId());
                    if (tempNode.getAttribute("visited").equals(false)) {
                        found = true;
                        break;
                    }
                    nodes.add(tempNode.getId());
                }
            }
        }

        // Si found est toujours false, alors on a parcouru tout le graphe sans trouver de noeud non-visité
        if (!found) {
            return "";
        }
        // Sinon, recherche du premier noeud sur le chemin vers le noeud non-visité
        retour = tempNode.getId();
        while (!peres.get(retour).equals(currentPosition)) {
            retour = peres.get(retour);
        }
        return retour;
    }
}
