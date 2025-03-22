package org.example.models;

import com.sun.javafx.geom.Edge;

import java.util.*;

// Implements shortest path algorithm.
public class DijkstraAlgorithm {

    public static Route findShortestPath(Graph graph, String start, String end, String metric, Map<String, Boolean> constraints) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previousTowns = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>();

        if (graph == null || start == null || end == null) {
            return new Route(Double.POSITIVE_INFINITY, new ArrayList<>());
        }
        for (String town : graph.getVertices()) {
            distances.put(town, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        priorityQueue.add(new Node(start, 0.0));
        Set<String> visited = new HashSet<>();

        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            String currentTown = current.getTown();

            if (visited.contains(currentTown)) {
                continue;
            }
            visited.add(currentTown);

            if (currentTown.equals(end)) {
                break;
            }

            Map<String, Edge> neighbors = graph.getAdjacencyMap().get(currentTown);
            if (neighbors == null) {
                continue; // Handle the case where a town has no neighbors
            }

            for (Map.Entry<String, Edge> neighborEntry : neighbors.entrySet()) {
                String neighbor = neighborEntry.getKey();
                Edge edge = neighborEntry.getValue();

                // Default constraint values
                boolean avoidTolls = constraints.getOrDefault("avoidTolls", false);
                boolean useHighways = constraints.getOrDefault("useHighways", false);
                boolean avoidHighways = constraints.getOrDefault("avoidHighways", false);
                boolean avoidInnerCity = constraints.getOrDefault("avoidInnerCity", false);
                boolean avoidHilly = constraints.getOrDefault("avoidHilly", false);

                if (avoidTolls && edge.isToll()) continue;
                if (useHighways && !edge.isHighway()) continue;
                if (avoidHighways && edge.isHighway()) continue;
                if (avoidInnerCity && edge.isInnerCity()) continue;
                if (avoidHilly && edge.isHilly()) continue;

                double newDistance = distances.get(currentTown) + (metric.equals("distance") ? edge.getDistance() : edge.getTime());

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousTowns.put(neighbor, currentTown);
                    priorityQueue.add(new Node(neighbor, newDistance));
                }
            }
        }

        if (distances.get(end) == Double.POSITIVE_INFINITY) {
            return new Route(Double.POSITIVE_INFINITY, new ArrayList<>()); // Return infinity and empty path
        }

        List<String> path = new ArrayList<>();
        String current = end;
        while (current != null) {
            path.add(current);
            current = previousTowns.get(current);
        }
        Collections.reverse(path);
        return new Route(distances.get(end), path);
    }

    // Node class for priority queue
    private static class Node implements Comparable<Node> {
        private String town;
        private double distance;

        public Node(String town, double distance) {
            this.town = town;
            this.distance = distance;
        }

        public String getTown() {
            return town;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}