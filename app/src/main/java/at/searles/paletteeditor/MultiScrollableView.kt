package at.searles.paletteeditor

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

interface MultiScrollableDrawableCanvas {

    val intendedWidth: Int
    val intendedHeight: Int

    fun setOffset(left: Int, top: Int)

    fun onClick(evt: MotionEvent): Boolean
    fun onDoubleClick(evt: MotionEvent): Boolean
    fun onLongPress(evt: MotionEvent): Boolean
    fun onScrollTo(evt: MotionEvent): Boolean
    fun onTapUp(event: MotionEvent): Boolean

    fun onDraw(canvas: Canvas)
}