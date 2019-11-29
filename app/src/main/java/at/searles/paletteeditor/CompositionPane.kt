package at.searles.paletteeditor

import android.graphics.Canvas
import android.view.MotionEvent
import at.searles.multiscrollview.InnerPane

class CompositionPane(val leftPane: InnerPane, val topPane: InnerPane, val rightPane: InnerPane, val bottomPane: InnerPane, val centerPane: InnerPane): InnerPane {
    override val width: Int
        get() = leftPane.width + centerPane.width + rightPane.width
    override val height: Int
        get() = topPane.height + centerPane.height + bottomPane.height

    override fun onClick(
        e: MotionEvent,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ): Boolean {
        return leftPane.onClick(e, visibleX0, visibleY0 - topPane.height, visibleWidth, visibleHeight) ||
                topPane.onClick(e, visibleX0 - leftPane.width, visibleY0, visibleWidth, visibleHeight) ||
                rightPane.onClick(e, visibleX0 - leftPane.width - centerPane.width, visibleY0 - topPane.height, visibleWidth, visibleHeight) ||
                bottomPane.onClick(e, visibleX0 - leftPane.width, visibleY0 - topPane.height - centerPane.height, visibleWidth, visibleHeight) ||
                centerPane.onClick(e, visibleX0 - leftPane.width, visibleY0 - topPane.height, visibleWidth, visibleHeight)
    }

    override fun onScrollTo(
        e: MotionEvent,
        visibleX0: Int,
        visibleY0: Int,
        visibleWidth: Int,
        visibleHeight: Int
    ): Boolean {
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
        centerPane.onDraw(canvas, visibleX0 - leftPane.width, visibleY0 - topPane.height, visibleWidth, visibleHeight)

        // in these other panes, visibleWidth/Height is ignored, thus no further maths
        leftPane.onDraw(canvas, visibleX0, visibleY0 - topPane.height, visibleWidth, visibleHeight)

        topPane.onDraw(canvas, visibleX0 - leftPane.width, visibleY0, visibleWidth, visibleHeight)

        rightPane.onDraw(canvas, visibleX0 - leftPane.width - centerPane.width, visibleY0 - topPane.height, visibleWidth, visibleHeight)

        bottomPane.onDraw(canvas, visibleX0 - leftPane.width, visibleY0 - topPane.height - centerPane.height, visibleWidth, visibleHeight)
    }
}