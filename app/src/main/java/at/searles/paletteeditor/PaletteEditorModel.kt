package at.searles.paletteeditor

import android.graphics.Color
import android.util.SparseArray
import androidx.core.util.containsKey
import at.searles.paletteeditor.colors.Lab
import at.searles.paletteeditor.colors.Rgb
import kotlin.math.*

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

    private val colorList = ArrayList<Int>()

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
        colorList.clear()

        for(row in 0 until rowCount) {
            for(col in 0 until columnCount) {
                colorList.add(calculateColorAt(col, row))
            }
        }
    }

    private fun calculateColorAt(col: Int, row: Int): Int {
        if(isColorPoint(col, row)) {
            return colorPoints.get(row).get(col).toRgb().toArgb()
        }

        var sumWeights = 0.0
        var l = 0.0
        var a = 0.0
        var b = 0.0
        var alpha = 0.0

        for(rowIndex in 0 until colorPoints.size()) {
            val y = colorPoints.keyAt(rowIndex)

            if(y >= rowCount) continue

            val rowValues = colorPoints.valueAt(rowIndex)

            for(colIndex in 0 until rowValues.size()) {
                val x = rowValues.keyAt(colIndex)

                if(x >= columnCount) continue

                val lab = rowValues.valueAt(colIndex)

                val weight = getWeight(x, y, col, row)
                sumWeights += weight

                alpha += lab.alpha * weight
                l += lab.l * weight
                a += lab.a * weight
                b += lab.b * weight
            }
        }

        return Lab((l / sumWeights).toFloat(), (a / sumWeights).toFloat(), (b / sumWeights).toFloat(), (alpha / sumWeights).toFloat()).toRgb().toArgb()
    }

    private fun getWeight(ptX: Int, ptY: Int, col: Int, row: Int): Double {
        // The weight uses a round-about.
        var weight = 0.0

        (-1..1).forEach { yAdd ->
            (-1..1).forEach { xAdd ->
                weight += getSimpleWeight(ptX + columnCount * xAdd, ptY + rowCount * yAdd, col, row)
            }
        }

        return weight
    }

    private fun getSimpleWeight(ptX: Int, ptY: Int, col: Int, row: Int): Double {
        val dx = (ptX - col).toDouble() / columnCount
        val dy = (ptY - row).toDouble() / rowCount

        if(dx < -1.0 || 1.0 <= dx || dy < -1.0 || 1.0 <= dy) {
            return 0.0
        }

        val dist2 = (dx * dx + dy * dy)

        return 1.0 / (exp(weightExpFactor * dist2) - 1)
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

    interface Listener {
        fun onPaletteSizeChanged()
        fun onOffsetChanged()
        fun onColorsChanged()
        fun onSelectionChanged()
    }

    companion object {
        private const val alphaMask = 0xff000000.toInt()
        private const val weightExpFactor = 12.0
    }
}