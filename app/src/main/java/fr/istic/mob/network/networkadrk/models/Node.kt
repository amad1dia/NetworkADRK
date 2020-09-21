package fr.istic.mob.network.networkadrk.models

import android.graphics.Color
import android.graphics.RectF

data class Node (
    val label: String,
    val color: Color,
    val position: Position,
    val objet: RectF
)