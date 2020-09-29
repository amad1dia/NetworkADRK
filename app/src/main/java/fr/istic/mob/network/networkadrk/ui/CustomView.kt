package fr.istic.mob.network.networkadrk.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import fr.istic.mob.network.networkadrk.R
import fr.istic.mob.network.networkadrk.models.Connection
import fr.istic.mob.network.networkadrk.models.Node
import fr.istic.mob.network.networkadrk.models.Position


// Stroke width for the the paint.
private const val STROKE_WIDTH = 12f

class CustomView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {


    private val TAG = "CustomView"

    // Holds the path you are currently drawing.
    private var path: Path = Path()
    private lateinit var frame: Rect
    private var xAbs: Float = 0f
    private var yAbs: Float = 0f

    companion object {
        var nodes: MutableList<Node> = mutableListOf()
    }

    private val drawColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
    private val backgroundColor = ResourcesCompat.getColor(
        context.resources,
        R.color.colorPrimaryDark,
        null
    )
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    private var firstObject: Node? = null
    private var secondObject: Node? = null

    // Set up the paint with which to draw.
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    private val pBg = Paint().apply {
        color = Color.GREEN
        strokeWidth = 12f
        style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
    }

    public fun drawRect() {
        val rect = RectF(xAbs, yAbs, xAbs + 40, yAbs + 40)

        nodes.add(
            Node(
                label = "Empty",
                objet = rect,
                position = Position(xAbs.toInt(), yAbs.toInt())
            )
        )
        //TODO show input for label
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        nodes.forEach {
            canvas.drawRect(it.objet, paint)
        }

        canvas.drawBitmap(extraBitmap, 0f, 0f, pBg);
        canvas.drawPath(path, pBg);
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent: ")
        xAbs = event.x
        yAbs = event.y

        invalidate()
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun touchStart() {
        Log.d(TAG, "touchStart: ")
        path.reset()
        firstObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)
        path.moveTo(motionTouchEventX, motionTouchEventY)
        firstObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        path.lineTo(motionTouchEventX, motionTouchEventY)
        invalidate()
    }

    private fun touchUp() {
        secondObject = getNodeByPosition(motionTouchEventX, motionTouchEventY)
        path.lineTo(motionTouchEventX, motionTouchEventY)
        if (firstObject != null && secondObject != null)
            extraCanvas.drawPath(path, pBg)

        path.reset()
    }

    fun connectObject(event: MotionEvent) {

        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_UP -> touchUp()
            MotionEvent.ACTION_MOVE -> touchMove()
        }
    }

    private fun getNodeByPosition(x: Float, y: Float): Node? {
        nodes.forEach {
            if (it.objet.contains(x, y))
                return it
        }

        return null
    }
}