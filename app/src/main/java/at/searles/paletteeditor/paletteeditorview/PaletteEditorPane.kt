package at.searles.paletteeditor.paletteeditorview

import android.R.attr.*
import android.content.ClipData
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.ScrollDirection
import at.searles.paletteeditor.Dpis
import at.searles.paletteeditor.PaletteEditorModel
import at.searles.paletteeditor.R
import at.searles.paletteeditor.colors.Colors
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
    private val colorPointPaint = Paint().apply { style = Paint.Style.FILL }

    override fun onClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun onDoubleClick(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        val col = columnAt(e.x, visibleX0)
        val row = rowAt(e.y, visibleY0)

        if(col < 0 || model.columnCount <= col || row < 0 || model.rowCount <= row) {
            return false
        }

        listener.editColorPointAt(col, row)

        return true
    }

    private var isMovingColorAction = false

    private var dragFromCol = 0
    private var dragFromRow = 0

    private val draggableView = Button(rootView.context).also {
        it.layoutParams = RelativeLayout.LayoutParams(
            iconSize.toInt(),
            iconSize.toInt()
        )

        it.visibility = View.INVISIBLE

        rootView.addView(it)
    }

    private fun isInRange(col: Int, row: Int): Boolean {
        return 0 <= col && col < model.columnCount && 0 <= row && row < model.rowCount
    }

    override fun onLongPress(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        val col = columnAt(e.x, visibleX0)
        val row = rowAt(e.y, visibleY0)

        if(!isInRange(col, row)) {
            return false
        }

        val selectedColor = model.colorAt(col, row)

        val dragLoad = createClipDataFromColor(model.colorAt(col, row))

        draggableView.backgroundTintList = ColorStateList.valueOf(selectedColor)
        val shadowBuilder = View.DragShadowBuilder(draggableView)
        draggableView.startDragAndDrop(dragLoad, shadowBuilder, draggableView, 0)

        isMovingColorAction = true

        dragFromCol = col
        dragFromRow = row

        return true
    }

    override fun onTapDown(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun onScrollTo(e: MotionEvent, visibleX0: Float, visibleY0: Float): ScrollDirection? {
        return null
    }

    override fun onTapUp(e: MotionEvent, visibleX0: Float, visibleY0: Float): Boolean {
        return false
    }

    override fun dragStarted(e: DragEvent, visibleX0: Float, visibleY0: Float): Boolean {
        // Yes, we accept drag events
        // TODO
        draggedIsMarkedForDeletion = false
        return true
    }

    override fun dragEntered(e: DragEvent, visibleX0: Float, visibleY0: Float): Boolean {
        // TODO
        return true
    }

    override fun dragExited(e: DragEvent, visibleX0: Float, visibleY0: Float): Boolean {
        draggedIsMarkedForDeletion = false
        return true
    }

    private var draggedIsMarkedForDeletion = false

    override fun dragLocation(e: DragEvent, visibleX0: Float, visibleY0: Float): ScrollDirection? {
        val col = columnAt(e.x, visibleX0)
        val row = rowAt(e.y, visibleY0)

        if(isInRange(col, row)) {
            listener.activateColorAt(col, row)

            if(isMovingColorAction) {
                draggedIsMarkedForDeletion = false
            }
        } else {
            listener.deactivateColor()

            if(isMovingColorAction) {
                draggedIsMarkedForDeletion = true
            }
        }

        return ScrollDirection.Both
    }

    override fun dragEnded(e: DragEvent, visibleX0: Float, visibleY0: Float): Boolean {
        listener.deactivateColor()
        isMovingColorAction = false
        rootView.invalidate()
        return true
    }

    override fun drop(e: DragEvent, visibleX0: Float, visibleY0: Float): Boolean {
        val col = columnAt(e.x, visibleX0)
        val row = rowAt(e.y, visibleY0)

        val color = getColorFromClipData(e.clipData)

        val isAddColorAction = isInRange(col, row)
        val isDeleteColorAction = !isAddColorAction && isMovingColorAction

        if(isDeleteColorAction && !model.isColorPoint(dragFromCol, dragFromRow)) {
            return false
        }

        if(isDeleteColorAction) {
            listener.removeColorPointAt(dragFromCol, dragFromRow)
            return true
        }

        if(isMovingColorAction && col == dragFromCol && row == dragFromRow) {
            return false
        }

        require(isAddColorAction)

        listener.addColorPointAt(col, row, color)

        if(isMovingColorAction) {
            listener.removeColorPointAt(dragFromCol, dragFromRow)
        }

        return true
    }

    fun columnAt(x: Float, visibleX0: Float): Int = ((x + visibleX0) / (iconSize + spacing) + 1f).toInt() - 1
    fun rowAt(y: Float, visibleY0: Float): Int = ((y + visibleY0) / (iconSize + spacing) + 1f).toInt() - 1

    fun x0At(col: Int, visibleX0: Float) = (col * (iconSize + spacing) - visibleX0)
    fun y0At(row: Int, visibleY0: Float) = (row * (iconSize + spacing) - visibleY0)

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
    }

    fun drawColorAt(canvas: Canvas, x0: Float, y0: Float, color: Int) {
        colorRectPaint.color = color
        canvas.drawRect(x0, y0, x0 + iconSize, y0 + iconSize, colorRectPaint)
    }

    fun drawColor(canvas: Canvas, col: Int, row: Int, visibleX0: Float, visibleY0: Float) {
        val x0 = x0At(col, visibleX0)
        val y0 = y0At(row, visibleY0)

        val isColorPoint = model.isColorPoint(col, row)

        // TODO: Change style Eg. each color gets an icon depending on it.

        if(!isMovingColorAction || dragFromRow != row || dragFromCol != col) {
            val color = model.colorAt(col, row)
            drawColorAt(canvas, x0, y0, color)

            if(isColorPoint) {
                val overlayColor = if(Colors.brightness(color) < 0.5f) Color.WHITE else Color.BLACK
                colorPointPaint.color = Colors.transparent(transparency, overlayColor)
                canvas.drawCircle(x0 + iconSize / 2f, y0 + iconSize / 2f, iconSize / 8f, colorPointPaint)
            }
        } else if(draggedIsMarkedForDeletion && isColorPoint) {
            val d: Drawable = rootView.resources.getDrawable(R.drawable.ic_delete_black_24dp, null)
            d.setBounds(x0.toInt(), y0.toInt(), (x0 + iconSize).toInt(), (y0 + iconSize).toInt())
            d.draw(canvas)
        }

        // todo if out of range draw bin

        if(model.selectedCol == col && model.selectedRow == row) {
            colorRectPaint.color = Colors.transparent(transparency, rootView.resources.getColor(R.color.colorAccent, null))
            canvas.drawRect(x0 - spacing / 2f, y0 - spacing / 2f, x0 + iconSize + spacing / 2f, y0 + iconSize + spacing / 2f, colorRectPaint)
        }
    }

    interface Listener {
        fun activateColorAt(col: Int, row: Int)
        fun deactivateColor()

        fun addColorPointAt(col: Int, row: Int, color: Int)
        fun removeColorPointAt(col: Int, row: Int)

        fun editColorPointAt(col: Int, row: Int)
    }

    companion object {
        const val transparency = 0.26f

        const val defaultIconSizeDp = 48f
        const val defaultSpacingDp = 2f
        private const val colorStringKey = "colorString"

        fun createClipDataFromColor(color: Int): ClipData {
            return ClipData.newPlainText(colorStringKey, Colors.toColorString(color))
        }

        fun getColorFromClipData(clipData: ClipData): Int {
            return Colors.fromColorString(clipData.getItemAt(0).text.toString())
        }
    }
}