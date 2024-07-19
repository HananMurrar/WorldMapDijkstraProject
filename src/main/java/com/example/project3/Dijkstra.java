package com.example.project3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Dijkstra {
    private Map<String, Node> nodesMap = new HashMap<>(); // map to store nodes by their names
    private double shortestPathWeight = 0; // weight of the shortest path
    private String[] shortestPathNodes; // nodes in the shortest path

    private double calculateWeight(double latitude1, double longitude1, double latitude2, double longitude2) {
        // convert degrees to radians
        double deltaLatitude = Math.toRadians(latitude2 - latitude1);
        double deltaLongitude = Math.toRadians(longitude2 - longitude1);
        double radianLatitude1 = Math.toRadians(latitude1);
        double radianLatitude2 = Math.toRadians(latitude2);
        // haversine formula to calculate distance
        double a = Math.pow(Math.sin(deltaLatitude / 2), 2) +
                Math.pow(Math.sin(deltaLongitude / 2), 2) * Math.cos(radianLatitude1) * Math.cos(radianLatitude2);
        double c = 2 * Math.asin(Math.sqrt(a));
        // earth radius in kilometers
        double earthRadiusKm = 6371;
        return earthRadiusKm * c;
    }

    public void readData(File graphFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(graphFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            String[] parts = line.split(":");
            if (parts.length == 3) {
                // reading node
                String name = parts[0];
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                Node node = new Node(name, x, y);
                nodesMap.put(name, node);
            } else if (parts.length == 2) {
                // reading edge
                String from = parts[0];
                String to = parts[1];
                Node fromNode = nodesMap.get(from);
                Node toNode = nodesMap.get(to);
                if (fromNode != null && toNode != null) {
                    double weight = calculateWeight(fromNode.x, fromNode.y, toNode.x, toNode.y);
                    fromNode.addEdge(toNode, weight);
                }
            }
        }
        scanner.close();
    }

    public void calculateShortestPath(String source, String destination) {
        // check if the source and destination nodes exist in the graph
        if (!nodesMap.containsKey(source) || !nodesMap.containsKey(destination)) {
            System.out.println("source or destination node not found in the graph");
            return;
        }
        // get the source and destination nodes from the nodes map
        Node sourceNode = nodesMap.get(source);
        Node destinationNode = nodesMap.get(destination);
        // initialize data structures
        Map<Node, Double> distances = new HashMap<>(); // map to store distances from source to each node
        Map<Node, Node> predecessors = new HashMap<>(); // map to store predecessors for each node
        Set<Node> visited = new HashSet<>(); // set to keep track of visited nodes
        // use a priority queue to efficiently extract the node with the smallest distance
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(node -> distances.get(node)));
        // initialize distances for all nodes to infinity
        for (Node node : nodesMap.values()) {
            distances.put(node, Double.MAX_VALUE);
        }
        // set distance of source node to 0 and add it to the priority queue
        distances.put(sourceNode, 0.0);
        priorityQueue.offer(sourceNode);
        // dijkstra algorithm
        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll(); // extract node with the smallest distance
            visited.add(currentNode);
            // stop when destination node is reached
            if (currentNode == destinationNode) break;
            // update distances to adjacent nodes
            for (Edge edge : currentNode.edges) {
                if (!visited.contains(edge.to)) {
                    double newDistance = distances.get(currentNode) + edge.weight;
                    if (newDistance < distances.get(edge.to)) {
                        distances.put(edge.to, newDistance);
                        predecessors.put(edge.to, currentNode);
                        // update priority queue with the new distance
                        priorityQueue.offer(edge.to);
                    }
                }
            }
        }
        // reconstruct the shortest path
        List<Node> path = new ArrayList<>();
        Node currentNode = destinationNode;
        while (currentNode != null) {
            path.add(currentNode);
            currentNode = predecessors.get(currentNode);
        }
        Collections.reverse(path);
        // no path found
        if (path.isEmpty() || path.get(0) != sourceNode || path.get(path.size() - 1) != destinationNode) {
            shortestPathNodes = null;
            shortestPathWeight = Double.POSITIVE_INFINITY;
            return;
        }
        // set short path nodes and weight
        shortestPathNodes = path.stream().map(node -> node.name).toArray(size -> new String[size]);
        shortestPathWeight = distances.get(destinationNode);
    }

    public Map<String, Node> getNodesMap() {
        return nodesMap;
    }

    public double getShortestPathWeight() {
        return shortestPathWeight;
    }

    public String[] getShortestPathNodes() {
        return shortestPathNodes;
    }
}
