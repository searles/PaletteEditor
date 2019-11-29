package at.searles.paletteeditor

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.paletteeditor.colors.Colors

// Name: Viewport.

class HorizontalOffsetPane(private val rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): InnerPane {
    override val width: Int
        get() = paletteEditorPane.width - paletteEditorPane.spacing
    override val height: Int
        get() = paletteEditorPane.iconSize

    private val model
        get() = paletteEditorPane.model!!

    private val iconSize
        get() = paletteEditorPane.iconSize

    private val spacing
        get() = paletteEditorPane.spacing

    private val sliderIconSize
        get() = iconSize / 1.5f

    private val sliderStrokeWidth
        get() = iconSize / 8f

    private val colorAccent = rootView.resources.getColor(R.color.colorAccent, null)

    private val paint = Paint().apply {
        color = colorAccent
        strokeWidth = sliderStrokeWidth
    }

    private val palePaint = Paint().apply {
        color = Colors.transparent(paleFactor, colorAccent)
        strokeWidth = sliderStrokeWidth
    }

    private val sliderIconPaint = Paint().apply {
        color = colorAccent
        style = Paint.Style.FILL
    }

    override fun isInside(x: Float, y: Float, visibleX0: Int, visibleY0: Int): Boolean {
        return -visibleX0 <= x && x < -visibleX0 + width && 0 <= y && y < height
    }

    override fun onClick(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        if(!isInside(e.x, e.y, visibleX0, visibleY0)) {
            return false
        }

        model.offsetX = offsetFromX(e.x, visibleX0)
        rootView.invalidate() // FIXME Move from here to listener.
        return true
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

    private fun offsetFromX(x: Float, visibleX0: Int) = (x + visibleX0 - iconSize / 2f) / width

    private fun sliderIconX(visibleX0: Int) = model.offsetX * width - visibleX0 + iconSize / 2f
    private fun sliderIconY(visibleY0: Int) = height / 2f

    override fun onDraw(
        canvas: Canvas,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ) {
        canvas.drawLine(0f - visibleX0, height / 2f, sliderIconX(visibleX0), height / 2f, paint)
        canvas.drawLine(sliderIconX(visibleX0), height / 2f, (width - visibleX0).toFloat(), height / 2f, palePaint)

        canvas.drawCircle(sliderIconX(visibleX0), sliderIconY(visibleY0), sliderIconSize / 2f, sliderIconPaint)
    }

    companion object {
        val paleFactor = 0.26f
    }
}