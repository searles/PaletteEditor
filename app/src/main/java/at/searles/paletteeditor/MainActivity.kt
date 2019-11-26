package at.searles.paletteeditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.android.recyclerplayground.layout.FixedGridLayoutManager

class MainActivity : AppCompatActivity() {

    private val paletteEditorView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.paletteEditorView)
    }

    private val colorsView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.colorsView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        paletteEditorView.layoutManager = FixedGridLayoutManager().apply {
        }


        paletteEditorView.adapter = PaletteEditorAdapter(this)
    }
}
