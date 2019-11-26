package at.searles.paletteeditor

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import java.util.*
import kotlin.math.sqrt

class MultiScrollView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    private val hscroll: HorizontalScrollView by lazy {
        findViewById<HorizontalScrollView>(R.id.hscroll)
    }

    private val vscroll: ScrollView by lazy {
        findViewById<ScrollView>(R.id.vscroll)
    }

    // spacings so that the scrolls will be of correct size
    private val hspace: View by lazy {
        findViewById<View>(R.id.hspace)
    }

    private val vspace: View by lazy {
        findViewById<View>(R.id.vspace)
    }

    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(context, GestureController())
    }

    private val drawableCanvas: MultiScrollableDrawableCanvas by lazy {
        findViewById<View>(R.id.paletteView) as MultiScrollableDrawableCanvas
    }

    init {
        View.inflate(context, R.layout.multiscroll_view, this)

        hscroll.viewTreeObserver.addOnScrollChangedListener { updateViewCoordinates() }
        vscroll.viewTreeObserver.addOnScrollChangedListener { updateViewCoordinates() }

        updateSize()

        post { updateViewCoordinates() }
    }

    private var currentScrollStepSize = 0f

    private var scrollDirectionX = 0f
    private var scrollDirectionY = 0f

    private var scrollDragTimer: Timer? = null

    public override fun onDraw(canvas: Canvas) {
        drawableCanvas.onDraw(canvas)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            if(drawableCanvas.onMoveTo(event)) {
                if(isWithinBorderScrollingMargin(event)) {
                    updateBorderScrolling(event)
                    return true
                }
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            cancelBorderScrolling()
            if (drawableCanvas.onTapUp(event)) {
                return true
            }
        }

        return gestureDetector.onTouchEvent(event)
                || (hscroll.dispatchTouchEvent(event) or vscroll.dispatchTouchEvent(event))
    }

    private fun isWithinBorderScrollingMargin(event: MotionEvent): Boolean {
        return event.x < borderScrollMargin || event.y < borderScrollMargin || event.x > width - borderScrollMargin || event.y > height - borderScrollMargin
    }

    private fun updateBorderScrolling(event: MotionEvent) {
        val dx = event.x - width / 2f
        val dy = event.y - height / 2f

        val d = sqrt(dx * dx + dy * dy)

        scrollDirectionX = dx / d
        scrollDirectionY = dy / d

        currentScrollStepSize = Dpis.dpiToPx(resources, initialBorderScrollStepSizeDp)

        if(scrollDragTimer == null) {
            scrollDragTimer = Timer()
            scrollDragTimer!!.scheduleAtFixedRate(
                BorderScrollUpdateTask(),
                0,
                borderScrollUpdateDelayMs
            )
        }
    }

    private fun cancelBorderScrolling() {
        scrollDragTimer?.cancel()
        scrollDragTimer = null
    }

    private fun updateViewCoordinates() {
        drawableCanvas.setOffset(hscroll.scrollX.toFloat(), vscroll.scrollY.toFloat())
    }

    private fun updateSize() {
        with(hspace) {
            minimumWidth = drawableCanvas.intendedWidth.toInt()
            layoutParams.width = drawableCanvas.intendedWidth.toInt()
        }

        with(vspace) {
            minimumHeight = drawableCanvas.intendedHeight.toInt()
            vspace.layoutParams.height = drawableCanvas.intendedHeight.toInt()
        }
    }

    private inner class BorderScrollUpdateTask: TimerTask() {
        override fun run() {
            currentScrollStepSize *= borderScrollAcceleration

            hscroll.scrollX = hscroll.scrollX + (scrollDirectionX * currentScrollStepSize).toInt()
            vscroll.scrollY = vscroll.scrollY + (scrollDirectionY * currentScrollStepSize).toInt()
        }
    }

    private inner class GestureController : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return drawableCanvas.onSingleTapUp(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return drawableCanvas.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent) {
            drawableCanvas.onLongPress(e)
        }
    }

    companion object {
        val borderScrollMargin = 64
        val initialBorderScrollStepSizeDp = 25f
        val borderScrollAcceleration = 1.04f
        val borderScrollUpdateDelayMs = 40L
    }
}