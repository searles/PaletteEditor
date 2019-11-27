package at.searles.paletteeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class PaletteEditorView(context: Context, attrs: AttributeSet) : MultiScrollableDrawableCanvas, View(context, attrs), PaletteEditorModel.Listener {

    var iconSize = 128
    var spacing = 4

    var model: PaletteEditorModel? = null
        set(value) {
            field?.removeListener(this)
            field = value
            field?.addListener(this)
            requestLayout()
        }

    override val intendedWidth: Int
        get() = if(model == null) 0 else model!!.columnCount * (iconSize + spacing) - spacing

    override val intendedHeight: Int
        get() = if(model == null) 0 else model!!.rowCount * (iconSize + spacing) - spacing

    var leftOffset: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var topOffset: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    private val listeners = ArrayList<Listener>(1)

    private val paint = Paint()

    override fun setOffset(left: Int, top: Int) {
        this.leftOffset = left
        this.topOffset = top

        invalidate()
    }

    override fun onClick(evt: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleClick(evt: MotionEvent): Boolean {
        return false
    }

    var isDragging = false

    override fun onLongPress(evt: MotionEvent): Boolean {
        isDragging = true
        return true
    }

    override fun onScrollTo(evt: MotionEvent): Boolean {
        return isDragging
    }

    override fun onTapUp(event: MotionEvent): Boolean {
        if(isDragging) {
            isDragging = false
            return true
        }

        return false
    }

    fun columnAt(x: Int): Int = max(0, min(model!!.columnCount - 1, x / (iconSize + spacing)))
    fun rowAt(y: Int): Int = max(0, min(model!!.rowCount - 1, y / (iconSize + spacing)))

    override fun onDraw(canvas: Canvas) {
        if(model == null) {
            return
        }

        val startCol = columnAt(0 + leftOffset)
        val endCol = columnAt(width + leftOffset)

        val startRow = rowAt(0 + topOffset)
        val endRow = rowAt(height + topOffset)

        for(row in startRow .. endRow) {
            for (col in startCol..endCol) {
                val x0 = (col * (iconSize + spacing) - leftOffset).toFloat()
                val y0 = (row * (iconSize + spacing) - topOffset).toFloat()

                val color = model!!.colorAt(col, row)

                paint.color = color

                canvas.drawRect(x0, y0, x0 + iconSize, y0 + iconSize, paint)
            }
        }
    }

    fun addListener(listener: Listener) {
        this.listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        this.listeners.remove(listener)
    }

    interface Listener {

    }

    override fun onPaletteSizeChanged(paletteEditorModel: PaletteEditorModel) {
        invalidate()
        // TODO
    }
}