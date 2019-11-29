package at.searles.multiscrollview

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
import at.searles.paletteeditor.R
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

    private val innerPaneView: InnerPaneView by lazy {
        findViewById<View>(R.id.innerPaneView) as InnerPaneView
    }

    init {
        View.inflate(context,
            R.layout.multiscroll_view, this)

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
        innerPaneView.draw(canvas)
    }

    override fun dispatchTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_MOVE) {
            if(innerPaneView.onScrollTo(e)) {
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
            if (innerPaneView.onTapUp(e)) {
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
        innerPaneView.visibleX0 = hscroll.scrollX
        innerPaneView.visibleY0 = vscroll.scrollY
    }

    private fun updateSize() {
        with(hspace) {
            minimumWidth = innerPaneView.intendedWidth
            layoutParams.width = innerPaneView.intendedWidth
        }

        with(vspace) {
            minimumHeight = innerPaneView.intendedHeight
            vspace.layoutParams.height = innerPaneView.intendedHeight
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
            return innerPaneView.onClick(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return innerPaneView.onDoubleClick(e)
        }

        override fun onLongPress(e: MotionEvent) {
            innerPaneView.onLongPress(e)
        }
    }
}