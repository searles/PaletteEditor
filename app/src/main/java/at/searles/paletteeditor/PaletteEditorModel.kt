package at.searles.paletteeditor

class PaletteEditorModel {
    private val listeners = ArrayList<Listener>()

    var columnCount: Int = 3
        private set(value) {
            field = value
            listeners.forEach { it.onPaletteSizeChanged(this) }
        }
    var rowCount: Int = 3
        private set(value) {
            field = value
            listeners.forEach { it.onPaletteSizeChanged(this) }
        }

    private val colorPoints = HashMap<Pair<Int, Int>, Int>()

    fun colorAt(col: Int, row: Int): Int {
        //if(colorPoints.isEmpty()) {
          //  return neutralColor
        //}

        require(col < columnCount && row < rowCount) {"out of bounds"}

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

    interface Listener {
        fun onPaletteSizeChanged(paletteEditorModel: PaletteEditorModel)
    }

    companion object {
        private val alphaMask = 0xff000000.toInt()
        private val neutralColor = 0
    }
}