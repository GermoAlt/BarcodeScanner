package com.assa.barcodereader.task

import android.content.Context
import com.assa.barcodereader.database.ScanDatabase
import com.assa.barcodereader.entity.Product
import java.util.concurrent.Callable

class LoadExistingScansTask(private val context: Context): Callable<ArrayList<Product>> {
    override fun call(): ArrayList<Product> {
        return ScanDatabase.getDatabase(context).scanDao().getAll() as ArrayList<Product>
    }
}