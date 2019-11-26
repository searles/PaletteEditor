package at.searles.paletteeditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val multiScrollView by lazy {
        findViewById<MultiScrollView>(R.id.multiScrollView)
    }

    private val colorsView by lazy {
        findViewById<RecyclerView>(R.id.colorsView)
    }

    private val paletteView by lazy {
        findViewById<PaletteEditorView>(R.id.paletteView)
    }

     override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

         colorsView.adapter = ColorsAdapter()
    }
}
