package at.searles.paletteeditor

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.MultiScrollView

class MainActivity : AppCompatActivity() {

    private val multiScrollView by lazy {
        findViewById<MultiScrollView>(R.id.multiScrollView)
    }

    private val colorsView by lazy {
        findViewById<RecyclerView>(R.id.colorsView)
    }

    private val innerPaneView by lazy {
        findViewById<InnerPaneView>(R.id.innerPaneView)
    }

    private lateinit var palettePane: PaletteEditorPane

    private lateinit var model: PaletteEditorModel
    private lateinit var controller: PaletteEditorController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorsView.adapter = ColorsAdapter()

        val scale = 20

        palettePane = PaletteEditorPane(innerPaneView).apply {
            iconSize = 8
        }

        innerPaneView.innerPane = palettePane


//        model = PaletteEditorModel().apply {
//            columnCount = 10 * scale
//            rowCount = 10 * scale
//
//            setColorPoint(2 * scale, 2 * scale, Color.RED)
//            setColorPoint(8 * scale, 2 * scale, Color.GREEN)
//            setColorPoint(6 * scale, 5 * scale, Color.YELLOW)
//            setColorPoint(4 * scale, 8 * scale, Color.BLACK)
//            setColorPoint(3 * scale, 6 * scale, Color.BLUE)
//            setColorPoint(5 * scale, 1 * scale, Color.CYAN)
//            setColorPoint(1 * scale, 4 * scale, Color.WHITE)
//            setColorPoint(7 * scale, 7 * scale, Color.MAGENTA)
//        }

        model = PaletteEditorModel().apply {
            columnCount = 10 * scale
            rowCount = 10 * scale

            setColorPoint(2 * scale, 2 * scale, Color.RED)
            setColorPoint(8 * scale, 2 * scale, Color.GREEN)
            setColorPoint(6 * scale, 2 * scale, Color.YELLOW)
            setColorPoint(4 * scale, 2 * scale, Color.BLACK)
            setColorPoint(3 * scale, 2 * scale, Color.BLUE)
            setColorPoint(5 * scale, 2 * scale, Color.CYAN)
            setColorPoint(1 * scale, 2 * scale, Color.WHITE)
            setColorPoint(9 * scale, 2 * scale, Color.MAGENTA)
            setColorPoint(7 * scale, 2 * scale, Color.GRAY)
        }

        controller = PaletteEditorController(model, palettePane)
    }
}
