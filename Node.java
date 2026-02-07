package com.example.project3;

import java.util.ArrayList;
import java.util.List;

public class Node {
    String name; // name of the node
    double x; // x coordinate (latitude)
    double y; // y coordinate (longitude)
    List<Edge> edges; // list of edges connected to this node

    public Node(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        edges = new ArrayList<>();
    }

    // function to add an edge from this node to another node with a specified weight
    public void addEdge(Node to, double weight) {
        edges.add(new Edge(this, to, weight));
    }
}

