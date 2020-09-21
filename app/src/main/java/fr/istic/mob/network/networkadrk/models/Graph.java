package fr.istic.mob.network.networkadrk.models;

import java.util.List;

public class Graph {
    private List<Object> nodes;
    private List<Object> connections;

    public List<Object> getNodes() {
        return nodes;
    }

    public void setNodes(List<Object> nodes) {
        this.nodes = nodes;
    }

    public List<Object> getConnections() {
        return connections;
    }

    public void setConnections(List<Object> connections) {
        this.connections = connections;
    }
}
