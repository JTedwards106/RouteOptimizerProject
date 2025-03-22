package org.example.views;

import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import com.google.maps.RoutesApi;
import com.google.maps.errors.ApiException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.example.controllers.RouteController;
import org.example.models.Graph;
import org.example.models.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

// Main UI class
public class MainView extends Application {

    private TextField sourceTextField;
    private TextField destinationTextField;
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
    private final String API_KEY = "YOUR_API_KEY"; // Replace with your actual API key

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AofA Delivery Route Planner");

        // Set up the JavaFX UI
        GridPane grid = new GridPane();
        grid.paddingProperty().set(new Insets(20));
        grid.hgapProperty().set(10);
        grid.vgapProperty().set(10);

        Label sourceLabel = new Label("Source:");
        sourceTextField = new TextField();
        Label destinationLabel = new Label("Destination:");
        destinationTextField = new TextField();
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
        grid.add(destinationLabel, 0, 1);
        grid.add(destinationTextField, 1, 1);
        grid.add(avoidTollsCheckBox, 0, 2);
        grid.add(useHighwaysCheckBox, 1, 2);
        grid.add(avoidHighwaysCheckBox, 0, 3);
        grid.add(avoidInnerCityCheckBox, 1, 3);
        grid.add(avoidHillyCheckBox, 0, 4);
        grid.add(findRouteButton, 1, 5);
        grid.add(distanceLabel, 0, 6);
        grid.add(timeLabel, 1, 6);
        grid.add(mapView, 0, 7, 2, 1); // Span two columns

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
    }

    // Method to update the UI with route details
    public void displayRoute(Route distanceRoute, Route timeRoute) {
        Platform.runLater(() -> {
            if (distanceRoute.getPath().isEmpty() || timeRoute.getPath().isEmpty()) {
                distanceLabel.setText("Shortest Distance: No route found");
                timeLabel.setText("Shortest Time: No route found");
                mapViewController.clearMap();
            } else {
                distanceLabel.setText("Shortest Distance: " + String.format("%.2f", distanceRoute.getTotalMetric()) + " km");
                timeLabel.setText("Shortest Time: " + String.format("%.2f", timeRoute.getTotalMetric()) + " minutes");
                mapViewController.displayRoute(distanceRoute.getPath());
            }
        });
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