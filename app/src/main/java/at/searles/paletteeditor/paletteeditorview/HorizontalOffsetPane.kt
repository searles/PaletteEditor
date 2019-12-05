package at.searles.paletteeditor.paletteeditorview

import android.graphics.Canvas
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection

// Name: Viewport.

class HorizontalOffsetPane(rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): OffsetPane(rootView, paletteEditorPane) {
    override val width: Float
        get() = paletteEditorPane.width - paletteEditorPane.spacing

    override val height: Float
        get() = paletteEditorPane.iconSize

    override val scrollDirection = ScrollDirection.Horizontal

    private fun offsetFromX(x: Float, visibleX0: Float) = (x + visibleX0 - sliderIconSize / 2f) / (width - sliderIconSize)

    lateinit var listener: Listener

    override fun updateOffsetFromEvent(e: MotionEvent, visibleX0: Float, visibleY0: Float) {
        listener.onHorizontalOffsetChanged(offsetFromX(e.x, visibleX0))
    }

    override fun sliderIconX(visibleX0: Float) = model.offsetX * (width - sliderIconSize) - visibleX0 + sliderIconSize / 2f
    override fun sliderIconY(visibleY0: Float) = height / 2f

    override fun drawRuler(canvas: Canvas, visibleX0: Float, visibleY0: Float) {
        sliderRulerPaint.color = activeColor
        canvas.drawLine(0f - visibleX0, height / 2f, sliderIconX(visibleX0) - sliderIconSize / 2f, height / 2f, sliderRulerPaint)

        sliderRulerPaint.color = passiveColor
        canvas.drawLine(sliderIconX(visibleX0) + sliderIconSize / 2f, height / 2f, (width - visibleX0).toFloat(), height / 2f, sliderRulerPaint)
    }

    interface Listener {
        fun onHorizontalOffsetChanged(offset: Float)
    }
}