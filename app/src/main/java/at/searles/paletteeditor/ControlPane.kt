package at.searles.paletteeditor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection
import at.searles.paletteeditor.colors.Colors
import kotlin.math.abs
import kotlin.math.hypot

abstract class ControlPane(private val rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): InnerPane {
    val iconSize
        get() = paletteEditorPane.iconSize

    val spacing
        get() = paletteEditorPane.spacing

    val model
        get() = paletteEditorPane.model!!

    val buttonPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 24f
    }

    protected abstract fun plusClicked()
    protected abstract fun minusClicked()

    override fun onClick(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        // is it +?
        if(2f * hypot((centerPlusX(visibleX0) - e.x).toDouble(), (centerPlusY(visibleY0) - e.y).toDouble()) < iconSize) {
            plusClicked()
            rootView.notifyIntendedSizeChanged() // FIXME move rather to a model listener.
            return true
        }

        // is it -?
        if(2f * hypot(centerMinusX(visibleX0) - e.x, centerMinusY(visibleY0) - e.y) < iconSize) {
            minusClicked()
            rootView.notifyIntendedSizeChanged() // FIXME move rather to a model listener
            return true
        }

        return false
    }

    override fun onScrollTo(e: MotionEvent, visibleX0: Int, visibleY0: Int): ScrollDirection {
        return ScrollDirection.NoScroll
    }

    override fun onLongPress(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        return false
    }

    override fun onTapDown(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        return false
    }

    override fun onTapUp(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        return false
    }

    override fun onDoubleClick(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        return false
    }

    abstract fun centerPlusX(visibleX0: Int): Float
    abstract fun centerPlusY(visibleY0: Int): Float

    abstract fun centerMinusX(visibleX0: Int): Float
    abstract fun centerMinusY(visibleY0: Int): Float

    private fun colorCoordinate(position: Int, visibleOffset: Int): Float {
        return (position * (iconSize + spacing) - visibleOffset).toFloat()
    }

    private val fillColorPaint = Paint()

    fun drawColorRange(canvas: Canvas, cols: IntRange, rows: IntRange, visibleX0: Int, visibleY0: Int) {
        for(row in rows) {
            for(col in cols) {
                val x0 = colorCoordinate(col, visibleX0)
                val y0 = colorCoordinate(row, visibleY0)
                val color = model.colorAt(col, row)

                fillColorPaint.color = Colors.transparent(paleFactor, color)

                canvas.drawRect(x0, y0, x0 + iconSize, y0 + iconSize, fillColorPaint)
            }
        }
    }

    companion object {
        const val paleFactor = 0.26f
    }
}