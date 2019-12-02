package at.searles.paletteeditor

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection
import at.searles.paletteeditor.colors.Colors
import kotlin.math.hypot

abstract class OffsetPane(private val rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): InnerPane {
    val model
        get() = paletteEditorPane.model!!

    val iconSize
        get() = paletteEditorPane.iconSize

    val sliderIconSize
        get() = iconSize / 1.5f

    private val sliderStrokeWidth
        get() = iconSize / 8f

    private val colorAccent = rootView.resources.getColor(R.color.colorAccent, null)

    val paint = Paint().apply {
        strokeWidth = sliderStrokeWidth
    }

    val passivePaint = Paint().apply {
        color = Colors.transparent(unselectedAlpha, colorAccent)
        strokeWidth = sliderStrokeWidth
    }

    val activePaint = Paint().apply {
        color = Colors.transparent(selectedAlpha, colorAccent)
        strokeWidth = sliderStrokeWidth
    }

    private val sliderIconPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private var isDragging = false

    abstract fun isInside(x: Float, y: Float, visibleX0: Int, visibleY0: Int): Boolean

    abstract fun updateOffsetFromEvent(e: MotionEvent, visibleX0: Int, visibleY0: Int)

    override fun onClick(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        if(!isInside(e.x, e.y, visibleX0, visibleY0)) {
            return false
        }

        updateOffsetFromEvent(e, visibleX0, visibleY0)
        rootView.invalidate()
        return true
    }

    override fun onTapDown(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        val d = hypot((e.x - sliderIconX(visibleX0)).toDouble(), (e.y - sliderIconY(visibleY0)).toDouble())

        if(2.0 * d < iconSize) {
            isDragging = true
            rootView.invalidate()
            return true
        }

        return false
    }

    protected abstract val scrollDirection: ScrollDirection

    override fun onScrollTo(e: MotionEvent, visibleX0: Int, visibleY0: Int): ScrollDirection {
        if(isDragging) {
            updateOffsetFromEvent(e, visibleX0, visibleY0)
            rootView.invalidate()
            return scrollDirection
        }

        return ScrollDirection.NoScroll
    }

    override fun onLongPress(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        return false
    }

    override fun onTapUp(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        if(isDragging) {
            isDragging = false
            rootView.invalidate()
            return true
        }

        return false
    }

    override fun onDoubleClick(e: MotionEvent, visibleX0: Int, visibleY0: Int): Boolean {
        return false
    }

    abstract fun sliderIconX(visibleX0: Int): Float
    abstract fun sliderIconY(visibleY0: Int): Float

    abstract fun drawRuler(canvas: Canvas, visibleX0: Int, visibleY0: Int)

    override fun onDraw(canvas: Canvas, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int) {
        drawRuler(canvas, visibleX0, visibleY0)

        sliderIconPaint.color = Colors.transparent(if(isDragging) selectedAlpha else unselectedAlpha, colorAccent)
        canvas.drawCircle(sliderIconX(visibleX0), sliderIconY(visibleY0), sliderIconSize / 2f, sliderIconPaint)

        sliderIconPaint.color = colorAccent
        canvas.drawCircle(sliderIconX(visibleX0), sliderIconY(visibleY0), (sliderStrokeWidth + 1) / 2f, sliderIconPaint)
    }

    companion object {
        const val unselectedAlpha = 0.26f
        const val selectedAlpha = 0.66f
    }
}