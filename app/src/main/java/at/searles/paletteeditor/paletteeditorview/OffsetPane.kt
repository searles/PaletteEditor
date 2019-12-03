package at.searles.paletteeditor.paletteeditorview

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection
import at.searles.paletteeditor.R
import at.searles.paletteeditor.colors.Colors
import kotlin.math.hypot

abstract class OffsetPane(private val rootView: InnerPaneView, private val paletteEditorPane: PaletteEditorPane): InnerPane {
    val model
        get() = paletteEditorPane.model

    private val iconSize
        get() = paletteEditorPane.iconSize

    val sliderIconSize
        get() = iconSize / 1.5f

    private val sliderStrokeWidth
        get() = iconSize / 8f

    val activeColor = rootView.resources.getColor(R.color.colorAccent, null)
    val passiveColor = Colors.toGray(activeColor)

    private val activeColorTransparent = Colors.transparent(transparentAlpha, activeColor)
    private val passiveColorTransparent = Colors.transparent(transparentAlpha, passiveColor)

    val sliderRulerPaint = Paint().apply {
        strokeWidth = sliderStrokeWidth
    }

    private val sliderIconPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private var isDragging = false

    abstract fun isInside(x: Float, y: Float, visibleX0: Float, visibleY0: Float): Boolean

    abstract fun updateOffsetFromEvent(e: MotionEvent, visibleX0: Float, visibleY0: Float)

    override fun onClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun cancelCurrentAction() {
        if(isDragging) {
            isDragging = false
            rootView.invalidate()
        }
    }

    override fun onTapDown(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        val d = hypot((e.x - sliderIconX(visibleX0)).toDouble(), (e.y - sliderIconY(visibleY0)).toDouble())

        if(2.0 * d < iconSize) {
            isDragging = true
            rootView.invalidate()
            return true
        }

        return false
    }

    protected abstract val scrollDirection: ScrollDirection

    override fun onScrollTo(e: MotionEvent, visibleX0: Float, visibleY0: Float): ScrollDirection {
        if(isDragging) {
            updateOffsetFromEvent(e, visibleX0, visibleY0)
            rootView.invalidate()
            return scrollDirection
        }

        return ScrollDirection.NoScroll
    }

    override fun onLongPress(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun onTapUp(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        if(isDragging) {
            isDragging = false
            rootView.invalidate()
            return true
        }

        return false
    }

    override fun onDoubleClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    abstract fun sliderIconX(visibleX0: Float): Float
    abstract fun sliderIconY(visibleY0: Float): Float

    abstract fun drawRuler(canvas: Canvas, visibleX0: Float, visibleY0: Float)

    override fun onDraw(canvas: Canvas, visibleX0: Float, visibleY0: Float, visibleWidth: Float, visibleHeight: Float) {
        drawRuler(canvas, visibleX0, visibleY0)

        sliderIconPaint.color = if(isDragging) activeColorTransparent else passiveColorTransparent
        canvas.drawCircle(sliderIconX(visibleX0), sliderIconY(visibleY0), sliderIconSize / 2f, sliderIconPaint)

        sliderIconPaint.color = if(isDragging) activeColor else passiveColor
        canvas.drawCircle(sliderIconX(visibleX0), sliderIconY(visibleY0), sliderIconSize / 8f, sliderIconPaint)
    }

    companion object {
        const val transparentAlpha = 0.26f
    }
}