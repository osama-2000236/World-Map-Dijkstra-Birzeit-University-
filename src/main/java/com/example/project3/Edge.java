package com.example.project3;


public class Edge {
    private Point destination;
    private double cost;
    private int time;

    public Edge(Point destination, double cost, int time) {
        this.destination = destination;
        this.cost = cost;
        this.time = time;
    }

    public Point getDestination() {
        return destination;
    }
    public double getCost() {
        return cost;
    }
    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Edge to " + destination.getName() + " [cost=" + cost + ", time=" + time + "]";
    }
}
