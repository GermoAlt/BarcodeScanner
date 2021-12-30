package com.assa.barcodereader.task

import android.content.Context
import com.assa.barcodereader.database.ProductDatabase
import com.assa.barcodereader.entity.Product
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

import java.util.concurrent.Callable

class DatabasePrepopulationTask(
    private val applicationContext: Context
): Callable<Unit> {

    override fun call() {
        csvReader().open(applicationContext.assets.open("assa_product.csv"))  {
            readAllAsSequence().forEach { row ->
                //Do something with the data
                val product = Product(
                    row[0], //barcode
                    row[4],//amount
                    row[2],//prep date
                    row[3],//boxnum
                    row[1],//desc
                    row[5],//bestby
                    row[6].toDouble() //weight
                )
                ProductDatabase.getDatabase(applicationContext).productDao().insert(product)
            }
        }
    }

}