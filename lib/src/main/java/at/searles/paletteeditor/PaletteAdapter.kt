package at.searles.paletteeditor

import android.util.SparseArray
import androidx.core.util.containsKey
import at.searles.paletteeditor.colors.Lab
import kotlin.math.exp

class PaletteAdapter(val width: Int, val height: Int, private val colorPoints: SparseArray<SparseArray<Lab>>) {
    fun createColorTable(): Array<Array<Lab>> {
        return Array(height) {y ->
            Array(width) {x ->
                calculateColorAt(x, y)
            }
        }
    }

    private fun isColorPoint(x: Int, y: Int): Boolean {
        return colorPoints.containsKey(y) && colorPoints.get(y).containsKey(x)
    }

    private fun calculateColorAt(x: Int, y: Int): Lab {
        if(isColorPoint(x, y)) {
            return colorPoints.get(y).get(x)
        }

        var sumWeights = 0.0
        var l = 0.0
        var a = 0.0
        var b = 0.0
        var alpha = 0.0

        for(yIndex in 0 until colorPoints.size()) {
            val yKey = colorPoints.keyAt(yIndex)

            if(yKey >= height) continue

            val rowValues = colorPoints.valueAt(yIndex)

            for(xIndex in 0 until rowValues.size()) {
                val xKey = rowValues.keyAt(xIndex)

                if(xKey >= width) continue

                val lab = rowValues.valueAt(xIndex)

                val weight = getWeight(xKey, yKey, x, y)
                sumWeights += weight

                alpha += lab.alpha * weight
                l += lab.l * weight
                a += lab.a * weight
                b += lab.b * weight
            }
        }

        return Lab((l / sumWeights).toFloat(), (a / sumWeights).toFloat(), (b / sumWeights).toFloat(), (alpha / sumWeights).toFloat())
    }

    private fun getWeight(ptX: Int, ptY: Int, col: Int, row: Int): Double {
        // The weight uses a round-about.
        var weight = 0.0

        (-1..1).forEach { yAdd ->
            (-1..1).forEach { xAdd ->
                weight += getSimpleWeight(ptX + width * xAdd, ptY + height * yAdd, col, row)
            }
        }

        return weight
    }

    private fun getSimpleWeight(ptX: Int, ptY: Int, col: Int, row: Int): Double {
        val dx = (ptX - col).toDouble() / width
        val dy = (ptY - row).toDouble() / height

        if(dx < -1.0 || 1.0 <= dx || dy < -1.0 || 1.0 <= dy) {
            return 0.0
        }

        val dist2 = (dx * dx + dy * dy)

        return 1.0 / (exp(weightExpFactor * dist2) - 1)
    }

    companion object {
        private const val weightExpFactor = 12.0
    }
}