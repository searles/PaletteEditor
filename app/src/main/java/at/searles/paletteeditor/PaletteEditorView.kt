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
            invalidate()
            requestLayout()
        }

    override val realWidth: Float
        get() = (model?.columnCount ?: 0) * (iconSize + spacing) - spacing

    override val realHeight: Float
        get() = (model?.rowCount ?: 0) * (iconSize + spacing) - spacing

    override var leftOffset = 0f
        private set

    override var topOffset = 0f
        private set

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

    override fun onDoubleClick(e: MotionEvent): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        // TODO
        return false
    }

    override fun onTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScrollTo(e: MotionEvent): Boolean {
        return true
    }

    fun columnAt(rx: Float): Int = (rx / (iconSize + spacing)).toInt()
    fun rowAt(ry: Float): Int = (ry / (iconSize + spacing)).toInt()

    override fun onDraw(canvas: Canvas) {
        if(model == null) {
            return
        }

        val startCol = columnAt(rx(0f)) // fixme borders!
        val endCol = columnAt(rx(width.toFloat()))

        val startRow = rowAt(ry(0f))
        val endRow = rowAt(ry(height.toFloat()))

        for(row in startRow .. endRow) {
            for (col in startCol..endCol) {
                val x0 = vx(col * (iconSize + spacing))
                val y0 = vy(row * (iconSize + spacing))

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
