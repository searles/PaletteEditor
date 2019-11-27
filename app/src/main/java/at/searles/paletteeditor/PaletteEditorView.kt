package at.searles.paletteeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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

    override val intendedWidth: Int
        get() = 10000//((model?.columnCount ?: 0) * (iconSize + spacing) - spacing).toInt()

    override val intendedHeight: Int
        get() = 10000//((model?.rowCount ?: 0) * (iconSize + spacing) - spacing).toInt()

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

    fun columnAt(x: Int): Int = (x / (iconSize + spacing)).toInt()
    fun rowAt(y: Int): Int = (y / (iconSize + spacing)).toInt()


//    override fun onDraw(canvas: Canvas) {
//        for(x in 0 until intendedWidth step 100) {
//            for(y in 0 until intendedHeight step 100) {
//                with(paint) {
//                    color =  Color.HSVToColor(floatArrayOf(((x + y) % 360).toFloat(), x.toFloat() / intendedWidth, y.toFloat() / intendedHeight))
//                }
//
//                val x0 = (x - leftOffset).toFloat()
//                val y0 = (y - topOffset).toFloat()
//
//                canvas.drawRect(x0, y0, x0 + 75, y0 + 75, paint)
//            }
//        }
//    }


    override fun onDraw(canvas: Canvas) {
        if(model == null) {
            //return
        }

        val startCol = columnAt(0 + leftOffset)
        val endCol = columnAt(width + leftOffset)

        val startRow = rowAt(0 + topOffset)
        val endRow = rowAt(height + topOffset)

        for(row in startRow .. endRow) {
            for (col in startCol..endCol) {
                val x0 = (col * (iconSize + spacing) - leftOffset)
                val y0 = (row * (iconSize + spacing) - topOffset)

                //val color = model!!.colorAt(col, row)

                paint.color = Color.HSVToColor(floatArrayOf(((x + y) % 360).toFloat(), x.toFloat() / intendedWidth, y.toFloat() / intendedHeight))

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