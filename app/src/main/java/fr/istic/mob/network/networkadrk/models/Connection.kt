package fr.istic.mob.network.networkadrk.models

import android.graphics.Color

data class Connection (
    val id_connexion: String,
    val color_connexion: Color,
    val node1: Node,
    val node2: Node?,
    val position1: Position,
    val position2: Position
)