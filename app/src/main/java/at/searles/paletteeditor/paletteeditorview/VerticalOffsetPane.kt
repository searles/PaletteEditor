package at.searles.paletteeditor.paletteeditorview

import android.graphics.Canvas
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection

class VerticalOffsetPane(rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): OffsetPane(rootView, paletteEditorPane) {
    override val width: Float
        get() = paletteEditorPane.iconSize
    override val height: Float
        get() = paletteEditorPane.height - paletteEditorPane.spacing

    override val scrollDirection = ScrollDirection.Vertical

    private fun offsetFromY(y: Float, visibleY0: Float) = (y + visibleY0 - sliderIconSize / 2f) / (height - sliderIconSize)

    lateinit var listener: Listener

    override fun updateOffsetFromEvent(e: MotionEvent, visibleX0: Float, visibleY0: Float) {
        listener.onVerticalOffsetChanged(offsetFromY(e.y, visibleY0))
    }

    override fun sliderIconX(visibleX0: Float) = width / 2f
    override fun sliderIconY(visibleY0: Float) = model.offsetY * (height - sliderIconSize) - visibleY0 + sliderIconSize / 2f

    override fun drawRuler(canvas: Canvas, visibleX0: Float, visibleY0: Float) {
        sliderRulerPaint.color = activeColor
        canvas.drawLine(width / 2f, 0f - visibleY0, width / 2f, sliderIconY(visibleY0) - sliderIconSize / 2f, sliderRulerPaint)

        sliderRulerPaint.color = passiveColor
        canvas.drawLine(width / 2f, sliderIconY(visibleY0) + sliderIconSize / 2f, width / 2f, (height - visibleY0).toFloat(), sliderRulerPaint)
    }

    interface Listener {
        fun onVerticalOffsetChanged(offset: Float)
    }
}