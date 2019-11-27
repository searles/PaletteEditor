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

/**
 * Thanks to http://stackoverflow.com/questions/12074950/android-horizontalscrollview-inside-scrollview
 */
class MultiScrollView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    var borderScrollMargin = 128f
    var initialBorderScrollStepSize = 24f
    var maxBorderScrollStepSize = 240f
    var borderScrollUpdateDelayMs = 40L
    var borderScrollAcceleration = 1.05f

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

        post {
            updateSize()
            updateViewCoordinates()
        }
    }

    private var currentScrollStepSize = 0f

    private var scrollDirectionX = 0f
    private var scrollDirectionY = 0f

    private var scrollDragTimer: Timer? = null

    public override fun onDraw(canvas: Canvas) {
        drawableCanvas.onDraw(canvas)
    }

    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_MOVE) {
            if(drawableCanvas.onScrollTo(e)) {
                if(isWithinBorderScrollingMargin(e)) {
                    updateBorderScrolling(e)
                    return true
                } else {
                    cancelBorderScrolling()
                }

                return true
            }
        } else if (e.action == MotionEvent.ACTION_UP) {
            cancelBorderScrolling()
            if (drawableCanvas.onTapUp(e)) {
                return true
            }
        }

        return gestureDetector.onTouchEvent(e)
                || (hscroll.dispatchTouchEvent(e) or vscroll.dispatchTouchEvent(e))
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

        if(scrollDragTimer == null) {
            scrollDragTimer = Timer()
            currentScrollStepSize = initialBorderScrollStepSize

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
        drawableCanvas.setOffset(hscroll.scrollX, vscroll.scrollY)
    }

    private fun updateSize() {
        with(hspace) {
            minimumWidth = drawableCanvas.intendedWidth
            layoutParams.width = drawableCanvas.intendedWidth
        }

        with(vspace) {
            minimumHeight = drawableCanvas.intendedHeight
            vspace.layoutParams.height = drawableCanvas.intendedHeight
        }
    }

    private inner class BorderScrollUpdateTask: TimerTask() {
        override fun run() {
            if (currentScrollStepSize < maxBorderScrollStepSize) {
                currentScrollStepSize *= borderScrollAcceleration
            }

            hscroll.scrollX = hscroll.scrollX + (scrollDirectionX * currentScrollStepSize).toInt()
            vscroll.scrollY = vscroll.scrollY + (scrollDirectionY * currentScrollStepSize).toInt()
        }
    }

    private inner class GestureController : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return drawableCanvas.onClick(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return drawableCanvas.onDoubleClick(e)
        }

        override fun onLongPress(e: MotionEvent) {
            drawableCanvas.onLongPress(e)
        }
    }
}