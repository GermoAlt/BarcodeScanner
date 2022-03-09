package com.assa.barcodereader.task

import android.content.Context
import com.assa.barcodereader.database.ProductDatabase
import com.assa.barcodereader.entity.Product
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

import java.util.concurrent.Callable

class DatabasePrepopulationTask(
    private val applicationContext: Context
): Callable<Unit> {

    override fun call() {
        val urlConnection = URL("https://assa-scanner-backend.herokuapp.com/api/getProducts").openConnection()
        val jsonArray = JSONArray(BufferedReader(InputStreamReader(urlConnection.getInputStream())).readLine())
        val products = ArrayList<Product>()
        (0 until jsonArray.length()).forEach{
            val item = (jsonArray.get(it) as JSONObject)
            products.add(Product(
                item.getString("barcodeNumber"),
                item.getString("description"),
                item.getString("productionDate"),
                item.getString("boxNumber"),
                item.getString("amount"),
                item.getString("bestBefore"),
                item.getDouble("weight"),
                item.getDouble("transactionNumber"),
            ))
        }
        ProductDatabase.getDatabase(applicationContext).productDao().batchInsert(products)
    }
}