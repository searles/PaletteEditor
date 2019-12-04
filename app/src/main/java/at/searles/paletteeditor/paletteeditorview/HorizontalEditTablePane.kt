package at.searles.paletteeditor.paletteeditorview

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import at.searles.multiscrollview.InnerPaneView
import at.searles.paletteeditor.R

class HorizontalEditTablePane(private val rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): EditTablePane(rootView, paletteEditorPane) {
    override val width: Float
        get() = paletteEditorPane.width - paletteEditorPane.spacing
    override val height: Float
        get() = paletteEditorPane.iconSize

    lateinit var listener: Listener

    override fun plusClicked() {
        listener.addRow()
    }

    override fun minusClicked() {
        listener.removeColumn()
    }

    override fun centerPlusX(visibleX0: Float) = 0f - visibleX0 - paletteEditorPane.iconSize / 2f
    override fun centerPlusY(visibleY0: Float) = height / 2f - visibleY0

    override fun centerMinusX(visibleX0: Float) = width - visibleX0 - paletteEditorPane.iconSize / 2f
    override fun centerMinusY(visibleY0: Float) = height / 2f - visibleY0 - spacing

    override fun onDraw(canvas: Canvas, visibleX0: Float, visibleY0: Float, visibleWidth: Float, visibleHeight: Float) {
        drawColorRange(canvas, 0 until (model.columnCount - 1), 0 .. 0, visibleX0, visibleY0)

        drawPlus(canvas, centerPlusX(visibleX0), centerPlusY(visibleY0))

        if(model.columnCount > 1) {
            drawMinus(canvas, centerMinusX(visibleX0), centerMinusY(visibleY0))
        }
    }

    interface Listener {
        fun addRow()
        fun removeColumn()
    }
}
