package fr.istic.mob.network.networkadrk.models

import android.graphics.Color
import android.graphics.RectF
import java.io.Serializable

data class Node (
    var label: String,
    var color: Int = Color.BLUE,
    val position: Position,
    val objet: RectObject
): Serializable