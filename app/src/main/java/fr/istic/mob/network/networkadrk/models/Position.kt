package fr.istic.mob.network.networkadrk.models

import android.graphics.Color

data class Position(val x: Int?, val y: Int?, val color: Color)

class Graph {
    var nodes: Set<Any>? = null
    var connections: Set<Any>? = null
}