package at.searles.paletteeditor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection

// Name: Viewport.

class VerticalControlPane(rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): ControlPane(rootView, paletteEditorPane) {
    override val width: Int
        get() = paletteEditorPane.iconSize
    override val height: Int
        get() = paletteEditorPane.height - paletteEditorPane.spacing

    override fun plusClicked() {
        model.columnCount++
    }

    override fun minusClicked() {
        model.rowCount--
    }

    override fun centerPlusX(visibleX0: Int) = width / 2f - visibleX0
    override fun centerPlusY(visibleY0: Int) = 0f - visibleY0 - paletteEditorPane.iconSize / 2f

    override fun centerMinusX(visibleX0: Int) = width / 2f - visibleX0 - spacing
    override fun centerMinusY(visibleY0: Int) = height - visibleY0 - paletteEditorPane.iconSize / 2f

    override fun onDraw(canvas: Canvas, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int) {
        drawColorRange(canvas, 0 .. 0, 0 until model.rowCount, visibleX0, visibleY0)

        canvas.drawCircle(centerPlusX(visibleX0), centerPlusY(visibleY0), iconSize / 2f, buttonPaint)

        if(model.rowCount > 1) {
            canvas.drawCircle(centerMinusX(visibleX0), centerMinusY(visibleY0), iconSize / 2f, buttonPaint)
        }
    }
}
