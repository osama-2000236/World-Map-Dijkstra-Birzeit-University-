package com.example.project3;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main application class to load capitals from a file,
 * display them on a world map, and run Dijkstra for shortest path.
 */
public class Main extends Application {

    // We store a list of capitals for the ComboBoxes
    private ObservableList<Capital> comboBoxList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {

        // This will hold all the capitals (vertices)
        classicSLL capitalsList = new classicSLL();

        BorderPane root = new BorderPane();
        MyPane thePane = new MyPane();
        root.setCenter(thePane);

        // Read from your new data file, e.g., "WorldCapitalsData.txt"
        readTheFile(capitalsList);

        // Set the combo box contents
        thePane.getSource().setItems(comboBoxList);
        thePane.getDestination().setItems(comboBoxList);

        // Place capital circles on the map
        thePane.placeCircles(capitalsList);

        // Set the action when the Run button is clicked
        thePane.getRun().setOnAction(e -> {

            // Reset text areas
            thePane.getDistance().clear();
            thePane.getPath().clear();

            // Get source/destination from the ComboBoxes
            Capital source = thePane.getSource().getSelectionModel().getSelectedItem();
            Capital destination = thePane.getDestination().getSelectionModel().getSelectedItem();

            if (source == null || destination == null) {
                thePane.popWarning("Pick both Source and Destination to continue.");
            } else if (source.equals(destination)) {
                thePane.popWarning("Source and Destination are the same.");
                thePane.getDistance().setText("0");
                thePane.getPath().setText(source.getName());
            } else {
                // Clear distances and reset map lines
                clearDistances(capitalsList);
                thePane.clear(capitalsList);

                // Get the shortest path via Dijkstra
                classicSLL path = getPath(source, destination, capitalsList);

                // Draw the path lines
                thePane.drawPath(path);

                // Format and show distance
                DecimalFormat formatter = new DecimalFormat("#.###");
                String result = formatter.format(destination.getDistance());
                thePane.getDistance().setText(result);

                // Show path as a string
                thePane.getPath().setText(path.toString());
            }
        });

        Scene scene = new Scene(root, 400, 400);
        // Load your CSS if desired
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setTitle("World Capitals");
        primaryStage.show();
    }

    /**
     * Loops through the list of capitals and sets their distance to infinity.
     */
    private void clearDistances(classicSLL capitals) {
        SLLNode current = capitals.getFirstNode();
        while (current != null) {
            current.getElement().setDistance(Double.MAX_VALUE);
            current = current.getNext();
        }
    }

    /**
     * Performs Dijkstra’s algorithm to get the shortest path from 'source' to 'destination'.
     */
    private classicSLL getPath(Capital source, Capital destination, classicSLL capitals) {

        // Create a heap large enough for all capitals
        Heap heap = new Heap(850);

        // Give the source the lowest priority (distance = 0)
        source.setDistance(0);
        heap.insertMinHeap(source);

        // Until the heap is empty, pop the minimum, then update neighbors
        while (!heap.isEmpty()) {
            Capital c = heap.removeMin();
            // adjacency list (neighbors)
            SLLNode adjacencyList = capitals.getNode(c.getName()).getEdges().getFirstNode();

            while (adjacencyList != null) {
                Capital neighbor = adjacencyList.getElement();
                double newDist = haversineDistance(c, neighbor);

                if (c.getDistance() + newDist < neighbor.getDistance()) {
                    neighbor.setDistance(c.getDistance() + newDist);
                    neighbor.setPre(c);
                    heap.insertMinHeap(neighbor);
                }
                adjacencyList = adjacencyList.getNext();
            }
        }

        // Build the path from destination back to source using 'pre'
        classicSLL path = new classicSLL();
        SLLNode node = capitals.getNode(destination.getName());

        while (node != null) {
            path.addFirst(node.getElement());
            if (node.getElement().getDistance() == 0) break;
            Capital predecessor = node.getElement().getPre();
            if (predecessor == null) break; // no path
            node = capitals.getNode(predecessor.getName());
        }
        return path;
    }

    /**
     * Returns the haversine distance in KM between two capitals.
     */
    private static double haversineDistance(Capital c1, Capital c2) {

        final double R = 6371.0; // Earth’s radius in km
        double lat1 = Math.toRadians(c1.getLatitude());
        double lon1 = Math.toRadians(c1.getLongitude());
        double lat2 = Math.toRadians(c2.getLatitude());
        double lon2 = Math.toRadians(c2.getLongitude());

        double latDiff = lat2 - lat1;
        double lonDiff = lon2 - lon1;
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                + Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2)
                - Math.cos(lat1) * Math.cos(lat2);
        // Note: The standard haversine is:
        // a = sin^2(latDiff/2) + cos(lat1)*cos(lat2)*sin^2(lonDiff/2)
        // Make sure it's correct:
        a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Reads capitals and edges from a file, populating the linked list.
     */
    private void readTheFile(classicSLL capitalsList) {
        try {
            // Suppose you have a file named "WorldCapitalsData.txt" in your project directory
            File selectedFile = new File("WorldCapitalsData.txt");
            Scanner input = new Scanner(selectedFile);

            if (!input.hasNext()) {
                throw new NumberFormatException("Empty file or missing first line");
            }

            // Read the first line: # of vertices, # of edges
            String[] firstLine = input.nextLine().split(" ");
            int vertexNum = Integer.parseInt(firstLine[0]);
            int edgeNum = Integer.parseInt(firstLine[1]);

            // Read the capitals
            for (int i = 0; i < vertexNum && input.hasNextLine(); i++) {
                String[] line = input.nextLine().split(" ");
                // line[0] -> name, line[1] -> lat, line[2] -> lon
                Capital c = new Capital(
                        line[0],
                        Double.parseDouble(line[1]),
                        Double.parseDouble(line[2])
                );
                capitalsList.addLast(c);
                comboBoxList.add(c);
            }

            // Read the edges
            for (int i = 0; i < edgeNum && input.hasNextLine(); i++) {
                String[] line = input.nextLine().split(" ");
                // example: "Paris London"
                // This means there's an adjacency from Paris -> London
                SLLNode fromNode = capitalsList.getNode(line[0]);
                Capital toCapital = capitalsList.get(line[1]);
                if (fromNode != null && toCapital != null) {
                    fromNode.getEdges().addLast(toCapital);
                }
            }

            input.close();

        } catch (FileNotFoundException e1) {
            System.out.println("File not found!");
            e1.printStackTrace();
        } catch (NumberFormatException ex) {
            System.out.println("Something wrong with the file format.");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
