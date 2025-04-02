package org.example.views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.example.controllers.RouteController;
import org.example.models.Route;

import java.util.ArrayList;
import java.util.List;

public class MainView extends Application {

    private TextField sourceTextField;
    private TextField destinationTextField;
    private ListView<String> sourceSuggestions;
    private ListView<String> destinationSuggestions;
    private CheckBox avoidTollsCheckBox;
    private CheckBox useHighwaysCheckBox;
    private CheckBox avoidHighwaysCheckBox;
    private CheckBox avoidInnerCityCheckBox;
    private CheckBox avoidHillyCheckBox;
    private Button findRouteButton;
    public Label distanceLabel;
    public Label timeLabel;
    private WebView mapView;
    private WebEngine webEngine;
    private RouteController routeController;
    private MapView mapViewController;

    // Google Maps API Key
    private final String API_KEY = "AIzaSyAostccD2PjvYuuP29C-JkS_L7aI_mI6lM"; // Replace with your actual API key

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AofA Delivery Route Planner");

        // Set up the JavaFX UI
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label sourceLabel = new Label("Source:");
        sourceTextField = new TextField();
        sourceSuggestions = new ListView<>();
        sourceSuggestions.setVisible(false); // Initially hidden
        Label destinationLabel = new Label("Destination:");
        destinationTextField = new TextField();
        destinationSuggestions = new ListView<>();
        destinationSuggestions.setVisible(false); // Initially hidden
        avoidTollsCheckBox = new CheckBox("Avoid Tolls");
        useHighwaysCheckBox = new CheckBox("Use Highways");
        avoidHighwaysCheckBox = new CheckBox("Avoid Highways");
        avoidInnerCityCheckBox = new CheckBox("Avoid Inner-City Roads");
        avoidHillyCheckBox = new CheckBox("Avoid Hilly Roads");
        findRouteButton = new Button("Find Route");
        distanceLabel = new Label("Shortest Distance: ");
        timeLabel = new Label("Shortest Time: ");
        mapView = new WebView();
        webEngine = mapView.getEngine();

        // Add the controls to the grid
        grid.add(sourceLabel, 0, 0);
        grid.add(sourceTextField, 1, 0);
        grid.add(sourceSuggestions, 1, 1);
        grid.add(destinationLabel, 0, 2);
        grid.add(destinationTextField, 1, 2);
        grid.add(destinationSuggestions, 1, 3);
        grid.add(avoidTollsCheckBox, 0, 4);
        grid.add(useHighwaysCheckBox, 1, 4);
        grid.add(avoidHighwaysCheckBox, 0, 5);
        grid.add(avoidInnerCityCheckBox, 1, 5);
        grid.add(avoidHillyCheckBox, 0, 6);
        grid.add(findRouteButton, 1, 6);
        grid.add(distanceLabel, 0, 7);
        grid.add(timeLabel, 1, 7);
        grid.add(mapView, 0, 8, 2, 1); // Span two columns

        Scene scene = new Scene(grid, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize the RouteController and MapView
        routeController = new RouteController(this);
        mapViewController = new MapView(webEngine, API_KEY);

        // Load initial map
        mapViewController.loadMap();

        // Set up event listeners
        findRouteButton.setOnAction(event -> routeController.findAndDisplayRoute());
        sourceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                sourceSuggestions.setVisible(false); // Hide suggestions when input is cleared
            } else {
                routeController.getPlaceSuggestions(newValue, true); // Fetch suggestions for source
            }
        });
        destinationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                destinationSuggestions.setVisible(false); // Hide suggestions when input is cleared
            } else {
                routeController.getPlaceSuggestions(newValue, false); // Fetch suggestions for destination
            }
        });
    }

    // Method to update the UI with route details
    public void displayRoute(Route distanceRoute, Route timeRoute) {
        Platform.runLater(() -> {
            if (distanceRoute == null || distanceRoute.getPath() == null || distanceRoute.getPath().isEmpty() ||
                    timeRoute == null || timeRoute.getPath() == null || timeRoute.getPath().isEmpty()) {
                distanceLabel.setText("Shortest Distance: No route found");
                timeLabel.setText("Shortest Time: No route found");
                mapViewController.clearMap();
            } else {
                double distance = distanceRoute.getTotalMetric();
                double duration = timeRoute.getTotalMetric() / 60.0; // Convert to minutes

                distanceLabel.setText("Shortest Distance: " + String.format("%.2f", distance / 1000.0) + " km"); // Convert to km
                timeLabel.setText("Shortest Time: " + String.format("%.2f", duration) + " minutes");

                // Extract path (list of towns) from the Route object.
                List<String> distancePath = extractPath(distanceRoute);
                List<String> timePath = extractPath(timeRoute);

                mapViewController.displayRoute(distancePath); // Show either distance or time path
            }
        });
    }

    private List<String> extractPath(Route route) {
        List<String> path = new ArrayList<>();
        if (route != null && route.getPath() != null && !route.getPath().isEmpty()) {
            path.addAll(route.getPath());
        }
        return path;
    }

    public void updateSourceSuggestions(List<String> suggestions) {
        if (suggestions.isEmpty()) {
            sourceSuggestions.setVisible(false);
        } else {
            sourceSuggestions.getItems().setAll(suggestions);
            sourceSuggestions.setVisible(true);
            sourceSuggestions.setOnMouseClicked(event -> {
                String selectedSuggestion = sourceSuggestions.getSelectionModel().getSelectedItem();
                if (selectedSuggestion != null) {
                    sourceTextField.setText(selectedSuggestion);
                    sourceSuggestions.setVisible(false);
                }
            });
        }
    }

    public void updateDestinationSuggestions(List<String> suggestions) {
        if (suggestions.isEmpty()) {
            destinationSuggestions.setVisible(false);
        } else {
            destinationSuggestions.getItems().setAll(suggestions);
            destinationSuggestions.setVisible(true);
            destinationSuggestions.setOnMouseClicked(event -> {
                String selectedSuggestion = destinationSuggestions.getSelectionModel().getSelectedItem();
                if (selectedSuggestion != null) {
                    destinationTextField.setText(selectedSuggestion);
                    destinationSuggestions.setVisible(false);
                }
            });
        }
    }

    public String getSource() {
        return sourceTextField.getText();
    }

    public String getDestination() {
        return destinationTextField.getText();
    }

    public boolean isAvoidTolls() {
        return avoidTollsCheckBox.isSelected();
    }

    public boolean isUseHighways() {
        return useHighwaysCheckBox.isSelected();
    }

    public boolean isAvoidHighways() {
        return avoidHighwaysCheckBox.isSelected();
    }

    public boolean isAvoidInnerCity() {
        return avoidInnerCityCheckBox.isSelected();
    }

    public boolean isAvoidHilly() {
        return avoidHillyCheckBox.isSelected();
    }

    public String getApiKey() {
        return API_KEY;
    }

}