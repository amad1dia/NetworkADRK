package fr.istic.mob.network.networkadrk.ui

import android.content.Context
import android.graphics.*
import android.text.InputType
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import fr.istic.mob.network.networkadrk.R
import fr.istic.mob.network.networkadrk.models.*
import java.io.*


// Stroke width for the the paint.
private const val STROKE_WIDTH = 12f

class CustomView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {


    private val TAG = "CustomView"

    // Holds the path you are currently drawing.
    private var path: Path = Path()
    private var xAbs: Float = 0f
    private var yAbs: Float = 0f
    private val rectWidth = resources.getInteger(R.integer.rect_width)
    private val rectHeight = resources.getInteger(R.integer.rect_height)
    private val labelSize = resources.getInteger(R.integer.text_size)
    private val connectionStrokeWidth = resources.getInteger(R.integer.stroke_width)

    companion object {
        var graph = Graph()
    }

    private val drawColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var firstObject: Node? = null
    private var secondObject: Node? = null
    private var objectToMove: Node? = null
    private var colors: Map<Int, Int> = mapOf(
        0 to Color.RED,
        1 to Color.GREEN,
        2 to Color.BLUE,
        3 to Color.rgb(255, 165, 0), //orange
        4 to Color.CYAN,
        5 to Color.MAGENTA,
        6 to Color.BLACK
    )

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    private val pBg = Paint().apply {
        color = Color.BLACK
        strokeWidth = connectionStrokeWidth.toFloat()
        style = Paint.Style.STROKE
    }

    private val objectPaint = Paint().apply {
        color = drawColor
        textSize = labelSize.toFloat()
        style = Paint.Style.FILL
    }

    private val connectionPaint = Paint().apply {
        color = drawColor
        textSize = labelSize.toFloat()
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
    }


    fun drawRect(objectName: String) {
        val objectR = RectObject(xAbs, yAbs, xAbs + rectWidth, yAbs + rectHeight)
        graph.nodes.add(
            Node(
                label = objectName,
                objet = objectR,
                position = Position(xAbs, yAbs)
            )
        )
    }

    private fun getRectFromObject(objectR: RectObject) =
        RectF(objectR.left, objectR.top, objectR.right, objectR.bottom)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        graph.nodes.forEach {
            val rect = getRectFromObject(it.objet)
            paint.color = it.color
            objectPaint.color = it.color

            canvas.drawRect(rect, paint)
            canvas.drawText(it.label, it.objet.right + 10, rect.centerY(), objectPaint)
        }

        graph.connections.forEach {
            val rect1 = getRectFromObject(it.node1?.objet!!)
            val rect2 = getRectFromObject(it.node2?.objet!!)
            val connectionCenterX = (rect1.centerX() + rect2.centerX()) / 2
            val connectionCenterY = (rect1.centerY() + rect2.centerY()) / 2

            canvas.drawLine(
                rect1.centerX(),
                rect1.centerY(),
                rect2.centerX(),
                rect2.centerY(),
                pBg
            )

            canvas.drawText(
                it.label,
                connectionCenterX - (it.label.length / 2),
                connectionCenterY - (it.label.length / 2),
                connectionPaint
            )
        }

        //Show line while drawing
        canvas.drawPath(path, pBg)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        xAbs = event.x
        yAbs = event.y

