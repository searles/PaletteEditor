package at.searles.paletteeditor

class PaletteEditorController(val model: PaletteEditorModel, val view: PaletteEditorView): PaletteEditorView.Listener {

    private var selectedRow = -1
    private var selectedCol = -1

    init {
        if(view.model != model) {
            view.model = model
            view.invalidate()
        }

        view.addListener(this)
    }

    private object ViewListener: PaletteEditorView.Listener
}