package at.searles.paletteeditor

import android.content.Context
import android.widget.ImageView
import at.searles.android.storage.data.PathContentProvider
//import com.bumptech.glide.Glide

class PaletteFilesProvider(context: Context): PathContentProvider(context.getDir(directoryName, 0)) {
    override fun setImageInView(name: String, imageView: ImageView) {
        /*Glide
                .with(imageView.context)
                .load(R.drawable.ic_text_24dp)
                .centerCrop()
                .into(imageView)*/
        // TODO Show palette in small view.
    }

    companion object {
        private const val directoryName = "palettes"
    }
}