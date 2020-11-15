package fr.istic.mob.network.networkadrk.models

import java.io.Serializable

data class Connection(
    val label: String,
    val color_connexion: Int,
    val node1: Node?,
    val node2: Node?,
): Serializable