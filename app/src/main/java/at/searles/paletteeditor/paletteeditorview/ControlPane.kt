package at.searles.paletteeditor.paletteeditorview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection
import kotlin.math.hypot

abstract class ControlPane(private val rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): InnerPane {
    val iconSize
        get() = paletteEditorPane.iconSize

    val spacing
        get() = paletteEditorPane.spacing

    val model
        get() = paletteEditorPane.model

    val buttonPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 24f
    }

    protected abstract fun plusClicked()
    protected abstract fun minusClicked()

    override fun onClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        // is it +?
        if(2f * hypot((centerPlusX(visibleX0) - e.x).toDouble(), (centerPlusY(visibleY0) - e.y).toDouble()) < iconSize) {
            plusClicked()
            return true
        }

        // is it -?
        if(2f * hypot(centerMinusX(visibleX0) - e.x, centerMinusY(visibleY0) - e.y) < iconSize) {
            minusClicked()
            return true
        }

        return false
    }

    override fun onScrollTo(e: MotionEvent, visibleX0: Float, visibleY0: Float): ScrollDirection {
        return ScrollDirection.NoScroll
    }

    override fun onLongPress(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun onTapDown(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun onTapUp(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun onDoubleClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun cancelCurrentAction() {
        // Nothing to do.
    }

    abstract fun centerPlusX(visibleX0: Float): Float
    abstract fun centerPlusY(visibleY0: Float): Float

    abstract fun centerMinusX(visibleX0: Float): Float
    abstract fun centerMinusY(visibleY0: Float): Float

    fun drawColorRange(canvas: Canvas, cols: IntRange, rows: IntRange, visibleX0: Float, visibleY0: Float) {
        for(row in rows) {
            for(col in cols) {
                // FIXME transparency
                paletteEditorPane.drawColor(canvas, col, row, visibleX0, visibleY0)
            }
        }
    }
}