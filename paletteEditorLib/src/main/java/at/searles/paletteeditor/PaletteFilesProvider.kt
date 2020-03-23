package at.searles.paletteeditor

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.set
import at.searles.android.storage.data.PathContentProvider
import at.searles.commons.color.Palette
import org.json.JSONObject
import java.util.*

class PaletteFilesProvider(context: Context): PathContentProvider(context.getDir(directoryName, 0)) {

    private val cache = WeakHashMap<String, Bitmap>()

    override fun setImageInView(name: String, imageView: ImageView) {
        try {
            val paletteBitmap = cache[name] ?: loadPaletteBitmap(name)
            imageView.setImageBitmap(paletteBitmap)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
        } catch(e: Exception) {
            Log.e("PaletteFilesProvider", "Error trying to create palette icon")
            e.printStackTrace()
        }
    }

    private fun loadPaletteBitmap(name: String): Bitmap? {
        var bm: Bitmap? = null

        load(name) {
            val palette = PaletteAdapter.toPalette(JSONObject(it))
            bm = Bitmap.createBitmap(palette.width, palette.height, Bitmap.Config.ARGB_8888)

            repeat(palette.height) { y ->
                repeat(palette.width) { x->
                    bm!![x, y] = palette.colorTable[y][x].toRgb().toArgb()
                }
            }

            cache[name] = bm!!
        }

        return bm
    }

    companion object {
        private const val directoryName = "palettes"
    }
}