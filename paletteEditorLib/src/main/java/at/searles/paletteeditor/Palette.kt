package at.searles.paletteeditor

import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.core.util.set
import at.searles.paletteeditor.colors.Colors
import at.searles.paletteeditor.colors.Lab
import at.searles.paletteeditor.colors.Rgb
import org.json.JSONArray
import org.json.JSONObject

class Palette(val width: Int, val height: Int, val offsetX: Float, val offsetY: Float, val colorPoints: SparseArray<SparseArray<Lab>>): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readFloat(),
        readColorPointsFromParcel(parcel)
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeFloat(offsetX)
        dest.writeFloat(offsetY)
        writeColorPointsToParcel(colorPoints, dest)
    }

    @Suppress("unused")
    val colorTable: Array<Array<Lab>> by lazy {
        PaletteAdapter(width, height, colorPoints).createColorTable()
    }

    fun createJson(): JSONObject {
        val obj = JSONObject()

        obj.put(widthKey, width)
        obj.put(heightKey, height)
        obj.put(offsetXKey, offsetX)
        obj.put(offsetYKey, offsetY)

        val array = JSONArray()

        colorPoints.forEach { y, row ->
            row.forEach { x, color ->
                array.put(JSONObject().
                    put(xKey, x).
                    put(yKey, y).
                    put(colorKey, Colors.toColorString(color.toRgb().toArgb()))
                )
            }
        }

        obj.put(pointsKey, array)

        return obj
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Palette> {

        const val widthKey = "width"
        const val heightKey = "height"
        const val offsetXKey = "offsetX"
        const val offsetYKey = "offsetY"
        const val pointsKey = "points"
        const val xKey = "x"
        const val yKey = "y"
        const val colorKey = "color"

        fun fromJson(obj: JSONObject): Palette {
            val width = obj.getInt(widthKey)
            val height = obj.getInt(heightKey)
            val offsetX = obj.getDouble(offsetXKey).toFloat()
            val offsetY = obj.getDouble(offsetYKey).toFloat()

            val points = obj.getJSONArray(pointsKey)
            val colorPoints = SparseArray<SparseArray<Lab>>()

            (0 until points.length()).forEach {
                val point = points[it] as JSONObject

                val x = point.get(xKey) as Int
                val y = point.get(yKey) as Int
                val color = Colors.fromColorString(point.get(colorKey).toString())

                val row = colorPoints.get(y, SparseArray())
                row[x] = Rgb.of(color).toLab()
                colorPoints[y] = row
            }

            return Palette(width, height, offsetX, offsetY, colorPoints)
        }

        override fun createFromParcel(parcel: Parcel): Palette {
            return Palette(parcel)
        }

        override fun newArray(size: Int): Array<Palette?> {
            return arrayOfNulls(size)
        }

        private fun readColorPointsFromParcel(parcel: Parcel): SparseArray<SparseArray<Lab>> {
            val palette = SparseArray<SparseArray<Lab>>()

            val size = parcel.readInt()

            repeat(size) {
                val rowKey = parcel.readInt()
                val rowSize = parcel.readInt()

                val row = SparseArray<Lab>()

                repeat(rowSize) {
                    val colKey = parcel.readInt()

                    // 15.7.4 Java Language spec: Left to right evaluation.
                    val lab = Lab(parcel.readFloat(), parcel.readFloat(), parcel.readFloat(), parcel.readFloat())
                    row.put(colKey, lab)
                }

                palette.put(rowKey, row)
            }

            return palette
        }

        private fun writeColorPointsToParcel(points: SparseArray<SparseArray<Lab>>, parcel: Parcel) {
            parcel.writeInt(points.size())

            for(i in 0 until points.size()) {
                val rowKey = points.keyAt(i)
                parcel.writeInt(rowKey)

                val row = points.valueAt(i)
                parcel.writeInt(row.size())

                for(k in 0 until row.size()) {
                    val colKey = row.keyAt(k)
                    parcel.writeInt(colKey)

                    val lab = row.valueAt(k)
                    parcel.writeFloat(lab.l)
                    parcel.writeFloat(lab.a)
                    parcel.writeFloat(lab.b)
                    parcel.writeFloat(lab.alpha)
                }
            }
        }
    }
}