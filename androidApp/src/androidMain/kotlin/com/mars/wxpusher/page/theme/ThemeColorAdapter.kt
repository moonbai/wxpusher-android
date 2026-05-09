package com.mars.wxpusher.page.theme

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mars.wxpusher.R

class ThemeColorAdapter(
    private val colors: List<Int>,
    private val onColorSelected: (Int) -> Unit
) : RecyclerView.Adapter<ThemeColorAdapter.ViewHolder>() {

    private var selectedColor: Int = colors[0]

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorCircle: View = view.findViewById(R.id.colorCircle)
        val ivCheck: ImageView = view.findViewById(R.id.ivCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_theme_color, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = colors[position]

        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(color)
        holder.colorCircle.background = drawable

        holder.ivCheck.visibility = if (color == selectedColor) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val oldPosition = colors.indexOf(selectedColor)
            selectedColor = color
            notifyItemChanged(oldPosition)
            notifyItemChanged(position)
            onColorSelected(color)
        }
    }

    override fun getItemCount() = colors.size

    fun setSelectedColor(color: Int) {
        if (colors.contains(color)) {
            selectedColor = color
            notifyDataSetChanged()
        }
    }
}
