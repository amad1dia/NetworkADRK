package fr.istic.mob.network.networkadrk.models

import android.graphics.RectF
import java.io.Serializable

data class RectObject(
    var left: Float,
    var top: Float,
    var right: Float,
    var bottom: Float
) : Serializable