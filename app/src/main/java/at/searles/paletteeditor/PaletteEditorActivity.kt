package at.searles.paletteeditor

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.searles.multiscrollview.CompositionCrossPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.paletteeditor.colorsview.ColorsAdapter
import at.searles.paletteeditor.paletteeditorview.*
import kotlinx.android.synthetic.main.activity_main.*

class PaletteEditorActivity : AppCompatActivity() {

    private val colorsView by lazy {
        findViewById<RecyclerView>(R.id.colorsView)
    }

    private val innerPaneView by lazy {
        findViewById<InnerPaneView>(R.id.innerPaneView)
    }

    private lateinit var model: PaletteEditorModel
    private lateinit var palettePane: PaletteEditorPane
    private lateinit var controller: PaletteEditorController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializePaletteModel()
        initializeController()

        initializePaletteEditor()
        initializeColorsView()
    }

    private fun initializeController() {
        controller = PaletteEditorController(model)
    }

    private fun initializeColorsView() {
        colorsView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        colorsView.adapter = ColorsAdapter(
            this
        ).apply { listener = controller }
    }

    private fun initializePaletteModel() {
        model = PaletteEditorModel().apply {
            columnCount = 8
            rowCount = 8

            setColorPoint(2, 2, Color.RED)
            setColorPoint(0, 2, Color.GREEN)
            setColorPoint(6, 5, Color.YELLOW)
            setColorPoint(4, 0, Color.BLACK)
            setColorPoint(3, 6, Color.BLUE)
            setColorPoint(5, 1, Color.CYAN)
            setColorPoint(1, 4, Color.WHITE)
            setColorPoint(7, 7, Color.MAGENTA)
        }

        model.addListener(object: PaletteEditorModel.Listener {
            override fun onPaletteSizeChanged() {
                multiScrollView.updateSize()
                innerPaneView.invalidate()
            }

            override fun onOffsetChanged() {
                innerPaneView.invalidate()
            }

            override fun onColorsChanged() {
                innerPaneView.invalidate()
            }

            override fun onSelectionChanged() {
                innerPaneView.invalidate()
            }
        })
    }

    private fun initializePaletteEditor() {
        palettePane = PaletteEditorPane(innerPaneView, model).apply { listener = controller }

        val hOffsetPane = HorizontalOffsetPane(innerPaneView, palettePane).apply { listener = controller }

        val vOffsetPane = VerticalOffsetPane(innerPaneView, palettePane).apply { listener = controller }

        val hControlPane = HorizontalControlPane(innerPaneView, palettePane).apply { listener = controller }

        val vControlPane = VerticalControlPane(innerPaneView, palettePane).apply { listener = controller }

        innerPaneView.innerPane = CompositionCrossPane(vOffsetPane, hOffsetPane, vControlPane, hControlPane, palettePane)
    }
}
