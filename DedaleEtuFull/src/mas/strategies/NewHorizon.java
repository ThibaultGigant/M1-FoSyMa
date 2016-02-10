package mas.strategies;

import env.Attribute;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import env.Environment;
import mas.abstractAgent;
import mas.util.CustomCouple;
import scala.util.parsing.combinator.testing.Str;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by Fayçal on 08/02/2016.
 */
public class NewHorizon implements IStrategy {

    private mas.abstractAgent myAgent;

    public String moveTo(HashMap<String, CustomCouple<Date, List<Attribute>>> knowledge) {
        List<Attribute> li;
        String room = ""; // The next room the agent will visite
        double distance = 0;    // Best distance between current position and a room not visited yet
        double currentDistance; // Template
        for  ( String roomKey : knowledge.keySet() ) {
            li = knowledge.get(roomKey).getRight();
            // Get the distance between current place and the room described in li
            // Only if the room isn't visited yet
            // Keep the nearest
            currentDistance = distanceToRoom(roomKey);
            if ((distance == 0 && currentDistance != 0)|| distance > currentDistance) {
                distance = currentDistance;
                room = roomKey;
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

    private double distanceToRoom(String room) {
        System.out.println(room);
        System.out.println(this.myAgent);
        String[] currentPosition = (this.myAgent.getCurrentPosition().split("_"));
        String[] roomPosition    = room.split("_");

        return sqrt(pow(Integer.parseInt(currentPosition[0])-Integer.parseInt(roomPosition[0]),2) + pow(Integer.parseInt(currentPosition[1])-Integer.parseInt(roomPosition[1]),2));

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
