package com.assa.barcodereader.task

import android.content.Context
import com.assa.barcodereader.database.ScanDatabase
import com.assa.barcodereader.entity.Product
import java.util.concurrent.Callable

class UpdateScansTask(private val context: Context, private val scanList: ArrayList<Product>): Callable<Unit> {
    override fun call(){
        ScanDatabase.getDatabase(context).scanDao().updateScans(scanList)
    }
}