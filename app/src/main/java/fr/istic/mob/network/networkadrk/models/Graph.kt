package fr.istic.mob.network.networkadrk.models;

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class Graph (
    val nodes: MutableList<Node> = mutableListOf(),
    val connections: MutableList<Connection> = mutableListOf()
) : Serializable
