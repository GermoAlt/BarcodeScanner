package com.assa.barcodereader.dao

import androidx.room.*
import com.assa.barcodereader.entity.Product

@Dao
interface ScanDao {
    @Query("SELECT * FROM product WHERE barcode_number = :barcode_number")
    fun getById(barcode_number: String): Product?

    @Query("SELECT * FROM product")
    fun getAll(): List<Product>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun batchInsert(products: List<Product>)

    @Query("DELETE FROM product")
    fun deleteAll()

    @Transaction
    fun updateScans(list: ArrayList<Product>){
        deleteAll()
        batchInsert(list)
    }
}