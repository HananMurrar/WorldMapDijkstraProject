package com.example.project3;

public class Edge {
    Node from; // starting node of the edge
    Node to;   // ending node of the edge
    double weight; // weight of the edge

    public Edge(Node from, Node to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }
}
