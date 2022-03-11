package com.assa.barcodereader.activity.main.weight_list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assa.barcodereader.R
import com.assa.barcodereader.VO.ProductGroupVO

class GroupedProductsAdapter (private val context: Context, val groupedProducts: ArrayList<ProductGroupVO>) : RecyclerView.Adapter<GroupedProductsAdapter.WeightViewHolder>() {
        class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val amount: TextView = itemView.findViewById(R.id.product_left_information)
            val description: TextView = itemView.findViewById(R.id.product_name)
            val weight: TextView = itemView.findViewById(R.id.product_right_information)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
            return WeightViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.weight_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
            if (groupedProducts[position].products.size > 1) {
                holder.amount.text = context.getString(
                    R.string.grouped_amount_total,
                    groupedProducts[position].products.size.toString()
                )
            } else {
                holder.amount.text = context.getString(R.string.grouped_amount_total_singular)
            }
            holder.description.text = groupedProducts[position].description
            holder.weight.text = context.getString(
                R.string.weight_total,
                groupedProducts[position].products.sumOf { p -> p.weight }.toString()
            )
        }

        override fun getItemCount(): Int {
            return groupedProducts.size
        }
    }