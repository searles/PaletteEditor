package at.searles.multiscrollview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class InnerPaneView(context: Context, attrs: AttributeSet): View(context, attrs) {
    var innerPane: InnerPane? = null
        set(value) {
            field = value
            requestLayout()
        }

    val intendedWidth: Int
        get() = innerPane?.width ?: 0

    val intendedHeight: Int
        get() = innerPane?.height ?: 0

    var visibleX0: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var visibleY0: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    lateinit var listener: OnIntendedSizeChangedObserver

    fun notifyIntendedSizeChanged() {
        listener.intendedSizeChanged()
    }

    fun onScrollTo(e: MotionEvent): ScrollDirection {
        return innerPane?.onScrollTo(e, visibleX0, visibleY0) ?: ScrollDirection.NoScroll
    }

    fun onTapUp(e: MotionEvent): Boolean {
        return innerPane?.onTapUp(e, visibleX0, visibleY0) ?: false
    }

    fun onClick(e: MotionEvent): Boolean {
        return innerPane?.onClick(e, visibleX0, visibleY0) ?: false
    }

    fun onDoubleClick(e: MotionEvent): Boolean {
        return innerPane?.onDoubleClick(e, visibleX0, visibleY0) ?: false
    }

    fun onTapDown(e: MotionEvent): Boolean {
        return innerPane?.onTapDown(e, visibleX0, visibleY0) ?: false
    }

    fun onLongPress(e: MotionEvent) {
        innerPane?.onLongPress(e, visibleX0, visibleY0)
    }

    override fun onDraw(canvas: Canvas) {
        innerPane?.onDraw(canvas, visibleX0, visibleY0, width, height)
    }

    interface OnIntendedSizeChangedObserver {
        fun intendedSizeChanged()
    }
}