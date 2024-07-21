package com.example.project3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class HelloApplication extends Application {

    Dijkstra dijkstra = new Dijkstra();
    File file = null;
    MapViewer mapViewer = new MapViewer("world map.jpg", dijkstra);
    int w = 999;
    int h = 650;

    public static void main(String[] args) {
        launch();
    }

    public Button designButton(String name, int width, int height, String color1, String color2) {
        Button button = new Button(name);
        button.setPrefSize(width, height);
        button.setStyle("-fx-background-color:" + color1 + "; -fx-background-radius: 15px; -fx-text-fill:" + color2);
        return button;
    }

    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();

        Image iconImage = new Image("icon.png");

        Image image = new Image("world map.jpg");
        ImageView imageview = new ImageView(image);
        imageview.setFitWidth(w);
        imageview.setFitHeight(h);

        Label label1 = new Label("Source");
        Label label2 = new Label("Target");
        Label label3 = new Label("Shortest Path");
        Label label4 = new Label("Distance");

        ChoiceBox<String> box1 = new ChoiceBox<>();
        box1.setPrefHeight(30);
        box1.setPrefWidth(150);

        ChoiceBox<String> box2 = new ChoiceBox<>();
        box2.setPrefHeight(30);
        box2.setPrefWidth(150);

        TextArea textArea = new TextArea();
        textArea.setPrefHeight(130);
        textArea.setPrefWidth(150);

        TextField textField = new TextField();
        textField.setPrefHeight(30);
        textField.setPrefWidth(150);

        Button load = designButton("Load", 150, 40, "#145A32", "#FDFDFD");
        Button run = designButton("Run", 150, 40, "#145A32", "#FDFDFD");
        Button rest = designButton("Clear", 150, 40, "#145A32", "#FDFDFD");

        run.setDisable(true);
        rest.setDisable(true);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().addAll(label1, box1, label2, box2, label3, textArea, label4, textField, load, run, rest);

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(imageview, vbox);

        pane.getChildren().addAll(hbox);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        stage.setMaximized(true);
        Scene scene = new Scene(pane, 0, 0);
        stage.setScene(scene);
        stage.getIcons().add(iconImage);
        stage.setTitle("World Map");
        stage.show();

        rest.setOnAction(e -> {
            box1.setValue(null);
            box2.setValue(null);
            textArea.setText("");
            textField.setText("");
            GraphicsContext gc = mapViewer.getMapCanvas().getGraphicsContext2D();
            gc.clearRect(0, 0, mapViewer.getMapCanvas().getWidth(), mapViewer.getMapCanvas().getHeight());
        });

        load.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    dijkstra.readData(file);
                    // retrieve and sort the country names
                    List<String> countryNames = new ArrayList<>(dijkstra.getNodesMap().keySet());
                    Collections.sort(countryNames);
                    // add the sorted names to the ChoiceBoxes
                    box1.getItems().addAll(countryNames);
                    box2.getItems().addAll(countryNames);
                    textArea.setText("File Loaded Successfully");
                    insertCountry(pane, box1, box2);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            run.setDisable(false);
            rest.setDisable(false);
        });

        run.setOnAction(e -> {
            String source = box1.getValue();
            String target = box2.getValue();
            if (source == null || target == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("Please Select Both Source And Target");
                alert.showAndWait();
                return;
            }
            // calculate the shortest path using dijkstra algorithm
            dijkstra.calculateShortestPath(source, target);
            String[] shortestPathNodes = dijkstra.getShortestPathNodes();
            double shortestPathWeight = dijkstra.getShortestPathWeight();
            if (shortestPathNodes == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("No Path Found From " + source + " To " + target);
                alert.showAndWait();
                return;
            }
            printShortestPath(shortestPathNodes, textArea);
            textField.appendText("" + shortestPathWeight);
            Pane mapView = mapViewer.getPane();
            pane.getChildren().removeIf(node -> node instanceof Canvas);
            mapViewer.drawShortestPath(shortestPathNodes);
            pane.getChildren().add(mapView.getChildren().get(1));
            insertCountry(pane, box1, box2);
        });
    }

    // function to insert buttons representing countries on the map
    public void insertCountry(Pane pane, ChoiceBox<String> box1, ChoiceBox<String> box2) {
        // iterate through each node in the map
        for (Node node : dijkstra.getNodesMap().values()) {
            // calculate the coordinates for the button
            double y = h - ((node.x + 90) / 180 * h);
            double x = ((node.y + 180) / 360 * w);
            // create a button representing the country
            Button button = new Button(node.name);
            // to display city names and coordinates
            Tooltip tooltip = new Tooltip(node.name + " (" + node.x + ", " + node.y + ")");
            Tooltip.install(button, tooltip);
            button.setOnMouseClicked(e -> {
                if (box1.getValue() == null) {
                    // set the source box value to the clicked city
                    box1.setValue(node.name);
                } else if (box2.getValue() == null) {
                    // set the destination box value to the clicked city
                    box2.setValue(node.name);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Source And Destination Are Full");
                    alert.showAndWait();
                }
            });
            button.setStyle(
                    "-fx-background-radius: 10em; " +
                            "-fx-min-width: 5px; " +
                            "-fx-min-height: 5px; " +
                            "-fx-max-width: 5px; " +
                            "-fx-max-height: 5px;" +
                            "-fx-background-color: red"
            );
            button.setLayoutX(x);
            button.setLayoutY(y);
            pane.getChildren().addAll(button);
        }
    }

    //function to print both the source and target for each step of the shortest path
    public void printShortestPath(String[] shortestPathNodes, TextArea text) {
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 0; i < shortestPathNodes.length - 1; i++) {
            if (i != 0) {
                pathBuilder.append(",\n");
            }
            pathBuilder.append(shortestPathNodes[i]).append(", ").append(shortestPathNodes[i + 1]);
        }
        text.setText(pathBuilder.toString());
    }
}
