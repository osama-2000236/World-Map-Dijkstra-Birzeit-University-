package com.example.project3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ClassicalDijkstra {

    private List<Point> pointsList;  // all vertices
    private TableEntry[] table;      // T[v].known, T[v].dist, T[v].path
    private static final double INF = Double.MAX_VALUE;

    private List<Point> path; // final path

    public ClassicalDijkstra(List<Point> points) {
        this.pointsList = points;
        this.path = new ArrayList<>();
        this.table = new TableEntry[points.size()];
    }

    public void dijkstra(Point source, Point destination, boolean useCost) {
        // Initialize the table
        for (int i = 0; i < pointsList.size(); i++) {
            table[i] = new TableEntry();
            table[i].dist = INF;
            table[i].known = false;
            table[i].path = -1;
        }

        int srcIndex = pointsList.indexOf(source);
        if (srcIndex < 0) return;
        table[srcIndex].dist = 0.0;

        while (true) {
            int v = smallestUnknown();
            if (v == -1) break; // no more unknowns
            table[v].known = true;

            // if we've reached destination, can break early
            if (pointsList.get(v).equals(destination)) {
                break;
            }

            // for each adjacent edge from v
            for (Edge edge : pointsList.get(v).getEdges()) {
                int w = pointsList.indexOf(edge.getDestination());
                if (w == -1 || table[w].known) continue;

                double weight = useCost ? edge.getCost() : edge.getTime() ;
                double newDist = table[v].dist + weight;
                if (newDist < table[w].dist) {
                    table[w].dist = newDist;
                    table[w].path = v;
                }
            }
        }

        buildPath(destination);
    }

    private int smallestUnknown() {
        double minDist = INF;
        int indexOfMin = -1;
        for (int i = 0; i < table.length; i++) {
            if (!table[i].known && table[i].dist < minDist) {
                minDist = table[i].dist;
                indexOfMin = i;
            }
        }
        return indexOfMin;
    }

    private void buildPath(Point destination) {
        int destIndex = pointsList.indexOf(destination);
        if (destIndex < 0) return;
        if (table[destIndex].dist == INF) return; // unreachable

        ArrayList<Point> reversePath = new ArrayList<>();
        int current = destIndex;
        while (current != -1) {
            reversePath.add(pointsList.get(current));
            current = table[current].path;
        }
        Collections.reverse(reversePath);
        this.path = reversePath;

        // Debug: Print the path
        System.out.println("Built path:");
        for (Point p : this.path) {
            System.out.print(p.getName() + " ");
        }
        System.out.println();
    }

    public List<Point> getPath() {
        return path;
    }


    public double calculateGeographicalDistance(List<Point> path) {
        if (path.size() < 2) return 0.0;
        double sum = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Point a = path.get(i);
            Point b = path.get(i + 1);
            sum += haversineDistance(a, b);
        }
        return sum;
    }



    private static double haversineDistance(Point p1, Point p2) {

        final double R = 6371.000; // Radius of the earth in kilometers

        // Correct usage of latitude and longitude
        double lat1 = Math.toRadians(p1.getLatitude());
        double lon1 = Math.toRadians(p1.getLongitude());
        double lat2 = Math.toRadians(p2.getLatitude());
        double lon2 = Math.toRadians(p2.getLongitude());

        // Haversine formula
        double latDiff = lat2 - lat1;
        double lonDiff = lon2 - lon1;
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Returning the distance in kilometers
    }



    public double calculateTotalCost(List<Point> path) {
        double total = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Point cur = path.get(i);
            Point nxt = path.get(i + 1);
            boolean edgeFound = false;
            for (Edge e : cur.getEdges()) {
                if (e.getDestination().equals(nxt)) {
                    total += e.getCost();
                    System.out.println("Adding Cost: " + e.getCost() + " from " + cur.getName() + " to " + nxt.getName());
                    edgeFound = true;
                    break;
                }
            }
            if (!edgeFound) {
                System.out.println("No edge found from " + cur.getName() + " to " + nxt.getName());
            }
        }
        return total;
    }


    public int calculateTotalTime(List<Point> path) {
        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Point cur = path.get(i);
            Point nxt = path.get(i + 1);
            boolean edgeFound = false;
            for (Edge e : cur.getEdges()) {
                if (e.getDestination().equals(nxt)) {
                    total += e.getTime();
                    System.out.println("Adding Time: " + e.getTime() + " from " + cur.getName() + " to " + nxt.getName());
                    edgeFound = true;
                    break;
                }
            }
            if (!edgeFound) {
                System.out.println("No edge found from " + cur.getName() + " to " + nxt.getName());
            }
        }
        return total;
    }


    public void printPath(Point v) {
        int idx = pointsList.indexOf(v);
        if (idx < 0) return;
        if (table[idx].path != -1) {
            printPath(pointsList.get(table[idx].path));
            System.out.print(" -> ");
        }
        System.out.print(v.getName());
    }

    private static class TableEntry {
        boolean known;
        double dist;
        int path; // predecessor index
    }
}
