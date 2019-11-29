package at.searles.multiscrollview

import android.graphics.Canvas
import android.view.MotionEvent

interface InnerPane {
    val width: Int
    val height: Int

    open fun isInside(x: Float, y: Float, visibleX0: Int, visibleY0: Int): Boolean {
        return -visibleX0 <= x && x < -visibleX0 + width && -visibleY0 <= y && y < -visibleY0 + height
    }

    fun onClick(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean
    fun onScrollTo(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean
    fun onLongPress(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean
    fun onTapUp(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean
    fun onDoubleClick(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean

    fun onDraw(canvas: Canvas, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int)
}