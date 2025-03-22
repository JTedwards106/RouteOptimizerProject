package org.example.models;

// Represents a road between two locations.
public class Edge {
    private double distance;
    private double time;
    private boolean isHighway;
    private boolean isToll;
    private boolean isInnerCity;
    private boolean isHilly;

    public Edge(double distance, double time, boolean isHighway, boolean isToll, boolean isInnerCity, boolean isHilly) {
        this.distance = distance;
        this.time = time;
        this.isHighway = isHighway;
        this.isToll = isToll;
        this.isInnerCity = isInnerCity;
        this.isHilly = isHilly;
    }

    public double getDistance() {
        return distance;
    }

    public double getTime() {
        return time;
    }

    public boolean isHighway() {
        return isHighway;
    }

    public boolean isToll() {
        return isToll;
    }

    public boolean isInnerCity() {
        return isInnerCity;
    }

    public boolean isHilly() {
        return isHilly;
    }
}
