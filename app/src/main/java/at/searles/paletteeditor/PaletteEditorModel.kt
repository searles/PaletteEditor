package at.searles.paletteeditor

class PaletteEditorModel {
    private val listeners = ArrayList<Listener>()

    var columnCount: Int = 11110
        private set
    var rowCount: Int = 11110
        private set

    private val colorPoints = HashMap<Pair<Int, Int>, Int>()

    fun colorAt(col: Int, row: Int): Int {
        if(colorPoints.isEmpty()) {
            //return neutralColor
        }

        // TODO must interpolate.
        return alphaMask or (col * row)
    }

    fun isColorPoint(col: Int, row: Int): Boolean {
        return colorPoints.contains(Pair(col, row))
    }

    fun setColorPoint(col: Int, row: Int, rgb: Int) {
        val argb = if((rgb and alphaMask) == 0) alphaMask or rgb else rgb

        colorPoints[Pair(col, row)] = argb
    }

    fun removeColorPoint(col: Int, row: Int) {
        colorPoints.remove(Pair(col, row))
    }

    fun addRow() {
        rowCount++
    }

    fun removeRow() {
        rowCount--
    }

    fun addColumn() {
        columnCount++
    }

    fun removeColumn() {
        columnCount--
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    interface Listener

    companion object {
        private val alphaMask = 0xff000000.toInt()
        private val neutralColor = 0
    }
}