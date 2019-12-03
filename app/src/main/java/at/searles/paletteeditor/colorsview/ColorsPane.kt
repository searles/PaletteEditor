package at.searles.paletteeditor.colorsview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection
import at.searles.paletteeditor.Dpis
import at.searles.paletteeditor.PaletteEditorModel
import at.searles.paletteeditor.R
import at.searles.paletteeditor.colors.DefaultColors
import kotlin.math.max
import kotlin.math.min

class ColorsPane(private val rootView: InnerPaneView, val orientation: Orientation): InnerPane {

    enum class Orientation { Horizonal, Vertical }

    val iconSize: Float = Dpis.dpiToPx(
        rootView.resources,
        defaultIconSizeDp
    )

    val spacing: Float = Dpis.dpiToPx(
        rootView.resources,
        defaultSpacingDp
    )
    
    lateinit var listener: Listener

    private val colorCount = DefaultColors.colors.size

    override val width: Float
        get() = if(orientation == Orientation.Horizonal) colorCount * (iconSize + spacing) else iconSize + spacing * 2

    override val height: Float
        get() = if(orientation == Orientation.Vertical) colorCount * (iconSize + spacing) else iconSize + spacing * 2

    private val colorRectPaint = Paint()

    override fun onClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun onDoubleClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    private var isDragging = false

    private var draggedColor = 0

    private var draggingX0: Float = 0f
    private var draggingY0: Float = 0f

    override fun onLongPress(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun onTapDown(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        val index = if(orientation == Orientation.Horizonal) at(e.x, visibleX0) else at(e.y, visibleY0)

        if(index < 0 || colorCount <= index) {
            return false
        }

        draggedColor = DefaultColors.colors[index]

        draggingX0 = e.x
        draggingY0 = e.y

        listener.onColorDragInitiated(draggedColor)

        return true
    }

    override fun onScrollTo(e: MotionEvent, visibleX0: Float, visibleY0: Float): ScrollDirection {
        draggingX0 = e.x
        draggingY0 = e.y

        rootView.invalidate()

        return ScrollDirection.NoScroll
    }

    override fun onTapUp(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        if(isDragging) {
            isDragging = false
            rootView.invalidate()
            return true
        }

        return false
    }

    override fun cancelCurrentAction() {
        isDragging = false
        rootView.invalidate()
    }

    private fun at(z: Float, visibleZ0: Float): Int = ((z + visibleZ0) / (iconSize + spacing)).toInt()

    override fun onDraw(canvas: Canvas, visibleX0: Float, visibleY0: Float, visibleWidth: Float, visibleHeight: Float) {
        if(orientation == Orientation.Vertical) {
            val start = max(0, min(colorCount - 1, at(0f, visibleY0)))
            val end = max(0, min(colorCount - 1, at(visibleHeight, visibleY0)))

            for (row in start..end) {
                drawColor(canvas, 0, row, visibleX0, visibleY0)
            }
        } else {
            val start = max(0, min(colorCount - 1, at(0f, visibleX0)))
            val end = max(0, min(colorCount - 1, at(visibleWidth, visibleX0)))

            for (col in start..end) {
                drawColor(canvas, col, 0, visibleX0, visibleY0)
            }
        }

        if(isDragging) {
            drawColorAt(canvas, draggingX0, draggingY0, draggedColor)
        }
    }

    private fun drawColorAt(canvas: Canvas, x0: Float, y0: Float, color: Int) {
        colorRectPaint.color = color
        canvas.drawRect(x0, y0, x0 + iconSize, y0 + iconSize, colorRectPaint)
    }

    private fun drawColor(canvas: Canvas, col: Int, row: Int, visibleX0: Float, visibleY0: Float) {
        val x0 = (col * (iconSize + spacing) - visibleX0)
        val y0 = (row * (iconSize + spacing) - visibleY0)

        drawColorAt(canvas, x0, y0, DefaultColors.colors[max(col, row)])
    }

    interface Listener {
        fun onColorDragInitiated(color: Int)
    }

    companion object {
        const val defaultIconSizeDp = 48f
        const val defaultSpacingDp = 4f
    }
}