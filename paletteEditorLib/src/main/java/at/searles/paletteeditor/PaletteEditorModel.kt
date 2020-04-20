package at.searles.paletteeditor

import at.searles.commons.color.Lab
import at.searles.commons.color.Palette
import at.searles.commons.color.Rgb
import at.searles.commons.util.IntIntMap
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class PaletteEditorModel {
    private val listeners = ArrayList<Listener>()

    var columnCount: Int = 1
        set(value) {
            require(value >= 1)
            field = value
            updateInterpolatedColors()
            listeners.forEach { it.onPaletteSizeChanged() }
        }
    var rowCount: Int = 1
        set(value) {
            require(value >= 1)
            field = value
            updateInterpolatedColors()
            listeners.forEach { it.onPaletteSizeChanged() }
        }

    private val colorPoints = IntIntMap<Lab>() // [row][col]

    var offsetX: Float = 0f
        set(value) {
            field = value - floor(value)
            listeners.forEach { it.onOffsetChanged() }
        }

    var offsetY: Float = 0f
        set(value) {
            field = value - floor(value)
            listeners.forEach { it.onOffsetChanged() }
        }

    var selectedRow: Int = -1
        private set
    var selectedCol: Int = -1
        private set

    private var colorTable: Array<IntArray> = Array(1) { IntArray(1) { 0 } }

    fun colorAt(col: Int, row: Int): Int {
        require(col < columnCount && row < rowCount) {"out of bounds"}
        return colorTable[row][col]
    }

    fun setSelection(col: Int, row: Int) {
        selectedCol = col
        selectedRow = row

        listeners.forEach { it.onSelectionChanged() }
    }

    private fun updateInterpolatedColors() {
        val labColorTable = Palette(columnCount, rowCount, 0f, 0f, colorPoints).colorTable

        colorTable = Array(labColorTable.size) {y ->
            IntArray(labColorTable[y].size) {x ->
                labColorTable[y][x].toRgb().toArgb()
            }
        }
    }

    fun isColorPoint(col: Int, row: Int): Boolean {
        return colorPoints.containsKey(col, row)
    }

    fun setColorPoint(col: Int, row: Int, rgb: Int) {
        require(col >= 0 && row >= 0)

        val argb = if((rgb and alphaMask) == 0) alphaMask or rgb else rgb

        colorPoints[col, row] = Rgb.of(argb).toLab()

        updateInterpolatedColors()
        listeners.forEach { it.onColorsChanged() }
    }

    fun removeColorPoint(col: Int, row: Int) {
        if(isColorPoint(col, row)) {
            colorPoints.remove(col, row)
            updateInterpolatedColors()
            listeners.forEach { it.onColorsChanged() }
        }
    }

    @Suppress("unused")
    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun createPalette(): Palette {
        return Palette(columnCount, rowCount, offsetX, offsetY, colorPoints)
    }

    fun restoreFromPalette(palette: Palette) {
        colorPoints.clear()

        columnCount = palette.width
        rowCount = palette.height
        offsetX = palette.offsetX
        offsetY = palette.offsetY

        palette.colorPoints.forEach { entry ->
            colorPoints[entry.x, entry.y] = entry.value
        }

        updateInterpolatedColors()
    }

    interface Listener {
        fun onPaletteSizeChanged()
        fun onOffsetChanged()
        fun onColorsChanged()
        fun onSelectionChanged()
    }

    companion object {
        private const val alphaMask = 0xff000000.toInt()
    }
}