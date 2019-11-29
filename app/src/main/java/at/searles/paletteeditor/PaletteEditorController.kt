package at.searles.paletteeditor

class PaletteEditorController(val model: PaletteEditorModel, val pane: PaletteEditorPane): PaletteEditorPane.Listener {

    private var selectedRow = -1
    private var selectedCol = -1

    init {
        if(pane.model != model) {
            pane.model = model
        }

        pane.addListener(this)
    }

    private object ViewListener: PaletteEditorPane.Listener
}