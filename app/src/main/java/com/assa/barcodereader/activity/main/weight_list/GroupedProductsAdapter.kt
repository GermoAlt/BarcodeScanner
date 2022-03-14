package com.assa.barcodereader.activity.main.weight_list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assa.barcodereader.R
import com.assa.barcodereader.VO.ProductGroupVO
import com.assa.barcodereader.entity.Product

class GroupedProductsAdapter (private val context: Context, val groupedProducts: ArrayList<ProductGroupVO>) : RecyclerView.Adapter<GroupedProductsAdapter.WeightViewHolder>() {
        class WeightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val amount: TextView = itemView.findViewById(R.id.product_group_left_information)
            val description: TextView = itemView.findViewById(R.id.product_group_name)
            val weight: TextView = itemView.findViewById(R.id.product_group_right_information)
            var positionOfGroup = adapterPosition
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
            return WeightViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.grouped_list_item, parent, false)
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

    @SuppressLint("NotifyDataSetChanged")
    fun handleProductDeletion(product: Product) {
        val group = groupedProducts.find { pg -> pg.description == product.description }!!
        val newSize = group.deleteProduct(product)
        if(newSize == 0) {
            groupedProducts.remove(group)
        }
        notifyDataSetChanged()
    }
}