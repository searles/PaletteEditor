package at.searles.paletteeditor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class PaletteEditorAdapter(context: Context) : RecyclerView.Adapter<PaletteEditorAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)    }

    override fun getItemCount(): Int {
        return 250
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = "$position"
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.textView)
    }
}
