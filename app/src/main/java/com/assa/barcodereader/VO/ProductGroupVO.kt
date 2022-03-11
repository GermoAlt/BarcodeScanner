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

    fun hasProduct(product: Product): Boolean {
        return products.find { p -> p.barcodeNumber == product.barcodeNumber } != null
    }
}