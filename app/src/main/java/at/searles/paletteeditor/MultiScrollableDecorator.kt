//package at.searles.paletteeditor
//
//import android.graphics.Canvas
//import android.view.MotionEvent
//
//// Name: Viewport.
//
//class PaletteOffsetWidthDecorator(val canvasDrawer: CanvasDrawer) { // what kind of pattern is this?
//    val indentX: Int
//        get() = canvasDrawer.iconSize + canvasDrawer.spacing
//
//    val indentY: Int
//        get() = 0
//
//    val width: Int
//        get() = canvasDrawer.intendedWidth - canvasDrawer.iconSize - canvasDrawer.spacing // FIXME careful, if intendedWith in paletteEditorView adds sth this.
//
//    val height: Int
//        get() = canvasDrawer.iconSize + canvasDrawer.spacing
//
//    val offsetPosition: Int
//
//
//    val x0: Int // the point x0 of this view corresponds to this value in real coordinates.
//        get() = canvasDrawer.leftOffset
//
//
//
//    fun isActionInView(evt: MotionEvent): Boolean {
//        val x = evt.x + x0 // relative in x direction
//        val y = evt.y // absolute in y-direction
//
//        return indentY < y && y < height + indentY &&
//                indentX < x && x < width + indentX
//    }
//
//    fun onClick(evt: MotionEvent): Boolean {
//        val x = evt.x + x0 // relative in x direction
//        val newOffset = (x - indentX).toFloat() / width.toFloat()
//
//        // TODO set offset.
//
//        return true
//    }
//
//    fun onScrollTo(evt: MotionEvent): Boolean {
//        val x = evt.x + x0 // relative in x direction
//        val y = evt.y // absolute in y-direction
//
//
//
//        if(x)
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    fun onDraw(canvas: Canvas) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}