package at.searles.paletteeditor

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.searles.android.storage.OpenSaveActivity
import at.searles.android.storage.data.PathContentProvider
import at.searles.multiscrollview.CompositionCrossPane
import at.searles.multiscrollview.InnerPaneView
import at.searles.multiscrollview.MultiScrollView
import at.searles.paletteeditor.colorsview.ColorsAdapter
import at.searles.paletteeditor.paletteeditorview.*
import org.json.JSONObject

class PaletteEditorActivity : OpenSaveActivity() {

    // TODO: Check what happens if there is a bad format
    override var contentString: String
        get() = model.createPalette().createJson().toString(4)
        set(value) { model.restoreFromPalette(Palette.fromJson(JSONObject(value))) }

    override val provider: PathContentProvider by lazy {
        PaletteFilesProvider(this)
    }

    override fun createReturnIntent(): Intent {
        return Intent().apply {
            putExtra(paletteKey, model.createPalette())
        }
    }

    override val fileNameEditor: EditText by lazy {
        findViewById<EditText>(R.id.nameEditText)
    }

    override val saveButton: Button by lazy {
        findViewById<Button>(R.id.saveButton)
    }

    override val storageActivityTitle: String
        get() = getString(R.string.openPalette)

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
        setContentView(R.layout.palette_editor_activity_main)

        val palette = if (savedInstanceState != null) {
            savedInstanceState.getParcelable<Palette>(paletteKey)
        } else {
            intent.getParcelableExtra(paletteKey)
        }

        initializePaletteModel(palette)
        initializeController()
        initializePaletteEditor()
        initializeColorsView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(paletteKey, model.createPalette())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.palette_editor_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.openStorageAction -> {
                startStorageActivity()
                true
            }
            R.id.returnAction -> {
                finishAndReturnContent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeController() {
        controller = PaletteEditorController(model)
    }

    private fun initializeColorsView() {
        val orientation = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayout.HORIZONTAL
        } else {
            LinearLayout.VERTICAL
        }

        colorsView.layoutManager = LinearLayoutManager(this, orientation, false)
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
                contentChanged()
            }

            override fun onOffsetChanged() {
                innerPaneView.invalidate()
                contentChanged()
            }

            override fun onColorsChanged() {
                innerPaneView.invalidate()
                contentChanged()
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
