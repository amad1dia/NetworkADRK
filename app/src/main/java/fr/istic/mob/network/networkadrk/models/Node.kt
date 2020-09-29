package fr.istic.mob.network.networkadrk.models

import android.graphics.*
import android.graphics.drawable.Drawable

data class Node (
    val label: String,
    val color: Int? = Color.BLUE,
    val position: Position,
    val objet: RectF
)