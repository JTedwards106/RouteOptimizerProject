package org.example.controllers;

import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import com.google.maps.RoutesApi;
import com.google.maps.errors.ApiException;
import javafx.application.Platform;
import org.example.models.DijkstraAlgorithm;
import org.example.models.Graph;
import org.example.models.Route;
import org.example.views.MainView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// Handles user input and fetches routes.
public class RouteController {

    private MainView view;
    private GeoApiContext context;
    private Graph graph;

    public RouteController(MainView view) {
        this.view = view;
        this.context = new GeoApiContext.Builder()
                .apiKey(view.getApiKey()) // Use the API key from MainView
                .build();
        this.graph = new Graph();
        initializeGraph();
    }

    private void initializeGraph() {
        // Add vertices (towns)
        graph.addVertex("Kingston");
        graph.addVertex("Morant Point");
        graph.addVertex("Spanish Town");
        graph.addVertex("Ocho Rios");
        graph.addVertex("Montego Bay");
        graph.addVertex("Negril");
        graph.addVertex("Mandeville");
        graph.addVertex("May Pen");

        // Add edges (roads) with distance and time.  Include road type info.
        graph.addEdge("Kingston", "Spanish Town", 21.7, 30, true, false, false, false); // Highway
        graph.addEdge("Kingston", "Morant Point", 87.4, 105, false, true, false, false);
        graph.addEdge("Spanish Town", "May Pen", 23.0, 30, true, false, false, false); // Highway
        graph.addEdge("May Pen", "Mandeville", 35.4, 45, false, false, false, true);
        graph.addEdge("Mandeville", "Montego Bay", 97.0, 120, false, false, false, true);
        graph.addEdge("Montego Bay", "Negril", 79.0, 100, false, false, false, false);
        graph.addEdge("Ocho Rios", "Montego Bay", 95.0, 110, false, true, false, false);
        graph.addEdge("Kingston", "Ocho Rios", 86.0, 90, true, true, false, false); //Highway

        // Additional edges can be added to expand the road network
    }

    // Finds and displays the route based on user input.
    public void findAndDisplayRoute() {
        String source = view.getSource();
        String destination = view.getDestination();

        if (source == null || destination == null) {
            view.displayRoute(new Route(0, new ArrayList<>()), new Route(0, new ArrayList<>()));
            return;
        }

        Map<String, Boolean> constraints = new HashMap<>();
        constraints.put("avoidTolls", view.isAvoidTolls());
        constraints.put("useHighways", view.isUseHighways());
        constraints.put("avoidHighways", view.isAvoidHighways());
        constraints.put("avoidInnerCity", view.isAvoidInnerCity());
        constraints.put("avoidHilly", view.isAvoidHilly());

        // Use CompletableFuture to perform the route calculation asynchronously
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {

                // 1.  Calculate Routes
                Route distanceRoute = DijkstraAlgorithm.findShortestPath(graph, source, destination, "distance", constraints);
                Route timeRoute = DijkstraAlgorithm.findShortestPath(graph, source, destination, "time", constraints);

                // 2. Update UI on the JavaFX thread
                Platform.runLater(() -> view.displayRoute(distanceRoute, timeRoute));

            } catch (Exception e) {
                e.printStackTrace();
                // Handle errors appropriately (e.g., show an error message in the UI)
                Platform.runLater(() -> {
                    view.distanceLabel.setText("Error: " + e.getMessage());
                    view.timeLabel.setText("Error: " + e.getMessage());
                });
            }
        });
    }
}