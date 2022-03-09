package com.assa.barcodereader.task

import android.content.Context
import com.assa.barcodereader.database.ProductDatabase
import com.assa.barcodereader.entity.Product

import java.util.concurrent.Callable

class ProductTask(
    private val barcode: String,
    private val applicationContext: Context
): Callable<Product?> {

    override fun call(): Product? {
        return ProductDatabase.getDatabase(applicationContext).productDao().getById(barcode)
    }
}