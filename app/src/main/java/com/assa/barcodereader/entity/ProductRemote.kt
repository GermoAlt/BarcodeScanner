package com.assa.barcodereader.entity

import java.time.LocalDate

open class ProductRemote(
    open var barcodeNumber: String,
    open var description: String,
    open var productionDate: LocalDate,
    open var boxNumber: String,
    open var amount: String,
    open var bestBefore: LocalDate,
)