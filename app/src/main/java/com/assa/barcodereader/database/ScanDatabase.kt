package com.assa.barcodereader.database

import android.content.Context
import androidx.room.*
import com.assa.barcodereader.dao.ProductDao
import com.assa.barcodereader.dao.ScanDao
import com.assa.barcodereader.entity.Product
import java.math.BigDecimal
import java.util.*

@Database(entities = [Product::class], version = 1)
@TypeConverters(ScanConverters::class)
abstract class ScanDatabase : RoomDatabase(){
    abstract fun scanDao(): ScanDao

    companion object {
        private var INSTANCE: ScanDatabase? = null

        fun getDatabase(context: Context): ScanDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(
                            context,
                            ScanDatabase::class.java,
                            "scan_database")
                            .build()
                }
            }
            return INSTANCE!!
        }
    }
}

class ScanConverters {
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