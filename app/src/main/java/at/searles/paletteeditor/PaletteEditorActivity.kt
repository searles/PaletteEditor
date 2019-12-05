package at.searles.paletteeditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.searles.multiscrollview.CompositionCrossPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.MultiScrollView
import at.searles.paletteeditor.colorsview.ColorsAdapter
import at.searles.paletteeditor.paletteeditorview.*

class PaletteEditorActivity : AppCompatActivity() {

    private val colorsView by lazy {
        findViewById<RecyclerView>(R.id.colorsView)
    }

    private val multiScrollView by lazy {
        findViewById<MultiScrollView>(R.id.multiScrollView)
    }

    private val innerPaneView by lazy {
        multiScrollView.findViewById<InnerPaneView>(R.id.innerPaneView)
    }

    private lateinit var model: PaletteEditorModel
    private lateinit var palettePane: PaletteEditorPane
    private lateinit var controller: PaletteEditorController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializePaletteModel(savedInstanceState?.getParcelable(paletteKey))
        initializeController()

        initializePaletteEditor()
        initializeColorsView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(paletteKey, model.createPalette())
    }

    private fun initializeController() {
        controller = PaletteEditorController(model)
    }

    private fun initializeColorsView() {
        colorsView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        colorsView.adapter = ColorsAdapter(
            this
        )
    }

    private fun initializePaletteModel(palette: Palette?) {
        model = PaletteEditorModel()

        if(palette != null) {
            model.restoreFromPalette(palette)
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

        val hControlPane = HorizontalEditTablePane(innerPaneView, palettePane).apply { listener = controller }

        val vControlPane = VerticalEditTablePane(innerPaneView, palettePane).apply { listener = controller }

        innerPaneView.innerPane = CompositionCrossPane(vOffsetPane, hOffsetPane, vControlPane, hControlPane, palettePane)
    }

    companion object {
        const val paletteKey = "palette"
    }
}
