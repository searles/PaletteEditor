package at.searles.paletteeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PaletteEditorView(context: Context, attrs: AttributeSet) : MultiScrollableDrawableCanvas, View(context, attrs), PaletteEditorModel.Listener {
    private val listeners = ArrayList<Listener>(1)

    var model: PaletteEditorModel? = null
        set(value) {
            field?.removeListener(this)
            field = value
            field?.addListener(this)
        }

    override val intendedWidth: Float
        get() = (model?.columnCount ?: 0) * (iconSize + spacing) - spacing

    override val intendedHeight: Float
        get() = (model?.rowCount ?: 0) * (iconSize + spacing) - spacing

    private var leftOffset = 0f
    private var topOffset = 0f

    var iconSizeDp = 128
    var spacingDp = 4

    private val iconSize: Float
        get() {
            return Dpis.dpiToPx(resources, iconSizeDp.toFloat())
        }

    private val spacing: Float
        get() {
            return Dpis.dpiToPx(resources, spacingDp.toFloat())
        }

    private val paint = Paint().apply{
        strokeWidth = 4f
    }

    override fun setOffset(left: Float, top: Float) {
        this.leftOffset = left
        this.topOffset = top

        invalidate()
    }

    override fun onSingleTapUp(evt: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTap(evt: MotionEvent): Boolean {
        return false
    }

    override fun onLongPress(evt: MotionEvent): Boolean {
        return false
    }

    override fun onMoveTo(evt: MotionEvent): Boolean {
        return false
    }

    override fun onTapUp(event: MotionEvent): Boolean {
        return false
    }

    fun columnAt(x: Float): Int = (x / (iconSize + spacing)).toInt()
    fun rowAt(y: Float): Int = (y / (iconSize + spacing)).toInt()

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
                val x0 = (col * (iconSize + spacing) - leftOffset)
                val y0 = (row * (iconSize + spacing) - topOffset)

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
}
