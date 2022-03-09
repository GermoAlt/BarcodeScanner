package com.assa.barcodereader.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assa.barcodereader.entity.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM product WHERE barcode_number = :barcode_number")
    fun getById(barcode_number: String): Product?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun batchInsert(products: List<Product>)
}