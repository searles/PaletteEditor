package at.searles.paletteeditor

import at.searles.multiscrollview.InnerPaneView
import at.searles.paletteeditor.paletteeditorview.*


class PaletteEditorController(private val model: PaletteEditorModel, private val innerPaneView: InnerPaneView): PaletteEditorPane.Listener, VerticalOffsetPane.Listener, HorizontalOffsetPane.Listener, VerticalEditTablePane.Listener, HorizontalEditTablePane.Listener {
    private fun inRange(col: Int, row: Int): Boolean {
        return 0 <= col && col < model.columnCount && 0 <= row && row < model.rowCount
    }

    override fun editColorPointAt(col: Int, row: Int) {
        require(inRange(col, row))
        // TODO Open edit-color dialog.
    }

    override fun removeColorPointAt(col: Int, row: Int) {
        require(inRange(col, row))

        model.removeColorPoint(col, row)
    }

    override fun addColorPointAt(col: Int, row: Int, color: Int) {
        model.setColorPoint(col, row, color)
    }

    override fun activateColorAt(col: Int, row: Int) {
        model.setSelection(col, row)
    }

    override fun deactivateColor() {
        model.setSelection(-1, -1)
    }

    override fun onVerticalOffsetChanged(offset: Float) {
        model.offsetY = offset
    }

    override fun onHorizontalOffsetChanged(offset: Float) {
        model.offsetX = offset
    }

    override fun addColumn() {
        model.columnCount++
    }

    override fun removeRow() {
        if(model.rowCount > 1) {
            model.rowCount--
        }
    }

    override fun addRow() {
        model.rowCount++
    }

    override fun removeColumn() {
        if(model.columnCount > 1) {
            model.columnCount--
        }
    }
}