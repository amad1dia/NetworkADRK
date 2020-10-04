package fr.istic.mob.network.networkadrk.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import fr.istic.mob.network.networkadrk.R
import fr.istic.mob.network.networkadrk.models.Connection
import fr.istic.mob.network.networkadrk.models.Node
import fr.istic.mob.network.networkadrk.models.Position


// Stroke width for the the paint.
private const val STROKE_WIDTH = 12f

@Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
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
        var nodes: MutableList<Node> = mutableListOf()
        var connections: MutableList<Connection> = mutableListOf()
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

    private val textPaint = Paint().apply {
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
        val rect = RectF(xAbs, yAbs, xAbs + rectWidth, yAbs + rectHeight)

        nodes.add(
            Node(
                label = objectName,
                objet = rect,
                position = Position(xAbs, yAbs)
            )
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        nodes.forEach {
            canvas.drawRect(it.objet, paint)
            canvas.drawText(it.label, it.objet.right + 10, it.objet.centerY(), textPaint)
        }

        connections.forEach {
            canvas.drawLine(
                it.node1?.objet!!.centerX(),
                it.node1.objet!!.centerY(),
                it.node2?.objet!!.centerX(),
                it.node2.objet!!.centerY(),
                pBg
            )
        }

//        canvas.drawBitmap(extraBitmap, 0f, 0f, pBg)
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
            if (firstObject != null && secondObject != null) {
                extraCanvas.drawLine(
                    firstObject!!.objet.centerX(),
                    firstObject!!.objet.centerY(),
                    secondObject!!.objet.centerX(),
                    secondObject!!.objet.centerY(),
                    pBg
                )

                connections.add(
                    Connection(
                        Color.GREEN,
                        firstObject,
                        secondObject
                    )
                )
            }
        }
        path.reset()
    }

    private fun moveObject() {
        objectToMove?.objet?.left = motionTouchEventX
        objectToMove?.objet?.top = motionTouchEventY
        objectToMove?.objet?.right = motionTouchEventX + rectWidth
        objectToMove?.objet?.bottom = motionTouchEventY + rectHeight

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


    fun reinitializeNetwork() {
        if (nodes.isNotEmpty() || connections.isNotEmpty())
            showConfirmDialog()
        else
            Toast.makeText(context, context.getString(R.string.toast_empty_network), Toast.LENGTH_SHORT).show()
    }

    private fun getNodeByPosition(x: Float, y: Float): Node? {
        nodes.forEach {
            if (it.objet.contains(x, y))
                return it
        }

        return null
    }

    private fun showConfirmDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setMessage(R.string.network_reinitialization)
            .setPositiveButton(R.string.yes_button) { dialog, _ ->
                nodes.clear()
                connections.clear()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.no_button) { dialog, _ ->
                dialog.cancel()
            }

        builder.create()
        builder.show()
    }
}