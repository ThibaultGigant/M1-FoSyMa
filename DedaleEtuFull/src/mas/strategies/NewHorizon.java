package mas.strategies;

import env.Attribute;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.graphstream.algorithm.AStar;
import org.graphstream.algorithm.AStar.Costs;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.networksimplex.NetworkSimplex;
import org.graphstream.graph.BreadthFirstIterator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import env.Environment;
import mas.abstractAgent;
import mas.agents.ExploAgent;
import mas.util.CustomCouple;
import scala.util.parsing.combinator.testing.Str;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by Fayçal on 08/02/2016.
 */
public class NewHorizon implements IStrategy {

    private mas.abstractAgent myAgent;

    public String moveTo(Graph knowledge) {
        List<Attribute> li;
        String room = ""; // The next room the agent will visite
        int distance = 0;    // Best distance between current position and a room not visited yet
        int currentDistance; // Template
        for  ( Node roomKey : knowledge.getNodeSet() ) {
            // Get the distance between current place and the room described in li
            // Only if the room isn't visited yet
            // Keep the nearest
        	if ((int) roomKey.getAttribute("visited") == 1)
        		continue;
        	
            currentDistance = distanceToRoom(roomKey.getId());
            if ((distance == 0 && currentDistance != 0)|| distance > currentDistance) {
                distance = currentDistance;
                room = roomKey.getId();
            }
        }

        // If there is no known room unvisited yet, choose randomly
        if (room.equals("")) {
            List<Environment.Couple<String, List<Attribute>>> lobs = this.myAgent.observe();
            //Random move from the current position
            Random r= new Random();
            int moveId=r.nextInt(lobs.size());

            //The move action (if any) should be the last action of your behaviour
            room = lobs.get(moveId).getLeft();
        }
        else {
            room = getNextRoom(room);
        }

        System.out.println("Agent " + this.myAgent.getLocalName() + " going to: " + room);
        return room;
    }

    private int distanceToRoom(String room) {
        String currentPosition = (this.myAgent.getCurrentPosition());
        
        AStar astar = new AStar( ((ExploAgent)(this.myAgent)).getKnowledge());
        astar.compute(currentPosition, room);

        Path path = astar.getShortestPath();
        
        if (path == null) {
        	return 0;
        }
        
        return path.getEdgeCount();

    }

    private double distanceToRoom(String room, String currentRoom) {
        String[] currentPosition = currentRoom.split("_");
        String[] roomPosition    = room.split("_");

        return sqrt(pow(Integer.parseInt(currentPosition[0])-Integer.parseInt(roomPosition[0]),2) + pow(Integer.parseInt(currentPosition[1])-Integer.parseInt(roomPosition[1]),2));

    }

    private String getNextRoom(String room) {
        double distance;

        List<Environment.Couple<String, List<Attribute>>> lobs = this.myAgent.observe();
        String[] roomPosition = room.split("_");

        double distanceMin = distanceToRoom(lobs.get(0).getLeft(), room);
        String destinationRoom = lobs.get(0).getLeft();
        for (Environment.Couple<String, List<Attribute>> c: lobs) {
            distance = distanceToRoom(c.getLeft(), room);
            if (distance < distanceMin) {
                destinationRoom = c.getLeft();
            }
        }

        return destinationRoom;
    }

    public void setMyAgent(abstractAgent myAgent) {
        this.myAgent = myAgent;
    }
}
