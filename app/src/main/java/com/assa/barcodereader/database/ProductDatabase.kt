package com.assa.barcodereader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.assa.barcodereader.dao.ProductDao
import com.assa.barcodereader.entity.Product

@Database(entities = [Product::class], version = 1)
abstract class ProductDatabase : RoomDatabase(){
    abstract fun productDao(): ProductDao

    companion object {
        private var INSTANCE: ProductDatabase? = null

        fun getDatabase(context: Context): ProductDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(
                            context,
                            ProductDatabase::class.java,
                            "product_database_3")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}