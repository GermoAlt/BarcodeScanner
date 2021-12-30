package com.assa.barcodereader.activity.main.weight_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assa.barcodereader.R
import com.assa.barcodereader.entity.Product

class ProductAdapter(private val context: Context, val weights: ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.WeightViewHolder>() {
    class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val barcode: TextView = itemView.findViewById(R.id.product_barcode)
        val description: TextView = itemView.findViewById(R.id.product_name)
        val weight: TextView = itemView.findViewById(R.id.product_weight)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        return WeightViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.weight_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        holder.barcode.text = weights[position].barcodeNumber
        holder.description.text = weights[position].description
        holder.weight.text = context.getString(
            R.string.weight_total,
            weights[position].weight.toString()
        )
    }

    override fun getItemCount(): Int {
        return weights.size
    }
}