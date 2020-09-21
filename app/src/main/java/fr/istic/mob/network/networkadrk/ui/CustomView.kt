package fr.istic.mob.network.networkadrk.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import fr.istic.mob.network.networkadrk.R


// Stroke width for the the paint.
private const val STROKE_WIDTH = 12f
class CustomView(context: Context): View(context) {
    private val TAG = "CustomView"
    // Holds the path you are currently drawing.
    private var path = Path()

    private val drawColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
    private val backgroundColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimaryDark, null)
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        super.setOnLongClickListener(l)
        Log.d(TAG, "setOnLongClickListener: ")
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}