package com.assa.barcodereader.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Product(
    @PrimaryKey @ColumnInfo(name = "barcode_number") var barcodeNumber: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "production_date") var productionDate: String,
    @ColumnInfo(name = "box_number") var boxNumber: String,
    @ColumnInfo(name = "amount") var amount: String,
    @ColumnInfo(name = "best_before") var bestBefore: String,
    @ColumnInfo(name = "weight") var weight: Double
)