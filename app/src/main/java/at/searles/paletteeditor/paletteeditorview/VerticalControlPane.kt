package at.searles.paletteeditor.paletteeditorview

import android.graphics.Canvas
import at.searles.multiscrollview.InnerPaneView

// Name: Viewport.

class VerticalControlPane(rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): ControlPane(rootView, paletteEditorPane) {
    override val width: Float
        get() = paletteEditorPane.iconSize
    override val height: Float
        get() = paletteEditorPane.height - paletteEditorPane.spacing

    lateinit var listener: Listener

    override fun plusClicked() {
        listener.addColumn()
    }

    override fun minusClicked() {
        listener.removeRow()
    }

    override fun centerPlusX(visibleX0: Float) = width / 2f - visibleX0
    override fun centerPlusY(visibleY0: Float) = 0f - visibleY0 - paletteEditorPane.iconSize / 2f

    override fun centerMinusX(visibleX0: Float) = width / 2f - visibleX0 - spacing
    override fun centerMinusY(visibleY0: Float) = height - visibleY0 - paletteEditorPane.iconSize / 2f

    override fun onDraw(canvas: Canvas, visibleX0: Float, visibleY0: Float, visibleWidth: Float, visibleHeight: Float) {
        drawColorRange(canvas, 0 .. 0, 0 until (model.rowCount - 1), visibleX0, visibleY0)

        canvas.drawCircle(centerPlusX(visibleX0), centerPlusY(visibleY0), iconSize / 2f, buttonPaint)

        if(model.rowCount > 1) {
            canvas.drawCircle(centerMinusX(visibleX0), centerMinusY(visibleY0), iconSize / 2f, buttonPaint)
        }
    }

    interface Listener {
        fun addColumn()
        fun removeRow()
    }
}
