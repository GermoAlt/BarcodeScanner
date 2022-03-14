package com.assa.barcodereader.VO

import com.assa.barcodereader.entity.Product
import java.math.BigDecimal

class ProductGroupVO(
    var description: String,
    var products: ArrayList<Product> = arrayListOf(),
    var amount: Int = products.size,
    var totalWeight: BigDecimal = products.sumOf { p -> p.weight }
) {
    constructor(product: Product) : this(product.description, arrayListOf(product))

    fun deleteProduct(p: Product): Int {
        val productToDelete = products.find { product: Product ->  product.barcodeNumber == p.barcodeNumber}
        products.remove(productToDelete)
        return products.size
    }
}