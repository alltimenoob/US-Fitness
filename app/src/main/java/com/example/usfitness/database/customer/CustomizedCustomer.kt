package com.example.usfitness.database.customer

import androidx.room.ColumnInfo
import java.time.LocalDate

data class CustomizedCustomer (
    @ColumnInfo(name = "cid") val cid: Int,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "date") val startDate: LocalDate,
    @ColumnInfo(name = "max(Record.end_date)") val endDate: LocalDate,
    @ColumnInfo(name = "sum(Payment.amount)-(sum(Record.total)/count(Payment.pid))") val debt : String,
    @ColumnInfo(name = "mobile") val mobile: String
)