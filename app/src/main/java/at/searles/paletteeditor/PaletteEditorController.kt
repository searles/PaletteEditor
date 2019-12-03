package at.searles.paletteeditor

import android.util.Log
import at.searles.paletteeditor.colorsview.ColorsAdapter
import at.searles.paletteeditor.paletteeditorview.*

class PaletteEditorController(private val model: PaletteEditorModel): PaletteEditorPane.Listener, VerticalOffsetPane.Listener, HorizontalOffsetPane.Listener, VerticalControlPane.Listener, HorizontalControlPane.Listener, ColorsAdapter.Listener {
    private fun inRange(col: Int, row: Int): Boolean {
        return 0 <= col && col < model.columnCount && 0 <= row && row < model.rowCount
    }

    override fun onColorClicked(col: Int, row: Int) {
        require(inRange(col, row))
        model.setSelection(col, row)
    }

    override fun onColorDoubleClicked(col: Int, row: Int) {
        require(inRange(col, row))
        // FIXME What to do in this case?
    }

    override fun onColorDraggedTo(fromCol: Int, fromRow: Int, toCol: Int, toRow: Int) {
        require(inRange(fromCol, fromRow))

        model.setSelection(toCol, toRow)

        if(fromCol == toCol && fromRow == toRow) {
            return
        }

        if(inRange(toCol, toRow)) {
            model.setColorPoint(toCol, toRow, model.colorAt(fromCol, fromRow))
        }

        if(model.isColorPoint(fromCol, fromRow)) {
            model.removeColorPoint(fromCol, fromRow)
        }
    }

    override fun onColorActivated(toCol: Int, toRow: Int) {
        model.setSelection(toCol, toRow)
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

    override fun onColorPicked(color: Int) {
        if(inRange(model.selectedCol, model.selectedRow)) {
            model.setColorPoint(model.selectedCol, model.selectedRow, color)
        } else {
            Log.i(javaClass.simpleName, "Could not set color because selection is out of range: ${model.selectedCol} x ${model.selectedRow}")
        }
    }
}