        invalidate()
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun touchStart(move: Boolean) {
        if (move) {
            objectToMove = getNodeByPosition(motionTouchEventX, motionTouchEventY)
        } else {
            path.reset()
            firstObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)
            path.moveTo(motionTouchEventX, motionTouchEventY)
            firstObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
        }
    }

    private fun touchMove(move: Boolean) {
        if (!move) {
            path.lineTo(motionTouchEventX, motionTouchEventY)
        } else {
            moveObject()
        }

        invalidate()
    }

    private fun touchUp(move: Boolean) {
        if (move) {
            moveObject()
        } else {
            secondObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)
            path.lineTo(motionTouchEventX, motionTouchEventY)
            if (firstObject != null && secondObject != null && firstObject != secondObject) {

                val rect1 = getRectFromObject(firstObject!!.objet)
                val rect2 = getRectFromObject(secondObject!!.objet)
                extraCanvas.drawLine(
                    rect1.centerX(),
                    rect1.centerY(),
                    rect2.centerX(),
                    rect2.centerY(),
                    pBg
                )

                showInputDialog()
            }
        }
        path.reset()
    }

    private fun moveObject() {
        objectToMove?.objet?.left = motionTouchEventX
        objectToMove?.objet?.top = motionTouchEventY
        objectToMove?.objet?.right = motionTouchEventX + rectWidth
        objectToMove?.objet?.bottom = motionTouchEventY + rectHeight
        objectToMove?.position?.x = motionTouchEventX
        objectToMove?.position?.y = motionTouchEventY
    }

    fun connectOrMoveObject(event: MotionEvent, move: Boolean = false) {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(move)
            MotionEvent.ACTION_UP -> touchUp(move)
            MotionEvent.ACTION_MOVE -> touchMove(move)
        }
    }

    fun updateObject(fragmentManager: FragmentManager, event: MotionEvent) {
        motionTouchEventX = event.x
        motionTouchEventY = event.y
        val currentObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)
        if (currentObject != null)
            showPopup(fragmentManager)
    }

    private fun showPopup(fragmentManager: FragmentManager) {
        val addPhotoBottomDialogFragment = ActionBottomDialogFragment.newInstance()
        addPhotoBottomDialogFragment.show(
            fragmentManager,
            ActionBottomDialogFragment.TAG
        )
    }

    fun reinitializeNetwork() {
        if (graph.nodes.isNotEmpty() || graph.connections.isNotEmpty())
            showConfirmDialog()
        else
            Toast.makeText(
                context,
                context.getString(R.string.toast_empty_network),
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun getNodeByPosition(x: Float, y: Float): Node? {
        graph.nodes.forEach {
            val rect = getRectFromObject(it.objet)

            if (rect.contains(x, y))
                return it
        }

        return null
    }

    private fun showConfirmDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setMessage(R.string.network_reinitialization)
            .setPositiveButton(R.string.yes_button) { dialog, _ ->
                graph.nodes.clear()
                graph.connections.clear()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.no_button) { dialog, _ ->
                dialog.cancel()
            }

        builder.create()
        builder.show()
    }

    private fun showInputDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setMessage(R.string.connection_label)
            .setPositiveButton(R.string.ok_button) { dialog, _ ->
                val connectionLabel = input.text.toString()

                graph.connections.add(
                    Connection(
                        label = connectionLabel,
                        Color.GREEN,
                        firstObject,
                        secondObject
                    )
                )
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }

        builder.create()
        builder.show()
    }

    private fun saveObject(graphName: String) {
        try {
            val fos = context.openFileOutput("$graphName.tmp", Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(graph)
            fos.close()
        } catch (ex: FileNotFoundException) {
            ex.printStackTrace()
        }

        Toast.makeText(context, context.getString(R.string.graph_created), Toast.LENGTH_SHORT)
            .show()
    }

    private fun showSavedNetwork(graphName: String) {
        var inputStream: InputStream? = null
        try {
            inputStream = context.openFileInput("$graphName.tmp")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        if (inputStream == null) {
            Toast.makeText(context, "Ce graphe n'existe pas", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val ois = ObjectInputStream(inputStream)
            try {
                graph = ois.readObject() as Graph
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: StreamCorruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun showOrSaveGraphDialog(action: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setMessage(R.string.graph_name)
            .setPositiveButton(R.string.ok_button) { dialog, _ ->
                val graphName = input.text.toString()
                if (graphName.isEmpty()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.graph_name),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@setPositiveButton
                }
                if (action == "save")
                    saveObject(graphName)
                else if (action == "show")
                    showSavedNetwork(graphName)

                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }

        builder.create()
        builder.show()
    }

    fun deleteObject() {
        val currentObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)
        if (currentObject != null) {
            graph.nodes.remove(currentObject)
            removeLinkedConnections(currentObject)
        }
    }

    private fun removeLinkedConnections(currentObject: Node) {
        val connections: MutableList<Connection> = mutableListOf()
        graph.connections.forEach {
            if (it.node1 == currentObject || it.node2 == currentObject)
                connections.add(it)
        }

        //La suppression de la connexion dans le premier foreach créé l'exception
        // java.util.ConcurrentModificationException
        connections.forEach {
            graph.connections.remove(it)
        }
    }

    fun changeLabel() {
        val currentObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)

        if (currentObject != null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)

            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.setText(currentObject.label)
            builder.setView(input)

            builder.setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.ok_button) { dialog, _ ->
                    val objectLabel = input.text.toString()
                    if (objectLabel.isEmpty()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.graph_name),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return@setPositiveButton
                    }

                    currentObject.label = objectLabel

                    dialog.dismiss()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }

            builder.create()
            builder.show()
        }
    }

    fun changeObjectColor() {
        val currentObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)

        if (currentObject != null) {
            val builder = AlertDialog.Builder(context)

            builder.setTitle(R.string.pick_color)
                .setItems(
                    R.array.colors
                ) { dialog, which ->
                    val color = colors[which]
                    if (color != null)
                        currentObject.color = color

                    dialog.dismiss()
                }
            builder.create()
            builder.show()
        }
    }

}
