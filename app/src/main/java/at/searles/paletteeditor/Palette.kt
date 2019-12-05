package at.searles.paletteeditor

import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import at.searles.paletteeditor.colors.Lab

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

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Palette> {
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
                    val colKey = row.keyAt(i)
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