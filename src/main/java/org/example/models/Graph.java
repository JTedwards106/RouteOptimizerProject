package org.example.models;



import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Represents a road network as a graph.
public class Graph {
    private Map<String, Map<String, Edge>> adjacencyMap;

    public Graph() {
        this.adjacencyMap = new HashMap<>();
    }

    public void addVertex(String vertex) {
        if (!adjacencyMap.containsKey(vertex)) {
            adjacencyMap.put(vertex, new HashMap<>());
        }
    }

    public void addEdge(String source, String destination, double distance, double time,
                        boolean isHighway, boolean isToll, boolean isInnerCity, boolean isHilly) {
        if (!adjacencyMap.containsKey(source) || !adjacencyMap.containsKey(destination)) {
            System.out.println("Vertex not found.  Source: " + source + ", Destination: " + destination);
            return;
        }
        adjacencyMap.get(source).put(destination, new Edge(distance, time, isHighway, isToll, isInnerCity, isHilly));
        adjacencyMap.get(destination).put(source, new Edge(distance, time, isHighway, isToll, isInnerCity, isHilly)); // Assuming undirected graph
    }

    public Map<String, Map<String, Edge>> getAdjacencyMap() {
        return adjacencyMap;
    }

    public Set<String> getVertices() {
        return adjacencyMap.keySet();
    }
}
