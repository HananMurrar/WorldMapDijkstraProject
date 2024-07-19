package com.example.project3;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class MapViewer {
    private ImageView mapView; // to display the map image
    private Canvas mapCanvas; // to draw paths on the map
    private GraphicsContext graphicsContext; // to draw on the canvas
    private Dijkstra dijkstraAlgorithm; // reference to the dijkstra algorithm class

    public MapViewer(String mapImageFile, Dijkstra dijkstraAlgorithm) {
        Image mapImage = new Image(mapImageFile);
        mapView = new ImageView(mapImage);
        mapView.setFitWidth(999);
        mapView.setFitHeight(650);
        mapCanvas = new Canvas(999, 650);
        graphicsContext = mapCanvas.getGraphicsContext2D();
        this.dijkstraAlgorithm = dijkstraAlgorithm;
    }

    public void drawShortestPath(String[] shortestPathNodes) {
        graphicsContext.setStroke(Color.ORANGE);
        graphicsContext.setLineWidth(1);
        // creates arrays to hold the x and y coordinates of each node in the path
        double[] xCoordinates = new double[shortestPathNodes.length];
        double[] yCoordinates = new double[shortestPathNodes.length];
        for (int i = 0; i < shortestPathNodes.length; i++) {
            Node node = dijkstraAlgorithm.getNodesMap().get(shortestPathNodes[i]);
            // normalizes nodes longitude to a value between 0 and 1 by adding 180 and dividing by 360 (since longitude ranges from -180 to 180 degrees)
            xCoordinates[i] = (node.y + 180) / 360 * mapCanvas.getWidth();
            // normalizes the nodes latitude to a value between 0 and 1 by adding 90 and dividing by 180 (since latitude ranges from -90 to 90 degrees)
            yCoordinates[i] = mapCanvas.getHeight() - (node.x + 90) / 180 * mapCanvas.getHeight();
        }
        graphicsContext.strokePolyline(xCoordinates, yCoordinates, shortestPathNodes.length);
    }

    public Canvas getMapCanvas() {
        return mapCanvas;
    }

    public Pane getPane() {
        Pane pane = new Pane();
        pane.getChildren().addAll(mapView, mapCanvas);
        return pane;
    }
}
