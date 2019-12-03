package at.searles.paletteeditor.paletteeditorview

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
import kotlin.math.max
import kotlin.math.min

class PaletteEditorPane(private val rootView: InnerPaneView, val model: PaletteEditorModel): InnerPane {

    val iconSize: Float = Dpis.dpiToPx(
        rootView.resources,
        defaultIconSizeDp
    )

    val spacing: Float = Dpis.dpiToPx(
        rootView.resources,
        defaultSpacingDp
    )
    
    lateinit var listener: Listener

    override val width: Float
        get() = model.columnCount * (iconSize + spacing)

    override val height: Float
        get() = model.rowCount * (iconSize + spacing)

    private val colorRectPaint = Paint()
    private val colorPointPaint = Paint().apply { color = Color.BLACK; strokeWidth = 4f }

    override fun onClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        val col = columnAt(e.x, visibleX0)
        val row = rowAt(e.y, visibleY0)

        if(col < 0 || model.columnCount <= col || row < 0 || model.rowCount <= row) {
            return false
        }

        listener.onColorClicked(col, row)

        return true
    }

    override fun onDoubleClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        val col = columnAt(e.x, visibleX0)
        val row = rowAt(e.y, visibleY0)

        if(col < 0 || model.columnCount <= col || row < 0 || model.rowCount <= row) {
            return false
        }

        listener.onColorDoubleClicked(col, row)

        return true
    }

    private var isDragging = false

    private var draggingFromCol = 0
    private var draggingFromRow = 0

    private var draggingToCol = 0
    private var draggingToRow = 0

    private var draggingX0: Float = 0f
    private var draggingY0: Float = 0f


    override fun onLongPress(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        val col = columnAt(e.x, visibleX0)
        val row = rowAt(e.y, visibleY0)

        if(col < 0 || model.columnCount <= col || row < 0 || model.rowCount <= row) {
            return false
        }

        isDragging = true

        draggingFromCol = col
        draggingFromRow = row

        draggingToCol = col
        draggingToRow = row

        draggingX0 = e.x
        draggingY0 = e.y

        listener.onColorActivated(draggingFromCol, draggingFromRow)

        return true
    }

    override fun onTapDown(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun onScrollTo(e: MotionEvent, visibleX0: Float, visibleY0: Float): ScrollDirection {
        if(isDragging) {
            draggingX0 = e.x
            draggingY0 = e.y

            draggingToCol = columnAt(e.x, visibleX0)
            draggingToRow = rowAt(e.y, visibleY0)

            listener.onColorActivated(draggingToCol, draggingToRow)
            
            rootView.invalidate()

            return ScrollDirection.Both
        }

        return ScrollDirection.NoScroll
    }

    override fun onTapUp(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        if(isDragging) {
            listener.onColorDraggedTo(draggingFromCol, draggingFromRow, draggingToCol, draggingToRow)

            isDragging = false
            return true
        }

        return false
    }

    override fun cancelCurrentAction() {
        isDragging = false
    }

    private fun columnAt(x: Float, visibleX0: Float): Int = ((x + visibleX0) / (iconSize + spacing) + 1f).toInt() - 1
    private fun rowAt(y: Float, visibleY0: Float): Int = ((y + visibleY0) / (iconSize + spacing) + 1f).toInt() - 1

    override fun onDraw(canvas: Canvas, visibleX0: Float, visibleY0: Float, visibleWidth: Float, visibleHeight: Float) {
        val startCol = max(0, min(model.columnCount - 1, columnAt(0f, visibleX0)))
        val endCol = max(0, min(model.columnCount - 1, columnAt(visibleWidth, visibleX0)))

        val startRow = max(0, min(model.rowCount - 1, rowAt(0f, visibleY0)))
        val endRow = max(0, min(model.rowCount - 1, rowAt(visibleHeight, visibleY0)))

        for(row in startRow .. endRow) {
            for (col in startCol..endCol) {
                drawColor(canvas, col, row, visibleX0, visibleY0)
            }
        }

        if(isDragging) {
            drawColorAt(canvas, draggingX0 - iconSize / 2f, draggingY0 - iconSize / 2f, model.colorAt(draggingFromCol, draggingFromRow))

            // TODO If draggingTo is out of range, mark as 'delete'.
        }
    }

    private fun drawColorAt(canvas: Canvas, x0: Float, y0: Float, color: Int) {
        colorRectPaint.color = color
        canvas.drawRect(x0, y0, x0 + iconSize, y0 + iconSize, colorRectPaint)
    }

    fun drawColor(canvas: Canvas, col: Int, row: Int, visibleX0: Float, visibleY0: Float) {
        val x0 = (col * (iconSize + spacing) - visibleX0)
        val y0 = (row * (iconSize + spacing) - visibleY0)

        if(model.selectedCol == col && model.selectedRow == row) {
            colorRectPaint.color = rootView.resources.getColor(R.color.colorAccent, null)
            canvas.drawRect(x0 - spacing / 2f, y0 - spacing / 2f, x0 + iconSize + spacing / 2f, y0 + iconSize + spacing / 2f, colorRectPaint)
        }

        if(!isDragging || draggingFromRow != row || draggingFromCol != col) {
            drawColorAt(canvas, x0, y0, model.colorAt(col, row))

            if(model.isColorPoint(col, row)) {
                canvas.drawCircle(x0 + iconSize / 2f, y0 + iconSize / 2f, iconSize / 3f, colorPointPaint)
            }
        }
    }

    interface Listener {
        fun onColorClicked(col: Int, row: Int)
        fun onColorDoubleClicked(col: Int, row: Int)
        fun onColorDraggedTo(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int)
        fun onColorActivated(toCol: Int, toRow: Int)
    }

    companion object {
        const val defaultIconSizeDp = 48f
        const val defaultSpacingDp = 4f
    }
}