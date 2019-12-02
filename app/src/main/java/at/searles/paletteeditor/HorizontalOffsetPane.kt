package at.searles.paletteeditor

import android.graphics.Canvas
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection

// Name: Viewport.

class HorizontalOffsetPane(rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): OffsetPane(rootView, paletteEditorPane) {
    override val width: Int
        get() = paletteEditorPane.width - paletteEditorPane.spacing

    override val height: Int
        get() = paletteEditorPane.iconSize

    override val scrollDirection = ScrollDirection.Horizontal

    override fun isInside(x: Float, y: Float, visibleX0: Int, visibleY0: Int): Boolean {
        return -visibleX0 <= x && x < -visibleX0 + width && 0 <= y && y < height
    }

    private fun offsetFromX(x: Float, visibleX0: Int) = (x + visibleX0 - sliderIconSize / 2f) / width

    override fun updateOffsetFromEvent(e: MotionEvent, visibleX0: Int, visibleY0: Int) {
        model.offsetX = offsetFromX(e.x, visibleX0)
    }

    override fun sliderIconX(visibleX0: Int) = model.offsetX * width - visibleX0 + sliderIconSize / 2f
    override fun sliderIconY(visibleY0: Int) = height / 2f

    override fun drawRuler(canvas: Canvas, visibleX0: Int, visibleY0: Int) {
        canvas.drawLine(0f - visibleX0, height / 2f, sliderIconX(visibleX0) - sliderIconSize / 2f, height / 2f, activePaint)
        canvas.drawLine(sliderIconX(visibleX0) + sliderIconSize / 2f, height / 2f, (width - visibleX0).toFloat(), height / 2f, passivePaint)
    }
}