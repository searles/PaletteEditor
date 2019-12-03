package at.searles.paletteeditor.colorsview

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import at.searles.paletteeditor.R
import at.searles.paletteeditor.colors.DefaultColors

class ColorsAdapter(private val context: Context) : RecyclerView.Adapter<ColorsAdapter.ViewHolder>() {
    lateinit var listener: Listener

    val colors = DefaultColors.colors

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.colored_button, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return colors.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val button = itemView.findViewById<Button>(R.id.coloredButton).apply {
            setOnClickListener { listener.onColorPicked(colors[adapterPosition]) }
        }

        fun bindTo(position: Int) {
            button.backgroundTintList = ColorStateList.valueOf(colors[position])
        }
    }

    interface Listener {
        fun onColorPicked(color: Int)
    }
}
