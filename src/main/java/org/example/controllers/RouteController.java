package org.example.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import org.example.models.Graph;
import org.example.models.Route;
import org.example.views.MainView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RouteController {

    private MainView view;
    private Graph graph;

    public RouteController(MainView view) {
        this.view = view;
        this.graph = new Graph();
        initializeGraph();
    }

    private void initializeGraph() {
        graph.addVertex("Kingston");
        graph.addVertex("Morant Point");
        graph.addVertex("Spanish Town");
        graph.addVertex("Ocho Rios");
        graph.addVertex("Montego Bay");
        graph.addVertex("Negril");
        graph.addVertex("Mandeville");
        graph.addVertex("May Pen");

        graph.addEdge("Kingston", "Spanish Town", 21.7, 30, true, false, false, false);
        graph.addEdge("Kingston", "Morant Point", 87.4, 105, false, true, false, false);
        graph.addEdge("Spanish Town", "May Pen", 23.0, 30, true, false, false, false);
        graph.addEdge("May Pen", "Mandeville", 35.4, 45, false, false, false, true);
        graph.addEdge("Mandeville", "Montego Bay", 97.0, 120, false, false, false, true);
        graph.addEdge("Montego Bay", "Negril", 79.0, 100, false, false, false, false);
        graph.addEdge("Ocho Rios", "Montego Bay", 95.0, 110, false, true, false, false);
        graph.addEdge("Kingston", "Ocho Rios", 86.0, 90, true, true, false, false);
    }

    public void findAndDisplayRoute() {
        String source = view.getSource();
        String destination = view.getDestination();

        if (source == null || destination == null) {
            view.displayRoute(new Route(0, new ArrayList<>()), new Route(0, new ArrayList<>()));
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                Route distanceRoute = getRoute(source, destination, "distance");
                Route timeRoute = getRoute(source, destination, "duration");

                Platform.runLater(() -> view.displayRoute(distanceRoute, timeRoute));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    view.distanceLabel.setText("Error: " + e.getMessage());
                    view.timeLabel.setText("Error: " + e.getMessage());
                });
            }
        });
    }

    private Route getRoute(String origin, String destination, String metric) throws Exception {
        String apiKey = view.getApiKey();
        String urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin +
                "&destination=" + destination + "&key=" + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        JsonArray routes = jsonResponse.getAsJsonArray("routes");

        if (routes.size() == 0) {
            return new Route(Double.POSITIVE_INFINITY, new ArrayList<>());
        }

        JsonObject route = routes.get(0).getAsJsonObject();
        JsonArray legs = route.getAsJsonArray("legs");
        JsonObject leg = legs.get(0).getAsJsonObject();

        double totalDistance = leg.getAsJsonObject("distance").get("value").getAsDouble(); // in meters
        double totalDuration = leg.getAsJsonObject("duration").get("value").getAsDouble(); // in seconds

        List<String> path = new ArrayList<>();
        JsonArray steps = leg.getAsJsonArray("steps");
        for (int i = 0; i < steps.size(); i++) {
            JsonObject step = steps.get(i).getAsJsonObject();
            String instruction = step.getAsJsonObject("html_instructions").getAsString();
            path.add(instruction);
        }

        if (metric.equals("distance")) {
            return new Route(totalDistance, path);
        } else {
            return new Route(totalDuration, path);
        }
    }

    public void getPlaceSuggestions(String input, boolean isSourceField) {
        CompletableFuture.runAsync(() -> {
            try {
                String apiKey = view.getApiKey();
                String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + input +
                        "&types=geocode&key=" + apiKey;

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                JsonArray predictions = jsonResponse.getAsJsonArray("predictions");

                List<String> suggestions = new ArrayList<>();
                for (int i = 0; i < predictions.size(); i++) {
                    JsonObject prediction = predictions.get(i).getAsJsonObject();
                    String description = prediction.get("description").getAsString();
                    suggestions.add(description);
                }

                Platform.runLater(() -> {
                    if (isSourceField) {
                        view.updateSourceSuggestions(suggestions);
                    } else {
                        view.updateDestinationSuggestions(suggestions);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}