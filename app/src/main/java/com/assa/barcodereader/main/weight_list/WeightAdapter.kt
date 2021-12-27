package com.assa.barcodereader.main.weight_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assa.barcodereader.R

class WeightAdapter(private val context: Context, val weights: List<String>) : RecyclerView.Adapter<WeightAdapter.WeightViewHolder>() {
    class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weightValue: TextView = itemView.findViewById(R.id.weight_item_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        return WeightViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.weight_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        holder.weightValue.text = weights[position]
    }

    override fun getItemCount(): Int {
        return weights.size
    }
}