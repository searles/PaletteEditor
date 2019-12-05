package at.searles.paletteeditor

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
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

        initializePaletteModel()
        initializeController()

        initializePaletteEditor()
        initializeColorsView()
    }

    private fun initializeController() {
        controller = PaletteEditorController(model, innerPaneView)
    }

    private fun initializeColorsView() {
        colorsView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        colorsView.adapter = ColorsAdapter(
            this
        )
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

        val hControlPane = HorizontalEditTablePane(innerPaneView, palettePane).apply { listener = controller }

        val vControlPane = VerticalEditTablePane(innerPaneView, palettePane).apply { listener = controller }

        innerPaneView.innerPane = CompositionCrossPane(vOffsetPane, hOffsetPane, vControlPane, hControlPane, palettePane)

        // Fix if things are dragged into a different view
        colorsView.setOnDragListener(FallBackDragListener(palettePane))
    }

    private class FallBackDragListener(val palettePane: PaletteEditorPane): View.OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            return when(event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DROP -> palettePane.isDeleteDropAction()
                else -> false
            }
        }
    }
}