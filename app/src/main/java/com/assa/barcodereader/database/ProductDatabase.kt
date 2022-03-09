package com.assa.barcodereader.database

import android.content.Context
import androidx.room.*
import com.assa.barcodereader.dao.ProductDao
import com.assa.barcodereader.entity.Product
import java.math.BigDecimal
import java.util.*

@Database(entities = [Product::class], version = 4)
@TypeConverters(Converters::class)
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
                            "products_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromBigDecimalToDouble(value: BigDecimal): Double{
        return value.toDouble()
    }

    @TypeConverter
    fun fromDoubleToBigDecimal(value: Double): BigDecimal{
        return BigDecimal(value)
    }
}