package at.searles.paletteeditor

import android.util.SparseArray
import androidx.core.util.containsKey
import androidx.core.util.forEach
import at.searles.paletteeditor.colors.Lab
import at.searles.paletteeditor.colors.Rgb
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

    private val colorPoints = SparseArray<SparseArray<Lab>>() // [row][col]

    var offsetX: Float = 0f
        set(value) {
            field = max(0f, min(value, 1f))
            listeners.forEach { it.onOffsetChanged() }
        }

    var offsetY: Float = 0f
        set(value) {
            field = max(0f, min(value, 1f))
            listeners.forEach { it.onOffsetChanged() }
        }

    var selectedRow: Int = -1
        private set
    var selectedCol: Int = -1
        private set

    private var colorList: List<Int> = listOf(0)

    fun colorAt(col: Int, row: Int): Int {
        require(col < columnCount && row < rowCount) {"out of bounds"}
        return colorList[row * columnCount + col]
    }

    fun setSelection(col: Int, row: Int) {
        selectedCol = col
        selectedRow = row

        listeners.forEach { it.onSelectionChanged() }
    }

    private fun updateInterpolatedColors() {
        colorList = PaletteAdapter(columnCount, rowCount, colorPoints).createColorArray().map { it.toRgb().toArgb() }
    }

    fun isColorPoint(col: Int, row: Int): Boolean {
        return colorPoints.containsKey(row) && colorPoints.get(row).containsKey(col)
    }

    fun setColorPoint(col: Int, row: Int, rgb: Int) {
        require(col >= 0 && row >= 0)

        val argb = if((rgb and alphaMask) == 0) alphaMask or rgb else rgb

        var rowValues = colorPoints.get(row)

        if(rowValues == null) {
            rowValues = SparseArray()
            colorPoints.put(row, rowValues)
        }

        rowValues.put(col, Rgb.of(argb).toLab())
        updateInterpolatedColors()
        listeners.forEach { it.onColorsChanged() }
    }

    fun removeColorPoint(col: Int, row: Int) {
        if(isColorPoint(col, row)) {
            colorPoints.get(row).remove(col)
            updateInterpolatedColors()
            listeners.forEach { it.onColorsChanged() }
        }
    }

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

        palette.colorPoints.forEach { rowKey, row ->
            colorPoints.put(rowKey, row.clone())
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