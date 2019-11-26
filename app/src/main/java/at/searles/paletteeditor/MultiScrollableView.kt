package at.searles.paletteeditor

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

interface MultiScrollableDrawableCanvas {

    val intendedWidth: Float
    val intendedHeight: Float

    fun setOffset(left: Float, top: Float)

    fun onSingleTapUp(evt: MotionEvent): Boolean
    fun onDoubleTap(evt: MotionEvent): Boolean
    fun onLongPress(evt: MotionEvent): Boolean
    fun onMoveTo(evt: MotionEvent): Boolean
    fun onTapUp(event: MotionEvent): Boolean

    fun onDraw(canvas: Canvas)
}