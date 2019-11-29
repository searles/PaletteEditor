package at.searles.paletteeditor

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView
import kotlin.math.max
import kotlin.math.min

/*
Offset bars:

CompositeView:

 */

class PaletteEditorPane(private val rootView: InnerPaneView): InnerPane, PaletteEditorModel.Listener {
    var iconSize = 32
    // FIXME set
    var spacing = 0
    // FIXME set

    var model: PaletteEditorModel? = null
        set(value) {
            field?.removeListener(this)
            field = value
            field?.addListener(this)
            rootView.requestLayout()
        }

    override val width: Int
        get() = if(model == null) 0 else model!!.columnCount * (iconSize + spacing) - spacing

    override val height: Int
        get() = if(model == null) 0 else model!!.rowCount * (iconSize + spacing) - spacing

    private val listeners = ArrayList<Listener>(1)

    private val paint = Paint()

    override fun onClick(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        return false
    }

    override fun onDoubleClick(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        return false
    }

    var isDragging = false

    override fun onLongPress(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        isDragging = true
        return true
    }

    override fun onScrollTo(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        return isDragging
    }

    override fun onTapUp(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        if(isDragging) {
            isDragging = false
            return true
        }

        return false
    }

    fun columnAt(x: Int): Int = max(0, min(model!!.columnCount - 1, x / (iconSize + spacing)))
    fun rowAt(y: Int): Int = max(0, min(model!!.rowCount - 1, y / (iconSize + spacing)))

    override fun onDraw(canvas: Canvas, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int) {
        if(model == null) {
            return
        }

        val startCol = columnAt(0 + visibleX0)
        val endCol = columnAt(visibleWidth + visibleX0)

        val startRow = rowAt(0 + visibleY0)
        val endRow = rowAt(visibleHeight + visibleY0)

        for(row in startRow .. endRow) {
            for (col in startCol..endCol) {
                val x0 = (col * (iconSize + spacing) - visibleX0).toFloat()
                val y0 = (row * (iconSize + spacing) - visibleY0).toFloat()

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
        rootView.requestLayout()
        rootView.invalidate()
    }

    override fun onOffsetChanged(paletteEditorModel: PaletteEditorModel) {
        // nothing to do for this view.
    }
}