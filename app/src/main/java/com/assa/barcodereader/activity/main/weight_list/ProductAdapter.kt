package com.assa.barcodereader.activity.main.weight_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.assa.barcodereader.R
import com.assa.barcodereader.entity.Product
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper

class ProductAdapter(val products: ArrayList<Product>, val notifyDeletedItem: (p:Product) -> Unit)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val swipeRevealLayout: SwipeRevealLayout = itemView.findViewById(R.id.swipe_layout)
        val deleteButton: ImageButton = itemView.findViewById(R.id.imageButton)
        val barcode: TextView = itemView.findViewById(R.id.product_left_information)
        val description: TextView = itemView.findViewById(R.id.product_name)
        val weight: TextView = itemView.findViewById(R.id.product_right_information)

        fun bind(product: Product){
            barcode.text = itemView.context.getString(
                R.string.barcode_display,
                product.barcodeNumber
            )
            description.text = product.description
            weight.text = itemView.context.getString(
                R.string.weight_total,
                product.weight.toString()
            )
            deleteButton.setOnClickListener {
                products.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyDeletedItem(product)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val viewHolder = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_list_item, parent, false)
        return ProductViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        viewBinderHelper.bind(
            holder.swipeRevealLayout,
            products[position].barcodeNumber + position
        )
        viewBinderHelper.closeLayout(products[position].barcodeNumber + position)
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }
}