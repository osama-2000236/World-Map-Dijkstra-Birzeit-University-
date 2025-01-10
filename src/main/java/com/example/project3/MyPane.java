package com.example.project3;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
// Removed incorrect import
// import org.w3c.dom.events.MouseEvent;

import java.util.List;


public class MyPane extends HBox {

    // private static final double MAP_WIDTH = 1685;
    // private static final double MAP_HEIGHT = 1080;

    private Pane picturePane;
    private ImageView img;
    private Group mapGroup;
    private static int click = 0;
    // UI combos, button, text fields
    private ComboBox<Point> sourceComboBox;
    private ComboBox<Point> destinationComboBox;
    private ComboBox<String> filterComboBox; // "Cost"/"Time"
    private Button runButton;
    private TextArea pathTextArea;
    private TextField distanceTextField;
    private TextField costTextField;
    private TextField timeTextField;

    public MyPane() {
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        // Force the map to 1529×980
        img = new ImageView(new Image("World-Map.jpg"));
        //img.setFitWidth(MAP_WIDTH);
        //img.setFitHeight(MAP_HEIGHT);
        img.setPreserveRatio(false);
        //img.setLayoutX(47);
        //img.setLayoutY(4);
        img.setFitHeight(980);
        img.setFitWidth(1580);

        mapGroup = new Group(img);

        picturePane = new Pane();
        picturePane.setStyle("-fx-border-color: black; -fx-background-color: white;");
        //picturePane.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
        picturePane.getChildren().add(mapGroup);

        // combos
        sourceComboBox = new ComboBox<>();
        sourceComboBox.setPrefWidth(200);

        destinationComboBox = new ComboBox<>();
        destinationComboBox.setPrefWidth(200);

        filterComboBox = new ComboBox<>();
        filterComboBox.getItems().addAll("Cost", "Time");
        filterComboBox.setValue("Cost");

        runButton = new Button("Run");

        pathTextArea = new TextArea();
        pathTextArea.setWrapText(true);
        pathTextArea.setPrefSize(300, 120);
        pathTextArea.setFont(Font.font("Times New Roman", 15));

        distanceTextField = new TextField();
        distanceTextField.setEditable(false);
        distanceTextField.setPrefWidth(100);

        costTextField = new TextField();
        costTextField.setEditable(false);
        costTextField.setPrefWidth(100);

        timeTextField = new TextField();
        timeTextField.setEditable(false);
        timeTextField.setPrefWidth(100);
    }

    private void layoutComponents() {
        VBox controls = new VBox(10);
        controls.setPadding(new Insets(20));
        controls.setAlignment(Pos.TOP_LEFT);

        Label srcLabel   = new Label("Source:");
        Label dstLabel   = new Label("Destination:");
        Label fltLabel   = new Label("Filter:");
        Label pathLabel  = new Label("Path:");
        Label distLabel  = new Label("Distance:");
        Label costLabel  = new Label("Cost:");
        Label timeLabel  = new Label("Time(min):");

        HBox sourceBox = new HBox(10, srcLabel, sourceComboBox);
        HBox destBox   = new HBox(10, dstLabel, destinationComboBox);
        HBox filterBox = new HBox(10, fltLabel, filterComboBox);
        HBox runBox    = new HBox(runButton);

        VBox pathBox   = new VBox(5, pathLabel, pathTextArea);
        HBox distBox   = new HBox(10, distLabel, distanceTextField);
        HBox cBox      = new HBox(10, costLabel, costTextField);
        HBox tBox      = new HBox(10, timeLabel, timeTextField);

        controls.getChildren().addAll(
                sourceBox,
                destBox,
                filterBox,
                runBox,
                pathBox,
                distBox,
                cBox,
                tBox
        );

        this.getChildren().addAll(picturePane, controls);
        this.setSpacing(10);
    }


    public void placeCircles(List<Point> points) {
        if (points == null) {
            popWarning("Points list is null.");
            return;
        }

        for (Point point : points) {
            if (point == null) continue;

            // Create and configure the Circle
            Circle c = new Circle(5);
            Circle p = new Circle(5);

            c.setFill(Color.BLUE);

            point.setCircle(c);
            c.setCenterX(point.getX());
            c.setCenterY(point.getY());
            c.setId(point.getName()); // Set the ID to the point's name
            point.setCircle(c);

// Set the ID to the point's name// Associate the circle with the point

            // Set the mouse click event handler for the circle
            c.setOnMouseClicked((MouseEvent e) -> {
                if (click % 2 == 0) {
                    sourceComboBox.getSelectionModel().select(point);
                } else {

                    destinationComboBox.getSelectionModel().select(point);
                }
                click++;
            });



            // Create and configure the Label for the city name
            Label city = new Label(point.getName());
            city.setId(point.getName() + "lbl");
            city.setFont(Font.font(null, FontWeight.BOLD, 12));
            city.setLayoutX(point.getX() + 6); // Position label slightly offset from the circle
            city.setLayoutY(point.getY() - 10);

            // Add the Circle and Label to the pane
            picturePane.getChildren().addAll(c, city);
        }
    }



    public void drawPath(List<Point> pathList) {
        Platform.runLater(() -> {
            mapGroup.getChildren().removeIf(node -> node instanceof Line);

            for (int i = 0; i < pathList.size() - 1; i++) {
                Point cur = pathList.get(i);
                Point nxt = pathList.get(i + 1);
                if (cur.getCircle() == null || nxt.getCircle() == null) continue;

                double x1 = cur.getCircle().getCenterX();
                double y1 = cur.getCircle().getCenterY();
                double x2 = nxt.getCircle().getCenterX();
                double y2 = nxt.getCircle().getCenterY();

                Line line = new Line(x1, y1, x2, y2);
                line.setStroke(Color.RED);
                line.setStrokeWidth(2);
                mapGroup.getChildren().add(line);
            }
        });
    }


    public void addStreetCircles(List<Point> pathList) {
        Platform.runLater(() -> {
            for (Point p : pathList) {
                if (p.getCircle() != null) {
                    Circle c = new Circle(p.getCircle().getCenterX(), p.getCircle().getCenterY(), 3, Color.GREEN);
                    mapGroup.getChildren().add(c);
                }
            }
        });
    }


    public void clear() {
        Platform.runLater(() -> {
            mapGroup.getChildren().removeIf(node ->
                    node instanceof Line ||
                            (node instanceof Circle && ((Circle) node).getRadius() == 3)
            );
            pathTextArea.clear();
            distanceTextField.clear();
            costTextField.clear();
            timeTextField.clear();
        });
    }

    public void popWarning(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    // Getters for combos, button, text fields
    public ComboBox<Point> getSourceComboBox() {
        return sourceComboBox;
    }
    public ComboBox<Point> getDestinationComboBox() {
        return destinationComboBox;
    }
    public ComboBox<String> getFilterComboBox() {
        return filterComboBox;
    }
    public Button getRunButton() {
        return runButton;
    }
    public TextArea getPathTextArea() {
        return pathTextArea;
    }
    public TextField getDistanceTextField() {
        return distanceTextField;
    }
    public TextField getCostTextField() {
        return costTextField;
    }
    public TextField getTimeTextField() {
        return timeTextField;
    }
}
