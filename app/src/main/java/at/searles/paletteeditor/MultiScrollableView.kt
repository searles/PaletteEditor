package at.searles.paletteeditor

import android.graphics.Canvas
import android.view.MotionEvent

interface MultiScrollableDrawableCanvas {

    val realWidth: Float
    val realHeight: Float

    val leftOffset: Float
    val topOffset: Float

    fun rx(vx: Float): Float {
        return vx + leftOffset
    }

    fun ry(vy: Float): Float {
        return vy + topOffset
    }

    fun vx(rx: Float): Float {
        return rx - leftOffset
    }

    fun vy(ry: Float): Float {
        return ry - topOffset
    }

    fun setOffset(left: Float, top: Float)

    /**
     * notifies view of a single click event
     */
    fun onDoubleClick(e: MotionEvent): Boolean

    fun onLongPress(e: MotionEvent): Boolean
    fun onSingleTapUp(e: MotionEvent): Boolean
    fun onTapUp(e: MotionEvent): Boolean
    fun onScrollTo(e: MotionEvent): Boolean

    fun onDraw(canvas: Canvas)
}