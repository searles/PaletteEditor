package at.searles.multiscrollview

import android.graphics.Canvas
import android.view.MotionEvent

interface InnerPane {
    val width: Int
    val height: Int

    fun onClick(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean
    fun onScrollTo(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean
    fun onLongPress(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean
    fun onTapUp(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean
    fun onDoubleClick(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean

    fun onDraw(canvas: Canvas, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int)
}