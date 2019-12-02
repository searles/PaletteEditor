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

class HorizontalControlPane(rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): ControlPane(rootView, paletteEditorPane) {
    override val width: Int
        get() = paletteEditorPane.width - paletteEditorPane.spacing
    override val height: Int
        get() = paletteEditorPane.iconSize

    override fun plusClicked() {
        model.rowCount++
    }

    override fun minusClicked() {
        model.columnCount--
    }

    override fun centerPlusX(visibleX0: Int) = 0f - visibleX0 - paletteEditorPane.iconSize / 2f
    override fun centerPlusY(visibleY0: Int) = height / 2f - visibleY0

    override fun centerMinusX(visibleX0: Int) = width - visibleX0 - paletteEditorPane.iconSize / 2f
    override fun centerMinusY(visibleY0: Int) = height / 2f - visibleY0 - spacing

    override fun onDraw(canvas: Canvas, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int) {
        drawColorRange(canvas, 0 until model.columnCount, 0 .. 0, visibleX0, visibleY0)

        canvas.drawCircle(centerPlusX(visibleX0), centerPlusY(visibleY0), iconSize / 2f, buttonPaint)

        if(model.columnCount > 1) {
            canvas.drawCircle(centerMinusX(visibleX0), centerMinusY(visibleY0), iconSize / 2f, buttonPaint)
        }
    }
}
