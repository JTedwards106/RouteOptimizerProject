package org.example.models;

import java.util.List;

// Stores route details.
public class Route {
    private double totalMetric; // Can be distance or time
    private List<String> path;

    public Route(double totalMetric, List<String> path) {
        this.totalMetric = totalMetric;
        this.path = path;
    }

    public double getTotalMetric() {
        return totalMetric;
    }

    public List<String> getPath() {
        return path;
    }
}
