package at.searles.paletteeditor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane
import at.searles.multiscrollview.InnerPaneView

// Name: Viewport.

class VerticalOffsetPane(val rootView: InnerPaneView, val paletteEditorPane: PaletteEditorPane): InnerPane {
    override val width: Int
        get() = paletteEditorPane.iconSize
    override val height: Int
        get() = paletteEditorPane.height - paletteEditorPane.spacing

    private val model
        get() = paletteEditorPane.model!!

    override fun onClick(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        return false
    }

    val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 24f
    }

    override fun onScrollTo(e: MotionEvent, visibleX0: Int, visibleY0: Int, visibleWidth: Int, visibleHeight: Int): Boolean {
        return false
    }

    override fun onLongPress(
        e: MotionEvent,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ): Boolean {
        return false
    }

    override fun onTapUp(
        e: MotionEvent,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ): Boolean {
        return false
    }

    override fun onDoubleClick(
        e: MotionEvent,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ): Boolean {
        return false
    }

    override fun onDraw(
        canvas: Canvas,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ) {
        canvas.drawLine(width / 2f, 0f - visibleY0.toFloat(), width / 2f,
            (height - visibleY0).toFloat(), paint)

        paletteEditorPane.model
    }
}