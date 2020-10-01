package fr.istic.mob.network.networkadrk.models;

data class Graph (
    val nodes: MutableSet<Node> = mutableSetOf(),
    val connexions: MutableSet<Connection> = mutableSetOf()
)
