package at.searles.paletteeditor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.paletteeditor.colors.Colors
import kotlin.math.abs

// Name: Viewport.

class HorizontalControlPane(val rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): InnerPane {
    override val width: Int
        get() = paletteEditorPane.width - paletteEditorPane.spacing
    override val height: Int
        get() = paletteEditorPane.iconSize

    private val iconSize
        get() = paletteEditorPane.iconSize

    private val spacing
        get() = paletteEditorPane.spacing

    private val model
        get() = paletteEditorPane.model!!

    private val buttonPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 24f
    }

    override fun onClick(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        // is it +?
        if(abs(centerPlusX(visibleX0) - e.x) < iconSize / 2f && abs(centerPlusY(visibleY0) - e.y) < iconSize / 2f) {
            model.rowCount++
            // FIXME move to controller
            rootView.requestLayout()
            rootView.invalidate()
            return true
        }

        // is it -?
        if(abs(centerMinusX(visibleX0) - e.x) < iconSize / 2f && abs(centerMinusY(visibleY0) - e.y) < iconSize / 2f) {
            model.columnCount--
            // FIXME move to controller
            rootView.requestLayout()
            rootView.invalidate()
            return true
        }

        return false
    }

    override fun onScrollTo(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        return false
    }

    override fun onLongPress(
        e: MotionEvent,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ): Boolean {
        return false
    }

    override fun onTapUp(
        e: MotionEvent,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ): Boolean {
        return false
    }

    override fun onDoubleClick(
        e: MotionEvent,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ): Boolean {
        return false
    }

    fun centerPlusX(visibleX0: Int) = 0f - visibleX0 - paletteEditorPane.iconSize / 2f
    fun centerPlusY(visibleY0: Int) = height / 2f - visibleY0

    fun centerMinusX(visibleX0: Int) = width - visibleX0 - paletteEditorPane.iconSize / 2f
    fun centerMinusY(visibleY0: Int) = height / 2f - visibleY0

    val fillColorPaint = Paint()

    override fun onDraw(
        canvas: Canvas,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ) {
        for(col  in 0 until model.columnCount) {
            val x0 = (col * (iconSize + spacing) - visibleX0).toFloat()
            val y0 = - visibleY0.toFloat()

            val color = model.colorAt(col, 0)

            fillColorPaint.color = Colors.transparent(paleFactor, color)

            canvas.drawRect(x0, y0, x0 + iconSize, y0 + iconSize, fillColorPaint)
        }

        canvas.drawCircle(centerPlusX(visibleX0), centerPlusY(visibleY0), iconSize / 2f, buttonPaint)

        canvas.drawCircle(centerMinusX(visibleX0), centerMinusY(visibleY0) - spacing, iconSize / 2f, buttonPaint)
    }

    companion object {
        val paleFactor = 0.26f
    }
}