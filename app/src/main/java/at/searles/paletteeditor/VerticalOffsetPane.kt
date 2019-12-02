package at.searles.paletteeditor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection

class VerticalOffsetPane(rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): OffsetPane(rootView, paletteEditorPane) {
    override val width: Int
        get() = paletteEditorPane.iconSize
    override val height: Int
        get() = paletteEditorPane.height - paletteEditorPane.spacing

    override val scrollDirection = ScrollDirection.Vertical

    override fun isInside(x: Float, y: Float, visibleX0: Int, visibleY0: Int): Boolean {
        return -visibleY0 <= y && y < -visibleY0 + height && 0 <= x && x < width
    }

    private fun offsetFromY(y: Float, visibleY0: Int) = (y + visibleY0 - sliderIconSize / 2f) / height

    override fun updateOffsetFromEvent(e: MotionEvent, visibleX0: Int, visibleY0: Int) {
        model.offsetY = offsetFromY(e.y, visibleY0)
    }

    override fun sliderIconX(visibleX0: Int) = width / 2f
    override fun sliderIconY(visibleY0: Int) = model.offsetY * height - visibleY0 + sliderIconSize / 2f

    override fun drawRuler(canvas: Canvas, visibleX0: Int, visibleY0: Int) {
        canvas.drawLine(width / 2f, 0f - visibleY0, width / 2f, sliderIconY(visibleY0) - sliderIconSize / 2f, activePaint)
        canvas.drawLine(width / 2f, sliderIconY(visibleY0) + sliderIconSize / 2f, width / 2f, (height - visibleY0).toFloat(), passivePaint)
    }
}